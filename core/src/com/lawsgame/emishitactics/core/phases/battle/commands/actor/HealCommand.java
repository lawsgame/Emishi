package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Formulas;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask.RendererThread;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class HealCommand extends ActorCommand {

    public HealCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, ActionChoice.HEAL, scheduler, playerInventory, false);
    }

    @Override
    public boolean isInitiatorValid(int rowActor, int colActor, Unit initiator) {
        return super.isInitiatorValid(rowActor, colActor, initiator) && initiator.has(Data.Ability.HEAL);
    }

    @Override
    protected void execute() {

        // update model
        int healPower = Formulas.getHealPower(rowActor, colActor, rowTarget, colTarget, getInitiator(), getTarget(), bfr.getModel());
        boolean treated = getTarget().treated(healPower);

        // push render task
        StandardTask task = new StandardTask();
        if(treated) task.addThread(new RendererThread(bfr.getUnitRenderer(getTarget()), getTarget(), healPower));
        task.addThread(new RendererThread(bfr.getUnitRenderer(getInitiator()), Data.AnimId.HEAL));
        scheduleRenderTask(task);

        // set outcome
        outcome.add(getInitiator(), choice.getExperience());


    }

    @Override
    public boolean isTargetValid(Unit initiator, int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        return isTargetAllyValid(initiator, rowActor0, colActor0, rowTarget0, colTarget0, true, false);
    }

    @Override
    public Array<int[]> getTargetsAtRange(int row, int col, Unit actor) {
        return getAlliesAtRange(row, col, actor, true, false);
    }



    //-------------------- GETTERS & SETTERS ----------------------

    public int getHealPower(){
        return Formulas.getHealPower(rowActor, colActor, rowTarget, colTarget, getInitiator(), getTarget(), bfr.getModel());
    }

    public int getRecoveredHitPoints(){
        int treatedHP = 0;
        if(bfr.getModel().isTileOccupied(rowTarget, colTarget))
            treatedHP = bfr.getModel().getUnit(rowTarget, colTarget).getRecoveredHitPoints(getHealPower());
        return treatedHP;
    }

    public int getRecoveredMoralPoints(){
        int moralPoints = 0;
        if(bfr.getModel().isTileOccupied(rowTarget, colTarget))
            moralPoints = bfr.getModel().getUnit(rowTarget, colTarget).getRecoveredMoralPoints(getHealPower());
        return moralPoints;
    }



}
