package com.lawsgame.emishitactics.core.models;

import com.lawsgame.emishitactics.core.models.interfaces.MilitaryForce;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class BattleTurnManagerImpTest {
    private Battlefield bf;
    private BattleTurnManagerImp solver;
    private MilitaryForce mf1;
    private MilitaryForce mf2;
    private MilitaryForce mf3;

    @Before
    public void before(){
        bf = BattlefieldTest.createPlainBF(10,10);
        solver = new BattleTurnManagerImp(bf);
        mf1 = new Army(Data.Affiliation.ALLY, "mf1");
        mf2 = new Army(Data.Affiliation.ALLY, "mf2");
        mf3 = new Army(Data.Affiliation.ALLY, "mf3");
    }

    @Test
    public void testAddArmy(){
        solver.addArmy(mf1);
        assertSame(solver.getArmyTurnOrder().size(), 1);
        solver.addArmy(mf1);
        assertSame(solver.getArmyTurnOrder().size(), 1);
        solver.addArmy(mf2);
        assertSame(solver.getCurrentArmy(), mf1);
        assertSame(solver.getArmyTurnOrder().size(), 2);
        solver.addArmy(null);
        assertSame(solver.getArmyTurnOrder().size(), 2);


    }

    @Test
    public void testContaines(){
        solver.addArmy(mf1);
        assertTrue(solver.contains("mf1"));
        assertTrue(solver.contains(mf1));
    }

    @Test
    public void testPushArmyForward(){
        solver.addArmy(mf1);
        solver.addArmy(mf2);
        solver.addArmy(mf3);
        solver.pushArmyTurnForward(mf3);
        assertSame(solver.getCurrentArmy(), mf3);
        solver.pushArmyTurnForward(mf2);
        assertSame(solver.getCurrentArmy(), mf2);
        solver.pushArmyTurnForward(null);
        assertSame(solver.getCurrentArmy(), mf2);
        assertSame(solver.getTurn(), 1);
    }

    @Test
    public void testNextArmy(){
        solver.addArmy(mf1);
        solver.addArmy(mf2);
        solver.addArmy(mf3);
        solver.nextArmy(false);
        assertSame(solver.getCurrentArmy(), mf2);
        solver.nextArmy(false);
        solver.nextArmy(false);
        assertSame(solver.getCurrentArmy(), mf1);
    }

    @Test
    public void testGetTurn(){
        solver.addArmy(mf1);
        solver.addArmy(mf2);
        assertSame(solver.getTurn(), 1);

        solver.nextArmy(false);
        solver.nextArmy(false);
        assertSame(1, solver.getTurn());

        solver.setFirstTurnArmy(mf1);

        for(int i = 2; i < 10; i++) {
            solver.nextArmy(false);
            solver.nextArmy(false);
            assertSame(i, solver.getTurn());
        }

    }


}
