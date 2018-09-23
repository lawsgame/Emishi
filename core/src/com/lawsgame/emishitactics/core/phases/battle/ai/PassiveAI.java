package com.lawsgame.emishitactics.core.phases.battle.ai;

import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.phases.battle.ai.interfaces.AI;
import com.lawsgame.emishitactics.core.phases.battle.commands.EndTurnCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.ActionPanelPool;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.TurnManager;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class PassiveAI extends AI {


    public PassiveAI(BattlefieldRenderer bfr, AnimationScheduler scheduler, ActionPanelPool app, Inventory playerInventory, TurnManager tm) {
        super(bfr, scheduler, app, playerInventory, tm);
    }

    @Override
    public int[] nextUnit(IArmy army) {
        return bfr.getModel().getStillActiveUnitCoords(army.getId()).random();
    }

    @Override
    public CommandBundle getCommandPackage(int[] actorPos) {
        CommandBundle bundle = new CommandBundle();
        if(actorPos != null) {
            EndTurnCommand endTurnCommand = new EndTurnCommand(bfr, scheduler, playerInventory);
            if(endTurnCommand.setInitiator(actorPos[0], actorPos[1])){
                if(endTurnCommand.isTargetValid()){
                    endTurnCommand.setDecoupled(true);
                    endTurnCommand.apply();
                    bundle.commands.add(endTurnCommand);
                    bundle.panels.add(app.getPanel(endTurnCommand));
                }
            }
        }
        return bundle;
    }
}
