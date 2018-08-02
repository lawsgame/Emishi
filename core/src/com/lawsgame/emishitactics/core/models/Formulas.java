package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;

public class Formulas {

    public static int getHitRate(int rowAttacker0, int colAttacker0, int rowDefender0, int colDefender0, Battlefield battlefield){
        int hitrate = getCurrentAttackAccuracy(rowAttacker0, colAttacker0, rowDefender0, colDefender0, battlefield) - getCurrentAvoidance(rowAttacker0, colAttacker0, rowDefender0, colDefender0, battlefield);
        return (hitrate > 0) ? hitrate : 0;
    }

    public static int getDealtDamage(int rowAttacker0, int colAttacker0, int rowDefender0, int colDefender0, Battlefield battlefield){
        int dealtdamage = getCurrentAttackMight(rowAttacker0, colAttacker0, rowDefender0, colDefender0, battlefield) - getCurrentDefense(rowAttacker0, colAttacker0, rowDefender0, colDefender0, battlefield);
        return (dealtdamage > 0) ? dealtdamage : 0;
    }

    public static int getCurrentAttackMight(int rowAttacker0, int colAttacker0, int rowDefender0, int colDefender0, Battlefield battlefield){
        int attackMight = 0;
        if(battlefield.isTileOccupied(rowAttacker0, colAttacker0) && battlefield.isTileOccupied(rowDefender0, colDefender0)){
            IUnit attacker = battlefield.getUnit(rowAttacker0, colAttacker0);
            attackMight += attacker.getCurrentWeapon().getTemplate().getDamage();
            attackMight += attacker.getAppStrength();
            attackMight += battlefield.getTile(rowAttacker0, colAttacker0).getAttackMightBonus();
        }
        return attackMight;
    }

    public static int getCurrentAttackAccuracy(int rowAttacker0, int colAttacker0, int rowDefender0, int colDefender0, Battlefield battlefield){
        int attackAccuracy = 0;
        if(battlefield.isTileOccupied(rowAttacker0, colAttacker0) && battlefield.isTileOccupied(rowDefender0, colDefender0)){
            IUnit defender = battlefield.getUnit(rowDefender0, colDefender0);
            IUnit attacker = battlefield.getUnit(rowAttacker0, colAttacker0);
            attackAccuracy += attacker.getCurrentWeapon().getTemplate().getAccuracy() ;
            attackAccuracy += attacker.getAppDexterity() * Data.DEX_FACTOR_ATT_ACC;
            attackAccuracy += battlefield.getTile(rowAttacker0, colAttacker0).getAttackAccBonus();
            if(attacker.getOrientation() == defender.getOrientation())
                attackAccuracy += Data.HIT_RATE_BACK_ACC_BONUS;
            if(attackAccuracy > 100)
                attackAccuracy = 100;
        }
        return attackAccuracy;
    }

    public static int getCurrentDefense(int rowAttacker0, int colAttacker0, int rowDefender0, int colDefender0, Battlefield battlefield){
        int defense = 0;
        if(battlefield.isTileOccupied(rowAttacker0, colAttacker0) && battlefield.isTileOccupied(rowDefender0, colDefender0)){
            IUnit target = battlefield.getUnit(rowDefender0, colDefender0);
            IUnit attacker = battlefield.getUnit(rowAttacker0, colAttacker0);
            defense += target.getAppDefense(attacker.getCurrentWeapon().getTemplate().getDamageType());
            defense += battlefield.getTile(rowDefender0, colDefender0).getDefenseBonus();
        }
        return defense;
    }

    public static int getCurrentAvoidance(int rowAttacker0, int colAttacker0, int rowDefender0, int colDefender0, Battlefield battlefield){
        int avoidance = 0;
        if(battlefield.isTileOccupied(rowAttacker0, colAttacker0) && battlefield.isTileOccupied(rowDefender0, colDefender0)){
            IUnit target = battlefield.getUnit(rowDefender0, colDefender0);
            avoidance += target.getAppAvoidance();
            avoidance += battlefield.getTile(rowDefender0, colDefender0).getAvoidBonus();
        }
        return avoidance;
    }

    public static int getGainedExperience(int levelActor, int levelTarget, boolean stillFigthing) {
        double expGained = 50 + (100 / Math.PI) * Math.atan((levelTarget - levelActor - Data.EXP_LVL_GAP_FACTOR) * Data.EXP_ALPHA) - levelActor;
        if(expGained < 0) expGained = 0;
        if(stillFigthing) expGained *= Data.EXP_WOUNDED_ONLY_FACTOR;
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

}
