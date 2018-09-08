package com.lawsgame.emishitactics.core.phases.battle.renderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.AreaRenderer;

public class IsoAreaRenderer extends AreaRenderer {
    public IsoAreaRenderer(Area model) {
        super(model);
    }

    @Override
    public void render(SpriteBatch batch) {

    }

    @Override
    public boolean isExecuting() {
        return false;
    }

    @Override
    public void setVisible(boolean visible) {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void getNotification(Object data) {

    }
}
