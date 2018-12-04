package com.lawsgame.emishitactics.core.phases.battle.widgets.tempo;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels.ChoicePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels.CommandPanel;

public class TempoCommandPanel extends CommandPanel {
    public static float X_PADDING = 15f;
    public static float Y_REL_PADDING = 10f;
    public static float SLIDE_DURATION = 0.5f;

    private Skin skin;
    private float yPadding;

    public TempoCommandPanel(Viewport uiport, Skin skin) {
        super(uiport, SLIDE_DURATION, X_PADDING, Y_REL_PADDING, TempoActionChoicePanel.WIDTH, 0, true, false);
        this.skin = skin;
        this.yPadding = Y_REL_PADDING;
    }

    @Override
    public TextButton getButtonInstance(ActorCommand choice) {

        return null;
    }


    @Override
    public void setContent(ChoicePanel.ButtonHandler handler) {
        clear();
        Array<TextButton> buttons = handler.getButtons();
        for(int i = 0; i < buttons.size; i++){
            add(buttons.get(i)).width(TempoActionChoicePanel.WIDTH).height(TempoActionChoicePanel.BUTTON_HEIGTH).row();
        }
        setHeight(buttons.size*TempoActionChoicePanel.BUTTON_HEIGTH);
        setY(yPadding, true);

    }


    /**
     * must be called before setContent !!
     *
     * @param buttonIndex : index of the button which trigger the appearance of the command panel
     */
    @Override
    public void setButtonIndex(int buttonIndex) {
        this.yPadding = TempoActionChoicePanel.Y_PADDING + TempoActionChoicePanel.BUTTON_HEIGTH * buttonIndex + Y_REL_PADDING;
    }





}
