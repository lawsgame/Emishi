package com.lawsgame.emishitactics.core.phases.battle.ai;

import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.interfaces.MilitaryForce;
import com.lawsgame.emishitactics.core.phases.battle.ai.interfaces.AI;
import com.lawsgame.emishitactics.core.phases.battle.helpers.ActionPanelPool;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.patterns.command.Command;

public class SimpleAI extends AI {

    public SimpleAI(BattlefieldRenderer bfr, AnimationScheduler scheduler, ActionPanelPool app, Inventory playerInventory) {
        super(bfr, scheduler, app, playerInventory, bfr.getModel().getCurrentArmy());
    }

    @Override
    protected void prepare(MilitaryForce army) {

    }

    @Override
    public int[] nextUnit(MilitaryForce army) {
        return new int[2];
    }

    @Override
    public void setCommandBundle(int[] actorPos, final CommandBundle bundle) {
        if(actorPos != null && bfr.getModel().isTileOccupied(actorPos[0], actorPos[1])) {

            //ActorCommand command = new AttackCommand(bfr, scheduler, playerInventory);




            /*
            EndUnitTurnCommand endUnitTurnCommand = new EndUnitTurnCommand(bfr, scheduler, playerInventory);
            if(endUnitTurnCommand.setInitiator(actorPos[0], actorPos[1])){
                if(endUnitTurnCommand.isTargetValid()){
                    endUnitTurnCommand.setDecoupled(true);
                    endUnitTurnCommand.apply();
                    bundle.commands.add(endUnitTurnCommand);
                    bundle.panels.add(app.getPanel(endUnitTurnCommand));
                }
            }
            */
        }
    }


}
