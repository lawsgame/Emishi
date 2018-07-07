package com.lawsgame.emishitactics.core.models;

import com.lawsgame.emishitactics.core.constants.Data.*;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;

public class Banner extends Observable{
    private BannerSign sign1;
    private BannerSign sign2;
    private BannerSign sign3;

    public Banner() {
        sign1 = BannerSign.NONE;
        sign2 = BannerSign.NONE;
        sign3 = BannerSign.NONE;
    }

    public  boolean setSign(BannerSign sign, int index){
        if(hasSign(sign) < sign.getMax()) {
            switch (index) {
                case 0:
                    sign1 = sign;
                    break;
                case 1:
                    sign2 = sign;
                    break;
                case 2:
                    sign3 = sign;
                    break;
            }
            notifyAllObservers(null);
            return true;
        }
        return false;
    }

    protected boolean addSign(BannerSign sign){
        if(sign1 == BannerSign.NONE){
            sign1 = sign;
        }else if(sign2  == BannerSign.NONE){
            sign2 = sign;
        }else if(sign3  == BannerSign.NONE){
            sign3 = sign;
        }else {
            notifyAllObservers(null);
            return false;
        }
        return true;
    }

    public int hasSign(BannerSign sign){
        int numberSigns = 0;
        if(sign == sign1) numberSigns ++;
        if(sign == sign2) numberSigns ++;
        if(sign == sign3) numberSigns ++;
        return numberSigns;
    }

    public int getBonusRelativeTo(BannerSign sign){
        int numberSigns = 0;
        if(sign == sign1) numberSigns += sign.getGain();
        if(sign == sign2) numberSigns += sign.getGain();
        if(sign == sign3) numberSigns += sign.getGain();
        return numberSigns;
    }

    // --------------------- GETTERS & SETTERS -------------------------------

    public BannerSign getSign1() {
        return sign1;
    }

    public BannerSign getSign2() {
        return sign2;
    }

    public BannerSign getSign3() {
        return sign3;
    }

    public String toString(){
        return "Banner = ("+sign1.name()+" | "+sign2.name()+" | "+sign3.name()+")";
    }

    public boolean isEmpty() {
        return sign1 == BannerSign.NONE && sign2 == BannerSign.NONE && sign3 == BannerSign.NONE;
    }
}
