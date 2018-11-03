package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.RangedBasedType;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data.TileType;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

import java.util.Stack;

/**
 *
 * I - Battle command usage
 *
 *  command.update(dt);
 *
 *  ActionChoice choice = ...;
 *  if(bcm.canActionBePerformed(actor, ...){
 *      ActorCommand command = bcm.get(...);
 *      command.setTarget(...);
 *      if(command.apply()){
 *
 *          // the command has been applied successfully*
 *      }else{
 *
 *          // the command failed to be applied
 *      }
 *  }
 *
 *  OR
 *
 *  ActionChoice choice = ...;
 *  ActorCommand command = new ActorCommand( choice, ...);
 *  if(command.setInitiator(...)){
 *      command.setTarget(...);
 *      if(command.apply()){
 *
 *          // the command has been applied successfully
 *      }else{
 *
 *          // the command failed to be applied
 *      }
 *  }
 *
 *  II - battle command flow
 *
 *  1 - get the command
 *  2 - setTiles initiator and target
 *  3 - call isTargetValid
 *  4 - execute the command
 *
 * no need to put back the command in the BCM
 *
 *
 * ActorCommand.execute() method struture :
 *  0 - (optional) register the old model state to perform undo() if required
 *  1 - update the model
 *  2 - push the render task
 *  3 - set the outcome bundle
 */
public abstract class ActorCommand extends BattleCommand{


    protected final ActionChoice choice;
    protected boolean registerAction;
    private boolean free;                           // command that does not count as the player choice i.e. set acted and moved as "registerAction" while being applied nor it costs any OA point


    private IUnit initiator;
    private IUnit target;
    protected int rowActor;
    protected int colActor;
    protected int rowTarget;
    protected int colTarget;
    protected boolean eventTriggered;

    protected Outcome outcome;


    public ActorCommand(BattlefieldRenderer bfr, ActionChoice choice, AnimationScheduler scheduler, Inventory playerInventory, boolean free){
        super(bfr, scheduler);
        this.choice = choice;
        this.outcome = new Outcome(playerInventory);
        this.free = free;
    }

    /**
     * to be reusable, useful for the BCM
     */
    public void init(){
        this.rowActor = -1;
        this.colActor = -1;
        this.rowTarget = -1;
        this.colTarget = -1;
        this.eventTriggered = false;
        this.registerAction = true;
    }

    public final void setInitiator(int rowActor, int colActor) {
        this.rowActor = rowActor;
        this.colActor = colActor;
        this.initiator = bfr.getModel().getUnit(rowActor, colActor);
    }

    public final void setTarget(int rowTarget, int colTarget){
        this.rowTarget = rowTarget;
        this.colTarget = colTarget;
        this.target = bfr.getModel().getUnit(rowTarget, colTarget);
    }


    @Override
    public final boolean apply() {
        // disable the blinking of the target
        // this.highlightTargets(false);

        // Outcome and animation scheduler cleared.
        this.outcome.reset();

        if(super.apply()) {

            /*
             * 1) set as moved or acted if required
             * 2) actor pays the cost of performing this action
             */
            if(!free){

                if(choice.isActedBased()) {
                    getInitiator().setActed(registerAction);
                }else {
                    getInitiator().setMoved(registerAction);
                }

                getInitiator().addActionPoints(-choice.getCost());
            }

            outcome.clean();
            outcome.resolve();

            return true;
        }
        return false;
    }

    public final boolean apply(int rowActor, int colActor, int rowTarget, int colTarget){
        init();
        setInitiator(rowActor, colActor);
        setTarget(rowTarget, colTarget);
        return apply();
    }

    public final boolean apply(int rowActor, int colActor){
        return apply(rowActor, colActor, rowActor, colActor);
    }

    @Override
    public final boolean undo(){
        if(super.undo()) {

            if (!free) {
                if (choice.isActedBased())
                    getInitiator().setActed(false);
                else
                    getInitiator().setMoved(false);

                getInitiator().addActionPoints(choice.getCost());
            }
            return true;
        }
        return false;
    }

    protected void unexecute(){ }

    @Override
    public boolean isUndoable() {
        return choice.isUndoable() && !initiator.isOutOfAction() && !eventTriggered;
    }

    @Override
    public boolean isApplicable() {
        return isInitiatorValid() && isTargetValid();
    }


    // called to checked initiator requirements
    public  boolean isInitiatorValid(){
        boolean valid = false;
        if(bfr.getModel().isTileOccupied(rowActor, colActor)){
            IUnit actor = bfr.getModel().getUnit(rowActor, colActor);
            if(!actor.isOutOfAction()) {
                if (free || choice.getCost() <= actor.getActionPoints()) {
                    if (choice.isActedBased()) {
                        valid = (free || !actor.hasActed()) && !actor.isDisabled();
                    } else {
                        valid = (free || !actor.hasMoved()) && !actor.isCrippled();
                    }
                }
            }
        }
        return valid;
    }

    /**
     * PLAYER ORIENTED METHOD
     *
     * TARGET CHECKING
     * @return whether or not THIS SPECIFIC TARGET is at range by the initiator performing the given action if one's is standing the buildingType (rowActor, colActor)
     * while ignoring the initiator's history and the unit other requirements to actually perform this action, namely : weapon/item and ability requirements.
     */
    public boolean isTargetValid() {
        return isTargetValid(rowActor, colActor, rowTarget, colTarget);
    }



        /*
    required for testing retaliation availability for the attacked target without copy and paste the code of the
    ActorCommand.isTargetValid() method.
     */
    public abstract boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0);


    public void highlightTargets(final boolean enable){
        if(target != null){

            final BattleUnitRenderer targetRenderer = bfr.getUnitRenderer(target);
            StandardTask blinkTask = new StandardTask(targetRenderer, Notification.Blink.get(enable));
            //blinkTask.tag("highlightTargets ("+enable+")");
            scheduler.addTask(blinkTask);

        }
    }




    /**
     * AI ORIENTED METHOD
     *
     * performEvent the third requirements to perform an action
     * - target availability requirements
     *
     * @param row
     * @param col
     * @return whether or not ANY TARGET is at range by the initiator performing the given action if one's is standing the buildingType (row, col)
     * while ignoring the initiator's history and the unit other requirements to actually perform this action, namely : weapon/item and ability requirements.
     */
    public final boolean atActionRange(int row, int col, IUnit actor){
        return getTargetsAtRange(row, col, actor).size > 0;
    }

    public final boolean atActionRange(){
        return bfr.getModel().isTileOccupied(rowActor, colActor)
                && atActionRange(rowActor, colActor, bfr.getModel().getUnit(rowActor, colActor));
    }

    /**
     * AI oriented method
     *
     * return : all tile coords of available targets
     */
    public abstract Array<int[]> getTargetsAtRange(int row, int col, IUnit actor);

    public final Array<int[]> getTargetsAtRange(){
        if(bfr.getModel().isTileOccupied(rowActor, colActor))
            return getTargetsAtRange(rowActor, colActor, bfr.getModel().getUnit(rowActor, colActor));
        return new Array<int[]>();
    }



    /**
     *
     * @return fetch all tiles where the given unit is at range to perform the chosen action
     */
    public Array<int[]> getActionArea() {
        Array<int[]> actionArea = new Array<int[]>();
        if(bfr.getModel().isTileOccupied(rowActor, colActor)) {
            switch (choice.getRangedType()) {
                case MOVE:
                    actionArea = bfr.getModel().getMoveArea(rowActor, colActor);
                    break;
                case WEAPON:
                    IUnit actor = bfr.getModel().getUnit(rowActor, colActor);
                    int rangeMin = actor.getCurrentWeaponRangeMin(rowActor, colActor, bfr.getModel());
                    int rangeMax = actor.getCurrentWeaponRangeMax(rowActor, colActor, bfr.getModel());
                    actionArea = Utils.getEreaFromRange(bfr.getModel(), rowActor, colActor, rangeMin, rangeMax);
                    break;
                case SPECIFIC:
                    actionArea = Utils.getEreaFromRange(bfr.getModel(), rowActor, colActor, choice.getRangeMin(), choice.getRangeMax());
                    break;
            }
        }
        return actionArea;
    }

    /**
     *
     * @return the relevantly oriented impact area of an action performed by an initiator while targeting the buildingType {rowTarget, colTarget}
     */
    public final Array<int[]> getImpactArea(){
        return getImpactArea(rowActor, colActor, rowTarget, colTarget);
    }

    public final Array<int[]> getImpactArea(int rowActor0, int colActor0, int rowTarget0, int colTarget0){
        Array<int[]> orientedArea = choice.getOrientedImpactArea(Utils.getOrientationFromCoords(rowActor0, colActor0, rowTarget0, colTarget0));
        if(bfr.getModel().isTileExisted(rowTarget0, colTarget0)) {
            for (int i = 0; i < orientedArea.size; i++) {
                orientedArea.get(i)[0] += rowTarget0;
                orientedArea.get(i)[1] += colTarget0;
                if (!bfr.getModel().isTileExisted(orientedArea.get(i)[0], orientedArea.get(i)[1])) {
                    orientedArea.removeIndex(i);
                    i--;
                }
            }
        }

        return orientedArea;

    }

    /**
     *
     * @param rowActor0
     * @param colActor0
     * @param rowTarget0
     * @param colTarget0
     * @param actor
     * @return the actual target to be acted upon by the actor if he was at (rowA, colA) and targets the tile (rowT, colT)
     */
    public Array<int[]> getTargetsFromImpactArea(int rowActor0, int colActor0, int rowTarget0, int colTarget0, IUnit actor){
        Array<int[]> impactArea = getImpactArea(rowActor0, colActor0, rowTarget0, colTarget0);
        Array<int[]> targets = getTargetsAtRange(rowActor0, colActor0, actor);
        return Utils.arrayGetElementsInBothOnly(impactArea, targets);
     }

    public Array<int[]> getTargetsFromImpactArea(){
        return getTargetsFromImpactArea(rowActor, colActor, rowTarget, colTarget, initiator);
    }


    //------------------- HELPER METHODS -----------------------------

    protected final boolean isTargetAllyValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0, boolean woundedRequired, boolean canMove){
        boolean valid = false;
        if(bfr.getModel().isTileOccupied(rowActor0, colActor0)
                && (choice.getRangedType() ==  Data.RangedBasedType.WEAPON || choice.getRangedType() == Data.RangedBasedType.SPECIFIC)){
            IUnit actor = bfr.getModel().getUnit(rowActor0, colActor0);
            int rangeMin = (choice.getRangedType() == Data.RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMin(rowActor0, colActor0, bfr.getModel()) : choice.getRangeMin();
            int rangeMax = (choice.getRangedType() == Data.RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMax(rowActor0, colActor0, bfr.getModel()) : choice.getRangeMax();
            int dist = Utils.dist(rowActor0, colActor0, rowTarget0, colTarget0);
            if (rangeMin <= dist && dist <= rangeMax) {
                Array<int[]> impactArea = getImpactArea(rowActor0, colActor0, rowTarget0, colTarget0);
                for(int i = 0; i < impactArea.size; i++) {
                    if(bfr.getModel().isTileOccupiedByAlly(impactArea.get(i)[0], impactArea.get(i)[1], actor.getArmy().getAffiliation())
                            && (!woundedRequired || bfr.getModel().getUnit(impactArea.get(i)[0], impactArea.get(i)[1]).isWounded())
                            && (!canMove || !actor.isCrippled())) {

                        valid = true;
                    }
                }
            }
        }
        return valid;
    }

    protected final boolean isEnemyTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0, boolean stealableRequired){
        boolean valid = false;
        if(bfr.getModel().isTileOccupied(rowActor0, colActor0)
                && (choice.getRangedType() ==  RangedBasedType.WEAPON || choice.getRangedType() == RangedBasedType.SPECIFIC)){
            IUnit actor = bfr.getModel().getUnit(rowActor0, colActor0);
            int rangeMin = (choice.getRangedType() == RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMin(rowActor0, colActor0, bfr.getModel()) : choice.getRangeMin();
            int rangeMax = (choice.getRangedType() == RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMax(rowActor0, colActor0, bfr.getModel()) : choice.getRangeMax();
            int dist = Utils.dist(rowActor0, colActor0, rowTarget0, colTarget0);
            if (rangeMin <= dist && dist <= rangeMax) {
                Array<int[]> impactArea = getImpactArea(rowActor0, colActor0, rowTarget0, colTarget0);
                for(int i = 0; i < impactArea.size; i++) {
                    if (bfr.getModel().isTileOccupiedByFoe(impactArea.get(i)[0], impactArea.get(i)[1], actor.getArmy().getAffiliation())
                            && (!stealableRequired || bfr.getModel().getUnit(impactArea.get(i)[0], impactArea.get(i)[1]).isStealable())) {
                        valid = true;
                    }
                }
            }
        }
        return valid;
    }

    protected final Array<int[]> getAlliesAtRange(int row, int col, IUnit actor, boolean woundedRequired, boolean canMove){
        Array<int[]>  targetsAtRange = new Array<int[]>();
        if(choice.getRangedType() ==  RangedBasedType.WEAPON || choice.getRangedType() == RangedBasedType.SPECIFIC) {
            int rangeMin = (choice.getRangedType() == RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMin(row, col, bfr.getModel()) : choice.getRangeMin();
            int rangeMax = (choice.getRangedType() == RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMax(row, col, bfr.getModel()) : choice.getRangeMax();
            int dist;
            for (int r = row - rangeMin; r <= row + rangeMax; r++) {
                for (int c = col - rangeMin; c <= col + rangeMax; c++) {

                    dist = Utils.dist(row, col, r, c);
                    if(rangeMin <= dist && dist <= rangeMax){
                        targetsAtRange.addAll(getTargetedAllies(row, col, r, c, actor, woundedRequired, canMove));
                    }
                }
            }

        }
        return Utils.arrayRemoveClones(targetsAtRange);
    }

    protected final Array<int[]> getFoesAtRange(int row, int col, IUnit actor, boolean stealableRequired){
        Array<int[]>  targetsAtRange = new Array<int[]>();
        if(choice.getRangedType() ==  RangedBasedType.WEAPON || choice.getRangedType() == RangedBasedType.SPECIFIC) {
            int rangeMin = (choice.getRangedType() == RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMin(row, col, bfr.getModel()) : choice.getRangeMin();
            int rangeMax = (choice.getRangedType() == RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMax(row, col, bfr.getModel()) : choice.getRangeMax();
            int dist;
            for (int r = row - rangeMin; r <= row + rangeMax; r++) {
                for (int c = col - rangeMin; c <= col + rangeMax; c++) {

                    dist = Utils.dist(row, col, r, c);
                    if(rangeMin <= dist && dist <= rangeMax){
                        targetsAtRange.addAll(getTargetedFoes(row, col, r, c, actor, stealableRequired));
                    }
                }
            }
        }

        return Utils.arrayRemoveClones(targetsAtRange);
    }

    protected final Array<int[]> getTargetedAllies(int rowActor, int colActor, int rowTarget, int colTarget, IUnit actor, boolean woundedRequired, boolean canMove){
        Array<int[]> targets = getImpactArea(rowActor, colActor, rowTarget, colTarget);
        if(actor == null){
            targets.clear();
        }else {
            for (int i = 0; i < targets.size; i++) {
                if (!bfr.getModel().isTileOccupiedByAlly(targets.get(i)[0], targets.get(i)[1], actor.getArmy().getAffiliation())
                        || (woundedRequired && !bfr.getModel().getUnit(targets.get(i)[0], targets.get(i)[1]).isWounded())
                        || (canMove && actor.isCrippled())) {

                    targets.removeIndex(i);
                    i--;
                }
            }
        }
        return targets;
    }
    protected final Array<int[]> getTargetedFoes(int rowActor, int colActor, int rowTarget, int colTarget, IUnit actor, boolean stealableRequired){
        Array<int[]> targets = getImpactArea(rowActor, colActor, rowTarget, colTarget);
        if(actor == null) {
            targets.clear();
        }else {
            for (int i = 0; i < targets.size; i++) {
                if (!bfr.getModel().isTileOccupiedByFoe(targets.get(i)[0], targets.get(i)[1], actor.getArmy().getAffiliation())
                        || (stealableRequired && !bfr.getModel().getUnit(targets.get(i)[0], targets.get(i)[1]).isStealable())) {
                    targets.removeIndex(i);
                    i--;
                }
            }
        }
        return targets;
    }


    //------------------ GETTERS & SETTERS ---------------------------


    public String getName(I18NBundle bundle){
        return bundle.get(choice.name());
    }

    public final ActionChoice getActionChoice() {
        return choice;
    }

    public final IUnit getInitiator(){
        return initiator;
    }

    public final int getRowActor() {
        return rowActor;
    }

    public final int getColActor() {
        return colActor;
    }

    public final IUnit getTarget(){
        return target;
    }

    public final int getRowTarget() { return rowTarget; }

    public final int getColTarget() { return colTarget; }

    public final TileType getTargetTile() {
        return bfr.getModel().getTile(rowTarget, colTarget).getType();
    }

    public final Outcome getOutcome(){
        return outcome;
    }

    public final Battlefield getBattlefield() {
        return bfr.getModel();
    }

    public final boolean isFree() {
        return free;
    }

    public final void setFree(boolean free) {
        this.free = free;
    }


    @Override
    public String toString() {
        String str = "\nActorCommand  : " +getActionChoice().name();
        str += "\n    initiator : "+getInitiator().getName();
        str += "\n    target : "+((bfr.getModel().isTileOccupied(rowTarget, colTarget)) ?  bfr.getModel().getUnit(rowTarget, colTarget).getName(): " ("+rowTarget+" "+colTarget+") ");
        str += "\n    executable ? "+isTargetValid();
        return str;
    }

    @Override
    public String toShortString() {
        return "ActorCommand  : " +getActionChoice().name();
    }

    // ----------------- Encounter outcome CLASS -----------------



public static class Outcome {
        public Array<ExperiencePointsHolder> expHolders;
        public Array<DroppedItemHolder> droppedItemHolders;
        public Inventory playerInventory;
        boolean resolved;

        public Outcome(Inventory playerInventory) {
            this.playerInventory = playerInventory;
            this.expHolders = new Array<ExperiencePointsHolder>();
            this.droppedItemHolders = new Array<DroppedItemHolder>();
            this.resolved = false;
        }

        public void reset(){
            this.droppedItemHolders.clear();
            this.expHolders.clear();
            this.resolved = false;

        }

        public void add(IUnit receiver, int experience){
            expHolders.add(new ExperiencePointsHolder(receiver, experience));
        }

        public void add(Item droppedItem, boolean playerOwned){
            droppedItemHolders.add(new DroppedItemHolder(droppedItem, playerOwned));
        }

        public void merge(Outcome outcome){
            expHolders.addAll(outcome.expHolders);
            droppedItemHolders.addAll(outcome.droppedItemHolders);
        }

        public boolean isExpHandled(){
            return expHolders.size == 0;
        }

        public boolean isHandled(){
            return expHolders.size == 0 && droppedItemHolders.size == 0;
        }

        void clean(){
            // remove OOA units
            for (int i = 0; i < expHolders.size; i++) {
                if (expHolders.get(i).isIrrelevant()) {
                    expHolders.removeIndex(i);
                    i--;
                }
            }

            // merges identical receivers and sums the associated exp gains
            for (int i = 0; i < expHolders.size; i++) {
                for (int j = i + 1; j < expHolders.size; j++) {
                    if (expHolders.get(i).receiver == expHolders.get(j).receiver) {
                        if(expHolders.get(i).experience < expHolders.get(j).experience)
                            expHolders.get(i).experience = expHolders.get(j).experience;
                        expHolders.removeIndex(j);
                        j--;
                    }
                }
            }
        }

        void resolve(){
            if(!resolved) {
                resolved = true;
                for (int i = 0; i < expHolders.size; i++) {
                    expHolders.get(i).solve();
                }

                for (int i = 0; i < droppedItemHolders.size; i++) {
                    if (droppedItemHolders.get(i).playerOwned)
                        playerInventory.storeItem(droppedItemHolders.get(i).droppedItem);
                }
            }
        }

        @Override
        public String toString(){
            String str = "\nOUTCOME\n";
            for(int i = 0; i < expHolders.size; i++){
                str += "\nReceiver : "+ expHolders.get(i).receiver.getName()+" => experience gained : "+ expHolders.get(i).experience;
            }
            for(int i = 0; i < droppedItemHolders.size; i++){
                str += "\nStolen item : "+ droppedItemHolders.get(i).toString();
            }
            return str+"\n";
        }
    }

    public static class ExperiencePointsHolder {
        public final IUnit receiver;
        public int experience;
        private Stack<int[]> statGained; // the 12 first entries are statistic inscreased while the last entry is experience gained before leveling up

        public ExperiencePointsHolder(IUnit receiver, int experience){
            this.receiver = receiver;
            this.experience = experience;
            this.statGained = new Stack<int[]>();
        }

        public boolean hasNext(){
            return statGained.size() > 0;
        }

        public int[] next(){
            return statGained.pop();
        }

        void solve(){
            statGained = receiver.addExpPoints(experience);
        }


        public boolean isIrrelevant() {
            return experience == 0 || receiver.isOutOfAction();
        }
    }


    public static class DroppedItemHolder {
        public Item droppedItem;
        public boolean playerOwned;

        public DroppedItemHolder(Item droppedItem, boolean playerOwned) {
            this.droppedItem = droppedItem;
            this.playerOwned = playerOwned;
        }
    }


}
