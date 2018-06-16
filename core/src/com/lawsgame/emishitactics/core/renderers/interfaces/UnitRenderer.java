package com.lawsgame.emishitactics.core.renderers.interfaces;

import com.lawsgame.emishitactics.core.constants.Props;
import com.lawsgame.emishitactics.core.models.Unit;

public abstract class UnitRenderer extends Renderer<Unit> {

    public UnitRenderer(Unit model) {
        super(model);
    }

    public abstract void setActionState(Props.ActionState actionState);
    public abstract void setX(float x);
    public abstract void setY(float y);
}
