package com.lawsgame.emishitactics.core.models;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BannerTest {
    private Banner banner1;

    public BannerTest(){}

    @Before
    public void before(){
        banner1 = new Banner();

    }

    @Test
    public void test(){
        banner1.setMaxPoints(5);
        banner1.incrementStrength();
        banner1.incrementStrength();
        assertTrue(banner1.getStrength() == 2);
        banner1.decrementStrength();
        banner1.decrementStrength();
        banner1.decrementStrength();
        banner1.decrementStrength();
        assertTrue(banner1.getStrength() == 0);

        banner1.setMaxPoints(4);
        assertTrue(banner1.getStrength() == 0);
        banner1.incrementLootRate(); //1

        assertTrue(banner1.getRemainingPoints() == 3);
        assertTrue(banner1.getLootrate() == 0.1f);
        banner1.incrementLootRate(); //1 + 2
        assertTrue(banner1.getRemainingPoints() == 1);
        banner1.incrementLootRate(); //1 + 2 + 3
        assertTrue(banner1.getLootrate() == 0.2f);
        banner1.decrementLootrate();
        assertTrue(banner1.getRemainingPoints() == 3);
        assertTrue(banner1.getLootrate() == 0.1f);

        banner1.setMaxPoints(6);
        assertTrue(banner1.getRemainingPoints() == 6);
        banner1.incrementRange();
        assertTrue(banner1.getRemainingPoints() == 0);
        assertTrue(banner1.getRange() == 1);
    }

}
