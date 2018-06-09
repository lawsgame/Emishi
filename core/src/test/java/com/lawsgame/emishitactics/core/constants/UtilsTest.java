package com.lawsgame.emishitactics.core.constants;

import com.badlogic.gdx.utils.Array;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class UtilsTest {



    @Test
    public void testArrayContains(){
        Array<int[]> array = new Array<int[]>();
        array.add(new int []{0,3,6});
        array.add(new int []{});
        array.add(new int []{1});

        int[] tab0 = null;
        int[] tab1 = new int []{};
        int[] tab2 = new int []{1};
        int[] tab3 = new int []{2,5,9,8};

        assertTrue(!Utils.arrayContains(array, tab0));
        assertTrue(Utils.arrayContains(array, tab1));
        assertTrue(Utils.arrayContains(array, tab2));
        assertTrue(!Utils.arrayContains(array, tab3));
    }

    @Test
    public void testDist(){
        assertTrue(Utils.dist(2,3,3,3) == 1);
        assertTrue(Utils.dist(2,3,5,3) == 3);
        assertTrue(Utils.dist(2,3,4,4) == 3);
    }

    @Test
    public void testGetOrientationFromCoords(){

        assertTrue(Utils.getOrientationFromCoords(0, 0, 1, 0) == Props.Orientation.NORTH);
        assertTrue(Utils.getOrientationFromCoords(0, 0, 0, 1) == Props.Orientation.EAST);
        assertTrue(Utils.getOrientationFromCoords(0, 0, -1, 0) == Props.Orientation.SOUTH);
        assertTrue(Utils.getOrientationFromCoords(0, 0, 0, -1) == Props.Orientation.WEST);
        assertTrue(Utils.getOrientationFromCoords(2, 2, 5, 3) == Props.Orientation.NORTH);
        assertTrue(Utils.getOrientationFromCoords(2, 2, 1, -5) == Props.Orientation.WEST);
    }
}
