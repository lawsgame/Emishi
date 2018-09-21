package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Formulas;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask.RendererThread;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class StealCommand extends BattleCommand{

    public StealCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, ActionChoice.STEAL, scheduler, playerInventory, false);
    }

    @Override
    public boolean canbePerformedBy(IUnit actor) {
        return super.canbePerformedBy(actor) && actor.has(Data.Ability.STEAL);
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
        StandardTask task = new StandardTask();
        RendererThread stealerRendererThread = new RendererThread(battlefieldRenderer.getUnitRenderer(stealer), stealer.getOrientation());
        stealerRendererThread.addQuery(stealer, Data.AnimId.STEAL);
        RendererThread stolenRendererThread = new RendererThread(battlefieldRenderer.getUnitRenderer(stolen), stealer.getOrientation().getOpposite());
        stolenRendererThread.addQuery(stolen, (stealSuccessful) ? Data.AnimId.WOUNDED : Data.AnimId.DODGE);
        stolenRendererThread.addQuery(stolen, stolen.getOrientation());
        task.addThread(stolenRendererThread);
        task.addThread(stealerRendererThread);
        scheduleRenderTask(task);

        // setTiles outoome
        if(stealSuccessful){
            outcome.expHolders.add(new ExperiencePointsHolder(stealer, choice.getExperience()));
            outcome.droppedItemHolders.add(new DroppedItemHolder(stoleItem, stealer.isMobilized() && stealer.getArmy().isPlayerControlled()));
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
