package com.lawsgame.emishitactics.core.states.commands;

import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.engine.GameUpdatableEntity;
import com.lawsgame.emishitactics.engine.patterns.command.Command;

public interface BattleCommand extends Command, GameUpdatableEntity{

    boolean isUndoable();
    boolean atActionRange(Battlefield bf, Unit actor);
}
