package com.lawsgame.emishitactics.core.phases.battle.ai;

import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.phases.battle.commands.EndTurnCommand;
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
