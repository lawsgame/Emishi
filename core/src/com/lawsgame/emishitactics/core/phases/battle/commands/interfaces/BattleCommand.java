package com.lawsgame.emishitactics.core.phases.battle.commands.interfaces;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.ActionChoice;
import com.lawsgame.emishitactics.core.models.ActionChoice.RangedBasedType;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.TileType;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.patterns.command.Command;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;

/**
 *
 * I - Battle command usage
 *
 *  command.update(dt);
 *
 *  ActionChoice choice = ...;
 *  if(bcm.canActionBePerformed(...){
 *      BattleCommand command = bcm.getInstance(...); /OR new XCommand(...);
 *      if(command != null && command.setActor(...)){
 *          command.setTarget(...);
 *          if(command.isTargetValid()){
 *              command.init();
 *              command.apply();
 *          }
 *      }
 *  }
 *
 *  II - battle command flow
 *
 *  1 - getInstance the command
 *  2 - set actor and target
 *  3 - call isTargetValid
 *  4 - execute the command
 *
 * no need to put back the command in the BCM
 *
 *
 * execute method struture :
 *  0 - (optional) register the old model state to perform undo() if required
 *  1 - update the model
 *  2 - push the render task
 *  3 - set the outcome bundle
 */
public abstract class BattleCommand implements Command, Observer{

    protected final Battlefield battlefield;
    protected final BattlefieldRenderer battlefieldRenderer;
    private final AnimationScheduler scheduler;
    protected final ActionChoice choice;
    protected final boolean undoable;
    private final boolean acted;                    // if true, command that turn acted to true is executed, moved otherwise.

    private boolean free;                           // command that does not count as the player choice i.e. set acted and moved as true while being applied nor it costs any OA point

    protected IUnit actor;
    protected IUnit target;
    protected int rowActor;
    protected int colActor;
    protected int rowTarget;
    protected int colTarget;

    protected EncounterOutcome outcome;
    private boolean initialized;                     // control variable to prevent a battle command to be executed before being initialized and checking if the target choice is valid

    private boolean launched;
    private Array<Task> renderTasks;                // ids which allows to certify that the rendering of the command is executing / completed


    public BattleCommand(BattlefieldRenderer bfr, ActionChoice choice, AnimationScheduler scheduler, boolean undoable, boolean acted, boolean free){
        this.battlefieldRenderer = bfr;
        this.battlefield = bfr.getModel();
        this.scheduler = scheduler;
        this.choice = choice;
        this.rowActor = -1;
        this.colActor = -1;
        this.rowTarget = -1;
        this.colTarget = -1;
        this.undoable = undoable;
        this.initialized = false;
        this.acted = acted;
        this.free = free;
        this.launched = false;
        this.outcome = new EncounterOutcome();
        this.renderTasks = new Array<Task>();
    }

    public void init(){
        if(isTargetValid()) {
            this.initialized = true;
            this.outcome.reset();
            this.renderTasks.clear();
        }
    }

    @Override
    public final void apply() {
        if(initialized) {
            initialized = false;

            // set as moved or acted if required
            if(!free){
                if(acted) {
                    getActor().setActed(true);
                }else {
                    getActor().setMoved( true);
                }
            }

            this.launched = true;
            execute();

            // remove already OoA units
            outcome.clean();
        }
    }

    public final void apply(int rowActor, int colActor, int rowTarget, int colTarget){
        setActor(rowActor, colActor);
        setTarget(rowTarget, colTarget);
        if(isTargetValid()){
            init();
            apply();
        }
    }

    public final void apply(int rowActor, int colActor){
        apply(rowActor, colActor, rowActor, colActor);
    }



    public boolean isExecuting(){
        return launched && renderTasks.size > 0;
    }

    public boolean isCompleted(){
        return launched && renderTasks.size == 0;
    }

    public void scheduleRenderTask(Task task){
        if(!task.isIrrelevant()) {
            task.attach(this);
            renderTasks.add(task);
            scheduler.addTask(task);
            System.out.println("\nClient "+this.toString());
            System.out.println("nb "+renderTasks.size+" ADD "+task);
        }
    }

    @Override
    public void getNotification(Object data) {
        if(data instanceof Task){
            Task completedTask = (Task)data;
            completedTask.detach(this);
            renderTasks.removeValue(completedTask, true);
            System.out.println("\nClient "+this.toString()+"\nnb "+renderTasks.size+" REMOVE ");
        }
    }

    protected abstract void execute();

    // can be deleted afterwards
    public final boolean isUndoable(){
        return undoable;
    }

    // called to checked actor requirements
    public boolean canbePerformedBy(IUnit actor){
        return choice.canbePerformedBy(actor);
    }

    /**
     * PLAYER ORIENTED METHOD
     *
     * TARGET CHECKING
     * @return whether or not THIS SPECIFIC TARGET is at range by the actor performing the given action if one's is standing the buildingType (rowActor, colActor)
     * while ignoring the actor's history and the unit other requirements to actually perform this action, namely : weapon/item and ability requirements.
     */
    public boolean isTargetValid() {
        return isTargetValid(rowActor, colActor, rowTarget, colTarget);
    }

    public void blink(final boolean enable){
        if(battlefield.isTileOccupied(rowTarget, colTarget)){

            final BattleUnitRenderer targetRenderer = battlefieldRenderer.getUnitRenderer(battlefield.getUnit(rowTarget, colTarget));
            StandardTask blinkTask = new StandardTask(targetRenderer, new Notification.Blink(enable));
            blinkTask.tag("blink ("+enable+")");
            scheduleRenderTask(blinkTask);

        }
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
     * @return whether or not ANY TARGET is at range by the actor performing the given action if one's is standing the buildingType (row, col)
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
     * @return the relevantly oriented impact area of an action performed by an actor while targeting the buildingType {rowTarget, colTarget}
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
                && (choice.getRangeType() ==  RangedBasedType.WEAPON || choice.getRangeType() == RangedBasedType.SPECIFIC)){
            IUnit actor = battlefield.getUnit(rowActor0, colActor0);
            int rangeMin = (choice.getRangeType() == RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMin(rowActor0, colActor0, battlefield) : choice.getRangeMin();
            int rangeMax = (choice.getRangeType() == RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMax(rowActor0, colActor0, battlefield) : choice.getRangeMax();
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
                && (choice.getRangeType() ==  RangedBasedType.WEAPON || choice.getRangeType() == RangedBasedType.SPECIFIC)){
            IUnit actor = battlefield.getUnit(rowActor0, colActor0);
            int rangeMin = (choice.getRangeType() == RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMin(rowActor0, colActor0, battlefield) : choice.getRangeMin();
            int rangeMax = (choice.getRangeType() == RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMax(rowActor0, colActor0, battlefield) : choice.getRangeMax();
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
        if(choice.getRangeType() ==  RangedBasedType.WEAPON || choice.getRangeType() == RangedBasedType.SPECIFIC) {
            int[] unitPos = battlefield.getUnitPos(actor);
            int rangeMin = (choice.getRangeType() == RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMin(unitPos[0], unitPos[1], battlefield) : choice.getRangeMin();
            int rangeMax = (choice.getRangeType() == RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMax(unitPos[0], unitPos[1], battlefield) : choice.getRangeMax();
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
        if(choice.getRangeType() ==  RangedBasedType.WEAPON || choice.getRangeType() == RangedBasedType.SPECIFIC) {
            int[] unitPos = battlefield.getUnitPos(actor);
            int rangeMin = (choice.getRangeType() == RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMin(unitPos[0], unitPos[1], battlefield) : choice.getRangeMin();
            int rangeMax = (choice.getRangeType() == RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMax(unitPos[0], unitPos[1], battlefield) : choice.getRangeMax();
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
     * @return getInstance all possible target buildingType knowing that the given buildingType is within the impact area
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


    public String getName(I18NBundle bundle){
        return choice.getName(bundle);
    }

    public final ActionChoice getActionChoice() {
        return choice;
    }


    public final boolean setActor(int rowActor, int colActor) {
        if(battlefield.isTileOccupied(rowActor, colActor)) {
            launched = false;
            this.rowActor = rowActor;
            this.colActor = colActor;
            this.actor = battlefield.getUnit(rowActor, colActor);
            if(choice.isActorIsTarget()){
                setTarget(rowActor, colActor);
            }
            return true;
        }
        return false;
    }

    public final IUnit getActor(){
        return actor;
    }


    public final int getRowActor() {
        return rowActor;
    }

    public final int getColActor() {
        return colActor;
    }


    public final void setTarget(int rowTarget, int colTarget){
        if(battlefield.isTileExisted(rowTarget, colTarget)){
            launched = false;
            this.rowTarget = rowTarget;
            this.colTarget = colTarget;
            this.target = battlefield.getUnit(rowTarget, colTarget);
        }
    }

    public final IUnit getTarget(){
        return target;
    }


    public final int getRowTarget() { return rowTarget; }

    public final int getColTarget() { return colTarget; }


    public final TileType getTargetTile() {
        return battlefield.getTile(rowTarget, colTarget);
    }

    public final EncounterOutcome getOutcome(){
        return outcome;
    }

    public final Battlefield getBattlefield() {
        return battlefield;
    }

    public final boolean isFree() {
        return free;
    }

    public final void setFree(boolean free) {
        this.free = free;
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

        public boolean isExperienceDistributed(){
            return receivers.size == 0;
        }

        public boolean isLootedItemsReclaimed(){ return droppedItems.size == 0; }

        public boolean isHandled(){
            return receivers.size == 0 && droppedItems.size == 0;
        }

        public void clean(){
            if(receivers.size != experienceGained.size){
                receivers.clear();
                experienceGained.clear();
                try{
                    throw new BattleOutcomeException("senders' and experience' arrays size don't match");
                }catch(Exception e){
                    e.getStackTrace();
                }
            }
            for(int i = 0; i < receivers.size; i++){
                if(receivers.get(i).isOutOfAction()){
                    receivers.removeIndex(i);
                    experienceGained.removeIndex(i);
                    i--;
                }
            }
        }

        @Override
        public String toString(){
            String str = "\nOUTCOME\n";
            for(int i = 0; i < receivers.size; i++){
                str += "\nReceiver : "+receivers.get(i).getName()+" => experience gained : "+experienceGained.get(i);
            }
            for(int i = 0; i < droppedItems.size; i++){
                str += "\nStolen item : "+ droppedItems.get(i).toString();
            }
            return str+"\n";
        }
    }

    public static class BattleOutcomeException extends Exception{
        public BattleOutcomeException(String s) {
            super(s);
        }
    }

    @Override
    public String toString() {
        return getActionChoice().getKey();
    }


}
