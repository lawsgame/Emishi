package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification.SwitchPosition;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class SwitchPositionCommand extends BattleCommand {
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
        actorRenderer = battlefieldRenderer.getUnitRenderer(getInitiator());
        targetRenderer = battlefieldRenderer.getUnitRenderer(getTarget());
        oldActorOrientation = getInitiator().getOrientation();
        oldTargetOrientation = getTarget().getOrientation();

        //update model
        battlefield.switchUnitPositions(rowActor, colActor, rowTarget, colTarget);

        // push render task
        scheduleRenderTask(new StandardTask(battlefieldRenderer, new SwitchPosition(getInitiator(), getTarget(), SwitchPosition.Mode.WALK, battlefield)));
    }

    @Override
    public void unexecute() {
        if(battlefield.isTileOccupied(rowActor, colActor)
                && targetRenderer.getModel() == battlefield.getUnit(rowActor, colActor)
                && battlefield.isTileOccupied(rowTarget, colTarget)
                && actorRenderer.getModel() == battlefield.getUnit(rowTarget, colTarget)
                && oldTargetOrientation != null
                && oldActorOrientation != null){

            IUnit actor = battlefield.getUnit(rowActor, colActor);
            IUnit target = battlefield.getUnit(rowTarget, colTarget);
            actor.setOrientation(oldActorOrientation);
            target.setOrientation(oldTargetOrientation);
            battlefield.switchUnitPositions(rowActor, colActor, rowTarget, colTarget);
            scheduleRenderTask(new StandardTask(battlefieldRenderer, new SwitchPosition(actor, target, SwitchPosition.Mode.INSTANT, battlefield)));
            scheduleRenderTask(new StandardTask(targetRenderer, oldTargetOrientation));
            scheduleRenderTask(new StandardTask(actorRenderer, oldActorOrientation));
        }
    }

    @Override
    public boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        return isTargetAllyValid(rowActor0, colActor0, rowTarget0, colTarget0, false);
    }

    @Override
    public boolean atActionRange(int row, int col, IUnit actor) {
        return isAllyAtActionRange(row, col, actor, false);
    }
}
