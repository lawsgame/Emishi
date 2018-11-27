package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.Panel;

public abstract class UnitPanel extends Panel {


    public UnitPanel(Viewport stageViewport) {
        super(stageViewport);
    }

    public abstract void set(Unit unit);
}
