package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Thread;
import com.lawsgame.emishitactics.core.models.Area.UnitArea;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.StandCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class GuardCommand extends StandCommand {

    protected IUnit actor;

    public GuardCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        super(bfr, Data.ActionChoice.GUARD, scheduler, true, false);
    }

    @Override
    protected void execute() {
        actor = battlefield.getUnit(rowActor, colActor);
        UnitArea area = battlefield.addGuardedArea(rowActor, colActor);

        Task task = new Task();
        task.addThread(new Thread(battlefieldRenderer, area));
        task.addThread(new Thread(battlefieldRenderer.getUnitRenderer(actor), Data.AnimationId.GUARD));
        IUnit guardedUnit;
        int dist;
        int rangeMin = Data.GUARD_REACTION_RANGE_MIN;
        int rangeMax = Data.GUARD_REACTION_RANGE_MAX;
        for(int r = rowActor - rangeMax; r <= rowActor + rangeMax; r++){
            for(int c = colActor - rangeMax; c <= colActor + rangeMax; c++){
                dist = Utils.dist(rowActor, colActor, r, c);
                if(rangeMin <= dist && dist <= rangeMax && battlefield.isTileOccupiedByAlly(r,c, actor.getAllegeance())){
                    guardedUnit = battlefield.getUnit(r,c);
                    task.addThread(new Thread(battlefieldRenderer.getUnitRenderer(guardedUnit), Data.AnimationId.GUARDED));
                }
            }
        }
        scheduler.addTask(task);

        actor.setActed(true);
    }

    @Override
    public void undo() {
        if(actor != null){
            UnitArea area = battlefield.removeGuardedArea(actor);
            if(area != null)
                scheduler.addTask(new Task(battlefieldRenderer, area));
        }
    }
}
