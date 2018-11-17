package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces;

import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class ExperiencePanel extends Panel{

    public ExperiencePanel(Viewport stageViewport) {
        super(stageViewport);
    }

    public abstract boolean isExecuting();
    public abstract void set(I18NBundle bundle, int initialExperience, int experienceGained);
}
