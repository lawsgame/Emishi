package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
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
            army.replenishMoral();
            army.updateActionPoints();
        }
    }

    @Override
    public void undo() {

    }

    @Override
    public void redo() {

    }
}
