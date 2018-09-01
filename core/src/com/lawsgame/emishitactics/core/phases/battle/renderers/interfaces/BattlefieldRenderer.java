package com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.engine.rendering.Renderer;

public abstract class BattlefieldRenderer extends Renderer<Battlefield> {

    public BattlefieldRenderer(Battlefield model) {
        super(model);
    }

    public abstract void renderTiles(SpriteBatch batch);
    public abstract void renderAreas(SpriteBatch batch);
    public abstract void renderUnits(SpriteBatch batch);
    public abstract BattleUnitRenderer getUnitRenderer(IUnit model);
    public abstract int getRowFromGameCoords(float gameX, float gameY);
    public abstract int getColFromGameCoords(float gameX, float gameY);

    public static class BFRendererException extends Exception{
        public BFRendererException(String msg){
            super(msg);
        }
    }
}
