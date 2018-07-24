package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.constants.Data.Allegeance;
import com.lawsgame.emishitactics.core.constants.Data.Item;
import com.lawsgame.emishitactics.core.constants.Data.TileType;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.Area.UnitArea;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;

import java.util.HashMap;


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

public class Battlefield extends Observable {
    private TileType[][] tiles;
    private IUnit[][] units;
    private boolean[][] looted;
    private Array<int[]> deploymentArea;
    private HashMap<Allegeance, Array<UnitArea>> guardedAreas;
    private HashMap<Allegeance, Array<UnitArea>> coveredAreas;
    private HashMap<Integer, IUnit> recruits;
    private HashMap<Integer, Item> tombItems;


    public Battlefield (int nbRows, int nbCols){
        if(nbRows > 0 && nbCols > 0) {
            this.tiles = new TileType[nbRows][nbCols];
            this.looted = new boolean[nbRows][nbCols];
            this.units = new IUnit[nbRows][nbCols];
        }
        this.deploymentArea = new Array<int[]>();

        this.guardedAreas = new HashMap<Allegeance, Array<UnitArea>>();
        this.coveredAreas = new HashMap<Allegeance, Array<UnitArea>>();
        for(Allegeance a: Allegeance.values()){
            this.guardedAreas.put(a, new Array<UnitArea>());
            this.coveredAreas.put(a, new Array<UnitArea>());
        }
        this.recruits = new HashMap<Integer, IUnit>();
        this.tombItems = new HashMap<Integer, Item>();
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


    // ---------------- TILE MANAGEMENT ------------------------------------
    /*
     ADD A TILE:
     1) add a TileType
     2) add the texture in the tiles atlas
     30) (checking) check the correspondance between its texture name and its TileType name
     31) (optional) add specific rules...

    TILE METHODS HIERARCHY

     > set
     -> setTileAs
     -> plunderTile
     */



    public boolean setTile(int r, int c, TileType type){
        if(checkIndexes(r,c) && type != null){
            //reset tile
            recruits.remove(_getLootId(r, c));
            tombItems.remove(_getLootId(r, c));
            looted[r][c] = false;

            //set tile
            tiles[r][c] = type;
            notifyAllObservers(new int[]{r, c});
            return true;
        }
        return false;
    }

    public void setTileAs(int r, int c, TileType type, Object obj){
        if(obj != null && type != null){
            if(obj instanceof IUnit && type == TileType.VILLAGE && setTile(r,c, type)){
                recruits.put(_getLootId(r,c), (Unit)obj);
                this.looted[r][c] = true;
            }
            if(obj instanceof Item && type == TileType.ANCIENT_SITE && setTile(r,c, type)){
                tombItems.put(_getLootId(r, c), (Item)obj);
                this.looted[r][c] = true;
            }
        }
    }

    public TileType getTile(int r, int c) {
        if(checkIndexes(r,c)){
            return getTiles()[r][c];
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

    public boolean plunderTile(int r, int c){
        if(isTilePlunderable(r,c)){
            setTile(r, c, TileType.RUINS);
            return true;
        }
        return false;
    }

    public IUnit getVillageRecruit(int r, int c){
        if(isTileLooted(r,c)){
            IUnit recruit = recruits.get(_getLootId(r,c));
            return  recruit;
        }
        return null;
    }

    public Item getTombItem(int r, int c){
        if(isTileLooted(r,c)){
            Item loot = tombItems.get(_getLootId(r,c));
            return  loot;
        }
        return null;
    }

    private int _getLootId(int r, int c){
        return c + getNbColumns() * r;
    }

    public boolean addCoveredArea(int rowActor, int colActor){
        if(isTileOccupied(rowActor, colActor)) {
            IUnit actor = getUnit(rowActor, colActor);
            if(actor.isMobilized()) {
                int rangeMin = actor.getCurrentWeaponRangeMin(rowActor, colActor, this);
                int rangeMax = actor.getCurrentWeaponRangeMax(rowActor, colActor, this);
                Array<int[]> tiles = Utils.getEreaFromRange(this, rowActor, colActor, rangeMin, rangeMax);
                Area.UnitArea area = new Area.UnitArea(this, Data.AreaType.COVERING_FIRE, tiles, actor);
                this.coveredAreas.get(actor.getAllegeance()).add(area);
                notifyAllObservers(area);
                return true;
            }
        }
        return false;
    }

    public void removeCoveredArea(IUnit actor){
        if(actor != null && actor.isMobilized()){
            Array<Area.UnitArea> areas = this.coveredAreas.get(actor.getAllegeance());
            for (int i = 0; i < areas.size; i++) {
                if (areas.get(i).getActor() == actor) {
                    Area.UnitArea area = areas.removeIndex(i);
                    notifyAllObservers(area);
                }
            }

        }
    }

    public boolean addGuardedArea(int rowActor, int colActor){
        if(isTileOccupied(rowActor, colActor)) {
            IUnit actor = getUnit(rowActor, colActor);
            if(actor.isMobilized()) {
                Array<int[]> tiles = Utils.getEreaFromRange(this, rowActor, colActor, Data.GUARD_RANGE_MIN, Data.GUARD_RANGE_MAX);
                Area.UnitArea area = new Area.UnitArea(this, Data.AreaType.GUARD_RANGE, tiles, actor);
                this.guardedAreas.get(actor.getAllegeance()).add(area);
                notifyAllObservers(area);
                return true;
            }
        }
        return false;
    }

    public void removeGuardedArea(IUnit actor){
        if(actor != null && actor.isMobilized()){
            Array<Area.UnitArea> areas = this.guardedAreas.get(actor.getAllegeance());
            for (int i = 0; i < areas.size; i++) {
                if (areas.get(i).getActor() == actor) {
                    notifyAllObservers(areas.removeIndex(i));
                }
            }

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

    public boolean isTileDeploymentTile(int row, int col) {
        for(int i = 0; i < deploymentArea.size; i++){
            if(deploymentArea.get(i).length >= 2 && deploymentArea.get(i)[0] == row && deploymentArea.get(i)[1] == col){
                return true;
            }
        }
        return false;
    }

    public boolean checkIndexes(int r, int c) {
        return r > -1 && r < getNbRows() && c > -1 && c < getNbColumns();
    }

    public boolean isTileLooted(int row, int col){
        return checkIndexes(row,col) && this.looted[row][col];
    }

    public boolean isTileExisted(int r, int c){
        return checkIndexes(r,c) && getTile(r,c) != null;
    }

    public boolean isTilePlunderable(int r, int c){
        return isTileExisted(r,c) && getTile(r,c).isPlunderable();
    }

    public boolean isTileReachable(int row, int col, boolean pathfinder) {
        if(isTileExisted(row, col)){
            return getTile(row, col).isReachable() || (pathfinder && (getTile(row,col) == TileType.MOUNTAINS || getTile(row, col) == TileType.FOREST));
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

    public boolean isTileOccupiedByAlly(int row , int col, Allegeance allegeance){
        return isTileOccupied(row, col) && getUnit(row, col).isAllyWith(allegeance);
    }

    public boolean isTileOccupiedByFoe(int row , int col, Allegeance allegeance){
        return isTileOccupied(row, col) && !getUnit(row, col).isAllyWith(allegeance);
    }

    public boolean isTileCovered(int row, int col, Allegeance alliedAllegeance){
        boolean res = false;
        if(isTileExisted(row, col)){
            for(Allegeance a : Allegeance.values()) {
                if(a != alliedAllegeance) {
                    Array<Area.UnitArea> foeCoveredArea = coveredAreas.get(alliedAllegeance);
                    for (int i = 0; i < foeCoveredArea.size; i++) {
                        if (foeCoveredArea.get(i).contains(row, col)) {
                            res = true;
                            continue;
                        }
                    }
                }
            }
        }
        return res;
    }

    public boolean isTileGuarded(int row, int col, Allegeance alliedAllegeance){
        boolean res = false;
        if(isTileExisted(row, col)){
            Array<Area.UnitArea> allyGuardedArea = coveredAreas.get(alliedAllegeance);
            for(int i = 0; i < allyGuardedArea.size; i++){
                if(allyGuardedArea.get(i).contains(row, col)){
                    res = true;
                    continue;
                }
            }
        }
        return res;
    }



    //----------------- UNIT MANAGEMENT ----------------------




    public void randomlyDeployArmy(IArmy army){
        if(army != null) {

            Array<IUnit> mobilizedTroops = army.getMobilizedUnits();
            Array<int[]> deploymentsTile = getDeploymentArea();
            IUnit unit;
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

            while (remainingUnits.size > 0 && remainingAvailableTiles.size > 0) {
                coords = remainingAvailableTiles.removeIndex(Data.rand(remainingAvailableTiles.size));
                unit = remainingUnits.removeIndex(Data.rand(remainingUnits.size));
                deployUnit(coords[0], coords[1], unit);
            }

        }
    }

    public void deployUnit(int row, int col, IUnit unit){
        boolean alreadyDeployed = false;
        for(int r = 0; r < getNbRows(); r++){
            for(int c = 0; c < getNbRows(); c++){
                if(this.units[r][c] == unit){
                    alreadyDeployed = true;
                    continue;
                }
            }
            if(alreadyDeployed){
                continue;
            }
        }
        if(isTileAvailable(row, col, unit.has(Data.PassiveAbility.PATHFINDER)) &&  !alreadyDeployed && unit.getArmy() != null){
            this.units[row][col] = unit;
            notifyAllObservers(new int[]{row, col});
        }
    }

    private boolean isUnitAlreadydeployed(IUnit unit){
        for(int r = 0; r < getNbRows(); r++){
            for(int c = 0; c < getNbRows(); c++){
                if(this.units[r][c] == unit){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean switchUnitsPosition(int rowUnit1, int colUnit1, int rowUnit2, int colUnit2){
        if(isTileOccupied(rowUnit1, colUnit1) && isTileOccupied(rowUnit2, colUnit2)){
            IUnit unit1 = getUnit(rowUnit1, colUnit1);
            IUnit unit2 = getUnit(rowUnit2, colUnit2);
            if(isTileReachable(rowUnit1, colUnit1, unit2.has(Data.PassiveAbility.PATHFINDER) && isTileReachable(rowUnit2, colUnit2, unit1.has(Data.PassiveAbility.PATHFINDER)))){
                removeCoveredArea(unit1);
                removeCoveredArea(unit2);
                removeGuardedArea(unit1);
                removeGuardedArea(unit2);
                this.units[rowUnit2][colUnit2] = unit1;
                this.units[rowUnit1][colUnit1] = unit2;
                return true;
            }
        }
        return false;
    }

    public boolean moveUnit(int rowI, int colI, int rowf, int colf){
        if(isTileOccupied(rowI, colI)) {
            IUnit unit = getUnit(rowI, colI);
            if(isTileAvailable(rowf, colf, unit.has(Data.PassiveAbility.PATHFINDER))){
                removeCoveredArea(unit);
                removeGuardedArea(unit);
                this.units[rowf][colf] = unit;
                this.units[rowI][colI] = null;
                return true;
            }
        }
        return false;
    }

    public boolean isUnitGuarding(int rowUnit, int colUnit){
        if(isTileOccupied(rowUnit, colUnit)){
            IUnit unit = getUnit(rowUnit, colUnit);
            if(unit.isMobilized()) {
                Array<UnitArea> areas = getGuardedAreas().get(unit.getArmy().getAllegeance());
                for(int i = 0; i < areas.size; i++){
                    if(areas.get(i).getActor() == unit){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isUnitCovering(int rowUnit, int colUnit){
        if(isTileOccupied(rowUnit, colUnit)){
            IUnit unit = getUnit(rowUnit, colUnit);
            if(unit.isMobilized()) {
                Array<UnitArea> areas = getCoveredAreas().get(unit.getArmy().getAllegeance());
                for(int i = 0; i < areas.size; i++){
                    if(areas.get(i).getActor() == unit){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public IUnit getUnit(int row, int col){
        return this.units[row][col];
    }

    public Array<IUnit> getStillActiveUnits(int armyId) {
        Array<IUnit> activeUnits = new Array<IUnit>();
        for(int r =0; r<getNbRows();r++){
            for(int c = 0; c<getNbColumns(); c++){
                if(isTileOccupied(r, c) && getUnit(r, c).getArmy().getId() == armyId && !getUnit(r, c).isDone()){
                    activeUnits.add(getUnit(r, c));
                }
            }
        }
        return activeUnits;
    }

    public IUnit removeUnit(int row, int col){
        IUnit unit = this.units[row][col];
        this.units[row][col] = null;
        notifyAllObservers(unit);
        return unit;
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
        int[] pos = null;
        for(int r = getNbRows()-1; r > -1 ; r--){
            for(int c = 0; c < getNbColumns(); c++){
                if(isTileOccupied(r, c) && getUnit(r, c) == unit){
                    pos = new int[]{r, c};
                }
            }
        }
        return pos;
    }

    /**
     *
     * @param unit
     * @param row
     * @param col
     * @return whether or not there is a standard bearer at range if the given unit is standing on the given tile = {row, col}
     */
    public boolean  isStandardBearerAtRange(IUnit unit, int row, int col){
        int dist;
        if(isTileExisted(row, col)&& unit.isMobilized()) {
            int bannerRange = unit.getArmy().getBannerRange();
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
        Allegeance allegeance;

        CheckMoveMap(){ }

        public Array<int[]> getMoveArea(Battlefield bf, int rowActor, int colActor){
            Array<int[]> moveArea = new Array<int[]>();
            if(bf.isTileOccupied(rowActor, colActor)) {
                set(bf, rowActor, colActor, true);
                setTilesMRP();
                condemnTiles();
                moveArea = getArea(true);
            }
            return moveArea;
        }

        private void set( Battlefield bf, int rowActor, int colActor, boolean moveAreaOnly){
            if(bf.isTileOccupied(rowActor, colActor)) {
                // get actor relevant pieces of information
                IUnit actor = bf.getUnit(rowActor, colActor);
                this.pathfinder = actor.has(Data.PassiveAbility.PATHFINDER);
                this.moveRange = actor.hasMoved() ? 0 : actor.getAppMobility();
                this.allegeance = actor.getAllegeance();
                this.battlefield = bf;

                // set the check map dimensions and origin point
                int rows;
                int colunms;
                if(moveAreaOnly) {
                    this.rowOrigin = rowActor - moveRange;
                    this.colOrigin = colActor - moveRange;
                    this.rowRelActor = moveRange;
                    this.colRelActor = moveRange;
                    rows = 2 * moveRange + 1;
                    colunms = 2 * moveRange + 1;
                }else{
                    this.rowOrigin = 0;
                    this.colOrigin = 0;
                    this.rowRelActor = rowActor;
                    this.colRelActor = colActor;
                    rows = bf.getNbRows();
                    colunms = bf.getNbColumns();
                }

                if (rowOrigin < 0) {
                    rowRelActor += rowOrigin;
                    rows += rowOrigin;
                    rowOrigin = 0;
                }
                if (colOrigin < 0) {
                    colRelActor += colOrigin;
                    colunms += colOrigin;
                    colOrigin = 0;
                }

                if (rowOrigin + rows > bf.getNbRows()) {
                    rows = bf.getNbRows() - rowOrigin;
                }
                if (colOrigin + colunms > bf.getNbColumns()) {
                    colunms = bf.getNbColumns() - colOrigin;
                }
                checkTiles = new int[rows][colunms];
            }
        }

        private boolean checkIndexes(int r, int c){
            return 0 < checkTiles.length && 0 <= r && r < checkTiles.length && 0 <= c && c < checkTiles[0].length;
        }

        // MRP = mobility remaining points
        private void setTilesMRP(){
            checkTiles[rowRelActor][colRelActor] = moveRange;
            if(moveRange > 0){
                if(checkIndexes(rowRelActor + 1, colRelActor))
                    updateTilesMRP(rowRelActor + 1, colRelActor, moveRange  , Data.Orientation.SOUTH);
                if(checkIndexes(rowRelActor - 1, colRelActor))
                    updateTilesMRP(rowRelActor - 1, colRelActor, moveRange , Data.Orientation.NORTH);
                if(checkIndexes(rowRelActor, colRelActor + 1))
                    updateTilesMRP(rowRelActor , colRelActor + 1, moveRange , Data.Orientation.WEST);
                if(checkIndexes(rowRelActor , colRelActor - 1))
                    updateTilesMRP(rowRelActor , colRelActor - 1, moveRange , Data.Orientation.EAST);
            }

        }

        private void updateTilesMRP(int row, int col, int remainingMovePoints, Data.Orientation from) {
            if(remainingMovePoints > checkTiles[row][col]) {
                if (remainingMovePoints == 1) {
                    if (battlefield.isTileAvailable(rowOrigin + row, colOrigin + col, pathfinder)) {
                        checkTiles[row][col] = remainingMovePoints;
                    }
                } else if (remainingMovePoints > 1) {
                    if(battlefield.isTileReachable(rowOrigin+row, colOrigin+col, pathfinder) && !battlefield.isTileOccupiedByFoe(rowOrigin+row, colOrigin+col, allegeance)){
                        checkTiles[row][col] = remainingMovePoints;
                        if(from != Data.Orientation.NORTH && checkIndexes(row + 1, col))
                            updateTilesMRP(row + 1, col, remainingMovePoints - 1  , Data.Orientation.SOUTH);
                        if(from != Data.Orientation.SOUTH && checkIndexes(row - 1, colRelActor))
                            updateTilesMRP(row - 1, col, remainingMovePoints - 1 , Data.Orientation.NORTH);
                        if(from != Data.Orientation.EAST && checkIndexes(row, col + 1))
                            updateTilesMRP(row , col + 1, remainingMovePoints - 1 , Data.Orientation.WEST);
                        if(from != Data.Orientation.WEST && checkIndexes(row , col - 1))
                            updateTilesMRP(row , col - 1, remainingMovePoints - 1 , Data.Orientation.EAST);
                    }
                }
            }
        }

        /**
         * condemn tiles occupied by allies and therefore unreachable.
         */
        private void condemnTiles(){
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
                            if(battlefield.isTileOccupied(r + rowOrigin, c + colOrigin)) checkTiles[r][c] = 0;
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

        private Array<int[]> getArea(boolean moveArea){
            Array<int[]> area = new Array<int[]>();
            for(int r = 0; r < checkTiles.length; r++){
                for(int c = 0; c < checkTiles[0].length; c++){
                    if (checkTiles[r][c] > 0) {
                        area.add(new int[]{r + rowOrigin, c + colOrigin});
                    }
                }
            }
            //System.out.println(toString());
            return area;
        }


        @Override
        public String toString(){
            String str ="\nOrigin :"+rowOrigin+" "+colOrigin+"\n\n";
            for(int r = checkTiles.length - 1 ; r > -1 ; r--){
                for(int c = 0; c < checkTiles[0].length; c++){
                    str += " "+checkTiles[r][c];
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
        return checkmap.getMoveArea(this, rowActor, colActor);
    }


    /**
     *
     * Algorythm A* : https://www.youtube.com/watch?v=-L-WgKMFuhE
     *
     * get the shortest path of a target tile using the A* algorithm
     *
     * @return an array like that {[row, col]} representing the shortest path from one tile to another
     */
    public Array<int[]>  getShortestPath(int rowI, int colI, int rowf, int colf, boolean pathfinder, Allegeance allegeance, boolean avoidCoveredArea){
        Array<int[]> res = new Array<int[]>();

        if(isTileAvailable(rowf, colf, pathfinder) && allegeance != null) {

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

                // path found
                if (current.getRow() == rowf && current.getCol() == colf) break;

                // get available neighbor nodes which are not yet in the closed list
                PathNode node;
                neighbours = new Array<PathNode>();
                if (isTileReachable(current.row + 1, current.col, pathfinder) && !isTileOccupiedByFoe(current.row + 1, current.col, allegeance)) {
                    node = new PathNode(current.row + 1, current.col, rowf, colf, current, this);
                    if (!closed.contains(node, false)) {
                        neighbours.add(node);
                    }
                }
                if (isTileReachable(current.row, current.col + 1, pathfinder) && !isTileOccupiedByFoe(current.row, current.col + 1, allegeance)) {
                    node = new PathNode(current.row, current.col + 1, rowf, colf, current, this);
                    if (!closed.contains(node, false)) {
                        neighbours.add(node);
                    }
                }
                if (isTileReachable(current.row - 1, current.col, pathfinder) && !isTileOccupiedByFoe(current.row - 1, current.col, allegeance)) {
                    node = new PathNode(current.row - 1, current.col, rowf, colf, current, this);
                    if (!closed.contains(node, false)) {
                        neighbours.add(node);
                    }
                }
                if (isTileReachable(current.row, current.col - 1, pathfinder) && !isTileOccupiedByFoe(current.row, current.col - 1, allegeance)) {
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

            for (int i = 0; i < path.size; i++) {
                res.add(new int[]{path.get(i).getRow(), path.get(i).getCol()});
            }
        }

        return res;
    }




    //-------------------GETTERS & SETTERS -------------------------




    public TileType[][] getTiles() {
        return tiles;
    }

    public int getWidth() {
        return getNbColumns();
    }

    public int getHeight() {
        return getNbRows();
    }

    public Array<int[]> getDeploymentArea() {
        return deploymentArea;
    }

    public void addDeploymentTile(int row, int col){
        if(isTileReachable(row, col, false)){
            this.deploymentArea.add(new int[]{row, col});
        }
    }

    public HashMap<Allegeance, Array<Area.UnitArea>> getGuardedAreas() {
        return guardedAreas;
    }

    public HashMap<Allegeance, Array<Area.UnitArea>> getCoveredAreas() {
        return coveredAreas;
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




    // --------------- BUILD NOTIFICATION -------------------------------------





    public static class BuildNotif {
        public int row;
        public int col;
        public TileType tile;

        private static BuildNotif msg = new BuildNotif();

        private BuildNotif(){ }

        public static BuildNotif get(int row, int col, TileType tile){
            msg.row = row;
            msg.col = col;
            msg.tile = tile;
            return msg;
        }

    }


}
