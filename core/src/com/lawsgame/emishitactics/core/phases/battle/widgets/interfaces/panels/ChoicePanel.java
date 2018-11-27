package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;

public abstract class ChoicePanel extends Table {
    protected TextButton.TextButtonStyle style;

    public void set(ButtonHandler handler){
        clear();
        setTouchable(Touchable.childrenOnly);
        Array<TextButton> buttons = handler.getButtons(style);
        for(int i = 0; i < buttons.size; i++)
            addButton(buttons.get(i));
        setLayout();
    }

    public abstract void addButton(TextButton button);
    public abstract void setLayout();


    public interface ButtonHandler{
        Array<TextButton> getButtons(TextButton.TextButtonStyle style);
    }
}
