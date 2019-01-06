package com.lawsgame.emishitactics.core.phases.battle.commands.actor;


import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification.StepOn;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.atomic.MoveCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;



public class WalkCommand extends ActorCommand {
    protected Array<int[]> validPath;
    protected MoveCommand moveCommand;

    public WalkCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, ActionChoice.WALK, scheduler, playerInventory);
    }

    @Override
    protected void execute() {

        Array<int[]> subpath = new Array<int[]>();
        int [] previousPos = new int[]{getRowinitiator(), getColInitiator()};
        for(int i = 0; i < validPath.size; i++){
            subpath.add(validPath.get(i));
            StepOn stepOn = new StepOn(previousPos[0], previousPos[1], subpath.peek()[0], subpath.peek()[1], getInitiator());
            previousPos[0] = subpath.peek()[0];
            previousPos[1] = subpath.peek()[1];
            if(isAnyEventTriggerable(stepOn, subpath.peek()[0], subpath.peek()[1])){
                moveCommand = new MoveCommand(bfr, scheduler, getOutcome().playerInventory, subpath, false);
                moveCommand.setFree(true);
                moveCommand.setDecoupled(true);
                if(moveCommand.apply(rowActor, colActor)) {
                    scheduleMultipleRenderTasks(moveCommand.confiscateTasks());
                    // perform event
                    handleEvents(stepOn, subpath.peek()[0], subpath.peek()[1]);
                    // keep walking if possible
                    WalkCommand walkCommand = new WalkCommand(bfr, scheduler, outcome.playerInventory);
                    walkCommand.setInitiator(subpath.peek()[0], subpath.peek()[1]);
                    if(walkCommand.isInitiatorValid()){
                        walkCommand.setFree(true);
                        walkCommand.setDecoupled(true);
                        walkCommand.setTarget(validPath.peek()[0], validPath.peek()[1]);
                        if(walkCommand.apply()){
                            scheduleMultipleRenderTasks(walkCommand.confiscateTasks());
                            outcome.merge(moveCommand.getOutcome());
                        }
                    }
                    break;
                }
            }
        }
        //System.out.println("    Is move without event ok (no event triggered)? "+!isEventTriggered());
        if(!isEventTriggered()){
            moveCommand = new MoveCommand(bfr, scheduler, getOutcome().playerInventory, subpath, false);
            moveCommand.setFree(true);
            moveCommand.setDecoupled(true);
            if(moveCommand.apply(rowActor, colActor)) {
                scheduleMultipleRenderTasks(moveCommand.confiscateTasks());
                outcome.merge(moveCommand.getOutcome());
            }
        }
    }

    /**
     *
     * addExpGained up the validPath if the buildingType targeted is a valid choice to move to
     *
     * @param rowActor0
     * @param colActor0
     * @param rowTarget0
     * @param colTarget0
     * @return true if the target buildingType if a valid buildingType to move to.
     */
    @Override
    public boolean isTargetValid(Unit initiator, int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        boolean valid = false;

        if(initiator != null && initiator.belongToAnArmy()) {
            this.validPath = bfr.getModel().getShortestPath(rowActor0, colActor0, rowTarget0, colTarget0, getInitiator().has(Data.Ability.PATHFINDER), getInitiator().getArmy().getAffiliation(), true);
            if (validPath.size > 0 && validPath.size <= getInitiator().getAppStat(Data.UnitStat.MOBILITY)) {
                valid = true;
            }else{
                this.validPath = bfr.getModel().getShortestPath(rowActor0, colActor0, rowTarget0, colTarget0, getInitiator().has(Data.Ability.PATHFINDER), getInitiator().getArmy().getAffiliation(), false);
                valid = validPath.size > 0 && validPath.size <= getInitiator().getAppStat(Data.UnitStat.MOBILITY);
            }
        }
        return valid;
    }

    @Override
    public Array<int[]> getTargetsAtRange(int row, int col, Unit actor) {
        return bfr.getModel().getMoveArea(row, col, actor);
    }

    @Override
    protected void unexecute() {
        moveCommand.undo();
        scheduleMultipleRenderTasks(moveCommand.confiscateTasks());
    }



}
