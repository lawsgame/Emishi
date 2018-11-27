package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels;

import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Player;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.Panel;

public abstract class LootPanel extends Panel {

    public LootPanel(Viewport stageViewport) {
        super(stageViewport);
    }

    public abstract void set(Item item, I18NBundle bundle);
    public abstract void set(Item item, I18NBundle bundle, Player player);
}

