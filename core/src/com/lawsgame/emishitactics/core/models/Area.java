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
    private String tag;

    public Area(Battlefield battlefield, Data.AreaType type){
        this.battlefield = battlefield;
        this.type = type;
        this.rowInit = -1;
        this.colInit = -1;
        this.checkmap = new boolean[][]{{false}};
        this.tag = "";
    }

    public Area(Battlefield battlefield, Data.AreaType type, int row, int col){
        this.battlefield = battlefield;
        setType(type, false);
        setTiles(row, col, false);
        this.tag = "";
    }

    public Area(Battlefield battlefield, Data.AreaType type, Array<int[]> tiles){
        this.battlefield = battlefield;
        setType(type, false);
        setTiles(tiles, false);
        this.tag = "";
    }

    public Area(Battlefield battlefield, Data.AreaType type, int rCenter, int cCenter, int rangeMin, int rangeMax){
        this(battlefield , type, Utils.getEreaFromRange(battlefield, rCenter, cCenter, rangeMin, rangeMax));
    }




    public void addTile(int row, int col, boolean notifyObservers){
        if(checkIndexes(row, col)) {
            checkmap[row - rowInit][col - colInit] = true;
            if(notifyObservers)
                notifyAllObservers(null);
        }else if (battlefield.isTileExisted(row, col)){
            Array<int[]> tiles = getTiles();
            tiles.add(new int[]{row, col});
            setTiles(tiles, notifyObservers);
        }
    }


    public void setTiles(int row, int col, boolean notifyObservers){
        if(battlefield.isTileExisted(row, col)) {
            this.checkmap = new boolean[][]{{true}};
            this.rowInit = row;
            this.colInit = col;
            if(notifyObservers)
                notifyAllObservers(null);
        }
    }

    public void setTiles(int rCenter, int cCenter, int rangeMin, int rangeMax, boolean notifyObservers){
        setTiles(Utils.getEreaFromRange(battlefield, rCenter, cCenter, rangeMin, rangeMax), notifyObservers);
    }

    public void setTiles(Array<int[]> tiles, boolean notifyObservers){

        if(tiles.size > 0) {
            // add checkmap size and row & col init
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
        if(notifyObservers)
            notifyAllObservers(null);
    }

    public void setType(Data.AreaType type, boolean notifyObservers){
        this.type = type;
        if(notifyObservers)
            notifyAllObservers(null);
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


    public Array<int[]> getTiles(){
        Array<int[]> tiles = new Array<int[]>();
        if(checkmap != null) {
            for (int r = checkmap.length - 1; r >= 0; r--) {
                for (int c = 0; c < checkmap[0].length; c++) {
                    if (checkmap[r][c]) {
                        tiles.add(new int[]{r+rowInit, c+colInit});
                    }
                }
            }
        }
        return tiles;
    }

    public void setTag(String tag){
        this.tag = tag;
    }

    public String getTag() {
        return tag;
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
