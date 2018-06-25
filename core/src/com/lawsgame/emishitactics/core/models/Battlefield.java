package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data.*;
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
 * battle field tiles coordinate system
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

    private CheckMap checkmap;

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

     resetTile
     > setTile
     -> setTileAs
     -> plunderTile
     */



    public boolean setTile(int r, int c, TileType type){
        if(checkIndexes(r,c) && type != null){
            _resetTile(r,c);
            getTiles()[r][c] = type;
            notifyAllObservers(new int[]{r, c});
            return true;
        }
        return false;
    }

    private void _resetTile(int r, int c){
        if(checkIndexes(r,c)) {
            tiles[r][c] = null;
            looted[r][c] = false;
            recruits.remove(_getLootId(r, c));
            tombItems.remove(_getLootId(r, c));
        }
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
    -> isTilePlunderable
    -> isTileReachable
    |-> isTileAvaible
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




    public void randomlyDeployArmy(AbstractArmy army){
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

    public boolean deployUnit(int row, int col, Unit unit){
        if(isTileAvailable(row, col, unit.has(PassiveAbility.PATHFINDER)) && !isUnitAlreadydeployed(unit)){
            this.units[row][col] = unit;
            notifyAllObservers(new int[]{row, col});
            return true;
        }

        return false;
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

    public boolean  isStandardBearerAtRange(Unit unit, int row, int col){
        if(isTileExisted(row, col)) {
            if(unit.isMobilized()) {
                int bannerRange = unit.getArmy().getBannerRange();
                for (int r = row - bannerRange; r < row + bannerRange + 1; r++) {
                    for (int c = col - bannerRange; c < col + bannerRange + 1; c++) {
                        if (isTileOccupiedBySameSquad(r, c, getUnit(r, c))
                                && Utils.dist(row, col, r, c) > 0
                                && getUnit(r, c).isStandardBearer()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     *
     * @param row
     * @param col
     * @return (r,c)
     */
    public Array<int[]> getAvailableNeighbourTiles(int row, int col, Unit recruit){
        Array<int[]> availableNeighbourTiles = new Array<int[]>();
        if (isTileAvailable(row+1, col, recruit.has(PassiveAbility.PATHFINDER))) availableNeighbourTiles.add(new int[]{row+1, col});
        if (isTileAvailable(row-1, col, recruit.has(PassiveAbility.PATHFINDER))) availableNeighbourTiles.add(new int[]{row-1, col});
        if (isTileAvailable( row,col+1, recruit.has(PassiveAbility.PATHFINDER))) availableNeighbourTiles.add(new int[]{row, col+1});
        if (isTileAvailable( row,col-1, recruit.has(PassiveAbility.PATHFINDER))) availableNeighbourTiles.add(new int[]{row, col-1});
        return availableNeighbourTiles;
    }

    public boolean isUnitNearGardien(int row, int  col){
        if(isTileOccupied(row, col)) {
            Unit unit = getUnit(row, col);
            for (int r = row - 1; r < row + 2; r++) {
                for (int c = col - 1; c < col + 2; c++) {
                    if (isTileOccupied(r, c)
                            && Utils.dist(row, col, r, c) > 0
                            && getUnit(r, c).sameSquadAs(unit)
                            && getUnit(r, c).has(PassiveAbility.GUARDIAN)) {
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
                            && getUnit(r, c).has(PassiveAbility.GUARDIAN)) {
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


    static class CheckMap{
        /**
         *
         * (ROW , COL):
         *
         * 0: EFFICIENCY = move requiredRange + attack requiredRange
         * 1: ACTION = { attack = 2 OU move = 1 }
         * 2: PREVIOUS ROW,
         * 3: PREVIOUS COL;
         *
         */

        float[][][] map;
        int rowCM;
        int colCM;

        public CheckMap(int nbrows, int nbcols, int rowCM, int colCM) {
            super();
            this.map = new float[nbrows][nbcols][4];
            this.rowCM = rowCM;
            this.colCM = colCM;

            for(int r = getNbRows()-1; r> -1; r--){
                for(int c = 0; c<getNbCols(); c++){
                    map[r][c][1] = 10;

                }
            }
        }

        public int getNbRows(){
            return map.length;
        }

        public int getNbCols(){
            return map[0].length;
        }

        /**
         * compare the value of efficient previously attributed to this tile with the new one.
         * @param row
         * @param col
         * @param efficiency; the higher the more efficient. Based on the number of required tiles to pass by to get at the targets tile.
         * @return Whether or the new 'paht'/option is more efficient.
         */
        public boolean checkEfficiency(int row, int col, int efficiency) {

            boolean moreEfficient = (map[row - rowCM][col - colCM][0] < efficiency);
            if (moreEfficient) map[row - rowCM][col - colCM][0] = efficiency;
            return moreEfficient;
        }

        public void resetEfficiencies() {
            for(int r = getNbRows()-1; r> -1; r--){
                for(int c = 0; c<getNbCols(); c++){
                    map[r][c][0] = 0;
                }
            }

        }

        public String toString(){
            String res = "";
            for(int r = getNbRows()-1; r> -1; r--){
                for(int c = 0; c<getNbCols(); c++){
                    if(map[r][c][1] == 10) res +=" .";
                    else if(map[r][c][1] == 1) res +=" _";
                    else res += " "+(int)(map[r][c][1] -1 );

                }
                res +="\n";
            }
            return res;
        }
    }

    /**
     *
     * @param rowActor
     * @param colActor
     * @return
     */
    public Array<int[]> getMoveArea(int rowActor, int colActor){
        Array<int[]> moveArea = new Array<int[]>();
        if(isTileOccupied(rowActor, colActor)){
            Unit actor = getUnit(rowActor, colActor);
            TileType actorTile = getTile(rowActor,colActor);
            int moveRange = actor.hasMoved()? 0 : actor.getCurrentMob(actorTile);

        }
        return moveArea;
    }
    /**
     *
     * @return the area where the action can be performed by the unit at (row, col)
     */
    /*
    public Array<float[]> getActionArea(int row, int col){
        Array<float[]> area = new Array<float[]>();

        if(isTileOccupied(row, col)){
            TileType tileType = getTile(row,col);
            Unit unit = getUnit(row, col);
            int moveRange = unit.hasMoved()? 0 : unit.getCurrentMob(tileType);

            int theoricAttMaxRange = Data.THEORICAL_MAX_RANGE;
            int rowCM = (row - moveRange - theoricAttMaxRange - 1< 0)? 0: row - moveRange - theoricAttMaxRange - 1;
            int colCM = (col - moveRange - theoricAttMaxRange - 1< 0)? 0: col - moveRange - theoricAttMaxRange - 1;
            int rowCMf = (row + moveRange + theoricAttMaxRange + 1> getNbRows()-1)? getNbRows()-1: row + moveRange + theoricAttMaxRange + 1;
            int colCMf = (col + moveRange + theoricAttMaxRange + 1> getNbColumns()-1)? getNbColumns()-1: col + moveRange + theoricAttMaxRange + 1;

            checkmap = new CheckMap(rowCMf - rowCM + 1, colCMf - colCM +1, rowCM, colCM);

            //int currentMaxRange = unit.getCurrentRangeMax(getTile(row, col).type);
            int currentMaxRange = unit.getCurrentRangeMax(tileType, isStandardBearerAtRange(row, col));

            _updateMoveMap(row, col, row, col, row, col+1, moveRange, unit) ;
            _updateMoveMap(row, col, row, col, row, col-1, moveRange, unit) ;
            _updateMoveMap(row, col, row, col, row+1, col, moveRange, unit) ;
            _updateMoveMap(row, col, row, col, row-1, col, moveRange, unit) ;

            checkmap.resetEfficiencies();
            _updateAttackMap(row, col, row, col, row, col+1, unit.getCurrentMob(tileType), currentMaxRange, unit) ;
            _updateAttackMap(row, col, row, col, row, col-1, unit.getCurrentMob(tileType), currentMaxRange, unit) ;
            _updateAttackMap(row, col, row, col, row+1, col, unit.getCurrentMob(tileType), currentMaxRange, unit) ;
            _updateAttackMap(row, col, row, col, row-1, col, unit.getCurrentMob(tileType), currentMaxRange, unit) ;

            for(int r = 0; r < checkmap.getNbRows(); r++){
                for(int c = 0; c < checkmap.getNbCols(); c++){
                    if(checkmap.map[r][c][1] != 10 && !((checkmap.map[r][c][1] == 2) && Utils.dist(row, col, r+rowCM, c + colCM) < unit.getCurrentRangeMin())){
                        area.add(new float[]{r + rowCM, c + colCM,  checkmap.map[r][c][1]-1});
                    }
                }
            }
            checkmap = null;
        }

        return area;
    }

    private void _updateAttackMap(int initRow, int initCol, int previousRow, int previousCol, int row, int col, int moveRange, int attackrange, Unit unit) {
        if(isTileExisted(row,col) && (row != initRow || col != initCol) && checkmap.checkEfficiency(row, col, moveRange + attackrange)){
            if(moveRange > 0 && checkmap.map[row-checkmap.rowCM][col - checkmap.colCM][1] == 1 ){

                int attackRangeMax = unit.getCurrentRangeMax(getTile(row, col), isStandardBearerAtRange(row, col));
                if(previousRow != row+1 || previousCol != col) _updateAttackMap(initRow, initCol, row, col, row + 1, col, moveRange - 1, attackRangeMax, unit);
                if(previousRow != row-1 || previousCol != col) _updateAttackMap(initRow, initCol, row, col, row - 1, col, moveRange - 1, attackRangeMax, unit);
                if(previousRow != row || previousCol != col-1) _updateAttackMap(initRow, initCol, row, col, row, col - 1, moveRange - 1, attackRangeMax, unit);
                if(previousRow != row || previousCol != col+1) _updateAttackMap(initRow, initCol, row, col, row, col + 1, moveRange - 1, attackRangeMax, unit);


            }else if(attackrange > 0 && checkmap.map[row-checkmap.rowCM][col - checkmap.colCM][1] > 1){

                checkmap.map[row-checkmap.rowCM][col - checkmap.colCM][1] = 2;

                if(previousRow != row+1 || previousCol != col) _updateAttackMap(initRow, initCol, row, col, row + 1, col, 0, attackrange - 1, unit);
                if(previousRow != row-1 || previousCol != col) _updateAttackMap(initRow, initCol, row, col, row - 1, col, 0, attackrange - 1, unit);
                if(previousRow != row || previousCol != col-1) _updateAttackMap(initRow, initCol, row, col, row, col - 1, 0, attackrange - 1, unit);
                if(previousRow != row || previousCol != col+1) _updateAttackMap(initRow, initCol, row, col, row, col + 1, 0, attackrange - 1, unit);

            }
        }
    }

    private void _updateMoveMap(int initRow, int initCol, int previousRow, int previousCol, int row, int col, int movePointLeft, Unit unit) {
        if (isTileReachable(row, col, unit.has(PassiveAbility.PATHFINDER))
                && checkmap.checkEfficiency(row, col, movePointLeft)
                && (initRow != row || initCol != col)
                && !isTileOccupiedByFoe(row, col, unit.getAllegeance())) {

            //set previous tile
            checkmap.map[row - checkmap.rowCM][col - checkmap.colCM][2] = previousRow;
            checkmap.map[row - checkmap.rowCM][col - checkmap.colCM][3] = previousCol;

            //set action value
            if(!isTileOccupied(row, col)){
                checkmap.map[row - checkmap.rowCM][col - checkmap.colCM][1] = 1;
                _cancelTrackBackValues(initRow , initCol, previousRow, previousCol);
            }else{
                checkmap.map[row - checkmap.rowCM][col - checkmap.colCM][1] = 2;
            }

            if(movePointLeft > 1){
                if(previousRow != row+1 || previousCol != col) _updateMoveMap(initRow, initCol, row, col, row + 1, col, movePointLeft - 1, unit);
                if(previousRow != row-1 || previousCol != col) _updateMoveMap(initRow, initCol, row, col, row - 1, col, movePointLeft - 1, unit);
                if(previousRow != row || previousCol != col-1) _updateMoveMap(initRow, initCol, row, col, row, col - 1, movePointLeft - 1, unit);
                if(previousRow != row || previousCol != col+1) _updateMoveMap(initRow, initCol, row, col, row, col + 1, movePointLeft - 1, unit);
            }

        }
    }

    private void _cancelTrackBackValues(int initRow, int initCol, int row, int col) {
        if(checkmap.map[row - checkmap.rowCM][col - checkmap.colCM][1] == 2 && (initRow != row || initCol != col)) {
            checkmap.map[row- checkmap.rowCM][col - checkmap.colCM][1] = 1;
            _cancelTrackBackValues(initRow, initCol, (int) checkmap.map[row - checkmap.rowCM][col - checkmap.colCM][2], (int) checkmap.map[row - checkmap.rowCM][col - checkmap.colCM][3]);
        }
    }

    */

    /**
     * there is 3 types of requirements for an action to be performable by an actor
     *  - abiility type
     *  - equipement type (weapon mainly)
     *  - target type
     *
     * @return whether or not an action can be physically performed by the actor if one's is standing the tile (row, col) to perform the given action while ignoring the actor's history.
     */
    public boolean canActionbePerformed(Unit actor, int row, int col, ActionChoice choice){
        boolean performable = false;

        if(isTileAvailable(row, col, actor.has(PassiveAbility.PATHFINDER))){

            // check ABILITY REQUIREMENTS
            switch (choice){
                case WALK:                  break;
                case SWITCH_WEAPON:         if(!actor.isPromoted()) return false; break;
                case SWITCH_POSITION:       break;
                case PUSH:                  if(actor.isHorseman()) return false; break;
                case PRAY:                  if(!actor.has(PassiveAbility.PRAYER)) return false; break;
                case HEAL:                  if(!actor.has(PassiveAbility.HEALER)) return false; break;
                case STEAL:                 if(!actor.has(PassiveAbility.THIEF)) return false; break;
                case BUILD:                 if(!actor.has(PassiveAbility.ENGINEER)) return false; break;
                case ATTACK:                break;
                case CHOOSE_ORIENTATION:    break;
                case CHOOSE_STANCE:         break;
                case USE_FOCUSED_BLOW:      if(!actor.has(OffensiveAbility.FOCUSED_BLOW)) return false; break;
                case USE_CRIPPLING_BLOW:    if(!actor.has(OffensiveAbility.CRIPPLING_BLOW)) return false; break;
                case USE_SWIRLING_BLOW:     if(!actor.has(OffensiveAbility.SWIRLING_BLOW)) return false; break;
                case USE_SWIFT_BLOW:        if(!actor.has(OffensiveAbility.SWIFT_BLOW)) return false; break;
                case USE_HEAVY_BLOW:        if(!actor.has(OffensiveAbility.HEAVY_BLOW)) return false; break;
                case USE_CRUNCHING_BLOW:    if(!actor.has(OffensiveAbility.CRUNCHING_BLOW)) return false; break;
                case USE_WAR_CRY:           if(!actor.has(OffensiveAbility.WAR_CRY)) return false; break;
                case USE_POISONOUS_ATTACK:  if(!actor.has(OffensiveAbility.POISONOUS_ATTACK)) return false; break;
                case USE_GUARD_BREAK:       if(!actor.has(OffensiveAbility.GUARD_BREAK)) return false; break;
                case USE_LINIENT_BLOW:      if(!actor.has(OffensiveAbility.LINIENT_BLOW)) return false; break;
                case USE_FURY:              if(!actor.has(OffensiveAbility.FURY)) return false; break;
                default:

                    return false;
            }

            // check WEAPON REQUIREMENTS
            if((choice.getDamageTypeRequired() != DamageType.NONE  && actor.getCurrentWeapon().getType() != choice.getDamageTypeRequired())
                    || (actor.getCurrentWeapon().isRangedW() &&  choice.isMeleeWeaponEquipedRequired())) {
                return false;
            }

            // check TARGET REQUIREMENTS
            performable = atActionRange(actor, row, col, choice);

        }
        return performable;
    }

    /**
     *
     * @param actor
     * @param row
     * @param col
     * @param choice
     * @return whether or not a target is at range by the actor performing the given action if one's is standing the tile (row, col)
     * while ignoring the actor's history and the unit other requirements to actually perform this action, namely : weapon and ability requirements.
     */
    public boolean atActionRange(Unit actor, int row, int col, ActionChoice choice){
        boolean atRange = false;
        if(isTileAvailable(row, col, actor.has(PassiveAbility.PATHFINDER))){
            if (choice.getTargetType() == TargetType.SPECIFIC) {
                if (choice == ActionChoice.BUILD) {
                    if(!actor.isBuildingResourcesConsumed()) {
                        int[][] neighborTiles = new int[][]{{row + 1, col}, {row - 1, col}, {row, col + 1}, {row, col - 1}};
                        int r;
                        int c;
                        for (int i = 0; i < neighborTiles.length; i++) {
                            r = neighborTiles[i][0];
                            c = neighborTiles[i][1];
                            if (isTileExisted(r, c)) {
                                if (getTile(r, c) == TileType.PLAIN) {

                                    atRange =  true;

                                } else if (getTile(r, c) == TileType.SHALLOWS) {
                                    if (isTileExisted(r + 1, c) && isTileExisted(r - 1, c) && getTile(r + 1, c) == TileType.PLAIN && getTile(r - 1, c) == TileType.PLAIN) {

                                        atRange = true;

                                    } else if (isTileExisted(r, c + 1) && isTileExisted(r, c - 1) && getTile(r + 1, c) == TileType.PLAIN && getTile(r - 1, c) == TileType.PLAIN) {

                                        atRange =  true;
                                    }
                                }
                            }
                        }
                    }
                } else if (choice == ActionChoice.WALK) {
                    //TODO: if relevant check if there is at least one tile available at move range.

                    atRange = true;

                }
            } else {
                if (choice.getTargetType() == TargetType.ONE_SELF || choice.getImpactAreaSize() > 1) {

                    atRange = true;

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

                                    atRange = true;

                                } else if (isTileOccupiedByFoe(r, c, actor.getAllegeance()) && choice.getTargetType() == TargetType.ENEMY) {

                                    atRange = true;

                                }
                            }
                        }
                    }
                }
            }
        }
        return atRange;
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
     * @return an array like that {[row, col]} witch is the path from one tile to another
     */
    public Array<int[]>  getShortestPath(int rowI, int colI, int rowf, int colf, boolean pathfinder, Allegeance allegeance){
        Array<int[]> res = new Array<int[]>();

        Array<Node> path = new Array<Node>();
        Array<Node> opened = new Array<Node>();
        Array<Node> closed = new Array<Node>();
        opened.add(new Node(rowI, colI, rowf, colf, null, this));
        Node current = opened.get(0);
        Array<Node> neighbours;
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
            Node node;
            neighbours = new Array<Node>();
            if (isTileReachable(current.row + 1, current.col, pathfinder) && !isTileOccupiedByFoe(current.row + 1, current.col, allegeance)) {
                node = new Node(current.row + 1, current.col, rowf, colf, current, this);
                if (!closed.contains(node, false)) {
                    neighbours.add(node);
                }
            }
            if (isTileReachable(current.row, current.col + 1, pathfinder) && !isTileOccupiedByFoe(current.row, current.col + 1, allegeance)) {
                node = new Node(current.row, current.col + 1, rowf, colf, current, this);
                if (!closed.contains(node, false)) {
                    neighbours.add(node);
                }
            }
            if (isTileReachable(current.row - 1, current.col, pathfinder) && !isTileOccupiedByFoe(current.row - 1, current.col, allegeance)) {
                node = new Node(current.row - 1, current.col, rowf, colf, current, this);
                if (!closed.contains(node, false)) {
                    neighbours.add(node);
                }
            }
            if (isTileReachable(current.row, current.col - 1, pathfinder ) && !isTileOccupiedByFoe(current.row, current.col - 1, allegeance)) {
                node = new Node(current.row, current.col - 1, rowf, colf, current, this);
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

        return res;
    }


    /**
     *
     * Algorythm A* : https://www.youtube.com/watch?v=-L-WgKMFuhE
     *
     * the initial tile is the one of the target.
     *
     * @return shortest path to attack the given target
     */
    public Array<int[]>  getShortestPath(int rowActor, int colActor, int rowf, int colf, ActionChoice choice){
        Array<int[]> res = new Array<int[]>();

        if(isTileOccupied(rowActor, colActor)) {
            Unit actor = getUnit(rowActor, colActor);
            StateNode node;
            int rowNeigh;
            int colNeigh;
            boolean atAttackRange;


            Array<Node> path = new Array<Node>();
            Array<StateNode> opened = new Array<StateNode>();
            Array<StateNode> closed = new Array<StateNode>();
            opened.add(new StateNode(rowf, colf, rowActor, colActor, null, this, true));
            StateNode current;
            Array<StateNode> neighbours;


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
                if (current.getRow() == rowf && current.getCol() == colf){
                    //TODO: handle the case where the unit is under the minimal requiredRange of one's weapon and therefore can not attack as one's is both too far and too close to the target.




                    break;
                }

                // get available neighbor nodes which are not yet in the closed list
                neighbours = new Array<StateNode>();


                rowNeigh = current.row + 1;
                colNeigh = current.col;
                atAttackRange = atActionRange(actor, rowNeigh, colNeigh, choice);
                node = null;
                if(atAttackRange){
                    /*TODO:
                    1) think about the case where the unit is precisely at the maximum range but the tile is occupied by an ally
                    2) implements Node.arrayContains(Node node) to replace this line : "!closed.contains(node, false)"
                     */
                    node = new StateNode(rowNeigh, colNeigh, rowf, colf, current, this, true);
                }else if (isTileReachable(rowNeigh, colNeigh, actor.has(PassiveAbility.PATHFINDER)) && !isTileOccupiedByFoe(rowNeigh, colNeigh, actor.getAllegeance())) {
                    node = new StateNode(rowNeigh, colNeigh, rowf, colf, current, this, false);
                }
                if (!closed.contains(node, false)) {
                    neighbours.add(node);
                }


                /*
                if (isTileReachable(current.row, current.col + 1, actor.has(PassiveAbility.PATHFINDER)) && !isTileOccupiedByFoe(current.row, current.col + 1, actor.getAllegeance())) {
                    node = new StateNode(current.row, current.col + 1, rowf, colf, current, this, false);
                    if (!closed.contains(node, false)) {
                        neighbours.add(node);
                    }
                }
                if (isTileReachable(current.row - 1, current.col, actor.has(PassiveAbility.PATHFINDER)) && !isTileOccupiedByFoe(current.row - 1, current.col, actor.getAllegeance())) {
                    node = new StateNode(current.row - 1, current.col, rowf, colf, current, this, false);
                    if (!closed.contains(node, false)) {
                        neighbours.add(node);
                    }
                }
                if (isTileReachable(current.row, current.col - 1, actor.has(PassiveAbility.PATHFINDER)) && !isTileOccupiedByFoe(current.row, current.col - 1, actor.getAllegeance())) {
                    node = new StateNode(current.row, current.col - 1, rowf, colf, current, this, false);
                    if (!closed.contains(node, false)) {
                        neighbours.add(node);
                    }
                }*/

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

            for (int i = path.size - 2; i > -1; i--) {
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

    public Unit[][] getUnits() {
        return units;
    }

    public Array<int[]> getDeploymentArea() {
        return deploymentArea;
    }

    public void addDeploymentTile(int row, int col){
        if(isTileReachable(row, col, false)){
            this.deploymentArea.add(new int[]{row, col});
        }
    }



    //----------------- NODE CLASSES -------------------




    public static class Node{
        protected int row;
        protected int col;
        protected float distTarget;
        protected float distSource;
        protected Node parent;
        protected Battlefield bf;

        public Node(int row, int col, int rowf, int colf, Node parent, Battlefield bf) {
            super();
            this.row = row;
            this.col = col;
            this.parent = parent;
            this.bf = bf;
            this.distSource = (parent != null) ? this.parent.distSource + 1 : 0;
            this.distTarget = Utils.dist(row, col, rowf, colf);
        }

        boolean better(Node node){
            return distSource + distTarget < node.distSource + node.distTarget
                    || ((distSource + distTarget == node.distSource + node.distTarget) && distTarget < node.distTarget);
        }

        public Array<Node> getPath(){
            Array<Node> bestpath;
            if(this.parent == null){
                bestpath = new Array<Node>();
                bestpath.add(this);
                return bestpath;
            }
            bestpath = parent.getPath();
            bestpath.add(this);
            return bestpath;

        }

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

        public String toString(){
            return row+" "+col+" "+" cost: "+ (distSource + distTarget);
        }


    }


    public static class StateNode extends Node{
        protected boolean atAttackRange;

        public StateNode(int row, int col, int rowf, int colf, Node parent, Battlefield bf, boolean atAttackRange) {
            super(row, col, rowf, colf, parent, bf);
            this.atAttackRange = atAttackRange;
            if(atAttackRange) this.distSource = 0;
        }

        /**
         * @return all nodes going through each parent until one parent is at attack requiredRange
          */

        @Override
        public Array<Node> getPath(){
            Array<Node> bestpath;
            if(atAttackRange){
                bestpath = new Array<Node>();
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


}
