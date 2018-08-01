package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Thread;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Area.UnitArea;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.StandCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class CoverCommand extends StandCommand {
    protected IUnit actor;

    public CoverCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        super(bfr, Data.ActionChoice.COVER, scheduler);
    }

    @Override
    protected void execute() {
        actor = battlefield.getUnit(rowActor, colActor);
        UnitArea area = battlefield.addCoveredArea(rowActor, colActor);

        Task task = new Task();
        task.addThread(new Thread(battlefieldRenderer, area));
        task.addThread(new Thread(battlefieldRenderer.getUnitRenderer(actor), Data.AnimationId.COVER));
        scheduler.addTask(task);

        actor.setActed(true);
    }

    @Override
    public void undo() {
        if(actor != null){
            UnitArea area = battlefield.removeCoveredArea(actor);
            if(area != null)
                scheduler.addTask(new Task(battlefieldRenderer, area));
        }
    }
}
