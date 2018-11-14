package com.lawsgame.emishitactics.core.phases.battle.commands.battle;

import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification.BeginArmyTurn;
import com.lawsgame.emishitactics.core.models.Player;
import com.lawsgame.emishitactics.core.models.interfaces.MilitaryForce;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class BeginArmyTurnCommand extends BattleCommand {
    protected MilitaryForce army;

    public BeginArmyTurnCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, MilitaryForce army) {
        super(bfr, scheduler, playerInventory);
        this.army = army;
    }


    @Override
    public boolean isApplicable() {
        return true;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    protected void execute() {
        if (army != null) {
            army.replenishMoral(true);
            army.updateActionPoints();
            if (army.isPlayerControlled())
                bfr.getModel().incrementTurn();

            handleEvents(new BeginArmyTurn(army), -1, -1);
        }
    }

    @Override
    protected void unexecute() {

    }

}
