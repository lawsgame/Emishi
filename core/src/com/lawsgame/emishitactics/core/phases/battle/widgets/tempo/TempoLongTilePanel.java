package com.lawsgame.emishitactics.core.phases.battle.widgets.tempo;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels.LongTilePanel;

public class TempoLongTilePanel extends LongTilePanel {
    private static int WIDTH = 350;
    private static int HEIGHT = 200;
    private static float FADE_DURATION = 0.2f;

    private Label label;

    public TempoLongTilePanel(Viewport stageUIViewport, float fadingDuration, int width, int height, Label label) {
        super(stageUIViewport, fadingDuration, width, height);
        this.label = label;
    }

    public static TempoLongTilePanel create(Viewport stageUIViewport, Skin skin){
        Label label = new Label("TILE TYPE", skin, "default");

        TempoLongTilePanel panel = new TempoLongTilePanel(stageUIViewport, FADE_DURATION, WIDTH, HEIGHT, label);
        panel.setBackground(skin.getDrawable(Assets.UI_BLACK_BACKGROUND));
        panel.add(label).center();

        return panel;
    }

    @Override
    protected void setContent(Data.TileType tileType) {
        StringBuilder builder = new StringBuilder();
        builder.append(tileType.name());

        builder.append("\n\n");
        if(tileType.isUrbanArea()) builder.append("urban ");
        if(tileType.isPlunderable()) builder.append("plunderable ");
        if(tileType.isReachable(false)) builder.append("reachable ");
        else if(tileType.isReachable(true)) builder.append("reachable only by pathfinder");
        else builder.append("unreachable ");
        builder.append("type");

        builder.append("\n\nHeal / turn : "+tileType.getHealPower());
        builder.append("\nStrength bonus : "+tileType.getAttackMightBonus());
        builder.append("\nDefense bonus : "+ tileType.getDefenseBonus());
        builder.append("\nAvoidance bonus : "+tileType.getAvoidBonus());
        builder.append("\nAttack accuracy bonus : "+tileType.getAttackAccBonus());
        builder.append("\nRange enhanced : "+tileType.enhanceRange());

        label.setText(builder.toString());

    }
}
