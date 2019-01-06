package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.exceptions.BattlefieldException;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.ChooseOrientationCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.EndUnitTurnCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.TileHighlighter;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;

public class EndUnitTurnBIS extends BattleInteractionState implements Observer {
    private Unit actor;
    private EndUnitTurnCommand endUnitTurnCommand;

    public EndUnitTurnBIS(BattleInteractionMachine bim, Unit actor) {
        super(bim, true, true, true, false, true);
        this.actor = actor;

        this.endUnitTurnCommand = new EndUnitTurnCommand(bim.bfr, bim.scheduler, bim.player.getInventory(), bim.thl);
    }

    @Override
    public void init() {
        System.out.println("END TURN of "+actor.getName());

        super.init();
        if(actor.isOutOfAction()){

            proceed();
        }else {
            bim.windroseImp.initialize(bim.bfr.getUnitRenderer(actor));
            bim.windroseImp.attach(this);
            if(bim.bfr.getModel().isUnitDeployed(actor)) {
                int[] actorPos = bim.bfr.getModel().getUnitPos(actor);
                bim.focusOn(actorPos[0], actorPos[1], true, true, true, TileHighlighter.SltdUpdateMode.MATCH_TOUCHED_TILE, true);
            }

        }
    }

    @Override
    public void end() {
        super.end();
        bim.windroseImp.detach(this);
        bim.windroseImp.setEnable(false);
        bim.windroseImp.setVisible(false);
    }

    @Override
    public void renderAhead(SpriteBatch batch) {
        bim.windroseImp.render(batch);
    }

    @Override
    public boolean handleTouchInput(float xTouch, float yTouch) {
        return bim.windroseImp.handleInput(xTouch, yTouch);
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        return true;
    }

    private void proceed(){
        if (!actor.isOutOfAction()) {
            int[] actorPos = bim.bfr.getModel().getUnitPos(actor);
            if(actorPos != null) {
                this.endUnitTurnCommand.apply(actorPos[0], actorPos[1]);
            }else{
                try {
                    throw new BattlefieldException("actor not OOA yet not deployed!");
                }catch (BattlefieldException e){
                    e.printStackTrace();
                }
            }
        }
        if (bim.bfr.getModel().getTurnSolver().getCurrentArmy().isDone()) {
            bim.replace(new EndArmyTurnBIS(bim));
        } else {
            bim.replace(new SelectActorBIS(bim, false));
        }

    }

    @Override
    public void getNotification(Observable sender, Object data) {
        if(sender  == bim.windroseImp && data instanceof Data.Orientation){
            //update actor orientation
            ChooseOrientationCommand orientationCommand = new ChooseOrientationCommand(bim.bfr, bim.scheduler, bim.player.getInventory(), (Data.Orientation) data);
            int[] actorPos = bim.bfr.getModel().getUnitPos(actor);
            orientationCommand.apply(actorPos[0], actorPos[1]);
            //proceed to the next BIS
            proceed();
        }
    }
}
