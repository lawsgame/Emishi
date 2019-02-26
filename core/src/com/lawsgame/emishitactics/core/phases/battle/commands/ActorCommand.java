package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.TacticsGame;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.RangedBasedType;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data.TileType;
import com.lawsgame.emishitactics.core.models.Formulas;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.utils.Lawgger;

import static com.lawsgame.emishitactics.core.models.Formulas.getCurrentWeaponRangeMin;
import static com.lawsgame.emishitactics.core.models.Formulas.getCurrentWeaponRangeMax;

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
 *      if(command.run()){
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
 *  command.setInitiator(...);
 *  command.setTarget(...);
 *  if(command.apply()){
 *
 *          // the command has been applied successfully
 *  }else{
 *
 *          // the command failed to be applied
 *  }
 *
 *
 *  II - battle command flow
 *
 *  1 - get the command
 *  2 - set initiator and target
 *  3 - (optional) display action pan
 *  4 - run the command
 *
 * no need to put back the command in the BCM
 *
 *
 * ActorCommand.run() method struture :
 *  0 - (optional) register the old model state to perform undo() if required
 *  1 - update the model
 *  2 - push the render task
 *  3 - set the outcome bundle
 *  4 - (optional) check for events
 */
public abstract class ActorCommand extends BattleCommand{
    private static Lawgger log = Lawgger.createInstance(ActorCommand.class);

    protected final ActionChoice choice;
    //does not set the actor.acted or .moved to true if the command is executed
    private boolean registerAction;
    // does not update the AP of the actor if the command is executed
    private boolean costless;

    private Unit initiator;
    private Unit target;
    protected int rowActor;
    protected int colActor;
    protected int rowTarget;
    protected int colTarget;


    public ActorCommand(BattlefieldRenderer bfr, ActionChoice choice, AnimationScheduler scheduler, Inventory playerInventory){
        super(bfr, scheduler, playerInventory);
        this.choice = choice;
        this.registerAction = true;
        this.costless = false;
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
        this.registerAction = true;
        if(super.apply()) {
            // set as moved or acted
            if (choice.isActedBased()) {
                getInitiator().setActed(registerAction);
            } else {
                getInitiator().setMoved(registerAction);
            }
            // clean and solve the outcome
            outcome.clean();
            outcome.resolve();
            debugCommand();
            return true;
        }
        return false;
    }


    private void debugCommand(){
        StringBuilder builder = new StringBuilder("\n             -----***$$ ACTION BEGIN $$***-----");
        builder.append("\nACTION REPORT of ");
        builder.append(initiator.getName());
        builder.append(" performing ");
        builder.append(choice.name());
        builder.append("\n");
        builder.append(isDecoupled() ? showTask() : scheduler.toString());
        builder.append("\n");
        builder.append(outcome);
        builder.append("\nEvent triggered while performing this command ? ");
        builder.append(isEventTriggered());
        builder.append("\n              ------***$$ ACTION END $$***------");
        log.info(builder.toString());
    }

    public final boolean apply(int rowActor, int colActor, int rowTarget, int colTarget){
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
            if(registerAction) {
                if (choice.isActedBased()) {
                    getInitiator().setActed(false);
                }else {
                    getInitiator().setMoved(false);
                }
            }

            return true;
        }
        return false;
    }

    protected void unexecute(){ }


    protected final boolean isAnyEventTriggerable(Object data){
        Array<int[]> area = getImpactArea();
        area.add(new int[]{rowActor, colActor});
        Utils.arrayRemoveIntTableClones(area);
        return isAnyEventTriggerable(data, area);
    }

    protected final void handleEvents(Object data){
        Array<int[]> area = getImpactArea();
        area.add(new int[]{rowActor, colActor});
        Utils.arrayRemoveIntTableClones(area);
        handleEvents(data, area);
    }

    public boolean isAppliableWihoutValidation(){
        return choice.isUndoable() && !initiator.isOutOfAction();
    }

    /**
     * {@link ActorCommand#isAppliableWihoutValidation()} and this method are almost the same except from taking
     * into account the value of {@link BattleCommand#isEventTriggered()} which is updated as late as possible just at the
     * beginning of the method {@link BattleCommand#apply()}. This fact required the existence of both those methods.
     *
     * @return true if can be undone
     */
    @Override
    public final boolean isUndoable() {
        return isAppliableWihoutValidation() && !isEventTriggered();
    }

    @Override
    public final boolean isApplicable() {
        return isInitiatorValid() && isTargetValid();
    }

    // called to checked initiator requirements AND that the actor DOES stand on the tile {rowActor, colActor}
    public  final boolean isInitiatorValid(){
        return isInitiatorValid(rowActor, colActor, initiator) && bfr.getModel().getUnit(rowActor, colActor) == initiator;
    }

    /**
     * intrinsec requirements checking
     */
    public boolean isInitiatorValid(int rowActor, int colActor, Unit initiator){

        boolean valid = false;
        if(initiator != null && bfr.getModel().isTileReachable(rowActor, colActor, initiator.has(Data.Ability.PATHFINDER))){
            if(!initiator.isOutOfAction()) {
                if (costless || choice.getCost(rowActor, colActor, initiator, bfr.getModel()) <= initiator.getActionPoints()) {
                    if (choice.isActedBased()) {
                        valid = (!registerAction || !initiator.isActed()) && !initiator.isDisabled();
                    } else {
                        valid = (!registerAction || !initiator.isMoved()) && !initiator.isCrippled();
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
    public final boolean isTargetValid() {
        if(isTargetValid(getInitiator(), rowActor, colActor, rowTarget, colTarget)){
            provideActionPanelInfos();
            return true;
        }
        return false;
    }

    protected void provideActionPanelInfos(){}

    /*
    required for testing retaliation availability for the attacked target without copy and paste the code of the
    ActorCommand.isTargetValid() method.
     */
    public abstract boolean isTargetValid(Unit actor, int rowActor0, int colActor0, int rowTarget0, int colTarget0);


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
    public final boolean atActionRange(int row, int col, Unit actor){
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
    public abstract Array<int[]> getTargetsAtRange(int row, int col, Unit actor);

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
                    Unit actor = bfr.getModel().getUnit(rowActor, colActor);
                    int rangeMin = getCurrentWeaponRangeMin(actor, rowActor, colActor, bfr.getModel());
                    int rangeMax = Formulas.getCurrentWeaponRangeMax(actor, rowActor, colActor, bfr.getModel());
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

    public Array<int[]> getImpactArea(int rowActor0, int colActor0, int rowTarget0, int colTarget0){
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
    public Array<int[]> getTargetsFromImpactArea(int rowActor0, int colActor0, int rowTarget0, int colTarget0, Unit actor){
        Array<int[]> impactArea = getImpactArea(rowActor0, colActor0, rowTarget0, colTarget0);
        Array<int[]> targets = getTargetsAtRange(rowActor0, colActor0, actor);
        return Utils.arrayGetElementsInBothOnly(impactArea, targets);
     }

    public final Array<int[]> getTargetsFromImpactArea(){
        return getTargetsFromImpactArea(rowActor, colActor, rowTarget, colTarget, initiator);
    }



    //------------------- HELPER METHODS -----------------------------

    protected final boolean isTargetAllyValid(Unit initiator, int rowActor0, int colActor0, int rowTarget0, int colTarget0, boolean woundedRequired, boolean targetMustBeAbleToMove){
        boolean valid = false;
        if(initiator != null && choice.getRangedType() ==  Data.RangedBasedType.WEAPON || choice.getRangedType() == Data.RangedBasedType.SPECIFIC){

            int rangeMin = (choice.getRangedType() == Data.RangedBasedType.WEAPON) ? getCurrentWeaponRangeMin(initiator, rowActor0, colActor0, bfr.getModel()) : choice.getRangeMin();
            int rangeMax = (choice.getRangedType() == Data.RangedBasedType.WEAPON) ? getCurrentWeaponRangeMax(initiator, rowActor0, colActor0, bfr.getModel()) : choice.getRangeMax();
            int dist = Utils.dist(rowActor0, colActor0, rowTarget0, colTarget0);
            if (rangeMin <= dist && dist <= rangeMax) {

                Array<int[]> impactArea = getImpactArea(rowActor0, colActor0, rowTarget0, colTarget0);
                for(int i = 0; i < impactArea.size; i++) {

                    if(bfr.getModel().isTileOccupiedByAlly(impactArea.get(i)[0], impactArea.get(i)[1], initiator)
                            && (!woundedRequired || bfr.getModel().getUnit(impactArea.get(i)[0], impactArea.get(i)[1]).isWounded(true, true))
                            && (!targetMustBeAbleToMove || !initiator.isCrippled())) {
                        valid = true;
                    }
                }
            }
        }
        return valid;
    }

    protected final boolean isEnemyTargetValid(Unit initiator, int rowActor0, int colActor0, int rowTarget0, int colTarget0, boolean stealableRequired){
        boolean valid = false;
        if(initiator != null && choice.getRangedType() ==  RangedBasedType.WEAPON || choice.getRangedType() == RangedBasedType.SPECIFIC){

            int rangeMin = (choice.getRangedType() == RangedBasedType.WEAPON) ? getCurrentWeaponRangeMin(initiator, rowActor0, colActor0, bfr.getModel()) : choice.getRangeMin();
            int rangeMax = (choice.getRangedType() == RangedBasedType.WEAPON) ? getCurrentWeaponRangeMax(initiator, rowActor0, colActor0, bfr.getModel()) : choice.getRangeMax();
            int dist = Utils.dist(rowActor0, colActor0, rowTarget0, colTarget0);
            if (rangeMin <= dist && dist <= rangeMax) {

                Array<int[]> impactArea = getImpactArea(rowActor0, colActor0, rowTarget0, colTarget0);
                for(int i = 0; i < impactArea.size; i++) {

                    if (bfr.getModel().isTileOccupiedByFoe(impactArea.get(i)[0], impactArea.get(i)[1], initiator.getArmy().getAffiliation())
                            && (!stealableRequired || bfr.getModel().getUnit(impactArea.get(i)[0], impactArea.get(i)[1]).isStealable())) {
                        valid = true;
                    }
                }
            }
        }
        return valid;
    }

    protected final Array<int[]> getAlliesAtRange(int row, int col, Unit initiator, boolean woundedRequired, boolean targetMustBeAbleToMove){
        Array<int[]>  targetsAtRange = new Array<int[]>();
        if(initiator != null && choice.getRangedType() ==  RangedBasedType.WEAPON || choice.getRangedType() == RangedBasedType.SPECIFIC) {

            int rangeMin = (choice.getRangedType() == RangedBasedType.WEAPON) ? getCurrentWeaponRangeMin(initiator, row, col, bfr.getModel()) : choice.getRangeMin();
            int rangeMax = (choice.getRangedType() == RangedBasedType.WEAPON) ? getCurrentWeaponRangeMax(initiator, row, col, bfr.getModel()) : choice.getRangeMax();
            int dist;
            for (int r = row - rangeMin; r <= row + rangeMax; r++) {
                for (int c = col - rangeMin; c <= col + rangeMax; c++) {

                    dist = Utils.dist(row, col, r, c);
                    if(rangeMin <= dist && dist <= rangeMax){
                        targetsAtRange.addAll(getTargetedAllies(row, col, r, c, initiator, woundedRequired, targetMustBeAbleToMove));
                    }
                }
            }

        }
        return Utils.arrayRemoveIntTableClones(targetsAtRange);
    }

    protected final Array<int[]> getFoesAtRange(int row, int col, Unit initiator, boolean stealableRequired){
        Array<int[]>  targetsAtRange = new Array<int[]>();
        if(initiator != null && choice.getRangedType() ==  RangedBasedType.WEAPON || choice.getRangedType() == RangedBasedType.SPECIFIC) {

            int rangeMin = (choice.getRangedType() == RangedBasedType.WEAPON) ? getCurrentWeaponRangeMin(initiator, row, col, bfr.getModel()) : choice.getRangeMin();
            int rangeMax = (choice.getRangedType() == RangedBasedType.WEAPON) ? getCurrentWeaponRangeMax(initiator, row, col, bfr.getModel()) : choice.getRangeMax();
            int dist;
            for (int r = row - rangeMin; r <= row + rangeMax; r++) {
                for (int c = col - rangeMin; c <= col + rangeMax; c++) {

                    dist = Utils.dist(row, col, r, c);
                    if(rangeMin <= dist && dist <= rangeMax){
                        targetsAtRange.addAll(getTargetedFoes(row, col, r, c, initiator, stealableRequired));
                    }
                }
            }
        }

        return Utils.arrayRemoveIntTableClones(targetsAtRange);
    }


    protected final Array<int[]> getTargetedAllies(int rowActor, int colActor, int rowTarget, int colTarget, Unit initiator, boolean woundedRequired, boolean targetMustBeAbleToMove){
        Array<int[]> targets = getImpactArea(rowActor, colActor, rowTarget, colTarget);
        if(initiator == null){
            targets.clear();
        }else {
            for (int i = 0; i < targets.size; i++) {
                if (!bfr.getModel().isTileOccupiedByAlly(targets.get(i)[0], targets.get(i)[1], initiator)
                        || (woundedRequired && !bfr.getModel().getUnit(targets.get(i)[0], targets.get(i)[1]).isWounded(true, true))
                        || (targetMustBeAbleToMove && initiator.isCrippled())) {

                    targets.removeIndex(i);
                    i--;
                }
            }
        }
        return targets;
    }

    protected final Array<int[]> getTargetedFoes(int rowActor, int colActor, int rowTarget, int colTarget, Unit initiator, boolean stealableRequired){
        Array<int[]> targets = getImpactArea(rowActor, colActor, rowTarget, colTarget);
        if(initiator == null) {
            targets.clear();
        }else {
            for (int i = 0; i < targets.size; i++) {
                if (!bfr.getModel().isTileOccupiedByFoe(targets.get(i)[0], targets.get(i)[1], initiator.getArmy().getAffiliation())
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

    public final Unit getInitiator(){
        return initiator;
    }

    public final int getRowinitiator() {
        return rowActor;
    }

    public final int getColInitiator() {
        return colActor;
    }

    public final Unit getTarget(){
        return target;
    }

    public final int getRowTarget() { return rowTarget; }

    public final int getColTarget() { return colTarget; }

    public final TileType getTargetTile() {
        return bfr.getModel().getTile(rowTarget, colTarget).getType();
    }

    public final Battlefield getBattlefield() {
        return bfr.getModel();
    }

    public final void  setRegisterAction(boolean registerAction) {
        this.registerAction = registerAction;
    }

    public final void setCostless(boolean costless) {
        this.costless = costless;
    }

    public final void setFree(boolean free) {
        setCostless(free);
        setRegisterAction(!free);
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

}
