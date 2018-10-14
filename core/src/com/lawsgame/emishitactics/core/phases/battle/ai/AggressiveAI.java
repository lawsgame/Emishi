package com.lawsgame.emishitactics.core.phases.battle.ai;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.AttackCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.EndUnitTurnCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.MoveCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.ActionPanelPool;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class AggressiveAI extends PassiveAI {

    public AggressiveAI(BattlefieldRenderer bfr, AnimationScheduler scheduler, ActionPanelPool app, Inventory playerInventory) {
        super(bfr, scheduler, app, playerInventory);
    }

    @Override
    public CommandBundle getCommandPackage(int[] actorPos) {
        CommandBundle bundle = new CommandBundle();
        if(actorPos != null) {

            int rowEndTile = actorPos[0];
            int colEndTile = actorPos[1];
            boolean attackPerformed = false;
            AttackCommand attackCommand = new AttackCommand(bfr, scheduler, playerInventory);
            MoveCommand moveCommand = new MoveCommand(bfr, scheduler, playerInventory);
            EndUnitTurnCommand endUnitTurnCommand = new EndUnitTurnCommand(bfr, scheduler, playerInventory);

            int[] attackTarget;
            Array<int[]> attackTargetsAtRange;
            if(attackCommand.setInitiator(actorPos[0], actorPos[1])) {
                attackTargetsAtRange = attackCommand.getTargetsAtRange();
                if (attackTargetsAtRange.size > 0) {
                    attackTarget = attackTargetsAtRange.random();
                    attackPerformed = applyAndStore(attackCommand,  attackTarget[0], attackTarget[1], bundle);
                }
            }

            if(!attackPerformed){
                int[] moveTarget;
                Array<int[]> moveTargetsAtRange;
                if(moveCommand.setInitiator(actorPos[0], actorPos[1])){

                    moveTargetsAtRange = moveCommand.getTargetsAtRange();
                    for(int i = 0; i < moveTargetsAtRange.size ; i++){

                        moveTarget = moveTargetsAtRange.get(i);
                        attackTargetsAtRange = attackCommand.getTargetsAtRange(moveTarget[0], moveTarget[1], attackCommand.getInitiator());
                        if(attackTargetsAtRange.size > 0){

                            attackTarget = attackTargetsAtRange.random();
                            if(applyAndStore(moveCommand, moveTarget[0], moveTarget[1], bundle)){

                                rowEndTile = moveTarget[0];
                                colEndTile = moveTarget[1];
                                attackCommand.setInitiator(rowEndTile, colEndTile);
                                if(!applyAndStore(attackCommand, attackTarget[0], attackTarget[1], bundle)){
                                    moveCommand.undo();
                                }
                            }
                        }
                    }
                }
            }

            if(endUnitTurnCommand.setInitiator(rowEndTile, colEndTile)){
                applyAndStore(endUnitTurnCommand, bundle);
            }

            //System.out.println(bundle);

        }
        return bundle;
    }
}
