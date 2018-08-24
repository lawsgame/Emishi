package com.lawsgame.emishitactics.core.phases.battle.widgets.tempo;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.phases.battle.BattlePhase;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.TilePanel;

public class LongTilePanel extends TilePanel {

    private static float X_TEXT_OFFSET = 8f;
    private static float Y_TEXT_OFFSET = 8f;
    private static float PANEL_WIDTH = 350f;
    private static float PANEL_HEIGHT = 200;

    private String description;
    private StringBuilder builder;

    public LongTilePanel(Viewport stageViewport) {
        super(stageViewport);
        setWidth(PANEL_WIDTH);
        setHeight(PANEL_HEIGHT);
        center();
        hide();
        this.description = "";
    }

    @Override
    public void set(Data.TileType tileType) {
        builder = new StringBuilder();
        builder.append(tileType.name());

        builder.append("\n\n");
        if(tileType.isUrbanArea()) builder.append("urban ");
        if(tileType.isPlunderable()) builder.append("plunderable ");
        if(tileType.isLootable()) builder.append("lootable ");
        if(tileType.isReachable()) builder.append("reachable "); else builder.append("unreachable ");
        builder.append("buildingType");

        builder.append("\n\nHeal / turn : "+tileType.getHealPower());
        builder.append("\nStrength bonus : "+tileType.getAttackMightBonus());
        builder.append("\nDefense bonus : "+ tileType.getDefenseBonus());
        builder.append("\nAvoidance bonus : "+tileType.getAvoidBonus());
        builder.append("\nATTACK accuracy bonus : "+tileType.getAttackAccBonus());
        builder.append("\nRange enhanced : "+tileType.enhanceRange());

        description = builder.toString();

    }

    @Override
    public void show() {
        setVisible(true);
    }

    @Override
    public void hide() {
        setVisible(false);
    }

    @Override
    public boolean isHiding() {
        return isVisible();
    }

    @Override
    public float getHidingTime() {
        return 0;
    }

    @Override
    public float getShowingTime() {
        return 0;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(TempoSpritePool.getInstance().getBlackBGSprite(),getX(), getY(), getWidth(), getHeight() );
        BattlePhase.testFont.draw(batch, description, getX() + X_TEXT_OFFSET, getY() + getHeight() - Y_TEXT_OFFSET);
    }
}
