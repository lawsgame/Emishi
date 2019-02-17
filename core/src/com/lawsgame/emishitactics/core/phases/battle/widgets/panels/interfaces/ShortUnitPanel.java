package com.lawsgame.emishitactics.core.phases.battle.widgets.panels.interfaces;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Unit;

public abstract class ShortUnitPanel extends SlidingPanel {

    public ShortUnitPanel(Viewport stageUIViewport, float slidingDuration, int xShowingPadding, int yPadding, int width, int height, boolean top, boolean left) {
        super(stageUIViewport, slidingDuration, xShowingPadding, yPadding, width, height, top, left);
    }

    public void update(final Unit content) {
        final ShortUnitPanel p = this;
        this.awaitingActions.add(new Action() {
            @Override
            public boolean act(float delta) {
                p.setContent(content);
                return true;
            }
        });
    }

    protected abstract void setContent(Unit content);

}
