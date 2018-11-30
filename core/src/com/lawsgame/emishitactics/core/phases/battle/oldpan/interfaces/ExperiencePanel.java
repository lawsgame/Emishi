package com.lawsgame.emishitactics.core.phases.battle.oldpan.interfaces;

import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.TPanel;

public abstract class ExperiencePanel extends TPanel {

    public ExperiencePanel(Viewport stageViewport) {
        super(stageViewport);
    }

    public abstract boolean isExecuting();
    public abstract void set(I18NBundle localization, int initialExperience, int experienceGained);
}
