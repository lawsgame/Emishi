package com.lawsgame.emishitactics.core.phases.battle.commands.event;

import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class MessageEvent extends BattleCommand{
    protected String message;

    public MessageEvent(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, String message) {
        super(bfr, scheduler, playerInventory);
        this.message = message;
    }

    @Override
    protected void execute() {
        System.out.println(message);
    }

    @Override
    protected void unexecute() {

    }

    @Override
    public boolean isApplicable() {
        return true;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }
}
