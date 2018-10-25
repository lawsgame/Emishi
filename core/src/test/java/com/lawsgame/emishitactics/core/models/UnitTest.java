package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;

import org.junit.Before;
import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.assertTrue;

public class UnitTest {
    Unit unit;

    @Before
    public void before(){
        unit = new Unit("rodo");
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

    @Test
    public void testSetExperience(){
        int[] res = unit.setExperience(88);

        assertTrue(unit.getExperience() == 88);
        assertTrue(res.length == 1 && res[0] == 88);

        res = unit.setExperience(238);

        System.out.println(unit.getExperience());
        assertTrue(unit.getExperience() == 38);
        assertTrue(res.length == 3);
        assertTrue(res[0] == 100);
        assertTrue(res[1] == 100);
        assertTrue(res[2] == 38);

    }

    @Test
    public void testAddExpPoints(){
        unit.setExperience(88);
        Stack<int[]> stacklvl = unit.addExpPoints(150);

        assertTrue(stacklvl.size() == 3);
        assertTrue(stacklvl.peek().length == 1);
        assertTrue(stacklvl.peek()[0] == 38);

        stacklvl.pop();

        assertTrue(stacklvl.peek().length > 1);
        assertTrue(stacklvl.peek()[stacklvl.peek().length - 1] == 100);

        stacklvl.pop();

        assertTrue(stacklvl.peek().length > 1);
        assertTrue(stacklvl.peek()[stacklvl.peek().length - 1] == 12);

    }

}
