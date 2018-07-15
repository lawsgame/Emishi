package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces;

import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class ActionPanel extends Panel {

    public ActionPanel(Viewport stageViewport) {
        super(stageViewport);
    }

    public abstract void set();
}
