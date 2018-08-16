package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;

public abstract class UnitPanel extends Panel {


    public UnitPanel(Viewport stageViewport) {
        super(stageViewport);
    }

    public abstract void set(IUnit unit);
}
