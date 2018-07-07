package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.constants.Data;

public abstract class ATilePanel extends APanel {


    public ATilePanel(Viewport stageViewport) {
        super(stageViewport);
    }

    public abstract void set(Data.TileType tileType);
}
