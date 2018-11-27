package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.Panel;

public abstract class TilePanel extends Panel {


    public TilePanel(Viewport stageViewport) {
        super(stageViewport);
    }

    public abstract void set(Data.TileType tileType);
}
