package com.lawsgame.emishitactics.core.phases.battle.renderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Notification;
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
    protected void addTileRenderer(int row, int col, Data.TileType type) {

    }

    @Override
    protected void addUnitRenderer(int row, int col, IUnit model) {

    }

    @Override
    protected void addaAreaRenderer(Area area) {

    }

    @Override
    protected boolean isUnitRendererCreated(IUnit model) {
        return false;
    }

    @Override
    protected boolean removeUnitRenderer(IUnit model) {
        return false;
    }

    @Override
    protected void removeAreaRenderersAssociatedWith(IUnit model) {

    }

    @Override
    protected void removeAreaRenderer(Area model) {

    }

    @Override
    protected boolean isCurrentTaskCompleted() {
        return false;
    }

    @Override
    protected void setBuildTask(Notification.Build build) {

    }

    @Override
    public boolean isExecuting() {
        return false;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
    }

    @Override
    public void getNotification(Object data) {

    }
}
