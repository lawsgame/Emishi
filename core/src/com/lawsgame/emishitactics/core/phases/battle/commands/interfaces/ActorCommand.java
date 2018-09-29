package com.lawsgame.emishitactics.core.phases.battle.commands.interfaces;

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

import java.util.Arrays;

/**
 *
 * I - Battle command usage
 *
 *  command.update(dt);
 *
 *  ActionChoice choice = ...;
 *  if(bcm.canActionBePerformed(...){
 *      ActorCommand command = bcm.get(...); /OR new XCommand(...);
 *      if(command != null && command.setInitiator(...)){
 *          command.setTarget(...);
 *          if(command.isTargetValid()){
 *              command.apply();
 *          }
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
    private boolean free;                           // command that does not count as the player choice i.e. setTiles acted and moved as true while being applied nor it costs any OA point

    private IUnit initiator;
    private IUnit target;
    protected int rowActor;
    protected int colActor;
    protected int rowTarget;
    protected int colTarget;

    protected EncounterOutcome outcome;

    private boolean initialized;


    public ActorCommand(BattlefieldRenderer bfr, ActionChoice choice, AnimationScheduler scheduler, Inventory playerInventory, boolean free){
        super(bfr, scheduler);
        this.choice = choice;
        this.rowActor = -1;
        this.colActor = -1;
        this.rowTarget = -1;
        this.colTarget = -1;
        this.outcome = new EncounterOutcome(playerInventory);
        this.free = free;
        this.initialized = false;
    }


    @Override
    public final void apply() {
        if(isTargetValid()) {


            // Outcome and animation scheduler cleared.
            this.outcome.reset();

            /*
             * 1) set as moved or acted if required
             * 2) actor pays the cost of performing this action
             */
            if(!free){

                if(choice.isActedBased()) {
                    getInitiator().setActed(true);
                }else {
                    getInitiator().setMoved( true);
                }

                getInitiator().addActionPoints(-choice.getCost());
            }


            super.apply();

            outcome.clean();
            outcome.resolve();

        }
    }

    public final void apply(int rowActor, int colActor, int rowTarget, int colTarget){
        if(setInitiator(rowActor, colActor)) {
            setTarget(rowTarget, colTarget);
            if (isTargetValid()) {
                apply();
            }
        }
    }

    public final void apply(int rowActor, int colActor){
        apply(rowActor, colActor, rowActor, colActor);
    }

    @Override
    public final void undo(){
        if(!free){

            if (choice.isActedBased())
                getInitiator().setActed(false);
            else
                getInitiator().setMoved( false);

            getInitiator().addActionPoints(choice.getCost());
        }
        unexecute();
    }







    protected void unexecute(){ }


    // called to checked initiator requirements
    public boolean canbePerformedBy(IUnit actor){
        return choice.getCost() <= actor.getActionPoints()
                && (choice.isActedBased() ? !actor.hasActed() : !actor.hasMoved() || free);
    }

    /**
     * PLAYER ORIENTED METHOD
     *
     * TARGET CHECKING
     * @return whether or not THIS SPECIFIC TARGET is at range by the initiator performing the given action if one's is standing the buildingType (rowActor, colActor)
     * while ignoring the initiator's history and the unit other requirements to actually perform this action, namely : weapon/item and ability requirements.
     */
    public final boolean isTargetValid() {
        boolean valid = false;
        if(isTargetValid(rowActor, colActor, rowTarget, colTarget)){
            valid = true;
            if(!initialized) {
                init();
            }
        }else{
            initialized = false;
        }
        return valid;
    }

    /**
     * especially required to build attributes values required for instanciating the associated ActionInfoPanel
     */
    protected void init(){
        initialized = true;
    }


        /*
    required for testing retaliation availability for the attacked target without copy and paste the code of the
    ActorCommand.isTargetValid() method.
     */
    public abstract boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0);


    public void blink(final boolean enable){
        if(battlefield.isTileOccupied(rowTarget, colTarget)){

            final BattleUnitRenderer targetRenderer = bfr.getUnitRenderer(battlefield.getUnit(rowTarget, colTarget));
            StandardTask blinkTask = new StandardTask(targetRenderer, Notification.Blink.get(enable));
            blinkTask.tag("blink ("+enable+")");
            scheduleRenderTask(blinkTask);

        }
    }




    /**
     * AI ORIENTED METHOD
     *
     * check the third requirements to perform an action
     * - target availability requirements
     *
     * @param row
     * @param col
     * @return whether or not ANY TARGET is at range by the initiator performing the given action if one's is standing the buildingType (row, col)
     * while ignoring the initiator's history and the unit other requirements to actually perform this action, namely : weapon/item and ability requirements.
     */
    public abstract boolean atActionRange(int row, int col, IUnit actor);

    public final boolean atActionRange(){
        if(battlefield.isTileOccupied(rowActor, colActor))
            return atActionRange(rowActor, colActor, battlefield.getUnit(rowActor, colActor));
        return false;
    }

    /**
     * AI oriented method
     *

    public abstract boolean getAvailablesTarget(int row, int col, IUnit actor);

    public final boolean atActionRange(){
        if(battlefield.isTileOccupied(rowActor, colActor))
            return atActionRange(rowActor, colActor, battlefield.getUnit(rowActor, colActor));
        return false;
    }
     */


    /**
     *
     * @return fetch all tiles where the given unit is at range to perform the chosen action
     */
    public Array<int[]> getActionArea() {
        Array<int[]> actionArea = new Array<int[]>();
        if(battlefield.isTileOccupied(rowActor, colActor)) {
            switch (choice.getRangedType()) {
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
     * @return the relevantly oriented impact area of an action performed by an initiator while targeting the buildingType {rowTarget, colTarget}
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
    public void redo() { }



    //------------------- HELPER METHODS -----------------------------

    protected final boolean isTargetAllyValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0, boolean woundedRequired){
        boolean valid = false;
        if(battlefield.isTileOccupied(rowActor0, colActor0)
                && (choice.getRangedType() ==  Data.RangedBasedType.WEAPON || choice.getRangedType() == Data.RangedBasedType.SPECIFIC)){
            IUnit actor = battlefield.getUnit(rowActor0, colActor0);
            int rangeMin = (choice.getRangedType() == Data.RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMin(rowActor0, colActor0, battlefield) : choice.getRangeMin();
            int rangeMax = (choice.getRangedType() == Data.RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMax(rowActor0, colActor0, battlefield) : choice.getRangeMax();
            int dist = Utils.dist(rowActor0, colActor0, rowTarget0, colTarget0);
            if (rangeMin <= dist && dist <= rangeMax) {
                if(battlefield.isTileOccupiedByAlly(rowTarget0, colTarget0, actor.getArmy().getAffiliation())
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
                && (choice.getRangedType() ==  RangedBasedType.WEAPON || choice.getRangedType() == RangedBasedType.SPECIFIC)){
            IUnit actor = battlefield.getUnit(rowActor0, colActor0);
            int rangeMin = (choice.getRangedType() == RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMin(rowActor0, colActor0, battlefield) : choice.getRangeMin();
            int rangeMax = (choice.getRangedType() == RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMax(rowActor0, colActor0, battlefield) : choice.getRangeMax();
            int dist = Utils.dist(rowActor0, colActor0, rowTarget0, colTarget0);
            if (rangeMin <= dist && dist <= rangeMax) {
                if(battlefield.isTileOccupiedByFoe(rowTarget0, colTarget0, actor.getArmy().getAffiliation())
                        && (!stealableRequired || battlefield.getUnit(rowTarget0, colTarget0).isStealable())) {
                    valid = true;
                }
            }
        }
        return valid;
    }

    protected final boolean isAllyAtActionRange(int row, int col, IUnit actor, boolean woundedRequired){
        boolean targetAtRange = false;
        if(choice.getRangedType() ==  RangedBasedType.WEAPON || choice.getRangedType() == RangedBasedType.SPECIFIC) {
            int[] unitPos = battlefield.getUnitPos(actor);
            int rangeMin = (choice.getRangedType() == RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMin(unitPos[0], unitPos[1], battlefield) : choice.getRangeMin();
            int rangeMax = (choice.getRangedType() == RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMax(unitPos[0], unitPos[1], battlefield) : choice.getRangeMax();
            int dist;
            loop:
            {
                for (int r = row - rangeMin; r <= row + rangeMax; r++) {
                    for (int c = col - rangeMin; c <= col + rangeMax; c++) {
                        dist = Utils.dist(row, col, r, c);
                        if (rangeMin <= dist && dist <= rangeMax
                                && battlefield.isTileOccupiedByAlly(r, c, actor.getArmy().getAffiliation())
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
        if(choice.getRangedType() ==  RangedBasedType.WEAPON || choice.getRangedType() == RangedBasedType.SPECIFIC) {
            int[] unitPos = battlefield.getUnitPos(actor);
            int rangeMin = (choice.getRangedType() == RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMin(unitPos[0], unitPos[1], battlefield) : choice.getRangeMin();
            int rangeMax = (choice.getRangedType() == RangedBasedType.WEAPON) ? actor.getCurrentWeaponRangeMax(unitPos[0], unitPos[1], battlefield) : choice.getRangeMax();
            int dist;
            loop:
            {
                for (int r = row - rangeMin; r <= row + rangeMax; r++) {
                    for (int c = col - rangeMin; c <= col + rangeMax; c++) {
                        dist = Utils.dist(row, col, r, c);
                        if (rangeMin <= dist
                                && dist <= rangeMax
                                && battlefield.isTileOccupiedByFoe(r, c, actor.getArmy().getAffiliation())
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
     * @return get all possible target buildingType knowing that the given buildingType is within the impact area
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
        return bundle.get(choice.name());
    }

    public final ActionChoice getActionChoice() {
        return choice;
    }


    public final boolean setInitiator(int rowActor, int colActor) {
        if(battlefield.isTileOccupied(rowActor, colActor)) {
            this.initialized = false;
            this.rowActor = rowActor;
            this.colActor = colActor;
            this.initiator = battlefield.getUnit(rowActor, colActor);
            if(choice.isActorIsTarget()){
                setTarget(rowActor, colActor);
            }
            return true;
        }
        return false;
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


    public final void setTarget(int rowTarget, int colTarget){
        if(battlefield.isTileExisted(rowTarget, colTarget)){
            this.initialized = false;
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
        public Array<ExperiencePointsHolder> expHolders;
        public Array<DroppedItemHolder> droppedItemHolders;
        public Inventory playerInventory;
        boolean resolved;

        public EncounterOutcome(Inventory playerInventory) {
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

        public boolean isExperienceShown(){
            return expHolders.size == 0;
        }

        public boolean isLootedItemsClaimed(){ return droppedItemHolders.size == 0; }

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
                        expHolders.get(i).experience += expHolders.get(j).experience;
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
        public IUnit receiver;
        public int experience;
        int[] statGained; // the 12 first entries are statistic inscreased while the last entry is a boolean int : 1 == levelup / 0 == not

        public ExperiencePointsHolder(IUnit receiver, int experience){
            this.receiver = receiver;
            this.experience = experience;
            this.statGained = new int[10];
        }

        void solve(){
            statGained = receiver.addExpPoints(experience);
        }

        public int[] getStatGained() {
            return Arrays.copyOfRange(statGained, 0, 12);
        }

        public boolean isReceiverLevelup() {
            return statGained[12] == 1;
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

    @Override
    public String toString() {
        return getActionChoice().name();
    }


}
