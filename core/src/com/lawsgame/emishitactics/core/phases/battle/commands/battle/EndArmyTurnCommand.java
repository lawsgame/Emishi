package com.lawsgame.emishitactics.core.phases.battle.commands.battle;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class EndArmyTurnCommand extends BattleCommand {
    protected IArmy army;

    public EndArmyTurnCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, IArmy army) {
        super(bfr, scheduler);
        this.army = army;
    }

    @Override
    protected void execute() {
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
                    }
                }
            }
        }
        scheduleRenderTask(resetDoneTask);
    }

    @Override
    public void undo() {

    }

    @Override
    public void redo() {

    }
}
