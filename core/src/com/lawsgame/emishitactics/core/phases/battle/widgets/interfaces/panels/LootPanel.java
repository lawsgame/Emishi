package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.FadingPanel;

public abstract class LootPanel extends FadingPanel<Item> {

    public LootPanel(Viewport stageUIViewport, float fadingDuration, int width, int height) {
        super(stageUIViewport, fadingDuration, width, height);
    }
}
