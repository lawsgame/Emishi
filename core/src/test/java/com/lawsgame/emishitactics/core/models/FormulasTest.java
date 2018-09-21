package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.math.MathUtils;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FormulasTest {

    @Test
    public void testGetDamageValueProbability(){
        assertTrue(MathUtils.isEqual(Formulas.getDamageValueProbability(1, 5, 1, 1.5f), 0.23f, 0.01f));
        assertTrue(MathUtils.isEqual(Formulas.getDamageValueProbability(2, 5, 1, 1.5f), 0.20f, 0.01f));
        assertTrue(MathUtils.isEqual(Formulas.getDamageValueProbability(3, 5, 1, 1.5f), 0.19f, 0.01f));
        assertTrue(MathUtils.isEqual(Formulas.getDamageValueProbability(4, 5, 1, 1.5f), 0.19f, 0.01f));
        assertTrue(MathUtils.isEqual(Formulas.getDamageValueProbability(5, 5, 1, 1.5f), 0.18f, 0.01f));
    }

}
