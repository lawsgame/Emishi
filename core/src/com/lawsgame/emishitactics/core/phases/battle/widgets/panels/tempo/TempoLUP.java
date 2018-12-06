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

        label.setText(builder.toString());
    }
}
