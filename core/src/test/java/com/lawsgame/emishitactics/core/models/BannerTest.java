package com.lawsgame.emishitactics.core.models;

import com.lawsgame.emishitactics.core.models.interfaces.IUnit;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BannerTest {
    private Banner banner;
    private IUnit unit;

    public BannerTest(){}

    @Before
    public void before(){
        unit = new Unit("Elo");
        unit.setLeadership(15);
        banner = new Banner(unit);

    }

    @Test
    public void testMaxPoints(){
        assertTrue(banner.getMaxPoints() == 15);
    }

    @Test
    public void test(){
        banner.increment(Data.BannerBonus.MORAL_SHIELD);
        banner.increment(Data.BannerBonus.MORAL_SHIELD);
        banner.increment(Data.BannerBonus.MORAL_SHIELD);
        banner.increment(Data.BannerBonus.RANGE);


        assertTrue(banner.getValue(Data.BannerBonus.MORAL_SHIELD, false) == 0.3f);
        assertTrue(banner.getUsedPoints() == 12);
        assertTrue(banner.getValue(Data.BannerBonus.RANGE, false) == 1);
        assertTrue(banner.getMaxPoints() == 15);
        assertTrue(banner.getRemainingPoints() == 3);

        banner.decrementBonus(Data.BannerBonus.MORAL_SHIELD);

        assertTrue(banner.getValue(Data.BannerBonus.MORAL_SHIELD, false) == 0.2f);
        assertTrue(banner.getUsedPoints() == 9);
        assertTrue(banner.getRemainingPoints() == 6);
    }


}
