package com.lawsgame.emishitactics.engine.utils;

import com.lawsgame.emishitactics.core.helpers.interfaces.SpriteProvider;

public class TestInstance {
    protected int integer;
    protected String string;
    protected float floatValue;
    protected boolean booleanValue;

    public TestInstance(Integer integer, String string, Float floatValue, Boolean booleanValue) {
        this.integer = integer;
        this.string = string;
        this.floatValue = floatValue;
        this.booleanValue = booleanValue;
    }

    @Override
    public String toString() {
        return "TestInstance{" +
                "integer=" + integer +
                ", string='" + string + '\'' +
                ", floatValue=" + floatValue +
                ", booleanValue=" + booleanValue +
                '}';
    }
}
