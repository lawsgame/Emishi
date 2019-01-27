package com.lawsgame.emishitactics.core.models.tempo;

import com.lawsgame.emishitactics.core.models.Data.WeaponType;

public class Mother {
    private int valInt;
    private float valFloat;
    private double valDouble;
    private boolean valBool;
    private String valString;
    private WeaponType weaponType;
    private Daughter daughter;

    public Mother(int valInt, float valFloat, double valDouble, boolean valBool, String valString, WeaponType weaponType, Daughter daughter) {
        this.valInt = valInt;
        this.valFloat = valFloat;
        this.valDouble = valDouble;
        this.valBool = valBool;
        this.valString = valString;
        this.weaponType = weaponType;
        this.daughter = daughter;


    }

    @Override
    public String toString() {
        return "Mother{" +
                "valInt=" + valInt +
                ", valFloat=" + valFloat +
                ", valDouble=" + valDouble +
                ", valBool=" + valBool +
                ", valString='" + valString + '\'' +
                ", weaponType=" + weaponType +
                ", daughter=" + daughter +
                '}';
    }
}
