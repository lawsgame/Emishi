package com.lawsgame.emishitactics.core.models;

import com.lawsgame.emishitactics.core.constants.Data.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BannerTest {
    private Banner banner1;
    private Banner banner2;
    private Banner banner3;

    public BannerTest(){}

    @BeforeClass
    public static void beforeAll(){
        System.out.println("\n---+++$$$ Banner TEST $$$+++---\n");
    }

    @Before
    public void beforeTest(){
        banner1 = new Banner();
        banner2 = new Banner();
        banner3 = new Banner();

        banner1.setSign(BannerSign.AMATERASU,0);
        banner1.setSign(BannerSign.AMATERASU,1);
        banner1.setSign(BannerSign.AMATERASU,2);
        banner2.setSign(BannerSign.HACHIMAN,0);
        banner2.setSign(BannerSign.HACHIMAN,1);
        banner2.setSign(BannerSign.HACHIMAN,2);
        banner3.setSign(BannerSign.HACHIMAN,2);
    }

    @Test
    public void testHasSign(){
        assertTrue(banner1.hasSign(BannerSign.AMATERASU) == BannerSign.AMATERASU.getMax());
        assertTrue(banner1.hasSign(BannerSign.NONE) == 3 - BannerSign.AMATERASU.getMax());
        assertTrue(banner2.hasSign(BannerSign.HACHIMAN) == BannerSign.HACHIMAN.getMax());
        banner2.setSign(BannerSign.NONE, 0);
        assertTrue(banner2.hasSign(BannerSign.HACHIMAN) == BannerSign.HACHIMAN.getMax() - 1);
        banner2.setSign(BannerSign.NONE, 5);
        assertTrue(banner2.hasSign(BannerSign.HACHIMAN) == BannerSign.HACHIMAN.getMax() - 1);
    }

    @Test
    public void testGetBonusRelativeTo(){
        banner2.setSign(BannerSign.TUNTU, 0);
        assertTrue(banner2.getBonusRelativeTo(BannerSign.TUNTU) == 1*BannerSign.TUNTU.getGain());
        assertTrue(banner2.getBonusRelativeTo(BannerSign.HACHIMAN) == 2*BannerSign.HACHIMAN.getGain());
        assertTrue(banner2.getBonusRelativeTo(BannerSign.SHIRAMBA) == 0);
    }

}
