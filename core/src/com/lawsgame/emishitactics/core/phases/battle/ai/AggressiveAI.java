package com.lawsgame.emishitactics.core.phases.battle.ai;

import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.EndUnitTurnCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.ActionPanelPool;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class AggressiveAI extends PassiveAI{

    public AggressiveAI(BattlefieldRenderer bfr, AnimationScheduler scheduler, ActionPanelPool app, Inventory playerInventory) {
        super(bfr, scheduler, app, playerInventory);
    }

    @Override
    public CommandBundle getCommandPackage(int[] actorPos) {
        CommandBundle bundle = new CommandBundle();
        if(actorPos != null) {

            //ActorCommand command = new AttackCommand(bfr, scheduler, playerInventory);




            EndUnitTurnCommand endUnitTurnCommand = new EndUnitTurnCommand(bfr, scheduler, playerInventory);
            if(endUnitTurnCommand.setInitiator(actorPos[0], actorPos[1])){
                if(endUnitTurnCommand.isTargetValid()){
                    endUnitTurnCommand.setDecoupled(true);
                    endUnitTurnCommand.apply();
                    bundle.commands.add(endUnitTurnCommand);
                    bundle.panels.add(app.getPanel(endUnitTurnCommand));
                }
            }
        }
        return bundle;
    }
}
