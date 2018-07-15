package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.constants.Data.BannerSign;
import com.lawsgame.emishitactics.core.constants.Data.Behaviour;
import com.lawsgame.emishitactics.core.constants.Data.Ethnicity;
import com.lawsgame.emishitactics.core.constants.Data.Item;
import com.lawsgame.emishitactics.core.constants.Data.Orientation;
import com.lawsgame.emishitactics.core.constants.Data.TileType;
import com.lawsgame.emishitactics.core.constants.Data.UnitAppointmentErrorMsg;
import com.lawsgame.emishitactics.core.constants.Data.UnitTemplate;
import com.lawsgame.emishitactics.core.constants.Data.Weapon;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;

import static com.lawsgame.emishitactics.core.constants.Data.AB_TRIGGER_RATE_SKILL_FACTOR;
import static com.lawsgame.emishitactics.core.constants.Data.AGI_DODGE_FACTOR;
import static com.lawsgame.emishitactics.core.constants.Data.Allegeance;
import static com.lawsgame.emishitactics.core.constants.Data.ArmyType;
import static com.lawsgame.emishitactics.core.constants.Data.CHARM_HEAL_BONUS;
import static com.lawsgame.emishitactics.core.constants.Data.CRITICAL_DAMAGE_MODIFIER;
import static com.lawsgame.emishitactics.core.constants.Data.CRIT_BONUS_IMP_GAUNLET;
import static com.lawsgame.emishitactics.core.constants.Data.DEF_BONUS_GREAT_SHIELD;
import static com.lawsgame.emishitactics.core.constants.Data.DEF_BONUS_KEIKO;
import static com.lawsgame.emishitactics.core.constants.Data.DEF_BONUS_OYOROI;
import static com.lawsgame.emishitactics.core.constants.Data.DEF_BONUS_TANKO;
import static com.lawsgame.emishitactics.core.constants.Data.DEF_BONUS_YAYOI_SHIELD;
import static com.lawsgame.emishitactics.core.constants.Data.DEX_BONUS_EMISHI_LEGGINS;
import static com.lawsgame.emishitactics.core.constants.Data.DEX_BONUS_TANKO;
import static com.lawsgame.emishitactics.core.constants.Data.DEX_BONUS_YAMATO_TROUSERS;
import static com.lawsgame.emishitactics.core.constants.Data.DEX_BONUS_YAYOI_SHIELD;
import static com.lawsgame.emishitactics.core.constants.Data.DEX_FAC_DROP_RATE;
import static com.lawsgame.emishitactics.core.constants.Data.DEX_HIT_FACTOR;
import static com.lawsgame.emishitactics.core.constants.Data.DamageType;
import static com.lawsgame.emishitactics.core.constants.Data.DamageType.NONE;
import static com.lawsgame.emishitactics.core.constants.Data.EXP_BASE_MODIFIER;
import static com.lawsgame.emishitactics.core.constants.Data.GrowthStat;
import static com.lawsgame.emishitactics.core.constants.Data.HEAL_BASE_POWER;
import static com.lawsgame.emishitactics.core.constants.Data.LVL_GAP_FACTOR;
import static com.lawsgame.emishitactics.core.constants.Data.MAX_LEVEL;
import static com.lawsgame.emishitactics.core.constants.Data.MAX_UNITS_UNDER_WARLORD;
import static com.lawsgame.emishitactics.core.constants.Data.MAX_UNITS_UNDER_WAR_CHIEF;
import static com.lawsgame.emishitactics.core.constants.Data.MOBILITY_BONUS_HORSEMAN;
import static com.lawsgame.emishitactics.core.constants.Data.MOBILITY_BONUS_PROMOTED;
import static com.lawsgame.emishitactics.core.constants.Data.OA_CHARGING_BAR_MAW_VALUE;
import static com.lawsgame.emishitactics.core.constants.Data.PROMOTION_LEVEL;
import static com.lawsgame.emishitactics.core.constants.Data.STR_FIXE_BONUS_NAGINATA_1;
import static com.lawsgame.emishitactics.core.constants.Data.STR_FIXE_BONUS_NAGINATE_2;
import static com.lawsgame.emishitactics.core.constants.Data.STR_FIXE_BONUS_YARI_1;
import static com.lawsgame.emishitactics.core.constants.Data.STR_FIXE_BONUS_YARI_2;
import static com.lawsgame.emishitactics.core.constants.Data.UNIQUE_EQUIPMENT_FIXE_STD_BONUS;
import static com.lawsgame.emishitactics.core.constants.Data.UNIQUE_EQUIPMENT_HIGH_GROWTH_BONUS;
import static com.lawsgame.emishitactics.core.constants.Data.UNIQUE_EQUIPMENT_LOW_GROWTH_BONUS;


public class Unit extends Observable{


    protected String name;
    protected Ethnicity ethnicity;
    protected int level;
    protected String job;
    protected UnitTemplate template;
    protected Weapon primaryWeapon = Weapon.NONE;
    protected Weapon secondaryWeapon = Weapon.NONE;
    protected boolean horseman;
    protected boolean standardBearer;

    protected int experience = 0;
    protected int commandmentExperience = 0;
    protected boolean primaryWeaponEquipped = true;
    protected boolean rightHanded = true;
    private AArmy army = null;

    protected int mobility;
    protected int charisma;
    protected int leadership;
    protected int hitPoints;
    protected int strength;
    protected int defense;
    protected int dexterity;
    protected int agility;
    protected int skill;
    protected int bravery;

    protected int currentMoral;
    protected int currentHitPoints;

    protected Item item1 = Item.NONE;
    protected Item item2 = Item.NONE;
    protected boolean itemStealable = false;
    protected Data.Ability passiveAbility;
    protected Data.Ability supportAbility;
    protected Data.Ability offensiveAbility = Data.Ability.NONE;
    protected final Banner banner = new Banner();

    /**
     * battlefield execution related attributes
     */
    protected boolean guarding;
    protected boolean covering;
    protected Orientation orientation = Orientation.SOUTH;
    protected Behaviour behaviour = Behaviour.PASSIVE;
    protected int numberOfOAUses = 0;
    protected int OAChargingBar = 0;
    protected boolean moved = false;
    protected boolean acted = false;
    protected int remainigBuildingResources = Data.NB_BUILDING_MAX;




    //----------------- CONTRUCTOR ---------------------


    /*
     *
     * Basic constructor for generic soldier
     * Have to call:
     *  - setAbPas1
     *  - setAbPas2
     *  - setAbOff
     *  - setItem1
     *  - setItem2
     *  - setStealableItem
     *  - bahaviour
     *  - orientatoin
     *  - favorite hand
     *  - addBannerSign(...) x3 if relevant
     */


    public Unit(
            UnitTemplate template,
            boolean standardBearer,
            int lvl,
            Ethnicity ethnicity,
            Weapon primaryWeapon,
            Weapon secondaryWeapon,
            boolean homogeneousLevelUp){

        if(lvl - template.getStartLevel() < 0){
            this.level = template.getStartLevel();
        }else if (lvl + template.getStartLevel() > MAX_LEVEL){
            this.level = MAX_LEVEL;
        }else{
            this.level = lvl + template.getStartLevel();
        }
        this.template = template;
        this.job = (PROMOTION_LEVEL <= level)? template.getJob().getPromotionName() : template.getJob().getRecruitName();
        this.name = job;
        this.ethnicity = ethnicity;
        this.standardBearer = template.getJob().isPossiblyStandardBearerJob() && standardBearer;
        setHorseman();
        setPassiveAbility(template.getJob().getNativePassiveAbility());
        setSupportActiveAbility(template.getJob().getNativeSupportAbility());

        this.mobility = template.getJob().getMobility();
        this.charisma = template.getBaseCha();
        this.leadership = template.getBaseLd();
        this.hitPoints = template.getBaseHP();
        this.strength = template.getBaseStr();
        this.defense = template.getBaseDef();
        this.dexterity = template.getBaseDex();
        this.agility = template.getBaseAg();
        this.skill = template.getBaseSk();
        this.bravery = template.getBaseBr();

        this.primaryWeapon = (isWeaponAvailable(primaryWeapon, true)) ? primaryWeapon : pickWeapon(true);
        Weapon chosenSecondaryW = (isWeaponAvailable(secondaryWeapon, true))? secondaryWeapon : pickWeapon(false);

        if(template.getStartLevel() >= PROMOTION_LEVEL)
            promote(chosenSecondaryW);

        // levels up the build
        if(homogeneousLevelUp){
            growUpHomogeneously(chosenSecondaryW);
        }else {
            for (int n = 0; n < getLevel(); n++) {
                levelUp();
                if(n == PROMOTION_LEVEL){
                    promote(chosenSecondaryW);
                }
            }
        }
        setInitialHPAndMoral();

    }

    public Unit(UnitTemplate template, boolean standardBearer, int lvl){
        this(template, standardBearer, lvl, Ethnicity.getStandard(), Weapon.NONE, Weapon.NONE, true);
    }


    // ----------------------------   METHODS --------------------------------





    public boolean isPromoted(){
        return level > PROMOTION_LEVEL;
    }

    public int[] promote(Weapon secondaryWeapon){
        boolean becomeHorsman = !isHorseman();

        this.job = template.getJob().getPromotionName();
        if(setWeapon(secondaryWeapon, false))
            this.secondaryWeapon = pickWeapon(false);
        this.setHorseman();
        setPassiveAbility(template.getJob().getNativePassiveAbilityUponPromotion());
        setSupportActiveAbility(template.getJob().getNativeSupportAbilityUponPromotion());


        becomeHorsman = becomeHorsman && isHorseman();

        int mob = MOBILITY_BONUS_PROMOTED + ((becomeHorsman) ? MOBILITY_BONUS_HORSEMAN: 0);
        int cha = template.getProBoCha();
        int ld = template.getProBoLd();
        int hpt = template.getProBoHP();
        int str = template.getProBoStr();
        int def = template.getProBoDef();
        int dex = template.getProBoDex();
        int agi = template.getProBoAg();
        int ski = template.getProBoSk();
        int bra = template.getProBoBr();

        this.mobility += mob;
        this.charisma +=  cha;
        this.setLeadership(this.leadership + ld);
        this.hitPoints += hpt;
        this.strength += str;
        this.defense += def;
        this.dexterity += dex;
        this.agility += agi;
        this.skill += ski;
        this.bravery += bra;

        int[] gainlvl = new int[]{hpt, mob, cha, ld, str, def, dex, agi, ski, bra};
        return gainlvl;
    }

     public void growUpHomogeneously(Weapon secondaryWeapon){
        int gainlvl = getLevel() - template.getStartLevel();

        float cha = 0;
        float ld = 0;
        float hpt = 0;
        float str = 0;
        float def = 0;
        float dex = 0;
        float agi = 0;
        float ski = 0;
        float bra = 0;

        int prePromotionLvl = 0;
        int postPromotionLvl = 0;
        if(template.getStartLevel() >= PROMOTION_LEVEL){
            promote(secondaryWeapon);
            if(getLevel() >= PROMOTION_LEVEL){
                postPromotionLvl = gainlvl;
            }else{
                prePromotionLvl = PROMOTION_LEVEL - template.getStartLevel() - 1;
                postPromotionLvl = getLevel() - PROMOTION_LEVEL;
            }
        }else{
            prePromotionLvl = gainlvl;
        }

        cha += template.getGrowthCha()*prePromotionLvl;
        ld += template.getGrowthLd()*prePromotionLvl;
        hpt += template.getGrowthHP()*prePromotionLvl;
        str += template.getGrowthStr()*prePromotionLvl;
        def += template.getGrowthDef()*prePromotionLvl;
        dex += template.getGrowthDex()*prePromotionLvl;
        agi += template.getGrowthAg()*prePromotionLvl;
        ski += template.getGrowthSk()*prePromotionLvl;
        bra += template.getGrowthBr()*prePromotionLvl;

        cha += template.getProGrowthCha()*postPromotionLvl;
        ld += template.getProGrowthLd()*postPromotionLvl;
        hpt += template.getProGrowthHP()*postPromotionLvl;
        str += template.getProGrowthStr()*postPromotionLvl;
        def += template.getProGrowthDex()*postPromotionLvl;
        dex += template.getProGrowthDex()*postPromotionLvl;
        agi += template.getProGrowthAg()*postPromotionLvl;
        ski += template.getProGrowthSk()*postPromotionLvl;
        bra += template.getProGrowthBr()*postPromotionLvl;

        this.charisma +=  cha;
        this.setLeadership(this.leadership + (int)ld);
        this.hitPoints += hpt;
        this.strength += str;
        this.defense += def;
        this.dexterity += dex;
        this.agility += agi;
        this.skill += ski;
        this.bravery += bra;

    }

    public int[] levelUp(){
        int mob = 0;
        int cha = 0;
        int ld = 0;
        int hpt = 0;
        int str = 0;
        int def = 0;
        int dex = 0;
        int agi = 0;
        int ski = 0;
        int bra = 0;

        if(this.level < MAX_LEVEL) {
            this.level++;

            if (PROMOTION_LEVEL != level) {
                cha += (getAppGrowthRate(GrowthStat.CHARISMA) * 100 > Data.rand(100)) ? 1 : 0;
                ld += (getAppGrowthRate(GrowthStat.LEADERSHIP) * 100 > Data.rand(100)) ? 1 : 0;
                hpt += (getAppGrowthRate(GrowthStat.HIT_POINTS) * 100 > Data.rand(100)) ? 1 : 0;
                str += (getAppGrowthRate(GrowthStat.STRENGTH) * 100 > Data.rand(100)) ? 1 : 0;
                def += (getAppGrowthRate(GrowthStat.DEFENSE) * 100 > Data.rand(100)) ? 1 : 0;
                dex += (getAppGrowthRate(GrowthStat.DEXTERITY) * 100 > Data.rand(100)) ? 1 : 0;
                agi += (getAppGrowthRate(GrowthStat.AGILITY) * 100 > Data.rand(100)) ? 1 : 0;
                ski += (getAppGrowthRate(GrowthStat.SKILL) * 100 > Data.rand(100)) ? 1 : 0;
                bra += (getAppGrowthRate(GrowthStat.BRAVERY) * 100 > Data.rand(100)) ? 1 : 0;
            }

            this.charisma += cha;
            this.setLeadership(this.leadership + ld);
            this.hitPoints += hpt;
            this.strength += str;
            this.defense += def;
            this.dexterity += dex;
            this.agility += agi;
            this.skill += ski;
            this.bravery += bra;
        }

        int[] gainlvl = new int[]{hpt, mob, cha, ld, str, def, dex, agi, ski, bra};
        return gainlvl;
    }




    //------------------- WEAPON RELATED METHODS -----------------------------------




    public Weapon pickWeapon(boolean primaryWeaponChoice){
        int randId;
        Weapon chosenW = Weapon.NONE;
        if(primaryWeaponChoice) {
            if (this.template.getJob().getAvailableWeapons().length > 0) {
                randId = Data.rand(this.template.getJob().getAvailableWeapons().length);
                chosenW = this.template.getJob().getAvailableWeapons()[randId];
            }
        }else{
            chosenW = this.primaryWeapon;
            if(this.template.getJob().getAvailableWeaponsAfterPromotion().length > 1) {
                while (chosenW == this.primaryWeapon) {
                    randId = Data.rand(this.template.getJob().getAvailableWeaponsAfterPromotion().length);
                    chosenW = this.template.getJob().getAvailableWeaponsAfterPromotion()[randId];
                }
            }
        }
        return chosenW;
    }

    public boolean isWeaponAvailable(Weapon weapon, boolean primaryWeaponChoice){
        boolean available = false;
        if(primaryWeaponChoice){
            if(weapon != secondaryWeapon) {
                for (int i = 0; i < template.getJob().getAvailableWeapons().length; i++) {
                    if (weapon == template.getJob().getAvailableWeapons()[i]) {
                        available = true;
                    }
                }
            }
        }else {
            if (weapon != this.primaryWeapon) {
                for (int i = 0; i < template.getJob().getAvailableWeaponsAfterPromotion().length; i++) {
                    if (weapon == template.getJob().getAvailableWeaponsAfterPromotion()[i]) {
                        available= true;
                    }
                }
            }
        }
        return available;
    }

    public boolean setWeapon(Weapon weapon, boolean primaryWeaponChoice){
        if((isPromoted() || primaryWeaponChoice) && isWeaponAvailable(weapon, primaryWeaponChoice)){
            if(primaryWeaponChoice){
                this.primaryWeapon = weapon;
            }else{
                this.secondaryWeapon = weapon;
            }
            notifyAllObservers(null);
            return true;
        }

        return false;
    }

    public void switchWeapon(){
        if(isPromoted())
            this.primaryWeaponEquipped = !primaryWeaponEquipped;
        notifyAllObservers(null);
    }

    public void setCurrentWeapon(boolean primaryWeaponEquiped) {
        this.primaryWeaponEquipped = primaryWeaponEquiped;
        notifyAllObservers(null);
    }

    public Weapon getCurrentWeapon() {
        return (primaryWeaponEquipped)? primaryWeapon: secondaryWeapon;
    }





    //-------------- ARMY RELATED METHODS --------------



    public boolean isPlayerControlled(){
        return army != null && army.isPlayerControlled();
    }

    public boolean isMobilized(){
        if(army != null)
            return army.isUnitMobilized(this);
        return  false;
    }

    public boolean sameSquadAs(Unit unit){
        boolean res = false;
        boolean thisBelongSquad = false;
        boolean unitBelongSquad = false;
        if(army != null && unit != null && unit.army != null && army == unit.army){
            Array<Array<Unit>> squads = army.getAllSquads();
            for(int i=0; i< squads.size; i++){
                for(int j=0; j < squads.get(i).size; j++){
                    if(squads.get(i).get(j) == this){
                        thisBelongSquad = true ;
                    }
                    if(squads.get(i).get(j) == unit){
                        unitBelongSquad = true;
                    }
                }
                res = res || (thisBelongSquad && unitBelongSquad);
                thisBelongSquad = false;
                unitBelongSquad = false;
            }
        }
        return res;
    }

    public boolean sameArmyAs(Unit unit){
        return unit != null && unit.army != null && army != null && army.getId() == unit.army.getId();
    }

    public Allegeance getAllegeance(){
        Allegeance a = null;
        if(army == null){
            a = Allegeance.NEUTRAL;
        }else{
            if(army.isAlly()){
                a = Allegeance.ALLY;
            }else{
                a = Allegeance.ENEMY;
            }
        }
        return a;
    }

    public boolean sideWith(Allegeance a){
        return getAllegeance() == a;
    }

    public boolean fightWith(Allegeance a){
        return (a == Allegeance.ENEMY && getAllegeance() == Allegeance.ALLY) || (a == Allegeance.ALLY && getAllegeance() == Allegeance.ENEMY);
    }

    public Array<Unit> getSquad(){
        Array<Unit> squad;
        if(army != null)
            squad = army.getSquad(this);
        else
            squad = new Array<Unit>();
        return  squad;
    }

    public boolean isStandardBearer(){ return standardBearer; }

    public boolean addBannerSign(BannerSign sign) {
        boolean res = false;
        if(standardBearer){
           res = banner.addSign(sign);
           if(isMobilized())
               army.checkComposition();
        }
        return res;
    }



    // --------------------- BATTLE RESOLUTION USEFULL METHODS ------------------------------
    /*
     - dealtdamage = attack damage - defense
     - hitrate = attack accuracy - avoidance
     - trigger ability rate
     - drop / steal chance of sucess
     - healpower & treated
     - experience : gain of leadership
     */


    public int getCurrentAbilityTriggerRate(boolean bannerAtRange){
        return AB_TRIGGER_RATE_SKILL_FACTOR * getCurrentSk() + getChiefCharsima() + getSquadBannerBonus(BannerSign.IZANAGI, bannerAtRange);
    }

    public int getStealRate(Unit target, TileType stealerTile, boolean bannerAtRange){
        int rate = 100 + 10*(target.getCurrentAg(this.getCurrentWeapon()) - getCurrentDex(bannerAtRange));
        if(rate > 100) rate = 100;
        if(rate < 0) rate = 0;
        return rate;
    }

    public int getDropRate(boolean bannerAtRange){
        return DEX_FAC_DROP_RATE * getCurrentDex(bannerAtRange) + getChiefCharsima();
    }

    public int getAppHealPower(){
        return HEAL_BASE_POWER + level + (isUsing(Item.CHARM) ? CHARM_HEAL_BONUS: 0);
    }

    public int getCurrentHealPower(){
        return getAppHealPower();
    }

    public boolean treated(int healPower){
        if(isWounded()) {
            if (healPower + hitPoints > getAppHitPoints()) {
                setInitialHPAndMoral();
            } else {
                currentHitPoints += healPower;
                if (currentMoral > 0) {
                    currentMoral += healPower;
                } else {
                    currentMoral = getAppMoral() + currentHitPoints - getAppHitPoints();
                    if (currentMoral < 0) {
                        currentMoral = 0;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public int addLeadershipExperience(int gainEXP) {
        this.commandmentExperience += gainEXP;
        int gainLd = this.commandmentExperience / Data.EXP_REQUIRED_LD_LEVEL_UP;
        this.setLeadership(this.leadership + gainLd);
        this.commandmentExperience = this.commandmentExperience % Data.EXP_REQUIRED_LD_LEVEL_UP;
        return gainLd;
    }

    public static int getExperiencePoints(int attackerLevel, int defenderLevel, boolean killed, boolean withdrawn){
        double exp = EXP_BASE_MODIFIER * Math.atan((defenderLevel - attackerLevel)* LVL_GAP_FACTOR) - attackerLevel;
        if(withdrawn){
            if(!killed){
                exp += Data.EXP_BONUS_FROM_LETTING_ENEMY_FLEES;
            }
        }else{
            exp *= Data.EXP_WOUNDED_ONLY_FACTOR;
        }
        exp = (exp < 99) ? exp : 99;
        exp = (exp > 1) ? exp : 1;
        return (int)exp;
    }

    public int getExperiencePoints(int defenderLevel, boolean killed, boolean withdrawn){
        return Unit.getExperiencePoints(this.getLevel(), defenderLevel, killed, withdrawn);
    }

    public int getExperiencePointsPerUnitSquad(Array<Integer> lvlFleeingUnits){
        int exp = 0;
        Array<Unit> squad = getSquad();

        if(squad.size > 0) {

            int averageLvl = 0;
            int squadsize = getSquad().size;
            for (int i = 0; i < squad.size; i++) {
                averageLvl = squad.get(i).getLevel();
            }

            averageLvl /=squadsize;
            for (int i = 0; i < lvlFleeingUnits.size; i++) {
                exp = Unit.getExperiencePoints(averageLvl, lvlFleeingUnits.get(i), false, true);
            }
            exp /= squadsize;

        }
        return exp;
    }

    public boolean addExperience(int exp) {
        boolean levelup = false;
        if(level < MAX_LEVEL) {
            if (this.experience + exp < 100) {
                this.experience += (level + 1 == MAX_LEVEL) ? 0 : exp;
            } else {
                this.experience = (level + 1 == MAX_LEVEL) ? 0 : this.experience + exp - Data.EXP_REQUIRED_LEVEL_UP;
                levelup = true;
            }
        }
        return levelup;

    }

    public int getAppAvoidance(DamageType damageType){
        return AGI_DODGE_FACTOR* getAppAgility(damageType);
    }

    public int getAppAttackAccuracy(){
        return getCurrentWeapon().getAccuracy() + getChiefCharsima() + DEX_HIT_FACTOR* getAppDexterity();
    }

    public int getHitRate(Unit defender, TileType defenderTile, boolean attackBannerAtRange){
        int attackAcc = getCurrentWeapon().getAccuracy() + getChiefCharsima() + DEX_HIT_FACTOR* getCurrentDex(attackBannerAtRange);
        int defenderAvo = AGI_DODGE_FACTOR* defender.getCurrentAg(this.getCurrentWeapon()) + defenderTile.getAvoidBonus();
        int hitrate = attackAcc - defenderAvo;
        return hitrate;
    }

    public int geAppCriticalHitAccuracy(){
        return getAppDexterity() + getAppSkill() + getChiefCharsima();
    }

    public int getAppCriticalHitAvoidance(DamageType damageType){
        return getAppAgility(damageType) + getAppSkill();
    }

    public int getCriticalHitRate(boolean attackerBannerAtRange, Unit defender, int currentHitRate){
        int currentHitAcc = getCurrentDex(attackerBannerAtRange) + getCurrentSk();
        int currentHitAvo = defender.getCurrentDex(attackerBannerAtRange) + defender.getCurrentSk();
        int criticalHitrate = currentHitAcc - currentHitAvo;
        if(criticalHitrate > currentHitRate) criticalHitrate = currentHitRate  -1;
        if(criticalHitrate < 0 ) criticalHitrate = 0;
        return criticalHitrate;
    }

    public int getAppAttackDamage(){
        return getCurrentWeapon().getDamage() + getAppStrength();
    }

    public int getDealtDamage( Unit defender, boolean critical, TileType attackerTile, TileType defenderTile, boolean attackBannerAtRange, boolean defenderBannerAtRange){
        int defenseDefender = defender.getCurrentDef(defenderTile, this.getCurrentWeapon(), this.getCurrentSk(), defenderBannerAtRange);
        int attackDamage = getCurrentWeapon().getDamage() + getCurrentStrength(attackerTile, defender.isHorseman(), attackBannerAtRange);
        float critMod = ((critical) ? getCriticalDamageModifier(defender.isUsing(Item.IMPERIAL_ARMBAND)) : 1f);
        return (int) (critMod*attackDamage - defenseDefender);
    }

    public void applyDamage(int damageTaken, boolean moralDamageOnly, boolean notifyObservers){
        if(this.currentMoral > damageTaken){
            // the unit survive
            this.currentMoral -= damageTaken;
            if(!moralDamageOnly) this.currentHitPoints -= damageTaken;

        }else{
            // the unit dies or flies
            if(this.currentHitPoints > damageTaken){
                this.currentMoral = 0;
                if(!moralDamageOnly) this.currentHitPoints -= damageTaken;
            }else{
                this.currentMoral = 0;
                if(!moralDamageOnly) this.currentHitPoints =0;
            }

            // if the unit is a war chief, the consequences deepens
            if(isWarChief()){
                int moralDamage = getAppCharisma();
                if(isWarlord()){
                    Array<Array<Unit>> army = getArmy().getAllSquads();
                    for(int i = 0; i < army.size; i++) {
                        for(int j = 0; j < army.get(i).size; j++){
                            if( i + j > 0) {
                                army.get(i).get(j).applyDamage((i == 0) ? moralDamage * 2 : moralDamage, true, notifyObservers);

                            }
                        }
                    }
                }else{
                    Array<Unit> squad = getArmy().getSquad(this);
                    for(int i = 1; i < squad.size; i++){
                        squad.get(i).applyDamage(moralDamage, true,notifyObservers);

                    }
                }
            }
        }

        /*
         * that line can be explained as followed
         * purpose : it follows the notification automatically subordinates who face moral damage without
         * having notify the target (main defender) whose notification requires more paramaters to be sent,
         * namely "critical" & "backstabé parameters, yet those parameters are irrelevant modelwise and shall not
         * be added to this method signature.
         * Therefore, adding the "&& moralDamageOnly" allows to elegantly accomplish all the points above.
         */
        if(notifyObservers && moralDamageOnly) notifyAllObservers(new DamageNotification(moralDamageOnly, damageTaken, false, false));
    }

    public static class DamageNotification{
        public boolean moralOnly;
        public int damageTaken;
        public boolean critical;
        public boolean backstab;

        public DamageNotification (boolean moralOnly, int damageTaken, boolean critical, boolean backstab){
            this.moralOnly = moralOnly;
            this.damageTaken = damageTaken;
            this.critical = critical;
            this.backstab = backstab;
        }

        public boolean isRelevant(){
            return damageTaken > 0;
        }
    }




    //-------------------- GETTERS & SETTERS --------------------------






    public Item getItem1() {
        return item1;
    }

    public Item getItem2() {
        return item2;
    }

    public int getNbItemsEquiped(){
        return ((item1 !=  Item.NONE) ? 1 : 0) + ((item2 !=  Item.NONE) ? 1 : 0);
    }

    public boolean equip(Item item, boolean firstSlot) {
        int currentLd = getAppLaedership();
        boolean armyRecomposed = false;


        // check if the unit does not hold yet the item of the same type
        if (firstSlot) {
            if (item2.getItemType() != item.getItemType()) this.item1 = item;
        } else {
            if (item1.getItemType() != item.getItemType()) this.item2 = item;
        }

        // update army composition
        if (getAppLaedership() < currentLd && isWarChief()) {
            armyRecomposed = true;
            if (isWarlord()) {
                army.appointWarLord(this);
            } else {
                army.disengage(this);
                army.appointWarChief(this);
            }
        }

        notifyAllObservers(null);
        return armyRecomposed;
    }

    public void unequip(boolean firstSlot){
        equip(Item.NONE, firstSlot);
    }

    public boolean isUsing(Item eq){
        return item1 == eq || item2 == eq;
    }

    public void setItemStealable(boolean itemStealable) {
        this.itemStealable = itemStealable;
        notifyAllObservers(null);
    }

    public boolean isStealable(){
        return itemStealable && item1 != null && item1 != Item.NONE;
    }

    public boolean has(Data.Ability abb) {
        return abb == passiveAbility || abb == offensiveAbility || abb == supportAbility;
    }

    public Banner getBanner() { return banner; }

    public boolean isDead() {
        return currentHitPoints == 0;
    }

    public boolean isOutOfCombat(){
        return currentMoral == 0;
    }

    public boolean isWarlord(){
        return army != null && army.getWarlord() != null && army.getWarlord() == this ;
    }

    public boolean isWarChief(){
        return army != null && army.getWarChiefs().contains(this, true);
    }

    public int getNbMaxUnits(){
        int nbMax = 0;
        if(isWarlord()){
            nbMax = (int) (3 + (getAppLaedership() + 2)/5.0f);
            if(nbMax > MAX_UNITS_UNDER_WARLORD ) nbMax = (int) MAX_UNITS_UNDER_WARLORD;
        }else if(isWarChief()){
            nbMax = (int) (2 + (getAppLaedership() + 1)/5.0f);
            if(nbMax > MAX_UNITS_UNDER_WAR_CHIEF ) nbMax = (int) MAX_UNITS_UNDER_WAR_CHIEF;
        }
        return nbMax;
    }

    public int getNbMaxUnits(boolean asWarlord){
        int nbMax = 0;
        if(asWarlord){
            nbMax = (int) (3 + (getAppLaedership() + 2)/5.0f);
            if(nbMax > MAX_UNITS_UNDER_WARLORD ) nbMax = (int) MAX_UNITS_UNDER_WARLORD;
        }else {
            nbMax = (int) (2 + (getAppLaedership() + 1)/5.0f);
            if(nbMax > MAX_UNITS_UNDER_WAR_CHIEF ) nbMax = (int) MAX_UNITS_UNDER_WAR_CHIEF;
        }
        return nbMax;
    }

    public int getNbMaxWarChiefs(){
        return 1 + (int) ((isWarlord())? getAppLaedership()/6.0f: 0);
    }

    public int getChiefCharsima(){
        int bonus = 0;
        if(isMobilized()){
            bonus = getSquad().get(0).getAppCharisma();
        }
        return bonus;
    }

    public void setPassiveAbility(Data.Ability pasAb) {
        if(pasAb.getType() == Data.AbilityType.PASSIVE || pasAb.getType() == Data.AbilityType.NONE)
            this.passiveAbility = pasAb;
        notifyAllObservers(null);
    }

    public void setSupportActiveAbility(Data.Ability supAb) {
        if(supAb.getType() == Data.AbilityType.PASSIVE || supAb.getType() == Data.AbilityType.NONE)
            this.supportAbility = supAb;
        notifyAllObservers(null);
    }

    public void setOffensiveActiveAbility(Data.Ability offAb) {
        if(offAb.getType() == Data.AbilityType.OFFENSIVE || offAb.getType() == Data.AbilityType.NONE)
            this.offensiveAbility = offAb;
        notifyAllObservers(null);
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public String getJobName() {
        return job;
    }

    public boolean isRightHanded() {
        return rightHanded;
    }

    public void setRightHanded(boolean rightHanded){
        this.rightHanded = rightHanded;
    }

    public UnitTemplate getTemplate() {
        return template;
    }

    public Weapon getPrimaryWeapon() {
        return primaryWeapon;
    }

    public Weapon getSecondaryWeapon() {
        return secondaryWeapon;
    }

    public boolean isHorseman() {
        return horseman;
    }

    public void setHorseman(){
        boolean canbehorseman = (isPromoted()) ? template.getJob().isPossiblyHorsemanUnponPromotion() : template.getJob().isPossiblyHorseman();
        if(!primaryWeapon.isFootmanOnly() && !secondaryWeapon.isFootmanOnly() && canbehorseman){
            horseman = true;
        }else{
            horseman = false;
        }
    }

    /*

    the stats go in 3 different flavors:
     - BASE : no modified by any factor
     - APPARENT : value of the stat enhance with battlefield-permanent bonus such as those given by items.
     - CURRENT : value of the stat at one specific moment of the battlefield

     */

    public void setLeadership(int leadership) {
        this.leadership = leadership;
        if(isMobilized()) army.checkComposition();
        notifyAllObservers(null);
    }

    public void setInitialHPAndMoral(){
        currentHitPoints = hitPoints;
        currentMoral = getAppMoral();
        notifyAllObservers(null);
    }

    public int getAppMoral(){
        int moral = Data.BRAVERY_MORAL_FACTOR*getAppBravery() + getChiefCharsima();
        if(moral >= currentHitPoints) moral = currentHitPoints - 1;
        return moral;
    }

    public int getBaseCharisma(){ return charisma;}
    public int getBaseLeadership() { return leadership; }
    public int getBaseMobility(){ return mobility + ((isHorseman()) ? Data.MOBILITY_BONUS_HORSEMAN : 0);}
    public int getBaseHitPoints() { return hitPoints; }
    public int getBaseStrength() { return  strength; }
    public int getBaseDefense(){ return defense; }
    public int getBaseDexterity() { return  dexterity; }
    public int getBaseAgility() { return agility; }
    public int getBaseSkill() { return skill; }
    public int getBaseBravery() { return bravery; }

    public int getAppCharisma(){ return charisma + (isUsing(Item.KABUTO)? UNIQUE_EQUIPMENT_FIXE_STD_BONUS : 0);}
    public int getAppLaedership(){ return leadership + (isUsing(Item.WAR_CHIEF_CLOAK)? UNIQUE_EQUIPMENT_FIXE_STD_BONUS : 0); }
    public int getAppMobility() { return mobility + (isUsing(Item.WEI_BOOTS)? 1: 0) + ((horseman) ? MOBILITY_BONUS_HORSEMAN: 0); }
    public int getAppHitPoints(){ return hitPoints; }
    public int getAppStrength(){ return strength + (isUsing(Item.GAUNLET)? UNIQUE_EQUIPMENT_FIXE_STD_BONUS : 0); }
    public int getAppDexterity(){ return dexterity + (isUsing(Item.ARMBAND)? UNIQUE_EQUIPMENT_FIXE_STD_BONUS : 0); }

    public int getAppDefense(DamageType opponentWeaponDamageType){
        int appDefense = defense + (isUsing(Item.OYOROI_ARMOR)? DEF_BONUS_OYOROI : 0);
        switch(opponentWeaponDamageType){
            case PIERCING:
                if(isUsing(Item.YAYOI_SHIELD))
                    appDefense += DEF_BONUS_YAYOI_SHIELD;
                else if(isUsing(Item.GREAT_SHIELD))
                    appDefense += DEF_BONUS_GREAT_SHIELD;
                break;
            case EDGED:
                if(isUsing(Item.TANKO_ARMOR))
                    appDefense += DEF_BONUS_TANKO;
                else if(isUsing(Item.KEIKO_ARMOR))
                    appDefense += DEF_BONUS_KEIKO;
                break;
            default: break;
        }
        return appDefense;
    }

    public int getAppAgility(DamageType opponentWeaponDamageType){
        int appAgility = agility;
        switch(opponentWeaponDamageType){
            case PIERCING:
                if(isUsing(Item.YAYOI_SHIELD))
                    appAgility += DEX_BONUS_YAYOI_SHIELD;
                break;
            case BLUNT:
                if(isUsing(Item.TANKO_ARMOR))
                    appAgility += DEX_BONUS_TANKO;
                break;
            case EDGED:
                if(isUsing(Item.EMISHI_LEGGINGS))
                    appAgility += DEX_BONUS_EMISHI_LEGGINS;
                if(isUsing(Item.YAMATO_TROUSERS))
                    appAgility += DEX_BONUS_YAMATO_TROUSERS;
                break;
            default: break;
        }
        return appAgility;
    }

    public int getAppBravery(){ return bravery; }
    public int getAppSkill(){ return skill + (isUsing(Item.MASTER_BELT)? UNIQUE_EQUIPMENT_FIXE_STD_BONUS : 0); }
    public int getAppPrimaryWeaponRangeMin(){ return primaryWeapon.getRangeMin(); }
    public int getAppPrimaryWeaponRangeMax(){ return primaryWeapon.getRangeMax() + ((isUsing(Item.EMISHI_RING) && primaryWeapon.isRangedW())? 1: 0); }
    public int getAppSecondaryWeaponRangeMin(){ return (secondaryWeapon != Weapon.NONE) ? secondaryWeapon.getRangeMin() : 0; }
    public int getAppSecondaryWeaponRangeMex(){ return (secondaryWeapon != Weapon.NONE) ?  secondaryWeapon.getRangeMax() + ((isUsing(Item.EMISHI_RING) && secondaryWeapon.isRangedW())? 1: 0) : 0; }
    public int getAppCurrentWeaponRangeMin(){ return (primaryWeaponEquipped) ? getAppPrimaryWeaponRangeMin() : getAppSecondaryWeaponRangeMin(); }
    public int getAppCurrentWeaponRangeMax(){ return (primaryWeaponEquipped) ? getAppPrimaryWeaponRangeMax() : getAppSecondaryWeaponRangeMex(); }

    public int getAppAbilityTriggerRate() {
        return AB_TRIGGER_RATE_SKILL_FACTOR * getCurrentSk() + getChiefCharsima();
    }

    public int getCurrentMob(){ return getAppMobility(); }
    public int getCurrentHitpoints() {
        return currentHitPoints;
    }
    public int getCurrentMoral(){
        return currentMoral;
    }

    public int getCurrentStrength(TileType tileType, boolean isDefenderHorseman, boolean bannerAtRange) {
        int str = getAppStrength();
        if(isDefenderHorseman){
            if(getCurrentWeapon() == Weapon.YARI)
                str += STR_FIXE_BONUS_YARI_1 + getCurrentSk()/ STR_FIXE_BONUS_YARI_2;
            else if(getCurrentWeapon() == Weapon.NAGINATA)
                str += STR_FIXE_BONUS_NAGINATA_1 + getCurrentSk()/ STR_FIXE_BONUS_NAGINATE_2;
        }
        return str + tileType.getStrengthBonus() + getSquadBannerBonus(BannerSign.APEHUCI,bannerAtRange);
    }

    public int getCurrentDef(TileType tileType, Weapon opponentWeapon , int opponentCurrentSkill, boolean bannerAtRange) {
        int baseDef = getAppDefense(NONE);

        int armorDefBonus = 0;
        switch(opponentWeapon.getType()){
            case PIERCING:
                if(isUsing(Item.YAYOI_SHIELD))
                    armorDefBonus += DEF_BONUS_YAYOI_SHIELD;
                else if(isUsing(Item.GREAT_SHIELD))
                    armorDefBonus += DEF_BONUS_GREAT_SHIELD;
                break;
            case EDGED:
                if(isUsing(Item.TANKO_ARMOR))
                    armorDefBonus += DEF_BONUS_TANKO;
                else if(isUsing(Item.KEIKO_ARMOR))
                    armorDefBonus += DEF_BONUS_KEIKO;
                break;
            default: break;
        }

        float defFactor = 0;
        if(opponentWeapon == Weapon.KANABO){
            defFactor += 0.7f - 0.05f*opponentCurrentSkill;
        }else if (opponentWeapon == Weapon.NODACHI){
            defFactor += 0.8f - 0.03f*opponentCurrentSkill;
        }
        return (int)(baseDef*defFactor) + armorDefBonus + tileType.getDefenseBonus() + getSquadBannerBonus(BannerSign.HACHIMAN,bannerAtRange) ;
    }

    public int getCurrentAg(Weapon opponentWeapon) {
        int agi = getAppAgility(NONE);
        switch(opponentWeapon.getType()){
            case PIERCING:
                if(isUsing(Item.YAYOI_SHIELD))
                    agi += DEX_BONUS_YAYOI_SHIELD;
                break;
            case BLUNT:
                if(isUsing(Item.TANKO_ARMOR))
                    agi += DEX_BONUS_TANKO;
                break;
            case EDGED:
                if(isUsing(Item.EMISHI_LEGGINGS))
                    agi += DEX_BONUS_EMISHI_LEGGINS;
                if(isUsing(Item.YAMATO_TROUSERS))
                    agi += DEX_BONUS_YAMATO_TROUSERS;
                break;
            default: break;
        }
        return agi;
    }

    public int getCurrentDex(boolean bannerAtRange) { return getAppDexterity() + getSquadBannerBonus(BannerSign.SHIRAMBA, bannerAtRange); }
    public int getCurrentSk() { return getAppSkill(); }
    public int getCurrentRangeMin(){ return getAppCurrentWeaponRangeMin(); }

    public int getCurrentRangeMax(TileType tile, boolean bannerAtRange){
        int rangeMax = getAppCurrentWeaponRangeMax();
        if(getCurrentWeapon().isRangedW()){
            rangeMax += tile.enhanceRange() ? 1 : 0;
            rangeMax += getSquadBannerBonus(BannerSign.AMATERASU, bannerAtRange);
        }
        return  rangeMax;
    }

    public float getBaseGrowthRate(GrowthStat growthStat){
        float gr = 0.0f;
        switch (growthStat){
            case CHARISMA: gr = isPromoted()? template.getProGrowthCha() : template.getGrowthCha(); break;
            case LEADERSHIP: gr = isPromoted()? template.getProGrowthLd() : template.getGrowthLd(); break;
            case MOBILITY: gr = 0; break;
            case HIT_POINTS: gr = isPromoted()? template.getProGrowthHP() : template.getGrowthHP(); break;
            case STRENGTH: gr = isPromoted()? template.getProGrowthStr() : template.getGrowthStr(); break;
            case DEFENSE: gr = isPromoted()? template.getProGrowthDef() : template.getGrowthDef(); break;
            case DEXTERITY: gr = isPromoted()? template.getProGrowthDex() : template.getGrowthDex(); break;
            case AGILITY: gr = isPromoted()? template.getProGrowthAg() : template.getGrowthAg(); break;
            case SKILL: gr = isPromoted()? template.getProGrowthSk() : template.getGrowthSk(); break;
            case BRAVERY: gr = isPromoted()? template.getProGrowthBr() : template.getGrowthBr(); break;
        }
        return  gr;
    }

    public float getAppGrowthRate(GrowthStat growthStat){
        float gr = 0.0f;
        switch (growthStat){
            case CHARISMA: gr = getBaseGrowthRate(GrowthStat.CHARISMA) + (isUsing(Item.KABUTO)? UNIQUE_EQUIPMENT_HIGH_GROWTH_BONUS :0); break;
            case LEADERSHIP: gr = getBaseGrowthRate(GrowthStat.LEADERSHIP); break;
            case MOBILITY: gr = getBaseGrowthRate(GrowthStat.MOBILITY); break;
            case HIT_POINTS: gr = getBaseGrowthRate(GrowthStat.HIT_POINTS); break;
            case STRENGTH: gr = getBaseGrowthRate(GrowthStat.STRENGTH) + (isUsing(Item.GAUNLET)? UNIQUE_EQUIPMENT_LOW_GROWTH_BONUS :0); break;
            case DEFENSE: gr = getBaseGrowthRate(GrowthStat.DEFENSE); break;
            case DEXTERITY: gr = getBaseGrowthRate(GrowthStat.DEXTERITY) + (isUsing(Item.ARMBAND)? UNIQUE_EQUIPMENT_HIGH_GROWTH_BONUS :0); break;
            case AGILITY: gr = getBaseGrowthRate(GrowthStat.AGILITY); break;
            case SKILL: gr = getBaseGrowthRate(GrowthStat.SKILL) + (isUsing(Item.MASTER_BELT)? UNIQUE_EQUIPMENT_HIGH_GROWTH_BONUS :0); break;
            case BRAVERY: gr = getBaseGrowthRate(GrowthStat.BRAVERY); break;
        }
        return  gr;
    }

    public float getCriticalDamageModifier(boolean defenderUseImpArmband){
        float critDamMod = CRITICAL_DAMAGE_MODIFIER;
        critDamMod += (isUsing(Item.IMPERIAL_GAUNTLET))? CRIT_BONUS_IMP_GAUNLET: 0;
        critDamMod /= defenderUseImpArmband ? Data.CRIT_REDUCTION_DAMAGE_IMP_ARMBAND : 0;
        return  critDamMod;
    }

    public Data.Ability getPassiveAbility() {
        return passiveAbility;
    }

    public Data.Ability getSupportAbility() {
        return supportAbility;
    }

    public Data.Ability getOffensiveAbility() {
        return offensiveAbility;
    }

    public int getSquadBannerBonus(BannerSign sign, boolean bannerAtRange){
        int bonus = 0;
        if(army != null && bannerAtRange) {
            Banner squadBanner = army.getSquadBanner(this);
            bonus = squadBanner.getBonusRelativeTo(sign);
        }
        return bonus;
    }

    public void setName(String name) {
        if(name != null) {
            this.name = name;
            notifyAllObservers(null);
        }
    }

    public boolean isDone() {
        return moved && acted;
    }

    public AArmy getArmy() {
        return army;
    }

    public boolean hasMoved(){ return moved; }

    public void setMoved(boolean moved){ this.moved = moved; }

    public boolean hasActed() { return acted; }

    public void setActed(boolean acted) { this.acted = acted; }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        notifyAllObservers(null);
    }

    public Ethnicity getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(Ethnicity ethnicity) {
        this.ethnicity = ethnicity;
        notifyAllObservers(null);
    }

    public boolean has(Weapon weapon){
        return weapon == primaryWeapon || weapon == secondaryWeapon;
    }

    public int getNumberOfOAUses(){
        return  numberOfOAUses;
    }

    public void incrementOfOAUses(){
        numberOfOAUses++;
    }

    public int getCommandmentExperience() {
        return commandmentExperience;
    }

    public int getRemainigBuildingResources(){
        return remainigBuildingResources;
    }

    public void consumeBuildingResource(){
        this.remainigBuildingResources--;
    }

    public boolean isWounded() { return currentHitPoints < getAppHitPoints(); }

    public int getOAChargingBar() {
        return OAChargingBar;
    }

    public void addPointsToOAChargingBar(int points){
        if(points + OAChargingBar < Data.OA_CHARGING_BAR_MAW_VALUE ){
            this.OAChargingBar +=points;
        }else{
            this.OAChargingBar = points + OAChargingBar - OA_CHARGING_BAR_MAW_VALUE;
            this.numberOfOAUses++;
        }
    }

    public int getExperience() {
        return experience;
    }

    public Behaviour getBehaviour() {
        return behaviour;
    }

    public void setBehaviour(Behaviour behaviour){
        this.behaviour = behaviour;
    }

    public boolean isGuarding() {
        return guarding;
    }

    public void setGuarding(boolean guarding) {
        this.guarding = guarding;
    }

    public boolean isCovering() {
        return covering;
    }

    public void setCovering(boolean covering) {
        this.covering = covering;
    }

    @Override
    public String toString() {
        return "Unit{" +
                "\nname='" + name + '\'' +
                "\nethnicity=" + ethnicity +
                "\nlevel=" + level +
                "\njob='" + job + '\'' +
                "\ntemplate=" + template +
                "\nprimaryWeapon=" + primaryWeapon +
                "\nsecondaryWeapon=" + secondaryWeapon +
                "\nhorseman=" + horseman +
                "standardBearer=" + standardBearer +
                "\nexperience=" + experience +
                "\ncommandmentExperience=" + commandmentExperience +
                "\nprimaryWeaponEquipped=" + primaryWeaponEquipped +
                "\nrightHanded=" + rightHanded +
                "\nposition =" + ((isWarlord()) ? "warlord" : ((isWarChief()) ? " warchief" : "soldier"))+
                "\n\n\nmobility=" + mobility +
                "charisma=" + charisma +
                "\nleadership=" + leadership +
                "\nhitPoints=" + hitPoints +
                "\nstrength=" + strength +
                "\ndefense=" + defense +
                "\ndexterity=" + dexterity +
                "\nagility=" + agility +
                "\nskill=" + skill +
                "\nbravery=" + bravery +
                "\n\ncurrentMoral=" + currentMoral +
                "\ncurrentHitPoints=" + currentHitPoints +
                "\n\n\nitem1=" + item1 +
                "\nitem2=" + item2 +
                "\nitemStealable=" + itemStealable +
                "\npassiveAbility=" + passiveAbility +
                "\nsupportAbility=" + supportAbility +
                "\noffensiveAbility=" + offensiveAbility +
                "\nbanner=" + banner +
                "\n\n\nguarding=" + guarding +
                "\norientation=" + orientation +
                "\nbehaviour=" + behaviour +
                "\nnumberOfOAUses=" + numberOfOAUses +
                "\nOAChargingBar=" + OAChargingBar +
                "\nmoved=" + moved +
                "\nacted=" + acted +
                "\nremainigBuildingResources=" + remainigBuildingResources +
                '}';
    }

    public String toStringStatOnly() {
        return "UNIT" +
                "\n\nlevel = " + level +
                "\nJob = " + job + '\'' +
                "\nTemplate = " + template.name() +
                "\nPrimaryWeapon = " + primaryWeapon.name() +
                "\nSecondaryWeapon = " + secondaryWeapon.name() +
                "\nHorseman = " + horseman +
                "\n\nMobility = " + mobility +
                "\nCharisma = " + charisma +
                "\nLeadership = " + leadership +
                "\nHitPoints = " + hitPoints +
                "\nStrength = " + strength +
                "\nDefense = " + defense +
                "\nDexterity = " + dexterity +
                "\nAgility = " + agility +
                "\nSkill = " + skill +
                "\nBravery = " + bravery +
                "\n\nCurrentMoral = " + currentMoral +
                "\nCurrentHitPoints = " + currentHitPoints;
    }



    //---------------------- ARMY CLASS --------------------------------



    public static class Army extends AArmy {

        /**
         *  mob troups =
         *  [ WL S S ... ]
         *  [ WC S S ... ]
         *  [ WC S S ... ]
         *  [ WC S S ... ]
         *
         */

        private static int ids = 0;

        private int id;
        private ArmyType armyType;
        private Array<Array<Unit>> mobilizedTroups;
        private Array<Unit> nonMobTroups;

        public Army(ArmyType armyType){
            this.id = ids++;
            this.armyType = armyType;
            this.mobilizedTroups = new Array<Array<Unit>>();
            this.nonMobTroups = new Array<Unit>();
        }

        public Army(Unit warlord, ArmyType aligment){
            this(aligment);
            appointWarLord(warlord);
        }

        @Override
        public int getId() {
            return id;
        }


        @Override
        public void setArmyType(ArmyType type) {
            this.armyType = type;
        }

        @Override
        public Unit getWarlord() {
            return this.mobilizedTroups.get(0).get(0);
        }

        @Override
        public Unit getWarchief(Unit unit) {
            if(unit.isMobilized()){
                Array<Array<Unit>> squads = getAllSquads();
                for(int i = 0; i < squads.size; i++){
                    for(int j = 0; j < squads.get(i).size; j++){
                        if(unit == squads.get(i).get(j)){
                            return squads.get(i).get(0);
                        }
                    }
                }
            }
            return null;
        }

        @Override
        public Array<Unit> getWarChiefs() {
            Array<Unit> warChiefs = new Array<Unit>();
            for(int i = 0; i < mobilizedTroups.size; i++){
                if(mobilizedTroups.get(i).size > 0) {
                    warChiefs.add(mobilizedTroups.get(i).get(0));
                }
            }
            return warChiefs;
        }

        @Override
        public Array<Unit> getSquad(Unit unit) {
            for(int i = 0; i < mobilizedTroups.size; i++){
                for(int j = 0; j < mobilizedTroups.get(i).size; j++){
                    if(mobilizedTroups.get(i).get(j) == unit){
                        return  mobilizedTroups.get(i);
                    }

                }
            }
            return new Array<Unit>();
        }

        @Override
        public Array<Array<Unit>> getAllSquads() {
            return mobilizedTroups;
        }

        @Override
        public Array<Unit> getMobilizedUnits() {
            Array<Unit> res = new Array<Unit>();
            for(int i = 0; i < mobilizedTroups.size; i++){
                for(int j = 0; j < mobilizedTroups.get(i).size; j++){
                    res.add(mobilizedTroups.get(i).get(j));
                }
            }
            return res;
        }

        @Override
        public Array<Unit> getNonMobilizedUnits() {
            return nonMobTroups;
        }

        @Override
        public boolean isAlly() {
            return armyType != ArmyType.FOE;
        }

        @Override
        public boolean isPlayerControlled(){
            return this.armyType == ArmyType.PLAYER;
        }

        public int getNbOfSquads(){
            return mobilizedTroups.size;
        }

        @Override
        public void appointWarLord(Unit warlord) {
            if(warlord != null) {
                add(warlord);
                disbandAllSquads();
                mobilizedTroups.add(new Array<Unit>());
                mobilizedTroups.get(0).add(warlord);
                nonMobTroups.removeValue(warlord, true);
                warlord.setInitialHPAndMoral();
                warlord.notifyAllObservers(null);
            }
        }

        /**
         *  PREMICE:
         *  1) check if unit is not NULL
         *  2) add the unit into the army if not yet done.
         *
         * IF :
         * 1) the squadIndex != 0 ELSE appointWarlord()
         * 2) there is a warlord
         * 3) there is a war chief slot available
         * 4) unit is not a war chief
         *
         * THEN:
         * 1) remove the unit from whether his former position within the mobilized or non mobilized troops.
         * 2) create a new squad
         * 3) add the a unit to the new squad as a war chief (index = 0)
         * 4)   IF: squadId is valid
         *          disengage the old WC and his troops and insert the new squad at the relevant index
         *      ELSE:
         *          add the squad to the mobilized forces.
         *
         *
         * @param unit
         * @return
         */
        private UnitAppointmentErrorMsg appointWarChief(Unit unit, int squadIndex){
            UnitAppointmentErrorMsg msg = UnitAppointmentErrorMsg.SUCCESSFULLY_INSERTED;

            if(getWarlord() != null && unit != null) {
                add(unit);
                if (squadIndex == 0) {
                    appointWarLord(unit);
                } else {
                    if (getWarlord() != null && (getNbOfSquads() < getWarlord().getNbMaxWarChiefs() || (0 < squadIndex && squadIndex < mobilizedTroups.size))) {
                        if (!unit.isWarChief()) {

                            remove(unit);
                            unit.army = this;
                            Array<Unit> newSquad = new Array<Unit>();
                            newSquad.add(unit);
                            if (0 < squadIndex && squadIndex < mobilizedTroups.size) {

                                //set an older squad
                                if (mobilizedTroups.get(squadIndex).size > 0)
                                    disengage(mobilizedTroups.get(squadIndex).get(0));
                                mobilizedTroups.insert(squadIndex, newSquad);
                            } else {

                                //add a new squad
                                mobilizedTroups.add(newSquad);
                            }
                            unit.setInitialHPAndMoral();
                            unit.notifyAllObservers(null);

                        } else {
                            msg = UnitAppointmentErrorMsg.IS_ALREADY_A_WC;
                        }
                    } else {
                        msg = UnitAppointmentErrorMsg.NO_WC_SLOT_AVAILABLE;
                    }
                }
            }else{
                msg = UnitAppointmentErrorMsg.NULL_ARG;
            }
            return msg;
        }

        @Override
        public UnitAppointmentErrorMsg appointWarChief(Unit unit) {
            return appointWarChief(unit, -1);
        }

        /**
         *  PREMICE:
         *  1) check if unit is not null AND if the army has a warlord
         *  2) add the unit into the army if not yet done.
         *
         * if :
         * 1) squadId is valid (the squad exists)
         * 2) the squad is not empty
         * 3) there is a soldier slot available
         * 4) if the soldier already belongs to this unit
         * 5) unit is not a standard bearer or the squad _has no standard bearer
         *
         * then:
         * 3) remove the unit from whether his former position within the mobilized or non mobilized troops.
         * 4) add the unit to his new squad
         *
         * @param unit
         * @return
         */
        public UnitAppointmentErrorMsg appointSoldier(Unit unit, int squadIndex, int soldierIndex) {
            UnitAppointmentErrorMsg msg = UnitAppointmentErrorMsg.SUCCESSFULLY_INSERTED;
            if(getWarlord() != null && unit != null) {
                add(unit);
                if (soldierIndex == 0) {
                    if (squadIndex == 0) {
                        appointWarLord(unit);
                    } else {
                        msg = appointWarChief(unit, squadIndex);
                    }
                } else {

                    // the references provided are those of the mere soldier as expected
                    if (0 <= squadIndex && squadIndex < getNbOfSquads()) {
                        if (0 < mobilizedTroups.get(squadIndex).size && mobilizedTroups.get(squadIndex).size < mobilizedTroups.get(squadIndex).get(0).getNbMaxUnits()) {
                            if (!mobilizedTroups.get(squadIndex).contains(unit, true)) {
                                if (!unit.isStandardBearer() || !hasSquadStandardBearer(squadIndex)) {

                                    //all conditions required validated
                                    remove(unit);
                                    unit.army = this;
                                    if (0 < soldierIndex && soldierIndex < mobilizedTroups.get(squadIndex).size) {
                                        disengage(mobilizedTroups.get(squadIndex).get(soldierIndex));
                                        mobilizedTroups.get(squadIndex).insert(soldierIndex, unit);
                                    } else {
                                        mobilizedTroups.get(squadIndex).add(unit);
                                    }
                                    unit.setInitialHPAndMoral();
                                    unit.notifyAllObservers(null);

                                } else {
                                    msg = UnitAppointmentErrorMsg.HAS_ALREADY_STANDARD_BEARER;
                                }
                            } else {
                                msg = UnitAppointmentErrorMsg.ALREADY_PART_OF_THIS_SQUAD;
                            }
                        } else {
                            msg = UnitAppointmentErrorMsg.NO_SLOT_AVAILABLE;
                        }
                    } else {
                        msg = UnitAppointmentErrorMsg.SELECTED_SQUAD_DOES_NOT_EXIST;
                    }
                }
            }else{
                msg = UnitAppointmentErrorMsg.NULL_ARG;
            }
            return msg;
        }

        @Override
        public UnitAppointmentErrorMsg appointSoldier(Unit unit, int squadIndex) {
            return appointSoldier(unit, squadIndex, -1);
        }

        private boolean hasSquadStandardBearer(int squadId) {
            if(0 < squadId && squadId < getNbOfSquads()) {
                for (int i = 0; i < mobilizedTroups.get(squadId).size; i++){
                    if(mobilizedTroups.get(squadId).get(i).isStandardBearer()){
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * disengagement consist in removing a unit form the mobilized army
         * 1) FIRST we check that the given unit is:
         *  - not null
         *  - mobilized
         *  - not a warlord
         *
         * 2) Then the method fetch the squad's unit and the unit indexes
         *
         * 3) then IF those indexes have been founded, 2 cases could rise:
         *      IF the unit is a war chief THEN the whole squad is demobilized
         *      ELSE the unit is demobilized
         *
         *
         * @param unit
         * @return
         */
        @Override
        public boolean disengage(Unit unit) {
            if(unit != null && isUnitMobilized(unit) && !unit.isWarlord()) {

                int squadId = -1;
                int unitId = -1;
                for (int i = 0; i < mobilizedTroups.size; i++) {
                    for (int j = 0; j < mobilizedTroups.get(i).size; j++) {
                        if (unit == mobilizedTroups.get(i).get(j)) {
                            squadId = i;
                            unitId = j;
                        }
                    }
                }

                if (squadId != -1 && unitId != -1) {
                    if (unit.isWarChief()) {
                        Array<Unit> squad = getSquad(unit);
                        mobilizedTroups.removeIndex(squadId);
                        nonMobTroups.addAll(squad);
                    } else {
                        mobilizedTroups.get(squadId).removeValue(unit, true);
                        nonMobTroups.add(unit);

                    }
                }
                unit.notifyAllObservers(null);
                return true;
            }
            return false;
        }

        /**
         * Re-integrate all mobilized troops in the on-mobilized array and clear the former.
         */
        @Override
        public void disbandAllSquads() {
            Unit unit;
            for(int i = 0 ; i <  mobilizedTroups.size; i++){
                for(int j = 0; j <  mobilizedTroups.get(i).size; j++){
                    unit = mobilizedTroups.get(i).removeIndex(j);
                    unit.setInitialHPAndMoral();
                    nonMobTroups.add(unit);
                    unit.notifyAllObservers(null);
                    j--;
                }
            }
        }

        @Override
        public boolean isUnitMobilized(Unit unit){
            for(int i = 0; i < getNbOfSquads(); i++){
                if(mobilizedTroups.get(i).contains(unit, true)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean contains(Unit unit){
            return nonMobTroups.contains(unit, true) || isUnitMobilized(unit);
        }

        /**
         *  if not yet contain in the army
         *      0) remove the unit for its former army if relevant.
         *      1) add unit to non-mobilized troops
         *      2) set army attribute of the unit
         *
         * @param unit
         * @return
         */
        @Override
        public boolean add(Unit unit) {
            boolean successfullytAdded = false;
            if( unit != null && !contains(unit) ){
                if (unit.army != null) {
                    AArmy army = unit.army;
                    army.remove(unit);
                }
                unit.army = this;
                nonMobTroups.add(unit);
                unit.notifyAllObservers(null);
                successfullytAdded = true;
            }
            return successfullytAdded ;
        }

        @Override
        public void remove(Unit unit) {
            if(unit != null && contains(unit)) {
                disengage(unit);
                unit.army = null;
                nonMobTroups.removeValue(unit, true);
            }
        }

        /**
         * update composition if one of members sees changes in critical stat, notably leadership
         */
        @Override
        public void checkComposition() {
            Array<Array<Unit>> squads = getAllSquads();
            Array<Unit> squad;
            
            //check warlord capacity
            if(squads.size > getWarlord().getNbMaxWarChiefs())
                disbandAllSquads();

            int nbOfStandardBearer = 0;
            for(int i = 0; i < squads.size; i++){
                squad = squads.get(i);

                if(squad.size == 0){
                    // the squad is empty => remove it
                    mobilizedTroups.removeIndex(i);
                }else{

                    // check banners & leadership requirements
                    for(int j = 0; j < squad.size; j++){
                        if(squad.get(j).isStandardBearer())
                           nbOfStandardBearer++;
                    }

                    if(nbOfStandardBearer > 1 || squad.get(0).getNbMaxUnits() < squad.size){
                        if(i == 0)
                            disbandAllSquads();
                        else
                            disengage(squad.get(0));
                    }
                }

                nbOfStandardBearer = 0;
            }
        }

        @Override
        public Banner getSquadBanner(Unit unit){
            if(unit != null) {
                Array<Unit> squad = getSquad(unit);
                for (Unit squadMember : squad) {
                    if (squadMember.isStandardBearer()) {
                        return squadMember.banner;
                    }
                }
            }
            return null;
        }

        @Override
        public int getBannerRange(){
            return (int) Math.sqrt(getWarlord().getAppLaedership());
        }



        @Override
        public String toString(){
            String str = "\n|CURRENT ARMY";
            for(int i = 0 ; i <  mobilizedTroups.size; i++){
                for(int j = 0; j <  mobilizedTroups.get(i).size; j++){
                    if(j == 0){
                        if(i == 0){
                            str += "\n|> ";
                        }else{
                            str += "\n|--> ";
                        }
                    }else{
                        str += "\n|----> ";
                    }
                    str += mobilizedTroups.get(i).get(j).getName();
                }
            }
            str += "\n|\n|RESERVE ARMY";
            for(int i = 0; i <  nonMobTroups.size; i++){
                str += "\n|  "+nonMobTroups.get(i).getName();
            }
            return str+"\n";
        }
    }

}
