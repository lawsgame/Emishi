package com.lawsgame.emishitactics.core.phases.battle.commands.event;

import com.lawsgame.emishitactics.core.phases.battle.commands.EventCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class MessageEvent extends EventCommand{
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
    public boolean isApplicable() {
        return true;
    }
}
