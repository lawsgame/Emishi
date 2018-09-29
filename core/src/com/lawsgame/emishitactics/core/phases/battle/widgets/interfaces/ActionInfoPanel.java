package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.ActorCommand;

public abstract class ActionInfoPanel extends Panel {
    protected ActionChoice choice;

    public ActionInfoPanel(Viewport stageViewport, ActionChoice choice) {
        super(stageViewport);
        this.choice = choice;
    }

    public abstract void set(ActorCommand command);

}
