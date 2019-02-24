package com.lawsgame.emishitactics.core.phases.battle.ai;

import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.interfaces.MilitaryForce;
import com.lawsgame.emishitactics.core.phases.battle.ai.interfaces.AI;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.EndUnitTurnCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.PanelPool;
import com.lawsgame.emishitactics.core.phases.battle.helpers.TileHighlighter;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class PassiveAI extends AI {
    protected TileHighlighter thl;

    public PassiveAI(BattlefieldRenderer bfr, AnimationScheduler scheduler, PanelPool ph, Inventory playerInventory, TileHighlighter thl) {
        super(bfr, scheduler, ph, playerInventory, bfr.getModel().getBattleTurnManager().getCurrentArmy());
        this.thl = thl;
    }

    @Override
    protected void prepare(MilitaryForce army) {

    }

    @Override
    public int[] nextUnit(MilitaryForce army) {
        return bfr.getModel().getUnitsPos(army, true, true).random();
    }

    @Override
    public void setCommandBundle(int[] actorPos, final CommandBundle bundle) {

        if(actorPos != null) {
            EndUnitTurnCommand endUnitTurnCommand = new EndUnitTurnCommand(bfr, scheduler, playerInventory, thl);
            endUnitTurnCommand.setInitiator(actorPos[0], actorPos[1]);
            checkApplyAndStore(endUnitTurnCommand, actorPos[0], actorPos[1], bundle);
        }
    }


}
