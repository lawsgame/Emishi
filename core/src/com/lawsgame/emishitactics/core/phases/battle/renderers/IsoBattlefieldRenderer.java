package com.lawsgame.emishitactics.core.phases.battle.renderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

/**
 *
 *
 *
 *
 * xCenter = (rows + cols)/4 + (c - r)/2
 * yCenter = [(rows + cols)/4 + (r + c)/2] / ratio
 */
public class IsoBattlefieldRenderer extends BattlefieldRenderer {


    private float ratio;

    /**
     *
     * @param model : battlefield
     * @param ratio : l over L
     */
    public IsoBattlefieldRenderer(Battlefield model, float ratio) {
        super(model);
        this.ratio = ratio;
    }

    @Override
    public void renderTiles(SpriteBatch batch) {

    }

    @Override
    public void renderAreas(SpriteBatch batch) {

    }

    @Override
    public void renderUnits(SpriteBatch batch) {

    }

    @Override
    public BattleUnitRenderer getUnitRenderer(IUnit model) {
        return null;
    }

    @Override
    public int getRowFromGameCoords(float gameX, float gameY) {
        float flatY = gameY/ratio - gameX + getModel().getNbRows()/2.0f;
        return (int)flatY;
    }

    @Override
    public int getColFromGameCoords(float gameX, float gameY) {
        float flatX = gameX + gameY/ratio - getModel().getNbColumns()/2.0f;
        return (int)flatX;

    }

    @Override
    public boolean isExecuting() {
        return false;
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void getNotification(Object data) {

    }
}
