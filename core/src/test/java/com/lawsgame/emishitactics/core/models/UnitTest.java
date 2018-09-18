package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;

import org.junit.Before;
import org.junit.Test;

public class UnitTest {
    Unit unit;

    @Before
    public void before(){

    }

    @Test
    public void testJob(){
        Array<Unit> units = new Array<Unit>();
        for(int i = 0; i < 1000; i++){
            units.add(new Unit(""+i, Data.UnitTemplate.SOLAIRE, 25, Data.WeaponType.SWORD, false, false, false, false, false));
        }

        Unit unit;
        float[] array = new float[units.size];
        for(int i = 0; i < array.length; i++){
            unit = units.get(i);
            array[i] = unit.getBaseAgility();
        }
    }

}
