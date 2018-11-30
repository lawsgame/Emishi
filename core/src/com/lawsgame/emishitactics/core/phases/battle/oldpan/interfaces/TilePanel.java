package com.lawsgame.emishitactics.core.phases.battle.oldpan.interfaces;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.TPanel;

public abstract class TilePanel extends TPanel {


    public TilePanel(Viewport stageViewport) {
        super(stageViewport);
    }

    public abstract void set(Data.TileType tileType);
}
