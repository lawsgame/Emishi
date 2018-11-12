package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.math.MathUtils;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;

public class Formulas {

    public static int getHitRate(int rowAttacker0, int colAttacker0, int rowDefender0, int colDefender0, IUnit attacker, IUnit defender, Battlefield battlefield){
        int hitrate = getCurrentAttackAccuracy(rowAttacker0, colAttacker0, rowDefender0, colDefender0, attacker, defender, battlefield) - getCurrentAvoidance(rowAttacker0, colAttacker0, rowDefender0, colDefender0, attacker, defender, battlefield);
        return (hitrate > 0) ? hitrate : 0;
    }

    public static int[] getDealtDamageRange(int rowAttacker0, int colAttacker0, int rowDefender0, int colDefender0, IUnit attacker, IUnit defender, Battlefield battlefield){
        int[] dealtdamage = getCurrentAttackMightRange(rowAttacker0, colAttacker0, rowDefender0, colDefender0, attacker, defender, battlefield);
        int defense = getCurrentDefense(rowAttacker0, colAttacker0, rowDefender0, colDefender0, attacker, defender, battlefield);
        dealtdamage[0] -= defense;
        dealtdamage[1] -= defense;
        if(dealtdamage[0] < 0) dealtdamage[0] = 0;
        if(dealtdamage[1] < 0) dealtdamage[1] = 0;
        return dealtdamage;
    }

    public static int getRandomDamageInput(int[] expectedDealtDamageRange){
        return MathUtils.random(expectedDealtDamageRange[0], expectedDealtDamageRange[1]);
    }

    /**
     *
     * @param k : k possibility
     * @param n : number of possibilities : 3 - 5 => 3 possibilities
     * @return probability, between 0.0 and 1.0
     */
    public static float getDamageValueProbability(float k, float n, int dexterity, float factor){
        float p = 0f;
        if(k > 0 && k <= n)
            p = (float) (Math.pow(k / n, Math.log(1 + factor*dexterity)) - Math.pow( (k - 1) / n, Math.log(1 + factor*dexterity)));
        return p;
    }

    public static int[] getCurrentAttackMightRange(int rowAttacker0, int colAttacker0, int rowDefender0, int colDefender0, IUnit attacker, IUnit defender, Battlefield battlefield){
        int[] attackMight = attacker.getAppAttackMight();
        for(int i =0; i < attackMight.length; i++) {
            attackMight[i] += battlefield.getTile(rowAttacker0, colAttacker0).getType().getAttackMightBonus();
        }
        return attackMight;
    }

    public static int getCurrentAttackAccuracy(int rowAttacker0, int colAttacker0, int rowDefender0, int colDefender0, IUnit attacker, IUnit defender, Battlefield battlefield){

        int attackAccuracy = attacker.getAppAttackAccuracy();
        attackAccuracy += battlefield.getTile(rowAttacker0, colAttacker0).getType().getAttackAccBonus();
        if(isBackstabAttack(rowAttacker0, colAttacker0, rowDefender0, colDefender0, attacker, defender, battlefield)) {
            attackAccuracy += Data.HIT_RATE_BACK_ACC_BONUS;
        }
        return attackAccuracy;
    }

    private static boolean isBackstabAttack(int rowAttacker0, int colAttacker0, int rowDefender0, int colDefender0, IUnit attacker, IUnit defender, Battlefield battlefield){
        Data.Orientation attackOr = Utils.getOrientationFromCoords(rowAttacker0, colAttacker0, rowDefender0, colDefender0);
        Data.Orientation defenderOr = defender.getOrientation();
        return attackOr == defenderOr;
    }

    public static int getCurrentDefense(int rowAttacker0, int colAttacker0, int rowDefender0, int colDefender0, IUnit attacker, IUnit defender, Battlefield battlefield){
        int defense = defender.getAppDefense(attacker.getCurrentWeapon().getTemplate().getDamageType());
        defense += battlefield.getTile(rowDefender0, colDefender0).getType().getDefenseBonus();
        return defense;
    }

    public static int getCurrentAvoidance(int rowAttacker0, int colAttacker0, int rowDefender0, int colDefender0, IUnit attacker, IUnit defender, Battlefield battlefield){
        int avoidance = defender.getAppAvoidance();
        avoidance += battlefield.getTile(rowDefender0, colDefender0).getType().getAvoidBonus();
        return avoidance;
    }

    public static int getGainedExperience(int levelActor, int levelTarget, boolean stillFigthing) {
        double expGained = 50 + (100 / Math.PI) * Math.atan((levelTarget - levelActor - Data.EXP_LVL_GAP_FACTOR) * Data.EXP_ALPHA) - levelActor;
        if(stillFigthing) expGained *= Data.EXP_WOUNDED_ONLY_FACTOR;
        if(expGained < 1) expGained = 1;
        return (int)expGained;
    }


    /*
    public static int getGainedExperienceFoeEachSquadMember(IUnit attacker, Array<IUnit> targetedSquad){
        int expPerSquadMember = 0;
        if(attacker.isMobilized()) {

            // calculate the mean of the squad members' level
            int levelmean = 0;
            Array<IUnit> victoriousSquad = attacker.getSquad(true);
            for (int i = 0; i < victoriousSquad.size; i++) {
                levelmean = victoriousSquad.get(i).getLevel();
            }
            if(victoriousSquad.size > 0)
                levelmean /= victoriousSquad.size;

            // get the sum of the exp gain for the fallen untis
            int sum = 0;
            for(int i = 0 ; i < targetedSquad.size; i++){
                sum += getGainedExperience(levelmean, targetedSquad.get(i).getLevel(), !targetedSquad.get(i).isOutOfAction());
            }
            expPerSquadMember = 1 + sum/victoriousSquad.size;
            if(expPerSquadMember > 100) expPerSquadMember = 100;
        }
        return expPerSquadMember;
    }
    */

    public static int getLootRate(IUnit attacker){
        int lootRate = 0;
        if(attacker.isMobilized() && attacker.getArmy().isPlayerControlled())
            lootRate = Data.BASE_DROP_RATE + attacker.getAppDexterity()/2 + attacker.getChiefCharisma()/2;
        return lootRate;
    }



    public static int getStealRate(int rowRobber, int colRobber, int rowStolen, int colStolen, IUnit stealer, IUnit stolen, Battlefield battlefield){
        int stealRate = 0;
        if(stolen.isStealable()) {
            stealRate = 100 + 10 * (stolen.getAppAgility() - stolen.getAppDexterity());
            if (stealRate > 100){
                stealRate = 100;
            }
        }
        return stealRate;
    }

    public static int getHealPower(int rowActor, int colActor, int rowTarget, int colTarget, IUnit healer, IUnit patient, Battlefield battlefield) {
        return Data.HEAL_BASE_POWER + healer.getLevel()/2;
    }
}
