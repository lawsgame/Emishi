package com.lawsgame.emishitactics.core.models.tempo;

public class Daughter {
    private int valueInt;
    private String valString;

    public Daughter(int valueInt, String valString) {
        this.valueInt = valueInt;
        this.valString = valString;
    }

    @Override
    public String toString() {
        return "Daughter{" +
                "valueInt=" + valueInt +
                ", valString='" + valString + '\'' +
                '}';
    }
}
