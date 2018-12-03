package com.lawsgame.emishitactics.core.phases.battle.widgets.tempo;


import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels.ExperiencePanel;

public class TempoEP extends ExperiencePanel {
    private static int WIDTH = 120;
    private static int HEIGHT = 60;
    private static float Y_PADDING_UP = 30f;
    private static float FADE_DURATION = 0.2f;

    private Label label;

    public TempoEP(Viewport stageUIViewport, float fadingDuration, int width, int height, Label label) {
        super(stageUIViewport, fadingDuration, width, height);
        this.label = label;
    }

    public static TempoEP create(Viewport stageUIViewport, Skin skin){
        Label label = new Label("EXPERIENCE", skin, "default");

        TempoEP panel = new TempoEP(stageUIViewport, FADE_DURATION, WIDTH, HEIGHT, label);
        panel.setY(panel.getY() + Y_PADDING_UP);
        panel.setBackground(skin.getDrawable(Assets.UI_BLACK_BACKGROUND));
        panel.add(label).center();

        return panel;
    }

    @Override
    protected void setContent(int exp) {
        StringBuilder builder = new StringBuilder();
        builder.append("+ ");
        builder.append(exp);
        builder.append(" EXP");
        label.setText(builder.toString());
    }
}
