package com.lawsgame.emishitactics.core.renderers.interfaces;

import com.lawsgame.emishitactics.core.models.AArmy;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Unit;

public abstract class ABattlefieldRenderer extends ARenderer<Battlefield> {

    public ABattlefieldRenderer(Battlefield model) {
        super(model);
    }

    public abstract void addArmyRenderer(AArmy army);
    public abstract AUnitRenderer getUnitRenderer(Unit unit);


}
