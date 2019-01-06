package com.lawsgame.emishitactics.core.phases.battle.widgets.panels.tempo;


import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.interfaces.LevelUpPanel;

public class TempoLUP extends LevelUpPanel {
    private static int WIDTH = 200;
    private static int HEIGHT = 350;
    private static float X_PADDING_RIGHT = 30f;
    private static float FADE_DURATION = 0.2f;

    private Label label;

    private TempoLUP(Viewport stageUIViewport, float fadingDuration, int width, int height, Label label) {
        super(stageUIViewport, fadingDuration, width, height);
        this.label = label;
    }

    public static TempoLUP create(Viewport uiport, Skin skin){
        Label label = new Label("LEVEL UP", skin, "default");

        TempoLUP panel = new TempoLUP(uiport, FADE_DURATION, WIDTH, HEIGHT, label);
        panel.setX(X_PADDING_RIGHT, false);
        panel.setBackground(skin.getDrawable(Assets.UI_BLACK_BACKGROUND));
        panel.add(label).center();

        return panel;
    }

    @Override
    protected void setContent(Unit luckyGuy, int[] statisticGain){
        StringBuilder builder = new StringBuilder();

        builder.append("name : "+luckyGuy.getName());
        builder.append("\nlevel : "+luckyGuy.getLevel());
        builder.append("\n");
        builder.append("\nmobility : "+luckyGuy.getBaseStat(Data.UnitStat.MOBILITY)+" +"+statisticGain[0]);
        builder.append("\nleadership : "+luckyGuy.getBaseStat(Data.UnitStat.LEADERSHIP)+" +"+statisticGain[1]);
        builder.append("\ncharisma : "+luckyGuy.getBaseStat(Data.UnitStat.CHARISMA)+" +"+statisticGain[2]);
        builder.append("\n__________________");
        builder.append("\nhitPoints : "+luckyGuy.getBaseStat(Data.UnitStat.HIT_POINTS)+" +"+statisticGain[3]);
        builder.append("\nbravery : "+luckyGuy.getBaseStat(Data.UnitStat.BRAVERY)+" +"+statisticGain[4]);
        builder.append("\n__________________");
        builder.append("\nstrength : "+luckyGuy.getBaseStat(Data.UnitStat.STRENGTH)+" +"+statisticGain[5]);
        builder.append("\nARMOR");
        builder.append("\n  armorPiercing : "+luckyGuy.getBaseStat(Data.UnitStat.ARMOR_PIERCING)+" +"+statisticGain[6]);
        builder.append("\n  armorBlunt : "+luckyGuy.getBaseStat(Data.UnitStat.ARMOR_BLUNT)+" +"+statisticGain[7]);
        builder.append("\n  armorEdged : "+luckyGuy.getBaseStat(Data.UnitStat.ARMOR_EDGED)+" +"+statisticGain[8]);
        builder.append("\nagility : "+luckyGuy.getBaseStat(Data.UnitStat.AGILITY)+" +"+statisticGain[9]);
        builder.append("\ndexterity : "+luckyGuy.getBaseStat(Data.UnitStat.DEXTERITY)+" +"+statisticGain[10]);
        builder.append("\nskill : "+luckyGuy.getBaseStat(Data.UnitStat.SKILL)+" +"+statisticGain[11]);
        builder.append("\nluck : "+luckyGuy.getBaseStat(Data.UnitStat.LUCK)+ " +"+statisticGain[12]);

        label.setText(builder.toString());
    }
}
