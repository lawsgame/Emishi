package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public abstract class EventCommand extends BattleCommand{
    public EventCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        super(bfr, scheduler);
    }

    @Override
    protected void unexecute() { }

    @Override
    public boolean isUndoable() {
        return false;
    }

}
