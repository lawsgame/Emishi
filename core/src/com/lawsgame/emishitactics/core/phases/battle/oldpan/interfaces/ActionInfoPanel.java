package com.lawsgame.emishitactics.core.phases.battle.oldpan.interfaces;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.TPanel;

public abstract class ActionInfoPanel extends TPanel {
    protected ActionChoice choice;

    public ActionInfoPanel(Viewport stageViewport, ActionChoice choice) {
        super(stageViewport);
        this.choice = choice;
    }

    public abstract void set(ActorCommand command);

}
