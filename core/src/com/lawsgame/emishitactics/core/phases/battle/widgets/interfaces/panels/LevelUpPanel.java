package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels;

import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.Panel;

public abstract class LevelUpPanel extends Panel {

    public LevelUpPanel(Viewport stageViewport) {
        super(stageViewport);
    }

    public abstract void set(I18NBundle bundle, Unit luckyGuy, int[] statisticGain);
}
