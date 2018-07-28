package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Thread;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class HealCommand extends BattleCommand {
    protected boolean launched;

    public HealCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        super(bfr, Data.ActionChoice.HEAL, scheduler, false, false);
    }

    @Override
    public void init() {
        launched = false;
    }

    @Override
    protected void execute() {
        launched = true;

        IUnit healer = battlefield.getUnit(rowActor, colActor);
        IUnit patient = battlefield.getUnit(rowTarget, colTarget);
        BattleUnitRenderer healerRenderer = battlefieldRenderer.getUnitRenderer(healer);
        BattleUnitRenderer patientRenderer = battlefieldRenderer.getUnitRenderer(patient);

        Task task = new Task();

        int healPower = getHealPower(rowActor, colActor, rowTarget, colTarget);
        patient.treated(healPower);

        task.addThread(new Thread(patientRenderer, patientRenderer, healPower));
        task.addThread(new Thread(healerRenderer, healerRenderer, Data.AnimationId.HEAL));
        scheduler.addTask(task);

    }

    @Override
    public boolean isExecuting() {
        return launched && battlefieldRenderer.isExecuting();
    }

    @Override
    public boolean isExecutionCompleted() {
        return launched && !battlefieldRenderer.isExecuting();
    }

    @Override
    public boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        boolean valid = false;
        if(battlefield.isTileOccupied(rowActor0, colActor0)){
            IUnit healer = battlefield.getUnit(rowActor0, colActor0);
            if(battlefield.isTileOccupiedByAlly(rowTarget0, colTarget0, healer.getAllegeance())){
                IUnit patient = battlefield.getUnit(rowTarget0, colTarget0);
                int dist = Utils.dist(rowActor0, colActor0, rowTarget0, colTarget0);
                if(choice.getRangeMin() <= dist && dist <= choice.getRangeMax() && patient.isWounded()) {
                    valid = true;
                }
            }
        }
        return valid;
    }

    @Override
    public boolean atActionRange(int row, int col, IUnit actor) {
        boolean targetAtRange = false;
        int rangeMin = choice.getRangeMin();
        int rangeMax = choice.getRangeMax();
        int dist;
        for(int r = row - rangeMax; r <= row + rangeMax; r++ ){
            for(int c = col - rangeMax; c <= col + rangeMax; c++ ){
                dist = Utils.dist(row, col, r, c);
                if(rangeMin <= dist && dist <= rangeMax && battlefield.isTileOccupiedByAlly(r, c, actor.getAllegeance())){
                    targetAtRange = true;
                    continue;
                }
            }
            if(targetAtRange)
                continue;
        }

        return targetAtRange;
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
