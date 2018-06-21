package com.lawsgame.emishitactics.engine.geometry;

import com.lawsgame.emishitactics.engine.math.geometry.Matrix;
import com.lawsgame.emishitactics.engine.math.geometry.Vector;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VectorTest {
    Vector v1;
    Vector v2;
    Vector v3;
    Vector v4;

    @BeforeClass
    public static void beforeClass(){
        System.out.println("---+++$$$ Vector TEST $$$+++--");
    }

    @Test
    public void testNormalize(){
        v1.normalize();
        v2.normalize();
        v3.normalize();
        v4.normalize();

        assertEquals(v4.x, 0, 0.01f);
        assertEquals(v4.y, 0, 0.01f);
        assertEquals(v3.x, 0, 0.01f);
        assertEquals(v3.y, 1, 0.01f);
        assertEquals(v2.x, -0.37139, 0.01f);
        assertEquals(v2.y, 0.92848, 0.01f);
        assertEquals(v1.x, 0.94868f, 0.01f);
        assertEquals(v1.y, 0.31623f, 0.01f);
    }

    @Test
    public void testApply(){
        Matrix m = new Matrix(0,2,3,-5);
        v1.apply(m);
        v2.apply(m);
        v3.apply(m);
        v4.apply(m);
        assertTrue(v1.equals(new Vector(2,4)));
        assertTrue(v2.equals(new Vector(10,-31)));
        assertTrue(v3.equals(new Vector(8,-20)));
        assertTrue(v4.equals(new Vector(0,0)));
    }

    @Before
    public void before(){
        v1 = new Vector(3,1);
        v2 = new Vector(-2,5);
        v3 = new Vector(0,4);
        v4 = new Vector(0,0);
    }

}
