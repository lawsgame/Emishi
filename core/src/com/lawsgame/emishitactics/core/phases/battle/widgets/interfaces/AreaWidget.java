package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.AreaRenderer;
import com.lawsgame.emishitactics.engine.GameElement;
import com.lawsgame.emishitactics.engine.GameRenderableEntity;

public abstract class AreaWidget implements GameRenderableEntity{
    public abstract void addTile(int r, int c);
    public abstract void setTile(int row, int col);
    public abstract void setTiles(Array<int[]> area);
    public abstract boolean contains(int r, int c);
    public abstract void setVisible(boolean visible);
    public abstract Area getModel();
    public abstract AreaRenderer getRenderer();


}
