package com.lawsgame.emishitactics.core.models;

public abstract class BattleSolver {

    protected Battlefield battlefield;

    public abstract boolean isBattleOver();

    public void setBattlefield(Battlefield battlefield){
        this.battlefield = battlefield;
    }


}
