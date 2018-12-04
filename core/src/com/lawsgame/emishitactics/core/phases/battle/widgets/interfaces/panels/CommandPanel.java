package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;


public abstract class CommandPanel<B extends Button> extends ChoicePanel<ActorCommand, B>{
    public CommandPanel(Viewport stageUIViewport, float slidingDuration, float xShowingPadding, float yPadding, int width, int height, boolean top, boolean left) {
        super(stageUIViewport, slidingDuration, xShowingPadding, yPadding, width, height, top, left);
    }

    public abstract void setButtonIndex(int buttonIndex);

}
