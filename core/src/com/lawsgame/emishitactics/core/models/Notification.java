package com.lawsgame.emishitactics.core.models;

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
        public Data.Orientation orientation;

        public Pushed(Data.Orientation orientation) {
            this.orientation = orientation;
        }
    }


    public static class Fled {
        public Data.Orientation orientation;

        public Fled(Data.Orientation orientation) {
            this.orientation = orientation;
        }
    }

    public static class SwitchPosition{

        public enum Mode{
            WALK,
            GUARDIAN
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
}
