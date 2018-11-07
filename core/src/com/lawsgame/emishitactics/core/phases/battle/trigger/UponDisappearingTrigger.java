package com.lawsgame.emishitactics.core.phases.battle.trigger;

import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Model;

public class UponDisappearingTrigger extends Model.Trigger {

    protected IUnit wounded;

    public UponDisappearingTrigger(boolean once, IUnit wounded) {
        super(once);
        this.wounded = wounded;
    }

    @Override
    public boolean isTriggered(Object data) {
        return wounded.isOutOfAction();
    }
}
