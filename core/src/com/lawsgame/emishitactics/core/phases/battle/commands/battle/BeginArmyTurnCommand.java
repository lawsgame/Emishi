package com.lawsgame.emishitactics.core.phases.battle.commands.battle;

import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class BeginArmyTurnCommand extends BattleCommand {
    protected IArmy army;

    public BeginArmyTurnCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, IArmy army) {
        super(bfr, scheduler);
        this.army = army;
    }

    @Override
    protected void execute() {
        if(army != null) {
            army.replenishMoral(true);
            army.updateActionPoints();
            if(army.isPlayerControlled())
                battlefield.incrementTurn();
        }
    }

    @Override
    public void undo() {

    }

    @Override
    public void redo() {

    }
}
