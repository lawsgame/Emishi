package com.lawsgame.emishitactics.core.phases.battle.widgets.panels.tempo;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.fronts.ShortTilePanel;


public class TempoShortTileP extends ShortTilePanel {

    private static final float SLIDE_DURATION = 0.5f;
    private static final int X_SHOWING_PADDING = 15;
    private static final int Y_PADDING = 15;
    private static final int WIDTH = 200;
    private static final int HEIGHT = 80;
    private static final boolean TOP = false;
    private static final boolean LEFT = true;

    private Label label;

    private TempoShortTileP(Viewport stageUIViewport, float slidingDuration, int xShowingPadding, int yPadding, int width, int height, boolean top, boolean left, Label label) {
        super(stageUIViewport, slidingDuration, xShowingPadding, yPadding, width, height, top, left);
        this.label = label;
    }

    public static TempoShortTileP create(Viewport stageUIViewport, Skin skin){
        LabelStyle style = skin.get("default", LabelStyle.class);
        Label label = new Label("TILE TYPE", style);
        TempoShortTileP panel = new TempoShortTileP(stageUIViewport, SLIDE_DURATION, X_SHOWING_PADDING, Y_PADDING, WIDTH, HEIGHT, TOP, LEFT, label);
        panel.setBackground(skin.getDrawable("black_background"));
        panel.add(label).expand();

        return panel;
    }

    @Override
    protected void setContent(Data.TileType content) {
        StringBuilder builder = new StringBuilder();
        builder.append(content.getName());
        if(content.getHealPower() != 0){
            builder.append("\n  Heal / turn : ");
            builder.append(content.getHealPower());
        }
        if(content.getAttackMightBonus() != 0){
            builder.append("\n  Attack might bonus : ");
            builder.append(content.getAttackMightBonus());
        }
        if(content.getAttackAccBonus() != 0){
            builder.append("\n  Attack accuracy bonus : ");
            builder.append(content.getAttackAccBonus());
        }
        if(content.getDefenseBonus() != 0){
            builder.append("\n  Defense bonus : ");
            builder.append(content.getDefenseBonus());
        }
        if(content.getAvoidBonus() != 0){
            builder.append("\n  Avoidance bonus : ");
            builder.append(content.getAvoidBonus());
        }
        if(content.enhanceRange())      {
            builder.append("\n  Range bonus : 1");
        }

        label.setText(builder.toString());
    }
}
