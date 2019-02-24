package com.lawsgame.emishitactics.core.phases.battle.widgets.panels.fronts;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.interfaces.SlidingPanel;

public abstract class ShortTilePanel extends SlidingPanel {

    public ShortTilePanel(Viewport stageUIViewport, float slidingDuration, int xShowingPadding, int yPadding, int width, int height, boolean top, boolean left) {
        super(stageUIViewport, slidingDuration, xShowingPadding, yPadding, width, height, top, left);
    }

    public void update(final Data.TileType content) {
        final ShortTilePanel p = this;
        this.awaitingActions.add(new Action() {
            @Override
            public boolean act(float delta) {
                p.setContent(content);
                return true;
            }
        });
    }

    protected abstract void setContent(Data.TileType content);
}
