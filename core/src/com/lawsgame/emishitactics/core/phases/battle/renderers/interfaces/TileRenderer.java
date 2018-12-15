package com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces;

import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Tile;
import com.lawsgame.emishitactics.engine.rendering.Renderer;

public abstract class TileRenderer extends Renderer<Tile> {

    public TileRenderer(Tile model) {
        super(model);
    }

    public abstract boolean isRevealed();
    public abstract void setRevealed(boolean revealed, Data.SparkleType type);
}
