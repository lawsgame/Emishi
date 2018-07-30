package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class TestCommand extends BattleCommand{
    protected boolean launched;

    public TestCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        super(bfr, Data.ActionChoice.TEST, scheduler, false, false);
    }

    @Override
    protected void execute() {

    }

    @Override
    public boolean isExecuting() {
        return false;
    }

    @Override
    public boolean isExecutionCompleted() {
        return false;
    }

    @Override
    public boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        return isTargetValid(rowActor0, colActor0, rowTarget0, colTarget0, TargetType.ALLY);
    }

    @Override
    public boolean atActionRange(int row, int col, IUnit actor) {
        return atActionRange(row, col, actor, TargetType.ALLY);
    }
}
