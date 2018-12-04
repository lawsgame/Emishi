package com.lawsgame.emishitactics.core.phases.battle.widgets.tempo;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels.ActionChoicePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels.ChoicePanel;

public class TempoActionChoicePanel extends ActionChoicePanel {
    public static int WIDTH = 150;
    public static int BUTTON_HEIGTH = 30;
    public static float X_PADDING = 25f;
    public static float Y_PADDING = 10f;
    public static float SLIDE_DURATION = 0.5f;

    private Skin skin;
    private I18NBundle localization;


    public TempoActionChoicePanel(Viewport uiport, Skin skin, I18NBundle localization) {
        super(uiport, SLIDE_DURATION, X_PADDING, Y_PADDING, WIDTH, 0, true, false);
        this.skin = skin;
        this.localization = localization;
    }


    @Override
    public TextButton getButtonInstance(Data.ActionChoice choice) {
        return new TextButton(choice.getName(localization), skin, "commandpan");
    }


    @Override
    public void setContent(ButtonHandler handler) {
        clear();
        Array<TextButton> buttons = handler.getButtons();
        for(int i = 0; i < buttons.size; i++){
            add(buttons.get(i)).width(WIDTH).height(BUTTON_HEIGTH).row();

        }
        setHeight(buttons.size*BUTTON_HEIGTH);
        setY(Y_PADDING, true);

    }
}
