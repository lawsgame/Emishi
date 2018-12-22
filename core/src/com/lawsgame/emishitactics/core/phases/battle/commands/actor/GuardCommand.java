package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Area.UnitArea;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class GuardCommand extends SelfInflitedCommand {

    public GuardCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, ActionChoice.GUARD, scheduler, playerInventory, false);
    }


    @Override
    public boolean isInitiatorValid(int rowActor, int colActor, Unit initiator) {
        return super.isInitiatorValid(rowActor, colActor, initiator) && initiator.has(Data.Ability.GUARD);
    }

    @Override
    protected void execute() {

        // update model
        UnitArea area = createGuardedArea(bfr.getModel(), rowActor, colActor, getInitiator());
        bfr.getModel().addUnitArea(area);


        // push render task
        StandardTask task = new StandardTask();
        task.addParallelSubTask(new StandardTask.RendererSubTaskQueue(bfr, area));
        task.addParallelSubTask(new StandardTask.RendererSubTaskQueue(bfr.getUnitRenderer(getInitiator()), Data.AnimId.GUARD));
        // animated guarded units as well
        Unit guardedUnit;
        int dist;
        int rangeMin = Data.GUARD_REACTION_RANGE_MIN;
        int rangeMax = Data.GUARD_REACTION_RANGE_MAX;
        for(int r = rowActor - rangeMax; r <= rowActor + rangeMax; r++){
            for(int c = colActor - rangeMax; c <= colActor + rangeMax; c++){
                dist = Utils.dist(rowActor, colActor, r, c);
                if(rangeMin <= dist && dist <= rangeMax && bfr.getModel().isTileOccupiedByAlly(r,c, getInitiator().getArmy().getAffiliation())){
                    guardedUnit = bfr.getModel().getUnit(r,c);
                    task.addParallelSubTask(new StandardTask.RendererSubTaskQueue(bfr.getUnitRenderer(guardedUnit), Data.AnimId.GUARDED));
                }
            }
        }
        scheduleRenderTask(task);


        //outcome
        outcome.add(getInitiator(), choice.getExperience());

        handleEvents(this);
    }

    @Override
    public void unexecute() {
        if(getInitiator() != null){
            Array<UnitArea> areas = bfr.getModel().removeUnitAreas(getInitiator(), Data.AreaType.GUARD_AREA, false);
            for(int i = 0; i < areas.size; i++) {
                scheduleRenderTask(new StandardTask(bfr, areas.get(i)));
            }
        }
    }

    public static UnitArea createGuardedArea(Battlefield bf, int rowActor, int colActor, Unit actor){
        return new UnitArea(bf, Data.AreaType.GUARD_AREA, Utils.getEreaFromRange(bf, rowActor, colActor, Data.GUARD_REACTION_RANGE_MIN, Data.GUARD_REACTION_RANGE_MAX), actor, true);
    }
}
