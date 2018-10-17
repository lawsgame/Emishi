package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;

public class Notification {


    public static class ApplyDamage {
        public Unit wounded;
        public boolean moralOnly;
        public int damageTaken;
        public State state;

        public boolean critical;
        public boolean backstab;
        public Data.Orientation fleeingOrientation;

        public enum State{
            WOUNDED,
            FLED,
            DIED
        }

        public ApplyDamage(Unit wounded, boolean moralOnly, int damageTaken){
            this.wounded = wounded;
            this.moralOnly = moralOnly;
            this.damageTaken = damageTaken;
        }

        public boolean isRelevant(){
            return damageTaken > 0;
        }
    }


    public static class Pushed {
        public final Data.Orientation orientation;

        private static final Pushed NORTH = new Pushed(Data.Orientation.NORTH);
        private static final Pushed SOUTH = new Pushed(Data.Orientation.SOUTH);
        private static final Pushed EAST = new Pushed(Data.Orientation.EAST);
        private static final Pushed WEST = new Pushed(Data.Orientation.WEST);


        private Pushed(Data.Orientation orientation) {
            this.orientation = orientation;
        }

        public static Pushed get(Data.Orientation or){
            Pushed notif = null;
            switch(or){

                case WEST: notif = WEST; break;
                case NORTH: notif = NORTH; break;
                case SOUTH: notif = SOUTH; break;
                case EAST: notif = EAST; break;
            }
            return notif;
        }
    }


    public static class Fled {
        public final Data.Orientation orientation;

        private static final Fled NORTH = new Fled(Data.Orientation.NORTH);
        private static final Fled SOUTH = new Fled(Data.Orientation.SOUTH);
        private static final Fled EAST = new Fled(Data.Orientation.EAST);
        private static final Fled WEST = new Fled(Data.Orientation.WEST);

        private Fled(Data.Orientation orientation) {
            this.orientation = orientation;
        }

        public static Fled get(Data.Orientation or){
            Fled notif = null;
            switch(or){

                case WEST: notif = WEST; break;
                case NORTH: notif = NORTH; break;
                case SOUTH: notif = SOUTH; break;
                case EAST: notif = EAST; break;
            }
            return notif;
        }
    }

    public static class SetTile {
        public int row;
        public int col;
        public Data.TileType tileType;

        public SetTile(int row, int col, Data.TileType tileType){
            this.row = row;
            this.col = col;
            this.tileType = tileType;
        }
    }

    public static class Build extends  SetTile{
        public IUnit builder;

        public Build(int row, int col, Data.TileType tile, IUnit builder){
            super(row, col, tile);
            this.builder = builder;
        }
    }

    public static class Walk {
        public IUnit unit;
        public Array<int[]> path;

        public Walk(IUnit unit, Array<int[]> path){
            this.unit = unit;
            this.path = path;
        }
    }

    public static class SetUnit {
        public int row;
        public int col;
        public IUnit unitModel;

        public SetUnit(int row, int col, IUnit unitModel){
            this.row = row;
            this.col = col;
            this.unitModel = unitModel;
        }
    }


    public static class Horseman {
        public boolean horseman;

        private static final Horseman FALSE = new Horseman(false);
        private static final Horseman TRUE = new Horseman(true);

        private Horseman(boolean horseman) {
            this.horseman = horseman;
        }

        public static Horseman get(boolean horsman){
            return (horsman) ? TRUE : FALSE;
        }
    }


    public static class Blink  {
        public final boolean targeted;

        private static final Blink FALSE = new Blink(false);
        private static final Blink TRUE = new Blink(true);

        private Blink(boolean targeted) {
            this.targeted = targeted;
        }

        public static Blink get(boolean targeted){
            return (targeted) ? TRUE : FALSE;
        }
    }

    public static class Done {
        public final boolean done;

        private static final Done FALSE = new Done(false);
        private static final Done TRUE = new Done(true);

        private Done(boolean done) {
            this.done = done;
        }

        public static Done get(boolean done){
            return (done) ? TRUE : FALSE;
        }


    }

    public static class Visible {
        public final boolean visible;

        private static final Visible FALSE = new Visible(false);
        private static final Visible TRUE = new Visible(true);

        private Visible(boolean visible) {
            this.visible = visible;
        }

        public static Visible get(boolean visible){
            return (visible) ? TRUE : FALSE;
        }


    }



}
