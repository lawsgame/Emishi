package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.battle.EndArmyTurnCommand;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;

public class EndArmyTurnBIS extends BattleInteractionState implements Observer {
    protected EndArmyTurnCommand endArmyTurnCommand;

    public EndArmyTurnBIS(BattleInteractionMachine bim) {
        super(bim, true, false, false);
        endArmyTurnCommand = new EndArmyTurnCommand(bim.bfr, bim.scheduler, bim.battlefield.getCurrentArmy());
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        return true;
    }

    @Override
    public void init() {
        System.out.println("EndTurnArmy BIS");

        endArmyTurnCommand.attach(this);
        endArmyTurnCommand.apply();
    }


    @Override
    public void getNotification(Observable sender, Object data) {
        if(sender instanceof BattleCommand && sender == data){
            endArmyTurnCommand.detach(this);

            //System.out.println(bim.battlefield.getCurrentArmy().toShortString());

            bim.battlefield.nextArmy();

            //System.out.println(bim.battlefield.getCurrentArmy().toShortString());

            if(bim.battlefield.getCurrentArmy().isPlayerControlled()){
                bim.replace(new SelectActorBIS(bim, true));
            }else {
                bim.replace(new AiBIS(bim));
            }
        }
    }
}
