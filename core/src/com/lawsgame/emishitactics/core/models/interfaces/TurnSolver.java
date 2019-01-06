package com.lawsgame.emishitactics.core.models.interfaces;

import com.lawsgame.emishitactics.core.models.Player;

public interface TurnSolver {

    void init(Player player);
    void addArmy(MilitaryForce army);
    void pushArmyTurnForward(MilitaryForce army);
    void nextArmy();
    MilitaryForce getCurrentArmy();
    MilitaryForce getArmyByName(String keyname);
    int getTurn();

}
