package com.lawsgame.emishitactics.core.renderers.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Props;
import com.lawsgame.emishitactics.core.models.AbstractArmy;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Unit;

public abstract class BattlefieldRenderer extends Renderer<Battlefield> {

    public BattlefieldRenderer(Battlefield model) {
        super(model);
    }

    public abstract void renderTiles(SpriteBatch batch);
    public abstract void renderUnits(SpriteBatch batch);
    public abstract UnitRenderer getUnitRenderer(Unit model);
    public abstract void triggerBuildAnimation(int row, int col, Props.TileType type, Unit builder);


}
