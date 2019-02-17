package com.lawsgame.emishitactics.core.phases.battle.widgets.panels.interfaces;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class ExperiencePanel extends FadingPanel {

    public ExperiencePanel(Viewport stageUIViewport, float fadingDuration, int width, int height) {
        super(stageUIViewport, fadingDuration, width, height);
    }

    public void update(final int experience) {
        final ExperiencePanel p = this;
        this.awaitingActions.add(new Action() {
            @Override
            public boolean act(float delta) {
                p.setContent(experience);
                return true;
            }
        });
    }

    protected abstract void setContent(int experience);
}
