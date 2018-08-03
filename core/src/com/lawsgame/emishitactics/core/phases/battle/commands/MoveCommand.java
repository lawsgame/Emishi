package com.lawsgame.emishitactics.core.phases.battle.commands;


import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Notification.Walk;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;


public class MoveCommand extends BattleCommand{
    protected BattleUnitRenderer walkerRenderer;
    protected Array<int[]> path;

    public MoveCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        super(bfr, Data.ActionChoice.MOVE, scheduler, true, false);
    }

    @Override
    public void init() {
        super.init();
        walkerRenderer = null;
        path = null;
    }

    @Override
    protected void execute() {
        IUnit walker = battlefield.getUnit(rowActor, colActor);
        walkerRenderer = battlefieldRenderer.getUnitRenderer(walker);

        battlefield.moveUnit(rowActor, colActor, rowTarget, colTarget);
        scheduler.addTask(new Task(battlefieldRenderer, battlefieldRenderer.getUnitRenderer(walker), new Walk(walker, path)));

        Data.Orientation or = (path.size > 1) ?
                Utils.getOrientationFromCoords(path.get(path.size - 2)[0], path.get(path.size - 2)[1], rowTarget, colTarget) :
                Utils.getOrientationFromCoords(rowActor, colActor, rowTarget, colTarget);
        walker.setOrientation(or);

        walker.setMoved(true);
    }

    /**
     *
     * addExpGained up the path if the tileType targeted is a valid choice to move to
     *
     * @param rowActor0
     * @param colActor0
     * @param rowTarget0
     * @param colTarget0
     * @return true if the target tileType if a valid tileType to move to.
     */
    @Override
    public boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        boolean valid = false;
        if(battlefield.isTileOccupied(rowActor0, colActor0)){
            IUnit actor = battlefield.getUnit(rowActor0, colActor0);
            path = battlefield.getShortestPath(rowActor0, colActor0, rowTarget0, colTarget0, actor.has(Data.Ability.PATHFINDER), actor.getAllegeance(), true);
            if(path.size > 0 && path.size <= actor.getAppMobility()){
                valid = true;
            }else{
                path = battlefield.getShortestPath(rowActor0, colActor0, rowTarget0, colTarget0, actor.has(Data.Ability.PATHFINDER), actor.getAllegeance(), false);
                if(path.size > 0 && path.size <= actor.getAppMobility()){
                    valid = true;
                }else{
                    path = null;
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
                scheduler.addTask(new Task(battlefieldRenderer, walkerRenderer, new Notification.SetUnit(rowActor, colActor, actor)));
            }
        }
    }

}
