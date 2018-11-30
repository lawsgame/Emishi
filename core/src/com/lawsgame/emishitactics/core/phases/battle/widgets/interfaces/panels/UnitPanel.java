package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.SlidingPanel;

public abstract class UnitPanel extends SlidingPanel<Unit> {

    public UnitPanel(Viewport stageUIViewport, float slidingDuration, int xShowingPadding, int yPadding, int width, int height, boolean top, boolean left) {
        super(stageUIViewport, slidingDuration, xShowingPadding, yPadding, width, height, top, left);
    }

}
