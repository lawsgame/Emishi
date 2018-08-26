package com.lawsgame.emishitactics.core.phases.battle.widgets.tempo;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.models.Player;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.BattlePhase;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.LootPanel;

public class TempoLootPanel extends LootPanel {
    private static float X_TEXT_OFFSET = 8f;
    private static float Y_TEXT_OFFSET = 8f;
    private static float PANEL_WIDTH = 210f;
    private static float PANEL_HEIGHT = 30;

    private String description;

    public TempoLootPanel(Viewport stageViewport) {
        super(stageViewport);
        setWidth(PANEL_WIDTH);
        setHeight(PANEL_HEIGHT);
        center();
        hide();
        this.description = "";
    }

    @Override
    public void set(Item item, I18NBundle bundle) {
        description = "looted item : "+item.getName(bundle);
    }

    @Override
    public void set(Item item, I18NBundle bundle, Player player) {
        set(item, bundle);
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
