package com.lawsgame.emishitactics.core.models.interfaces;

import com.lawsgame.emishitactics.core.models.Player;

public interface BattleTurnManager {

    void init(Player player);
    void addArmy(MilitaryForce army);
    void pushArmyTurnForward(MilitaryForce army);
    void nextArmy();
    MilitaryForce getCurrentArmy();
    MilitaryForce getArmyByName(String keyname);
    int getTurn();

}
