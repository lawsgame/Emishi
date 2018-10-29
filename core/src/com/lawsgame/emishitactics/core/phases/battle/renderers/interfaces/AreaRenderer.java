package com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.engine.GameRenderableEntity;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.rendering.Renderer;

public abstract class AreaRenderer extends Renderer<Area> implements GameRenderableEntity{
    protected boolean visible = true;

    public AreaRenderer(Area model) {
        super(model);
    }

    public abstract void change();
    protected abstract void renderArea(SpriteBatch batch);

    @Override
    public void render(SpriteBatch batch) {
        if(visible){
            renderArea(batch);
        }
    }

    @Override
    public void update(float dt) { }

    @Override
    public boolean isExecuting() {
        return false;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void getNotification(Observable sender, Object data) {
        if(data == null) {
            change();
        }else if(data instanceof Notification.Disabled){
            setVisible(((Notification.Disabled) data).disabled);
        }
    }
}
