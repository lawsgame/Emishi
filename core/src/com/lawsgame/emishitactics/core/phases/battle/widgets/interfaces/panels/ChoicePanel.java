package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.SlidingPanel;

public abstract class ChoicePanel<C, B extends Button> extends SlidingPanel {

    public ChoicePanel(Viewport stageUIViewport, float slidingDuration, float xShowingPadding, float yPadding, int width, int height, boolean top, boolean left) {
        super(stageUIViewport, slidingDuration, xShowingPadding, yPadding, width, height, top, left);
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

    public abstract B getButtonInstance(C choice);
    public abstract void setContent(ButtonHandler handler);



    //---------------- BUTTON HANDLER CLASSES --------------------------------

    public interface ButtonHandler<B extends Button>{
        Array<B> getButtons();
    }
}
