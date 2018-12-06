package com.lawsgame.emishitactics.core.phases.battle.widgets.panels.tempo;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.BattleCommandManager;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.interfaces.ChoicePanel;

import java.util.Stack;

public class TempoActionCP extends ChoicePanel.ActionChoicePanel {

    public static int WIDTH = 150;
    public static int BUTTON_HEIGTH = 30;
    public static float X_PADDING = 90f;
    public static float Y_PADDING = 10f;
    public static float SLIDE_DURATION = 0.5f;

    private Skin skin;
    private final CommandChoicePanel commandPanel;


    public TempoActionCP(Viewport uiport, Skin skin, CommandChoicePanel commandPanel) {
        super(uiport, SLIDE_DURATION, X_PADDING, Y_PADDING, WIDTH, 0, true, false);
        this.skin = skin;
        this.commandPanel = commandPanel;
        commandPanel.attach(this);
    }


    @Override
    public void setContent(final int rowActor, final int colActor, final BattleCommandManager bcm, final Stack<ActorCommand> history) {
        final Array<ActionChoice> choices = bcm.getAvailableChoices(rowActor, colActor, history);
        Array<Button> buttons = new Array<Button>();
        Button button;

        // create all buttons and attach the relevant listener to it.

        for(int i = 0; i < choices.size; i++){
            final int buttonIndex = i;
            final ActionChoice choice = choices.get(i);
            button = new ActionButton(choices.get(i), skin);
            buttons.add(button);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Array<ActorCommand> commands = bcm.getAvailableCommands(rowActor, colActor, choice, true);
                    if(commands.size == 1){

                        // notify the result : the panel purpose is fulfilled.
                        notifyReceiver(commands.get(0));
                    }else if(commands.size > 1){


                        commandPanel.setButtonIndex(buttonIndex);
                        commandPanel.setContent(rowActor, colActor, bcm, choice);
                        commandPanel.show();

                    }else{
                        try {
                            throw new PanelException("no content of display for this choice panel : either there is no unit at ("+rowActor+", "+colActor+") OR the selected unit can not perform any action / is done.");
                        }catch (PanelException e){
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        // set the panel layout

        clear();
        setTouchable(Touchable.childrenOnly);
        for(int i = 0; i < buttons.size; i++){
            add(buttons.get(i)).width(WIDTH).height(BUTTON_HEIGTH).row();
        }
        setHeight(buttons.size*BUTTON_HEIGTH);
        updateY();
    }

    @Override
    public void resetPanel(boolean hideCP) {
        setTouchable(Touchable.childrenOnly);
        commandPanel.hide();
        if(hideCP) {
            hide();
        }
    }

    @Override
    public void getChoicePanelNotification(ActorCommand command) {
        notifyReceiver(command);
    }


    //----------------- ACTION BUTTON ---------------------------------

    public static class ActionButton extends TextButton {

        public ActionButton(ActionChoice choice, Skin skin) {
            super(choice.getName(), skin, "commandpan");
        }
    }
}
