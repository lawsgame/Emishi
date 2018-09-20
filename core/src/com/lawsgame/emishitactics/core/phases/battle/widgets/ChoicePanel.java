package com.lawsgame.emishitactics.core.phases.battle.widgets;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;

public class ChoicePanel extends Table {

    private ChoicePanelRendering rendering;
    private ButtonHandler handler;

    public ChoicePanel(ChoicePanelRendering rendering, ButtonHandler handler) {
        this.rendering = rendering;
        this.handler = handler;
    }

    public void init(){
        clear();
        setTouchable(Touchable.childrenOnly);
        Array<TextButton> buttons = handler.getButtons();
        for(int i = 0; i < buttons.size; i++)
            rendering.addButton(buttons.get(i));
        rendering.setLayout();
    }


    public interface ChoicePanelRendering {
        void addButton(TextButton button);
        void setLayout();
    }

    public interface ButtonHandler{
        Array<TextButton> getButtons();
    }
}
