package com.lawsgame.emishitactics.core.phases.battle.commands.interfaces;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.GameUpdatableEntity;
import com.lawsgame.emishitactics.engine.patterns.command.Command;

/**
 *
 * I - Battle command usage
 *
 *  command.update(dt);
 *
 *  ActionChoice choice = ...;
 *  if(bcm.canActionBePerformed(...){
 *      BattleCommand command = bcm.get(...); OR new XCommand(...);
 *      if(command != null && command.setActor(...)){
 *          command.setTarget(...);
 *          if(command.isTargetValid()){
 *              command.apply();
 *          }
 *      }
 *  }
 *
 *  II - battle command flow
 *
 *  1 - get the command initialized
 *  2 - set actor and target
 *  3 - validate the target availability
 *  4 - execute the command
 *  5 - undo it if required
 *
 * no need to put back the command in the BCM
 */
public abstract class BattleCommand implements Command{
    protected Battlefield battlefield;
    protected BattlefieldRenderer battlefieldRenderer;
    protected AnimationScheduler scheduler;
    protected Data.ActionChoice choice;
    protected int rowActor;
    protected int colActor;
    protected int rowTarget;
    protected int colTarget;
    protected boolean validate;
    protected boolean undoable;
    protected boolean endturnCommandOnly;


    public BattleCommand(BattlefieldRenderer bfr, Data.ActionChoice choice, AnimationScheduler scheduler, boolean undoable, boolean endturnCommandOnly){
        this.battlefieldRenderer = bfr;
        this.battlefield = bfr.getModel();
        this.choice = choice;
        this.scheduler = scheduler;
        this.validate = false;
        this.undoable = undoable;
        this.endturnCommandOnly = endturnCommandOnly;
    }

    /**
     * you need to valid the inputs : actor as well as target before execute the command.
     */
    public void apply(){
        if(validate){
            execute();
        }
    }

    public abstract void init();                                       // called when a command is fetched
    protected abstract void execute();
    public abstract boolean isExecuting();
    public abstract boolean isExecutionCompleted();

    // can be deleted afterwards
    public boolean isUndoable(){
        return undoable;
    }

    // is only callable when the unit turn is ending
    public boolean isEndTurnCommandOnly(){
        return endturnCommandOnly;
    }

    /**
     * PLAYER ORIENTED METHOD
     *
     * TARGET CHECKING
     * @return whether or not THIS SPECIFIC TARGET is at range by the actor performing the given action if one's is standing the tile (rowActor, colActor)
     * while ignoring the actor's history and the unit other requirements to actually perform this action, namely : weapon/item and ability requirements.
     */
    public boolean isTargetValid() {
        if(!validate)
            validate = isTargetValid(rowActor, colActor, rowTarget, colTarget);
        return validate;
    }

    /*
    required for testing retaliation availability for the attacked target without copy and paste the code of the
    BattleCommand.isTargetValid() method.
     */
    public abstract boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0);



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
    public abstract boolean atActionRange(int row, int col, IUnit actor);

    public boolean atActionRange(){
        if(battlefield.isTileOccupied(rowActor, colActor))
            return atActionRange(rowActor, colActor, battlefield.getUnit(rowActor, colActor));
        return false;
    }

    /**
     *
     * @return fetch all tiles where the given unit is at range to perform the chosen action
     */
    public Array<int[]> getActionArea() {
        Array<int[]> actionArea = new Array<int[]>();
        if(battlefield.isTileOccupied(rowActor, colActor)) {
            switch (choice.getRangeType()) {
                case MOVE:
                    actionArea = battlefield.getMoveArea(rowActor, colActor);
                    break;
                case WEAPON:
                    IUnit actor = battlefield.getUnit(rowActor, colActor);
                    int rangeMin = actor.getCurrentWeaponRangeMin(rowActor, colActor, battlefield);
                    int rangeMax = actor.getCurrentWeaponRangeMax(rowActor, colActor, battlefield);
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


    /**
     *
     * @param rowImpactTile
     * @param colImpactTile
     * @return get all possible target tile knowing that the given tile is within the impact area
     */
    protected Array<int[]> getTargetFromCollateral(int rowImpactTile, int colImpactTile) {
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

    @Override
    public void undo() { }

    @Override
    public void redo() { }



    //------------------- HELPER METHODS -----------------------------



    public enum TargetType{
        ALLY,
        WOUNDED_ALLY,
        ENEMY
    }

    protected boolean isTargetValid(int rowActor0, int colActor0, int rowTarget0, int colTarget0, boolean weaponBasedRange, TargetType targetType){
        boolean valid = false;
        if(battlefield.isTileOccupied(rowActor0, colActor0)){
            IUnit actor = battlefield.getUnit(rowActor0, colActor0);
            int rangeMin = (weaponBasedRange) ? actor.getCurrentWeaponRangeMin(rowActor0, colActor0, battlefield) : choice.getRangeMin();
            int rangeMax = (weaponBasedRange) ? actor.getCurrentWeaponRangeMax(rowActor0, colActor0, battlefield) : choice.getRangeMax();
            int dist = Utils.dist(rowActor0, colActor0, rowTarget0, colTarget0);
            if (rangeMin <= dist && dist <= rangeMax) {
                switch (targetType){
                    case ALLY:
                        if(battlefield.isTileOccupiedByAlly(rowTarget0, colTarget0, actor.getAllegeance()))
                            valid = true;
                        break;
                    case WOUNDED_ALLY:
                        if(battlefield.isTileOccupiedByAlly(rowTarget0, colTarget0, actor.getAllegeance())
                                && battlefield.getUnit(rowTarget0, colTarget0).isWounded())
                            valid = true;
                        break;
                    case ENEMY:
                        if(battlefield.isTileOccupiedByFoe(rowTarget0, colTarget0, actor.getAllegeance()))
                            valid = true;
                        break;
                }
            }
        }
        return valid;
    }


    protected boolean atActionRange(int row, int col, IUnit actor, boolean weaponBasedRange, TargetType targetType){
        boolean targetAtRange = false;
        int[] unitPos = battlefield.getUnitPos(actor);
        int rangeMin = (weaponBasedRange) ? actor.getCurrentWeaponRangeMin(unitPos[0], unitPos[1], battlefield) : choice.getRangeMin();
        int rangeMax = (weaponBasedRange) ? actor.getCurrentWeaponRangeMax(unitPos[0], unitPos[1], battlefield) : choice.getRangeMax();
        int dist;
        for(int r = row - rangeMin; r <= row + rangeMax; r++ ){
            for(int c = col - rangeMin; c <= col + rangeMax; c++ ){
                dist = Utils.dist(row, col, r, c);
                if(rangeMin <= dist && dist <= rangeMax){
                    switch (targetType){
                        case ALLY:
                            if(battlefield.isTileOccupiedByAlly(r, c, actor.getAllegeance())) {
                                targetAtRange = true;
                                continue;
                            }
                            break;
                        case WOUNDED_ALLY:
                            if(battlefield.isTileOccupiedByAlly(r, c, actor.getAllegeance()) && battlefield.getUnit(r, c).isWounded()) {
                                targetAtRange = true;
                                continue;
                            }
                            break;
                        case ENEMY:
                            if(battlefield.isTileOccupiedByFoe(r, c, actor.getAllegeance())) {
                                targetAtRange = true;
                                continue;
                            }
                            break;
                            default:
                    }
                }
            }
            if(targetAtRange)
                continue;
        }

        return targetAtRange;
    }



    //------------------ GETTERS & SETTERS ---------------------------


    public Data.ActionChoice getActionChoice() {
        return choice;
    }

    public void setBattlefield(BattlefieldRenderer battlefieldRenderer){
        this.battlefield = battlefieldRenderer.getModel();
        this.battlefieldRenderer = battlefieldRenderer;

    }

    public boolean setActor(int rowActor, int colActor) {
        if(battlefield.isTileOccupied(rowActor, colActor)) {
            this.rowActor = rowActor;
            this.colActor = colActor;
            validate = false;
            return true;
        }
        return false;
    }

    public void setTarget(int rowTarget, int colTarget){
        if(battlefield.isTileExisted(rowTarget, colTarget)){
            this.rowTarget = rowTarget;
            this.colTarget = colTarget;
            validate = false;
        }
    }


}
