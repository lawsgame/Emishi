package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Notification.SwitchPosition;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class SwitchPositionCommand extends BattleCommand {

    public SwitchPositionCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        super(bfr, Data.ActionChoice.SWITCH_POSITION, scheduler, true, false);
    }

    @Override
    protected void execute() {
        IUnit actor = battlefield.getUnit(rowActor, colActor);
        IUnit target = battlefield.getUnit(rowTarget, colTarget);
        battlefield.switchUnitPositions(rowActor, colActor, rowTarget, colTarget);
        scheduler.addTask(new Task(battlefieldRenderer, new SwitchPosition(actor, target, SwitchPosition.Mode.WALK, battlefield)));
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
