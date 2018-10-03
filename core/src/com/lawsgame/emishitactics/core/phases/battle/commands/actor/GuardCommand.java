package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Area.UnitArea;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask.RendererThread;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class GuardCommand extends SelfInflitedCommand {

    protected IUnit actor;

    public GuardCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, ActionChoice.GUARD, scheduler, playerInventory, false);
    }


    @Override
    public boolean canbePerformedBy(IUnit actor) {
        return super.canbePerformedBy(actor) && actor.has(Data.Ability.GUARD);
    }

    @Override
    protected void execute() {

        // register old state
        actor = battlefield.getUnit(rowActor, colActor);

        // update model
        UnitArea area = Area.createGuardedArea(battlefield, rowActor, colActor, actor);
        battlefield.addUnitArea(area, false);


        // push render task
        StandardTask task = new StandardTask();
        task.addThread(new RendererThread(bfr, area));
        task.addThread(new RendererThread(bfr.getUnitRenderer(actor), Data.AnimId.GUARD));
        // animated guarded units as well
        IUnit guardedUnit;
        int dist;
        int rangeMin = Data.GUARD_REACTION_RANGE_MIN;
        int rangeMax = Data.GUARD_REACTION_RANGE_MAX;
        for(int r = rowActor - rangeMax; r <= rowActor + rangeMax; r++){
            for(int c = colActor - rangeMax; c <= colActor + rangeMax; c++){
                dist = Utils.dist(rowActor, colActor, r, c);
                if(rangeMin <= dist && dist <= rangeMax && battlefield.isTileOccupiedByAlly(r,c, actor.getArmy().getAffiliation())){
                    guardedUnit = battlefield.getUnit(r,c);
                    task.addThread(new RendererThread(bfr.getUnitRenderer(guardedUnit), Data.AnimId.GUARDED));
                }
            }
        }
        scheduleRenderTask(task);


        //outcome
        outcome.expHolders.add(new ExperiencePointsHolder(getInitiator(), choice.getExperience()));
    }

    @Override
    public void unexecute() {
        if(actor != null){
            Array<UnitArea> areas = battlefield.removeUnitAreas(actor, Data.AreaType.GUARD_AREA, false);
            for(int i = 0; i < areas.size; i++) {
                scheduleRenderTask(new StandardTask(bfr, areas.get(i)));
            }
        }
    }
}
