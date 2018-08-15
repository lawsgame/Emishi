package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler.Thread;
import com.lawsgame.emishitactics.core.models.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Formulas;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class StealCommand extends BattleCommand{

    public StealCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        super(bfr, ActionChoice.STEAL, scheduler, false, true, false);
    }

    @Override
    protected void execute() {

        // update model
        IUnit stealer = battlefield.getUnit(rowActor, colActor);
        IUnit stolen = battlefield.getUnit(rowTarget, colTarget);
        Item stoleItem = null;
        int stealRate = getStealRate();
        boolean stealSuccessful = Utils.getMean(1,100) < stealRate;
        if(stealSuccessful)
            stoleItem = stolen.getRandomlyStealableItem();
        stealer.setOrientation(Utils.getOrientationFromCoords(rowActor, colActor, rowTarget, colTarget));
        stealer.setActed(true);

        // push render taks
        Task task = new Task();
        Thread stealerThread = new Thread(battlefieldRenderer.getUnitRenderer(stealer), stealer.getOrientation());
        stealerThread.addQuery(stealer, Data.AnimationId.STEAL);
        Thread stolenThread = new Thread(battlefieldRenderer.getUnitRenderer(stolen), stealer.getOrientation().getOpposite());
        stolenThread.addQuery(stolen, (stealSuccessful) ? Data.AnimationId.TAKE_HIT : Data.AnimationId.DODGE);
        stolenThread.addQuery(stolen, stolen.getOrientation());
        task.addThread(stolenThread);
        task.addThread(stealerThread);
        scheduler.addTask(task);

        // set outoome
        if(stealSuccessful){
            outcome.receivers.add(stealer);
            outcome.experienceGained.add(choice.getExperience());
            outcome.droppedItems.add(stoleItem);
        }

    }

    @Override
    public boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        return isEnemyTargetValid(rowActor0, colActor0, rowTarget0, colTarget0, false);
    }

    @Override
    public boolean atActionRange(int row, int col, IUnit actor) {
        return isEnemyAtActionRange(row, col, actor, false);
    }

    //----------- HELPERS -------------------


    public int getStealRate(){
        return Formulas.getStealRate(rowActor, colActor, rowTarget, colTarget, battlefield);
    }
}
