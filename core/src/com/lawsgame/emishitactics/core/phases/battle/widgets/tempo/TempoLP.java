package com.lawsgame.emishitactics.core.phases.battle.widgets.tempo;


import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels.LootPanel;

public class TempoLP extends LootPanel {
    private static int WIDTH = 120;
    private static int HEIGHT = 60;
    private static float Y_PADDING_UP = 30f;
    private static float FADE_DURATION = 0.2f;

    private Label label;

    private TempoLP(Viewport stageUIViewport, float fadingDuration, int width, int height, Label label) {
        super(stageUIViewport, fadingDuration, width, height);
        this.label = label;
    }

    public static TempoLP create(Viewport stageUIViewport, Skin skin){
        Label label = new Label("ITEM", skin, "default");

        TempoLP panel = new TempoLP(stageUIViewport, FADE_DURATION, WIDTH, HEIGHT, label);
        panel.setY(panel.getY() + Y_PADDING_UP);
        panel.setBackground(skin.getDrawable("black_background"));
        panel.add(label).center();

        return panel;
    }

    @Override
    protected void setContent(Item content, boolean forThePlayer) {
        label.setText(content.getName());
    }
}
