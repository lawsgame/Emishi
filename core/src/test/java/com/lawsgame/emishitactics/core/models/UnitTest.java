package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Props;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class UnitTest {
    private Unit.Army army1;
    private Unit.Army army2;
    private final int highLd = 11;
    private final int lowLd = 3;

    private Unit clovus;
    private Unit merovee;
    private Unit jean;
    private Unit phillipe;
    private Unit alexandre;
    private Unit pierre;
    private Unit andre;
    private Unit luc;

    private String name1 = "Clovis";
    private String name2 = "Merovee";
    private String name3 = "jean";
    private String name4 = "phillipe";
    private String name5 = "alexandre";
    private String name6 = "pierre";
    private String name7 = "andré";
    private String name8 = "luc";


    public UnitTest(){ }

    static boolean armyShown;
    @BeforeClass
    public static void beforeClass(){
        System.out.println("\n---+++$$$ Unit TEST $$$+++---\n");
        armyShown = false;
    }

    @Before
    public void before(){
        clovus = new Unit(true, Props.UnitTemplate.CONSCRIPT, 12, Props.Weapon.BO, Props.Weapon.SAI, true);
        clovus.setName(name1);
        clovus.setLeadership(highLd);
        merovee = new Unit(false, Props.UnitTemplate.EMISHI_TRIBESMAN, 19, Props.Weapon.WARABITE, Props.Weapon.NONE, true);
        merovee.setName(name2);
        merovee.setLeadership(lowLd);
        jean = new Unit(Props.UnitTemplate.CONSCRIPT, 9);
        jean.setName(name3);
        jean.setLeadership(lowLd);
        phillipe = new Unit(Props.UnitTemplate.CONSCRIPT, 5);
        phillipe.setName(name4);
        phillipe.setLeadership(lowLd);
        alexandre = new Unit(Props.UnitTemplate.CONSCRIPT, 7);
        alexandre.setName(name5);
        alexandre.setLeadership(lowLd);
        pierre = new Unit(Props.UnitTemplate.CONSCRIPT, 14);
        pierre.setName(name6);
        pierre.setLeadership(highLd);
        andre = new Unit(Props.UnitTemplate.CONSCRIPT, 13);
        andre.setName(name7);
        andre.setLeadership(lowLd);
        luc = new Unit(Props.UnitTemplate.CONSCRIPT, 15);
        luc.setName(name8);
        luc.setLeadership(lowLd);

        army1 = new Unit.Army(clovus, true);
        army2 = new Unit.Army(pierre, false);

        army1.appointWarChief(merovee);
        army1.appointSoldier(jean,0);
        army1.appointSoldier(phillipe,0);
        army1.appointSoldier(andre,1);
        army1.appointSoldier(alexandre,1);
        army1.add(luc);


        if(!armyShown) {
            //System.out.println(army1.toString());
            //System.out.println(army2.toString() + "\n");
            armyShown = true;
        }

    }

    /**
     * for on-the-fly tests
     */
    @Test
    public void testPrint(){

    }

    @Test
    public void testAllMethodsArmy(){
        Props.UnitAppointmentErrorMsg msg;

        army1.disengage(merovee);

        assertTrue(army1.getNonMobilizedUnits().size == 4);
        assertTrue( army1.getNonMobilizedUnits().contains(andre, true));
        assertTrue(army1.getAllSquads().size == 1);

        army1.remove(phillipe);

        assertTrue(army1.getSquad(army1.getWarlord()).size == 2);
        assertTrue(army1.getNonMobilizedUnits().size == 4);
        assertTrue(!army1.contains(phillipe));

        army1.appointWarLord(jean);
        army1.appointWarChief(alexandre);
        army2.appointWarLord(clovus);
        army2.appointWarChief(andre);
        army2.appointSoldier(pierre, 1, 1);
        army2.appointSoldier(merovee, 1, 0);
        army2.appointSoldier(andre, 0);

        assertTrue(merovee.isWarChief());
        assertTrue(army2.getNonMobilizedUnits().size == 1);
        assertTrue(army2.getSquad(clovus).contains(andre, true));
    }


    @Test
    public void testPickWeapon(){
        Array<Props.Weapon> availableWeapon = new Array< Props.Weapon>();
        Array<Props.Weapon> availableWeaponAfterPromotion = new Array< Props.Weapon>();
        availableWeapon.addAll(clovus.template.getJob().getAvailableWeapons());
        availableWeaponAfterPromotion.addAll(clovus.template.getJob().getAvailableWeaponsAfterPromotion());

        Props.Weapon primaryW;
        Props.Weapon secondaryW;
        for(int i=0; i<10; i++){
            primaryW = clovus.pickWeapon(true);

            assertTrue(availableWeapon.contains(primaryW, true));

            clovus.primaryWeapon = primaryW;
            secondaryW = clovus.pickWeapon(false);

            assertTrue(availableWeaponAfterPromotion.contains(secondaryW, true) && secondaryW != primaryW);
        }
    }


    @Test
    public void testSameSquadAs(){
        assertTrue(jean.sameSquadAs(phillipe));
        assertTrue(jean.sameSquadAs(clovus));
        assertTrue(!jean.sameSquadAs(andre));
        assertTrue(!jean.sameSquadAs(pierre));
        assertTrue(!jean.sameSquadAs(alexandre));
        assertTrue(!alexandre.sameSquadAs(luc));
    }


    @Test
    public void testAligmentAs(){
        assertTrue(jean.sameAligmentAs(alexandre));
        assertTrue(jean.sameAligmentAs(merovee));
        assertTrue(!jean.sameAligmentAs(pierre));
        assertTrue(pierre.sameAligmentAs(pierre));
    }

    @Test
    public void testIsWeaponAvailable(){
        assertTrue(merovee.isWeaponAvailable(Props.Weapon.WARABITE, true));
        assertTrue(!merovee.isWeaponAvailable(Props.Weapon.KANABO, true));
        assertTrue(merovee.isWeaponAvailable(Props.Weapon.KANABO, false));
        assertTrue(!merovee.isWeaponAvailable(Props.Weapon.SAI, false));
        assertTrue(merovee.has(Props.Weapon.YUMI) || merovee.isWeaponAvailable(Props.Weapon.YUMI, true));
    }

    @Test
    public void testSetWeapon(){
        assertTrue(!clovus.setWeapon(Props.Weapon.KANABO, true));
        assertTrue(!clovus.setWeapon(Props.Weapon.KANABO, false));
        assertTrue(clovus.setWeapon(Props.Weapon.KATANA, true));
        assertTrue(merovee.setWeapon(Props.Weapon.WARABITE, true));
        assertTrue(merovee.has(Props.Weapon.YUMI) || merovee.setWeapon(Props.Weapon.YUMI, true));
        assertTrue(merovee.setWeapon(Props.Weapon.KANABO, false));
    }


}
