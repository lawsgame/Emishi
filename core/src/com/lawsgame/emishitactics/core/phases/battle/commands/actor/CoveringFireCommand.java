package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification.StepOn;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.atomic.HitCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class CoveringFireCommand extends SelfInflitedCommand {


    public CoveringFireCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, Data.ActionChoice.COVER_AREA, scheduler, playerInventory, false);
    }

    @Override
    public boolean isInitiatorValid(int rowActor, int colActor, Unit initiator) {
        return super.isInitiatorValid(rowActor, colActor, initiator) && !initiator.getCurrentWeapon().getTemplate().isMelee();
    }

    @Override
    protected void execute() {
        // update model
        int rangeMin = getInitiator().getCurrentWeaponRangeMin(rowActor, colActor, bfr.getModel());
        int rangeMax = getInitiator().getCurrentWeaponRangeMax(rowActor, colActor, bfr.getModel());
        Array<int[]> tiles = Utils.getEreaFromRange(bfr.getModel(), rowActor, colActor, rangeMin, rangeMax);
        Area.UnitArea area = new CoveringUnitArea(rowActor, colActor, bfr, scheduler, outcome.playerInventory, tiles);
        bfr.getModel().addUnitArea(area);
        // push render task
        StandardTask task = new StandardTask();
        task.addThread(new StandardTask.RendererThread(bfr, area));
        task.addThread(new StandardTask.RendererThread(bfr.getUnitRenderer(getInitiator()), Data.AnimId.COVER));
        scheduleRenderTask(task);
        //  outcome
        outcome.add(getInitiator(), choice.getExperience());
        // handle event
        handleEvents(this);
    }


    public static class CoveringUnitArea extends Area.UnitArea {

        public CoveringUnitArea(int rowAttacker, int colAttacker, BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, Array<int[]> tiles) {
            super(bfr.getModel(), Data.AreaType.COVER, tiles, bfr.getModel().getUnit(rowAttacker, colAttacker), true);
            final HitCommand command = new HitCommand(bfr, Data.ActionChoice.ATTACK, scheduler, playerInventory);
            command.setInitiator(rowAttacker, colAttacker);
            add(new Trigger(false, command) {
                @Override
                protected boolean isTriggerable(Object data) {
                    if(data instanceof StepOn){
                        StepOn stepOn = (StepOn) data;
                        if(contains(stepOn.rowTile, stepOn.colTile)){ // && !contains(stepOn.fromRow, stepOn.fromCol)) {
                            command.setTarget(stepOn.rowTile, stepOn.colTile);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }

}
