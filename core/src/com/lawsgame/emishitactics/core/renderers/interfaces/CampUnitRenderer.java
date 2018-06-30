package com.lawsgame.emishitactics.core.renderers.interfaces;

import com.lawsgame.emishitactics.core.models.Unit;

public abstract class CampUnitRenderer extends UnitRenderer{

    public CampUnitRenderer(Unit model) {
        super(model);
        model.getArmy().attach(this);
        model.getBanner().attach(this);
    }

    @Override
    public void dispose(){
        super.dispose();
        this.model.getBanner().detach(this);
        this.model.getArmy().detach(this);
    }
}
