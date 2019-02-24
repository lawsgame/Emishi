package com.lawsgame.emishitactics.core.models.interfaces;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.battlesolvers.Endless;

/**
 * Allow to check the current state of the occuring battle
 *  1) {@link #isBattleOver(Battlefield)} allow to verify if a battle is over
 */
public abstract class BattleSolver {

    protected  int turnMin;
    protected  int turrMax;
    protected Array<Objective> objectives;

    public BattleSolver(int turnMin, int turnMax){
        this.turnMin = turnMin;
        this.turrMax = turnMax;
        this.objectives = new Array<Objective>();
    }

    public static BattleSolver getDefaultValue() {
        return new Endless();
    }

    public abstract boolean isBattleOver(Battlefield battlefield);

    public int getVictorySecondaryBonus(Battlefield battlefield){
        int bonus = 0;
        for(int i = 0; i < objectives.size; i++){
            if(objectives.get(i).isMet(battlefield)){
                bonus += objectives.get(i).getValue();
            }
        }
        return bonus;
    }

    public Array<Objective> getObjectives() {
        return objectives;
    }

    public int getTurnMin() {
        return turnMin;
    }

    public int getTurrMax() {
        return turrMax;
    }
}
