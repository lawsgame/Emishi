package com.lawsgame.emishitactics.core.phases.battle.commands.interfaces;

import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public abstract class SelfInflitedCommand extends BattleCommand{

    public SelfInflitedCommand(BattlefieldRenderer bfr, ActionChoice choice, AnimationScheduler scheduler, boolean free) {
        super(bfr, choice, scheduler, free);
    }

    @Override
    public boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        boolean valid = false;
        if(battlefield.isTileOccupied(rowActor0, colActor0)
                && rowActor0 == rowTarget0
                && colActor0 == colTarget0){
            valid = true;
        }
        return valid;
    }

    @Override
    public boolean atActionRange(int row, int col, IUnit actor) {
        return true;
    }

}
