package com.lawsgame.emishitactics.core.phases.battle.ai;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.AttackCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.EndUnitTurnCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.WalkCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.ActionPanelPool;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class AggressiveAI extends PassiveAI {

    public AggressiveAI(BattlefieldRenderer bfr, AnimationScheduler scheduler, ActionPanelPool app, Inventory playerInventory) {
        super(bfr, scheduler, app, playerInventory);
    }

    @Override
    public void setCommandBundle(int[] actorPos, final CommandBundle bundle) {

        if(actorPos != null) {

            int rowEndTile = actorPos[0];
            int colEndTile = actorPos[1];
            Unit actor = bfr.getModel().getUnit(actorPos[0], actorPos[1]);
            boolean attackPerformed = false;
            AttackCommand attackCommand = new AttackCommand(bfr, scheduler, playerInventory);
            WalkCommand walkCommand = new WalkCommand(bfr, scheduler, playerInventory);
            EndUnitTurnCommand endUnitTurnCommand = new EndUnitTurnCommand(bfr, scheduler, playerInventory);

            System.out.println("\n____________________________________________________________________");
            System.out.println("      > AggressiveAI.setCommandBundle() with ACTOR : "+actor.getName());

            // foe at range without moving
            int[] attackTarget;
            Array<int[]> attackTargetsAtRange;
            attackCommand.setInitiator(rowEndTile, colEndTile);
            if(attackCommand.isInitiatorValid()) {
                attackTargetsAtRange = attackCommand.getTargetsAtRange();
                if (attackTargetsAtRange.size > 0) {
                    attackTarget = attackTargetsAtRange.random();
                    attackPerformed = checkApplyAndStore(attackCommand,  attackTarget[0], attackTarget[1], bundle);
                    System.out.println("                    attack performed without moving");

                }
            }

            //seek for foes
            walkCommand.setInitiator(rowEndTile, colEndTile);
            if(!attackPerformed && walkCommand.isInitiatorValid()){

                System.out.println("            walkCommand initiator valid ? : "+walkCommand.isInitiatorValid());

                int[] moveTarget;
                Array<int[]> moveTargetsAtRange = walkCommand.getTargetsAtRange();
                loop :
                {
                    while (moveTargetsAtRange.size > 0) {
                        moveTarget = moveTargetsAtRange.random();
                        moveTargetsAtRange.removeValue(moveTarget, true);


                        // for a given destination {moveTarget} check if a foe can be targeted from there
                        attackTargetsAtRange = attackCommand.getTargetsAtRange(moveTarget[0], moveTarget[1], actor);

                        if (attackTargetsAtRange.size > 0) {
                            // target found
                            attackTarget = attackTargetsAtRange.random();

                            System.out.println("                interesting MOVE TILE: " + moveTarget[0] + " " + moveTarget[1]);
                            System.out.println("                    1) actor valid ? : "+walkCommand.isInitiatorValid(actorPos[0], actorPos[1], actor));
                            System.out.println("                    2) target valid ? : "+walkCommand.isTargetValid(actor, actorPos[0], actorPos[1], moveTarget[0], moveTarget[1]));
                            System.out.println("                    TARGET TILE: " + attackTarget[0] + " " + attackTarget[1]);
                            System.out.println("                        1) actor valid ? : "+attackCommand.isInitiatorValid(actorPos[0], actorPos[1], actor));
                            System.out.println("                        2) target valid ? : "+attackCommand.isTargetValid(actor, moveTarget[0], moveTarget[1], attackTarget[0], attackTarget[1]));

                            // apply
                            if (checkApplyAndStore(walkCommand, moveTarget[0], moveTarget[1], bundle)) {
                                rowEndTile = moveTarget[0];
                                colEndTile = moveTarget[1];
                                attackCommand.setInitiator(rowEndTile, colEndTile);
                                checkApplyAndStore(attackCommand, attackTarget[0], attackTarget[1], bundle);
                            }

                            break;

                        }
                    }
                }
            }

            endUnitTurnCommand.setInitiator(rowEndTile, colEndTile);
            checkApplyAndStore(endUnitTurnCommand, rowEndTile, colEndTile, bundle);


            System.out.println("\n         > AggresiveAI.setCommandBundle() : result:");
            System.out.println(bundle.toString());
        }
    }
}
