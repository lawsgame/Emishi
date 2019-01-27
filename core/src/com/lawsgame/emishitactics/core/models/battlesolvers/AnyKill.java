package com.lawsgame.emishitactics.core.models.battlesolvers;

import com.lawsgame.emishitactics.core.models.BattleSolver;

public class AnyKill extends BattleSolver {


    @Override
    public boolean isBattleOver() {
        return false;
    }
}
