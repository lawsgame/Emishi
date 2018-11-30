package com.lawsgame.emishitactics.core.phases.battle.oldpan.interfaces;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.TPanel;

public abstract class UnitPanel extends TPanel {


    public UnitPanel(Viewport stageViewport) {
        super(stageViewport);
    }

    public abstract void set(Unit unit);
}
