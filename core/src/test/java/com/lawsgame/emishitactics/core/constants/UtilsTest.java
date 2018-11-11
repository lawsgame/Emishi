package com.lawsgame.emishitactics.core.constants;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;
import java.util.TreeMap;

import static org.junit.Assert.assertTrue;

public class UtilsTest {
    private Battlefield bf;
    private Array<int[]> a1;
    private Array<int[]> a2;

    @BeforeClass
    public static void testOnfly()  {

    }

    @Before
    public void before(){
        bf = new Battlefield(9,9);
        for(int r = 0; r < 9; r++){
            for(int c = 0; c < 9; c++){
                bf.setTile(r,c, Data.TileType.PLAIN, false);
            }
        }

        a1 = new Array<int[]>();
        a1.add(new int []{0,3,6});
        a1.add(new int []{2,5});
        a1.add(new int []{});
        a1.add(new int []{1});

        a2 = new Array<int[]>();
        a2.add(new int []{0,3,9});
        a2.add(new int []{2,5});
        a2.add(new int []{2,5});
        a2.add(new int []{2,5});
        a2.add(new int []{8});
        a2.add(new int []{-1,-1});

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

    @Test
    public void testArrayContains(){

        int[] tab0 = null;
        int[] tab1 = new int []{};
        int[] tab2 = new int []{1};
        int[] tab3 = new int []{0,3,9,8};
        int[] tab4 = new int[]{2,5};

        assertTrue(!Utils.arrayContains(a1, tab0));
        assertTrue(Utils.arrayContains(a1, tab1));
        assertTrue(Utils.arrayContains(a1, tab2));
        assertTrue(!Utils.arrayContains(a1, tab3));
        assertTrue(Utils.arrayContains(a1, tab4));

        assertTrue(Utils.arrayContains(a1, 0, 3));
        assertTrue(!Utils.arrayContains(a1, 2, 6));
        assertTrue(Utils.arrayContains(a1, 2, 5));
        assertTrue(!Utils.arrayContains(a1, -1, 9));
    }

    public void testArrayRemove(){

        Utils.arrayRemove(a1, 0, 4);

        assertTrue(a1.size == 4);

        Utils.arrayRemove(a1, 0, 3);

        assertTrue(a1.size == 3);
    }

    @Test
    public void testRemoveClones(){
        Utils.arrayRemoveClones(a2);

        assertTrue(a2.size == 4);
        assertTrue(Utils.arrayContains(a2 , new int[]{2,5}));
    }

    @Test
    public void testArrayGetElementsInBothOnly(){
        Array<int[]> a3 = Utils.arrayGetElementsInBothOnly(a1, a2);

        assertTrue(a3.size == 1);
        assertTrue(Utils.arrayContains(a3 , new int[]{2,5}));


    }
}
