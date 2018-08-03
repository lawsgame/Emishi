package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class BuildCommand extends BattleCommand {

    public BuildCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler ) {
        super(bfr, Data.ActionChoice.BUILD, scheduler, false, false);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    protected void execute() {

    }

    @Override
    public boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        return false;
    }

    @Override
    public boolean atActionRange(int row, int col, IUnit actor) {
        return false;
    }
}
