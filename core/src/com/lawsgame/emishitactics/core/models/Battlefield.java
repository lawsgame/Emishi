package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.constants.Data.ActionChoice;
import com.lawsgame.emishitactics.core.constants.Data.Allegeance;
import com.lawsgame.emishitactics.core.constants.Data.Item;
import com.lawsgame.emishitactics.core.constants.Data.PassiveAbility;
import com.lawsgame.emishitactics.core.constants.Data.R;
import com.lawsgame.emishitactics.core.constants.Data.TargetType;
import com.lawsgame.emishitactics.core.constants.Data.TileType;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;

import java.util.HashMap;


/*
 *  TODO:
 * 2) solve the requiredRange issue on the following methods
 *      - getActionArea
 *      - getShortestPathToAttack
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
    private Unit[][] units;
    private boolean[][] looted;
    private Array<int[]> deploymentArea;

    private HashMap<Integer, Unit> recruits;
    private HashMap<Integer, Item> tombItems;


    public Battlefield (int nbRows, int nbCols){
        if(nbRows > 0 && nbCols > 0) {
            this.tiles = new TileType[nbRows][nbCols];
            this.looted = new boolean[nbRows][nbCols];
            this.units = new Unit[nbRows][nbCols];
        }
        this.deploymentArea = new Array<int[]>();
        this.recruits = new HashMap<Integer, Unit>();
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

     > setTile
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
            if(obj instanceof Unit && type == TileType.VILLAGE && setTile(r,c, type)){
                recruits.put(_getLootId(r,c), (Unit)obj);
                this.looted[r][c] = true;
            }
            if(obj instanceof Item && type == TileType.TOMB && setTile(r,c, type)){
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

    public Unit getVillageRecruit(int r, int c){
        if(isTileLooted(r,c)){
            Unit recruit = recruits.get(_getLootId(r,c));
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


    //-------------- TILE STATE CHECK METHODS HIERARCHY ----------------------
    /*
    if one is true, its parent are as well

    checkIndexes
    > isTileLooted
    > isTileExisted
    -> isTileType
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

    public boolean isTileType(int r, int c, TileType type) {
        return isTileExisted(r, c) && getTile(r , c) == type;}

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

    public boolean isTileOccupiedBySameSquad(int row , int col, Unit unit){
        return isTileOccupied(row, col) && this.units[row][col].sameSquadAs(unit);
    }

    public boolean isTileOccupiedByAlly(int row , int col, Allegeance allegeance){
        return isTileOccupied(row, col) && getUnit(row, col).sideWith(allegeance);
    }

    public boolean isTileOccupiedByFoe(int row , int col, Allegeance allegeance){
        return isTileOccupied(row, col) && getUnit(row, col).fightWith(allegeance);
    }



    //----------------- UNIT MANAGEMENT ----------------------




    public void randomlyDeployArmy(AArmy army){
        if(army != null) {
            Array<Unit> mobilizedTroops = army.getMobilizedUnits();
            Array<int[]> deploymentsTile = getDeploymentArea();
            Unit unit;
            int[] coords;
            Array<int[]> remainingAvailableTiles = new Array<int[]>();
            Array<Unit> remainingUnits = new Array<Unit>();


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
                coords = remainingAvailableTiles.removeIndex(R.getR().nextInt(remainingAvailableTiles.size));
                unit = remainingUnits.removeIndex(R.getR().nextInt(remainingUnits.size));
                deployUnit(coords[0], coords[1], unit);
            }

        }
    }

    public void deployUnit(int row, int col, Unit unit){
        if(isTileAvailable(row, col, unit.has(PassiveAbility.PATHFINDER)) &&  !isUnitAlreadydeployed(unit) && unit.getArmy() != null){
            this.units[row][col] = unit;
            notifyAllObservers(new int[]{row, col});
        }
    }

    public boolean switchUnitsPosition(int rowUnit1, int colUnit1, int rowUnit2, int colUnit2){
        if(isTileOccupied(rowUnit1, colUnit1) && isTileOccupied(rowUnit2, colUnit2) && Utils.dist(rowUnit1, colUnit1, rowUnit2, colUnit2) == 1){
            Unit unit1 = getUnit(rowUnit1, colUnit1);
            Unit unit2 = getUnit(rowUnit2, colUnit2);
            if(isTileReachable(rowUnit1, colUnit1, unit2.has(PassiveAbility.PATHFINDER) && isTileReachable(rowUnit2, colUnit2, unit1.has(PassiveAbility.PATHFINDER)))){
                notifyAllObservers(new int[]{rowUnit1, colUnit1, rowUnit2, colUnit2});
                this.units[rowUnit2][colUnit2] = unit1;
                this.units[rowUnit1][colUnit1] = unit2;
                return true;
            }
        }
        return false;
    }

    public Array<int[]> moveUnit(int rowI, int colI, int rowf, int colf){
        Array<int[]> path  = new Array<int[]>();
        if(isTileOccupied(rowI, colI)) {
            Unit unit = getUnit(rowI, colI);
            if(isTileAvailable(rowf, colf, unit.has(PassiveAbility.PATHFINDER))){
                path  = getShortestPath(rowI, colI, rowf, colf, unit.has(PassiveAbility.PATHFINDER), unit.getAllegeance());
                notifyAllObservers(path);
                this.units[rowf][colf] = unit;
                this.units[rowI][colI] = null;
                return path;
            }
        }
        return path;
    }

    public boolean isUnitAlreadydeployed(Unit unit){
        for(int r = 0; r < getNbRows(); r++){
            for(int c = 0; c < getNbRows(); c++){
                if(this.units[r][c] == unit){
                    return true;
                }
            }
        }
        return false;
    }

    public Unit getUnit(int row, int col){
        return this.units[row][col];
    }

    public Array<Unit> getStillActiveUnits(int armyId) {
        Array<Unit> activeUnits = new Array<Unit>();
        for(int r =0; r<getNbRows();r++){
            for(int c = 0; c<getNbColumns(); c++){
                if(isTileOccupied(r, c) && getUnit(r, c).getArmy().getId() == armyId && !getUnit(r, c).isDone()){
                    activeUnits.add(getUnit(r, c));
                }
            }
        }
        return activeUnits;
    }

    public Unit removeUnit(int row, int col){
        Unit unit = this.units[row][col];
        this.units[row][col] = null;
        notifyAllObservers(unit);
        return unit;
    }

    public void build(int rowActor, int colActor, int rowTarget, int colTarget, boolean bridge){
        boolean contructable = false;
        if(isTileExisted(rowTarget, colTarget) && !isTileOccupied(rowTarget, colTarget)){

            TileType tile = getTile(rowTarget, colTarget);
            if(bridge && tile == TileType.SHALLOWS){
                if (isTileExisted(rowTarget + 1, colTarget) && isTileExisted(rowTarget - 1, colTarget) && getTile(rowTarget + 1, colTarget) == TileType.PLAIN && getTile(rowTarget - 1, colTarget) == TileType.PLAIN) {
                    contructable = true;
                } else if (isTileExisted(rowTarget, colTarget + 1) && isTileExisted(rowTarget, colTarget - 1) && getTile(rowTarget, colTarget+1) == TileType.PLAIN && getTile(rowTarget, colTarget-1) == TileType.PLAIN) {
                    contructable = true;
                }
            }else if (!bridge && tile == TileType.PLAIN){
                contructable = true;
            }

            if(contructable && isTileOccupied(rowActor, colActor)){
                Unit actor = getUnit(rowActor, colActor);
                actor.setOrientation(Utils.getOrientationFromCoords(rowActor, colActor, rowTarget, colTarget));
                actor.notifyAllObservers(Data.AnimationId.BUILD);
                getTiles()[rowTarget][colTarget] = (bridge) ? TileType.BRIDGE : TileType.WATCH_TOWER;
                notifyAllObservers(BuildMessage.get(rowTarget, colTarget, tiles[rowTarget][colTarget]));
            }

        }
    }

    public void push(int rowActor, int colActor, int rowTarget, int colTarget){

        if(isTileOccupied(rowActor, colActor) && isTileOccupied(rowTarget,colActor) && Utils.dist(rowActor, colActor, rowTarget, colTarget) == 1){
            Unit actor = getUnit(rowActor, colActor);
            Unit target = getUnit(rowTarget, colTarget);
            boolean pathfinder = target.has(PassiveAbility.PATHFINDER);

            if(!target.isHorseman()){
                if(rowActor < rowTarget && isTileAvailable(rowTarget + 1, colTarget, pathfinder)){
                    this.units[rowTarget][rowTarget] = null;
                    this.units[rowTarget + 1][rowTarget] = target;
                    actor.notifyAllObservers(Data.AnimationId.PUSH);
                    target.notifyAllObservers(Utils.getOrientationFromCoords(rowActor, colActor, rowTarget, colTarget));
                }else if(rowActor > rowTarget && isTileAvailable(rowTarget - 1, colTarget, pathfinder)){
                    this.units[rowTarget][rowTarget] = null;
                    this.units[rowTarget + 1][rowTarget] = target;
                    actor.notifyAllObservers(Data.AnimationId.PUSH);
                    target.notifyAllObservers(Utils.getOrientationFromCoords(rowActor, colActor, rowTarget, colTarget));
                }else if(colActor < colTarget && isTileAvailable(rowTarget, colTarget + 1, pathfinder)){
                    this.units[rowTarget][rowTarget] = null;
                    this.units[rowTarget + 1][rowTarget] = target;
                    actor.notifyAllObservers(Data.AnimationId.PUSH);
                    target.notifyAllObservers(Utils.getOrientationFromCoords(rowActor, colActor, rowTarget, colTarget));
                }else if(colActor > colTarget && isTileAvailable(rowTarget, colTarget - 1, pathfinder)){
                    this.units[rowTarget][rowTarget] = null;
                    this.units[rowTarget + 1][rowTarget] = target;
                    actor.notifyAllObservers(Data.AnimationId.PUSH);
                    target.notifyAllObservers(Utils.getOrientationFromCoords(rowActor, colActor, rowTarget, colTarget));
                }
            }
        }
    }

    public Unit getUnitByName(String name) {
        Unit target = null;
        for(int r = getNbRows()-1; r > -1 ; r--){
            for(int c = 0; c < getNbColumns(); c++){
                if(isTileOccupied(r, c) && getUnit(r, c).getName().equals(name)){
                    target = getUnit(r, c);
                }
            }
        }
        return target;
    }

    public int[] getUnitPos(Unit unit){
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
    public boolean  isStandardBearerAtRange(Unit unit, int row, int col){
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

     public boolean isGuardianAtRange(int row, int  col){
        if(isTileOccupied(row, col)) {
            Unit unit = getUnit(row, col);
            for (int r = row - 1; r < row + 2; r++) {
                for (int c = col - 1; c < col + 2; c++) {
                    if (isTileOccupied(r, c)
                            && Utils.dist(row, col, r, c) > 0
                            && getUnit(r, c).sameSquadAs(unit)
                            && getUnit(r, c).isGuarding()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Array<Unit> getNearbyGardiens(int row, int col) {
        Array<Unit> units = new Array<Unit>();
        if(isTileOccupied(row, col)) {
            Unit unit = getUnit(row, col);
            for (int r = row - 1; r < row + 2; r++) {
                for (int c = col - 1; c < col + 2; c++) {
                    if (isTileOccupied(r, c)
                            && Utils.dist(row, col, r, c) > 0
                            && getUnit(r, c).sameSquadAs(unit)
                            && getUnit(r, c).isGuarding()) {
                        units.add(getUnit(r, c));
                    }
                }
            }
        }
        return units;
    }

    public void setAsPlain() {
        if(tiles != null){
            for(int r = 0; r < getNbRows(); r++){
                for(int c = 0; c < getNbColumns(); c++){
                    tiles[r][c] = TileType.PLAIN;
                }
            }
        }
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
            set(bf, rowActor, colActor);
            setTilesMRP();
            condemnTiles();
            return getMoveArea();
        }

        private void set( Battlefield bf, int rowActor, int colActor){
            if(bf.isTileOccupied(rowActor, colActor)) {
                // get actor relevant pieces of information
                Unit actor = bf.getUnit(rowActor, colActor);
                this.pathfinder = actor.has(PassiveAbility.PATHFINDER);
                this.moveRange = actor.hasMoved() ? 0 : actor.getCurrentMob();
                this.allegeance = actor.getAllegeance();
                this.battlefield = bf;

                // set the check map dimensions and origin point
                this.rowOrigin = rowActor - moveRange;
                this.colOrigin = colActor - moveRange;
                this.rowRelActor = moveRange;
                this.colRelActor = moveRange;
                int rows = 2 * moveRange + 1;
                int colunms = 2 * moveRange + 1;

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

        private Array<int[]> getMoveArea(){
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
     * @param rowActor
     * @param colActor
     * @param choice
     * @return fetch all tiles where the given unit can act upon without checking if the action can be performed
     */
    public Array<int[]> getActionArea(int rowActor, int colActor, ActionChoice choice){
        Array<int[]>  actionArea = new Array<int[]>();
        if(isTileOccupied(rowActor, colActor)){
            Unit actor = getUnit(rowActor, colActor);


            if (choice.getTargetType() == TargetType.SPECIFIC) {
                if (choice == ActionChoice.BUILD) {
                    if(!actor.isBuildingResourcesConsumed()) {
                        int[][] neighborTiles = new int[][]{{rowActor + 1, colActor}, {rowActor - 1, colActor}, {rowActor, colActor + 1}, {rowActor, colActor - 1}};
                        int r;
                        int c;
                        for (int i = 0; i < neighborTiles.length; i++) {
                            r = neighborTiles[i][0];
                            c = neighborTiles[i][1];
                            if (isTileExisted(r, c)) {
                                if (getTile(r, c) == TileType.PLAIN) {

                                    actionArea.add(new int[]{r, c});

                                } else if (getTile(r, c) == TileType.SHALLOWS) {
                                    if (isTileExisted(r + 1, c) && isTileExisted(r - 1, c) && getTile(r + 1, c) == TileType.PLAIN && getTile(r - 1, c) == TileType.PLAIN) {

                                        actionArea.add(new int[]{r, c});

                                    } else if (isTileExisted(r, c + 1) && isTileExisted(r, c - 1) && getTile(r , c + 1) == TileType.PLAIN && getTile(r, c - 1) == TileType.PLAIN) {

                                        actionArea.add(new int[]{r, c});
                                    }
                                }
                            }
                        }
                    }
                } else if (choice == ActionChoice.WALK) {

                    actionArea.addAll(getMoveArea(rowActor, colActor));

                } else if( choice == ActionChoice.PUSH){

                    if(!actor.isHorseman()){
                        Unit target;
                        if (isTileOccupiedByAlly(rowActor + 1, colActor, actor.getAllegeance())) {
                            target = getUnit(rowActor + 1, colActor);
                            if (!target.isHorseman() && isTileAvailable(rowActor + 2, colActor, target.has(PassiveAbility.PATHFINDER))) {
                                actionArea.add(new int[]{rowActor + 1, colActor});
                            }
                        }
                        if (isTileOccupiedByAlly(rowActor - 1, colActor, actor.getAllegeance())) {
                            target = getUnit(rowActor - 1, colActor);
                            if (!target.isHorseman() && isTileAvailable(rowActor - 2, colActor, target.has(PassiveAbility.PATHFINDER))) {
                                actionArea.add(new int[]{rowActor - 1, colActor});
                            }
                        }
                        if (isTileOccupiedByAlly(rowActor, colActor+1, actor.getAllegeance())) {
                            target = getUnit(rowActor, colActor+1);
                            if (!target.isHorseman() && isTileAvailable(rowActor, colActor-2, target.has(PassiveAbility.PATHFINDER))) {
                                actionArea.add(new int[]{rowActor, colActor-2});
                            }
                        }
                        if (isTileOccupiedByAlly(rowActor, colActor-1, actor.getAllegeance())) {
                            target = getUnit(rowActor, colActor-1);
                            if (!target.isHorseman() && isTileAvailable(rowActor, colActor-2, target.has(PassiveAbility.PATHFINDER))) {
                                actionArea.add(new int[]{rowActor, colActor-1});
                            }
                        }

                    }
                }
            } else {
                if (choice.getTargetType() == TargetType.ONE_SELF) {

                    actionArea.add(new int[]{rowActor, colActor});

                } else {
                    //generic algorithm
                    int rangeMin = getActionRangeMin(actor, rowActor, colActor, choice);
                    int rangeMax = getActionRangeMax(actor, rowActor, colActor, choice);

                    actionArea.addAll(Utils.getEreaFromRange(this, rowActor, colActor, rangeMin, rangeMax));

                }
            }
        }
        return actionArea;
    }

    /**
     * AI ORIENTED METHOD
     *
     * check the third requirements to perform an action
     * - target availability requirements
     *
     * @param actor
     * @param row
     * @param col
     * @param choice
     * @return whether or not ANY TARGET is at range by the actor performing the given action if one's is standing the tile (row, col)
     * while ignoring the actor's history and the unit other requirements to actually perform this action, namely : weapon/item and ability requirements.
     */
    public boolean atActionRange(Unit actor, int row, int col, ActionChoice choice){
        boolean validate = false;
        if(isTileAvailable(row, col, actor.has(PassiveAbility.PATHFINDER)) || (isTileOccupied(row, col) && getUnit(row, col) == actor)){
            if (choice.getTargetType() == TargetType.SPECIFIC) {
                if (choice == ActionChoice.BUILD) {
                    int[][] neighborTiles = new int[][]{{row + 1, col}, {row - 1, col}, {row, col + 1}, {row, col - 1}};
                    int r;
                    int c;
                    for (int i = 0; i < neighborTiles.length; i++) {
                        r = neighborTiles[i][0];
                        c = neighborTiles[i][1];
                        if (isTileExisted(r, c)) {
                            if (getTile(r, c) == TileType.PLAIN) {

                                validate =  true;
                                break;

                            } else if (getTile(r, c) == TileType.SHALLOWS) {
                                if (isTileExisted(r + 1, c) && isTileExisted(r - 1, c) && getTile(r + 1, c) == TileType.PLAIN && getTile(r - 1, c) == TileType.PLAIN) {

                                    validate = true;
                                    break;

                                } else if (isTileExisted(r, c + 1) && isTileExisted(r, c - 1) && getTile(r + 1, c) == TileType.PLAIN && getTile(r - 1, c) == TileType.PLAIN) {

                                    validate =  true;
                                    break;
                                }
                            }
                        }
                    }
                } else if (choice == ActionChoice.WALK) {

                    validate = getMoveArea(row, col).size > 0;

                } else if (choice == ActionChoice.PUSH){

                    Unit target;
                    if(isTileOccupiedByAlly(row + 1, col, actor.getAllegeance())){
                        target = getUnit(row + 1, col);
                        if(!target.isHorseman() && isTileAvailable(row + 2, col, getUnit(row + 1, col).has(PassiveAbility.PATHFINDER))){
                            validate = true;
                        }
                    }
                    if(!validate && isTileOccupiedByAlly(row - 1, col, actor.getAllegeance())){
                        target = getUnit(row - 1, col);
                        if(!target.isHorseman() && isTileAvailable(row - 2, col, getUnit(row - 1, col).has(PassiveAbility.PATHFINDER))){
                            validate = true;
                        }
                    }
                    if(!validate && isTileOccupiedByAlly(row, col + 1, actor.getAllegeance())){
                        target = getUnit(row, col + 1);
                        if(!target.isHorseman() && isTileAvailable(row, col + 2, getUnit(row, col + 1).has(PassiveAbility.PATHFINDER))){
                            validate = true;
                        }
                    }
                    if(!validate && isTileOccupiedByAlly(row, col - 1, actor.getAllegeance())){
                        target = getUnit(row, col - 1);
                        if(!target.isHorseman() && isTileAvailable(row, col - 2, getUnit(row, col - 1).has(PassiveAbility.PATHFINDER))){
                            validate = true;
                        }
                    }

                }
            } else {
                if (choice.getTargetType() == TargetType.ONE_SELF || choice.getImpactAreaSize() > 1) {

                    validate = true;

                } else {
                    //generic algorithm
                    int rangeMin = getActionRangeMin(actor, row, col, choice);
                    int rangeMax = getActionRangeMax(actor, row, col, choice);
                    int dist;


                    for (int r = row - rangeMax; r <= row + rangeMax; r++) {
                        for (int c = col - rangeMax; c <= col + rangeMax; c++) {
                            dist = Utils.dist(row, col, r, c);
                            if (rangeMin <= dist && dist <= rangeMax) {

                                if (isTileOccupiedByAlly(r, c, actor.getAllegeance())
                                        && (choice.getTargetType() == TargetType.ALLY
                                        || (choice.getTargetType() == TargetType.WOUNDED_ALLY && getUnit(r, c).isWounded())
                                        || (choice.getTargetType() == TargetType.FOOTMAN_ALLY && !getUnit(r, c).isHorseman()))) {

                                    validate = true;
                                    break;

                                } else if (isTileOccupiedByFoe(r, c, actor.getAllegeance()) && choice.getTargetType() == TargetType.ENEMY) {

                                    validate = true;
                                    break;

                                }
                            }
                        }
                    }
                }
            }
        }
        return validate;
    }

    /**
     * TARGET CHECKING
     * @param actor
     * @param choice
     * @return whether or not THIS SPECIFIC TARGET is at range by the actor performing the given action if one's is standing the tile (rowActor, colActor)
     * while ignoring the actor's history and the unit other requirements to actually perform this action, namely : weapon/item and ability requirements.
     */
    public boolean isTargetValid(Unit actor, int rowActor, int colActor, int rowTarget, int colTarget, ActionChoice choice) {
        boolean validate = false;
        if (isTileExisted(rowTarget, colTarget)
                && (isTileAvailable(rowActor, colActor, actor.has(PassiveAbility.PATHFINDER)) || (isTileOccupied(rowActor, colActor) && actor == getUnit(rowActor, colActor)))){


            if (choice.getTargetType() == TargetType.SPECIFIC) {
                if(choice == ActionChoice.PUSH){
                    if(isTileOccupiedByAlly(rowTarget, colTarget, actor.getAllegeance())
                            && !getUnit(rowTarget, colTarget).isHorseman()
                            && Utils.dist(rowActor, colActor, rowTarget, colTarget) == 1){
                        Unit target = getUnit(rowTarget, colTarget);
                        if(rowActor < rowTarget && isTileAvailable(rowTarget + 1, colTarget, target.has(PassiveAbility.PATHFINDER)))
                            validate = true;
                        else if(rowActor > rowTarget && isTileAvailable(rowTarget - 1, colTarget, target.has(PassiveAbility.PATHFINDER)))
                            validate = true;
                        else if(rowActor < rowTarget && isTileAvailable(rowTarget + 1, colTarget, target.has(PassiveAbility.PATHFINDER)))
                            validate = true;
                        else if(rowActor < rowTarget && isTileAvailable(rowTarget + 1, colTarget, target.has(PassiveAbility.PATHFINDER)))
                            validate = true;

                    }
                }else if(choice == ActionChoice.WALK){
                    if(Utils.dist(rowActor, colActor, rowTarget, colTarget) > 0){
                        Array<int[]> path = getShortestPath(rowActor, colActor, rowTarget, colTarget, actor.has(PassiveAbility.PATHFINDER), actor.getAllegeance());
                        if(path.size > 0 && path.size <= actor.getCurrentMob()){
                            validate = true;
                        }
                    }
                }else if(choice == ActionChoice.BUILD){
                    if(Utils.dist(rowActor, colActor, rowTarget, colTarget) == 1) {
                        if(isTileType(rowTarget, colTarget, TileType.PLAIN)){
                            validate = true;
                        }else if(isTileType(rowTarget, colTarget, TileType.SHALLOWS) ){
                            if(isTileType(rowTarget + 1, colTarget, TileType.PLAIN) && isTileType(rowTarget - 1, colTarget, TileType.PLAIN)){
                                validate = true;
                            }else if(isTileType(rowTarget, colTarget + 1, TileType.PLAIN) && isTileType(rowTarget, colTarget - 1, TileType.PLAIN)){
                                validate = true;
                            }
                        }
                    }
                }
            }else if(choice.getTargetType() == TargetType.ONE_SELF)
                validate = rowActor == rowTarget && colActor == colTarget;
            else{
                int rangeMin = getActionRangeMin(actor, rowActor, colActor, choice);
                int rangeMax = getActionRangeMax(actor, rowActor, colActor, choice);
                int dist = Utils.dist(rowActor, colActor, rowTarget, colTarget);
                boolean atRange = rangeMin <= dist && dist <= rangeMax;

                if(!atRange && choice.getImpactAreaSize() > 1){
                    Array<int[]> impactArea = getTargetFromCollateral(choice, rowActor, colActor, rowTarget, colTarget);
                    for(int i =0; i < impactArea.size; i++){
                        dist = Utils.dist(rowActor, colActor, impactArea.get(i)[0], impactArea.get(i)[1]);
                        atRange = atRange || (rangeMin <= dist && dist <= rangeMax);
                    }
                }

                if (atRange) {
                    if (isTileOccupiedByAlly(rowTarget, colTarget, actor.getAllegeance())) {
                        Unit target = getUnit(rowTarget, colTarget);
                        if(choice.getTargetType() == TargetType.ALLY){
                            validate = true;
                        }else if(choice.getTargetType() == TargetType.FOOTMAN_ALLY && !target.isHorseman()){
                            validate = true;
                        }else if(choice.getTargetType() == TargetType.WOUNDED_ALLY && target.isWounded()){
                            validate = true;
                        }
                    } else if (isTileOccupiedByFoe(rowTarget, colTarget, actor.getAllegeance()) && choice.getTargetType() == TargetType.ENEMY){
                        validate = true;
                    }
                }
            }
        }
        return validate;
    }

    /**
     *
     * @param choice
     * @param rowActor
     * @param colActor
     * @param rowImpactTile
     * @param colImpactTile
     * @return get all possible target tile knowing thtat the given tile is within the impact area
     */
    private Array<int[]> getTargetFromCollateral(ActionChoice choice, int rowActor, int colActor, int rowImpactTile, int colImpactTile) {
        Array<int[]> possibleTargetTiles = new Array<int[]>();
        Array<int[]> impactArea;
        int row;
        int col;
        for(Data.Orientation or: Data.Orientation.values()){
            impactArea = choice.getOrientedImpactArea(or);
            for(int i = 0; i < impactArea.size; i++){
                row =rowImpactTile - impactArea.get(i)[0];
                col =colImpactTile - impactArea.get(i)[1];
                if(isTileExisted(row, col) && or == Utils.getOrientationFromCoords(rowActor, colActor, row, col)){
                    possibleTargetTiles.add(new int[]{row, col});
                }
            }
        }
        return possibleTargetTiles;
    }


    /**
     *
     * @param actor
     * @param row
     * @param col
     * @param choice
     * @return the minimal range of actor performing the given action while standing on the given tile
     */
    public int getActionRangeMin(Unit actor, int row, int col, ActionChoice choice){
        int rangeMin = 0;
        if(isTileExisted(row, col)){
            rangeMin = (choice.isWeaponBasedRange()) ? actor.getCurrentRangeMin() : choice.getRangeMin();
        }
        return rangeMin;
    }
    /**
     *
     * @param actor
     * @param row
     * @param col
     * @param choice
     * @return the maximal range of actor performing the given action while standing on the given tile
     */
    public int getActionRangeMax(Unit actor, int row, int col, ActionChoice choice){
        int rangeMin = 0;
        if(isTileExisted(row, col)){
            TileType actorTile = getTile(row, col);
            boolean bannerAtRange = isStandardBearerAtRange(actor, row, col);
            rangeMin = (choice.isWeaponBasedRange()) ? actor.getCurrentRangeMax(actorTile, bannerAtRange) : choice.getRangeMin();
        }
        return rangeMin;
    }


    /**
     * get the shortest path of a target tile using the A* algorithm
     *
     * @return an array like that {[row, col]} representing the shortest path from one tile to another
     */
    public Array<int[]>  getShortestPath(int rowI, int colI, int rowf, int colf, boolean pathfinder, Allegeance allegeance){
        Array<int[]> res = new Array<int[]>();

        if(isTileAvailable(rowf, colf, pathfinder)) {
            Array<PathNode> path = new Array<PathNode>();
            Array<PathNode> opened = new Array<PathNode>();
            Array<PathNode> closed = new Array<PathNode>();
            opened.add(new PathNode(rowI, colI, rowf, colf, null, this));
            PathNode current = opened.get(0);
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


    /**
     *
     * Algorythm A* : https://www.youtube.com/watch?v=-L-WgKMFuhE
     *
     * the initial tile is the one of the target.
     *
     * @return shortest path to perform the given action upon the given target
     */
    public Array<int[]>  getShortestPath(int rowActor, int colActor, int rowTarget, int colTarget, ActionChoice choice){
        //TODO:
        return null;
    }

    /**
     *
     * @param choice
     * @param rowActor
     * @param colActor
     * @param rowTarget
     * @param colTarget
     * @return the relevantly oriented impact area of an action performed by an actor while targeting the tile {rowTarget, colTarget}
     */
    public Array<int[]> getImpactArea(ActionChoice choice, int rowActor, int colActor, int rowTarget, int colTarget){
        Array<int[]> orientedArea = choice.getOrientedImpactArea(Utils.getOrientationFromCoords(rowActor, colActor, rowTarget, colTarget));
        if(isTileExisted(rowTarget, colTarget)) {
            for (int i = 0; i < orientedArea.size; i++) {
                orientedArea.get(i)[0] += rowTarget;
                orientedArea.get(i)[1] += colTarget;
                if (!isTileExisted(orientedArea.get(i)[0], orientedArea.get(i)[1])) {
                    orientedArea.removeIndex(i);
                    i--;
                }
            }
            orientedArea.add(new int[]{rowTarget, colTarget});
        }
        return orientedArea;
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


    public static class StateNode extends PathNode{
        protected boolean atAttackRange;

        public StateNode(int row, int col, int rowf, int colf, PathNode parent, Battlefield bf, boolean atAttackRange) {
            super(row, col, rowf, colf, parent, bf);
            this.atAttackRange = atAttackRange;
            if(atAttackRange) this.distSource = 0;
        }

        /**
         * @return all nodes going through each parent until one parent is at attack requiredRange
          */

        @Override
        public Array<PathNode> getPath(){
            Array<PathNode> bestpath;
            if(atAttackRange){
                bestpath = new Array<PathNode>();
                bestpath.add(this);
                return bestpath;

            }
            StateNode parent0 = (StateNode)parent;
            bestpath = parent0.getPath();
            bestpath.add(this);
            return bestpath;

        }

        @Override
        public String toString(){
            return super.toString() + " attackrange: "+atAttackRange;
        }

    }

    // --------------- BUILD NOTIFICATION -------------------------------------

    public static class BuildMessage {
        public int row;
        public int col;
        public TileType tile;

        private static BuildMessage msg = new BuildMessage();

        private BuildMessage(){ }

        public static BuildMessage get(int row, int col, TileType tile){
            msg.row = row;
            msg.col = col;
            msg.tile = tile;
            return msg;
        }

    }
}
