package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Props;
import com.lawsgame.emishitactics.core.constants.Props.*;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;

import java.util.HashMap;


/*
 *  TODO:
 * 2) solve the range issue on the following methods
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
            if(deploymentArea.get(i).length == 2 && deploymentArea.get(i)[0] == row && deploymentArea.get(i)[1] == col){
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
        if(isTileExisted(row, col)){
            return this.units[row][col] != null;
        }
        return false;
    }

    public boolean isTileAvailable(int row, int col, boolean pathfinder){
        return isTileReachable(row, col, pathfinder) && !isTileOccupied(row, col);
    }

    public boolean isTileOccupiedBySameArmyUnit(int row , int col, Unit unit){
        return isTileOccupied(row, col) && this.units[row][col].sameArmyAs(unit);
    }

    public boolean isTileOccupiedByAlly(int row , int col, Unit unit){
        return isTileOccupied(row, col) && this.units[row][col].sameAligmentAs(unit);
    }

    public boolean isTileOccupiedByFoe(int row , int col, Unit unit){
        return isTileOccupied(row, col) && !this.units[row][col].isEnemyWith(unit);
    }




    //----------------- UNIT MANAGEMENT ----------------------


    public void MoveUnit(int rowI, int colI, int rowf, int colf){
        //TODO:
    }

    public boolean addUnit(int row, int col, Unit unit){
        if(units != null && !isTileOccupied(row, col) && isTileReachable(row, col, unit.has(PassiveAbility.PATHFINDER))){
            this.units[row][col] = unit;
            return true;
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

    public boolean  isStandardBearerAtRange(int row, int col){
        if(isTileOccupied(row, col)) {
            Unit unit = getUnit(row, col);
            if(unit.isMobilized()) {
                int bannerRange = unit.getArmy().getBannerRange();
                for (int r = row - bannerRange; r < row + bannerRange + 1; r++) {
                    for (int c = col - bannerRange; c < col + bannerRange + 1; c++) {
                        if (isTileOccupied(r, c)
                                && Utils.dist(row, col, r, c) > 0
                                && unit.sameSquadAs(getUnit(r, c))
                                && getUnit(r, c).getTemplate().getJob().isStandardBearer()) {
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

    public boolean isNextToGardien(int row, int  col){
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

    static class CheckMap{
        /**
         *
         * (ROW , COL):
         *
         * 0: EFFICIENCY = move range + attack range
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


    public boolean atActionRange(int row, int col){
        return false;
    }

    /**
     *
     * @param row
     * @param col
     * @return the area where the action can be performed by the unit at (row, col)
     */
    public Array<float[]> getActionArea(int row, int col){
        Array<float[]> area = new Array<float[]>();

        if(isTileOccupied(row, col)){
            TileType tileType = getTile(row,col);
            Unit unit = getUnit(row, col);
            int moveRange = unit.hasMoved()? 0 : unit.getCurrentMob(tileType);

            int theoricAttMaxRange = Utils.getTheoricRangeMax();
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
                && !isTileOccupiedByFoe(row, col, unit)) {

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



    /**
     * @return an array like that {[row, col]} witch is the path from one tile to another
     */
    public Array<float[]> getShortestPath(int rowI, int colI, int rowf, int colf){
        Array<float[]> res = new Array<float[]>();

        if(isTileOccupied(rowI, colI)) {
            Unit unit = getUnit(rowI, colI);

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
                if (isTileReachable(current.row + 1, current.col, unit.has(PassiveAbility.PATHFINDER)) && !isTileOccupiedByFoe(current.row + 1, current.col, unit)) {
                    node = new Node(current.row + 1, current.col, rowf, colf, current, this);
                    if (!closed.contains(node, false)) {
                        neighbours.add(node);
                    }
                }
                if (isTileReachable(current.row, current.col + 1, unit.has(PassiveAbility.PATHFINDER)) && !isTileOccupiedByFoe(current.row, current.col + 1, unit)) {
                    node = new Node(current.row, current.col + 1, rowf, colf, current, this);
                    if (!closed.contains(node, false)) {
                        neighbours.add(node);
                    }
                }
                if (isTileReachable(current.row - 1, current.col, unit.has(PassiveAbility.PATHFINDER)) && !isTileOccupiedByFoe(current.row - 1, current.col, unit)) {
                    node = new Node(current.row - 1, current.col, rowf, colf, current, this);
                    if (!closed.contains(node, false)) {
                        neighbours.add(node);
                    }
                }
                if (isTileReachable(current.row, current.col - 1, unit.has(PassiveAbility.PATHFINDER)) && !isTileOccupiedByFoe(current.row, current.col - 1, unit)) {
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

            for (int i = path.size - 2; i > -1; i--) {
                res.add(new float[]{path.get(i).getCol(), path.get(i).getRow()});
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
     * @return shortest path to attack the given target

    public Array<float[]> getShortestPathToAttack(int rowTarget, int colTarget, int rowUnit, int colUnit, boolean aligment, boolean pathfinder){

        Array<StateNode> path = new Array<StateNode>();
        int attackrange;

        Array<StateNode> opened = new Array<Battlefield.StateNode>();
        Array<StateNode> closed = new Array<Battlefield.StateNode>();
        opened.add(new StateNode(rowTarget, colTarget, rowUnit, colUnit, null,this, true));
        StateNode current;
        Array<StateNode> neighbours;
        while(true){

            //no solution
            if(opened.size == 0) break;

            //the current node is the last node added to the closed array
            current = opened.get(0);
            for(int i =0; i <opened.size; i++){
                if(opened.get(i).better(current)){
                    current = opened.get(i);
                }
            }
            opened.removeValue(current, true);
            closed.add(current);

            attackrange = attackRangeMax - closed.get(closed.size - 1).getPathTo().size;

            // path found
            if(current.getRow() == rowUnit && current.getCol() == colUnit) break;

            // get available neighbor nodes which are not yet in the closed list
            neighbours = new Array<StateNode>();
            _collectNeighbours(Props.ORIENTATION_EAST, current, aligment, rowUnit, colUnit, closed, neighbours, pathfinder, attackrange, attackRangeMax);
            _collectNeighbours(Props.ORIENTATION_WEST, current, aligment, rowUnit, colUnit, closed, neighbours, pathfinder, attackrange, attackRangeMax);
            _collectNeighbours(Props.ORIENTATION_NORTH, current, aligment, rowUnit, colUnit, closed, neighbours, pathfinder, attackrange, attackRangeMax);
            _collectNeighbours(Props.ORIENTATION_SOUTH, current, aligment, rowUnit, colUnit, closed, neighbours, pathfinder, attackrange, attackRangeMax);

            StateNode node;
            boolean isNotInOpened = true;
            for(int i = 0; i < neighbours.size ; i++){
                node = neighbours.get(i);
                for(int j = 0; j <opened.size; j++){
                    if(opened.get(j).equals(node)){
                        isNotInOpened = false;
                        if(node.better(opened.get(j))){
                            opened.removeIndex(j);
                            opened.add(node);
                        }
                    }
                }
                if(isNotInOpened){
                    opened.add(node);
                }
                isNotInOpened = true;
            }
        }

        if(closed.get(closed.size - 1).getRow() == rowUnit && closed.get(closed.size - 1).getCol() == colUnit){
            path = closed.get(closed.size - 1).getPathTo();
        }

        Array<float[]> res = new Array<float[]>();
        for(int i= 1;  i < path.size ; i++){
            if(!path.get(i).atAttackRange) {
                res.add(new float[]{path.get(i).getCol(), path.get(i).getRow()});
            }
        }
        return res;
    }

    private void _collectNeighbours(int or, StateNode current, boolean aligment, int rowInit, int colInit, Array<StateNode> closed, Array<StateNode> neighbours, boolean pathfinder, int range, int rangeMax){
        int col = current.col;
        int row = current.row;
        switch (or){
            case Props.ORIENTATION_NORTH:row += 1;break;
            case Props.ORIENTATION_SOUTH:row -= 1;break;
            case Props.ORIENTATION_EAST: col += 1;break;
            case Props.ORIENTATION_WEST: col -= 1;break;
        }

        StateNode node = null;
        if(checkIndexes(row, col) ) {
            if (range > 0) {
                node = new StateNode(row, col, rowInit, colInit, current, this, true);
            } else if (range == 0) {
                if ( isTileReachable(row, col, pathfinder)	&& !isTileOccupied(row, col)) {
                    node = new StateNode(row, col, rowInit, colInit, current, this, false);
                } else if( rangeMax > 1 && _nearDune(current.row, current.col, row, col)){
                    node = new StateNode(row, col, rowInit, colInit, current, this, true);
                }
            } else if (range == -1) {
                if( (isTileReachable(row, col, pathfinder)	&& !isTileOccupiedByFoe(row, col, aligment)) && ((isTileReachable(current.row, current.col, pathfinder)	&& !isTileOccupied(current.row, current.col)) || getTile(row, col).type == DUNE)){
                    node = new StateNode(row, col, rowInit, colInit, current, this, false);
                }
            } else{
                if ( isTileReachable(row, col, pathfinder)	&& !isTileOccupiedByFoe(row, col, aligment)){
                    node = new StateNode(row, col, rowInit, colInit, current, this, false);
                }
            }
        }

        if(node != null && !closed.contains(node,false)) neighbours.add(node);
    }

    private boolean _nearDune(int currentRow, int currentCol, int row, int col) {
        boolean res = false;
        int r = row + 1;
        int c = col;
        if(r != currentRow || c != currentCol)
            res = res || getTile(r,c).type == DUNE;
        r = row - 1;
        c = col;
        if(r != currentRow || c != currentCol)
            res = res || getTile(r,c).type == DUNE;
        r = row;
        c = col + 1;
        if(r != currentRow || c != currentCol)
            res = res || getTile(r,c).type == DUNE;
        r = row;
        c = col - 1;
        if(r != currentRow || c != currentCol)
            res = res || getTile(r,c).type == DUNE;
        return res;
    }
    */

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

    public Array<int[]> getEreaFromRange( int rangeMin, int rangeMax){
        Array<int[]> area = new Array<int[]>();
        for(int r = -rangeMax ; r <= rangeMax; r++){
            for(int c = -rangeMax ; c <= rangeMax; c++){
                if(isTileExisted(r,c)) {
                    int dist = Utils.dist(0, 0, r, c);
                    if (dist <= rangeMax && rangeMin <= dist) {
                        area.add(new int[]{r, c});
                    }
                }

            }
        }
        return area;
    }



    //----------------- NODE CLASSES -------------------



    static class Node{
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

        public boolean better(Node node){
            return distSource + distTarget < node.distSource + node.distTarget
                    || ((distSource + distTarget == node.distSource + node.distTarget) && distTarget < node.distTarget);
        }

        public Array<Node> getPath(){
            Array<Node> path = new Array<Node>();
            path.add(this);
            if(this.parent != null){
                path.addAll(parent.getPath());
            }
            return path;
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

    /*
    static class StateNode extends Node{
        protected boolean atAttackRange;

        public StateNode(int row, int col, int rowf, int colf, Node parent, Battlefield bf, boolean atAttackRange) {
            super(row, col, rowf, colf, parent, bf);
            this.atAttackRange = atAttackRange;
            this.distSource = ((parent != null)) ? this.parent.distSource + (!atAttackRange ? 1f : 0.1f) : 0f;
        }

        // @return all nodes going through each parent

        public Array<StateNode> getPathTo(){
            Array<StateNode> path = new Array<StateNode>();
            path.add(this);
            if(this.parent != null){
                path.addAll(((StateNode)parent).getPathTo());
            }
            return path;
        }

        @Override
        public String toString(){
            return super.toString() + " attackrange: "+atAttackRange;
        }

    }
    */
}
