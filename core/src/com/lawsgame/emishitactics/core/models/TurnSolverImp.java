package com.lawsgame.emishitactics.core.models;

import com.lawsgame.emishitactics.core.models.interfaces.MilitaryForce;
import com.lawsgame.emishitactics.core.models.interfaces.TurnSolver;

import java.util.LinkedList;


/**
 * multiple process are automatized
 *  - the addition of a new army is warranted once the first man is deployed
 *  - a wipe-out force is removed while calling {@link TurnSolverImp#nextArmy()}
 *  - turn is incremented when the player army is the new current army;
 */
public class TurnSolverImp implements TurnSolver {

    private int turn;
    private final Battlefield battlefield;
    // if not set to NULL, the turn count will be incremented
    private MilitaryForce firstTurnArmy;
    public LinkedList<MilitaryForce> armyTurnOrder;

    public TurnSolverImp(Battlefield battlefield) {
        this.turn = 1;
        this.battlefield = battlefield;
        this.firstTurnArmy = null;
        this.armyTurnOrder = new LinkedList<MilitaryForce>();

    }

    @Override
    public void init(Player player){
        pushArmyTurnForward(player.getArmy());
        setFirstTurnArmy(player.getArmy());
    }

    public boolean contains(String armyName){
        for(int i = 0; i < armyTurnOrder.size(); i++){
            if(armyTurnOrder.get(i).getName().equals(armyName)){
                return true;
            }
        }
        return false;
    }

    public boolean contains(MilitaryForce army){
        if(army != null) {
            for (int i = 0; i < armyTurnOrder.size(); i++) {
                if (armyTurnOrder.get(i) == army) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void addArmy(MilitaryForce army){
        if(!contains(army) && army != null) {
            armyTurnOrder.offer(army);
        }
    }

    /**
     * is required to be called after all parties are deployed.
     */
    @Override
    public void pushArmyTurnForward(MilitaryForce army){
        if(contains(army)) {
            while (army != armyTurnOrder.peek()) {
                armyTurnOrder.offer(armyTurnOrder.pop());
            }
        }
    }

    @Override
    public void nextArmy() {
        nextArmy(true);
    }

    /**
     * get next army contains within the ring with at least one  unit still fighting
     */
    public void nextArmy(boolean checkArmyStillFighting){
        if(!armyTurnOrder.isEmpty()) {
            armyTurnOrder.offer(armyTurnOrder.pop());
            // remove next army if the current army does not fit the requirements to stay in the turn loop.
            while (!armyTurnOrder.isEmpty()
                    && (armyTurnOrder.peek() == null
                        || (checkArmyStillFighting && !battlefield.isDeployedTroopsStillFighting(armyTurnOrder.peek())))) {
                armyTurnOrder.pop();
            }
            // increment turn if requires
            if(armyTurnOrder.peek() == firstTurnArmy){
                turn++;
            }
        }
    }

    @Override
    public MilitaryForce getCurrentArmy(){
        return armyTurnOrder.peek();
    }

    /**
     *
     * @param keyname : name of the looked after army.
     * @return the first army that goes by {@param keyname}
     */
    @Override
    public MilitaryForce getArmyByName(String keyname){
        for(int i = 0; i < armyTurnOrder.size(); i++){
            if(armyTurnOrder.get(i).getName().equals(keyname)){
                return armyTurnOrder.get(i);
            }
        }
        return null;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public MilitaryForce getFirstTurnArmy() {
        return firstTurnArmy;
    }

    public void setFirstTurnArmy(MilitaryForce firstTurnArmy) {
        this.firstTurnArmy = firstTurnArmy;
    }

    public Battlefield getBattlefield() {
        return battlefield;
    }

    public LinkedList<MilitaryForce> getArmyTurnOrder() {
        return armyTurnOrder;
    }
}
