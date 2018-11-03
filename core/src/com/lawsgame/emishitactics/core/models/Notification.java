package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;

public class Notification {

    public static class StepOn {
        public final IUnit walker;
        public int rowTile;
        public int colTile;

        public StepOn(IUnit walker){
            this.walker = walker;
        }

        public StepOn(int rowTile, int colTile, IUnit walker){
            this.walker = walker;
            this.rowTile = rowTile;
            this.colTile = colTile;
        }
    }

    public static class BeginArmyTurn{
        public final IArmy army;

        public BeginArmyTurn(IArmy army) {
            this.army = army;
        }
    }

    public static class Attack{
        public final boolean specialmove;
        public int lifeDrained;

        public Attack(boolean specialmove) {
            this.specialmove = specialmove;
            this.lifeDrained = 0;
        }
    }

    public static class ApplyDamage {
        public final IUnit wounded;
        public final boolean moralOnly;
        public final int damageDealt;
        public final int lifeDamageTaken;
        public boolean crippled;
        public boolean disabled;
        public int healFromDamage;
        public final State state;

        public boolean critical;
        public boolean backstab;
        public Data.Orientation fleeingOrientation;

        public enum State{
            WOUNDED,
            FLED,
            UNDAMAGED, DIED
        }

        public ApplyDamage(IUnit wounded, boolean moralOnly, int damageDealt, int lifeDamageTaken, State state){
            this.wounded = wounded;
            this.moralOnly = moralOnly;
            this.damageDealt = damageDealt;
            this.lifeDamageTaken = lifeDamageTaken;
            this.state = state;
            this.disabled = false;
            this.crippled =false;
            this.healFromDamage = 0;
        }

        public boolean isRelevant(){
            return damageDealt > 0;
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
        public Tile tile;

        public SetTile(int row, int col, Tile tile){
            this.row = row;
            this.col = col;
            this.tile = tile;
        }
    }

    public static class Build extends  SetTile{
        public IUnit builder;

        public Build(int row, int col, Tile tile, IUnit builder){
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

    public static class Crippled {
        public final boolean crippled;

        private static final Crippled FALSE = new Crippled(false);
        private static final Crippled TRUE = new Crippled(true);

        private Crippled(boolean crippled) {
            this.crippled = crippled;
        }

        public static Crippled get(boolean crippled){
            return (crippled) ? TRUE : FALSE;
        }


    }

    public static class Disabled {
        public final boolean disabled;

        private static final Disabled FALSE = new Disabled(false);
        private static final Disabled TRUE = new Disabled(true);

        private Disabled(boolean disabled) {
            this.disabled = disabled;
        }

        public static Disabled get(boolean visible){
            return (visible) ? TRUE : FALSE;
        }


    }



}
