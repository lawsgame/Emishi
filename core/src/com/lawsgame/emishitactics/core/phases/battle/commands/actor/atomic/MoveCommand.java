package com.lawsgame.emishitactics.core.phases.battle.commands.actor.atomic;

import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.Orientation;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.phases.battle.commands.SelfInflitedCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

public class MoveCommand extends SelfInflitedCommand{
    protected Orientation orientation;
    protected Orientation oldOrientation;
    protected int rowTargetTile = -1;
    protected int colTargetTile = -1;

    public MoveCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, ActionChoice.MOVE, scheduler, playerInventory, true);
    }

    @Override
    public boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        if(super.isTargetValid(rowActor0, colActor0, rowTarget0, colTarget0)){
            this.rowTargetTile = rowActor0;
            this.colTargetTile = colActor0;
            switch(orientation){
                case WEST: colTargetTile -= 1; break;
                case NORTH: rowTargetTile += 1; break;
                case SOUTH: rowTargetTile -=1; break;
                case EAST: colTargetTile +=1; break;
            }
            return getBattlefield().isTileAvailable(rowTargetTile, colTargetTile, getBattlefield().getUnit(rowActor0, colActor0).has(Data.Ability.PATHFINDER));
        }
        return false;
    }

    @Override
    protected void execute() {
        //store old state
        oldOrientation = getInitiator().getOrientation();

        // update model
        bfr.getModel().moveUnit(rowActor, colActor, rowTargetTile, colTargetTile, false);
        getInitiator().setOrientation(orientation);



    }

    @Override
    protected void unexecute() {

    }
}
