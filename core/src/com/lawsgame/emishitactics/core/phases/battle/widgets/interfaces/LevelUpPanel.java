package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces;

import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Unit;

public abstract class LevelUpPanel extends Panel {

    public LevelUpPanel(Viewport stageViewport) {
        super(stageViewport);
    }

    public abstract void set(I18NBundle bundle, Unit luckyGuy, int[] statisticGain);
}
