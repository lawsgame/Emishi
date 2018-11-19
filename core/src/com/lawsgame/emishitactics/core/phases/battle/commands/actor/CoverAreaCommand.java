package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class CoverAreaCommand extends ActorCommand {
    public CoverAreaCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, Data.ActionChoice.COVER_AREA, scheduler, playerInventory, true);
    }

    @Override
    public boolean isTargetValid(Unit actor, int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        return false;
    }

    @Override
    public Array<int[]> getTargetsAtRange(int row, int col, Unit actor) {
        return null;
    }

    @Override
    protected void execute() {

    }
}
