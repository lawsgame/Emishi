package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;

public abstract class BattleSolver {

    protected Battlefield battlefield;

    public abstract boolean isBattleOver();

    public void setBattlefield(Battlefield battlefield){
        this.battlefield = battlefield;
    }


    //--------------- classes -----------------


    public static class KillAll extends BattleSolver{


        /*
         *
         * @return true if at least two affiliation are represented by two still well and alive units, AND the player army is still active
         */
        @Override
        public boolean isBattleOver(){
            Array<Data.Affiliation> affiliations = new Array<Data.Affiliation>();
            boolean playerArmyRemain = false;
            for (int r = 0; r < battlefield.getNbRows(); r++) {
                for (int c = 0; c < battlefield.getNbColumns(); c++) {
                    if (battlefield.isTileOccupied(r, c)) {
                        IUnit unit = battlefield.getUnit(r, c);
                        if (unit.isMobilized()
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
}
