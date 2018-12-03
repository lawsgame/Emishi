package com.lawsgame.emishitactics.core.phases.battle.widgets.tempo;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels.ChoicePanel;

public class TempoActionChoicePanel extends ChoicePanel {
    private static int WIDTH = 150;
    private static int BUTTON_HEIGTH = 30;
    private static float X_PADDING = 25f;
    private static float Y_PADDING = 10f;
    private static float SLIDE_DURATION = 0.5f;

    private Skin skin;

    public TempoActionChoicePanel(Viewport uiport, Skin skin) {
        super(uiport);
        this.skin = skin;
    }

    @Override
    public void setContent(ButtonHandler handler) {
        clear();
        Array<TextButton> buttons = handler.getButtons(skin);
        for(int i = 0; i < buttons.size; i++){
            add(buttons.get(i)).width(WIDTH).height(BUTTON_HEIGTH).row();
        }
        setFillParent(true);
        align(Align.right | Align.top);
        padTop(Y_PADDING);
        padRight(X_PADDING);

    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

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
}
