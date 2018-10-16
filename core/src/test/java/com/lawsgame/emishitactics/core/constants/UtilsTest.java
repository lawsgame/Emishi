package com.lawsgame.emishitactics.core.constants;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class UtilsTest {
    Battlefield bf;

    @BeforeClass
    public static void beforeAll(){

    }



    @BeforeClass
    public static void testOnfly(){

    }

    @Before
    public void before(){
        bf = new Battlefield(9,9);
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                bf.setTile(r,c, Data.TileType.PLAIN, false);
            }
        }
    }

    @Test
    public void testGetAreaFromRange(){
        Array<int[]> area = Utils.getEreaFromRange(bf, 1 ,1 ,2,2);
        assertTrue(area.size == 6);
        assertTrue(Utils.arrayContains(area, 0,0));
        assertTrue(Utils.arrayContains(area, 2,0));
        assertTrue(Utils.arrayContains(area, 0,2));
        assertTrue(Utils.arrayContains(area, 3,1));
        assertTrue(Utils.arrayContains(area, 2,2));
        assertTrue(Utils.arrayContains(area, 1,3));

    }


    @Test
    public void testArrayContains(){
        Array<int[]> array = new Array<int[]>();
        array.add(new int []{0,3,6});
        array.add(new int []{2,5});
        array.add(new int []{});
        array.add(new int []{1});

        int[] tab0 = null;
        int[] tab1 = new int []{};
        int[] tab2 = new int []{1};
        int[] tab3 = new int []{0,3,9,8};
        int[] tab4 = new int[]{2,5};

        assertTrue(!Utils.arrayContains(array, tab0));
        assertTrue(Utils.arrayContains(array, tab1));
        assertTrue(Utils.arrayContains(array, tab2));
        assertTrue(!Utils.arrayContains(array, tab3));
        assertTrue(Utils.arrayContains(array, tab4));
    }

    @Test
    public void testDist(){
        assertTrue(Utils.dist(2,3,3,3) == 1);
        assertTrue(Utils.dist(2,3,5,3) == 3);
        assertTrue(Utils.dist(2,3,4,4) == 3);
    }

    @Test
    public void testGetOrientationFromCoords(){

        assertTrue(Utils.getOrientationFromCoords(0, 0, 1, 0) == Data.Orientation.NORTH);
        assertTrue(Utils.getOrientationFromCoords(0, 0, 0, 1) == Data.Orientation.EAST);
        assertTrue(Utils.getOrientationFromCoords(0, 0, -1, 0) == Data.Orientation.SOUTH);
        assertTrue(Utils.getOrientationFromCoords(0, 0, 0, -1) == Data.Orientation.WEST);
        assertTrue(Utils.getOrientationFromCoords(2, 2, 5, 3) == Data.Orientation.NORTH);
        assertTrue(Utils.getOrientationFromCoords(2, 2, 1, -5) == Data.Orientation.WEST);
    }


    @Test
    public void testSwitchBetweenRGBAAnd32bitsColor(){
        int r = 28;
        int g = 39;
        int b = 125;
        int[] rgbaRes = Utils.getRGBA(Utils.getColor32Bits(r,g,b));
        assertTrue(r == rgbaRes[0] && g == rgbaRes[1] && b == rgbaRes[2]);
    }

}
