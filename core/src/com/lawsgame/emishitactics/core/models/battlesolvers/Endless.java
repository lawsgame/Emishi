package com.lawsgame.emishitactics.core.models.battlesolvers;

import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.interfaces.BattleSolver;

public class Endless extends BattleSolver {


    public Endless() {
        super(1, 100);
    }

    @Override
    public boolean isBattleOver(Battlefield battlefield) {
        return false;
    }
}
