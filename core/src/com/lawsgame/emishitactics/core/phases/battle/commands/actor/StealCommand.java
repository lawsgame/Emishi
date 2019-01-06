package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Formulas;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask.RendererSubTaskQueue;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class StealCommand extends ActorCommand {

    public StealCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, ActionChoice.STEAL, scheduler, playerInventory);
    }

    @Override
    public boolean isInitiatorValid(int rowActor, int colActor, Unit initiator) {
        return super.isInitiatorValid(rowActor, colActor, initiator) && initiator.has(Data.Ability.STEAL);
    }

    @Override
    protected void execute() {

        // update model
        Unit stealer = bfr.getModel().getUnit(rowActor, colActor);
        Unit stolen = bfr.getModel().getUnit(rowTarget, colTarget);
        Item stoleItem = null;
        int stealRate = getStealRate();
        boolean stealSuccessful = Utils.getMean(1,100) < stealRate;
        if(stealSuccessful)
            stoleItem = stolen.getRandomlyStealableItem();
        stealer.setOrientation(Utils.getOrientationFromCoords(rowActor, colActor, rowTarget, colTarget));
        stealer.setActed(true);

        // push render taks
        StandardTask task = new StandardTask();
        RendererSubTaskQueue stealerRendererThread = new RendererSubTaskQueue(bfr.getUnitRenderer(stealer), stealer.getOrientation());
        stealerRendererThread.addQuery(stealer, Data.AnimId.STEAL);
        RendererSubTaskQueue stolenRendererThread = new RendererSubTaskQueue(bfr.getUnitRenderer(stolen), stealer.getOrientation().getOpposite());
        stolenRendererThread.addQuery(stolen, (stealSuccessful) ? Data.AnimId.WOUNDED : Data.AnimId.DODGE);
        stolenRendererThread.addQuery(stolen, stolen.getOrientation());
        task.addParallelSubTask(stolenRendererThread);
        task.addParallelSubTask(stealerRendererThread);
        scheduleRenderTask(task);

        // se outoome
        if(stealSuccessful){
            outcome.add(stealer, choice.getExperience());
            outcome.add(stoleItem, stealer.belongToAnArmy() && stealer.getArmy().isPlayerControlled());
        }

        handleEvents(this);
    }

    @Override
    public boolean isTargetValid(Unit initiator, int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        return isEnemyTargetValid(initiator, rowActor0, colActor0, rowTarget0, colTarget0, true);
    }

    @Override
    public Array<int[]> getTargetsAtRange(int row, int col, Unit actor) {
        return getFoesAtRange(row, col, actor, true);
    }

    //----------- HELPERS -------------------


    public int getStealRate(){
        return Formulas.getStealRate(rowActor, colActor, rowTarget, colTarget, getInitiator(), getTarget(), bfr.getModel());
    }
}
