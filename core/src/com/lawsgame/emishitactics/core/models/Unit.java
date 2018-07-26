package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.constants.Data.*;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;

public class Unit extends IUnit{

    protected String name;
    protected int level;
    protected Job job;
    protected WeaponType weaponType;
    protected boolean horseman;
    protected boolean horsemanUponPromotion;
    protected boolean standardBearer;

    protected Weapon weapon1 = Weapon.FIST;
    protected Weapon weapon2 = Weapon.FIST;
    protected boolean weapon1Equipped = true;
    protected int experience = 0;
    protected int commandmentExperience = 0;
    protected boolean rightHanded = true;
    private IArmy army = null;

    protected int mobility;
    protected int charisma;
    protected int leadership;
    protected int hitPoints;
    protected int strength;
    protected int piercinfArmor;
    protected int bluntArmor;
    protected int edgedArmor;
    protected int dexterity;
    protected int agility;
    protected int skill;
    protected int bravery;

    protected int currentMoral;
    protected int currentHitPoints;

    protected Item item1 = Item.NOTHING;
    protected Item item2 = Item.NOTHING;
    protected boolean item1Stealable = false;
    protected boolean item2Stealable = false;
    protected final Banner banner = new Banner();

    /**
     * battlefield execution related attributes
     */
    protected Orientation orientation = Orientation.SOUTH;
    protected Behaviour behaviour = Behaviour.PASSIVE;
    protected int OAChargingBar = 0;
    protected boolean moved = false;
    protected boolean acted = false;


    /**
     *
     * Can be set as well:
     * - weapons
     * - battle and leadership experience
     * - right handed
     * - items (and whether each is stealable or not)
     * - abilities
     * - banner signs
     * - orientation
     * - behavious
     *
     *
     * @param name
     * @param job
     * @param level
     * @param weaponType
     * @param horseman
     * @param horsemanUponPromotion
     * @param standardBearer
     * @param homogeneousLevelsup
     */
    public Unit(
            String name,
            Job job,
            int level,
            WeaponType weaponType,
            boolean horseman,
            boolean horsemanUponPromotion,
            boolean standardBearer,
            boolean homogeneousLevelsup){
        this.name = name;
        this.job = job;
        this.level = job.getStartingLevel();
        this.weaponType = weaponType;
        this.horseman = horseman;
        this.horsemanUponPromotion = horsemanUponPromotion;
        this.standardBearer = standardBearer;

        this.mobility = (horseman) ? job.getHorsemanMob() : job.getFootmanMob();
        this.charisma = job.getBaseCha();
        this.leadership = job.getBaseLd();
        this.hitPoints = job.getBaseHP();
        this.strength = job.getBaseStr();
        this.piercinfArmor = job.getBasePiercingArmor();
        this.bluntArmor = job.getBaseBluntArmor();
        this.edgedArmor = job.getBaseEgdedArmor();
        this.dexterity = job.getBaseDex();
        this.agility = job.getBaseAg();
        this.skill = job.getBaseSk();
        this.bravery = job.getBaseBr();

        if(homogeneousLevelsup) {
            growup(level);
        }else{
            for(int lvl = job.getStartingLevel(); lvl < level; lvl++){
                levelup();
            }
        }

        this.currentHitPoints = getAppHitpoints();
        resetCurrentMoral();
    }

    public Unit(String name){
        this(name, Job.getStandard(), 10, WeaponType.SWORD, false, false, false, true);
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Data.Job getJob() {
        return job;
    }

    @Override
    public Data.WeaponType getWeaponType() {
        return weaponType;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int[] levelup() {
        int mob = 0;
        int cha = 0;
        int ld = 0;
        int hpt = 0;
        int str = 0;
        int armorE = 0;
        int armorP = 0;
        int armorB = 0;
        int dex = 0;
        int agi = 0;
        int ski = 0;
        int bra = 0;

        if(this.level < Data.MAX_LEVEL) {


            if (Data.PROMOTION_LEVEL != level) {
                float GRcha = (isPromoted()) ? job.getProGrowthCha() : job.getGrowthCha();
                float GRHP = (isPromoted()) ? job.getGetProGrowthHP() : job.getGrowthHP();
                float GRstr = (isPromoted()) ? job.getProGrowthStr() : job.getGrowthStr();
                float GRArmorP = (isPromoted()) ? job.getProGrowthPiercingArmor(): job.getGrowthPiercingArmor();
                float GRArmorB = (isPromoted()) ? job.getProGrowthBluntArmor() : job.getGrowthBluntArmor();
                float GRArmorE = (isPromoted()) ? job.getProGrowthEdgedArmor() : job.getGrowthEdgegArmor();
                float GRdex = (isPromoted()) ? job.getProGrowthDex() : job.getGrowthDex();
                float GRagi = (isPromoted()) ? job.getProGrowthAg() : job.getGrowthAg();
                float GRski = (isPromoted()) ? job.getProGrowthSk() : job.getGrowthSk();
                float GRbra = (isPromoted()) ? job.getProGrowthBr() : job.getGrowthBr();


                cha += (GRcha * 100 > Data.rand(100)) ? 1 : 0;
                hpt += (GRHP * 100 > Data.rand(100)) ? 1 : 0;
                str += (GRstr * 100 > Data.rand(100)) ? 1 : 0;
                armorE += (GRArmorE * 100 > Data.rand(100)) ? 1 : 0;
                armorB += (GRArmorB * 100 > Data.rand(100)) ? 1 : 0;
                armorP += (GRArmorP * 100 > Data.rand(100)) ? 1 : 0;
                dex += (GRdex * 100 > Data.rand(100)) ? 1 : 0;
                agi += (GRagi * 100 > Data.rand(100)) ? 1 : 0;
                ski += (GRski * 100 > Data.rand(100)) ? 1 : 0;
                bra += (GRbra * 100 > Data.rand(100)) ? 1 : 0;
            }else{
                mob = promoted();
                cha = job.getProBoCha();
                ld = job.getProBoLd();
                hpt = job.getProBoHP();
                str = job.getProBoStr();
                armorP = job.getProBoPiercingArmor();
                armorE = job.getProBoEdgedArmor();
                armorB = job.getProBoBluntArmor();
                dex = job.getProBoDex();
                agi = job.getProBoAg();
                ski = job.getProBoSk();
                bra = job.getProBoBr();
            }

            this.level++;

            this.charisma += cha;
            this.setLeadership(this.leadership + ld);
            this.hitPoints += hpt;
            this.strength += str;
            this.bluntArmor += armorB;
            this.edgedArmor += armorE;
            this.piercinfArmor += armorP;
            this.dexterity += dex;
            this.agility += agi;
            this.skill += ski;
            this.bravery += bra;
        }

        int[] gainlvl = new int[]{hpt, mob, cha, ld, str, armorP, armorB, armorE, dex, agi, ski, bra};
        return gainlvl;
    }

    private int promoted(){
        this.horseman = horsemanUponPromotion;
        int mob = (horseman) ? job.getHorsemanMob() : job.getFootmanMob();
        mob += Data.MOBILITY_BONUS_PROMOTED;
        return mob - this.mobility;
    }

    private void growup(int upto){
        int gainLvl = upto - getLevel();

        if(gainLvl > 0){
            int gainBeforePromotion = Data.PROMOTION_LEVEL - getLevel() - 1;
            int gainAfterPromotion = upto - Data.PROMOTION_LEVEL;

            float mob = 0;
            float cha = 0;
            float ld = 0;
            float hpt = 0;
            float str = 0;
            float armorE = 0;
            float armorP = 0;
            float armorB = 0;
            float dex = 0;
            float agi = 0;
            float ski = 0;
            float bra = 0;

            if(gainBeforePromotion > 0){
                cha += job.getGrowthCha() * gainBeforePromotion;
                hpt += job.getGrowthHP() * gainBeforePromotion;
                str += job.getGrowthStr() * gainBeforePromotion;
                armorE += job.getGrowthEdgegArmor()  * gainBeforePromotion;
                armorB += job.getGrowthBluntArmor()  * gainBeforePromotion;
                armorP += job.getGrowthPiercingArmor()  * gainBeforePromotion;
                dex += job.getGrowthDex()  * gainBeforePromotion;
                agi += job.getGrowthAg() * gainBeforePromotion;
                ski += job.getGrowthSk() * gainBeforePromotion;
                bra += job.getGrowthBr()  * gainBeforePromotion;
            }

            if(getLevel() < Data.PROMOTION_LEVEL && Data.PROMOTION_LEVEL <= upto){
                mob += promoted();
                cha += job.getProBoCha();
                ld += job.getProBoLd();
                hpt += job.getProBoHP();
                str += job.getProBoStr();
                armorP += job.getProBoPiercingArmor();
                armorE += job.getProBoEdgedArmor();
                armorB += job.getProBoBluntArmor();
                dex += job.getProBoDex();
                agi += job.getProBoAg();
                ski += job.getProBoSk();
                bra += job.getProBoBr();
            }

            if(gainAfterPromotion > 0){
                cha += job.getProGrowthCha()  * gainAfterPromotion;
                hpt += job.getGetProGrowthHP() * gainAfterPromotion;
                str += job.getProGrowthStr() * gainAfterPromotion;
                armorE += job.getProGrowthEdgedArmor()  * gainAfterPromotion;
                armorB += job.getProGrowthBluntArmor()  * gainAfterPromotion;
                armorP += job.getProGrowthPiercingArmor()  * gainAfterPromotion;
                dex += job.getProGrowthDex()  * gainAfterPromotion;
                agi += job.getProGrowthAg() * gainAfterPromotion;
                ski += job.getProGrowthSk() * gainAfterPromotion;
                bra += job.getProGrowthBr()  * gainAfterPromotion;
            }

            this.level = upto;
            this.mobility += mob;
            this.charisma += cha;
            this.leadership += ld;
            this.hitPoints += hpt;
            this.strength += str;
            this.piercinfArmor += armorP;
            this.edgedArmor += armorE;
            this.bluntArmor += armorB;
            this.dexterity += dex;
            this.agility +=agi;
            this.skill += ski;
            this.bravery += bra;
        }

    }

    @Override
    public boolean isPromoted() {
        return Data.PROMOTION_LEVEL <= level ;
    }

    @Override
    public boolean isRightHanded() {
        return rightHanded;
    }

    @Override
    public boolean setRightHanded(boolean righthanded) {
        return this.rightHanded = righthanded;
    }

    @Override
    public boolean isStandardBearer() {
        return standardBearer;
    }

    @Override
    public void setStandardBearer(boolean standardBearer) {
        this.standardBearer = standardBearer;
    }

    @Override
    public boolean isHorseman() {
        return horseman;
    }

    @Override
    public void setHorseman(boolean horseman) {
        this.horseman = horseman;
    }

    @Override
    public boolean isHorsemanUponPromotion() {
        return horsemanUponPromotion;
    }

    @Override
    public void setHorsemanUponPromotion(boolean horseman) {
        this.horsemanUponPromotion = horseman;
        if(isPromoted()) this.horseman = horseman;
    }

    @Override
    public Banner getBanner() {
        return banner;
    }

    @Override
    public boolean addWeapon(Data.Weapon weapon) {
        boolean weaponAdded = false;
        if(weapon.getWeaponType() == weaponType) {
            if (weapon1 == Weapon.FIST) {
                weapon1 = weapon;
                weaponAdded = true;
            } else if (weapon2 == Weapon.FIST && isPromoted()) {
                weapon2  = weapon;
                weaponAdded = true;
            }
        }
        return weaponAdded;
    }


    @Override
    public Weapon removeWeapon(int index) {
        Weapon weaponToreturn = null;
        if(index == 1) {
            weaponToreturn = weapon1;
            weapon1 = Weapon.FIST;
        }
        if(index == 2){
            weaponToreturn = weapon2;
            weapon2 = Weapon.FIST;
        }
        return weaponToreturn;
    }

    @Override
    public Weapon replace(int index, Data.Weapon weapon) {
        Weapon weaponToreturn = null;
        if(weapon.getWeaponType() == this.weaponType){
            if(index == 1){
                weaponToreturn = weapon1;
                weapon1 = weapon;
            }
            if(index == 2 && isPromoted()){
                weaponToreturn = weapon2;
                weapon2 = weapon;
            }
        }

        return weaponToreturn;
    }

    @Override
    public void removeAllWeapons() {
        weapon1 = Weapon.FIST;
        weapon2 = Weapon.FIST;
    }

    @Override
    public Array<Data.Weapon> getWeapons() {
        Array<Weapon> weapons = new Array<Weapon>();
        weapons.add(weapon1);
        if(isPromoted()) weapons.add(weapon2);
        return weapons;
    }

    @Override
    public Data.Weapon getCurrentWeapon() {
        return (weapon1Equipped) ? weapon1 : weapon2;
    }

    @Override
    public void switchWeapon() {
        if(isPromoted()){
            this.weapon1Equipped = !weapon1Equipped;
        }
    }

    @Override
    public int getBaseHitpoints() {
        return hitPoints;
    }

    @Override
    public int getAppHitpoints() {
        return hitPoints;
    }

    @Override
    public int getCurrentHP() {
        return currentHitPoints;
    }

    @Override
    public void resetCurrentMoral() {
        this.currentMoral = getAppMoral();
    }

    @Override
    public int getAppMoral() {
        return getAppBravery() + getChiefMoralBonus();
    }

    @Override
    public int getCurrentMoral() {
        return currentMoral;
    }

    @Override
    public int getExperience() {
        return experience;
    }

    @Override
    public void setExperience(int experience) {
        this.experience = experience % Data.EXP_REQUIRED_LEVEL_UP;
    }

    @Override
    public int[] addExpPoints(int exp) {
        int[] gainLvl = new int[0];
        if(level < Data.MAX_LEVEL) {
            if (this.experience + exp < Data.EXP_REQUIRED_LEVEL_UP) {
                this.experience += exp;
            } else {
                this.experience = (level + 1 == Data.MAX_LEVEL) ? 0 : this.experience + exp - Data.EXP_REQUIRED_LEVEL_UP;
                gainLvl = levelup();
            }
        }
        return gainLvl;
    }

    @Override
    public int getExpGained(int levelOpponent, boolean stillFigthing) {
        double expGained = 50 + (100 / Math.PI) * Math.atan((levelOpponent - level - Data.EXP_LVL_GAP_FACTOR_2)/Data.EXP_LVL_GAP_FACTOR_1) - level;
        if(expGained < 0) expGained = 0;
        if(stillFigthing) expGained *= Data.EXP_WOUNDED_ONLY_FACTOR;
        return (int)expGained;
    }

    @Override
    public int getLeadershipExperience() {
        return commandmentExperience;
    }

    @Override
    public void setLeadershipExperience(int experience) {
        this.commandmentExperience = experience % Data.EXP_REQUIRED_LD_LEVEL_UP;
    }

    @Override
    public boolean addLdExpPoints(int exp) {
        this.commandmentExperience += exp;
        if(commandmentExperience > Data.EXP_REQUIRED_LD_LEVEL_UP){
            this.setLeadership(this.leadership + commandmentExperience / Data.EXP_REQUIRED_LD_LEVEL_UP);
            this.commandmentExperience = this.commandmentExperience % Data.EXP_REQUIRED_LD_LEVEL_UP;
            return true;
        }
        return false;
    }

    @Override
    public int getBaseCharisma() {
        return charisma;
    }

    @Override
    public int getAppCharisma() {
        return charisma;
    }

    @Override
    public int getBaseLeadership() {
        return leadership;
    }

    @Override
    public int getAppLeadership() {
        return leadership;
    }

    @Override
    public int getBaseStrength() {
        return strength;
    }

    @Override
    public int getAppStrength() {
        return strength;
    }

    @Override
    public int getBaseArmor(Data.DamageType damageType) {
        int armor = 0;
        switch(damageType){
            case BLUNT: armor = bluntArmor; break;
            case EDGED: armor = edgedArmor; break;
            case PIERCING: armor = piercinfArmor;break;
        }
        return armor;
    }

    @Override
    public int getAppArmor(Data.DamageType damageType) {
        return getBaseArmor(damageType);
    }

    @Override
    public int getBaseAgility() {
        return agility;
    }

    @Override
    public int getAppAgility() {
        return agility;
    }

    @Override
    public int getBaseDexterity() {
        return dexterity;
    }

    @Override
    public int getAppDexterity() {
        return dexterity;
    }

    @Override
    public int getBaseSkill() {
        return skill;
    }

    @Override
    public int getAppSkill() {
        return skill;
    }

    @Override
    public int getBaseBravery() {
        return bravery;
    }

    @Override
    public int getAppBravery() {
        return bravery;
    }

    @Override
    public int getBaseMobility() {
        return mobility;
    }

    @Override
    public int getAppMobility() {
        return mobility;
    }

    @Override
    public boolean has(Data.PassiveAbility ability) {
        return item1.getPassiveAbility() == ability || item2.getPassiveAbility() == ability;
    }

    @Override
    public boolean has(ActiveAbility ability) {
        return item1.getActiveAbility() == ability || item2.getActiveAbility() == ability || getCurrentWeapon().getAbility() == ability;
    }

    @Override
    public Array<PassiveAbility> getPassiveAbilities() {
        Array<PassiveAbility> abilities = new Array<PassiveAbility>();
        abilities.add(item1.getPassiveAbility());
        abilities.add(item2.getPassiveAbility());
        return abilities;
    }

    @Override
    public Array<ActiveAbility> getActiveAbilities() {
        Array<ActiveAbility> abilities = new Array<ActiveAbility>();
        abilities.add(item1.getActiveAbility());
        abilities.add(item2.getActiveAbility());
        abilities.add(getCurrentWeapon().getAbility());
        return abilities;
    }

    @Override
    public boolean has(Data.Item item) {
        return this.item1 == item || this.item2 == item;
    }

    @Override
    public boolean addItem(Data.Item item) {
        boolean itemAdded = false;
        if(this.item1 == Item.NOTHING){
            this.item1 = item;
            itemAdded = true;
        }
        if(this.item2 == Item.NOTHING){
            this.item2 = item;
            itemAdded = true;
        }
        return itemAdded;
    }

    @Override
    public Item setItem1(Item item) {
        Item oldItem = this.item1;
        this.item1 = item;
        return oldItem;
    }

    @Override
    public Item setItem2(Item item) {
        Item oldItem = this.item2;
        this.item2 = item;
        return oldItem;
    }

    @Override
    public void setItem1Stealable(boolean stealable) {
        this.item1Stealable = stealable;
    }

    @Override
    public void setitem2Stealable(boolean stealable) {
        this.item2Stealable = stealable;
    }

    @Override
    public boolean isItem1Stealable() {
        return item1Stealable;
    }

    @Override
    public boolean isItem2Stealable() {
        return item2Stealable;
    }

    @Override
    public Array<Item> disequipAllItem() {
        Array<Item> items = new Array<Item>();
        if(this.item1 != Item.NOTHING){
            items.add(item1);
            this.item1 = Item.NOTHING;
        }
        if(this.item2 != Item.NOTHING){
            items.add(item2);
            this.item2 = Item.NOTHING;
        }
        return items;
    }

    @Override
    public Array<Data.Item> getItems() {
        Array<Item> items = new Array<Item>();
        if(this.item1 != Item.NOTHING){
            items.add(item1);
        }
        if(this.item2 != Item.NOTHING){
            items.add(item2);
        }
        return items;
    }

    @Override
    public Item removeItem(int index) {
        Item item = null;
        if(index == 0 && this.item1 != Item.NOTHING){
            item = this.item1;
            this.item1 = Item.NOTHING;
        }
        if(index == 1 && this.item2 != Item.NOTHING){
            item = this.item2;
            this.item2 = Item.NOTHING;
        }
        return item;
    }

    @Override
    public Item replaceItem(int index, Data.Item item) {
        Item olditem = null;
        if(index == 0){
            olditem = this.item1;
            this.item1 = item;
        }
        if(index == 1){
            olditem = this.item2;
            this.item2 = item;
        }
        return olditem;
    }

    @Override
    public int getAppWeaponRangeMin() {
        return getCurrentWeapon().getRangeMin();
    }

    @Override
    public int getAppWeaponRangeMax() {
        return getCurrentWeapon().getRangeMax();
    }

    @Override
    public int getCurrentWeaponRangeMin(int rowUnit, int colUnit, Battlefield battlefield) {
        return getCurrentWeapon().getRangeMin();
    }

    @Override
    public int getCurrentWeaponRangeMax(int rowUnit, int colUnit, Battlefield battlefield) {
        int rangeMax = getCurrentWeapon().getRangeMax();
        if(battlefield.isTileExisted(rowUnit, colUnit)){
            if(rangeMax > 1) {
                TileType tileType = battlefield.getTile(rowUnit, colUnit);
                if(tileType.enhanceRange()) rangeMax++;
            }
        }
        return rangeMax;
    }

    @Override
    public int getAppAttackAccuracy() {
        return getCurrentWeapon().getAccuracy() + Data.DEX_FACTOR_ATT_ACC * getAppDexterity() + getChiefCharisma();
    }

    @Override
    public int getAppAttackMight() {
        return getCurrentWeapon().getDamage() + getAppStrength();
    }

    @Override
    public int getAppDefense(DamageType damageType) {
        return getAppArmor(damageType);
    }

    @Override
    public int getAppAvoidance() {
        return Data.DEX_FACTOR_AVO * getAppAgility();
    }

    @Override
    public int getAppDropRate() {
        return getAppDexterity() * Data.DEX_FACTOR_DROP + getChiefCharisma();
    }

    @Override
    public void setOAChargingBarPoints(int barProgression) {
        this.OAChargingBar = barProgression % Data.OA_CHARGING_BAR_MAX_VALUE;
    }

    @Override
    public void addOAChargingBarPoints(int points) {
        this.OAChargingBar += points;
        if(OAChargingBar > Data.OA_CHARGING_BAR_MAX_VALUE) OAChargingBar = Data.OA_CHARGING_BAR_MAX_VALUE;
        if(OAChargingBar < 0 ) OAChargingBar = 0;
    }

    @Override
    public int getOAChargingBarPoints() {
        return this.OAChargingBar;
    }

    @Override
    public int getAppOATriggerRate() {
        return getAppSkill();
    }

    @Override
    public int getCurrentOATriggerRate(int rowUnit, int colUnit, Battlefield battlefield) {
        return getAppSkill();
    }

    @Override
    public boolean isMobilized() {
        return (army != null) && getArmy().isUnitMobilized(this);
    }

    @Override
    public boolean isWarChief() {
        return isMobilized() && army.getWarChiefs().contains(this, true);
    }

    @Override
    public boolean isWarlord() {
        return isMobilized() && this == army.getWarlord();
    }

    @Override
    public Data.Allegeance getAllegeance() {
        if(isMobilized()){
            return army.getAllegeance();
        }
        return null;
    }

    @Override
    public int getMaxSoldiersAs(boolean warlord) {
        int maxSoldiers = 0;
        if(warlord){
            maxSoldiers = 2 + (this.leadership + 2) / 5;
            if(maxSoldiers > Data.MAX_UNITS_UNDER_WARLORD){
                maxSoldiers = 5;
            }
        }else{
            maxSoldiers = 1 + (this.leadership + 1) / 5;
            if(maxSoldiers > Data.MAX_UNITS_UNDER_WAR_CHIEF){
                maxSoldiers = 4;
            }
        }
        return maxSoldiers;
    }

    @Override
    public int getMaxWarChiefs() {
        int maxWC = this.leadership / 6;
        if(maxWC > 3 )
            maxWC = 3;
        return maxWC + 1;
    }


    @Override
    public void setArmy(IArmy army) {
        this.army = army;
    }

    @Override
    public boolean isAllyWith(Data.Allegeance allegeance) {
        return (army != null) && army.getAllegeance() == allegeance;
    }

    @Override
    public Array<IUnit> getSquad() {
        return (army != null) ? army.getSquad(this) : null;
    }

    @Override
    public IArmy getArmy() {
        return army;
    }

    @Override
    public boolean sameSquadAs(IUnit unit) {
        if(army != null){
            Array<IUnit> squad =  army.getSquad(unit);
            for(int i = 0; i < squad.size; i++){
                if(squad.get(i) == this){
                    return true;
                }

            }
        }
        return false;
    }

    @Override
    public boolean sameArmyAs(IUnit unit) {
        return (army != null) ? this.army == unit.getArmy() : false;
    }

    @Override
    public void setLeadership(int leadership) {
        this.leadership = leadership;
    }

    @Override
    public int getChiefCharisma() {
        return isMobilized() ? army.getWarchief(this).getAppCharisma() : 0;
    }

    @Override
    public int getChiefMoralBonus() {
        return getChiefCharisma();
    }

    @Override
    public void setOrientation(Data.Orientation orientation) {
        this.orientation = orientation;
    }

    @Override
    public Data.Orientation getOrientation() {
        return orientation;
    }

    @Override
    public void setBehaviour(Data.Behaviour behaviour) {
        this.behaviour = behaviour;
    }

    @Override
    public Data.Behaviour getBehaviour() {
        return behaviour;
    }

    @Override
    public boolean hasActed() {
        return acted;
    }

    @Override
    public boolean hasMoved() {
        return moved;
    }

    @Override
    public boolean isWounded() {
        return currentHitPoints < getAppHitpoints();
    }

    @Override
    public boolean isOut() {
        return currentMoral == 0;
    }

    @Override
    public boolean isDead() {
        return currentHitPoints == 0;
    }

    @Override
    public void setActed(boolean acted) {
        this.acted = acted;
    }

    @Override
    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    @Override
    public boolean isDone() {
        return acted && moved;
    }

    @Override
    public boolean treated(int healPower) {
        if(isWounded()) {
            if (healPower + hitPoints > getAppHitpoints()) {
                this.currentHitPoints = getAppHitpoints();
                this.currentMoral = getAppMoral();
            } else {


                if(currentMoral > 0) {
                    if (healPower + currentMoral > getAppMoral()) {
                        currentMoral = getAppMoral();
                    } else {
                        currentMoral += healPower;
                    }
                }else{
                    currentMoral = healPower + hitPoints - (getAppHitpoints() - getAppMoral());
                    if(currentMoral < 0 )
                        currentMoral = 0;
                }
                currentHitPoints += healPower;

            }
            return true;
        }
        return false;
    }

    public Array<DamageNotification> applyDamage(int damageTaken, boolean moralDamageOnly){
        Array<DamageNotification> notifications = new Array<DamageNotification>();
        DamageNotification notification = new DamageNotification(this, moralDamageOnly, damageTaken, false, false);
        notifications.add(notification);

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
                if(!moralDamageOnly) this.currentHitPoints = 0;
            }

            // if the unit is a war chief, the consequences deepens
            if(isWarChief()){
                int moralDamage = getChiefMoralBonus();
                Array<IUnit> squad = getArmy().getSquad(this);
                for(int i = 1; i < squad.size; i++){
                    notifications.addAll(squad.get(i).applyDamage(moralDamage, true));
                }
            }
        }
        return notifications;
    }

    public static class DamageNotification{
        public Unit wounded;
        public boolean moralOnly;
        public int damageTaken;
        public boolean critical;
        public boolean backstab;

        public DamageNotification (Unit wounded, boolean moralOnly, int damageTaken, boolean critical, boolean backstab){
            this.wounded = wounded;
            this.moralOnly = moralOnly;
            this.damageTaken = damageTaken;
            this.critical = critical;
            this.backstab = backstab;
        }

        public boolean isRelevant(){
            return damageTaken > 0;
        }
    }



}
