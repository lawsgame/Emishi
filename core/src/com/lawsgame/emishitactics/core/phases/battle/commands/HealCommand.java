package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask.RendererThread;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class HealCommand extends BattleCommand {

    public HealCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        super(bfr, ActionChoice.HEAL, scheduler, false);
    }

    @Override
    public boolean canbePerformedBy(IUnit actor) {
        return super.canbePerformedBy(actor) && actor.has(Data.Ability.HEAL);
    }

    @Override
    protected void execute() {

        // update model
        int healPower = getHealPower(rowActor, colActor, rowTarget, colTarget);
        getActor().treated(healPower);

        // push render task
        StandardTask task = new StandardTask();
        task.addThread(new RendererThread(battlefieldRenderer.getUnitRenderer(getTarget()), getTarget(), healPower));
        task.addThread(new RendererThread(battlefieldRenderer.getUnitRenderer(getActor()), Data.AnimationId.HEAL));
        scheduleRenderTask(task);

        // setTiles outcome
        outcome.receivers.add(getActor());
        outcome.experienceGained.add(choice.getExperience());


    }

    @Override
    public boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        return isTargetAllyValid(rowActor0, colActor0, rowTarget0, colTarget0, true);
    }

    @Override
    public boolean atActionRange(int row, int col, IUnit actor) {
        return isAllyAtActionRange(row, col, actor, true);
    }


    //-------------------- GETTERS & SETTERS ----------------------

    public int getHealPower(){
        return getHealPower(rowActor, colActor, rowTarget, colTarget);
    }

    public int getHealPower(int rowActor, int colActor, int rowTarget, int colTarget) {
        int healPower = 0;
        if(battlefield.isTileOccupied(rowActor, colActor)){
            IUnit healer = battlefield.getUnit(rowActor, colActor);
            healPower +=Data.HEAL_BASE_POWER + healer.getLevel()/2;
        }
        return healPower;
    }

    public int getRecoveredHitPoints(){
        int treatedHP = 0;
        if(battlefield.isTileOccupied(rowTarget, colTarget))
            treatedHP = battlefield.getUnit(rowTarget, colTarget).getRecoveredHitPoints(getHealPower());
        return treatedHP;
    }

    public int getRecoveredMoralPoints(){
        int moralPoints = 0;
        if(battlefield.isTileOccupied(rowTarget, colTarget))
            moralPoints = battlefield.getUnit(rowTarget, colTarget).getRecoveredMoralPoints(getHealPower());
        return moralPoints;
    }

}
