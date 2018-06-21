package com.lawsgame.emishitactics.engine.geometry;

import com.badlogic.gdx.math.MathUtils;
import com.lawsgame.emishitactics.engine.math.geometry.Matrix;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MatrixTest {

    Matrix m1;
    Matrix m2;
    Matrix identity = Matrix.getIdentity();

    public MatrixTest(){ }

    @BeforeClass
    public static void beforeClass(){
        System.out.println("\n---+++$$$ Matrix TEST $$$+++--\n");
    }

    @Test
    public void testGetValue(){
        assertTrue(m1.getValue(0,0) == 4);
        assertTrue(m1.getValue(0,1) == -3);
        assertTrue(m1.getValue(1,0) == 5);
    }

    @Test
    public void testAdd(){
        m1.add(m2);
        assertTrue(m1.equals(new Matrix(5,-1,6,3)));
    }

    @Test
    public void testScalarMultiplication(){
        m1.multiply(2);
        assertTrue(m1.equals(new Matrix(8,-6,10,2)));
    }

    @Test
    public void testSub(){
        m1.sub(m2);
        assertTrue(m1.equals(new Matrix(3,-5,4,-1)));
    }

    @Test
    public void testTranspose(){
        m1.tranpose();
        m2.tranpose();
        assertTrue(m1.equals(new Matrix(4,5,-3,1)));
        assertTrue(m2.equals(new Matrix(1,1,2,2)));
    }

    @Test
    public void testMatricialMultiplication(){
        Matrix m1Copy = m1.duplicate();
        m1.multiply(m2);
        m2.multiply(m1Copy);
        assertTrue(m1.equals(new Matrix(1,2,6,12)));
        assertTrue(m2.equals(new Matrix(14,-1,14,-1)));
    }

    @Test
    public void testGetRotationMatrix() {
        Matrix m3 = Matrix.getRotationMatrix(MathUtils.PI);

        assertEquals(m3.getValue(0,0), -1, 0.0001f);
        assertEquals(m3.getValue(1,0), 0, 0.0001f);
        assertEquals(m3.getValue(0,1), 0, 0.0001f);
        assertEquals(m3.getValue(1,1), -1, 0.0001f);
    }

    @Test
    public void testRotate(){
        m1.rotate(MathUtils.PI);

        assertEquals(m1.getValue(0,0), -4, 0.0001f);
        assertEquals(m1.getValue(0,1), 3, 0.0001f);
        assertEquals(m1.getValue(1,0), -5, 0.0001f);
        assertEquals(m1.getValue(1,1), -1, 0.0001f);

        m2.rotate(MathUtils.PI/3f);

        assertEquals(m2.getValue(0,0), 2.232060f, 0.001f);
        assertEquals(m2.getValue(0,1), 0.133970f, 0.001f);
        assertEquals(m2.getValue(1,0), 2.232060, 0.001f);
        assertEquals(m2.getValue(1,1), 0.133970, 0.001f);
    }

    @Test
    public void testInversibility(){
        assertTrue(m1.isInversable());
        assertTrue(!m2.isInversable());

        Matrix invM1 = m1.getInverse();

        m1.multiply(invM1);

        assertEquals(invM1.getValue(0,0), 0.052632f, 0.01f);
        assertEquals(invM1.getValue(0,1), 0.157895f, 0.01f);
        assertEquals(invM1.getValue(1,0), -0.263158f, 0.01f);
        assertEquals(invM1.getValue(1,1), 0.210526f, 0.01f);

        assertEquals(m1.getValue(0,0), 1, 0.01f);
        assertEquals(m1.getValue(0,1), 0, 0.01f);
        assertEquals(m1.getValue(1,0), 0, 0.01f);
        assertEquals(m1.getValue(1,1), 1, 0.01f);

    }

    @Before
    public void before(){
        m1 = new Matrix(4,-3,5,1);
        m2 = new Matrix (1,2,1,2);

    }


}
