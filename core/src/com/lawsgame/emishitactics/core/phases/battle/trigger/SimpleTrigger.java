package com.lawsgame.emishitactics.core.phases.battle.trigger;

import com.lawsgame.emishitactics.core.models.interfaces.Trigger;

public class SimpleTrigger extends Trigger{

    public SimpleTrigger(boolean once) {
        super(once);
    }

    @Override
    public boolean isTriggered() {
        return true;
    }
}
