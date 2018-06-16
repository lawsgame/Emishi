package com.lawsgame.emishitactics.core.renderers.interfaces;

import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Unit;

public abstract class BattlefieldRenderer extends Renderer<Battlefield> {

    public BattlefieldRenderer(Battlefield model) {
        super(model);
    }
    public abstract UnitRenderer getUnitRenderer(Unit model);

}
