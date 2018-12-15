package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.interfaces.MilitaryForce;

public class Notification {

    public static class igniteSparkles {
        public final Array<int[]> tiles;
        public final Array<Data.SparkleType> sparkleType;

        public igniteSparkles() {
            this.tiles = new Array<int[]>();
            this.sparkleType = new Array<Data.SparkleType>();
        }
    }
    
    public static class ExtinguishSparkle {
        public final int rowTile;
        public final int colTile;

        public ExtinguishSparkle(int rowTile, int colTile) {
            this.rowTile = rowTile;
            this.colTile = colTile;
        }
    }

    public static class StateChanged {
        private  static StateChanged changed = null;

        public static StateChanged getInstance(){
            if(changed == null)
                changed = new StateChanged();
            return changed;
        }
    }

    public static class StepOn {
        public final Unit walker;
        public final int rowTile;
        public final int colTile;
        public final int fromRow;
        public final int fromCol;

        public StepOn(int fromRow, int fromCol, int rowTile, int colTile, Unit walker){
            this.walker = walker;
            this.fromRow = fromRow;
            this.fromCol = fromCol;
            this.rowTile = rowTile;
            this.colTile = colTile;
        }

        @Override
        public String toString() {
            return "StepOn{" +
                    "walker=" + walker +
                    ", rowTile=" + rowTile +
                    ", colTile=" + colTile +
                    ", fromRow=" + fromRow +
                    ", fromCol=" + fromCol +
                    '}';
        }
    }

    public static class BeginArmyTurn{
        public final MilitaryForce army;

        public BeginArmyTurn(MilitaryForce army) {
            this.army = army;
        }
    }

    public static class OOAReport {
        public final Array<Unit> OOAUnits;
        public final Array<int[]> OOACoords;

        public OOAReport(){
            this.OOAUnits = new Array<Unit>();
            this.OOACoords = new Array<int[]>();
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

    public static class TakeDamage {
        public final Unit wounded;
        public final boolean ignorePhysicalDmg;
        public final boolean ignoreMoralDmg;
        public final int damageDealt;
        public final int lifeDamageTaken;
        public final State state;

        public boolean crippled;
        public boolean disabled;
        public int healFromDamage;
        public boolean critical;
        public boolean backstab;
        public Data.Orientation fleeingOrientation;

        public enum State{
            WOUNDED,
            FLED,
            UNDAMAGED, DIED
        }

        public TakeDamage(Unit wounded, boolean ignorePhysicalDmg, boolean ignoreMoralDmg, int damageDealt, int lifeDamageTaken, State state){
            this.wounded = wounded;
            this.ignorePhysicalDmg = ignorePhysicalDmg;
            this.ignoreMoralDmg = ignoreMoralDmg;
            this.damageDealt = damageDealt;
            this.lifeDamageTaken = lifeDamageTaken;
            this.state = state;
            this.disabled = false;
            this.crippled =false;
            this.healFromDamage = 0;
        }

        public void set(boolean crippled, boolean disabled, int healFromDamage, boolean critical, boolean backstab, Data.Orientation fleeingOrientation){
            this.crippled = crippled;
            this.disabled = disabled;
            this.healFromDamage = healFromDamage;
            this.critical = critical;
            this.backstab = backstab;
            this.fleeingOrientation  = fleeingOrientation;
        }

        public boolean isRelevant(){
            return damageDealt > 0;
        }
    }


    public static class Treated{
        public final int healPower;

        public Treated(int healPower) {
            this.healPower = healPower;
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
        public final int row;
        public final int col;
        public final Tile newTile;
        public final Tile oldTile;
        public final TransformationType type;

        public enum TransformationType{
            PLUNDERED,
            COLLAPSED,
            BUILT,
            INSTANTANEOUSLY
        }

        public SetTile(int row, int col, Tile newTile, Tile oldTile, TransformationType type){
            this.row = row;
            this.col = col;
            this.newTile = newTile;
            this.oldTile = oldTile;
            this.type = type;
        }

        public SetTile(int row, int col, Tile newTile){
            this(row, col, newTile, null, TransformationType.INSTANTANEOUSLY);
        }
    }

    public static class Walk {
        public final Unit unit;
        public Array<int[]> path;
        public final boolean reveal;

        public Walk(Unit unit, Array<int[]> path, boolean reveal){
            this.unit = unit;
            this.path = path;
            this.reveal = reveal;
        }
    }

    public static class SetUnit {
        public int row;
        public int col;
        public Unit unitModel;

        public SetUnit(int row, int col, Unit unitModel){
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
