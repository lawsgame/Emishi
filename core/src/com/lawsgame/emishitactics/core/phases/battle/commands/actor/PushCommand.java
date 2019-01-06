package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask.RendererSubTaskQueue;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class PushCommand extends ActorCommand {

    public PushCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, ActionChoice.PUSH, scheduler, playerInventory);
    }

    @Override
    public boolean isInitiatorValid(int rowActor, int colActor, Unit initiator) {
        return super.isInitiatorValid(rowActor, colActor, initiator) && !initiator.isHorseman();
    }

    @Override
    protected void execute() {

        // update model
        Unit actor = bfr.getModel().getUnit(rowActor, colActor);
        Unit pushed = bfr.getModel().getUnit(rowTarget, colTarget);
        Data.Orientation pushOr = Utils.getOrientationFromCoords(rowActor, colActor, rowTarget, colTarget);
        pushed.setOrientation(pushOr);
        int rowEndTile;
        int colEndTile;
        switch(pushOr){
            case WEST:
                bfr.getModel().moveUnit(rowTarget, colTarget, rowTarget, colTarget - 1, false);
                rowEndTile = rowTarget;
                colEndTile = colTarget - 1;
                break;
            case NORTH:
                bfr.getModel().moveUnit(rowTarget, colTarget, rowTarget + 1, colTarget, false);
                rowEndTile = rowTarget + 1;
                colEndTile = colTarget;
                break;
            case SOUTH:
                bfr.getModel().moveUnit(rowTarget, colTarget, rowTarget - 1, colTarget, false);
                rowEndTile = rowTarget - 1;
                colEndTile = colTarget;
                break;
            default:
                bfr.getModel().moveUnit(rowTarget, colTarget, rowTarget, colTarget + 1, false);
                rowEndTile = rowTarget;
                colEndTile = colTarget + 1;
                break;
        }

        // push render task
        StandardTask task = new StandardTask();
        task.addParallelSubTask(new StandardTask.RendererSubTaskQueue(bfr.getUnitRenderer(actor), Data.AnimId.PUSH));
        task.addParallelSubTask(new RendererSubTaskQueue(bfr.getUnitRenderer(pushed), Notification.Pushed.get(pushOr)));
        scheduleRenderTask(task);

        // handle event
        Notification.StepOn stepOn = new Notification.StepOn(rowTarget, colTarget, rowEndTile, colEndTile, getTarget());
        handleEvents(stepOn, rowEndTile, colEndTile);
    }

    @Override
    public boolean isTargetValid(Unit initiator, int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        boolean valid = false;
        if(initiator != null){

            if(bfr.getModel().isTileOccupiedByAlly(rowTarget0, colTarget0, initiator.getArmy().getAffiliation()) && Utils.dist(rowActor0, colActor0, rowTarget0, colTarget0) == 1){

                Unit pushed = bfr.getModel().getUnit(rowTarget0, colTarget0);
                if(rowActor0 < rowTarget0 && bfr.getModel().isTileAvailable(rowTarget0 + 1, colTarget0, pushed.has(Data.Ability.PATHFINDER), initiator))
                    valid = true;
                if(rowActor0 > rowTarget0 && bfr.getModel().isTileAvailable(rowTarget0 - 1, colTarget0, pushed.has(Data.Ability.PATHFINDER), initiator))
                    valid = true;
                if(colActor0 < colTarget0 && bfr.getModel().isTileAvailable(rowTarget0, colTarget0 + 1, pushed.has(Data.Ability.PATHFINDER), initiator))
                    valid = true;
                if(colActor0 > colTarget0 && bfr.getModel().isTileAvailable(rowTarget0, colTarget0 - 1, pushed.has(Data.Ability.PATHFINDER), initiator))
                    valid = true;
            }
        }
        return valid;
    }

    @Override
    public Array<int[]> getTargetsAtRange(int row, int col, Unit actor) {
        Array<int[]> targetsAtRange = new Array<int[]>();
        if(actor.belongToAnArmy()) {
            if (bfr.getModel().isTileOccupiedByAlly(row + 1, col, actor.getArmy().getAffiliation())
                    && bfr.getModel().isTileAvailable(row + 2, col, actor.has(Data.Ability.PATHFINDER), actor)) {
                targetsAtRange.add(new int[]{row + 1, col});
            }

            if (bfr.getModel().isTileOccupiedByAlly(row - 1, col, actor.getArmy().getAffiliation())
                    && bfr.getModel().isTileAvailable(row - 2, col, actor.has(Data.Ability.PATHFINDER), actor)) {
                targetsAtRange.add(new int[]{row - 1, col});
            }

            if (bfr.getModel().isTileOccupiedByAlly(row, col + 1, actor.getArmy().getAffiliation())
                    && bfr.getModel().isTileAvailable(row, col + 2, actor.has(Data.Ability.PATHFINDER), actor)) {
                targetsAtRange.add(new int[]{row, col + 1});
            }

            if (bfr.getModel().isTileOccupiedByAlly(row, col - 1, actor.getArmy().getAffiliation())
                    && bfr.getModel().isTileAvailable(row, col - 2, actor.has(Data.Ability.PATHFINDER), actor)) {
                targetsAtRange.add(new int[]{row, col - 1});
            }


        }
        return targetsAtRange;
    }
}
