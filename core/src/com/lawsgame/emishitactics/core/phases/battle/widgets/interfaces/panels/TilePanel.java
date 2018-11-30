package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.SlidingPanel;

public abstract class TilePanel extends SlidingPanel<Data.TileType> {

    public TilePanel(Viewport stageUIViewport, float slidingDuration, int xShowingPadding, int yPadding,int width, int height, boolean top, boolean left) {
        super(stageUIViewport, slidingDuration, xShowingPadding, yPadding, width, height, top, left);
    }
}
