package com.lawsgame.emishitactics.core.phases.battle.commands.event;

import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.EventCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class MessageEvent extends BattleCommand{
    protected String message;

    public MessageEvent(BattlefieldRenderer bfr, AnimationScheduler scheduler, String message) {
        super(bfr, scheduler);
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
