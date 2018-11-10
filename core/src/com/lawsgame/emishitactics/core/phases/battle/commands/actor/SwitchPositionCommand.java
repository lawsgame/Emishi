package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Tile;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class SwitchPositionCommand extends ActorCommand {
    private BattleUnitRenderer actorRenderer;
    private BattleUnitRenderer targetRenderer;
    private Data.Orientation oldTargetOrientation;
    private Data.Orientation oldActorOrientation;

    public SwitchPositionCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, ActionChoice.SWITCH_POSITION, scheduler, playerInventory, true);
    }

    @Override
    protected void execute() {

        // store old state
        actorRenderer = bfr.getUnitRenderer(getInitiator());
        targetRenderer = bfr.getUnitRenderer(getTarget());
        oldActorOrientation = getInitiator().getOrientation();
        oldTargetOrientation = getTarget().getOrientation();

        //update model
        bfr.getModel().switchUnitPositions(rowActor, colActor, rowTarget, colTarget);
        actorRenderer.getModel().setOrientation(Utils.getOrientationFromCoords(rowActor, colActor, rowTarget, colTarget));
        targetRenderer.getModel().setOrientation(Utils.getOrientationFromCoords(rowTarget, colTarget, rowActor, colActor));

        // push render task

        StandardTask task = new StandardTask();
        Array<int[]> path = new Array<int[]>();
        path.add(new int[]{rowTarget, colTarget});
        task.addThread(new StandardTask.RendererThread(bfr.getUnitRenderer(getInitiator()), bfr.getModel(), new Notification.Walk(getInitiator(), path, false)));
        path = new Array<int[]>();
        path.add(new int[]{rowActor, colActor});
        task.addThread(new StandardTask.RendererThread(bfr.getUnitRenderer(getTarget()), bfr.getModel(), new Notification.Walk(getTarget(), path, false)));
        scheduleRenderTask(task);


        // handle event

        Notification.StepOn stepOn = new Notification.StepOn(rowTarget, colTarget, getInitiator());
        if(isAnyEventTriggerable(stepOn, rowTarget, colTarget)){
            this.eventTriggered = true;
            handleEvents(stepOn, rowTarget, colTarget );
        }
        stepOn = new Notification.StepOn(rowActor, colActor, getTarget());
        if(isAnyEventTriggerable(stepOn, rowActor, colActor)){
            this.eventTriggered = true;
           handleEvents(stepOn, rowActor, colActor);
        }

    }

    @Override
    public boolean isInitiatorValid(IUnit initiator) {
        return super.isInitiatorValid(initiator) && !initiator.isCrippled();
    }

    @Override
    public void unexecute() {
        if(bfr.getModel().isTileOccupied(rowActor, colActor)
                && targetRenderer.getModel() == bfr.getModel().getUnit(rowActor, colActor)
                && bfr.getModel().isTileOccupied(rowTarget, colTarget)
                && actorRenderer.getModel() == bfr.getModel().getUnit(rowTarget, colTarget)
                && oldTargetOrientation != null
                && oldActorOrientation != null){

            IUnit actor = bfr.getModel().getUnit(rowActor, colActor);
            IUnit target = bfr.getModel().getUnit(rowTarget, colTarget);
            actor.setOrientation(oldActorOrientation);
            target.setOrientation(oldTargetOrientation);
            bfr.getModel().switchUnitPositions(rowActor, colActor, rowTarget, colTarget);
            scheduleRenderTask(new StandardTask(bfr.getModel(), actorRenderer, new Notification.SetUnit(rowActor, colActor, getInitiator())));
            scheduleRenderTask(new StandardTask(bfr.getModel(), targetRenderer, new Notification.SetUnit(rowTarget, colTarget, getTarget())));
            scheduleRenderTask(new StandardTask(targetRenderer, oldTargetOrientation));
            scheduleRenderTask(new StandardTask(actorRenderer, oldActorOrientation));
        }
    }

    @Override
    public boolean isTargetValid(IUnit initiator, int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        return isTargetAllyValid(initiator, rowActor0, colActor0, rowTarget0, colTarget0, false, true);
    }

    @Override
    public Array<int[]> getTargetsAtRange(int row, int col, IUnit actor) {
        return getAlliesAtRange(row, col, actor, false, true);
    }



}
