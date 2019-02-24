package com.lawsgame.emishitactics.core.phases.battle.widgets.panels.fronts;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.interfaces.FadingPanel;

public abstract class LootPanel extends FadingPanel {

    public LootPanel(Viewport stageUIViewport, float fadingDuration, int width, int height) {
        super(stageUIViewport, fadingDuration, width, height);
    }

    public void update(final Item item, final boolean forThePlayer) {
        final LootPanel p = this;
        this.awaitingActions.add(new Action() {
            @Override
            public boolean act(float delta) {
                p.setContent(item, forThePlayer);
                return true;
            }
        });
    }

    protected abstract void setContent(Item content, boolean forThePlayer);
}
