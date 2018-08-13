package com.lawsgame.emishitactics.core.phases.battle.widgets.tempo;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.phases.battle.BattlePhase;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.TilePanel;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;


public class ShortTilePanel extends TilePanel {



    private static float X_OFFSET = 15f;
    private static float Y_OFFSET = 15f;
    private static float X_TEXT_OFFSET = 8f;
    private static float Y_TEXT_OFFSET = 8f;

    private static float PANEL_WIDTH = 130;
    private static float PANEL_HEIGHT = 30f;

    StringBuilder builder = new StringBuilder("");
    protected String description;
    private float slideDuration;


    public ShortTilePanel(Viewport stageViewport){
        super(stageViewport);
        setWidth(PANEL_WIDTH);
        setHeight(PANEL_HEIGHT);
        setX(-getWidth());
        setY(Y_OFFSET);
        setVisible(true);

        this.slideDuration = (X_OFFSET + getWidth())/ Data.PANEL_SLIDE_SPEED;
        this.description = "";
    }

    public void set(Data.TileType tileType){
        builder = new StringBuilder("");
        builder.append(tileType.name().toLowerCase());
        description = builder.toString();
    }

    @Override
    public void show(){
        awaitingActions.offer(moveTo(X_OFFSET,Y_OFFSET, slideDuration));
    }

    @Override
    public void hide(){
        awaitingActions.offer(moveTo(-getWidth(),Y_OFFSET, slideDuration));
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
        batch.draw(TempoSpritePool.getInstance().getBlackBGSprite(),getX(), getY(), getWidth(), getHeight() );
        BattlePhase.testFont.draw(batch, description, getX() + X_TEXT_OFFSET, getY() + getHeight() - Y_TEXT_OFFSET);
    }

}
