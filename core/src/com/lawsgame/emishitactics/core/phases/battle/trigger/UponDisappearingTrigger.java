package com.lawsgame.emishitactics.core.phases.battle.trigger;

import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.interfaces.Model;

public class UponDisappearingTrigger extends Model.Trigger {

    protected Unit wounded;

    public UponDisappearingTrigger(boolean once, Unit wounded) {
        super(once, true);
        this.wounded = wounded;
    }

    @Override
    public boolean isTriggered(Object data) {
        return wounded.isOutOfAction();
    }
}
