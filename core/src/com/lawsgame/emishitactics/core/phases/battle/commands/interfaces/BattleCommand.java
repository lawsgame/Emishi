package com.lawsgame.emishitactics.core.phases.battle.commands.interfaces;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.engine.GameUpdatableEntity;
import com.lawsgame.emishitactics.engine.patterns.command.Command;

public abstract class BattleCommand implements Command, GameUpdatableEntity{
    protected Battlefield battlefield;
    protected Data.ActionChoice choice;
    protected int rowActor;
    protected int colActor;
    protected int rowTarget;
    protected int colTarget;

    public BattleCommand(Battlefield bf, Data.ActionChoice choice){
        this.battlefield = bf;
        this.choice = choice;
    }

    public abstract boolean isUndoable();                              // can be deleted afterwards
    public abstract boolean isFree();                                  // does not set Unit.acted as true
    public abstract boolean isEndTurnCommandOnly();                    // is only callable when the unit turn is ending
    public abstract void setTarget(int rowTarget, int colTarget);
    public abstract boolean isExecuting();
    public abstract boolean isExecutionCompleted();

    /**
     * PLAYER ORIENTED METHOD
     *
     * TARGET CHECKING
     * @return whether or not THIS SPECIFIC TARGET is at range by the actor performing the given action if one's is standing the tile (rowActor, colActor)
     * while ignoring the actor's history and the unit other requirements to actually perform this action, namely : weapon/item and ability requirements.
     */
    public abstract boolean isTargetValid();

    /**
     * AI ORIENTED METHOD
     *
     * check the third requirements to perform an action
     * - target availability requirements
     *
     * @param row
     * @param col
     * @return whether or not ANY TARGET is at range by the actor performing the given action if one's is standing the tile (row, col)
     * while ignoring the actor's history and the unit other requirements to actually perform this action, namely : weapon/item and ability requirements.
     */
    public abstract boolean atActionRange(int row, int col);


    /**
     *
     * @return fetch all tiles where the given unit is at range to perform the action chosen
     */
    public Array<int[]> getActionArea() {
        Array<int[]> actionArea = new Array<int[]>();
        if(battlefield.isTileOccupied(rowActor, colActor)) {
            switch (choice.getRangedBasedType()) {
                case MOVE:
                    actionArea = battlefield.getMoveArea(rowActor, colActor);
                    break;
                case WEAPON:
                    Unit actor = battlefield.getUnit(rowActor, colActor);
                    Data.TileType tileType = battlefield.getTile(rowActor, colActor);
                    boolean bannerAtRange = battlefield.isStandardBearerAtRange(actor, rowActor, colActor);
                    int rangeMin = actor.getCurrentRangeMin();
                    int rangeMax = actor.getCurrentRangeMax(tileType, bannerAtRange);
                    actionArea = Utils.getEreaFromRange(battlefield, rowActor, colActor, rangeMin, rangeMax);
                    break;
                case ONESELF:
                    actionArea.add(new int[]{rowActor, colActor});
                    break;
                case SPECIFIC:
                    actionArea = Utils.getEreaFromRange(battlefield, rowActor, colActor, choice.getRangeMin(), choice.getRangeMax());
                    break;
            }
        }
        return actionArea;
    }

    /**
     *
     * @param choice
     * @param rowActor
     * @param colActor
     * @param rowImpactTile
     * @param colImpactTile
     * @return get all possible target tile knowing thtat the given tile is within the impact area
     */
    private Array<int[]> getTargetFromCollateral(Data.ActionChoice choice, int rowActor, int colActor, int rowImpactTile, int colImpactTile) {
        Array<int[]> possibleTargetTiles = new Array<int[]>();
        Array<int[]> impactArea;
        int row;
        int col;
        for(Data.Orientation or: Data.Orientation.values()){
            impactArea = choice.getOrientedImpactArea(or);
            for(int i = 0; i < impactArea.size; i++){
                row =rowImpactTile - impactArea.get(i)[0];
                col =colImpactTile - impactArea.get(i)[1];
                if(battlefield.isTileExisted(row, col) && or == Utils.getOrientationFromCoords(rowActor, colActor, row, col)){
                    possibleTargetTiles.add(new int[]{row, col});
                }
            }
        }
        return possibleTargetTiles;
    }

    /**
     *
     * @return the relevantly oriented impact area of an action performed by an actor while targeting the tile {rowTarget, colTarget}
     */
    public Array<int[]> getImpactArea(){
        Array<int[]> orientedArea = choice.getOrientedImpactArea(Utils.getOrientationFromCoords(rowActor, colActor, rowTarget, colTarget));
        if(battlefield.isTileExisted(rowTarget, colTarget)) {
            for (int i = 0; i < orientedArea.size; i++) {
                orientedArea.get(i)[0] += rowTarget;
                orientedArea.get(i)[1] += colTarget;
                if (!battlefield.isTileExisted(orientedArea.get(i)[0], orientedArea.get(i)[1])) {
                    orientedArea.removeIndex(i);
                    i--;
                }
            }
            orientedArea.add(new int[]{rowTarget, colTarget});
        }
        return orientedArea;
    }




    //------------------ GETTERS & SETTERS ---------------------------

    public Data.ActionChoice getActionChoice() {
        return choice;
    }

    public void setBattlefield(Battlefield battlefield){
        setBattlefield(battlefield);
    }

    public Battlefield getBattlefield() {
        return battlefield;
    }

    public boolean setActor(int rowActor, int colActor) {

        if(battlefield.isTileOccupied(rowActor, colActor)) {
            this.rowActor = rowActor;
            this.colActor = colActor;
            return true;
        }
        return false;
    }

    private class BCWrongSetCallingException extends Exception {
        BCWrongSetCallingException(String msg){
            super(msg);
        }
    }
}
