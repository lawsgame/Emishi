package com.lawsgame.emishitactics.core.models.battlesolvers;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.interfaces.BattleSolver;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;

public class KillAll extends BattleSolver {

    public KillAll(int turnMin, int turnMax) {
        super(turnMin, turnMax);
    }

    /*
     *
     * @return true if at least two affiliation are represented by two still well and alive units, AND the player army is still active
     */
    @Override
    public boolean isBattleOver(Battlefield battlefield){
        Array<Data.Affiliation> affiliations = new Array<Data.Affiliation>();
        boolean playerArmyRemain = false;
        for (int r = 0; r < battlefield.getNbRows(); r++) {
            for (int c = 0; c < battlefield.getNbColumns(); c++) {
                if (battlefield.isTileOccupied(r, c)) {
                    Unit unit = battlefield.getUnit(r, c);
                    if (unit.belongToAnArmy()
                            && !unit.isOutOfAction()
                            && !affiliations.contains(unit.getArmy().getAffiliation(), true)) {
                        affiliations.add(unit.getArmy().getAffiliation());
                        if(unit.getArmy().isPlayerControlled()) {
                            playerArmyRemain = true;
                        }
                    }
                }
            }
        }
        return affiliations.size < 2 || !playerArmyRemain;
    }
}
