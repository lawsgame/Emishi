package com.lawsgame.emishitactics.core.models;

import com.lawsgame.emishitactics.core.models.Data.BannerBonus;
import com.lawsgame.emishitactics.core.models.interfaces.Model;

import java.util.HashMap;

public class Banner extends Model  {

    private final Unit bearer;
    private HashMap<BannerBonus, Integer> bonuses;
    private Data.BBMode mode;

    public Banner(Unit bearer){
        this.bearer = bearer;
        this.bonuses = new HashMap<BannerBonus, Integer>();
        this.mode = Data.BBMode.OFFENSIVE;
        this.reset();
    }

    public void reset(){
        bonuses.clear();
        for(int i = 0; i < BannerBonus.values().length; i++){
            bonuses.put(BannerBonus.values()[i], 0);
        }

    }

    public int getMaxPoints(){
        return bearer.getAppLeadership();
    }

    public int getUsedPoints(){
        int usedPoints = 0;
        for (BannerBonus bb : bonuses.keySet()) {
            for(int i = 1; i <= bonuses.get(bb); i++) {
                usedPoints += bb.getCost()[i - 1];
            }
        }
        return usedPoints;
    }

    public int getRemainingPoints(){
        return getMaxPoints() - getUsedPoints();
    }

    public boolean decrementBonus(BannerBonus bb){
        if(0 < bonuses.get(bb)){
            this.bonuses.put(bb, bonuses.get(bb) -1);
            return true;
        }
        return false;
    }

    public boolean increment(BannerBonus bb){
        boolean notmaxed = bonuses.get(bb) < bb.getCost().length;
        boolean enoughPoints =  bb.getCost()[bonuses.get(bb)] <= getRemainingPoints();
        if(notmaxed && enoughPoints){
            this.bonuses.put(bb, bonuses.get(bb) + 1);
            return true;
        }
        return false;
    }

    private float getValue(BannerBonus bb){
        return bonuses.get(bb) * bb.getBaseValue();
    }

    public float getValue(BannerBonus bb, boolean takeModeIntoAccount){
        return (bb.getMode() == mode || bb.getMode() == Data.BBMode.ALL || !takeModeIntoAccount) ? getValue(bb) : 0;
    }

    public float getValue(BannerBonus bb, Data.BBMode desiredMode){
        return (bb.getMode() == desiredMode || bb.getMode() == Data.BBMode.ALL) ? getValue(bb) : 0;
    }

    public void setMode(Data.BBMode mode) {
        this.mode = mode;
    }

    public Data.BBMode getMode() {
        return mode;
    }

    public String toString(){
        String res = "\nBanner of "+bearer;
        res += "\n    war chief ? "+bearer.isWarChief()+"\n";
        for(BannerBonus bb : BannerBonus.values()) {
            res +="\n    "+bb.name()+" : "+bonuses.get(bb)+" pts => value : "+getValue(bb, false);
        }
        return res;
    }


}
