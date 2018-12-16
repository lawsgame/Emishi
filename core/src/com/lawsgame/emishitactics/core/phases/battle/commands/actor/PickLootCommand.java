package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class PickLootCommand extends SelfInflitedCommand {


    public PickLootCommand(BattlefieldRenderer bfr, Data.ActionChoice choice, AnimationScheduler scheduler, Inventory playerInventory, boolean free) {
        super(bfr, choice, scheduler, playerInventory, free);
    }

    @Override
    public boolean isTargetValid(Unit initiator, int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        return super.isTargetValid(initiator, rowActor0, colActor0, rowTarget0, colTarget0) && bfr.getModel().isTileLooted(rowTarget0, colTarget0);
    }

    @Override
    protected void execute() {

    }
}
