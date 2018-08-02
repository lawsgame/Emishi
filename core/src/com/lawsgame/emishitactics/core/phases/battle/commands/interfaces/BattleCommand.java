package com.lawsgame.emishitactics.core.phases.battle.commands.interfaces;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Formulas;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.patterns.command.Command;

/**
 *
 * I - Battle command usage
 *
 *  command.update(dt);
 *
 *  ActionChoice choice = ...;
 *  if(bcm.canActionBePerformed(...){
 *      BattleCommand command = bcm.get(...); /OR new XCommand(...);
 *      if(command != null && command.setActor(...)){
 *          command.setTarget(...);
 *          command.init();
 *          if(command.isTargetValid()){
 *              command.apply();
 *          }
 *      }
 *  }
 *
 *  II - battle command flow
 *
 *  1 - get the command
 *  2 - set actor and target
 *  3 - call isTargetValid
 *  4 - execute the command
 *
 * no need to put back the command in the BCM
 */
public abstract class BattleCommand implements Command{
    protected Battlefield battlefield;
    protected BattlefieldRenderer battlefieldRenderer;
    protected AnimationScheduler scheduler;
    protected Data.ActionChoice choice;
    protected boolean undoable;
    protected boolean endturnCommandOnly;

    protected int rowActor;
    protected int colActor;
    protected int rowTarget;
    protected int colTarget;
    protected EncounterOutcome outcome;
    private boolean initialize;


    public BattleCommand(BattlefieldRenderer bfr, Data.ActionChoice choice, AnimationScheduler scheduler, boolean undoable, boolean endturnCommandOnly){
        this.battlefieldRenderer = bfr;
        this.battlefield = bfr.getModel();
        this.scheduler = scheduler;
        this.choice = choice;
        this.rowActor = -1;
        this.colActor = -1;
        this.rowTarget = -1;
        this.colTarget = -1;
        this.undoable = undoable;
        this.endturnCommandOnly = endturnCommandOnly;
        this.outcome = new EncounterOutcome();
        this.initialize = false;

    }

    public void init(){
        this.initialize = true;
        this.outcome.reset();
    }

    @Override
    public final void apply() {
        if(isTargetValid())
            execute();
    }

    protected void apply(int rowActor, int colActor, int rowTarget, int colTarget){
        init();
        setActor(rowActor, colActor);
        setTarget(rowTarget, colTarget);
        apply();
    }

    protected abstract void execute();

    // can be deleted afterwards
    public final boolean isUndoable(){
        return undoable;
    }

    // is only callable when the unit turn is ending
    public final boolean isEndTurnCommandOnly(){
        return endturnCommandOnly;
    }

    /**
     * PLAYER ORIENTED METHOD
     *
     * TARGET CHECKING
     * @return whether or not THIS SPECIFIC TARGET is at range by the actor performing the given action if one's is standing the tile (rowActor, colActor)
     * while ignoring the actor's history and the unit other requirements to actually perform this action, namely : weapon/item and ability requirements.
     */
    public final boolean isTargetValid() {
        return initialize && isTargetValid(rowActor, colActor, rowTarget, colTarget);
    }

    /*
    required for testing retaliation availability for the attacked target without copy and paste the code of the
    BattleCommand.isTargetValid() method.
     */
    public abstract boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0);



    /**
     * AI ORIENTED METHOD
     *
     * check the third requirements to perform an action
     * - target availability requirements
     *
     * @param row
     * @param col
     * @return whether or not ANY TARGET is at range by the actor performing the given action if one's is standing the tile (row, col)
     * while ignoring the actor's history and the unit other requirements to actually perform this action, namely : weapon/item and ability requirements.
     */
    public abstract boolean atActionRange(int row, int col, IUnit actor);

    public final boolean atActionRange(){
        if(battlefield.isTileOccupied(rowActor, colActor))
            return atActionRange(rowActor, colActor, battlefield.getUnit(rowActor, colActor));
        return false;
    }

    /**
     *
     * @return fetch all tiles where the given unit is at range to perform the chosen action
     */
    public Array<int[]> getActionArea() {
        Array<int[]> actionArea = new Array<int[]>();
        if(battlefield.isTileOccupied(rowActor, colActor)) {
            switch (choice.getRangeType()) {
                case MOVE:
                    actionArea = battlefield.getMoveArea(rowActor, colActor);
                    break;
                case WEAPON:
                    IUnit actor = battlefield.getUnit(rowActor, colActor);
                    int rangeMin = actor.getCurrentWeaponRangeMin(rowActor, colActor, battlefield);
                    int rangeMax = actor.getCurrentWeaponRangeMax(rowActor, colActor, battlefield);
                    actionArea = Utils.getEreaFromRange(battlefield, rowActor, colActor, rangeMin, rangeMax);
                    break;
                case SPECIFIC:
                    actionArea = Utils.getEreaFromRange(battlefield, rowActor, colActor, choice.getRangeMin(), choice.getRangeMax());
                    break;
            }
        }
        return actionArea;
    }

    /**
     * TESTED
     * @return the relevantly oriented impact area of an action performed by an actor while targeting the tile {rowTarget, colTarget}
     */
    public final Array<int[]> getImpactArea(){
        Array<int[]> orientedArea = choice.getOrientedImpactArea(Utils.getOrientationFromCoords(rowActor, colActor, rowTarget, colTarget));
        if(battlefield.isTileExisted(rowTarget, colTarget)) {
            for (int i = 0; i < orientedArea.size; i++) {
                orientedArea.get(i)[0] += rowTarget;
                orientedArea.get(i)[1] += colTarget;
                if (!battlefield.isTileExisted(orientedArea.get(i)[0], orientedArea.get(i)[1])) {
                    orientedArea.removeIndex(i);
                    i--;
                }
            }
            orientedArea.add(new int[]{rowTarget, colTarget});
        }
        return orientedArea;
    }


    @Override
    public void undo() { }

    @Override
    public void redo() { }



    //------------------- HELPER METHODS -----------------------------

    protected final boolean isTargetAllyValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0, boolean woundedRequired){
        boolean valid = false;
        if(battlefield.isTileOccupied(rowActor0, colActor0)
                && (choice.getRangeType() ==  Data.RangedBasedType.WEAPON || choice.getRangeType() == Data.RangedBasedType.SPECIFIC)){
            IUnit actor = battlefield.getUnit(rowActor0, colActor0);
            int rangeMin = (choice.getRangeType() == Data.RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMin(rowActor0, colActor0, battlefield) : choice.getRangeMin();
            int rangeMax = (choice.getRangeType() == Data.RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMax(rowActor0, colActor0, battlefield) : choice.getRangeMax();
            int dist = Utils.dist(rowActor0, colActor0, rowTarget0, colTarget0);
            if (rangeMin <= dist && dist <= rangeMax) {
                if(battlefield.isTileOccupiedByAlly(rowTarget0, colTarget0, actor.getAllegeance())
                        && (!woundedRequired || battlefield.getUnit(rowTarget0, colTarget0).isWounded())) {
                    valid = true;
                }
            }
        }
        return valid;
    }

    protected final boolean isEnemyTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0, boolean stealableRequired){
        boolean valid = false;
        if(battlefield.isTileOccupied(rowActor0, colActor0)
                && (choice.getRangeType() ==  Data.RangedBasedType.WEAPON || choice.getRangeType() == Data.RangedBasedType.SPECIFIC)){
            IUnit actor = battlefield.getUnit(rowActor0, colActor0);
            int rangeMin = (choice.getRangeType() == Data.RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMin(rowActor0, colActor0, battlefield) : choice.getRangeMin();
            int rangeMax = (choice.getRangeType() == Data.RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMax(rowActor0, colActor0, battlefield) : choice.getRangeMax();
            int dist = Utils.dist(rowActor0, colActor0, rowTarget0, colTarget0);
            if (rangeMin <= dist && dist <= rangeMax) {
                if(battlefield.isTileOccupiedByFoe(rowTarget0, colTarget0, actor.getAllegeance())
                        && (!stealableRequired || battlefield.getUnit(rowTarget0, colTarget0).isStealable())) {
                    valid = true;
                }
            }
        }
        return valid;
    }

    protected final boolean isAllyAtActionRange(int row, int col, IUnit actor, boolean woundedRequired){
        boolean targetAtRange = false;
        if(choice.getRangeType() ==  Data.RangedBasedType.WEAPON || choice.getRangeType() == Data.RangedBasedType.SPECIFIC) {
            int[] unitPos = battlefield.getUnitPos(actor);
            int rangeMin = (choice.getRangeType() == Data.RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMin(unitPos[0], unitPos[1], battlefield) : choice.getRangeMin();
            int rangeMax = (choice.getRangeType() == Data.RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMax(unitPos[0], unitPos[1], battlefield) : choice.getRangeMax();
            int dist;
            loop:
            {
                for (int r = row - rangeMin; r <= row + rangeMax; r++) {
                    for (int c = col - rangeMin; c <= col + rangeMax; c++) {
                        dist = Utils.dist(row, col, r, c);
                        if (rangeMin <= dist && dist <= rangeMax
                                && battlefield.isTileOccupiedByAlly(r, c, actor.getAllegeance())
                                && (!woundedRequired || battlefield.getUnit(r, c).isWounded())) {
                            targetAtRange = true;
                            break loop;

                        }
                    }
                }
            }
        }
        return targetAtRange;
    }

    protected final boolean isEnemyAtActionRange(int row, int col, IUnit actor, boolean stealableRequired){
        boolean targetAtRange = false;
        if(choice.getRangeType() ==  Data.RangedBasedType.WEAPON || choice.getRangeType() == Data.RangedBasedType.SPECIFIC) {
            int[] unitPos = battlefield.getUnitPos(actor);
            int rangeMin = (choice.getRangeType() == Data.RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMin(unitPos[0], unitPos[1], battlefield) : choice.getRangeMin();
            int rangeMax = (choice.getRangeType() == Data.RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMax(unitPos[0], unitPos[1], battlefield) : choice.getRangeMax();
            int dist;
            loop:
            {
                for (int r = row - rangeMin; r <= row + rangeMax; r++) {
                    for (int c = col - rangeMin; c <= col + rangeMax; c++) {
                        dist = Utils.dist(row, col, r, c);
                        if (rangeMin <= dist
                                && dist <= rangeMax
                                && battlefield.isTileOccupiedByFoe(r, c, actor.getAllegeance())
                                && (!stealableRequired || battlefield.getUnit(r, c).isStealable())) {
                            targetAtRange = true;
                            break loop;
                        }
                    }
                }
            }
        }
        return targetAtRange;
    }

    /**
     *
     * @param rowImpactTile
     * @param colImpactTile
     * @return get all possible target tile knowing that the given tile is within the impact area
     */
    protected final Array<int[]> getTargetFromCollateral(int rowImpactTile, int colImpactTile) {
        Array<int[]> possibleTargetTiles = new Array<int[]>();
        Array<int[]> impactArea;
        int row;
        int col;
        for(Data.Orientation or: Data.Orientation.values()){
            impactArea = choice.getOrientedImpactArea(or);
            for(int i = 0; i < impactArea.size; i++){
                row =rowImpactTile - impactArea.get(i)[0];
                col =colImpactTile - impactArea.get(i)[1];
                if(battlefield.isTileExisted(row, col) && or == Utils.getOrientationFromCoords(rowActor, colActor, row, col)){
                    possibleTargetTiles.add(new int[]{row, col});
                }
            }
        }
        return possibleTargetTiles;
    }




    //------------------ GETTERS & SETTERS ---------------------------


    public final Data.ActionChoice getActionChoice() {
        return choice;
    }

    public final void setBattlefield(BattlefieldRenderer battlefieldRenderer){
        this.battlefield = battlefieldRenderer.getModel();
        this.battlefieldRenderer = battlefieldRenderer;

    }

    public final boolean setActor(int rowActor, int colActor) {
        if(battlefield.isTileOccupied(rowActor, colActor)) {
            this.rowActor = rowActor;
            this.colActor = colActor;
            return true;
        }
        return false;
    }

    public final void setTarget(int rowTarget, int colTarget){
        if(battlefield.isTileExisted(rowTarget, colTarget)){
            this.rowTarget = rowTarget;
            this.colTarget = colTarget;

        }
    }

    public final EncounterOutcome getOutcome(){
        return outcome;
    }







    // ----------------- Encounter outcome CLASS -----------------

    public static class EncounterOutcome {
        public Array<IUnit> receivers;
        public Array<Integer> experienceGained;
        public Array<Item> droppedItems;

        public EncounterOutcome() {
            reset();
        }

        public void reset(){
            this.receivers = new Array<IUnit>();
            this.experienceGained = new Array<Integer>();
            this.droppedItems = new Array<Item>();

        }

        public boolean isExperienceGainedHandled(){
            return receivers.size == 0;
        }

        public boolean isHandled(){
            return receivers.size == 0 && droppedItems.size == 0;
        }

        @Override
        public String toString(){
            String str = "\nOUTCOME\n";
            for(int i = 0; i < receivers.size; i++){
                str += "\nReceiver : "+receivers.get(i).getName()+" => experience gained : "+experienceGained.get(i);
            }
            for(int i = 0; i < droppedItems.size; i++){
                str += "\nStolen item : "+ droppedItems.get(i).getName();
            }
            return str+"\n";
        }
    }

}
