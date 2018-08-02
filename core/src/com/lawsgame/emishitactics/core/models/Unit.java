package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Notification.ApplyDamage;
import com.lawsgame.emishitactics.core.models.Data.Behaviour;
import com.lawsgame.emishitactics.core.models.Data.DamageType;
import com.lawsgame.emishitactics.core.models.Data.Job;
import com.lawsgame.emishitactics.core.models.Data.Orientation;
import com.lawsgame.emishitactics.core.models.Data.TileType;
import com.lawsgame.emishitactics.core.models.Data.WeaponType;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Item;

public class Unit extends IUnit{

    protected String name;
    protected int level;
    protected Job job;
    protected WeaponType weaponType;
    protected boolean horseman;
    protected boolean horsemanUponPromotion;
    protected boolean standardBearer;

    protected Array<Weapon> weapons;
    private int currentWeaponIndex;
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

    protected Array<Equipment> equipments;
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
     * Can be addExpGained as well:
     * - weapons
     * - battle and leadership experience
     * - right handed
     * - equipments (and whether each is stealable or not)
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

        this.weapons = new Array<Weapon>();
        this.weapons.add(Weapon.FIST);
        this.currentWeaponIndex = 0;
        this.equipments = new Array<Equipment>();

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
    public boolean addWeapon(Weapon weapon) {
        boolean weaponAdded = false;
        if(weapon.getTemplate().getWeaponType() == weaponType) {
            if(weapons.contains(Weapon.FIST, true)){
                weapons.removeValue(Weapon.FIST, true);
            }
            if(weapons.size < ((isPromoted()) ? Data.MAX_WEAPON_CARRIED_UPON_PROMOTION : Data.MAX_WEAPON_CARRIED)){
                weapons.add(weapon);
                weaponAdded = true;
            }
        }
        return weaponAdded;
    }


    @Override
    public Weapon removeWeapon(int index) {
        Weapon weaponToreturn = null;
        if(0 <= index && index < weapons.size)
            weaponToreturn = weapons.removeIndex(index);
        if(weapons.size == 0)
            weapons.add(Weapon.FIST);
        return weaponToreturn;
    }

    @Override
    public Weapon replace(int index, Weapon weapon) {
        Weapon weaponToreturn = null;
        if(weapon.getTemplate().getWeaponType() == this.weaponType){
            if(0 <= index && index < weapons.size) {
                weaponToreturn = weapons.removeIndex(index);
                weapons.insert(index, weapon);
            }
        }
        return weaponToreturn;
    }

    @Override
    public Array<Weapon> removeAllWeapons() {
        Array<Weapon> removedWeapons = weapons;
        weapons = new Array<Weapon>();
        return removedWeapons;
    }

    @Override
    public Array<Weapon> getWeapons() {
        return weapons;
    }

    @Override
    public Weapon getCurrentWeapon() {
        return weapons.get(currentWeaponIndex);
    }

    @Override
    public void switchWeapon(int index) {
        if(0 <= index && index < weapons.size){
            this.currentWeaponIndex = index;
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
    public void setCurrentHitPoints(int hitPoints) {
        if(0<= hitPoints && hitPoints <= getAppHitpoints()){
            this.currentHitPoints = hitPoints;
        }
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
    public void setCurrentMoral(int moral) {
        if(0 <= moral && moral <= getAppMoral()){
            this.currentMoral = moral;
        }
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
    public boolean has(Data.Ability ability) {
        boolean hasAbility = false;
        for(int i = 0; i < equipments.size; i++){
            if(equipments.get(i).getTemplate().getAbility() == ability){
                hasAbility = true;
                continue;
            }
        }
        if(!hasAbility){
            for(int i = 0; i < weapons.size; i++){
                if(weapons.get(i).getTemplate().getAbility() == ability){
                    hasAbility = true;
                    continue;
                }
            }
        }
        if(!hasAbility){
            for(int i = 0; i < job.getNativeAbilities().length; i++){
                if(job.getNativeAbilities()[i] == ability){
                    hasAbility = true;
                    continue;
                }
            }
        }
        return hasAbility;
    }

    @Override
    public Array<Data.Ability> getAbilities() {
        Array<Data.Ability> abilities = new Array<Data.Ability>();
        Data.Ability ability;
        for(int i = 0; i < equipments.size; i++){
            if(equipments.get(i).getTemplate().getAbility() != Data.Ability.NONE){
                ability = equipments.get(i).getTemplate().getAbility();
                if(!abilities.contains(ability, true))
                    abilities.add(ability);
            }
        }
        for(int i = 0; i < weapons.size; i++){
            if(weapons.get(i).getTemplate().getAbility() == Data.Ability.NONE){
                ability = weapons.get(i).getTemplate().getAbility();
                if(!abilities.contains(ability, true))
                    abilities.add(ability);
            }
        }
        for(int i = 0; i < job.getNativeAbilities().length; i++){
            ability = job.getNativeAbilities()[i];
            if(!abilities.contains(ability, true))
                abilities.add(ability);
        }
        return abilities;
    }

    @Override
    public boolean has(Equipment item) {
        return equipments.contains(item, true);
    }

    @Override
    public boolean addEquipment(Equipment item) {
        boolean itemAdded = false;
        if(!equipments.contains(item, true) && equipments.size < ((isPromoted()) ? Data.MAX_ITEM_CARRIED_UPON_PROMOTION: Data.MAX_ITEM_CARRIED)){
            equipments.add(item);
            itemAdded = true;
        }
        return itemAdded;
    }

    @Override
    public Array<Equipment> disequipAllEquipment() {
        Array<Equipment> removedItems = equipments;
        this.equipments = new Array<Equipment>();
        return removedItems;
    }

    @Override
    public Array<Equipment> getEquipments() {
        return equipments;
    }

    @Override
    public Equipment removeEquipment(int index) {
        Equipment item = null;
        if(0 <= index && index < equipments.size ){
            item = equipments.removeIndex(index);
        }
        return item;
    }

    @Override
    public Equipment replaceEquipment(int index, Equipment item) {
        Equipment olditem = null;
        if(0 <= index && index < equipments.size ){
            olditem = equipments.removeIndex(index);
            equipments.insert(index, item);
        }
        return olditem;
    }

    @Override
    public void setStealable(boolean weapon, int index, boolean stealable) {
        if(weapon){
            if(0 <= index && index < weapons.size){
                weapons.get(index).setStealable(stealable);
            }
        }else{
            if(0 <= index && index < weapons.size){
                equipments.get(index).setStealable(stealable);
            }
        }
    }

    @Override
    public boolean isStealable() {
        boolean stealable = false;
        for(int i = 0; i < equipments.size; i++){
            if(equipments.get(i).isStealable()){
                stealable = true;
                continue;
            }
        }
        if(!stealable){
            for(int i = 0; i < weapons.size; i++){
                if(weapons.get(i).isStealable()){
                    stealable = true;
                    continue;
                }
            }
        }
        return stealable;
    }

    @Override
    public Item getStealable() {
        Array<Item> items = new Array<Item>();
        Item stolenItem = null;
        for(int i = 0; i < equipments.size; i++){
            if(equipments.get(i).isStealable()){
                items.add(equipments.get(i));
            }
        }
        for(int i = 0; i < weapons.size; i++){
            if(weapons.get(i).isStealable()){
                items.add(weapons.get(i));
            }
        }

        stolenItem = items.random();

        if(stolenItem instanceof Equipment) {
            equipments.removeValue((Equipment)stolenItem, true);
        }else if(stolenItem instanceof Weapon){
            weapons.removeValue((Weapon)stolenItem, true);
        }

        return stolenItem;
    }

    @Override
    public Item getDroppableItem() {
        Item droppedItem = null;

        if(!weapons.contains(Weapon.FIST, true)|| equipments.size > 0) {
            int dropRange = 0;
            for (int i = 0; i < weapons.size; i++) {
                dropRange += weapons.get(i).getDropRate();
            }
            for (int i = 0; i < equipments.size; i++) {
                dropRange += equipments.get(i).getDropRate();
            }

            int pick = 1 + Data.rand(dropRange);
            dropRange = 0;
            for (int i = 0; i < weapons.size; i++) {
                dropRange += weapons.get(i).getDropRate();
                if(pick <= dropRange) {
                    droppedItem = removeWeapon(i);

                    continue;
                }
            }
            if(droppedItem == null) {
                for (int i = 0; i < equipments.size; i++) {
                    dropRange += equipments.get(i).getDropRate();
                    if(pick <= dropRange) {
                        droppedItem = removeEquipment(i);
                        continue;
                    }
                }
            }
        }
        return droppedItem;
    }

    @Override
    public int getAppWeaponRangeMin() {
        return getCurrentWeapon().getTemplate().getRangeMin();
    }

    @Override
    public int getAppWeaponRangeMax() {
        return getCurrentWeapon().getTemplate().getRangeMax();
    }

    @Override
    public int getCurrentWeaponRangeMin(int rowUnit, int colUnit, Battlefield battlefield) {
        return getCurrentWeapon().getTemplate().getRangeMin();
    }

    @Override
    public int getCurrentWeaponRangeMax(int rowUnit, int colUnit, Battlefield battlefield) {
        int rangeMax = getCurrentWeapon().getTemplate().getRangeMax();
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
        return getCurrentWeapon().getTemplate().getAccuracy() + Data.DEX_FACTOR_ATT_ACC * getAppDexterity() + getChiefCharisma();
    }

    @Override
    public int getAppAttackMight() {
        return getCurrentWeapon().getTemplate().getDamage() + getAppStrength();
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
    public Array<IUnit> getSquad(boolean stillFighting) {
        return (army != null) ? army.getSquad(this, stillFighting) : null;
    }

    @Override
    public IArmy getArmy() {
        return army;
    }

    @Override
    public boolean sameSquadAs(IUnit unit) {
        if(army != null){
            Array<IUnit> squad =  army.getSquad(unit, false);
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
    public boolean isOutOfAction() {
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

    public Array<ApplyDamage> applyDamage(int damageTaken, boolean moralDamageOnly){
        Array<ApplyDamage> notifications = new Array<ApplyDamage>();
        ApplyDamage notification = new ApplyDamage(this, moralDamageOnly, damageTaken);
        notifications.add(notification);

        if(this.currentMoral > damageTaken){
            // the unit survive
            this.currentMoral -= damageTaken;
            if(!moralDamageOnly) this.currentHitPoints -= damageTaken;
            notification.state = ApplyDamage.State.WOUNDED;

        }else{
            // the unit dies or flies
            if(this.currentHitPoints > damageTaken){
                this.currentMoral = 0;
                if(!moralDamageOnly) this.currentHitPoints -= damageTaken;
                notification.state = ApplyDamage.State.FLED;
            }else{
                this.currentMoral = 0;
                notification.state = ApplyDamage.State.FLED;
                if(!moralDamageOnly){
                    this.currentHitPoints = 0;
                    notification.state = ApplyDamage.State.DIED;
                }
            }

            // if the unit is a war chief, the consequences deepens
            if(isWarChief()){
                int moralDamage = getChiefMoralBonus();
                Array<IUnit> squad = getArmy().getSquad(this, true);
                for(int i = 1; i < squad.size; i++){
                    if(!squad.get(i).isOutOfAction())
                        notifications.addAll(squad.get(i).applyDamage(moralDamage, true));
                }
            }
        }
        return notifications;
    }

}
