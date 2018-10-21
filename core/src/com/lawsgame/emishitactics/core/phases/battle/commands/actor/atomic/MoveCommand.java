package com.lawsgame.emishitactics.core.phases.battle.commands.actor.atomic;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class MoveCommand extends SelfInflitedCommand {
    protected Array<int[]> path;
    protected Data.Orientation oldWalkerOrientation;

    public MoveCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, Array<int[]> path){
        super(bfr, Data.ActionChoice.MOVE, scheduler, playerInventory, true);
        this.path = path;
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
        scheduleRenderTask(new StandardTask(bfr.getModel(), bfr.getUnitRenderer(getInitiator()), new Notification.Walk(getInitiator(), path)));
    }

    @Override
    protected void unexecute() {
        if(bfr.getModel().isTileOccupied(path.peek()[0], path.peek()[1])){
            IUnit unit = bfr.getModel().getUnit(path.peek()[0], path.peek()[1]);
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
    public boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        return super.isTargetValid(rowActor0, colActor0, rowTarget0, colTarget0) && !getBattlefield().isTileOccupied(path.peek()[0], path.peek()[1]);
    }
}
