package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;

public abstract class ActionChoice {

    public static ActionChoice ATTACK =             new ActionChoice("ATTACK",          1, 0, RangedBasedType.WEAPON, new int[0][0]) {
        @Override
        public boolean canbePerformedBy(IUnit actor) {
            return cost <= actor.getActionPoints() && !actor.hasActed();
        }
    };
    public static ActionChoice MOVE =               new ActionChoice("MOVE",            1, 0, RangedBasedType.MOVE, new int[0][0]) {
        @Override
        public boolean canbePerformedBy(IUnit actor) {
            return cost <= actor.getActionPoints() && !actor.hasMoved();
        }
    };
    public static ActionChoice SWITCH_WEAPON =      new ActionChoice("SWITCH_WEAPON",   1, 0, 0, 0, new int[0][0]) {
        @Override
        public boolean canbePerformedBy(IUnit actor) {
            return cost <= actor.getActionPoints() && actor.getWeapons().size > 1;
        }
    };
    public static ActionChoice SWITCH_POSITION =    new ActionChoice("SWITCH_POSITION", 1, 0, 1, 1, new int[0][0]) {
        @Override
        public boolean canbePerformedBy(IUnit actor) {
            return cost <= actor.getActionPoints();
        }
    };
    public static ActionChoice PUSH =               new ActionChoice("PUSH",            1, 0, 1, 1, new int[0][0]) {
        @Override
        public boolean canbePerformedBy(IUnit actor) {
            return cost <= actor.getActionPoints() && !actor.hasActed() && !actor.isHorseman();
        }
    };
    public static ActionChoice CHOOSE_ORIENTATION = new ActionChoice("CHOOSE_ORIENTATION",0, 0, 0, 0, RangedBasedType.SPECIFIC,  true, new int[0][0]) {
        @Override
        public boolean canbePerformedBy(IUnit actor) {
            return cost <= actor.getActionPoints();
        }
    };
    public static ActionChoice END_TURN  =          new ActionChoice("END_TURN",         0, 0, 0, 0, RangedBasedType.SPECIFIC,  false, new int[0][0]) {
        @Override
        public boolean canbePerformedBy(IUnit actor) {
            return cost <= actor.getActionPoints();
        }
    };
    public static ActionChoice HEAL =               new AbilityBasedAC("HEAL",          1, 10, 1, 1, new int[0][0], Data.Ability.HEAL);
    public static ActionChoice GUARD =              new AbilityBasedAC("GUARD",         1, 0, 0, 0, new int[0][0], Data.Ability.GUARD);
    public static ActionChoice STEAL =              new AbilityBasedAC("STEAL",         1, 10, 1, 1, new int[0][0], Data.Ability.STEAL);
    public static ActionChoice BUILD =              new AbilityBasedAC("BUILD",         1, 10, 1, 1, new int[0][0], Data.Ability.BUILD);
    public static ActionChoice NONE =               new ActionChoice("NONE",            0, 0, 0, 0, new int[0][0]){

        @Override
        public boolean canbePerformedBy(IUnit actor) {
            return false;
        }
    };

    public static ActionChoice[] choices = new ActionChoice[]{
            ATTACK,
            MOVE,
            SWITCH_POSITION,
            SWITCH_POSITION,
            PUSH,
            CHOOSE_ORIENTATION,
            HEAL,
            GUARD,
            STEAL,
            BUILD,
            END_TURN,
            NONE
    };

    public static ActionChoice[] values(){
        return choices;
    }

    public enum RangedBasedType{
        MOVE,
        WEAPON,
        SPECIFIC
    }

    protected String key;
    protected int cost;
    protected int experience;
    protected int rangeMin;
    protected int rangeMax;
    protected RangedBasedType rangedBasedType;
    protected boolean endTurnActionOnly;
    /**
     * AreaWidget of impact :
     * ( 0, 0) = targeted buildingType
     * ( 1, 0) = in front of targeted buildingType
     * ( 0, 1) = right of targeted buildingType
     * ( 0,-1) = left of targeted buildingType
     * (-1, 0) = in the back of targeted buildingType
     */
    protected Array<int[]> impactArea;



    ActionChoice(String key, int cost, int experience, int rangeMin, int rangeMax, RangedBasedType type, boolean endTurnActionOnly, int[][] impactArea) {
        this.key = key;
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

    ActionChoice(String key, int cost, int experience, int rangeMin, int rangeMax, int[][] impactArea){
        this(key, cost, experience, rangeMax, rangeMin, RangedBasedType.SPECIFIC, false, impactArea);
    }

    ActionChoice(String key, int cost, int experience, RangedBasedType type, int[][] impactArea) {
        this(key, cost, experience, -1, -1 , type, false, impactArea);
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

    public String getKey() {
        return key;
    }

    public String getName(I18NBundle bundle){
        return bundle.get(key);
    }

    public boolean isActorIsTarget() {
        return rangeMin == 0 && rangeMax == 0;
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

    static class AbilityBasedAC extends  ActionChoice{
        protected Data.Ability ability;

        AbilityBasedAC(String key, int cost, int experience, int rangeMin, int rangeMax, int[][] impactArea, Data.Ability ability) {
            super(key,cost, experience, rangeMin, rangeMax, impactArea);
            this.ability = ability;
        }

        AbilityBasedAC(String key, int cost, int experience, RangedBasedType type, int[][] impactArea, Data.Ability ability) {
            super(key, cost, experience, type, impactArea);
            this.ability = ability;
        }

        @Override
        public boolean canbePerformedBy(IUnit actor) {
            return cost <= actor.getActionPoints() && !actor.hasActed() && actor.has(ability);
        }
    }


}
