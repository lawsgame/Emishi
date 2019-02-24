package com.lawsgame.emishitactics.core.phases.battle.widgets.panels.fronts;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.interfaces.FadingPanel;

public abstract class LongUnitPanel extends FadingPanel {

    public LongUnitPanel(Viewport stageUIViewport, float fadingDuration, int width, int height) {
        super(stageUIViewport, fadingDuration, width, height);
    }

    public void update(final Unit content) {
        final LongUnitPanel p = this;
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
