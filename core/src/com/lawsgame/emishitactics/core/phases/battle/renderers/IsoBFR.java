package com.lawsgame.emishitactics.core.phases.battle.renderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.AreaRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class IsoBFR extends BattlefieldRenderer {
    private final float RATIO = 0.5f; // <1 : ratio between the the great and little dialogue

    public IsoBFR(Battlefield model) {
        super(model);
    }

    @Override
    public void render(SpriteBatch batch) {

    }

    @Override
    public int getRowFrom(float gameX, float gameY) {
        return (int)(gameY/RATIO - gameX + getModel().getNbRows()/2.0f);
    }

    @Override
    public int getColFrom(float gameX, float gameY) {
        return (int) (gameY/RATIO + gameX - getModel().getNbColumns()/2.0f);
    }

    @Override
    protected float getXFrom(int row, int col) {
        return 0;
    }

    @Override
    protected float getYFrom(int row, int col) {
        return 0;
    }

    @Override
    public void displayDeploymentAreas(boolean visible) {

    }

    @Override
    public BattleUnitRenderer getUnitRenderer(IUnit model) {
        return null;
    }

    @Override
    public AreaRenderer getAreaRenderer(Area area) {
        return null;
    }

    @Override
    public void addTileRenderer(int row, int col, Data.TileType type) {

    }

    @Override
    public void addUnitRenderer(int row, int col, IUnit model) {

    }

    @Override
    public void addAreaRenderer(Area area) {

    }

    @Override
    public void removeUnitRenderer(IUnit model) {

    }

    @Override
    public void removeAreaRenderer(Area model) {

    }

    @Override
    protected boolean isUnitRendererCreated(IUnit model) {
        return false;
    }

    @Override
    protected void removeAreaRenderersAssociatedWith(IUnit model) {

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
    public void setVisible(boolean visible) {

    }
}
