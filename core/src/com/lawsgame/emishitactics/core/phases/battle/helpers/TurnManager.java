package com.lawsgame.emishitactics.core.phases.battle.helpers;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class TurnManager {
    private BattlefieldRenderer bfr;
    private AnimationScheduler scheduler;

    public TurnManager(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        this.bfr = bfr;
        this.scheduler = scheduler;
    }

    public void beginTurn(IArmy army) {
        army.replenishMoral();
        army.updateActionPoints();
    }

    public void endTurn(IArmy army) {
        //update model
        army.setDone(false, false);

        //push render tasks
        Array<Array<IUnit>> mobilizedTroops = army.getAllSquads();
        StandardTask resetDoneTask = new StandardTask();
        StandardTask.RendererThread doneThread;
        BattleUnitRenderer bur;
        for(int i = 0; i < mobilizedTroops.size; i++){
            for(int j = 0; j < mobilizedTroops.get(i).size; j++){
                bur = bfr.getUnitRenderer(mobilizedTroops.get(i).get(j));
                if(!mobilizedTroops.get(i).get(j).isOutOfAction()) {
                    if(bur != null) {
                        doneThread = new StandardTask.RendererThread(bur, Notification.Done.get(false));
                        resetDoneTask.addThread(doneThread);
                    }else{
                        try {
                            throw new BattleInteractionState.BISException("no BattleUnitRenderer related to the following still active unit: "+mobilizedTroops.get(i).get(j).getName());
                        } catch (BattleInteractionState.BISException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        scheduler.addTask(resetDoneTask);
    }
}
