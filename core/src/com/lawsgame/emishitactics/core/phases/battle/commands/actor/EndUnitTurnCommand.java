package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.phases.battle.commands.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.TileHighlighter;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

import static com.lawsgame.emishitactics.core.models.Data.ActionChoice.END_TURN;

public class EndUnitTurnCommand extends SelfInflitedCommand {
    private TileHighlighter thl;


    public EndUnitTurnCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, TileHighlighter thl) {
        super(bfr, END_TURN, scheduler, playerInventory, true);
        this.thl = thl;
    }

    @Override
    protected void execute() {

        getInitiator().setMoved(true);
        getInitiator().setActed(true);

        StandardTask task = new StandardTask();
        task.addParallelSubTask(new StandardTask.RendererSubTaskQueue(bfr.getUnitRenderer(getInitiator()), Notification.Done.get(true)));
        task.addParallelSubTask(new StandardTask.CommandSubTask( 0){

            @Override
            public void run() {
                thl.generateTileHighlightingEndTurnCommand().apply();
            }
        });
        this.scheduleRenderTask(task);
    }



}
