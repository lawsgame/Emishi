package com.lawsgame.emishitactics.core.models;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BattlefieldTest {
    Battlefield battlefield;

    @Before
    public void before(){
        battlefield = new Battlefield(9,9);
        battlefield.setAsPlain();
    }

    @BeforeClass
    public static void beforeAll(){
        System.out.println("\n---+++$$$ Battlefield TEST $$$+++---\n");
    }

    /**
     * for on-the-fly tests
     */
    @Test
    public void testPrint(){


    }
}
