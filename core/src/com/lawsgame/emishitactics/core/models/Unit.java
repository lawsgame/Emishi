package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Props;
import com.lawsgame.emishitactics.core.constants.Props.BannerSign;
import com.lawsgame.emishitactics.core.constants.Props.Behaviour;
import com.lawsgame.emishitactics.core.constants.Props.DefensiveStance;
import com.lawsgame.emishitactics.core.constants.Props.EquipMsg;
import com.lawsgame.emishitactics.core.constants.Props.Ethnicity;
import com.lawsgame.emishitactics.core.constants.Props.Item;
import com.lawsgame.emishitactics.core.constants.Props.ItemType;
import com.lawsgame.emishitactics.core.constants.Props.OffensiveAbility;
import com.lawsgame.emishitactics.core.constants.Props.Orientation;
import com.lawsgame.emishitactics.core.constants.Props.PassiveAbility;
import com.lawsgame.emishitactics.core.constants.Props.TileType;
import com.lawsgame.emishitactics.core.constants.Props.UnitAppointmentErrorMsg;
import com.lawsgame.emishitactics.core.constants.Props.UnitTemplate;
import com.lawsgame.emishitactics.core.constants.Props.Weapon;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;



/**
 *  TODO:
 *  - Abilities?
 */

public class Unit extends Observable{


    protected String name;
    protected Ethnicity ethnicity;

    protected int level;
    protected int leadershipEXP = 0;
    protected String job;
    protected boolean rightHanded;
    protected UnitTemplate template;
    protected Weapon primaryWeapon = Weapon.NONE;
    protected Weapon secondaryWeapon = Weapon.NONE;
    protected boolean primaryWeaponEquipped = true;
    protected boolean horseman;

    private AbstractArmy army = null;

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

    protected PassiveAbility pasAb1 = PassiveAbility.NONE;
    protected PassiveAbility pasAb2 = PassiveAbility.NONE;
    protected OffensiveAbility offAb = OffensiveAbility.NONE;
    protected int numberOfOAUses = 0;
    protected final Banner banner = new Banner();

    /**
     * battle execution related attributes
     */
    protected Props.Orientation orientation;
    protected Props.Behaviour behaviour;
    protected boolean moved = false;
    protected boolean acted = false;
    protected boolean builded = false;
    protected DefensiveStance stance = DefensiveStance.DODGE;




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
     *  - setBanner if relevant
     */
    public Unit(boolean rightHanded, UnitTemplate template, int gainLvl, Ethnicity ethnicity, Orientation or, Behaviour behaviour,  Weapon primaryWeapon, Weapon secondaryWeapon, boolean homogeneousLevelUp){
        this.level = template.getStartLevel();
        this.rightHanded = rightHanded;
        this.template = (template == null) ? UnitTemplate.CONSCRIPT: template;
        this.job = (Props.PROMOTION_LEVEL <= gainLvl + template.getStartLevel())? template.getJob().getPromotionName() : template.getJob().getRecruitName();
        this.name = "";
        this.ethnicity = (ethnicity == null)? Ethnicity.JAPANESE :ethnicity;
        this.behaviour = (behaviour == null) ? Behaviour.PASSIVE : behaviour;
        this.orientation = (or == null) ? Orientation.SOUTH :or;
        this.horseman = false;

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
        this.currentHitPoints = hitPoints;
        this.currentMoral = calculateInitialMoral();

        this.primaryWeapon = (isWeaponAvailable(primaryWeapon, true)) ? primaryWeapon : pickWeapon(true);
        Weapon chosenSecondaryW = (isWeaponAvailable(secondaryWeapon, true))? secondaryWeapon : pickWeapon(false);

        if(template.getStartLevel() >= Props.PROMOTION_LEVEL)
            _promote(chosenSecondaryW);

        // levels up the build
        if(homogeneousLevelUp){
            _growUp(gainLvl, chosenSecondaryW);
        }else {
            for (int lvl = 0; lvl < gainLvl; lvl++) {
                levelUp(chosenSecondaryW);
            }
        }
    }

    /**
     * choose randomly weapons for the unit.
     *
     * @param template
     * @param gainLvl
     */
    public Unit(UnitTemplate template, int gainLvl){
        this(true, template,  gainLvl, Ethnicity.getStandard(), Orientation.getStandard(), Behaviour.getStandard(), Weapon.NONE, Weapon.NONE, true);
    }

    public Unit(boolean rightHanded, UnitTemplate template, int gainLvl, Weapon primaryWeapon, Weapon secondaryWeapon, boolean homogeneous){
        this(rightHanded, template,  gainLvl, Ethnicity.getStandard(), Orientation.getStandard(), Behaviour.getStandard(), primaryWeapon, secondaryWeapon, homogeneous);
    }



    // ----------------------------   METHODS --------------------------------






    public int getLdExp(boolean[] secondaryObjectiveDone, int[] bonusSecondaryObjective, int nbOfTurnsToCompletion, int nbOfTurnMin, int nbOfTurnMax) {
        float exp = 0f;
        if(isWarChief()) {
            int nbTurns;
            if (nbOfTurnsToCompletion <= nbOfTurnMin) {
                nbTurns = nbOfTurnMin;
            } else if (nbOfTurnMax <= nbOfTurnsToCompletion) {
                nbTurns = nbOfTurnMax;
            } else {
                nbTurns = nbOfTurnsToCompletion;
            }

            //calculate the base exp gained
            exp = 100f * getAppGrowthRate(Props.Stat.LEADERSHIP) * ((float)(nbOfTurnMax - nbTurns)) / ((float)(nbOfTurnMax - nbOfTurnMin));

            //factorize the size of the squad in the equation
            exp *= 1f + (float)(getSquad().size - 1) / ((isWarlord())?Props.MAX_UNITS_UNDER_WARLORD - 1: Props.MAX_UNITS_UNDER_WAR_CHIEF - 1) ;

            //add secondary objective completion bonus
            for (int i = 0; i < secondaryObjectiveDone.length; i++) {
                if (i < bonusSecondaryObjective.length && secondaryObjectiveDone[i]) {
                    exp += bonusSecondaryObjective[i];
                }
            }
        }
        return (int)exp;
    }


    public void inscreaseLeadership(){
        this.leadership++;
    }


    public boolean isPromoted(){
        return level > Props.PROMOTION_LEVEL;
    }

    protected int _promote(Weapon secondaryWeapon){
        int mob = 0;
        this.job = template.getJob().getPromotionName();
        mob += Props.MOBILITY_BONUS_PROMOTED;
        if (!this.primaryWeapon.isFootmanOnly() && !secondaryWeapon.isFootmanOnly()) {
            this.horseman = true;
            mob += Props.MOBILITY_BONUS_HORSEMAN;
        }
        this.secondaryWeapon = secondaryWeapon;
        this.mobility += mob;
        return mob;
    }

     private void _growUp(int gainLvl, Weapon secondaryWeapon){
        if(gainLvl < 0) gainLvl = 0;
        if(gainLvl + getLevel() > Props.MAX_LEVEL)  gainLvl = Props.MAX_LEVEL - getLevel();

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
        if(getLevel() + gainLvl >= Props.PROMOTION_LEVEL){
            _promote(secondaryWeapon);
            if(getLevel() >= Props.PROMOTION_LEVEL){
                postPromotionLvl = gainLvl;
            }else{
                prePromotionLvl = Props.PROMOTION_LEVEL - getLevel() - 1;
                postPromotionLvl = getLevel() + gainLvl + 1 - Props.PROMOTION_LEVEL;
            }
        }else{
            prePromotionLvl = gainLvl;
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
        this.leadership += ld;
        this.hitPoints += hpt;
        this.strength += str;
        this.defense += def;
        this.dexterity += dex;
        this.agility += agi;
        this.skill += ski;
        this.bravery += bra;

        level += gainLvl;

    }

    public int[] levelUp(Weapon secondaryWeapon){
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

        if(this.level < Props.MAX_LEVEL) {
            this.level++;

            if (Props.PROMOTION_LEVEL == level) {
                mob += _promote(secondaryWeapon);
                cha += template.getProBoCha();
                ld += template.getProBoLd();
                hpt += template.getProBoHP();
                str += template.getProBoStr();
                def += template.getProBoDef();
                dex += template.getProBoDex();
                agi += template.getProBoAg();
                ski += template.getProBoSk();
                bra += template.getProBoBr();
            } else {
                cha += (getAppGrowthRate(Props.Stat.CHARISMA) * 100 > Props.R.getR().nextInt(100)) ? 1 : 0;
                ld += (getAppGrowthRate(Props.Stat.LEADERSHIP) * 100 > Props.R.getR().nextInt(100)) ? 1 : 0;
                hpt += (getAppGrowthRate(Props.Stat.HIT_POINTS) * 100 > Props.R.getR().nextInt(100)) ? 1 : 0;
                str += (getAppGrowthRate(Props.Stat.STRENGTH) * 100 > Props.R.getR().nextInt(100)) ? 1 : 0;
                def += (getAppGrowthRate(Props.Stat.DEFENSE) * 100 > Props.R.getR().nextInt(100)) ? 1 : 0;
                dex += (getAppGrowthRate(Props.Stat.DEXTERITY) * 100 > Props.R.getR().nextInt(100)) ? 1 : 0;
                agi += (getAppGrowthRate(Props.Stat.AGILITY) * 100 > Props.R.getR().nextInt(100)) ? 1 : 0;
                ski += (getAppGrowthRate(Props.Stat.SKILL) * 100 > Props.R.getR().nextInt(100)) ? 1 : 0;
                bra += (getAppGrowthRate(Props.Stat.BRAVERY) * 100 > Props.R.getR().nextInt(100)) ? 1 : 0;
            }

            this.charisma += cha;
            this.leadership += ld;
            this.hitPoints += hpt;
            this.strength += str;
            this.defense += def;
            this.dexterity += dex;
            this.agility += agi;
            this.skill += ski;
            this.bravery += bra;
        }

        int[] gainlvl = new int[]{hpt, mob, cha, ld, str, def, dex, agi, ski, bra};
        notifyAllObservers(gainlvl);
        return gainlvl;
    }




    //------------------- WEAPON RELATED METHODS -----------------------------------




    public Weapon pickWeapon(boolean primaryWeaponChoice){
        int randId;
        Weapon chosenW = Weapon.NONE;
        if(primaryWeaponChoice) {
            if (this.template.getJob().getAvailableWeapons().length > 0) {
                randId = Props.R.getR().nextInt(this.template.getJob().getAvailableWeapons().length);
                chosenW = this.template.getJob().getAvailableWeapons()[randId];
            }
        }else{
            chosenW = this.primaryWeapon;
            if(this.template.getJob().getAvailableWeaponsAfterPromotion().length > 1) {
                while (chosenW == this.primaryWeapon) {
                    randId = Props.R.getR().nextInt(this.template.getJob().getAvailableWeaponsAfterPromotion().length);
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
        this.primaryWeaponEquipped = !primaryWeaponEquipped;
        notifyAllObservers(getCurrentWeapon());
    }

    public void setCurrentWeapon(boolean primaryWeaponEquiped) {
        this.primaryWeaponEquipped = primaryWeaponEquiped;
        notifyAllObservers(null);
    }

    public Weapon getCurrentWeapon() {
        return (primaryWeaponEquipped)? primaryWeapon: secondaryWeapon;
    }



    //-------------- ARMY RELATED METHODS --------------




    public boolean sameSquadAs(Unit unit){
        boolean res = false;
        boolean thisBelongSquad = false;
        boolean unitBelongSquad = false;
        if(army != null && army == unit.army){
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

    public boolean sameAligmentAs(Unit unit){
        return unit != null && army != null && ((army.isAlly() && unit.army.isAlly()) ||(!army.isAlly() && !unit.army.isAlly())) ;
    }

    public boolean isEnemyWith(Unit unit){
        return unit != null && army != null && ((army.isAlly() && !unit.army.isAlly()) ||(!army.isAlly() && unit.army.isAlly())) ;
    }

    public boolean sameArmyAs(Unit unit){
        return unit != null && army != null && army.getId() == unit.army.getId();
    }

    public Array<Unit> getSquad(){
        return army.getSquad(this);
    }




    // --------------------- BATTLE RESOLUTION USEFULL METHODS ------------------------------
    /*
     - attack damage
     - attack accuracy
     - avoidance
     - trigger ability rate
     - drop / steal chance of sucess
     */




    public int getAttackDamage(){
        return getCurrentWeapon().getDamage() + getAppStat(Props.Stat.STRENGTH);
    }

    public int getCurrentAttackDamage(TileType type, Unit foe, boolean crit, boolean bannerAtRange){
        int str = getCurrentWeapon().getDamage() + getCurrentStrength(type,foe, bannerAtRange);
        if(crit) str *= getCurrentCritDamageModifier(foe);
        return str;
    }

    public int getAvoidance(){
        int avoidance = 0;
        switch(stance){
            case DODGE:
                avoidance = Props.AGI_DODGE_FACTOR* getAppStat(Props.Stat.AGILITY);
                break;
            case PARRY:
                // multiple the parry capacity of the current wielded weapon with the average parry vulnerability of the weapon roster
                float parryAverageAbility = (float) Math.sqrt(getCurrentWeapon().getParryCapacity() * Weapon.getHighAverageParryVulnerabilty());
                avoidance = (int) (Props.DEX_PARRY_FACTOR* getAppStat(Props.Stat.DEXTERITY)*parryAverageAbility);
                break;
            default: break;
        }
        return avoidance;
    }

    public int getCurrentAvoidance(TileType tileType, Unit attacker, boolean bannerAtRange){
        int avoidance = 0;
        switch(stance){
            case DODGE:
                avoidance = Props.AGI_DODGE_FACTOR* getCurrentAg(attacker);
                break;
            case PARRY:
                float parryAbility = (float) Math.sqrt(getCurrentWeapon().getParryCapacity() * attacker.getCurrentWeapon().getParryVulnerability());
                avoidance = (int) ( Props.DEX_PARRY_FACTOR * getCurrentDex(tileType, bannerAtRange) * parryAbility);
                break;
            default: break;
        }
        return avoidance + tileType.getAvoidBonus();
    }

    public int getAttackAccuracy(){
        return getCurrentWeapon().getAccuracy() + getChaChiefsBonus() + Props.DEX_HIT_FACTOR* getAppStat(Props.Stat.DEXTERITY);
    }

    public int getCurrentAttackAccuracy(TileType tileType, boolean crit, boolean bannerAtRange){
        int acc = getCurrentWeapon().getAccuracy() + getChaChiefsBonus() + Props.DEX_HIT_FACTOR* getCurrentDex(tileType, bannerAtRange);
        if(crit) acc += Props.ACCURACY_BONUS_CRIT;
        return acc;
    }

    public int getAbilityTriggerRate(Banner bannerAtRange){
        return Props.AB_TRIGGER_RATE_SKILL_FACTOR * getCurrentSk() + getChaChiefsBonus() + bannerAtRange.getBonusRelativeTo(BannerSign.IZANAGI);
    }

    public int getStealRate(Unit target, TileType tileType, boolean bannerAtRange){
        int rate = 100 + 10*(target.getCurrentAg(this) - getCurrentDex(tileType, bannerAtRange));
        if(rate > 100) rate = 100;
        if(rate < 0) rate = 0;
        return rate;
    }

    public int getDropRate(TileType tileType, boolean bannerAtRange){
        int rate = Props.DEX_FAC_DROP_RATE * getCurrentDex(tileType, bannerAtRange) + getChaChiefsBonus();
        return rate;
    }

    public int getExperienceFrom(int levelKilled){
        double exp = Props.EXP_BASE_MODIFIER * Math.atan((levelKilled - getLevel())*Props.LVL_GAP_FACTOR) - getLevel();
        return (exp > 1) ? (int)exp : 1;
    }

    public void receiveDamage(int damageTaken){
        if(this.currentHitPoints < damageTaken){
            this.currentHitPoints -= damageTaken;
            if(this.getCurrentMoral() < damageTaken){
                this.currentMoral -= damageTaken;
                notifyAllObservers(damageTaken);
            }else{
                this.currentMoral = 0;
                notifyAllObservers(false);
            }
        }else{
            this.currentHitPoints = 0;
            this.currentMoral = 0;
            notifyAllObservers(true);
        }
    }

    public void heal(int rawHealPower){
        int[] oldHpts = new int[]{currentHitPoints, currentMoral};
        if(rawHealPower > hitPoints){
            currentHitPoints  = hitPoints;
            currentMoral = calculateInitialMoral();
        }else{
            currentHitPoints += rawHealPower;
            if(currentMoral > 0) {
                currentMoral += rawHealPower;
            }else{
                currentMoral = calculateInitialMoral() + currentHitPoints - getAppStat(Props.Stat.HIT_POINTS);
                if(currentMoral < 0){
                    currentMoral = 0;
                }
            }
        }
        notifyAllObservers(oldHpts);
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


    public Props.EquipMsg equip(Item item, boolean firstSlot) {
        int currentLd = getAppStat(Props.Stat.LEADERSHIP);
        Props.EquipMsg msg = Props.EquipMsg.FAILED_TO_EQUIP;

        if(item.getItemType() != ItemType.SHIELD || ( !template.getJob().isAllowedWieldShield()  && !template.getJob().isStandardBearer())) {

            // check if the unit does not hold yet the item of the same type
            if (firstSlot) {
                if (item2.getItemType() != item.getItemType()) {
                    this.item1 = item;
                    msg = Props.EquipMsg.SUCCESSFULLY_EQUIPED;
                }else{
                    msg = Props.EquipMsg.TYPE_ALREADY_EQUIPED;
                }
            } else {
                if (item1.getItemType() != item.getItemType()) {
                    this.item2 = item;
                    msg = Props.EquipMsg.SUCCESSFULLY_EQUIPED;
                }else{
                    msg = Props.EquipMsg.TYPE_ALREADY_EQUIPED;
                }
            }

            // update army composition
            if (getAppStat(Props.Stat.LEADERSHIP) < currentLd && isWarChief()) {
                if (isWarlord()) {
                    army.appointWarLord(this);
                    msg = EquipMsg.SUCCESSFULLY_EQUIPED_AND_ARMY_RECOMPOSED_WL;
                } else {
                    army.disengage(this);
                    army.appointWarChief(this);
                    msg = EquipMsg.SUCCESSFULLY_EQUIPED_AND_ARMY_RECOMPOSED_WC;
                }
            }

        }else{
            if(template.getJob().isAllowedWieldShield()){
                msg = EquipMsg.JOB_DOES_NOT_ALLOW_SHIELD_BEARING;
            }
            if (template.getJob().isStandardBearer()){
                msg = EquipMsg.STANDARD_BEARER_CANNOT_EQUIP_SHIELD;
            }
        }

        notifyAllObservers(null);
        return msg;
    }


    public void unequip(boolean firstSlot){
        equip(Item.NONE, firstSlot);
    }


    public boolean isUsing(Item eq){
        boolean used = item1 == eq || item2 == eq;
        if(eq.getItemType() == ItemType.SHIELD){
            used = used && !getCurrentWeapon().isDualWieldingRequired();
        }
        return used;
    }


    public boolean isUsingShield(){
        for(Item item: Item.values()){
            if(isUsing(item) && item.getItemType() == ItemType.SHIELD){
                return  true;
            }
        }
        return  false;
    }

    public boolean possessItemStealable() {
        return (item1 != Item.NONE) && itemStealable;
    }

    public void setItemStealable(boolean itemStealable) {
        this.itemStealable = itemStealable;
        notifyAllObservers(null);
    }

    public boolean has(PassiveAbility abb) {
        return abb == pasAb1 || abb == pasAb2;
    }

    public boolean has(OffensiveAbility abb) {
        return abb == offAb;
    }


    public Banner getBanner() {
        return banner;
    }

    public boolean isDead() {
        return currentHitPoints == 0;
    }

    public boolean isWithdrawn() {
        return  currentHitPoints != 0 && currentMoral == 0;
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
            nbMax = (int) (3 + (getAppStat(Props.Stat.LEADERSHIP) + 2)/5.0f);
            if(nbMax > Props.MAX_UNITS_UNDER_WARLORD ) nbMax = (int) Props.MAX_UNITS_UNDER_WARLORD;
        }else if(isWarChief()){
            nbMax = (int) (2 + (getAppStat(Props.Stat.LEADERSHIP) + 1)/5.0f);
            if(nbMax > Props.MAX_UNITS_UNDER_WAR_CHIEF ) nbMax = (int) Props.MAX_UNITS_UNDER_WAR_CHIEF;
        }
        return nbMax;
    }

    public int getNbMaxWarChiefs(){
        return 1 + (int) ((isWarlord())? getAppStat(Props.Stat.LEADERSHIP)/6.0f: 0);
    }

    public int getChaChiefsBonus(){
        int bonus = 0;
        if(army != null){
            Array<Array<Unit>> squads = army.getAllSquads();
            for(int i=0; i< squads.size; i++){
                for(int j=0; j < squads.get(i).size; j++){
                    if(squads.get(i).get(j) == this){
                        bonus += squads.get(0).get(0).getCurrentCha();
                        bonus += squads.get(i).get(0).getCurrentCha();
                    }
                }
            }
        }
        return bonus;
    }

    public void setPasAb1(PassiveAbility pasAb1) {
        this.pasAb1 = pasAb1;
        notifyAllObservers(null);
    }

    public void setPasAb2(PassiveAbility pasAb2) {
        this.pasAb2 = pasAb2;
        notifyAllObservers(null);
    }

    public void addPassiveAbility(PassiveAbility ability){
        if(pasAb1 == PassiveAbility.NONE){
            this.pasAb1 = ability;
        }else if(pasAb2 == PassiveAbility.NONE){
            this.pasAb2 = ability;
        }
        notifyAllObservers(null);
    }

    public void setOffensiveAbility(OffensiveAbility offAb) {
        this.offAb = offAb;
        notifyAllObservers(null);
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public String getJob() {
        return job;
    }

    public boolean isRightHanded() {
        return rightHanded;
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


    /*

    the stats go in 3 different flavors:
     - BASE : no modified by any factor
     - APPARENT : value of the stat enhance with battle-permanent bonus such as those given by items.
     - CURRENT : value of the stat at one specific moment of the battle

     */

    public void setLeadership(int leadership) {
        this.leadership = leadership;
        notifyAllObservers(null);
    }

    public void resetHPAndMoral(){
        currentHitPoints = hitPoints;
        currentMoral = calculateInitialMoral();
        notifyAllObservers(null);
    }


    public int calculateInitialMoral(){
        int moralFactor = (int) (0.9 - 0.8 * Math.exp(-(getAppStat(Props.Stat.BRAVERY) + getChaChiefsBonus())/13.0));
        int moral = moralFactor * this.hitPoints;
        return (moral > 0)?  moral: 1;
    }

    public int getBaseStat(Props.Stat stat){
        int statValue = 0;
        switch(stat){
            case CHARISMA: statValue = charisma; break;
            case LEADERSHIP: statValue = leadership; break;
            case MOBILITY: statValue = mobility; break;
            case HIT_POINTS: statValue = hitPoints; break;
            case STRENGTH:  statValue = strength; break;
            case DEFENSE:  statValue = defense; break;
            case DEXTERITY: statValue = dexterity; break;
            case AGILITY:  statValue = agility; break;
            case SKILL:  statValue = skill; break;
            case BRAVERY:  statValue = bravery; break;
            case PRIMARY_WEAPON_RANGE_MIN: statValue = primaryWeapon.getRangeMin(); break;
            case PRIMARY_WEAPON_RANGE_MAX: statValue = primaryWeapon.getRangeMax(); break;
            case SECONDARY_WEAPON_RANGE_MIN: statValue = secondaryWeapon.getRangeMin(); break;
            case SECONDARY_WEAPON_RANGE_MAX: statValue = secondaryWeapon.getRangeMax(); break;
            case CURRENT_WEAPON_RANGE_MIN: statValue = getCurrentWeapon().getRangeMin(); break;
            case CURRENT_WEAPON_RANGE_MAX: statValue = getCurrentWeapon().getRangeMax(); break;
        }
        return statValue;
    }

    public int getAppStat(Props.Stat stat){
        int statValue = 0;
        switch(stat){
            case CHARISMA: statValue = charisma + (isUsing(Item.KABUTO)? Props.UNIQUE_EQUIPMENT_FIXE_STD_BONUS : 0) ; break;
            case LEADERSHIP: statValue = leadership + (isUsing(Item.WAR_CHIEF_CLOAK)? Props.UNIQUE_EQUIPMENT_FIXE_STD_BONUS : 0) ; break;
            case MOBILITY: statValue =  mobility + (isUsing(Item.WEI_BOOTS)? 1: 0); break;
            case HIT_POINTS: statValue = hitPoints; break;
            case STRENGTH:  statValue = strength + (isUsing(Item.GAUNLET)? Props.UNIQUE_EQUIPMENT_FIXE_STD_BONUS : 0); break;
            case DEFENSE:  statValue = defense + (isUsing(Item.OYOROI_ARMOR)? Props.DEF_BONUS_OYOROI : 0); break;
            case DEXTERITY: statValue = dexterity + (isUsing(Item.ARMBAND)? Props.UNIQUE_EQUIPMENT_FIXE_STD_BONUS : 0); break;
            case AGILITY:  statValue = agility; break;
            case BRAVERY:  statValue = bravery; break;
            case SKILL:  statValue = skill + (isUsing(Item.MASTER_BELT)? Props.UNIQUE_EQUIPMENT_FIXE_STD_BONUS : 0); break;
            case PRIMARY_WEAPON_RANGE_MIN: statValue = primaryWeapon.getRangeMin(); break;
            case PRIMARY_WEAPON_RANGE_MAX: statValue = primaryWeapon.getRangeMax() + ((isUsing(Item.EMISHI_RING) && primaryWeapon.isRangedW())? 1: 0);break;
            case SECONDARY_WEAPON_RANGE_MIN: statValue = secondaryWeapon.getRangeMin(); break;
            case SECONDARY_WEAPON_RANGE_MAX: statValue = secondaryWeapon.getRangeMax() + ((isUsing(Item.EMISHI_RING) && secondaryWeapon.isRangedW())? 1: 0);break;
            case CURRENT_WEAPON_RANGE_MIN: statValue = getCurrentWeapon().getRangeMin(); break;
            case CURRENT_WEAPON_RANGE_MAX: statValue = getCurrentWeapon().getRangeMax() + ((isUsing(Item.EMISHI_RING) && getCurrentWeapon().isRangedW())? 1: 0); break;
        }
        return statValue;
    }

    public int getCurrentCha() {
        return getAppStat(Props.Stat.CHARISMA);
    }

    public int getCurrentMob(TileType type){
        return getAppStat(Props.Stat.MOBILITY);
    }

    public int getCurrentHitpoints() {
        return currentHitPoints;
    }

    public int getCurrentMoral(){
        return currentMoral;
    }

    public int getCurrentStrength(TileType tileType, Unit foe, boolean bannerAtRange) {
        int str = getAppStat(Props.Stat.STRENGTH);
        if(foe.isHorseman()){
            if(getCurrentWeapon() == Weapon.YARI)
                str += Props.STR_FIXE_BONUS_YARI_1 + getCurrentSk()/ Props.STR_FIXE_BONUS_YARI_2;
            else if(getCurrentWeapon() == Weapon.NAGINATA)
                str += Props.STR_FIXE_BONUS_NAGINATA_1 + getCurrentSk()/ Props.STR_FIXE_BONUS_NAGINATE_2;
        }
        return str + tileType.getStrengthBonus() + getSquadBannerBonus(BannerSign.APEHUCI,bannerAtRange);
    }

    /*
    1) get apparent defense
    2) add the type of damage specific defense
    3)
     */
    public int getCurrentDef(TileType tileType, Unit attacker, boolean bannerAtRange) {
        int def = getAppStat(Props.Stat.DEFENSE);

        int damageSpecificDefense = 0;
        switch(attacker.getCurrentWeapon().getType()){
            case PIERCING:
                if(isUsing(Item.YAYOI_SHIELD))
                    damageSpecificDefense += Props.DEF_BONUS_YAYOI_SHIELD;
                else if(isUsing(Item.GREAT_SHIELD))
                    damageSpecificDefense += Props.DEF_BONUS_GREAT_SHIELD;
                break;
            case EDGED:
                if(isUsing(Item.TANKO_ARMOR))
                    damageSpecificDefense += Props.DEF_BONUS_TANKO;
                else if(isUsing(Item.KEIKO_ARMOR))
                    damageSpecificDefense += Props.DEF_BONUS_KEIKO;
                break;
            default: break;
        }

        float defFactor = 0;
        if(attacker.getCurrentWeapon() == Weapon.KANABO){
            defFactor += 0.7f - 0.05f*attacker.getCurrentSk();
        }else if (attacker.getCurrentWeapon() == Weapon.NODACHI){
            defFactor += 0.8f - 0.03f*attacker.getCurrentSk();
        }

        return (int)(def*defFactor) + damageSpecificDefense + tileType.getDefenseBonus() + getSquadBannerBonus(BannerSign.HACHIMAN,bannerAtRange) ;
    }

    public int getCurrentAg(Unit attacker) {
        int agi = getAppStat(Props.Stat.AGILITY);
        switch(attacker.getCurrentWeapon().getType()){
            case PIERCING:
                if(isUsing(Item.YAYOI_SHIELD))
                    agi += Props.DEX_BONUS_YAYOI_SHIELD;
                break;
            case BLUNT:
                if(isUsing(Item.TANKO_ARMOR))
                    agi += Props.DEX_BONUS_TANKO;
                break;
            case EDGED:
                if(isUsing(Item.EMISHI_LEGGINGS))
                    agi += Props.DEX_BONUS_EMISHI_LEGGINS;
                if(isUsing(Item.YAMATO_TROUSERS))
                    agi += Props.DEX_BONUS_YAMATO_TROUSERS;
                break;
            default: break;
        }
        return agi;
    }

    public int getCurrentDex(TileType tileType, boolean bannerAtRange) {
        return getAppStat(Props.Stat.DEXTERITY) + getSquadBannerBonus(BannerSign.SHIRAMBA, bannerAtRange);
    }

    public int getCurrentSk() {
        return getAppStat(Props.Stat.SKILL);
    }

    public int getCurrentRangeMin(){ return (primaryWeaponEquipped)? getAppStat(Props.Stat.PRIMARY_WEAPON_RANGE_MIN): getAppStat(Props.Stat.SECONDARY_WEAPON_RANGE_MIN); }

    public int getCurrentRangeMax(TileType tile, boolean bannerAtRange){
        int rangeMax =  (primaryWeaponEquipped)             ? getAppStat(Props.Stat.PRIMARY_WEAPON_RANGE_MAX)                                               : getAppStat(Props.Stat.SECONDARY_WEAPON_RANGE_MAX);
        rangeMax +=     (getCurrentWeapon().isRangedW())    ? (tile.enhanceRange() ? 1 : 0) + getSquadBannerBonus(BannerSign.AMATERASU, bannerAtRange)     : 0;
        return rangeMax;
    }

    public float getBaseGrowthRate(Props.Stat stat){
        float gr = 0.0f;
        switch (stat){
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

    public float getAppGrowthRate(Props.Stat stat){
        float gr = 0.0f;
        switch (stat){
            case CHARISMA: gr = getBaseGrowthRate(Props.Stat.CHARISMA) + (isUsing(Item.KABUTO)? Props.UNIQUE_EQUIPMENT_HIGH_GROWTH_BONUS :0); break;
            case LEADERSHIP: gr = getBaseGrowthRate(Props.Stat.LEADERSHIP); break;
            case MOBILITY: gr = getBaseGrowthRate(Props.Stat.MOBILITY); break;
            case HIT_POINTS: gr = getBaseGrowthRate(Props.Stat.HIT_POINTS); break;
            case STRENGTH: gr = getBaseGrowthRate(Props.Stat.STRENGTH) + (isUsing(Item.GAUNLET)? Props.UNIQUE_EQUIPMENT_LOW_GROWTH_BONUS :0); break;
            case DEFENSE: gr = getBaseGrowthRate(Props.Stat.DEFENSE); break;
            case DEXTERITY: gr = getBaseGrowthRate(Props.Stat.DEXTERITY) + (isUsing(Item.ARMBAND)? Props.UNIQUE_EQUIPMENT_HIGH_GROWTH_BONUS :0); break;
            case AGILITY: gr = getBaseGrowthRate(Props.Stat.AGILITY); break;
            case SKILL: gr = getBaseGrowthRate(Props.Stat.SKILL) + (isUsing(Item.MASTER_BELT)? Props.UNIQUE_EQUIPMENT_HIGH_GROWTH_BONUS :0); break;
            case BRAVERY: gr = getBaseGrowthRate(Props.Stat.BRAVERY); break;
        }
        return  gr;
    }

    public float getCurrentCritDamageModifier(Unit foe){
        float critDamMod = Props.CRITICAL_DAMAGE_MODIFIER;
        critDamMod += (isUsing(Item.IMPERIAL_GAUNTLET))? Props.CRIT_BONUS_IMP_GAUNLET: 0;
        critDamMod -= foe.isUsing(Item.IMPERIAL_ARMBAND)? 1f: 0;
        return  critDamMod;
    }

    public PassiveAbility getPasAb1() {
        return pasAb1;
    }

    public PassiveAbility getPasAb2() {
        return pasAb2;
    }

    public OffensiveAbility getOffAb() {
        return offAb;
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

    public AbstractArmy getArmy() {
        return army;
    }

    public boolean isMobilized(){
        if(army != null)
            return army.isUnitMobilized(this);
        return  false;
    }

    public boolean hasMoved(){ return moved; }

    public void setMoved(boolean moved){ this.moved = moved; }

    public boolean hasActed() { return acted; }

    public void setActed(boolean acted) { this.acted = acted; }

    public Props.Orientation getCurrentOrientation() {
        return orientation;
    }

    public void setOrientation(Props.Orientation orientation) {
        this.orientation = orientation;
        notifyAllObservers(null);
    }

    public Props.Ethnicity getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(Props.Ethnicity ethnicity) {
        this.ethnicity = ethnicity;
        notifyAllObservers(null);
    }

    public boolean has(Weapon weapon){
        return weapon == primaryWeapon || weapon == secondaryWeapon;
    }

    public DefensiveStance getStance() {
        return stance;
    }

    public void setStance(DefensiveStance stance) {
        this.stance = stance;
        notifyAllObservers(stance);
    }

    public int getNumberOfOAUses(){
        return  numberOfOAUses;
    }

    public void incrementOfOAUses(){
        numberOfOAUses++;
    }

    public int getLeadershipEXP() {
        return leadershipEXP;
    }

    public void setLeadershipEXP(int leadershipEXP) {
        this.leadershipEXP = leadershipEXP;
    }

    public boolean isBuilded() { return builded; }

    public void setBuilded(boolean builded) {
        this.builded = builded;
    }

    @Override
    public String toString() {
        return "UNIT\n" +
                "\nname='" + name + '\'' +
                "\nethnicity=" + ethnicity +
                "\nlevel=" + level +
                "\njob='" + job + '\'' +
                "\nrightHanded=" + rightHanded +
                "\ntemplate=" + template +
                "\nhorseman=" + horseman +
                "\narmy=" + army.getId() +
                "\n\nprimaryWeapon=" + primaryWeapon +
                "\nsecondaryWeapon=" + secondaryWeapon +
                "\nprimaryWeaponEquipped=" + primaryWeaponEquipped +
                "\n\nmobility=" + mobility +
                "\ncharisma=" + charisma +
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
                "\n\nitem1=" + item1 +
                "\nitem2=" + item2 +
                "\nitemStealable=" + itemStealable +
                "\npasAb1=" + pasAb1 +
                "\npasAb2=" + pasAb2 +
                "\noffAb=" + offAb +
                "\nbanner=" + banner +
                "\n\norientation=" + orientation +
                "\nbehaviour=" + behaviour +
                "\nstance=" + stance +
                '}';
    }

    public String toStringStatOnly() {
        return "UNIT" +
                "\n\nlevel = " + level +
                "\njob = " + job + '\'' +
                "\nrightHanded = " + rightHanded +
                "\ntemplate = " + template.name() +
                "\nprimaryWeapon = " + primaryWeapon.name() +
                "\nsecondaryWeapon = " + secondaryWeapon.name() +
                "\nhorseman = " + horseman +
                "\n\nmobility = " + mobility +
                "\ncharisma = " + charisma +
                "\nleadership = " + leadership +
                "\nhitPoints = " + hitPoints +
                "\nstrength = " + strength +
                "\ndefense = " + defense +
                "\ndexterity = " + dexterity +
                "\nagility = " + agility +
                "\nskill = " + skill +
                "\nbravery = " + bravery +
                "\n\ncurrentMoral = " + currentMoral +
                "\ncurrentHitPoints = " + currentHitPoints;
    }




    //---------------------- ARMY CLASS --------------------------------



    public static class Army extends AbstractArmy {

        /**
         *  mob troups =
         *  [ WL S S ... ]
         *  [ WC S S ... ]
         *  [ WC S S ... ]
         *  [ WC S S ... ]
         *
         */

        private static final Banner EMPTY_BANNER = new Banner();
        private static int ids = 0;

        private int id;
        private boolean aligment;
        private Array<Array<Unit>> mobilizedTroups;
        private Array<Unit> nonMobTroups;

        public Army(Unit warlord, boolean aligment){
            this(aligment);
            appointWarLord(warlord);
        }

        public Army(boolean aligment){
            this.id = ids++;
            this.aligment = aligment;
            this.mobilizedTroups = new Array<Array<Unit>>();
            this.nonMobTroups = new Array<Unit>();
        }


        @Override
        public int getId() {
            return id;
        }

        @Override
        public void setAligment(boolean aligment) {
            this.aligment = aligment;
        }

        @Override
        public Unit getWarlord() {
            return this.mobilizedTroups.get(0).get(0);
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
            return aligment;
        }

        public int getNbOfSquads(){
            return mobilizedTroups.size;
        }

        @Override
        public void appointWarLord(Unit warlord) {
            if(warlord != null) {
                add(warlord);
                resetComposition();
                mobilizedTroups.add(new Array<Unit>());
                mobilizedTroups.get(0).add(warlord);
                nonMobTroups.removeValue(warlord, true);
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
                                if (mobilizedTroups.get(squadIndex).size > 0)
                                    disengage(mobilizedTroups.get(squadIndex).get(0));
                                mobilizedTroups.insert(squadIndex, newSquad);
                            } else {
                                mobilizedTroups.add(newSquad);
                            }
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
         *  1) check if unit is not NULL
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
                                if (!unit.template.getJob().isStandardBearer() || !hasSquadStandardBearer(squadIndex)) {

                                    //all conditions required validated
                                    remove(unit);
                                    unit.army = this;
                                    if (0 < soldierIndex && soldierIndex < mobilizedTroups.get(squadIndex).size) {
                                        disengage(mobilizedTroups.get(squadIndex).get(soldierIndex));
                                        mobilizedTroups.get(squadIndex).insert(soldierIndex, unit);
                                    } else {
                                        mobilizedTroups.get(squadIndex).add(unit);
                                    }
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
                    if(mobilizedTroups.get(squadId).get(i).template.getJob().isStandardBearer()){
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public boolean disengage(Unit unit) {
            if(unit != null && contains(unit) && !unit.isWarlord()) {
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
        public void resetComposition() {
            for(int i = 0 ; i <  mobilizedTroups.size; i++){
                for(int j = 0; j <  mobilizedTroups.get(i).size; j++){
                    nonMobTroups.add(mobilizedTroups.get(i).get(j));
                    mobilizedTroups.get(i).get(j).notifyAllObservers(null);
                }
            }
            mobilizedTroups.clear();
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
                    AbstractArmy army = unit.army;
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

        @Override
        public Banner getSquadBanner(Unit unit){
            if(unit != null) {
                Array<Unit> squad = getSquad(unit);
                for (Unit squadMember : squad) {
                    if (squadMember.template.getJob().isStandardBearer()) {
                        return squadMember.banner;
                    }
                }
            }
            return EMPTY_BANNER;
        }

        @Override
        public int getBannerRange(){
            return (int) Math.sqrt(getWarlord().getAppStat(Props.Stat.LEADERSHIP));
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
