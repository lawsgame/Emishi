package com.lawsgame.emishitactics.core.phases.battle.commands;


import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;



public class MoveCommand extends BattleCommand{
    private boolean executed;
    private BattleUnitRenderer actorRenderer;
    private Array<int[]> path;

    public MoveCommand(BattlefieldRenderer bfr) {
        super(bfr, Data.ActionChoice.MOVE);
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
        battlefield.getUnit(rowActor, colActor).setMoved(true);
        battlefield.moveUnit(rowActor, colActor, rowTarget, colTarget);
        battlefieldRenderer.getNotification(path);

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
    public boolean atActionRange(int row, int col) {
        return true;
    }

    @Override
    public void update(float dt) { }


    @Override
    public void undo() {
        if(executed){
            battlefield.moveUnit(rowTarget, colTarget, rowActor, colActor);
            battlefield.getUnit(rowActor, colActor).setMoved(false);
            battlefieldRenderer.getNotification(new int[]{rowActor, colActor});
        }
    }

    @Override
    public void redo() {    }
}
