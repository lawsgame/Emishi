package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.Panel;

public abstract class ChoicePanel extends Panel {

    public ChoicePanel(Viewport stageUIViewport) {
        super(stageUIViewport);
    }

    public void update(final ButtonHandler handler){
        final ChoicePanel p = this;
        this.awaitingActions.add(new Action() {
            @Override
            public boolean act(float delta) {
                p.setContent(handler);
                return true;
            }
        });
    }

    public abstract void setContent(ButtonHandler handler);





    //---------------- BUTTON HANDLER CLASSES --------------------------------

    public interface ButtonHandler{
        Array<TextButton> getButtons(Skin skin);
    }

}
