package com.lawsgame.emishitactics.core.models;

import com.lawsgame.emishitactics.core.models.interfaces.IArmy;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BattlefieldTest {
    private Battlefield battlefield;

    @Before
    public void before(){
        battlefield = new Battlefield(9,9);
        for(int r = 0; r < 7; r++){
            for(int c = 0; c < 7; c++){
                battlefield.getTiles()[r][c] = Data.TileType.PLAIN;
            }
        }
    }

    /**
     * for on-the-fly tests
     */
    @Test
    public void testPrint(){


    }

    @Test
    public void testArmyQueue(){
        Army army1 = Army.createPlayerArmyTemplate();
        Army army2 = new Army(Data.Affiliation.ALLY);
        Army army3 = new Army(Data.Affiliation.ENEMY_0);
        Army army4 = new Army(Data.Affiliation.ENEMY_0);

        battlefield.randomlyDeploy(army1);
        battlefield.randomlyDeploy(army2);
        battlefield.randomlyDeploy(army2);
        battlefield.randomlyDeploy(army3);
        battlefield.randomlyDeploy(army4);

        assertTrue(battlefield.armyTurnOrder.size() == 4);
        assertTrue(battlefield.isBattleOver());

        Unit phillipe = new Unit("Phillipe");
        Unit debby = new Unit("Debby");
        army1.add(phillipe);
        army3.add(debby);
        battlefield.randomlyDeploy(army1);
        battlefield.randomlyDeploy(army3);

        assertTrue(battlefield.armyTurnOrder.size() == 4);
        assertTrue(battlefield.isBattleOver());
        assertTrue(battlefield.getNextArmy() == null);
        assertTrue(battlefield.armyTurnOrder.size() == 0);

        army1.appointWarLord(phillipe);
        army3.appointWarLord(debby);
        battlefield.deploy(3, 3, phillipe, false);
        battlefield.deploy(3, 4, debby, false);

        assertTrue(battlefield.armyTurnOrder.size() == 2);
        assertTrue(!battlefield.isBattleOver());

        IArmy army = battlefield.getNextArmy();

        assertTrue(army == army1);

        army = battlefield.getNextArmy();

        assertTrue(army == army3);

        army = battlefield.getNextArmy();

        assertTrue(army == army1);

        army3.setDone(true, false);
        army = battlefield.getNextArmy();

        assertTrue(debby.isDone());
        assertTrue(army == army3);

        phillipe.applyDamage(300, false);
        army = battlefield.getNextArmy();

        assertTrue(phillipe.isDead());
        assertTrue(army == army3);

        army = battlefield.getNextArmy();


        assertTrue(army == army3);

    }
}
