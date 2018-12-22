package com.lawsgame.emishitactics.core.models;

import com.lawsgame.emishitactics.core.models.Unit;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BannerTest {
    private Banner banner;
    private Unit unit;

    public BannerTest(){}

    @Before
    public void before(){
        unit = new Unit("Elo", Data.UnitTemplate.getStandard(), Data.WeaponType.SWORD);
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


        assertTrue(banner.getValue(Data.BannerBonus.MORAL_SHIELD) == 0.3f);
        assertTrue(banner.getUsedPoints() == 12);
        assertTrue(banner.getValue(Data.BannerBonus.RANGE) == 1);
        assertTrue(banner.getMaxPoints() == 15);
        assertTrue(banner.getRemainingPoints() == 3);

        banner.decrementBonus(Data.BannerBonus.MORAL_SHIELD);

        assertTrue(banner.getValue(Data.BannerBonus.MORAL_SHIELD) == 0.2f);
        assertTrue(banner.getUsedPoints() == 9);
        assertTrue(banner.getRemainingPoints() == 6);
    }


}
