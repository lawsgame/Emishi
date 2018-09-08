package com.lawsgame.emishitactics.engine.datastructures;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MapsTest {

    private Map2 map2;
    private Map3 map3;

    @Before
    public void before(){
        map2 = new Map2<String, Integer, String>();
        map3 = new Map3<String, Boolean, Integer, String>();
    }

    @Test
    public void testPut(){
        String strkey0 = "knight";
        int intkey0 = 0;
        String strres0 = "squire";
        int intkey1 = 1;
        String strres1 = "young knight";

        map2.put(strkey0, intkey0, strres0);
        map2.put(strkey0, intkey1, strres1);

        assertTrue(map2.get(strkey0, 0).equals("squire"));
        assertTrue(map2.get(strkey0, 1).equals("young knight"));
        assertTrue(map2.get(strkey0, 4) == null);
        assertTrue(map2.get("hey", 4) == null);


        map3.put(strkey0, true, intkey0, strres0);
        map3.put(strkey0, true, intkey1, strres1);

        assertTrue(map3.get(strkey0, true, 0).equals("squire"));
        assertTrue(map3.get(strkey0, true, 1).equals("young knight"));
        assertTrue(map3.get(strkey0, false, 4) == null);
        assertTrue(map3.get("hey", true, 0) == null);

    }

}
