package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;

public abstract class ActionChoice {

    public static ActionChoice ATTACK =             new ActionChoice(0, 0, RangedBasedType.WEAPON, new int[0][0]) {
        @Override
        public boolean canbePerformedBy(IUnit actor) {
            return !actor.hasActed();
        }
    };
    public static ActionChoice MOVE =               new ActionChoice(0, 0, RangedBasedType.MOVE, new int[0][0]) {
        @Override
        public boolean canbePerformedBy(IUnit actor) {
            return !actor.hasMoved();
        }
    };
    public static ActionChoice SWITCH_WEAPON =      new ActionChoice(0, 0, 0, 0, new int[0][0]) {
        @Override
        public boolean canbePerformedBy(IUnit actor) {
            return actor.getWeapons().size > 1;
        }
    };
    public static ActionChoice SWITCH_POSITION =    new ActionChoice(0, 0, 1, 1, new int[0][0]) {
        @Override
        public boolean canbePerformedBy(IUnit actor) {
            return true;
        }
    };
    public static ActionChoice PUSH =               new ActionChoice(0, 0, 1, 1, new int[0][0]) {
        @Override
        public boolean canbePerformedBy(IUnit actor) {
            return !actor.hasActed() && !actor.isHorseman();
        }
    };
    public static ActionChoice CHOOSE_ORIENTATION = new ActionChoice(0, 0, 0, 0, RangedBasedType.SPECIFIC, true, new int[0][0]) {
        @Override
        public boolean canbePerformedBy(IUnit actor) {
            return true;
        }
    };
    public static ActionChoice HEAL =               new AbilityBasedActionChoice(0, 10, 1, 1, new int[0][0], Data.Ability.HEAL);
    public static ActionChoice GUARD =              new AbilityBasedActionChoice(0, 0, 0, 0, new int[0][0], Data.Ability.GUARD);
    public static ActionChoice STEAL =              new AbilityBasedActionChoice(0, 10, 1, 1, new int[0][0], Data.Ability.STEAL);
    public static ActionChoice BUILD =              new AbilityBasedActionChoice(0, 10, 1, 1, new int[0][0], Data.Ability.BUILD);


    public enum RangedBasedType{
        MOVE,
        WEAPON,
        SPECIFIC
    }

    protected int cost;
    protected int experience;
    protected int rangeMin;
    protected int rangeMax;
    protected RangedBasedType rangedBasedType;
    protected boolean endTurnActionOnly;
    /**
     * AreaWidget of impact :
     * ( 0, 0) = targeted tileType
     * ( 1, 0) = in front of targeted tileType
     * ( 0, 1) = right of targeted tileType
     * ( 0,-1) = left of targeted tileType
     * (-1, 0) = in the back of targeted tileType
     */
    protected Array<int[]> impactArea;



    ActionChoice(int cost, int experience, int rangeMin, int rangeMax, RangedBasedType type, boolean endTurnActionOnly, int[][] impactArea) {
        this.rangedBasedType = type;
        this.rangeMax = rangeMax;
        this.rangeMin = rangeMin;
        this.cost = cost;
        this.experience = experience;
        this.endTurnActionOnly = endTurnActionOnly;
        this.impactArea = new Array<int[]>();
        for(int[] relativeTileCoordinates : impactArea){
            this.impactArea.add(relativeTileCoordinates);
        }
    }

    ActionChoice(int cost, int experience, int rangeMin, int rangeMax, int[][] impactArea){
        this(cost, experience, rangeMax, rangeMin, RangedBasedType.SPECIFIC, false, impactArea);
    }

    ActionChoice(int cost, int experience, RangedBasedType type, int[][] impactArea) {
        this(cost, experience, -1, -1 , type,false, impactArea);
    }

    /**
     * there is 3 types of requirements for an action to be performable by an actor
     *  - Internal
     *      + history type: if unit has already moved or acted
     *      + abiility type
     *      + equipement type
     *  - External :
     *      + range & target  type (checked by the method: BattleCommand.atActionRange() and Battlefield.isTargetValid())
     *
     * @return whether or not an action can be performed by the actor regardless the actor's history or target availability.
     */
    public abstract boolean canbePerformedBy(IUnit actor);


    public RangedBasedType getRangeType() { return rangedBasedType; }

    public int getCost(){
        return cost;
    }

    public int getExperience() {
        return experience;
    }

    public int getRangeMax() {
        return rangeMax;
    }

    public int getRangeMin() {
        return rangeMin;
    }

    public boolean isEndTurnActionOnly() {        return endTurnActionOnly; }

    public Array<int[]> getOrientedImpactArea(Data.Orientation orientation) {
        Array<int[]> orientedArea = new Array<int[]>();
        switch (orientation){
            case WEST:
                for(int i = 0; i < impactArea.size; i++){
                    orientedArea.add(new int[]{impactArea.get(i)[1], -impactArea.get(i)[0]});
                }
                break;
            case NORTH:
                for(int i = 0; i < impactArea.size; i++) {
                    orientedArea.addAll(new int[]{impactArea.get(i)[0], impactArea.get(i)[1]});
                }
                break;
            case SOUTH:
                for(int i = 0; i < impactArea.size; i++){
                    orientedArea.add(new int[]{-impactArea.get(i)[0], -impactArea.get(i)[1]});
                }
                break;
            case EAST:
                for(int i = 0; i < impactArea.size; i++){
                    orientedArea.add(new int[]{-impactArea.get(i)[1], impactArea.get(i)[0]});
                }
                break;
        }


        return orientedArea;
    }

    public Array<int[]> getOrientedImpactArea(Data.Orientation orientation, int rowActor, int colActor, Battlefield battlefield) {
        Array<int[]> orientedArea = getOrientedImpactArea(orientation);
        int r;
        int c;
        for(int i = 0; i < orientedArea.size; i++){
            r = orientedArea.get(i)[0] + rowActor;
            c  = orientedArea.get(i)[0] + colActor;
            if(battlefield.isTileExisted(r, c)){
                orientedArea.get(i)[0] = r;
                orientedArea.get(i)[1] = c;
            }
        }
        return orientedArea;
    }


    public int getImpactAreaSize(){
        return impactArea.size + 1;
    }



    //-------------- SUB CLASS of ACTION CHOICE -----------------------------

    static class AbilityBasedActionChoice extends  ActionChoice{
        protected Data.Ability ability;

        AbilityBasedActionChoice(int cost, int experience, int rangeMin, int rangeMax, int[][] impactArea, Data.Ability ability) {
            super(cost, experience, rangeMin, rangeMax, impactArea);
            this.ability = ability;
        }

        AbilityBasedActionChoice(int cost, int experience, RangedBasedType type, int[][] impactArea, Data.Ability ability) {
            super(cost, experience, type, impactArea);
            this.ability = ability;
        }

        @Override
        public boolean canbePerformedBy(IUnit actor) {
            return !actor.hasActed() && actor.has(ability);
        }
    }


}
