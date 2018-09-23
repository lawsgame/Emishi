package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;

public class Formulas {

    public static int getHitRate(int rowAttacker0, int colAttacker0, int rowDefender0, int colDefender0, Battlefield battlefield){
        int hitrate = getCurrentAttackAccuracy(rowAttacker0, colAttacker0, rowDefender0, colDefender0, battlefield) - getCurrentAvoidance(rowAttacker0, colAttacker0, rowDefender0, colDefender0, battlefield);
        return (hitrate > 0) ? hitrate : 0;
    }

    public static int getDealtDamage(int rowAttacker0, int colAttacker0, int rowDefender0, int colDefender0, Battlefield battlefield){
        int dealtdamage = 0;
        if(battlefield.isTileOccupied(rowAttacker0, colAttacker0)) {
            dealtdamage += getRandomlyAttackMight(
                    getCurrentAttackMightRange(rowAttacker0, colAttacker0, rowDefender0, colDefender0, battlefield),
                    battlefield.getUnit(rowAttacker0, colAttacker0).getAppDexterity());
            dealtdamage -= getCurrentDefense(rowAttacker0, colAttacker0, rowDefender0, colDefender0, battlefield);
        }
        return (dealtdamage > 0) ? dealtdamage : 0;
    }

    public static int getRandomlyAttackMight(int[] attackMightRange, int attackerDexterity){
        int attackMight = 0;
        float p = 0f;
        float r = MathUtils.random();
        for (int am = attackMightRange[0]; am <= attackMightRange[1]; am++) {
            p += getDamageValueProbability(am - attackMightRange[0] + 1,
                    attackMightRange[1] - attackMightRange[0] + 1,
                    attackerDexterity,
                    Data.DEALT_DAMAGE_LN_RANDOM);
            System.out.println("am : "+am+" | r : "+r+" | p :"+p);
            if(r < p){
                attackMight = am;
                break;
            }
        }
        return attackMight;
    }

    /**
     *
     * @param k : k possibility
     * @param n : number of possibilities : 3 - 5 => 3 possibilities
     * @return probability, between 0.0 and 1.0
     */
    public static float getDamageValueProbability(float k, float n, int dexterity, float factor){
        float res = 0f;
        if(k > 0 && k <= n)
            res = (float) (Math.pow(k / n, Math.log(1 + factor*dexterity)) - Math.pow( (k - 1) / n, Math.log(1 + factor*dexterity)));
        System.out.print("dp : "+res+" => ");
        return res;
    }

    public static int[] getCurrentAttackMightRange(int rowAttacker0, int colAttacker0, int rowDefender0, int colDefender0, Battlefield battlefield){
        int[] attackMight = new int[2];
        if(battlefield.isTileOccupied(rowAttacker0, colAttacker0) && battlefield.isTileOccupied(rowDefender0, colDefender0)){
            IUnit attacker = battlefield.getUnit(rowAttacker0, colAttacker0);
            attackMight = attacker.getAppAttackMight();
            for(int i =0; i < attackMight.length; i++) {
                attackMight[i] += battlefield.getTile(rowAttacker0, colAttacker0).getAttackMightBonus();
            }
        }
        return attackMight;
    }

    public static int getCurrentAttackAccuracy(int rowAttacker0, int colAttacker0, int rowDefender0, int colDefender0, Battlefield battlefield){
        int attackAccuracy = 0;
        if(battlefield.isTileOccupied(rowAttacker0, colAttacker0)){
            IUnit attacker = battlefield.getUnit(rowAttacker0, colAttacker0);
            attackAccuracy = attacker.getAppAttackAccuracy();
            attackAccuracy += battlefield.getTile(rowAttacker0, colAttacker0).getAttackAccBonus();
            if(isBackstabAttack(rowAttacker0, colAttacker0, rowDefender0, colDefender0, battlefield)) {
                attackAccuracy += Data.HIT_RATE_BACK_ACC_BONUS;
            }
        }
        return attackAccuracy;
    }

    public static boolean isBackstabAttack(int rowAttacker0, int colAttacker0, int rowDefender0, int colDefender0, Battlefield battlefield){
        if( battlefield.isTileOccupied(rowDefender0, colDefender0)){
            Data.Orientation attackOr = Utils.getOrientationFromCoords(rowAttacker0, colAttacker0, rowDefender0, colDefender0);
            Data.Orientation defenderOr = battlefield.getUnit(rowDefender0, colDefender0).getOrientation();
            if(attackOr == defenderOr){
                return true;
            }
        }
        return false;
    }

    public static int getCurrentDefense(int rowAttacker0, int colAttacker0, int rowDefender0, int colDefender0, Battlefield battlefield){
        int defense = 0;
        if(battlefield.isTileOccupied(rowAttacker0, colAttacker0) && battlefield.isTileOccupied(rowDefender0, colDefender0)){
            IUnit defender = battlefield.getUnit(rowDefender0, colDefender0);
            IUnit attacker = battlefield.getUnit(rowAttacker0, colAttacker0);
            defense += defender.getAppDefense(attacker.getCurrentWeapon().getTemplate().getDamageType());
            defense += battlefield.getTile(rowDefender0, colDefender0).getDefenseBonus();
        }
        return defense;
    }

    public static int getCurrentAvoidance(int rowAttacker0, int colAttacker0, int rowDefender0, int colDefender0, Battlefield battlefield){
        int avoidance = 0;
        if(battlefield.isTileOccupied(rowDefender0, colDefender0)){
            IUnit target = battlefield.getUnit(rowDefender0, colDefender0);
            avoidance += target.getAppAvoidance();
            avoidance += battlefield.getTile(rowDefender0, colDefender0).getAvoidBonus();
        }
        return avoidance;
    }

    public static int getGainedExperience(int levelActor, int levelTarget, boolean stillFigthing) {
        double expGained = 50 + (100 / Math.PI) * Math.atan((levelTarget - levelActor - Data.EXP_LVL_GAP_FACTOR) * Data.EXP_ALPHA) - levelActor;
        if(stillFigthing) expGained *= Data.EXP_WOUNDED_ONLY_FACTOR;
        if(expGained < 1) expGained = 1;
        return (int)expGained;
    }


    /**
     *
     * @param attacker
     * @param targetedSquad, still fighting members of the opponent squad before the attack
     * @return
     */
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

    public static int getLootRate(int rowAttacker0, int colAttacker0, Battlefield battlefield){
        int lootRate = 0;
        if(battlefield.isTileOccupied(rowAttacker0, colAttacker0)){
            IUnit attacker = battlefield.getUnit(rowAttacker0, colAttacker0);
            if(attacker.isMobilized() && attacker.getArmy().isPlayerControlled())
                lootRate = Data.BASE_DROP_RATE + attacker.getAppDexterity()/2 + attacker.getChiefCharisma()/2;
        }
        return lootRate;
    }

    public static int getStealRate(int rowRobber, int colRobber, int rowStolen, int colStolen, Battlefield battlefield){
        int stealRate = 0;
        if(battlefield.isTileOccupied(rowRobber, colRobber) && battlefield.isTileOccupied(rowStolen, colStolen)){

            IUnit stolen = battlefield.getUnit(rowStolen, colStolen);
            if(stolen.isStealable()) {

                IUnit robber = battlefield.getUnit(rowRobber, colRobber);
                stealRate = 100 + 10 * (stolen.getAppAgility() - robber.getAppDexterity());
                if (stealRate > 100) stealRate = 100;
            }
        }
        return stealRate;
    }
}
