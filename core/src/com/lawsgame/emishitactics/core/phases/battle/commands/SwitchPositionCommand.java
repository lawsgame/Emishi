package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask.RendererThread;
import com.lawsgame.emishitactics.core.models.ActionChoice;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Notification.SwitchPosition;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class SwitchPositionCommand extends BattleCommand {
    private BattleUnitRenderer actorRenderer;
    private BattleUnitRenderer targetRenderer;

    public SwitchPositionCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        super(bfr, ActionChoice.SWITCH_POSITION, scheduler, true, false, true);
    }

    @Override
    public void init() {
        super.init();
        actorRenderer = battlefieldRenderer.getUnitRenderer(battlefield.getUnit(rowActor, colActor));
        targetRenderer = battlefieldRenderer.getUnitRenderer(battlefield.getUnit(rowTarget, colTarget));
    }

    @Override
    protected void execute() {
        //update model
        IUnit actor = battlefield.getUnit(rowActor, colActor);
        IUnit target = battlefield.getUnit(rowTarget, colTarget);
        battlefield.switchUnitPositions(rowActor, colActor, rowTarget, colTarget);

        // push render task
        scheduleRenderTask(new StandardTask(battlefieldRenderer, new SwitchPosition(actor, target, SwitchPosition.Mode.WALK, battlefield)));
    }

    @Override
    public void undo() {
        if(battlefield.isTileOccupied(rowActor, colActor)
                && targetRenderer.getModel() != battlefield.getUnit(rowActor, colActor)
                && battlefield.isTileOccupied(rowTarget, colTarget)
                && actorRenderer.getModel() != battlefield.getUnit(rowTarget, colTarget)){
            IUnit actor = battlefield.getUnit(rowActor, colActor);
            IUnit target = battlefield.getUnit(rowTarget, colTarget);
            battlefield.switchUnitPositions(rowActor, colActor, rowTarget, colTarget);

            StandardTask task = new StandardTask();
            RendererThread actorRendererThread = new RendererThread(battlefieldRenderer, new Notification.SetUnit(rowActor, colActor, actor));
            RendererThread targetRendererThread = new RendererThread(battlefieldRenderer, new Notification.SetUnit(rowTarget, colTarget, target));
            task.addThread(actorRendererThread);
            task.addThread(targetRendererThread);
            scheduleRenderTask(task);
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
