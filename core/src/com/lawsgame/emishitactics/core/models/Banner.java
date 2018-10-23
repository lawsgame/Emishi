package com.lawsgame.emishitactics.core.models;

import com.lawsgame.emishitactics.core.models.interfaces.Model;

public class Banner extends Model  {


    private int maxpoints;
    private int usedpoints;
    private int strength;
    private int range;
    private int lootrate;
    private int apregen;

    public Banner(){
        this.maxpoints = 0;
        this.usedpoints = 0;
        this.strength = 0;
        this.range = 0;
        this.lootrate = 0;
        this.apregen = 0;
    }

    public void incrementMaxPoints(){
        this.maxpoints++;
    }

    public void setMaxPoints(int leadership){
        this.maxpoints = leadership;
        this.usedpoints = 0;
        this.strength = 0;
        this.range = 0;
        this.lootrate = 0;
        this.apregen = 0;
    }

    public int getMaxPoints(){
        return maxpoints;
    }

    public int getUsedPoints(){
        return usedpoints;
    }

    public int getRemainingPoints(){
        return getMaxPoints() - getUsedPoints();
    }



    // ------------- SETTERS && GETTERS --------------------

    public boolean decrementStrength(){
        if(0 < strength){
            this.usedpoints -= Data.BANNER_STRENGTH_BONUS_COST[strength];
            this.strength--;
            return true;
        }
        return false;
    }

    public boolean decrementRange(){
        if(0 < range){
            this.usedpoints -= Data.BANNER_RANGE_BONUS_COST[range];
            this.range--;
            return true;
        }
        return false;
    }

    public boolean decrementLootrate(){
        if(0 < lootrate){
            this.usedpoints -= Data.BANNER_LOOTRATE_BONUS_COST[lootrate];
            this.lootrate--;
            return true;
        }
        return false;
    }

    public boolean decrementAPRegen(){
        if(0 < apregen){
            this.usedpoints -= Data.BANNER_AP_REGEN_BONUS_COST[apregen];
            this.apregen--;
            return true;
        }
        return false;
    }

    public boolean incrementStrength(){
        boolean notmaxed = strength <  Data.BANNER_STRENGTH_BONUS_COST.length - 1;
        boolean enoughPoints =  Data.BANNER_STRENGTH_BONUS_COST[strength + 1] <= getRemainingPoints();
        if(notmaxed && enoughPoints){
            this.usedpoints +=  Data.BANNER_STRENGTH_BONUS_COST[strength + 1];
            this.strength++;
            return true;
        }
        return false;
    }
    public boolean incrementRange(){
        boolean notmaxed = range <  Data.BANNER_RANGE_BONUS_COST.length - 1;
        boolean enoughPoints =  Data.BANNER_RANGE_BONUS_COST[range + 1] <= getRemainingPoints();
        if(notmaxed && enoughPoints){
            this.usedpoints +=  Data.BANNER_RANGE_BONUS_COST[range + 1];
            this.range++;
            return true;
        }
        return false;
    }
    public boolean incrementLootRate(){
        boolean notmaxed = lootrate <  Data.BANNER_LOOTRATE_BONUS_COST.length - 1;
        boolean enoughPoints =  Data.BANNER_LOOTRATE_BONUS_COST[lootrate + 1] <= getRemainingPoints();
        if(notmaxed && enoughPoints){
            this.usedpoints +=  Data.BANNER_LOOTRATE_BONUS_COST[lootrate + 1];
            this.lootrate++;
            return true;
        }
        return false;
    }
    public boolean incrementAPRegen(){
        boolean notmaxed = apregen <  Data.BANNER_AP_REGEN_BONUS_COST.length - 1;
        boolean enoughPoints =  Data.BANNER_AP_REGEN_BONUS_COST[apregen + 1] <= getRemainingPoints();
        if(notmaxed && enoughPoints){
            this.usedpoints +=  Data.BANNER_AP_REGEN_BONUS_COST[apregen + 1];
            this.apregen++;
            return true;
        }
        return false;
    }



    public float getStrength() {
        return strength * Data.BANNER_STRENGTH_BONUS_COST[0];
    }

    public float getRange() {
        return range * Data.BANNER_RANGE_BONUS_COST[0];
    }

    public float getLootrate() {
        return lootrate * Data.BANNER_LOOTRATE_BONUS_COST[0];
    }

    public float getAPRegeneration() {
        return apregen * Data.BANNER_AP_REGEN_BONUS_COST[1];
    }

}
