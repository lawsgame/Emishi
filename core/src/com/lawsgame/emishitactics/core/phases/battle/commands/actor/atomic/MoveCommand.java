package com.lawsgame.emishitactics.core.phases.battle.commands.actor.atomic;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class MoveCommand extends SelfInflitedCommand {
    protected Array<int[]> path;
    protected Data.Orientation oldWalkerOrientation;
    // if true, the walker appears while walking to his destination.
    protected boolean reveal;

    public MoveCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory){
        this(bfr, scheduler, playerInventory, new Array<int[]>(), false);
    }

    public MoveCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, Array<int[]> path, boolean reveal) {
        super(bfr, Data.ActionChoice.MOVE, scheduler, playerInventory, true);
        this.path = path;
        this.reveal = reveal;
    }

    @Override
    protected void execute() {
        oldWalkerOrientation = getInitiator().getOrientation();

        // update model
        bfr.getModel().moveUnit(rowActor, colActor, path.peek()[0], path.peek()[1], false);
        Data.Orientation or = (path.size > 1) ?
                Utils.getOrientationFromCoords(path.get(path.size - 2)[0], path.get(path.size - 2)[1], path.peek()[0], path.peek()[1]) :
                Utils.getOrientationFromCoords(rowActor, colActor, path.peek()[0], path.peek()[1]);
        getInitiator().setOrientation(or);

        // push render task
        StandardTask task = new StandardTask();
        StandardTask.RendererSubTaskQueue thread = new StandardTask.RendererSubTaskQueue(bfr.getUnitRenderer(getInitiator()));
        if(reveal)
            thread.addQuery(Notification.Visible.get(true));
        thread.addQuery(bfr.getModel(), new Notification.Walk(getInitiator(), path, reveal));
        task.addParallelSubTask(thread);
        scheduleRenderTask(task);
    }

    @Override
    public boolean isAppliableWihoutValidation() {
        return super.isAppliableWihoutValidation() && ! reveal;
    }

    @Override
    protected void unexecute() {
        if(bfr.getModel().isTileOccupied(path.peek()[0], path.peek()[1])){
            Unit unit = bfr.getModel().getUnit(path.peek()[0], path.peek()[1]);
            if(unit == getInitiator()) {
                BattleUnitRenderer walkerRenderer = bfr.getUnitRenderer(getInitiator());
                bfr.getModel().moveUnit(path.peek()[0], path.peek()[1], rowActor, colActor, false);
                unit.setOrientation(oldWalkerOrientation);
                scheduleRenderTask(new StandardTask(bfr.getModel(), walkerRenderer, new Notification.SetUnit(rowActor, colActor, unit)));
                scheduleRenderTask(new StandardTask(walkerRenderer, oldWalkerOrientation));
            }

        }
    }

    @Override
    public boolean isTargetValid(Unit initiator, int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        return super.isTargetValid(initiator, rowActor0, colActor0, rowTarget0, colTarget0) && path.size > 0 && !getBattlefield().isTileOccupied(path.peek()[0], path.peek()[1]);
    }

    // -------------- GETTERS & SETTERS -----------------------------------------


    public void setPath(Array<int[]> path) {
        this.path = path;
    }

    public void setReveal(boolean reveal) {
        this.reveal = reveal;
    }
}
