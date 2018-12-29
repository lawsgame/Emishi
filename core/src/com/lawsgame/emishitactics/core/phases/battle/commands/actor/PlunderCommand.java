package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification.SetTile;
import com.lawsgame.emishitactics.core.models.Tile;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class PlunderCommand extends SelfInflitedCommand {


    public PlunderCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, Data.ActionChoice.PLUNDER, scheduler, playerInventory);
    }

    @Override
    protected void execute() {
        // update model
        Data.TileType damagedType = bfr.getModel().getTile(rowTarget, colTarget).getType().getDamagedType();
        Tile oldTile = bfr.getModel().getTile(rowTarget, colTarget);
        bfr.getModel().setTile(rowTarget, colTarget, damagedType, false);

        // push render task
        StandardTask task = new StandardTask();
        task.addParallelSubTask(new StandardTask.RendererSubTaskQueue(bfr, new SetTile(rowTarget, colTarget, bfr.getModel().getTile(rowTarget, colTarget), oldTile, SetTile.TransformationType.PLUNDERED)));
        task.addParallelSubTask(new StandardTask.RendererSubTaskQueue(bfr.getUnitRenderer(getInitiator()), Data.AnimId.PLUNDER));
        scheduleRenderTask(task);

        // set outcome
        outcome.add(getInitiator(), choice.getExperience());

        handleEvents(this);

    }

    @Override
    public boolean isTargetValid(Unit initiator, int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        return super.isTargetValid(initiator, rowActor0, colActor0, rowTarget0, colTarget0) && bfr.getModel().isTilePlunderable(rowTarget0, colTarget0);
    }
}
