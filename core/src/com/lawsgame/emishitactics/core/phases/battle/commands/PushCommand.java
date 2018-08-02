package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Thread;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class PushCommand extends BattleCommand{

    public PushCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        super(bfr, Data.ActionChoice.PUSH, scheduler, false, false);
    }

    @Override
    protected void execute() {

        IUnit actor = battlefield.getUnit(rowActor, colActor);
        IUnit pushed = battlefield.getUnit(rowTarget, colTarget);
        Data.Orientation pushOr = Utils.getOrientationFromCoords(rowActor, colActor, rowTarget, colTarget);
        pushed.setOrientation(pushOr);

        switch(pushOr){
            case WEST:  battlefield.moveUnit(rowTarget, colTarget, rowTarget, colTarget - 1); break;
            case NORTH: battlefield.moveUnit(rowTarget, colTarget, rowTarget + 1, colTarget);
            case SOUTH: battlefield.moveUnit(rowTarget, colTarget, rowTarget - 1, colTarget); break;
            case EAST:  battlefield.moveUnit(rowTarget, colTarget, rowTarget, colTarget + 1); break;
        }

        Task task = new Task();
        task.addThread(new Thread(battlefieldRenderer.getUnitRenderer(actor), Data.AnimationId.PUSH));
        task.addThread(new Thread(battlefieldRenderer.getUnitRenderer(pushed), new Notification.Pushed(pushOr)));
        scheduler.addTask(task);

        actor.setActed(true);

    }

    @Override
    public boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        boolean valid = false;
        if(battlefield.isTileOccupied(rowActor0, colActor0)){

            IUnit pusher = battlefield.getUnit(rowActor0, colActor0);
            if(battlefield.isTileOccupiedByAlly(rowTarget0, colTarget0, pusher.getAllegeance()) && Utils.dist(rowActor0, colActor0, rowTarget0, colTarget0) == 1){

                IUnit pushed = battlefield.getUnit(rowTarget0, colTarget0);
                if(rowActor0 < rowTarget0 && battlefield.isTileAvailable(rowTarget0 + 1, colTarget0, pushed.has(Data.Ability.PATHFINDER)))
                    valid = true;
                if(rowActor0 > rowTarget0 && battlefield.isTileAvailable(rowTarget0 - 1, colTarget0, pushed.has(Data.Ability.PATHFINDER)))
                    valid = true;
                if(colActor0 < colTarget0 && battlefield.isTileAvailable(rowTarget0, colTarget0 + 1, pushed.has(Data.Ability.PATHFINDER)))
                    valid = true;
                if(colActor0 > colTarget0 && battlefield.isTileAvailable(rowTarget0, colTarget0 - 1, pushed.has(Data.Ability.PATHFINDER)))
                    valid = true;
            }
        }
        return valid;
    }

    @Override
    public boolean atActionRange(int row, int col, IUnit actor) {
        boolean targetAtRange = false;
        if(battlefield.isTileOccupiedByAlly(row + 1, col, actor.getAllegeance())
                && battlefield.isTileAvailable(row + 2, col, actor.has(Data.Ability.PATHFINDER))){
            targetAtRange = true;
        }else{
            if(battlefield.isTileOccupiedByAlly(row - 1, col, actor.getAllegeance())
                    && battlefield.isTileAvailable(row - 2, col, actor.has(Data.Ability.PATHFINDER))){
                targetAtRange = true;
            }else{
                if (battlefield.isTileOccupiedByAlly(row, col + 1, actor.getAllegeance())
                        && battlefield.isTileAvailable(row, col + 2, actor.has(Data.Ability.PATHFINDER))){
                    targetAtRange = true;
                }else if (battlefield.isTileOccupiedByAlly(row, col - 1, actor.getAllegeance())
                        && battlefield.isTileAvailable(row, col - 2, actor.has(Data.Ability.PATHFINDER))){
                    targetAtRange = true;
                }
            }
        }
        return targetAtRange;
    }
}
