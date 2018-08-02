package com.lawsgame.emishitactics.core.models;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BattlefieldTest {
    Battlefield battlefield;

    @Before
    public void before(){
        battlefield = new Battlefield(9,9);
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
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
}
