package com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;

public abstract class ActionPanel extends Panel {
    protected ActionChoice choice;

    public ActionPanel(Viewport stageViewport, ActionChoice choice) {
        super(stageViewport);
        this.choice = choice;
    }

    public abstract void set(BattleCommand command);

}
