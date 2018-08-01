package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Thread;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class HealCommand extends BattleCommand {

    public HealCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        super(bfr, Data.ActionChoice.HEAL, scheduler, false, false);
    }


    @Override
    protected void execute() {

        IUnit healer = battlefield.getUnit(rowActor, colActor);
        IUnit patient = battlefield.getUnit(rowTarget, colTarget);

        int healPower = getHealPower(rowActor, colActor, rowTarget, colTarget);
        patient.treated(healPower);
        outcome.receivers.add(healer);
        outcome.experienceGained.add(choice.getExperience());

        Task task = new Task();
        task.addThread(new Thread(battlefieldRenderer.getUnitRenderer(patient), healPower));
        task.addThread(new Thread(battlefieldRenderer.getUnitRenderer(healer), Data.AnimationId.HEAL));
        scheduler.addTask(task);

        healer.setActed(true);

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


    public int getHealPower(int rowActor, int colActor, int rowTarget, int colTarget) {
        int healPower = 0;
        if(battlefield.isTileOccupied(rowActor, colActor)){
            IUnit healer = battlefield.getUnit(rowActor, colActor);
            healPower +=Data.HEAL_BASE_POWER + healer.getLevel()/2;
        }
        return healPower;
    }

}
