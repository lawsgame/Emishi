package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Player;
import com.lawsgame.emishitactics.core.models.interfaces.Item;

public abstract class LootPanel extends Panel {

    public LootPanel(Viewport stageViewport) {
        super(stageViewport);
    }

    public abstract void set(Item item);
    public abstract void set(Item item, Player player);
}

