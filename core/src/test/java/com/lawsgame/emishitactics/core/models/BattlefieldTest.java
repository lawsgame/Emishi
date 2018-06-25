package com.lawsgame.emishitactics.core.models;

import org.junit.Before;
import org.junit.Test;

public class BattlefieldTest {
    Battlefield battlefield;

    @Before
    public void before(){
        battlefield = new Battlefield(9,9);
        battlefield.setAsPlain();
    }

    /**
     * for on-the-fly tests
     */
    @Test
    public void testPrint(){


    }
}
