package com.lawsgame.emishitactics.core.phases.battle.commands.actor;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Tile;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask.RendererThread;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class PushCommand extends ActorCommand {

    public PushCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, ActionChoice.PUSH, scheduler, playerInventory, false);
    }

    @Override
    public boolean isInitiatorValid(int rowActor, int colActor, IUnit initiator) {
        return super.isInitiatorValid(rowActor, colActor, initiator) && !initiator.isHorseman();
    }

    @Override
    protected void execute() {

        // update model
        IUnit actor = bfr.getModel().getUnit(rowActor, colActor);
        IUnit pushed = bfr.getModel().getUnit(rowTarget, colTarget);
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
        task.addThread(new RendererThread(bfr.getUnitRenderer(actor), Data.AnimId.PUSH));
        task.addThread(new RendererThread(bfr.getUnitRenderer(pushed), Notification.Pushed.get(pushOr)));
        scheduleRenderTask(task);

        // handle event
        Notification.StepOn stepOn = new Notification.StepOn(rowEndTile, colEndTile, getTarget());
        if(isAnyEventTriggerable(stepOn, rowEndTile, colEndTile)){
            this.eventTriggered = true;
            handleEvents(stepOn, rowEndTile, colEndTile);
        }

    }

    @Override
    public boolean isTargetValid(IUnit initiator, int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        boolean valid = false;
        if(initiator != null){

            if(bfr.getModel().isTileOccupiedByAlly(rowTarget0, colTarget0, initiator.getArmy().getAffiliation()) && Utils.dist(rowActor0, colActor0, rowTarget0, colTarget0) == 1){

                IUnit pushed = bfr.getModel().getUnit(rowTarget0, colTarget0);
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
    public Array<int[]> getTargetsAtRange(int row, int col, IUnit actor) {
        Array<int[]> targetsAtRange = new Array<int[]>();
        if(actor.isMobilized()) {
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
