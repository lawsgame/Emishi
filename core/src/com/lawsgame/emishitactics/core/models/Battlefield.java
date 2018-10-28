package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Area.UnitArea;
import com.lawsgame.emishitactics.core.models.Data.Affiliation;
import com.lawsgame.emishitactics.core.models.Data.AreaType;
import com.lawsgame.emishitactics.core.models.Data.TileType;
import com.lawsgame.emishitactics.core.models.Data.Environment;
import com.lawsgame.emishitactics.core.models.Data.Weather;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Model;
import com.lawsgame.emishitactics.core.models.interfaces.Trigger;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler.Task;


import java.util.LinkedList;
import java.util.Observable;


/*
 *
 *
 *
 * battlefield field tiles coordinate system
 * ^
 * |
 * |
 * |
 * (0,0)------>
 *
 */

public class Battlefield extends Model {
    private Tile[][] tiles;
    private IUnit[][] units;
    private Array<Area> deploymentAreas;
    private Array<UnitArea> unitAreas;
    private Weather weather;
    private Environment environment;

    private int turn;
    public LinkedList<IArmy> armyTurnOrder;
    private BattleSolver solver;


    public Battlefield (int nbRows, int nbCols, Weather weather, Environment env, BattleSolver solver){
        if(nbRows > 0 && nbCols > 0) {
            this.tiles = new Tile[nbRows][nbCols];
            this.units = new IUnit[nbRows][nbCols];
        }
        this.deploymentAreas = new Array<Area>();
        this.deploymentAreas.add(new Area(this, Data.AreaType.DEPLOYMENT_AREA));
        this.unitAreas = new Array<UnitArea>();
        this.environment = env;
        setWeather(weather, true);

        setSolver(solver);
        this.armyTurnOrder = new LinkedList<IArmy>();
        this.turn = 0;
    }

    public Battlefield(int nbRows, int nbCols){
        this(nbRows, nbCols, Data.Weather.getStandard(), Environment.getStandard(), new BattleSolver.KillAll());
    }

    public BattleSolver getSolver(){
        return this.solver;
    }

    public void setSolver(BattleSolver battleSolver) {
        this.solver = battleSolver;
        this.solver.setBattlefield(this);
    }

    public int getNbRows() {
        if(getTiles() != null)
            return getTiles().length;
        return 0;
    }

    public int getNbColumns() {
        if(getTiles() != null && getTiles().length > 0){
            return getTiles()[0].length;
        }
        return 0;
    }

    // --------------- EVENT HANDLING --------------------------------------

    public boolean isAnyEventTriggerable(int row, int col, Object data){
        boolean any = false;
        if(isTileExisted(row, col)) {
            any = tiles[row][col].isAnyEventTriggerable(data) || isAnyEventTriggerable(data);
            if (!any) {
                for (int i = 0; i < unitAreas.size; i++) {
                    if (unitAreas.get(i).contains(row, col) && unitAreas.get(i).isAnyEventTriggerable(data)) {
                        any = true;
                        break;
                    }
                }
            }
        }
        return any;
    }

    /**
     * event ordering:
     * - tile
     * - area
     * - bf
     *
     * @param row
     * @param col
     * @param data
     * @return
     */
    public Array<Task> performEvents(int row, int col, Object data){
        Array<Task> tasks = new Array<Task>();
        if(isTileExisted(row, col)){
            tasks.addAll(tiles[row][col].performEvents(data));
            for(int i = 0; i < unitAreas.size; i++){
                tasks.addAll(unitAreas.get(i).performEvents(data));
            }
            tasks.addAll(performEvents(data));
        }
        return tasks;
    }

    public void removeEventTrigger(Trigger trigger){
        this.remove(trigger);
        for(int r = 0; r < tiles.length; r++){
            for(int c = 0; c < tiles[0].length; c++){
                if(isTileExisted(r, c)) {
                    tiles[r][c].remove(trigger);
                    if(isTileOccupied(r, c)){
                        units[r][c].remove(trigger);
                    }
                }
            }
        }
        for(int i = 0; i < unitAreas.size; i++){
            unitAreas.get(i).remove(trigger);
        }

    }


    //----------------- ARMY TURN MANAGEMENT --------------------------------

    /**
     * is required to be called after all parties are deployed.
     */
    public void pushPlayerArmyTurnForward(){
        if(!armyTurnOrder.isEmpty()) {
            IArmy army = armyTurnOrder.peek();
            while (!army.isPlayerControlled()) {
                armyTurnOrder.offer(armyTurnOrder.pop());
                army = armyTurnOrder.peek();
            }
        }

    }


    /**
     * only for loaded AI armies which do not use Battlefield.randomlyDeploy()
     * @param army
     */
    public void addArmyId(IArmy army){
        //performEvent if the army if already deployed
        boolean armyAlreadyAdded = false;
        for(int i = 0; i < armyTurnOrder.size(); i++){
            if(armyTurnOrder.get(i) == army) {
                armyAlreadyAdded = true;
                break;
            }
        }
        if(!armyAlreadyAdded) {
            armyTurnOrder.offer(army);
        }
    }


    public IArmy getCurrentArmy() {
        return armyTurnOrder.peek();
    }

    /*
     *
     * @return get next army contains within the ring with at least one  unit who still fights
     */
    public void nextArmy(){

        if(!armyTurnOrder.isEmpty()) {
            armyTurnOrder.offer(armyTurnOrder.pop());
            IArmy army = armyTurnOrder.peek();
            while (!armyTurnOrder.isEmpty() && !army.isDeployedTroopsStillFighting(this)) {
               armyTurnOrder.pop();
               army = armyTurnOrder.peek();
            }
        }
    }



    // ---------------- TILE MANAGEMENT ------------------------------------
    /*
     ADD A TILE:
     1) addExpGained a TileType
     2) addExpGained the texture in the tiles atlas
     30) (checking) performEvent the correspondance between its texture name and its TileType name
     31) (optional) addExpGained specific rules...

    TILE METHODS HIERARCHY

     > addExpGained
     -> setTileAs
     -> plunderTile
     */


    public boolean setTile(int r, int c, Tile tile, boolean notifyObservers){
        if(checkIndexes(r,c) && tile != null){
            tiles[r][c] = tile;
            if(notifyObservers)
                notifyAllObservers( new Notification.SetTile(r, c, tile.getType()));
            return true;
        }
        return false;
    }

    public boolean setTile(int r, int c, TileType type, boolean notifyObservers){
        return setTile(r, c, new Tile(type), notifyObservers);
    }

    public Tile getTile(int r, int c) {
        if(checkIndexes(r,c)){
            return tiles[r][c];
        }
        return null;
    }

    public Array<int[]> getNeighbourTiles(int row, int col) {
        Array<int[]> tiles = new Array<int[]>();
        if(isTileExisted(row + 1 , col)) tiles.add(new int[]{row + 1 , col});
        if(isTileExisted(row , col + 1)) tiles.add(new int[]{row , col + 1});
        if(isTileExisted(row - 1 , col)) tiles.add(new int[]{row - 1 , col});
        if(isTileExisted(row , col - 1)) tiles.add(new int[]{row , col - 1});
        return tiles;
    }

    public boolean plunderTile(int r, int c, boolean notifyObservers){
        if(isTilePlunderable(r,c)){
            setTile(r, c, TileType.RUINS, notifyObservers);
            return true;
        }
        return false;
    }



    //------------------ AREAS MAGEMETNS --------------------------------------

    public void addUnitArea(UnitArea area, boolean notifyObservers){
        if(area != null && area.getActor() != null && !isUnitAreaExisted(area)) {
            unitAreas.add(area);
            if(notifyObservers) {
                notifyAllObservers(area);
            }
        }
    }

    public Array<UnitArea> removeUnitAreas(IUnit actor, AreaType type, boolean notifyObservers){
        Array<UnitArea> removedAreas = new Array<UnitArea>();
        for(int i = 0; i < unitAreas.size; i++){
            if(unitAreas.get(i).getActor() == actor && unitAreas.get(i).getType() == type){
                removedAreas.add(unitAreas.removeIndex(i));
                i--;
                if(notifyObservers) {
                    notifyAllObservers(removedAreas.peek());
                }
            }
        }
        return removedAreas;
    }

    public Array<UnitArea> removeAllAttachedArea(IUnit actor, boolean moved, boolean notifyObservers){
        Array<UnitArea> removedAreas = new Array<UnitArea>();
        for(int i = 0; i < unitAreas.size; i++){
            if(unitAreas.get(i).getActor() == actor && (!moved || unitAreas.get(i).isRemovedUponMovingUnit())){
                removedAreas.add(unitAreas.removeIndex(i));
                i--;
                if(notifyObservers) {
                    notifyAllObservers(removedAreas.peek());
                }
            }
        }
        return removedAreas;
    }

    public Array<UnitArea> removeAllAreaType(AreaType type, boolean notifyObservers){
        Array<UnitArea> removedAreas = new Array<UnitArea>();
        for(int i = 0; i < unitAreas.size; i++){
            if(unitAreas.get(i).getType() == type){
                removedAreas.add(unitAreas.removeIndex(i));
                i--;
                if(notifyObservers) {
                    notifyAllObservers(removedAreas.peek());
                }
            }
        }
        return removedAreas;
    }

    public Array<UnitArea> getUnitAreas() {
        return unitAreas;
    }

    public boolean isUnitAreaExisted(IUnit actor, AreaType type){
        for(int i = 0; i < unitAreas.size; i++){
            if(unitAreas.get(i).getActor() == actor && unitAreas.get(i).getType() == type){
                return true;
            }
        }
        return false;
    }

    public boolean isUnitAreaExisted(UnitArea area){
        for(int i = 0; i < unitAreas.size; i++){
            if(unitAreas.get(i).getActor() == area.getActor() && unitAreas.get(i).getType() == area.getType()){
                return true;
            }
        }
        return false;
    }

    public Array<IUnit> getAvailableGuardian(int row, int col, Affiliation alleageance){
        Array<IUnit> guardians =  new Array<IUnit>();
        for (int k = 0; k < unitAreas.size; k++) {
            if (unitAreas.get(k).getType() == AreaType.GUARD_AREA
                    && unitAreas.get(k).getActor() != null
                    && unitAreas.get(k).getActor().isAllyWith(alleageance)
                    && unitAreas.get(k).contains(row, col)) {
                guardians.add(unitAreas.get(k).getActor());
            }
        }
        return guardians;
    }

    public IUnit getStrongestGuardian(int row, int col, Affiliation alleageance){
        Array<IUnit> guardians = getAvailableGuardian(row, col , alleageance);
        IUnit electedGuardian = null;
        if(guardians.size > 0) {
            electedGuardian = guardians.get(0);
            for (int i = 1; i < guardians.size; i++) {
                if (guardians.get(i).getLevel() > electedGuardian.getLevel()) {
                    electedGuardian = guardians.get(i);
                }
            }
        }
        return electedGuardian;
    }

    public void addDeploymentTile(int row, int col, int areaIndex, boolean notifyObservers){

        if(isTileReachable(row, col, false) && 0 <= areaIndex){

            // add deployment areas not yet added while the areaIndex is positive and superior than the size of the deployment area array
            while(areaIndex >= deploymentAreas.size){
                deploymentAreas.add(new Area(this, Data.AreaType.VANGUARD_DEPLOYMENT_AREA));
            }

            deploymentAreas.get(areaIndex).addTile(row, col, notifyObservers);
        }
    }


    //-------------- TILE STATE CHECK METHODS HIERARCHY ----------------------
    /*
    if one is true, its parent are as well

    checkIndexes
    > isTileLooted
    > isTileExisted
    -> isTilePlunderable
    -> isTileReachable
    |-> isTileAvailable
    -> isTileOccupied
    --> isTileOccupiedByX

    > isTileReachable && isTileOccupied ==

     */

    public boolean isTileDeploymentTile(int row, int col, int areaIndex) {
        return getDeploymentArea(areaIndex).contains(row, col);
    }

    public boolean isTileDeploymentTile(int row, int col){
        for(int i = 0; i < deploymentAreas.size; i++){
            if(isTileDeploymentTile(row, col, i))
                return true;
        }
        return false;
    }

    public boolean checkIndexes(int r, int c) {
        return r > -1 && r < getNbRows() && c > -1 && c < getNbColumns();
    }

    public boolean isTileLooted(int row, int col){
        return checkIndexes(row,col) && tiles[row][col] != null && tiles[row][col].isLooted();
    }

    public boolean isTileExisted(int r, int c){
        return checkIndexes(r,c) && getTile(r,c) != null;
    }

    public boolean isTileOfType(int r, int c, TileType tileType){
        return isTileExisted(r,c) && getTile(r,c).getType() == tileType;
    }

    public boolean isTilePlunderable(int r, int c){
        return isTileExisted(r,c) && getTile(r,c).getType().isPlunderable();
    }

    public boolean isTileReachable(int row, int col, boolean pathfinder) {
        if(isTileExisted(row, col)){
            return getTile(row, col).getType().isReachable(pathfinder);
        }
        return false;
    }

    public boolean isTileOccupied(int row , int col){
        if(isTileExisted(row, col) && units != null){
            return this.units[row][col] != null;
        }
        return false;
    }

    public boolean isTileAvailable(int row, int col, boolean pathfinder){
        return isTileReachable(row, col, pathfinder) && !isTileOccupied(row, col);
    }

    public boolean isTileOccupiedBySameSquad(int row , int col, IUnit unit){
        return isTileOccupied(row, col) && this.units[row][col].sameSquadAs(unit);
    }

    public boolean isTileOccupiedByAlly(int row , int col, Affiliation affiliation){
        return isTileOccupied(row, col) && getUnit(row, col).isAllyWith(affiliation);
    }

    public boolean isTileOccupiedByFoe(int row , int col, Data.Affiliation affiliation){
        return isTileOccupied(row, col) && !getUnit(row, col).isAllyWith(affiliation);
    }

    public boolean isTileOccupiedByPlayerControlledUnit(int row, int col) {
        return isTileOccupied(row, col) && getUnit(row, col).getArmy() != null && getUnit(row, col).getArmy().isPlayerControlled();
    }

    public boolean isTileGuarded(int row, int col, Affiliation alliedAffiliation){
        boolean res = false;
        for(int i = 0; i < unitAreas.size; i++){
            if (unitAreas.get(i).getType() == AreaType.GUARD_AREA
                    && unitAreas.get(i).getActor() != null
                    && unitAreas.get(i).getActor().isAllyWith(alliedAffiliation)
                    && unitAreas.get(i).contains(row, col)) {

                res = true;
                break;
            }
        }
        return res;
    }



    //----------------- UNIT MANAGEMENT ----------------------


    /**
     * deploy mobilized units
     *
     * @param row
     * @param col
     * @param unit
     * @param notifyObservers
     */
    public void deploy(int row, int col, IUnit unit, boolean notifyObservers){
        if(isTileAvailable(row, col, unit.has(Data.Ability.PATHFINDER))
                && !isUnitDeployed(unit)
                && unit.isMobilized()){

            this.units[row][col] = unit;
            addArmyId(unit.getArmy());
            if(notifyObservers)
                notifyAllObservers(new  Notification.SetUnit(row, col, unit));
        }
    }

    public void randomlyDeploy(IArmy army){
        if(army != null) {
            randomlyDeploy(army.getMobilizedUnits(true), 0);
            addArmyId(army);
        }
    }

    /**
     * deploy a group of units on the available tiles of the given deployment area
     *
     * @param mobilizedTroops
     * @param areaIndex
     */
    public void randomlyDeploy(Array<IUnit> mobilizedTroops, int areaIndex){

        Array<int[]> deploymentsTile = getDeploymentArea(areaIndex).getTiles();

        int[] coords;
        Array<int[]> remainingAvailableTiles = new Array<int[]>();
        Array<IUnit> remainingUnits = new Array<IUnit>();
        for (int i = 0; i < deploymentsTile.size; i++) {
            coords = deploymentsTile.get(i);
            if (coords.length >= 2 && isTileAvailable(coords[0], coords[1], false)) {
                remainingAvailableTiles.add(coords);
            }
        }
        for (int i = 0; i < mobilizedTroops.size; i++) {
            remainingUnits.add(mobilizedTroops.get(i));

        }

        IUnit unit;
        while (remainingUnits.size > 0 && remainingAvailableTiles.size > 0) {
            coords = remainingAvailableTiles.removeIndex(Data.rand(remainingAvailableTiles.size));
            unit = remainingUnits.removeIndex(Data.rand(remainingUnits.size));
            deploy(coords[0], coords[1], unit, true);
        }
    }


    public boolean isUnitDeployed(IUnit unit) {
        for (int r = 0; r < getNbRows(); r++) {
            for (int c = 0; c < getNbRows(); c++) {
                if (this.units[r][c] == unit) {
                    return true;
                }
            }
        }
        return false;
    }

    public void switchUnitPositions(int rowUnit1, int colUnit1, int rowUnit2, int colUnit2){
        if(isTileOccupied(rowUnit1, colUnit1) && isTileOccupied(rowUnit2, colUnit2)){
            IUnit unit1 = getUnit(rowUnit1, colUnit1);
            IUnit unit2 = getUnit(rowUnit2, colUnit2);
            if(isTileReachable(rowUnit1, colUnit1, unit2.has(Data.Ability.PATHFINDER) && isTileReachable(rowUnit2, colUnit2, unit1.has(Data.Ability.PATHFINDER)))){
                removeAllAttachedArea(unit1,  true,false);
                removeAllAttachedArea(unit2, true, false);
                this.units[rowUnit2][colUnit2] = unit1;
                this.units[rowUnit1][colUnit1] = unit2;

            }
        }
    }

    public void moveUnit(int rowI, int colI, int rowf, int colf, boolean notifyObservers){

        if(isTileOccupied(rowI, colI)) {
            IUnit unit = getUnit(rowI, colI);
            if(isTileAvailable(rowf, colf, unit.has(Data.Ability.PATHFINDER))){
                removeAllAttachedArea(unit,true,  notifyObservers);
                this.units[rowf][colf] = unit;
                this.units[rowI][colI] = null;
                if(notifyObservers) {
                    notifyAllObservers(new Notification.SetUnit(rowf, colf, unit));
                }
            }
        }
    }

    public boolean isUnitGuarding(int rowUnit, int colUnit){
        if(isTileOccupied(rowUnit, colUnit)){
            IUnit unit = getUnit(rowUnit, colUnit);
            if(unit.isMobilized()) {
                for(int i = 0; i < unitAreas.size; i++){
                    if (unitAreas.get(i).getType() == AreaType.GUARD_AREA
                            && unitAreas.get(i).getActor() != null
                            && unitAreas.get(i).getActor().isAllyWith(unit.getArmy().getAffiliation())
                            && unitAreas.get(i).contains(rowUnit, colUnit)) {

                        return true;
                    }
                }
            }
        }

        return false;
    }



    public IUnit getUnit(int row, int col){
        if(checkIndexes(row, col))
            return this.units[row][col];
        return null;
    }

    public Array<IUnit> getStillActiveUnits(int armyId) {
        Array<IUnit> activeUnits = new Array<IUnit>();
        for(int r =0; r<getNbRows();r++){
            for(int c = 0; c<getNbColumns(); c++){
                if(isTileOccupied(r, c)
                        && getUnit(r, c).getArmy().getId() == armyId
                        && !getUnit(r, c).isDone()){
                    activeUnits.add(getUnit(r, c));
                }
            }
        }
        return activeUnits;
    }

    public Array<int[]> getStillActiveUnitCoords(int armyId) {
        Array<int[]> activeUnitCoords = new Array<int[]>();
        for(int r =0; r<getNbRows();r++){
            for(int c = 0; c<getNbColumns(); c++){
                if(isTileOccupied(r, c)
                        && getUnit(r, c).getArmy().getId() == armyId
                        && !getUnit(r, c).isDone()){
                    activeUnitCoords.add(new int[]{r, c});
                }
            }
        }
        return activeUnitCoords;
    }

    public int[] getRandomlyStillActiveUnitsCoords(int armyId){
        Array<int[]> activeUnits = new Array<int[]>();
        for(int r =0; r<getNbRows();r++){
            for(int c = 0; c<getNbColumns(); c++){
                if(isTileOccupied(r, c) && getUnit(r, c).getArmy().getId() == armyId && !getUnit(r, c).isDone()){
                    activeUnits.add(new int[]{r, c});
                }
            }
        }
        return activeUnits.random();
    }


    public IUnit removeUnit(int row, int col, boolean notifyObservers){
        IUnit unit = this.units[row][col];
        this.units[row][col] = null;
        removeAllAttachedArea(unit, false, notifyObservers);
        if(notifyObservers)
            notifyAllObservers(getUnit(row, col));
        return unit;
    }

    public void removeOOAUnits(boolean notifyObservers) {
        for(int r= 0; r < getNbRows(); r++){
            for(int c = 0; c < getNbColumns(); c++){
                if(isTileOccupied(r, c) && getUnit(r, c).isOutOfAction()){
                    removeUnit(r, c, notifyObservers);
                }
            }
        }
    }

    public Array<IUnit> getOOAUnits() {
        Array<IUnit> OOAUnits = new Array<IUnit>();
        for(int r= 0; r < getNbRows(); r++){
            for(int c = 0; c < getNbColumns(); c++){
                if(isTileOccupied(r, c) && getUnit(r, c).isOutOfAction()){
                    OOAUnits.add(units[r][c]);
                }
            }
        }
        return OOAUnits;
    }

    public IUnit getUnitByName(String name) {
        IUnit target = null;
        for(int r = getNbRows()-1; r > -1 ; r--){
            for(int c = 0; c < getNbColumns(); c++){
                if(isTileOccupied(r, c) && getUnit(r, c).getName().equals(name)){
                    target = getUnit(r, c);
                }
            }
        }
        return target;
    }

    public int[] getUnitPos(IUnit unit){
        for(int r = getNbRows()-1; r > -1 ; r--){
            for(int c = 0; c < getNbColumns(); c++){
                if(isTileOccupied(r, c) && getUnit(r, c) == unit){
                    return new int[]{r, c};
                }
            }
        }
        return null;
    }

    public Array<IUnit> findUnit(Affiliation a) {
        Array<IUnit> units = new Array<IUnit>();
        for(int r = getNbRows()-1; r > -1 ; r--){
            for(int c = 0; c < getNbColumns(); c++){
                if(isTileOccupied(r, c)) {
                    IUnit unit = getUnit(r, c);
                    if(unit.isMobilized() && unit.getArmy().getAffiliation() == a) {
                        units.add(unit);
                    }
                }
            }
        }
        return units;
    }


    /**
     *
     * @param unit
     * @param row
     * @param col
     * @return whether or not there is a standard bearer at range if the given unit is standing on the given buildingType = {row, col}
     */
    public boolean  isStandardBearerAtRange(IUnit unit, int row, int col){
        int dist;
        if(isTileExisted(row, col)&& unit.isMobilized()) {
            int bannerRange = unit.getArmy().getBannerRange(unit);
            for (int r = row - bannerRange; r <= row + bannerRange; r++) {
                for (int c = col - bannerRange; c <= col + bannerRange; c++) {
                    dist = Utils.dist(row, col, r, c);
                    if (checkIndexes(r, c )
                            && dist > 0
                            && dist <= bannerRange
                            && isTileOccupiedBySameSquad(r, c, getUnit(r, c))
                            && getUnit(r, c).isStandardBearer()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }





    //------------------- PATH & AREA FETCHING METHODS -----------------------




    private static final CheckMoveMap checkmap = new CheckMoveMap();

    static class CheckMoveMap{
        int rowOrigin;
        int colOrigin;
        int rowRelActor;
        int colRelActor;

        Battlefield battlefield;
        int[][] checkTiles;
        int moveRange;
        boolean pathfinder;
        Data.Affiliation affiliation;

        CheckMoveMap(){ }

        public Array<int[]> getActionArea(Battlefield bf, int rowActor, int colActor, IUnit actor, boolean moveOnly){
            if(actor != null) {
                set(bf, rowActor, colActor, actor);
                setTilesMRP(actor);
                condemnTiles(actor);
                if (!moveOnly) addAttackTiles(actor);
                return getTiles(moveOnly);
            }
            return new Array<int[]>();
        }

        private void addAttackTiles(IUnit actor) {

            int dist;
            int rangeMin;
            int rangeMax;
            for(int rUnit = 0; rUnit < checkTiles.length; rUnit++){
                for(int cUnit = 0; cUnit < checkTiles[0].length; cUnit++){
                    if(checkTiles[rUnit][cUnit] > 0){

                        rangeMin = actor.getCurrentWeaponRangeMin(rowOrigin + rUnit, colOrigin + cUnit, battlefield);
                        rangeMax = actor.getCurrentWeaponRangeMax( rowOrigin + rUnit, colOrigin + cUnit, battlefield);
                        for(int r = rUnit - rangeMax; r <= rUnit + rangeMax; r++){
                            for(int c = cUnit - rangeMax; c <= cUnit + rangeMax; c++){

                                dist = Utils.dist(r, c, rUnit, cUnit);
                                if(checkIndexes(r, c)
                                        && checkTiles[r][c] == 0
                                        && dist <= rangeMax
                                        && dist >= rangeMin ){
                                    checkTiles[r][c] = -1;
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * instanciate relevatn attributes and create a blank checkmap
         *
         * @param bf
         * @param rowActor
         * @param colActor
         */
        private void set( Battlefield bf, int rowActor, int colActor, IUnit actor){
            if(actor.isMobilized()) {

                // get actor relevant pieces of information
                this.pathfinder = actor.has(Data.Ability.PATHFINDER);
                this.moveRange = actor.getAppMobility();
                this.affiliation = actor.getArmy().getAffiliation();
                this.battlefield = bf;

                // add the performEvent map dimensions and origin point

                int range = moveRange + 6;
                this.rowOrigin = rowActor - range;
                this.colOrigin = colActor - range;
                this.rowRelActor = range;
                this.colRelActor = range;
                int rows = 2 * range + 1;
                int cols = 2 * range + 1;


                if (rowOrigin < 0) {
                    rowRelActor += rowOrigin;
                    rows += rowOrigin;
                    rowOrigin = 0;
                }
                if (colOrigin < 0) {
                    colRelActor += colOrigin;
                    cols += colOrigin;
                    colOrigin = 0;
                }

                if (rowOrigin + rows > bf.getNbRows()) {
                    rows = bf.getNbRows() - rowOrigin;
                }
                if (colOrigin + cols > bf.getNbColumns()) {
                    cols = bf.getNbColumns() - colOrigin;
                }
                checkTiles = new int[rows][cols];
            }
        }

        private boolean checkIndexes(int r, int c){
            return 0 < checkTiles.length && 0 <= r && r < checkTiles.length && 0 <= c && c < checkTiles[0].length;
        }

        // MRP = mobility remaining points
        private void setTilesMRP(IUnit actor){
            checkTiles[rowRelActor][colRelActor] = moveRange;
            if(moveRange > 0){
                if(checkIndexes(rowRelActor + 1, colRelActor))
                    updateTilesMRP(rowRelActor + 1, colRelActor, moveRange  , actor, Data.Orientation.SOUTH);
                if(checkIndexes(rowRelActor - 1, colRelActor))
                    updateTilesMRP(rowRelActor - 1, colRelActor, moveRange , actor, Data.Orientation.NORTH);
                if(checkIndexes(rowRelActor, colRelActor + 1))
                    updateTilesMRP(rowRelActor , colRelActor + 1, moveRange , actor, Data.Orientation.WEST);
                if(checkIndexes(rowRelActor , colRelActor - 1))
                    updateTilesMRP(rowRelActor , colRelActor - 1, moveRange , actor, Data.Orientation.EAST);
            }

        }

        private void updateTilesMRP(int row, int col, int remainingMovePoints, IUnit actor, Data.Orientation comefrom) {
            if(remainingMovePoints > checkTiles[row][col]) {
                if (remainingMovePoints == 1) {
                    if (battlefield.isTileAvailable(rowOrigin + row, colOrigin + col, pathfinder)
                            || (battlefield.getUnit(rowOrigin + row, colOrigin + col) == actor )) {
                        checkTiles[row][col] = remainingMovePoints;
                    }
                } else if (remainingMovePoints > 1) {
                    if(battlefield.isTileReachable(rowOrigin+row, colOrigin+col, pathfinder) && !battlefield.isTileOccupiedByFoe(rowOrigin+row, colOrigin+col, affiliation)){
                        checkTiles[row][col] = remainingMovePoints;
                        if(comefrom != Data.Orientation.NORTH && checkIndexes(row + 1, col))
                            updateTilesMRP(row + 1, col, remainingMovePoints - 1  , actor, Data.Orientation.SOUTH);
                        if(comefrom != Data.Orientation.SOUTH && checkIndexes(row - 1, colRelActor))
                            updateTilesMRP(row - 1, col, remainingMovePoints - 1 , actor, Data.Orientation.NORTH);
                        if(comefrom != Data.Orientation.EAST && checkIndexes(row, col + 1))
                            updateTilesMRP(row , col + 1, remainingMovePoints - 1 , actor, Data.Orientation.WEST);
                        if(comefrom != Data.Orientation.WEST && checkIndexes(row , col - 1))
                            updateTilesMRP(row , col - 1, remainingMovePoints - 1 , actor, Data.Orientation.EAST);
                    }
                }
            }
        }

        /**
         * condemn tiles occupied by allies and therefore unreachable.
         */
        private void condemnTiles(IUnit actor){
            int oldMoveAreaSize = 0;
            int moveAreaSize = getMoveAreaSize();
            while(oldMoveAreaSize != moveAreaSize){
                oldMoveAreaSize = moveAreaSize;

                for(int r = 0; r < checkTiles.length; r++){
                    for(int c = 0; c < checkTiles[0].length; c++){
                        if (checkTiles[r][c] > 1) {
                            if(checkIndexes(r + 1, c) && checkTiles[r+1][c] > 0 && checkTiles[r+1][c] < checkTiles[r][c]) continue;
                            if(checkIndexes(r - 1, c) && checkTiles[r-1][c] > 0 && checkTiles[r-1][c] < checkTiles[r][c]) continue;
                            if(checkIndexes(r, c + 1) && checkTiles[r][c+1] > 0 && checkTiles[r][c+1] < checkTiles[r][c]) continue;
                            if(checkIndexes(r, c - 1) && checkTiles[r][c-1] > 0 && checkTiles[r][c-1] < checkTiles[r][c]) continue;
                            if(battlefield.isTileOccupied(r + rowOrigin, c + colOrigin)
                                    && (battlefield.getUnit(r + rowOrigin, c + colOrigin ) != actor ))
                                checkTiles[r][c] = 0;
                        }
                    }
                }
                moveAreaSize = getMoveAreaSize();
            }
        }



        private int getMoveAreaSize(){
            int size = 0;
            for(int r = 0; r < checkTiles.length; r++){
                for(int c = 0; c < checkTiles[0].length; c++){
                    if (checkTiles[r][c] > 0) {
                        size++;
                    }
                }
            }
            return size;
        }

        private Array<int[]> getTiles(boolean moveOnly){
            Array<int[]> area = new Array<int[]>();
            for(int r = 0; r < checkTiles.length; r++){
                for(int c = 0; c < checkTiles[0].length; c++){
                    if (checkTiles[r][c] > 0 || (checkTiles[r][c] < 0 && !moveOnly)) {
                        area.add(new int[]{r + rowOrigin, c + colOrigin});
                    }
                }
            }
            return area;
        }


        @Override
        public String toString(){
            String str ="\nOrigin :"+rowOrigin+" "+colOrigin+"\n\n";
            for(int r = checkTiles.length - 1 ; r > -1 ; r--){
                for(int c = 0; c < checkTiles[0].length; c++){
                    if(checkTiles[r][c] < 0)
                        str += " "+ checkTiles[r][c];
                    else if(checkTiles[r][c] > 0)
                        str += "  "+checkTiles[r][c];
                    else
                        str += "   ";
                }
                str+="\n";
            }
            return str;
        }
    }

    /**
     *
     * @param rowActor
     * @param colActor
     * @return fetch all tiles where the given unit can moved on
     */
    public Array<int[]> getMoveArea(int rowActor, int colActor){
        return checkmap.getActionArea(this, rowActor, colActor, getUnit(rowActor, colActor), true);
    }

    public Array<int[]> getMoveArea(int row, int col, IUnit actor){
        return checkmap.getActionArea(this, row, col, actor,true);
    }


    /**
     *
     * @param rowActor
     * @param colActor
     * @return fetch all tiles where the given unit can act upon
     */
    public Array<int[]> getActionArea(int rowActor, int colActor){
        return checkmap.getActionArea(this, rowActor, colActor, getUnit(rowActor, colActor), false);
    }

    public Array<int[]> getActionArea(int row, int col, IUnit actor){
        return checkmap.getActionArea(this, row, col, actor,false);
    }


    /**
     *
     * Algorythm A* : https://www.youtube.com/watch?v=-L-WgKMFuhE
     *
     * get the shortest validPath of a target buildingType using the A* algorithm
     *
     * @return an {[row, col]} array  representing the shortest validPath from one buildingType to another,
     * excluding (rowI, colI) and finishing by (rowf, colf). If the path is invalid, the returned array will be empty
     *
     */
    public Array<int[]>  getShortestPath(int rowI, int colI, int rowf, int colf, boolean pathfinder, Affiliation affiliation){
        Array<int[]> res = new Array<int[]>();

        if(isTileAvailable(rowf, colf, pathfinder) && affiliation != null) {

            Array<PathNode> path = new Array<PathNode>();
            Array<PathNode> opened = new Array<PathNode>();
            Array<PathNode> closed = new Array<PathNode>();
            opened.add(new PathNode(rowI, colI, rowf, colf, null, this));
            PathNode current;
            Array<PathNode> neighbours;
            while (true) {

                //no solution
                if (opened.size == 0) break;

                current = opened.get(0);
                for (int i = 0; i < opened.size; i++) {
                    if (opened.get(i).better(current)) {
                        current = opened.get(i);
                    }
                }
                opened.removeValue(current, true);
                closed.add(current);

                // validPath found
                if (current.getRow() == rowf && current.getCol() == colf) break;

                // get available neighbor nodes which are not yet in the closed list
                PathNode node;
                neighbours = new Array<PathNode>();
                if (isTileReachable(current.row + 1, current.col, pathfinder)
                        && !isTileOccupiedByFoe(current.row + 1, current.col, affiliation)) {
                    node = new PathNode(current.row + 1, current.col, rowf, colf, current, this);
                    if (!closed.contains(node, false)) {
                        neighbours.add(node);
                    }
                }
                if (isTileReachable(current.row, current.col + 1, pathfinder)
                        && !isTileOccupiedByFoe(current.row, current.col + 1, affiliation)) {
                    node = new PathNode(current.row, current.col + 1, rowf, colf, current, this);
                    if (!closed.contains(node, false)) {
                        neighbours.add(node);
                    }
                }
                if (isTileReachable(current.row - 1, current.col, pathfinder)
                        && !isTileOccupiedByFoe(current.row - 1, current.col, affiliation)) {
                    node = new PathNode(current.row - 1, current.col, rowf, colf, current, this);
                    if (!closed.contains(node, false)) {
                        neighbours.add(node);
                    }
                }
                if (isTileReachable(current.row, current.col - 1, pathfinder)
                        && !isTileOccupiedByFoe(current.row, current.col - 1, affiliation)) {
                    node = new PathNode(current.row, current.col - 1, rowf, colf, current, this);
                    if (!closed.contains(node, false)) {
                        neighbours.add(node);
                    }
                }

                boolean isNotInOpened = true;
                for (int i = 0; i < neighbours.size; i++) {
                    node = neighbours.get(i);
                    for (int j = 0; j < opened.size; j++) {
                        if (opened.get(j).equals(node)) {
                            isNotInOpened = false;
                            if (node.better(opened.get(j))) {
                                opened.removeIndex(j);
                                opened.add(node);
                            }
                        }
                    }
                    if (isNotInOpened) {
                        opened.add(node);
                    }
                    isNotInOpened = true;
                }
            }

            if (closed.get(closed.size - 1).getRow() == rowf && closed.get(closed.size - 1).getCol() == colf) {
                path = closed.get(closed.size - 1).getPath();
            }
            for (int i = 1; i < path.size; i++) {
                res.add(new int[]{path.get(i).getRow(), path.get(i).getCol()});
            }
        }

        return res;
    }

    /**
     * AI oriented method
     *
     * Algorythm A* : https://www.youtube.com/watch?v=-L-WgKMFuhE
     *
     * get the shortest validPath of a target buildingType using the A* algorithm
     *
     * @return an {[row, col]} array  representing the shortest validPath from one tile to another,
     * excluding (rowActor, colActor) and finishing by a tile at range of {rowTarget, colTarget}. If the path is invalid, the returned array will be empty
     *
     */
    public Array<int[]>  getShortestPath(int rowActor, int colActor, int rowTarget, int colTarget, boolean pathfinder, Affiliation affiliation, int rangeMax){
        Array<int[]> res = new Array<int[]>();

        if(affiliation != null) {

            Array<PathAreaNode> path = new Array<PathAreaNode>();
            Array<PathAreaNode> opened = new Array<PathAreaNode>();
            Array<PathAreaNode> closed = new Array<PathAreaNode>();
            opened.add(new PathAreaNode(rowTarget, colTarget, rowActor, colActor, rangeMax, null, this));
            PathAreaNode current;
            Array<PathAreaNode> neighbours;
            while (true) {

                //no solution
                if (opened.size == 0) break;

                current = opened.get(0);
                for (int i = 0; i < opened.size; i++) {
                    if (opened.get(i).better(current)) {
                        current = opened.get(i);
                    }
                }
                opened.removeValue(current, true);
                closed.add(current);

                // validPath found
                if (current.getRow() == rowActor && current.getCol() == colActor) break;

                // get available neighbor nodes which are not yet in the closed list
                PathAreaNode node;
                neighbours = new Array<PathAreaNode>();
                if (current.remainingRange > 1 || (isTileReachable(current.row + 1, current.col, pathfinder) && !isTileOccupiedByFoe(current.row + 1, current.col, affiliation))) {
                    node = new PathAreaNode(current.row + 1, current.col, rowActor, colActor, rangeMax, current, this);
                    if (!closed.contains(node, false)) {
                        neighbours.add(node);
                    }
                }
                if (current.remainingRange > 1 || (isTileReachable(current.row, current.col + 1, pathfinder) && !isTileOccupiedByFoe(current.row, current.col + 1, affiliation))) {
                    node = new PathAreaNode(current.row, current.col + 1, rowActor, colActor, rangeMax, current, this);
                    if (!closed.contains(node, false)) {
                        neighbours.add(node);
                    }
                }
                if (current.remainingRange > 1 || (isTileReachable(current.row - 1, current.col, pathfinder) && !isTileOccupiedByFoe(current.row - 1, current.col, affiliation))) {
                    node = new PathAreaNode(current.row - 1, current.col, rowActor, colActor, rangeMax, current, this);
                    if (!closed.contains(node, false)) {
                        neighbours.add(node);
                    }
                }
                if (current.remainingRange > 1 || (isTileReachable(current.row, current.col - 1, pathfinder) && !isTileOccupiedByFoe(current.row, current.col - 1, affiliation))) {
                    node = new PathAreaNode(current.row, current.col - 1, rowActor, colActor, rangeMax, current, this);
                    if (!closed.contains(node, false)) {
                        neighbours.add(node);
                    }
                }

                boolean isNotInOpened = true;
                for (int i = 0; i < neighbours.size; i++) {
                    node = neighbours.get(i);
                    for (int j = 0; j < opened.size; j++) {
                        if (opened.get(j).equals(node)) {
                            isNotInOpened = false;
                            if (node.better(opened.get(j))) {
                                opened.removeIndex(j);
                                opened.add(node);
                            }
                        }
                    }
                    if (isNotInOpened) {
                        opened.add(node);
                    }
                    isNotInOpened = true;
                }
            }

            if (closed.get(closed.size - 1).getRow() == rowActor && closed.get(closed.size - 1).getCol() == colActor) {
                path = closed.get(closed.size - 1).getPath();
            }
            for (int i = path.size - 2; i > -1 ; i--) {
                if(path.get(i).remainingRange < 1) {
                    res.add(new int[]{path.get(i).getRow(), path.get(i).getCol()});
                }
            }
        }

        return res;
    }




    //-------------------GETTERS & SETTERS -------------------------




    public Tile[][] getTiles() {
        return tiles;
    }

    public int getWidth() {
        return getNbColumns();
    }

    public int getHeight() {
        return getNbRows();
    }

    public Array<Area> getDeploymentAreas() {
        return deploymentAreas;
    }

    public Area getDeploymentArea(int areaIndex) {
        if(0 <= areaIndex && areaIndex < deploymentAreas.size)
            return deploymentAreas.get(areaIndex);
        return null;
    }

    public int getNumberOfDeploymentAreas(){
        return deploymentAreas.size;
    }

    public void setWeather(Data.Weather weather, boolean notifyObservers) {
        this.weather = weather;
        if(notifyObservers){
            notifyAllObservers(weather);
        }
    }

    public Data.Weather getWeather() {
        return weather;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public void incrementTurn(){
        this.turn++;
    }

    @Override
    public String toString() {
        return "battlefield";
    }

    //----------------- NODE HELPER CLASSES -------------------




    public static abstract class Node<N extends Node>{
        protected int row;
        protected int col;
        protected int distSource;
        protected Battlefield bf;

        public Node(int row, int col, Battlefield bf) {
            super();
            this.row = row;
            this.col = col;
            this.bf = bf;
            this.distSource = 0;
        }


        abstract boolean better(N node);

        @Override
        public boolean equals(Object obj){
            if(obj instanceof Node){
                Node node = (Node) obj;
                return row == node.row && col == node.col;
            }
            return false;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        @Override
        public String toString(){
            return row+" "+col+" "+" range: "+ distSource;
        }

    }

    public static class PathNode extends Node<PathNode>{
        protected int distTarget;
        protected PathNode parent;

        public PathNode(int row, int col, int rowf, int colf, PathNode parent, Battlefield bf) {
            super(row, col, bf);
            this.parent = parent;
            this.distSource = (parent != null) ? this.parent.distSource + 1 : 0;
            this.distTarget = Utils.dist(row, col, rowf, colf);
        }

        @Override
        boolean better(PathNode node){
            return distSource + distTarget < node.distSource + node.distTarget
                    || ((distSource + distTarget == node.distSource + node.distTarget) && distTarget < node.distTarget);
        }

        public Array<PathNode> getPath(){
            Array<PathNode> bestpath;
            if(this.parent == null){
                bestpath = new Array<PathNode>();
                bestpath.add(this);
                return bestpath;
            }
            bestpath = parent.getPath();
            bestpath.add(this);
            return bestpath;

        }


        public String toString(){
            return row+" "+col+" "+" cost: "+ (distSource + distTarget);
        }


    }

    public static class PathAreaNode extends Node<PathAreaNode>{
        protected int distTarget;
        protected PathAreaNode parent;
        protected int remainingRange;

        public PathAreaNode(int row, int col, int rowf, int colf, int rangeMax, PathAreaNode parent, Battlefield bf) {
            super(row, col, bf);
            this.parent = parent;
            this.remainingRange = (parent != null) ? parent.remainingRange - 1 : rangeMax;
            this.distSource = (parent != null  && remainingRange == 0) ? this.parent.distSource + 1 : 0;
            this.distTarget = Utils.dist(row, col, rowf, colf);
        }

        @Override
        boolean better(PathAreaNode node){
            return distSource + distTarget < node.distSource + node.distTarget
                    || ((distSource + distTarget == node.distSource + node.distTarget) && distTarget < node.distTarget);
        }

        public Array<PathAreaNode> getPath(){
            Array<PathAreaNode> bestpath;
            if(this.parent == null){
                bestpath = new Array<PathAreaNode>();
                bestpath.add(this);
                return bestpath;
            }
            bestpath = parent.getPath();
            bestpath.add(this);
            return bestpath;

        }


        public String toString(){
            return row+" "+col+" "+" cost: "+ (distSource + distTarget);
        }


    }
    


}
