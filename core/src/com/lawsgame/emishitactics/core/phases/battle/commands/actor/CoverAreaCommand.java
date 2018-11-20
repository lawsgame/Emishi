package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class CoverAreaCommand extends SelfInflitedCommand {
    public CoverAreaCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, Data.ActionChoice.COVER_AREA, scheduler, playerInventory, true);
    }

    @Override
    protected void execute() {

    }

    @Override
    public boolean isInitiatorValid(int rowActor, int colActor, Unit initiator) {
        return super.isInitiatorValid(rowActor, colActor, initiator)
                && initiator.getCurrentWeapon().getTemplate().isRange()
                && !initiator.getCurrentWeapon().getTemplate().isMelee();
    }
}
