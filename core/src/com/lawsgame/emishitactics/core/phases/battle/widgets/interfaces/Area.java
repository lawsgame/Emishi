package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.engine.GameElement;
import com.lawsgame.emishitactics.engine.GameRenderableEntity;

public abstract class Area implements GameRenderableEntity{
    protected boolean visible = true;

    @Override
    public void render(SpriteBatch batch) {
        if(visible){
            renderTiles(batch);
        }
    }

    public abstract void renderTiles(SpriteBatch batch);
    public abstract void addTile(int r, int c);
    public abstract void addTiles(Array<int[]> area);
    public abstract void reset();
    public abstract boolean contains(int r, int c);

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
