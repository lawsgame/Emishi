package com.lawsgame.emishitactics.core.phases.battle.commands;


import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.models.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Notification.Walk;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;


public class MoveCommand extends BattleCommand{
    protected BattleUnitRenderer walkerRenderer;
    protected Array<int[]> validPath;

    public MoveCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        super(bfr, ActionChoice.MOVE, scheduler, true, false, false);
    }

    @Override
    public void init() {
        super.init();

        // register old state
        IUnit walker = battlefield.getUnit(rowActor, colActor);
        walkerRenderer = battlefieldRenderer.getUnitRenderer(walker);

        // set validPath
        validPath = battlefield.getShortestPath(rowActor, colActor, rowTarget, colTarget, walker.has(Data.Ability.PATHFINDER), walker.getAllegeance(), true);
        if(validPath.size == 0 || validPath.size > walker.getAppMobility()){
            validPath = battlefield.getShortestPath(rowActor, colActor, rowTarget, colTarget, walker.has(Data.Ability.PATHFINDER), walker.getAllegeance(), false);
        }
    }

    @Override
    protected void execute() {
        IUnit walker = battlefield.getUnit(rowActor, colActor);

        // update model
        battlefield.moveUnit(rowActor, colActor, rowTarget, colTarget);
        Data.Orientation or = (validPath.size > 1) ?
                Utils.getOrientationFromCoords(validPath.get(validPath.size - 2)[0], validPath.get(validPath.size - 2)[1], rowTarget, colTarget) :
                Utils.getOrientationFromCoords(rowActor, colActor, rowTarget, colTarget);
        walker.setOrientation(or);

        // push render task
        scheduler.addTask(new Task(battlefield, battlefieldRenderer.getUnitRenderer(walker), new Walk(walker, validPath)));

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
        if(battlefield.isTileOccupied(rowActor0, colActor0)){
            IUnit actor = battlefield.getUnit(rowActor0, colActor0);
            Array<int[]> path = battlefield.getShortestPath(rowActor0, colActor0, rowTarget0, colTarget0, actor.has(Data.Ability.PATHFINDER), actor.getAllegeance(), true);
            if(path.size > 0 && path.size <= actor.getAppMobility()){
                valid = true;
            }else{
                path = battlefield.getShortestPath(rowActor0, colActor0, rowTarget0, colTarget0, actor.has(Data.Ability.PATHFINDER), actor.getAllegeance(), false);
                if(path.size > 0 && path.size <= actor.getAppMobility()){
                    valid = true;
                }
            }
        }
        return valid;
    }

    @Override
    public boolean atActionRange(int row, int col, IUnit actor) {
        return true;
    }

    @Override
    public void undo() {
        if(battlefield.isTileOccupied(rowTarget, colTarget)){
            IUnit actor = battlefield.getUnit(rowTarget, colTarget);
            if(actor == walkerRenderer.getModel()) {
                battlefield.moveUnit(rowTarget, colTarget, rowActor, colActor);
                actor.setMoved(false);
                scheduler.addTask(new Task(battlefield, walkerRenderer, new Notification.SetUnit(rowActor, colActor, actor)));
            }
        }
    }

}
