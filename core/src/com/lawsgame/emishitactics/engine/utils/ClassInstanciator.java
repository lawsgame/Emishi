package com.lawsgame.emishitactics.engine.utils;


import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import org.apache.commons.lang3.EnumUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ClassInstanciator {

    public static <T> T getInstanceOf(String className, Array<String> attrClassPath, Array<String> attrValues, Class<T> expectedClass){
        try {
            Class<?>[] paramTypes = new Class[attrClassPath.size];
            Object[] paramArray = new Object[attrValues.size];
            Constructor paramC;
            Class[] attrParamTypes = new Class[]{String.class};
            for(int i = 0; i < attrClassPath.size; i++){
                paramTypes[i] = Class.forName(attrClassPath.get(i));
                paramC = paramTypes[i].getConstructor(attrParamTypes);
                paramArray[i] = paramC.newInstance(attrValues.get(i));
            }
            Class<? extends T> classEntity = Class.forName(className).asSubclass(expectedClass);
            Constructor<? extends T> constructor = classEntity.getConstructor(paramTypes);
            return constructor.newInstance(paramArray);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * expected XML Structure:
     * <'Name0' type="classpath">
     *      <'Name1' type = "classpath">
     *          <.../>
     *      </'Name1'>
     *      <String value="..."/>
     *      <Primitive type="int" value="1/>
     *      <Enum type="classpth" value="value"/>
     * </'Name'>
     *
     * NOT HANDLE :
     *  - any collection pf any sort : array, Java's collections, Gdx's Arrays
     *
     * @param expectedClass : expected class of the returned object  by the client
     * @param xmlInstance : XML Java object equivalent
     * @param <T> : class of the instance to be returned
     * @return instance of T
     */
    public static <T> T parseXmlIntoInstanceOf(Class<? extends T> expectedClass, XmlReader.Element xmlInstance){
        try {
            Object[] paramValues = new Object[xmlInstance.getChildCount()];
            Class[] paramTypes = new Class[xmlInstance.getChildCount()];
            String classPath;
            XmlReader.Element child;
            for(int i = 0; i < xmlInstance.getChildCount(); i++){
                child = xmlInstance.getChild(i);
                if(child.getName().equals("Primitive")){
                    if(child.get("type").equals("int")){
                        paramTypes[i] = Integer.TYPE;
                        paramValues[i] = Integer.parseInt(child.get("value"));
                    }
                    if(child.get("type").equals("float")){
                        paramTypes[i] = Float.TYPE;
                        paramValues[i] = Float.parseFloat(child.get("value"));
                    }
                    if(child.get("type").equals("double")){
                        paramTypes[i] = Double.TYPE;
                        paramValues[i] = Double.parseDouble(child.get("value"));
                    }
                    if(child.get("type").equals("boolean")){
                        paramTypes[i] = Boolean.TYPE;
                        paramValues[i] = Boolean.parseBoolean(child.get("value"));
                    }
                }else if(child.getName().equals("String")){
                    paramTypes[i] = String.class;
                    paramValues[i] = child.get("value");
                }else if(child.getName().equals("Enum")){
                    classPath = child.get("type");
                    paramTypes[i] = Class.forName(classPath);
                    paramValues[i] = EnumUtils.getEnum(paramTypes[i], child.get("value"));
                }else{
                    classPath = child.get("type");
                    paramTypes[i] = Class.forName(classPath);
                    paramValues[i] = parseXmlIntoInstanceOf(paramTypes[i], child);
                }
            }
            classPath = xmlInstance.get("type");
            Class<? extends T> classEntity = Class.forName(classPath).asSubclass(expectedClass);
            Constructor<? extends T> constructor = classEntity.getConstructor(paramTypes);
            return constructor.newInstance(paramValues);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static <T extends Enum<T>> T parseXmlIntoEnumConstant(XmlReader.Element enumElt, T defaultValue){
        try {
            String classpath = enumElt.get("type");
            String value = enumElt.get("value");
            Class<T> enumClass = (Class<T>)Class.forName(classpath);
            T t =EnumUtils.getEnum(enumClass, value);
            return (t == null) ? defaultValue : t;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch(ClassCastException e){
            e.printStackTrace();
        }
        return defaultValue;

    }


/*
    public static <T extends Enum<T>> T parseXmlIntoEnumConstant(String classpath, String value){
        try {
            Class<T> enumClass = (Class<T>)Class.forName(classpath);
            T t =EnumUtils.getEnum(enumClass, value);
            if(t != null){
                return t;
            }else {
                Method getDefault = enumClass.getMethod("getDefaultValue");
                Object defaultValue = getDefault.invoke(null);
                if(defaultValue != null) {
                    return (T)defaultValue;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch(ClassCastException e){
            e.printStackTrace();
        }
        return null;
    }
    */
}
