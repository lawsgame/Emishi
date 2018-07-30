package com.lawsgame.emishitactics.core.models;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ArmyTest {
    Army army1;
    Army army2;
    Unit phillipe;
    Unit eric;
    Unit louis;
    Unit charles;
    Unit claude;
    Unit herald;
    Unit richard;


    @Before
    public void before(){
        army1 = new Army(Data.Allegeance.ALLY, true);
        army2 = new Army(Data.Allegeance.ENEMY, true);
        phillipe = new Unit("phillipe");
        phillipe.setLeadership(20);
        eric = new Unit("eric");
        eric.setLeadership(20);
        louis = new Unit("charles");
        louis.setLeadership(20);
        charles = new Unit("charles");
        claude = new Unit("claude");
        herald = new Unit("herald");
        richard = new Unit("Richard");
    }

    @Test
    public void test1(){
        army1.add(phillipe);
        army1.add(eric);
        army1.add(claude);
        army1.add(herald);

        army2.add(louis);
        army2.add(charles);

        assertTrue(army1.contains(phillipe));
        assertTrue(army1.isUnitReserve(phillipe));
        assertTrue(!army1.contains(charles));
        assertTrue(!army1.isUnitReserve(charles));
        assertTrue(!army1.contains(richard));
        assertTrue(!army1.isUnitReserve(richard));

        army1.add(charles);

        assertTrue(army1.isUnitReserve(charles));
        assertTrue(!army2.isUnitReserve(charles));

        army2.remove(charles);

        assertTrue(army1.isUnitReserve(charles));

        army1.remove(charles);

        assertTrue(!army1.isUnitReserve(charles));

        army2.add(charles);
        army1.appointWarChief(phillipe);
        army1.appointSoldier(claude, 0);

        assertTrue(!army1.isUnitMobilized(phillipe));
        assertTrue(!army1.isUnitMobilized(claude));
        assertTrue(army1.getNbOfSquads() == 0);

        army1.appointWarLord(phillipe);
        army1.appointSoldier(claude, 0);

        assertTrue(army1.isUnitMobilized(phillipe));
        assertTrue(army1.isUnitMobilized(claude));
        assertTrue(army1.getNbOfSquads() == 1);
        assertTrue(army1.getWarlord() == phillipe);
        assertTrue(army1.getAllSquads().get(0).size == 2);

        army2.appointWarLord(phillipe);

        assertTrue(army1.isUnitMobilized(phillipe));
        assertTrue(!army2.isUnitMobilized(phillipe));
        assertTrue(army1.getNbOfSquads() == 1);
        assertTrue(army1.getWarlord() == phillipe);
        assertTrue(army1.getAllSquads().get(0).size == 2);

        army2.add(phillipe);
        army2.appointWarLord(phillipe);

        assertTrue(!army1.isUnitMobilized(phillipe));
        assertTrue(army2.isUnitMobilized(phillipe));
        assertTrue(army1.getNbOfSquads() == 0);
        assertTrue(army2.getWarlord() == phillipe);
        assertTrue(army2.getAllSquads().get(0).size == 1);
        assertTrue(army1.isUnitReserve(claude));


    }
}
