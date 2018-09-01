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

    public static class SwitchPosition{

        public enum Mode{
            WALK,
            GUARDIAN,
            INSTANT
        }

        public int rowUnit1;
        public int colUnit1;
        public int rowUnit2;
        public int colUnit2;
        public IUnit unit1;
        public IUnit unit2;
        public Mode mode;

        public SwitchPosition(IUnit unit1, IUnit unit2, Mode mode, Battlefield battlefield) {
            int[] unit1Pos = battlefield.getUnitPos(unit1);
            int[] unit2Pos = battlefield.getUnitPos(unit2);
            this.rowUnit1 = unit2Pos[0];
            this.colUnit1 = unit2Pos[1];
            this.rowUnit2 = unit1Pos[0];
            this.colUnit2 = unit1Pos[1];
            this.unit1 = unit1;
            this.unit2 = unit2;
            this.mode = mode;
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


    public static class HorsemanUpdate{
        public boolean horseman;

        private static final HorsemanUpdate FALSE = new HorsemanUpdate(false);
        private static final HorsemanUpdate TRUE = new HorsemanUpdate(true);

        private HorsemanUpdate(boolean horseman) {
            this.horseman = horseman;
        }

        public static HorsemanUpdate get(boolean horsman){
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



}
