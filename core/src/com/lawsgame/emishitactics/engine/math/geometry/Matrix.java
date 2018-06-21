package com.lawsgame.emishitactics.engine.math.geometry;

import com.badlogic.gdx.math.MathUtils;

public class Matrix {

    private static final Matrix identity = new Matrix(1,0,0,1);

    private float[][] values;

    public Matrix(){
        values = new float[2][2]; // row then col
    }

    /**
     *
     * a11 a12
     * a21 a22
     *
     * @param a11
     * @param a12
     * @param a21
     * @param a22
     */
    public Matrix(float a11, float a12, float a21, float a22){
        this();
        values[0][0] = a11;
        values[0][1] = a12;
        values[1][0] = a21;
        values[1][1] = a22;
    }

    public Matrix(float[][] values){
        this();
        if(values.length == 2 && values[0].length == 2){
            this.values = values;
        }else{
            try{
            throw new MatrixGeoException("values given does not match the matrix dimensions of 2*2: "+values.length+"*"+((values.length > 0) ? values[0].length : 0)  );
            }catch (MatrixGeoException e){
                e.getStackTrace();
            }
        }
    }

    public static Matrix getIdentity() {
        return identity;
    }

    public Matrix duplicate(){
        return new Matrix(values[0][0], values[0][1], values[1][0], values[1][1]);
    }

    public float getValue(int r, int c) {
        float value = 0;
        if( 0 <= r && r < 2 && 0 <= c && c < 2){
            value = values[r][c];
        }else {
            try {
                throw new MatrixGeoException("the input is out of bounds");
            }catch (MatrixGeoException e){
                e.getStackTrace();
            }
        }
        return value;
    }

    public void add(Matrix matrix){
        this.values[0][0] += matrix.values[0][0];
        this.values[0][1] += matrix.values[0][1];
        this.values[1][0] += matrix.values[1][0];
        this.values[1][1] += matrix.values[1][1];
    }

    public void multiply(float factor){
        this.values[0][0] *= factor;
        this.values[0][1] *= factor;
        this.values[1][0] *= factor;
        this.values[1][1] *= factor;
    }

    public void sub(Matrix matrix){
        this.values[0][0] -= matrix.values[0][0];
        this.values[0][1] -= matrix.values[0][1];
        this.values[1][0] -= matrix.values[1][0];
        this.values[1][1] -= matrix.values[1][1];
    }

    public void tranpose(){
        float tempo12 = values[0][1];
        values[0][1] = values[1][0];
        values[1][0] = tempo12;
    }

    /**
     * this = this * matrix
     *
     * @param m: a matrix
     */
    public void multiply(Matrix m){
        float tempo11 = values[0][0];
        float tempo12 = values[0][1];
        float tempo21 = values[1][0];
        float tempo22 = values[1][1];

        values[0][0] = tempo11 * m.values[0][0] + tempo12 * m.values[1][0];
        values[0][1] = tempo11 * m.values[0][1] + tempo12 * m.values[1][1];
        values[1][0] = tempo21 * m.values[0][0] + tempo22 * m.values[1][0];
        values[1][1] = tempo21 * m.values[0][1] + tempo22 * m.values[1][1];

    }

    public void rotate(float radiansClockwise){
        Matrix m = getRotationMatrix(radiansClockwise);
        multiply(m);
    }

    public float getDeterminant(){
        return values[0][0]*values[1][1] - values[0][1] * values[1][0];
    }

    public float getTrace(){
        return  values[0][0] + values[1][1];
    }

    public Matrix getAdjugate(){
        return new Matrix(values[1][1], -values[0][1], -values[1][0], values[0][0]);
    }

    public boolean isInversable(){
        return getDeterminant() != 0;
    }

    public Matrix getInverse(){
        Matrix m = getAdjugate();
        float det = getDeterminant();
        if(det != 0){
            m.multiply(1.0f/det);
            return m;
        }
        return null;
    }

    public static Matrix getRotationMatrix(float radiansClockwise){
        return new Matrix(MathUtils.cos(radiansClockwise), -MathUtils.sin(radiansClockwise), MathUtils.sin(radiansClockwise), MathUtils.cos(radiansClockwise));
    }

    @Override
    public String toString(){
        String str = "";
        str += " ("+this.values[0][0];
        str += " "+this.values[0][1]+")";
        str += "\n( "+this.values[1][0];
        str += " "+this.values[1][1]+")";
        return str;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Matrix){
            Matrix m = (Matrix)o;
            return m.values[0][0] == values[0][0]
                    && m.values[1][0] == values[1][0]
                    && m.values[0][1] == values[0][1]
                    && m.values[1][1] == values[1][1];
        }
        return false;
    }



    public static class MatrixGeoException extends Throwable {
        public MatrixGeoException(String s) {
            super(s);
        }
    }
}
