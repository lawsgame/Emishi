package com.lawsgame.emishitactics.core.phases.battle.commands.actor;


import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Notification.Walk;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;


public class WalkCommand extends ActorCommand {
    protected Array<int[]> validPath;
    protected Data.Orientation oldWalkerOrientation;

    public WalkCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory) {
        super(bfr, ActionChoice.MOVE, scheduler, playerInventory, false);
    }

    @Override
    protected void execute() {

        //store old state info
        oldWalkerOrientation = getInitiator().getOrientation();

        // update model
        bfr.getModel().moveUnit(rowActor, colActor, rowTarget, colTarget, false);
        Data.Orientation or = (validPath.size > 1) ?
                Utils.getOrientationFromCoords(validPath.get(validPath.size - 2)[0], validPath.get(validPath.size - 2)[1], rowTarget, colTarget) :
                Utils.getOrientationFromCoords(rowActor, colActor, rowTarget, colTarget);
        getInitiator().setOrientation(or);

        // push render task
        scheduleRenderTask(new StandardTask(bfr.getModel(), bfr.getUnitRenderer(getInitiator()), new Walk(getInitiator(), validPath)));


    }

    /**
     *
     * addExpGained up the validPath if the buildingType targeted is a valid choice to move to
     *
     * @param rowActor0
     * @param colActor0
     * @param rowTarget0
     * @param colTarget0
     * @return true if the target buildingType if a valid buildingType to move to.
     */
    @Override
    public boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        boolean valid = false;
        if(bfr.getModel().isTileOccupied(rowActor0, colActor0)){

            this.validPath = bfr.getModel().getShortestPath(rowActor0, colActor0, rowTarget0, colTarget0, getInitiator().has(Data.Ability.PATHFINDER), getInitiator().getArmy().getAffiliation());
            if(validPath.size > 0 && validPath.size <= getInitiator().getAppMobility()){

                valid = true;
            }
        }
        return valid;
    }

    @Override
    public Array<int[]> getTargetsAtRange(int row, int col, IUnit actor) {
        return bfr.getModel().getMoveArea(row, col, actor);
    }

    @Override
    protected void unexecute() {
        if(bfr.getModel().isTileOccupied(rowTarget, colTarget)){
            IUnit unit = bfr.getModel().getUnit(rowTarget, colTarget);
            if(unit == getInitiator()) {
                BattleUnitRenderer walkerRenderer = bfr.getUnitRenderer(getInitiator());
                bfr.getModel().moveUnit(rowTarget, colTarget, rowActor, colActor, false);
                unit.setOrientation(oldWalkerOrientation);
                scheduleRenderTask(new StandardTask(bfr.getModel(), walkerRenderer, new Notification.SetUnit(rowActor, colActor, unit)));
                scheduleRenderTask(new StandardTask(walkerRenderer, oldWalkerOrientation));
            }

        }
    }

}
