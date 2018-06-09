package com.lawsgame.emishitactics.core.renderers.interfaces;

import com.lawsgame.emishitactics.core.constants.Props;
import com.lawsgame.emishitactics.core.models.Unit;

public abstract class AUnitRenderer extends ARenderer<Unit> {

    protected Props.ActionState actionState = Props.ActionState.REST;

    public AUnitRenderer(Unit model) {
        super(model);
    }

    public Props.ActionState getActionState() {
        return actionState;
    }

    public void setActionState(Props.ActionState actionState) {
        this.actionState = actionState;
    }
}
