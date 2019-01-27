package com.lawsgame.emishitactics.engine.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.lawsgame.emishitactics.core.constants.Assets;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;

public class ClassInstanciatorTest {
    private TestInstance instance;

    @BeforeClass
    public static void testOnFly(){

    }


    @Test
    public void testGetInstanceOf(){

        Array<String> classPaths = new Array<String>();
        classPaths.add(Integer.class.getCanonicalName());
        classPaths.add(String.class.getCanonicalName());
        classPaths.add(Float.class.getCanonicalName());
        classPaths.add(Boolean.class.getCanonicalName());
        Array<String> values = new Array<String>();
        values.add("1");
        values.add("salut");
        values.add("1.0f");
        values.add("false");
        instance = ClassInstanciator.getInstanceOf(
                "com.lawsgame.emishitactics.engine.utils.TestInstance",
                classPaths,
                values,
                TestInstance.class
        );
        assertFalse(instance.booleanValue);

    }


}
