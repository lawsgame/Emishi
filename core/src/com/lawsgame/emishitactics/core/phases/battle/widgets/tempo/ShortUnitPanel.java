package com.lawsgame.emishitactics.core.phases.battle.widgets.tempo;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.BattlePhase;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.UnitPanel;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;

public class ShortUnitPanel extends UnitPanel {

    private static float X_OFFSET = 15f;
    private static float Y_OFFSET = 15f;
    private static float X_TEXT_OFFSET = 8f;
    private static float Y_TEXT_OFFSET = 8f;
    private static float PANEL_WIDTH = 220;
    private static float PANEL_HEIGHT = 130f;

    private String description;
    private float slideDuration;
    StringBuilder builder = new StringBuilder("");

    public ShortUnitPanel(Viewport stageViewport){
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
    public void set(Battlefield bf, int rowUnit, int colUnit) {
        if(bf.isTileOccupied(rowUnit, colUnit)) {
            IUnit unit = bf.getUnit(rowUnit, colUnit);
            builder = new StringBuilder("");
            builder.append("Name : " + unit.getName());
            builder.append("\nLevel : " + unit.getLevel());

            if (unit.isWarlord()) {
                builder.append("\nPosition : warlord");
            } else {
                if (unit.isWarChief()) {
                    builder.append("\nPosition : war chief");
                } else {
                    builder.append("\nPosition : soldier");
                }
            }

            builder.append("\nHP  : " + unit.getCurrentHP() + "/" + unit.getAppHitpoints());
            builder.append("\nMoral  : " + unit.getCurrentMoral() + "/" + unit.getAppMoral());
            builder.append("\nCurrent weapon : " + unit.getCurrentWeapon().toString() + " ");
        }
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
        batch.draw(TempoSpritePool.getInstance().getBlackBGSprite(),getX(), getY(), getWidth(), getHeight() );
        BattlePhase.testFont.draw(batch, description, getX() + X_TEXT_OFFSET, getY() + getHeight() - Y_TEXT_OFFSET);
    }
}
