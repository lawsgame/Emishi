package com.lawsgame.emishitactics.core.models;

import com.lawsgame.emishitactics.core.models.interfaces.MilitaryForce;

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
                battlefield.setTile(r, c, Data.TileType.PLAIN, false);
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
        assertTrue(battlefield.getSolver().isBattleOver());

        Unit phillipe = new Unit("Phillipe", Data.UnitTemplate.SOLAR_KNIGHT, Data.WeaponType.SWORD);
        Unit debby = new Unit("Debby", Data.UnitTemplate.SOLAIRE, Data.WeaponType.SWORD);
        army1.add(phillipe);
        army3.add(debby);
        battlefield.randomlyDeploy(army1);
        battlefield.randomlyDeploy(army3);

        assertTrue(battlefield.armyTurnOrder.size() == 4);
        assertTrue(battlefield.getSolver().isBattleOver());

        battlefield.nextArmy();

        assertTrue(battlefield.getCurrentArmy() == null);
        assertTrue(battlefield.armyTurnOrder.size() == 0);

        army1.appointWarLord(phillipe);
        army3.appointWarLord(debby);
        battlefield.deploy(3, 3, phillipe, false);
        battlefield.deploy(3, 4, debby, false);

        assertTrue(battlefield.armyTurnOrder.size() == 2);
        assertTrue(!battlefield.getSolver().isBattleOver());

        //battlefield.nextArmy();
        MilitaryForce army = battlefield.getCurrentArmy();

        assertTrue(army == army1);

        battlefield.nextArmy();
        army = battlefield.getCurrentArmy();

        assertTrue(army == army3);

        battlefield.nextArmy();
        army = battlefield.getCurrentArmy();

        assertTrue(army == army1);

        army3.setDone(true, false);
        battlefield.nextArmy();
        army = battlefield.getCurrentArmy();

        assertTrue(debby.isDone());
        assertTrue(army == army3);

        phillipe.takeDamage(300, false, false, 1f);
        battlefield.nextArmy();
        army = battlefield.getCurrentArmy();

        assertTrue(phillipe.isDead());
        assertTrue(army == army3);

        battlefield.nextArmy();
        army = battlefield.getCurrentArmy();


        assertTrue(army == army3);

    }
}
