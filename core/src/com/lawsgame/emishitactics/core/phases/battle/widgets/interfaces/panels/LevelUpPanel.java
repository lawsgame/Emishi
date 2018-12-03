package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.FadingPanel;

public abstract class LevelUpPanel extends FadingPanel {

    public LevelUpPanel(Viewport stageUIViewport, float fadingDuration, int width, int height) {
        super(stageUIViewport, fadingDuration, width, height);
    }

    public void update(final Unit luckyGuy, final int[] statisticGain) {
        final LevelUpPanel p = this;
        this.awaitingActions.add(new Action() {
            @Override
            public boolean act(float delta) {
                p.setContent(luckyGuy, statisticGain);
                return true;
            }
        });
    }

    protected abstract void setContent(Unit luckyGuy, int[] statisticGain);
}
