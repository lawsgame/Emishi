package com.lawsgame.emishitactics.core.phases.battle.widgets.panels.tempo;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.interfaces.ShortUnitPanel;

public class TempoSUP extends ShortUnitPanel {
    private static final float SLIDE_DURATION = 0.5f;
    private static final int X_SHOWING_PADDING = 15;
    private static final int Y_PADDING = 15;
    private static final int WIDTH = 260;
    private static final int HEIGHT = 180;
    private static final boolean TOP = false;
    private static final boolean LEFT = false;

    private Label label;

    public TempoSUP(Viewport stageUIViewport, float slidingDuration, int xShowingPadding, int yPadding, int width, int height, boolean top, boolean left, Label label) {
        super(stageUIViewport, slidingDuration, xShowingPadding, yPadding, width, height, top, left);
        this.label = label;
    }

    public static TempoSUP create(Viewport stageUIViewpor, Skin skin){
        Label label = new Label("UNIT", skin, "default");
        TempoSUP panel = new TempoSUP(stageUIViewpor, SLIDE_DURATION, X_SHOWING_PADDING, Y_PADDING, WIDTH, HEIGHT, TOP, LEFT, label);
        panel.add(label).expand();
        panel.setBackground(skin.getDrawable(Assets.UI_BLACK_BACKGROUND));
        return panel;
    }

    @Override
    protected void setContent(Unit content) {
        StringBuilder builder = new StringBuilder();

        builder.append("Name : ");
        builder.append(content.getName());
        builder.append("\nLevel : ");
        builder.append(content.getLevel());

        if (content.isWarlord()) {
            builder.append("\nPosition : warlord");
        } else {
            if (content.isWarChief()) {
                builder.append("\nPosition : war chief");
            } else {
                builder.append("\nPosition : soldier");
            }
        }

        builder.append("\nHit points  : ");
        builder.append(content.getCurrentHitPoints());
        builder.append("/");
        builder.append(content.getAppHitpoints());
        builder.append("\nMoral  : ");
        builder.append(content.getCurrentMoral());
        builder.append("/");
        builder.append(content.getAppMoral());
        builder.append("\nAction Points  : ");
        builder.append(content.getActionPoints());
        builder.append("\nCurrent weapon : ");
        builder.append(content.getCurrentWeapon().toString());

        label.setText(builder.toString());
    }
}
