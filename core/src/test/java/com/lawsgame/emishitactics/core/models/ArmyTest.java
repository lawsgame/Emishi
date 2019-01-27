package com.lawsgame.emishitactics.core.models;

import com.lawsgame.emishitactics.core.constants.StringKey;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ArmyTest {

    Army army1;
    Army army2;
    Army prepared;

    Unit neron;
    Unit tibere;
    Unit claude;

    Unit tom;
    Unit alice;
    Unit gregory;
    Unit meg;
    Unit lizzy;
    Unit grimalkin;

    // to print the army "prepared"
    static boolean once = false;

    @Before
    public void before(){

        army1 = new Army(Data.Affiliation.ALLY, StringKey.UNNAMED_ARMY_NAME);
        army2 = new Army(Data.Affiliation.ENEMY_0, StringKey.UNNAMED_ARMY_NAME);

        neron = new Unit("neron", Data.UnitTemplate.getDefaultValue(false), Data.WeaponType.SWORD);
        tibere = new Unit("tibere", Data.UnitTemplate.getDefaultValue(false), Data.WeaponType.SWORD);
        claude = new Unit("claude", Data.UnitTemplate.getDefaultValue(false), Data.WeaponType.SWORD);
        neron.setLeadership(20);
        tibere.setLeadership(20);


        prepared = new Army(Data.Affiliation.ALLY, StringKey.UNNAMED_ARMY_NAME);

        tom = new Unit("tom", Data.UnitTemplate.getDefaultValue(false), Data.WeaponType.SWORD);
        gregory = new Unit("gregory", Data.UnitTemplate.getDefaultValue(false), Data.WeaponType.SWORD);
        meg = new Unit("meg", Data.UnitTemplate.getDefaultValue(false), Data.WeaponType.SWORD);
        alice = new Unit("alice", Data.UnitTemplate.getDefaultValue(false), Data.WeaponType.SWORD);
        lizzy = new Unit("lizzy", Data.UnitTemplate.getDefaultValue(false), Data.WeaponType.SWORD);
        grimalkin = new Unit("grimalkin", Data.UnitTemplate.getDefaultValue(false), Data.WeaponType.SWORD);

        prepared.add(tom);
        prepared.add(gregory);
        prepared.add(meg);
        prepared.add(alice);
        prepared.add(lizzy);
        prepared.add(grimalkin);

        tom.setLeadership(20);
        gregory.setLeadership(20);


        prepared.appointWarLord(tom);
        prepared.appointWarChief(gregory);
        prepared.appointSoldier(meg, 0);
        prepared.appointSoldier(alice, 0);
        prepared.appointSkirmisher(lizzy);


        if(once) {
            once = false;
            System.out.println(prepared.toLongString());
        }

    }

    @Test
    public void addRemoveTest(){
        army1.add(neron);
        army1.add(tibere);
        army1.add(claude);

        assertTrue(army1.isUnitReserve(neron));
        assertEquals(army1.getReserve().size, 3);

        army1.remove(claude);

        assertTrue(army1.isUnitReserve(neron));
        assertEquals(army1.getReserve().size, 2);

        army2.add(neron);

        assertTrue(!army1.isUnitReserve(neron));
        assertTrue(army2.isUnitReserve(neron));
        assertEquals(army1.getReserve().size, 1);
        assertEquals(army2.getReserve().size, 1);
    }

    @Test
    public void appointWarlordTest(){
        army1.add(neron);
        army1.add(tibere);
        army1.add(claude);
        army1.appointWarLord(neron);

        assertEquals(army1.getWarlord(), neron);
        assertTrue(!army1.isUnitReserve(neron));
        assertEquals(army1.getReserve().size, 2);

        army1.appointWarLord(tibere);

        assertEquals(army1.getWarlord(), tibere);
        assertTrue(army1.isUnitReserve(neron));
        assertEquals(army1.getReserve().size, 2);
    }

    @Test
    public void appointWarchiefTest(){
        army1.add(neron);
        army1.appointWarLord(neron);
        army1.appointWarChief(tibere);
        assertTrue(!tibere.isWarChief());

        army1.add(tibere);
        army1.appointWarChief(tibere);
        assertTrue(tibere.isWarChief());

        army1.appointWarChief(tibere, 0);
        assertTrue(army1.isWarlord(neron));

        army1.add(claude);
        army1.appointWarChief(claude, 0);
        assertTrue(army1.isUnitReserve(neron));
        assertTrue(army1.isUnitReserve(tibere));
        assertTrue(army1.isWarlord(claude));
    }

    @Test
    public void appointSoldierTest(){
        army1.add(neron);
        army1.add(tibere);
        army1.appointSoldier(claude, 0);
        assertTrue(!army1.isUnitRegular(claude));

        army1.appointWarLord(neron);
        army1.appointSoldier(claude, 0);
        assertTrue(!army1.isUnitRegular(claude));

        army1.add(claude);
        army1.appointSoldier(claude, 0);
        assertTrue(army1.isUnitRegular(claude));
        assertTrue(!claude.isWarChief());

        army1.appointSoldier(tibere,0, 0);
        assertTrue(tibere.isWarlord());
        assertTrue(army1.isUnitReserve(claude));
        assertTrue(army1.isUnitReserve(neron));
    }

    @Test
    public void appointSkirmisher(){
        army1.add(neron);
        army1.appointSkirmisher(neron);
        assertTrue(army1.isUnitSkirmisher(neron));
    }

    @Test
    public void disengageTest(){
        prepared.disengage(lizzy);
        assertTrue(prepared.isUnitReserve(lizzy));
        assertEquals(prepared.getMobilizedUnits(false).size, 4);

        prepared.disengage(alice);
        assertTrue(prepared.isUnitReserve(alice));
        assertEquals(prepared.getMobilizedUnits(false).size, 3);


        prepared.disengage(tom);
        assertTrue(prepared.isUnitReserve(tom));
        assertEquals(prepared.getMobilizedUnits(false).size, 0);
    }

    @Test
    public void disbandAllSquadsTest(){
        prepared.disbandAllSquads();
        assertEquals(prepared.getMobilizedUnits(false).size, 0);
        assertEquals(prepared.getReserve().size, 6);
    }

    @Test
    public void testGetNbOfSquads(){
        assertEquals(prepared.getNbOfSquads(), 2);

        prepared.disbandAllSquads();
        assertEquals(prepared.getNbOfSquads(), 0);
    }

    @Test
    public void testGetSquadSize(){
        assertEquals(prepared.getSquad(tom, false).size, 3);
        assertEquals(prepared.getSquad(lizzy, false).size, 0);
        assertEquals(prepared.getSquad(grimalkin, false).size, 0);

        tom.kill(false);

        assertEquals(prepared.getSquad(tom, false).size, 3);
        assertEquals(prepared.getSquad(tom, true).size, 2);
    }

    @Test
    public void testGetSquadBanner(){
        assertNull(prepared.getSquadBanner(lizzy, false));
        assertNull(prepared.getSquadBanner(grimalkin, false));
        assertNotNull(prepared.getSquadBanner(tom, false));
        assertNotNull(prepared.getSquadBanner(tom, true));

        tom.kill(false);
        assertNotNull(prepared.getSquadBanner(tom, false));
        assertNull(prepared.getSquadBanner(tom, true));
    }

    @Test
    public void testDone(){
        assertFalse(prepared.isDone());
        prepared.setDone(true, false);
        assertTrue(prepared.isDone());
    }

}
