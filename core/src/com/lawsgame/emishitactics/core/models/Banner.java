package com.lawsgame.emishitactics.core.models;

import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Model;
import com.lawsgame.emishitactics.core.models.Data.BannerBonus;

import java.util.HashMap;

public class Banner extends Model  {

    private final IUnit bearer;
    private BannerBonus currentBonus;
    private HashMap<BannerBonus, Integer> bonuses;

    public Banner(IUnit bearer){
        this.bearer = bearer;
        this.bonuses = new HashMap<BannerBonus, Integer>();
        this.currentBonus = BannerBonus.STRENGTH;
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

    public float getValue(BannerBonus bb, boolean inFight){
        return (bb == currentBonus || !inFight) ? bonuses.get(bb) * bb.getBaseValue() : 0;
    }

    public void setCurrentBonus(BannerBonus currentBonus) {
        this.currentBonus = currentBonus;
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
