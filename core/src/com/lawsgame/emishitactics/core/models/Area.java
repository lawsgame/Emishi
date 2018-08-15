package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;


public  class Area extends Observable {
    private int rowInit;
    private int colInit;
    private boolean[][] checkmap;
    private Battlefield battlefield;
    private Data.AreaType type;

    public Area(Battlefield battlefield, Data.AreaType type, int row, int col){
        this.battlefield = battlefield;
        this.type = type;
        set(row, col);
    }

    public Area(Battlefield battlefield, Data.AreaType type, Array<int[]> tiles){
        this.battlefield = battlefield;
        this.type = type;
        set(tiles);
    }

    public Area(Battlefield battlefield, Data.AreaType type, int rCenter, int cCenter, int rangeMin, int rangeMax){
        this(battlefield , type, Utils.getEreaFromRange(battlefield, rCenter, cCenter, rangeMin, rangeMax));
    }

    public void addTile(int row, int col){
        if(checkIndexes(row, col)) {
            checkmap[row - rowInit][col - colInit] = true;
            notifyAllObservers(null);
        }
    }

    public void set( int row, int col){
        if(battlefield.isTileExisted(row, col)) {
            this.checkmap = new boolean[][]{{true}};
            this.rowInit = row;
            this.colInit = col;
            notifyAllObservers(null);
        }
    }

    public void set(int row, int col, Data.AreaType type){
        if(battlefield.isTileExisted(row, col)) {
            this.checkmap = new boolean[][]{{true}};
            this.rowInit = row;
            this.colInit = col;
            this.type = type;
            notifyAllObservers(null);
        }
    }

    public void set(int rCenter, int cCenter, int rangeMin, int rangeMax){
        Array<int[]> tiles = Utils.getEreaFromRange(battlefield, rCenter, cCenter, rangeMin, rangeMax);
        set(tiles);
    }

    public void set(int rCenter, int cCenter, int rangeMin, int rangeMax, Data.AreaType type){
        this.type = type;
        set(rCenter, cCenter, rangeMin, rangeMax);
    }

    public void set(Array<int[]> tiles){

        if(tiles.size > 0) {
            // addExpGained checkmap size and row & col init
            this.rowInit = battlefield.getNbRows();
            this.colInit = battlefield.getNbColumns();
            int rowEnd = 0;
            int colEnd = 0;
            int rowTile;
            int colTile;
            for (int i = 0; i < tiles.size; i++) {
                rowTile = tiles.get(i)[0];
                colTile = tiles.get(i)[1];
                if (rowTile < rowInit) rowInit = rowTile;
                if (colTile < colInit) colInit = colTile;
                if (rowTile > rowEnd) rowEnd = rowTile;
                if (colTile > colEnd) colEnd = colTile;
            }
            this.checkmap = new boolean[rowEnd - rowInit + 1][colEnd - colInit + 1];

            // fill the checkmap
            for (int i = 0; i < tiles.size; i++) {
                checkmap[tiles.get(i)[0] - rowInit][tiles.get(i)[1] - colInit] = true;
            }

        }else{
            rowInit = 0;
            colInit = 0;
            checkmap = new boolean[1][1];
        }
        notifyAllObservers(null);
    }

    public void set(Array<int[]> tiles, Data.AreaType type){
        this.type = type;
        set(tiles);
    }

    public boolean contains(int row, int col){
        if(checkIndexes(row, col)) {
            return checkmap[row - rowInit][col - colInit];
        }
        return false;
    }

    private boolean checkIndexes(int row, int col){
        if(checkmap != null && checkmap.length > 0)
            return rowInit <=  row
                    && row  < rowInit + checkmap.length
                    && colInit <= col
                    && col < colInit + checkmap[0].length;
        return false;
    }

    public int getRowInit() {
        return rowInit;
    }

    public int getColInit() {
        return colInit;
    }

    public boolean[][] getCheckmap() {
        return checkmap;
    }

    public Battlefield getBattlefield() {
        return battlefield;
    }

    public Data.AreaType getType() {
        return type;
    }




    public String toString(){
        String str ="";
        if(checkmap != null) {
            str += "\nrowInit : "+ rowInit +"\ncolInit : "+colInit+"\n\n";
            for (int r = checkmap.length - 1; r >= 0; r--) {
                for (int c = 0; c < checkmap[0].length; c++) {
                    str += (checkmap[r][c]) ? "1" : "0";
                }
                str += "\n";
            }
        }
        return str;
    }

    // --------------- SPECIFIC IMPLEMENTATION OF THE AREA CLASS

    public static class UnitArea extends Area{
        private IUnit actor;

        public UnitArea(Battlefield battlefield, Data.AreaType type, Array<int[]> tiles, IUnit actor) {
            super(battlefield, type, tiles);
            this.actor = actor;
        }

        public UnitArea(Battlefield battlefield, Data.AreaType type, int rCenter, int cCenter, int rangeMin, int rangeMax, IUnit actor) {
            super(battlefield, type, rCenter, cCenter, rangeMin, rangeMax);
            this.actor = actor;
        }

        public IUnit getActor(){
            return actor;
        }


    }



}
