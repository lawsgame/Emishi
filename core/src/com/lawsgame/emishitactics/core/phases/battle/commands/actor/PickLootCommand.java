package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Tile;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class PickLootCommand extends SelfInflitedCommand {

    public PickLootCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, Data.ActionChoice.PICK_LOOT, scheduler, playerInventory);
        setRegisterAction(false);
    }

    @Override
    public boolean isTargetValid(Unit initiator, int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        return super.isTargetValid(initiator, rowActor0, colActor0, rowTarget0, colTarget0) && !bfr.getModel().isTileLooted(rowTarget0, colTarget0);
    }

    @Override
    protected void execute() {
        Tile tile = bfr.getModel().getTile(rowTarget, colTarget);
        StandardTask task = new StandardTask();
        task.addParallelSubTask(new StandardTask.RendererSubTaskQueue(bfr.getUnitRenderer(getInitiator()), Data.AnimId.PICK_LOOT));
        if(tile.isRevealed()){
            task.addParallelSubTask(new StandardTask.RendererSubTaskQueue(bfr, new Notification.ExtinguishSparkle(rowTarget, colTarget)));
        }
        // schedule tasks
        scheduleRenderTask(task);
        // outcome
        outcome.add(tile.getLoot(true), getInitiator().getArmy().isPlayerControlled());
        // handle event
        handleEvents(this);
    }
}
