package com.lawsgame.emishitactics.core.phases.battle.widgets.tempo;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.phases.battle.BattlePhase;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels.ExperiencePanel;

public class TempoExperiencePanel extends ExperiencePanel {

    private static float X_TEXT_OFFSET = 8f;
    private static float Y_TEXT_OFFSET = 8f;
    private static float PANEL_WIDTH = 90f;
    private static float PANEL_HEIGHT = 30;

    private String description;
    private StringBuilder builder;

    public TempoExperiencePanel(Viewport stageViewport) {
        super(stageViewport);
        setWidth(PANEL_WIDTH);
        setHeight(PANEL_HEIGHT);
        center();
        setY(stageViewport.getWorldHeight()/4, true);
        hide();
        this.description = "";
    }

    @Override
    public boolean isExecuting() {
        return false;
    }

    @Override
    public void set(I18NBundle localization, int initialExperience, int experienceGained) {
        description = "+"+experienceGained+" exp";
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
        batch.draw(TempoSpritePool.get().getBlackBGSprite(),getX(), getY(), getWidth(), getHeight() );
        BattlePhase.testFont.draw(batch, description, getX() + X_TEXT_OFFSET, getY() + getHeight() - Y_TEXT_OFFSET);
    }
}
