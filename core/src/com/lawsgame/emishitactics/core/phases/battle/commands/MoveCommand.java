package com.lawsgame.emishitactics.core.phases.battle.commands;


import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler.Thread;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;


public class MoveCommand extends BattleCommand{
    protected boolean executed;
    protected BattleUnitRenderer actorRenderer;
    protected Array<int[]> path;

    public MoveCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler) {
        super(bfr, Data.ActionChoice.MOVE, scheduler);
    }

    @Override
    public void init() {
        executed = false;
        actorRenderer = null;
        path = null;
    }

    @Override
    protected void execute() {
        executed = true;

        /*
        the order is crucial here.
        As the unit moved is identified by his position, the moment where the unit model is changed accordingly must be taken into consideration
         */
        IUnit actor = battlefield.getUnit(rowActor, colActor);

        actor.setMoved(true);
        battlefield.moveUnit(rowActor, colActor, rowTarget, colTarget);
        scheduler.addTask(new Task(battlefieldRenderer.getUnitRenderer(actor), path));

        Data.Orientation or;
        if(path.size > 1) {
            or = Utils.getOrientationFromCoords(path.get(path.size - 2)[0], path.get(path.size - 2)[1], rowTarget, colTarget);
        }else{
            or = Utils.getOrientationFromCoords(rowActor, colActor, rowTarget, colTarget);
        }
        actor.setOrientation(or);

    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public boolean isEndTurnCommandOnly() {
        return false;
    }

    @Override
    public boolean isExecuting() {
        if(actorRenderer == null && battlefield.isTileOccupied(rowActor, colActor)) {
            actorRenderer = battlefieldRenderer.getUnitRenderer(battlefield.getUnit(rowActor, colActor));
        }
        return actorRenderer != null && actorRenderer.isExecuting();
    }

    @Override
    public boolean isExecutionCompleted() {
        return executed && actorRenderer != null && !actorRenderer.isExecuting();
    }

    /**
     *
     * set up the path if the tile targeted is a valid choice to move to
     *
     * @param rowActor0
     * @param colActor0
     * @param rowTarget0
     * @param colTarget0
     * @return true if the target tile if a valid tile to move to.
     */
    @Override
    public boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0) {
        if(battlefield.isTileOccupied(rowActor0, colActor0)){
            IUnit actor = battlefield.getUnit(rowActor0, colActor0);
            path = battlefield.getShortestPath(rowActor0, colActor0, rowTarget0, colTarget0, actor.has(Data.PassiveAbility.PATHFINDER), actor.getAllegeance(), true);
            if(path.size > 0 && path.size <= actor.getAppMobility()){
                return true;
            }else{
                path = battlefield.getShortestPath(rowActor0, colActor0, rowTarget0, colTarget0, actor.has(Data.PassiveAbility.PATHFINDER), actor.getAllegeance(), false);
                if(path.size > 0 && path.size <= actor.getAppMobility()){
                    return true;
                }else{
                    path = null;
                }
            }
        }
        return false;
    }

    @Override
    public boolean atActionRange(int row, int col, IUnit actor) {
        return true;
    }

    @Override
    public void undo() {
        if(executed){
            battlefield.moveUnit(rowTarget, colTarget, rowActor, colActor);
            battlefield.getUnit(rowActor, colActor).setMoved(false);

            //battlefieldRenderer.getNotification(new int[]{rowActor, colActor});
            BattleUnitRenderer bur = battlefieldRenderer.getUnitRenderer(battlefield.getUnit(rowActor, colActor));
            scheduler.addTask(new Task(battlefieldRenderer, bur, new int[]{rowActor, colActor}));
        }
    }

    @Override
    public void redo() {    }
}
