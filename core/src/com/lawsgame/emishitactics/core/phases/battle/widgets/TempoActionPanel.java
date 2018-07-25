package com.lawsgame.emishitactics.core.phases.battle.widgets;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.phases.BattlePhase;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ActionPanel;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;

public abstract class TempoActionPanel extends ActionPanel{

    private static float X_OFFSET = 15f;
    private static float Y_OFFSET = 15f;
    private static float X_TEXT_OFFSET = 8f;
    private static float Y_TEXT_OFFSET = 8f;
    private static float PANEL_WIDTH = 220;
    private static float PANEL_HEIGHT = 130;

    protected String description;
    protected float slideDuration;
    protected StringBuilder builder = new StringBuilder("");

    public TempoActionPanel(Viewport stageViewport){
        super(stageViewport);
        setWidth(PANEL_WIDTH);
        setHeight(PANEL_HEIGHT);
        setX(stageViewport.getWorldWidth());
        setY(Y_OFFSET);
        setVisible(true);

        this.slideDuration = (X_OFFSET + getWidth())/ Data.PANEL_SLIDE_SPEED;
        this.description = "";
    }

    @Override
    public void show() {
        awaitingActions.offer(moveTo(stageViewport.getWorldWidth() - X_OFFSET - getWidth(),Y_OFFSET, slideDuration));
    }

    @Override
    public void hide() {
        awaitingActions.offer(moveTo( stageViewport.getWorldWidth(),Y_OFFSET, slideDuration));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(getX() == stageViewport.getWorldWidth()){
            description = builder.toString();
        }

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(TempoSpritePool.get().getBlackBGSprite(),getX(), getY(), getWidth(), getHeight() );
        BattlePhase.testFont.draw(batch, description, getX() + X_TEXT_OFFSET, getY() + getHeight() - Y_TEXT_OFFSET);
    }

    // ------------- GETTERS -------------------


    public StringBuilder getBuilder() {
        return builder;
    }

    public static class AttackPanel extends TempoActionPanel{
        protected int damage;
        protected int hitrate;

        public AttackPanel(Viewport stageViewport, int damage, int hitrate) {
            super(stageViewport);
            this.damage = damage;
            this.hitrate = hitrate;
        }

        @Override
        public void set() {
            builder = new StringBuilder();
            builder.append("Damage : "+ damage);
            builder.append("\nHit rate : "+ hitrate);
        }
    }
}
