package com.lawsgame.emishitactics.core.phases.battle.oldpan.tempo;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.BattlePhase;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.interfaces.LevelUpPanel;

public class TempoLevelUpPanel extends LevelUpPanel {
    private static float X_TEXT_OFFSET = 8f;
    private static float Y_TEXT_OFFSET = 8f;
    private static float PANEL_WIDTH = 200f;
    private static float PANEL_HEIGHT = 360;

    private String description;
    private StringBuilder builder;

    public TempoLevelUpPanel(Viewport stageViewport) {
        super(stageViewport);
        setWidth(PANEL_WIDTH);
        setHeight(PANEL_HEIGHT);
        center();
        setX(stageViewport.getWorldWidth()/4, false);
        hide();
        this.description = "";
    }

    @Override
    public void set(I18NBundle bundle, Unit luckyGuy, int[] statisticGain) {
        builder = new StringBuilder();

        builder.append("name : "+luckyGuy.getName());
        builder.append("\nlevel : "+luckyGuy.getLevel());
        builder.append("\n");
        builder.append("\nmobility : "+luckyGuy.getMobility()+" +"+statisticGain[1]);
        builder.append("\nleadership : "+luckyGuy.getLeadership()+" +"+statisticGain[3]);
        builder.append("\n__________________");
        builder.append("\nhitPoints : "+luckyGuy.getHitpoints()+" +"+statisticGain[0]);
        builder.append("\nbravery : "+luckyGuy.getBravery()+" +"+statisticGain[11]);
        builder.append("\ndexterity : "+luckyGuy.getDexterity()+" +"+statisticGain[8]);
        builder.append("\nskill : "+luckyGuy.getSkill()+" +"+statisticGain[10]);
        builder.append("\ncharisma : "+luckyGuy.getCharisma()+" +"+statisticGain[2]);
        builder.append("\n__________________");
        builder.append("\nstrength : "+luckyGuy.getStrength()+" +"+statisticGain[4]);
        builder.append("\nARMOR");
        builder.append("\n  piercinfArmor : "+luckyGuy.getArmor(Data.DamageType.PIERCING)+" +"+statisticGain[5]);
        builder.append("\n  bluntArmor : "+luckyGuy.getArmor(Data.DamageType.BLUNT)+" +"+statisticGain[6]);
        builder.append("\n  edgedArmor : "+luckyGuy.getArmor(Data.DamageType.EDGED)+" +"+statisticGain[7]);
        builder.append("\nagility : "+luckyGuy.getAgility()+" +"+statisticGain[9]);



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
        return false;
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
