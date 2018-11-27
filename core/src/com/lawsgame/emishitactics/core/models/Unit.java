package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.Data.Behaviour;
import com.lawsgame.emishitactics.core.models.Data.DamageType;
import com.lawsgame.emishitactics.core.models.Data.Orientation;
import com.lawsgame.emishitactics.core.models.Data.TileType;
import com.lawsgame.emishitactics.core.models.Data.UnitTemplate;
import com.lawsgame.emishitactics.core.models.Data.WeaponType;
import com.lawsgame.emishitactics.core.models.Notification.TakeDamage;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.models.interfaces.MilitaryForce;
import com.lawsgame.emishitactics.core.models.interfaces.Model;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Stack;

public class Unit extends Model {

    protected final String name;
    protected int level;
    protected final UnitTemplate template;
    protected final WeaponType weaponType;
    protected boolean shielbearer;
    protected boolean horseman;
    protected boolean horsemanUponPromotion;

    protected boolean rightHanded = true;
    protected Array<Weapon> weapons;
    protected int experience = 0;
    protected int commandmentExperience = 0;
    private MilitaryForce army = null;

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

    protected int actionPoints = 0;
    protected boolean disabled = false;
    protected boolean crippled = false;

    protected Array<Data.Ability> nativeAbilities;
    protected Array<Equipment> equipments;
    protected Banner banner;

    /**
     * battlefield execution related attributes
     */
    protected Orientation orientation = Orientation.SOUTH;
    protected Behaviour behaviour = Behaviour.PASSIVE;
    protected boolean moved = false;
    protected boolean acted = false;

    /**
     * GENERIC UNIT constructor
     *
     * Can be set as well:
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
     * @param template
     * @param level
     * @param weaponType
     * @param horseman
     * @param horsemanUponPromotion
     * @param homogeneousLevelsup
     */
    public Unit(
            String name,
            UnitTemplate template,
            int level,
            WeaponType weaponType,
            boolean shielbearer,
            boolean horseman,
            boolean horsemanUponPromotion,
            boolean homogeneousLevelsup){

        this.name = name;
        this.template = template;
        this.level = template.getStartingLevel();
        this.weaponType = weaponType;
        this.horseman = horseman;
        this.horsemanUponPromotion = horsemanUponPromotion;

        this.mobility = (horseman) ? template.getHorsemanMob() : template.getFootmanMob();
        this.charisma = template.getBaseCha();
        this.leadership = template.getBaseLd();
        this.hitPoints = template.getBaseHP();
        this.strength = template.getBaseStr() + ((shielbearer) ?  template.getShieldBearerStrengthBonus(): 0) + ((horseman) ? template.getHorsemanStrengthBonus(): 0);
        this.piercinfArmor = template.getBasePiercingArmor() + ((shielbearer) ?  template.getShieldBearerArmorPBonus(): 0) + ((horseman) ? template.getHorsemanArmorPBonus(): 0);
        this.bluntArmor = template.getBaseBluntArmor() + ((shielbearer) ?  template.getShieldBearerArmorBBonus(): 0) + ((horseman) ? template.getHorsemanArmorBBonus(): 0);
        this.edgedArmor = template.getBaseEgdedArmor() + ((shielbearer) ?  template.getShieldBearerArmorEBonus(): 0) + ((horseman) ? template.getShieldBearerArmorEBonus(): 0);
        this.dexterity = template.getBaseDex() + ((shielbearer) ?  template.getShieldBearerDexBonus(): 0) + ((horseman) ? template.getHorsemanDexBonus(): 0);
        this.agility = template.getBaseAg() + ((shielbearer) ?  template.getShieldBearerAgilityBonus(): 0) + ((horseman) ? template.getHorsemanAgilityBonus(): 0);
        this.skill = template.getBaseSk();
        this.bravery = template.getBaseBr();

        this.weapons = new Array<Weapon>();
        this.weapons.add(Weapon.FIST);
        this.equipments = new Array<Equipment>();
        this.banner = new Banner(this);
        this.nativeAbilities = new Array<Data.Ability>();
        this.nativeAbilities.addAll(template.getNativeAbilities());

        if(homogeneousLevelsup) {
            growup(level);
        }else{
            for(int lvl = template.getStartingLevel(); lvl < level; lvl++){
                levelup();
            }
        }

        this.currentHitPoints = getAppHitpoints();
        resetCurrentMoral();
        resetActionPoints();
    }

    public Unit(String name, UnitTemplate template, WeaponType weaponType){
        this(name, template, template.getStartingLevel(), weaponType, false, false, false, true);
    }

   public static Unit createGenericUnit(
           String name,
           UnitTemplate template,
           int level,
           WeaponType weaponType,
           boolean shielbearer,
           boolean horseman,
           boolean horsemanUponPromotion,
           boolean homogeneousLevelsup){
        return new Unit(name, template, level, weaponType, shielbearer, horseman, horsemanUponPromotion, homogeneousLevelsup);
   }

    public static Unit createCharacterUnit(
            String name,
            String title,
            UnitTemplate template,
            int level,
            WeaponType weaponType,
            boolean shielbearer,
            boolean horseman,
            boolean horsemanUponPromotion){
        return new CharacterUnit(name, title, template, level, weaponType, shielbearer, horseman, horsemanUponPromotion, true);
    }


    //------------------------- PROCESSING -------------------------

    public String getName(I18NBundle bundle) {
        return bundle.get(name);
    }


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


            if (Data.PROMOTION_LEVEL != level ) {
                float GRcha = getAppGrCharisma();
                float GRHP = getAppGrHitpoints();
                float GRstr = getAppGrStrength();
                float GRArmorP = getAppGrArmor(DamageType.PIERCING);
                float GRArmorB = getAppGrArmor(DamageType.BLUNT);
                float GRArmorE = getAppGrArmor(DamageType.EDGED);
                float GRdex = getAppGrDexterity();
                float GRagi = getAppGrAgility();
                float GRski = getAppGrSkill();
                float GRbra = getAppGrBravery();

                cha += (GRcha  > MathUtils.random()) ? 1 : 0;
                hpt += (GRHP  > MathUtils.random()) ? 1 : 0;
                str += (GRstr  > MathUtils.random()) ? 1 : 0;
                armorE += (GRArmorE  > MathUtils.random()) ? 1 : 0;
                armorB += (GRArmorB  > MathUtils.random()) ? 1 : 0;
                armorP += (GRArmorP  > MathUtils.random()) ? 1 : 0;
                dex += (GRdex  > MathUtils.random()) ? 1 : 0;
                agi += (GRagi  > MathUtils.random()) ? 1 : 0;
                ski += (GRski  > MathUtils.random()) ? 1 : 0;
                bra += (GRbra  > MathUtils.random()) ? 1 : 0;
            }else{
                int[] promoStats = promoted();

                mob = promoStats[0];
                cha = template.getProBoCha();
                ld = template.getProBoLd();
                hpt = template.getProBoHP();
                str = template.getProBoStr() + promoStats[1];
                armorP = template.getProBoPiercingArmor() + promoStats[4];
                armorE = template.getProBoEdgedArmor() + promoStats[5];
                armorB = template.getProBoBluntArmor() + promoStats[6];
                dex = template.getProBoDex() + promoStats[2];
                agi = template.getProBoAg() + promoStats[3];
                ski = template.getProBoSk();
                bra = template.getProBoBr();
            }

            this.level++;

            this.mobility += mob;
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

        return new int[]{hpt, mob, cha, ld, str, armorP, armorB, armorE, dex, agi, ski, bra};
    }

    private int[] promoted(){
        int mob = (!horseman && horsemanUponPromotion) ? template.getHorsemanMob() - template.getFootmanMob() : 0;
        int str = ((!horseman && horsemanUponPromotion) ? template.getHorsemanStrengthBonus(): 0);
        int dex = ((!horseman && horsemanUponPromotion) ? template.getHorsemanDexBonus(): 0);
        int agi = ((!horseman && horsemanUponPromotion) ? template.getHorsemanAgilityBonus(): 0);
        int armorP = ((!horseman && horsemanUponPromotion) ? template.getHorsemanArmorPBonus(): 0);
        int armorB = ((!horseman && horsemanUponPromotion) ? template.getHorsemanArmorBBonus(): 0);
        int armorE = ((!horseman && horsemanUponPromotion) ? template.getShieldBearerArmorEBonus(): 0);

        this.horseman = horsemanUponPromotion;
        mob += Data.MOBILITY_BONUS_PROMOTED;

        return new int[]{mob, str, dex, agi, armorP, armorB, armorE};
    }

    public void growup(int upto){
        int gainLvl = upto - getLevel();

        if(gainLvl > 0){
            int gainBeforePromotion = (Data.PROMOTION_LEVEL <= upto) ? Data.PROMOTION_LEVEL - getLevel() - 1 : upto - getLevel();
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
                cha += template.getGrowthCha() * gainBeforePromotion;
                hpt += template.getGrowthHP() * gainBeforePromotion;
                str += template.getGrowthStr() * gainBeforePromotion;
                armorE += template.getGrowthEdgegArmor()  * gainBeforePromotion;
                armorB += template.getGrowthBluntArmor()  * gainBeforePromotion;
                armorP += template.getGrowthPiercingArmor()  * gainBeforePromotion;
                dex += template.getGrowthDex()  * gainBeforePromotion;
                agi += template.getGrowthAg() * gainBeforePromotion;
                ski += template.getGrowthSk() * gainBeforePromotion;
                bra += template.getGrowthBr()  * gainBeforePromotion;
            }

            if(getLevel() < Data.PROMOTION_LEVEL && Data.PROMOTION_LEVEL <= upto){
                int[] promoStats = promoted();

                mob = promoStats[0];
                cha = template.getProBoCha();
                ld = template.getProBoLd();
                hpt = template.getProBoHP();
                str = template.getProBoStr() + promoStats[1];
                armorP = template.getProBoPiercingArmor() + promoStats[4];
                armorE = template.getProBoEdgedArmor() + promoStats[5];
                armorB = template.getProBoBluntArmor() + promoStats[6];
                dex = template.getProBoDex() + promoStats[2];
                agi = template.getProBoAg() + promoStats[3];
                ski = template.getProBoSk();
                bra = template.getProBoBr();
            }

            if(gainAfterPromotion > 0){
                cha += template.getProGrowthCha()  * gainAfterPromotion;
                hpt += template.getGetProGrowthHP() * gainAfterPromotion;
                str += template.getProGrowthStr() * gainAfterPromotion;
                armorE += template.getProGrowthEdgedArmor()  * gainAfterPromotion;
                armorB += template.getProGrowthBluntArmor()  * gainAfterPromotion;
                armorP += template.getProGrowthPiercingArmor()  * gainAfterPromotion;
                dex += template.getProGrowthDex()  * gainAfterPromotion;
                agi += template.getProGrowthAg() * gainAfterPromotion;
                ski += template.getProGrowthSk() * gainAfterPromotion;
                bra += template.getProGrowthBr()  * gainAfterPromotion;
            }

            this.level = upto;
            this.mobility += mob;
            this.charisma += cha;
            this.setLeadership((int) (this.leadership + ld));
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


    public boolean isPromoted() {
        return Data.PROMOTION_LEVEL <= level ;
    }

    public String getTitle(I18NBundle bundle) {
        return template.getName(bundle);
    }


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


    public Weapon removeWeapon(int index) {
        Weapon weaponToreturn = null;
        if(0 <= index && index < weapons.size) {
            weaponToreturn = weapons.removeIndex(index);
        }
        if(weapons.size == 0)
            weapons.add(Weapon.FIST);
        return weaponToreturn;
    }


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


    public Array<Weapon> removeAllWeapons() {
        Array<Weapon> removedWeapons = weapons;
        weapons = new Array<Weapon>();
        weapons.add(Weapon.FIST);
        weapons.removeValue(Weapon.FIST, true);
        return removedWeapons;
    }

    public Weapon getCurrentWeapon() {
        return weapons.get(0);
    }

    public boolean switchWeapon(int index) {
        if (0 < index && index < weapons.size) {
            weapons.swap(0, index);
            return true;
        }
        return false;
    }


    public Weapon getWeapon(int index) {
        Weapon weapon = null;
        if (0 < index && index < weapons.size) {
            weapon = weapons.get(index);
        }
        return weapon;
    }

    public boolean isStandardBearer() {
        return isWarChief();
    }

    public int getAppHitpoints() {
        return hitPoints;
    }


    public float getAppGrHitpoints() {
        return  (isPromoted()) ? template.getGetProGrowthHP() : template.getGrowthHP();
    }

    public void resetCurrentMoral() {
        this.currentMoral = getAppMoral();
        if(this.currentMoral > this.currentHitPoints)
            this.currentMoral = currentHitPoints;
    }


    public int getAppMoral() {
        return getAppBravery();
    }

    public Stack<int[]> addExpPoints(int exp) {
        Stack<int[]> gainLvl = new Stack<int[]>();

        int previousExperience = getExperience();
        int[] exps = setExperience(this.experience + exp);
        exps[0] -= previousExperience;

        for(int i = 0; i < exps.length - 1; i++){
            gainLvl.push(ArrayUtils.add(levelup(), exps[i]));
        }
        gainLvl.push(new int[]{exps[exps.length - 1]});
        return gainLvl;
    }

    public int addLdExpPoints(int exp) {
        int ldLevelGained = 0;
        this.commandmentExperience += exp;
        if(commandmentExperience > Data.EXP_REQUIRED_LD_LEVEL_UP){
            ldLevelGained = commandmentExperience / Data.EXP_REQUIRED_LD_LEVEL_UP;
            this.setLeadership(this.leadership + ldLevelGained);
            this.commandmentExperience = this.commandmentExperience % Data.EXP_REQUIRED_LD_LEVEL_UP;
        }
        return ldLevelGained;
    }


    public int getAppCharisma() {
        return charisma;
    }

    public float getAppGrCharisma() {
        return (isPromoted()) ? template.getProGrowthDex() : template.getGrowthDex();
    }


    public int getAppLeadership() {
        return leadership;
    }

    public int getAppStrength() {
        return strength;
    }

    public float getAppGrStrength() {
        return (isPromoted()) ? template.getProGrowthStr() : template.getGrowthStr();
    }

    public int getAppArmor(Data.DamageType damageType) {
        return getArmor(damageType);
    }

    public float getAppGrArmor(DamageType damageType) {
        float growthrate = 0;
        switch(damageType){
            case BLUNT: growthrate = (isPromoted()) ? template.getProGrowthBluntArmor(): template.getGrowthBluntArmor(); break;
            case EDGED: growthrate = (isPromoted()) ? template.getProGrowthEdgedArmor(): template.getGrowthEdgegArmor();break;
            case PIERCING: growthrate = (isPromoted()) ? template.getProGrowthPiercingArmor(): template.getGrowthPiercingArmor();break;
        }
        return growthrate;
    }

    public int getAppAgility() {
        return agility;
    }

    public float getAppGrAgility() {
        return (isPromoted()) ? template.getProGrowthAg() : template.getGrowthAg();
    }

    public int getAppDexterity() {
        return dexterity;
    }

    public float getAppGrDexterity() {
        return (isPromoted()) ? template.getProGrowthDex() : template.getGrowthDex();
    }

    public int getAppSkill() {
        return skill;
    }

    public float getAppGrSkill() {
        return (isPromoted()) ? template.getProGrowthSk() : template.getGrowthSk();
    }

    public int getAppBravery() {
        return bravery;
    }


    public float getAppGrBravery() {
        return (isPromoted()) ? template.getProGrowthBr() : template.getGrowthBr();
    }

    public int getAppMobility() {
        return mobility;
    }


    public void addNativeAbility(Data.Ability ability) {
        if(!nativeAbilities.contains(ability, true))
            nativeAbilities.add(ability);
    }

    public boolean has(Data.Ability ability) {
        boolean hasAbility = false;
        for(int i = 0; i < equipments.size; i++){
            if(equipments.get(i).getTemplate().getAbility() == ability){
                hasAbility = true;
                break;
            }
        }
        if(!hasAbility){
            for(int i = 0; i < weapons.size; i++){
                if(weapons.get(i).getTemplate().getAbility() == ability){
                    hasAbility = true;
                    break;
                }
            }
        }
        if(!hasAbility){
            for(int i = 0; i < nativeAbilities.size; i++){
                if(nativeAbilities.get(i) == ability){
                    hasAbility = true;
                    break;
                }
            }
        }
        return hasAbility;
    }


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
        for(int i = 0; i < nativeAbilities.size; i++){
            ability = nativeAbilities.get(i);
            if(!abilities.contains(ability, true))
                abilities.add(ability);
        }
        return abilities;
    }

    public boolean has(Equipment item) {
        return equipments.contains(item, true);
    }


    public boolean addEquipment(Equipment item) {
        boolean itemAdded = false;
        if(!equipments.contains(item, true) && equipments.size < ((isPromoted()) ? Data.MAX_ITEM_CARRIED_UPON_PROMOTION: Data.MAX_ITEM_CARRIED)){
            equipments.add(item);
            itemAdded = true;
        }
        return itemAdded;
    }


    public Array<Equipment> disequipAllEquipment() {
        Array<Equipment> removedItems = equipments;
        this.equipments = new Array<Equipment>();
        return removedItems;
    }

    public Equipment removeEquipment(int index) {
        Equipment item = null;
        if(0 <= index && index < equipments.size ){
            item = equipments.removeIndex(index);
        }
        return item;
    }


    public Equipment replaceEquipment(int index, Equipment item) {
        Equipment olditem = null;
        if(0 <= index && index < equipments.size ){
            olditem = equipments.removeIndex(index);
            equipments.insert(index, item);
        }
        return olditem;
    }

    public boolean isStealable() {
        boolean stealable = false;
        if(!has(Data.Ability.VIGILANT)) {
            for (int i = 0; i < equipments.size; i++) {
                if (equipments.get(i).isStealable()) {
                    stealable = true;
                    break;
                }
            }
            if (!stealable) {
                for (int i = 0; i < weapons.size; i++) {
                    if (weapons.get(i).isStealable()) {
                        stealable = true;
                        break;
                    }
                }
            }
        }
        return stealable;
    }


    public Item getRandomlyStealableItem() {
        return getStealableItems().random();
    }


    public Item getRandomlyDroppableItem() {
        Item droppedItem = null;

        if(!weapons.contains(Weapon.FIST, true)|| equipments.size > 0) {
            int dropRange = 0;
            for (int i = 0; i < weapons.size; i++) { dropRange += weapons.get(i).getDropRate(); }
            for (int i = 0; i < equipments.size; i++) { dropRange += equipments.get(i).getDropRate(); }

            int pick = 1 + Data.rand(dropRange);
            dropRange = 0;
            for (int i = 0; i < weapons.size; i++) {
                dropRange += weapons.get(i).getDropRate();
                if(pick <= dropRange) {
                    droppedItem = removeWeapon(i);
                    break;
                }
            }
            if(droppedItem == null) {
                for (int i = 0; i < equipments.size; i++) {
                    dropRange += equipments.get(i).getDropRate();
                    if(pick <= dropRange) {
                        droppedItem = removeEquipment(i);
                        break;
                    }
                }
            }
        }
        return droppedItem;
    }


    public Array<Item> getStealableItems() {
        Array<Item> stealableItems = new Array<Item>();
        for(int i =0; i < weapons.size; i++){
            if(weapons.get(i).isStealable()){
                stealableItems.add(weapons.get(i));
            }
        }
        for(int i =0; i < equipments.size; i++){
            if(equipments.get(i).isStealable()){
                stealableItems.add(equipments.get(i));
            }
        }
        return stealableItems;
    }


    public int getAppWeaponRangeMin() {
        return getCurrentWeapon().getTemplate().getRangeMin();
    }


    public int getAppWeaponRangeMax() {
        return getCurrentWeapon().getTemplate().getRangeMax();
    }


    public int getCurrentWeaponRangeMin(int rowUnit, int colUnit, Battlefield battlefield) {
        return getCurrentWeapon().getTemplate().getRangeMin();
    }


    public int getCurrentWeaponRangeMax(int rowUnit, int colUnit, Battlefield battlefield) {
        int rangeMax = getCurrentWeapon().getTemplate().getRangeMax();
        if(battlefield.isTileExisted(rowUnit, colUnit)){
            if(rangeMax > 1) {
                TileType tileType = battlefield.getTile(rowUnit, colUnit).getType();
                if(tileType.enhanceRange()) rangeMax++;
                if(battlefield.isStandardBearerAtRange(this, rowUnit, colUnit)){
                    rangeMax += getArmy().getSquadBanner(this, true).getValue(Data.BannerBonus.RANGE, true);
                }
            }
        }
        return rangeMax;
    }


    public int getAppAttackAccuracy() {
        return getCurrentWeapon().getTemplate().getAccuracy() + Data.DEX_FACTOR_ATT_ACC * getAppDexterity() + Data.WC_CHARISMA_BONUS_ATT_ACC* getChiefCharisma();
    }


    public int[] getAppAttackMight() {
        return new int[]{getCurrentWeapon().getTemplate().getDamageMin() + getAppStrength(), getCurrentWeapon().getTemplate().getDamageMax() + getAppStrength()};
    }

    public int getAppDefense(DamageType damageType){
        return getAppArmor(damageType);
    }


    public int getAppAvoidance() {
        return Data.DEX_FACTOR_AVO * getAppAgility();
    }

    public void resetActionPoints() {
        this.actionPoints = getAppSkill();
    }

    public void addActionPoints(int points) {
        this.actionPoints += points;
        if(actionPoints < 0 )
            actionPoints = 0;

    }

    public boolean isMobilized() {
        return (army != null) && getArmy().isUnitMobilized(this);
    }


    public boolean isWarChief() {
        return isMobilized() && army.getWarChiefs().contains(this, true);
    }


    public boolean isWarlord() {
        return isMobilized() && this == army.getWarlord();
    }


    public int getMaxSoldiersAs(boolean warlord) {
        int maxSoldiers;
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

    public int getMaxWarChiefs() {
        int maxWC = this.leadership / 6;
        if(maxWC > 3 )
            maxWC = 3;
        return maxWC + 1;
    }


    public boolean isAllyWith(Data.Affiliation affiliation) {
        return (army != null) && army.getAffiliation() == affiliation;
    }


    public Array<Unit> getSquad(boolean stillFighting) {
        return (army != null) ? army.getSquad(this, stillFighting) : new Array<Unit>();
    }

    public boolean sameSquadAs(Unit unit) {
        if(army != null){
            Array<Unit> squad =  army.getSquad(unit, false);
            for(int i = 0; i < squad.size; i++){
                if(squad.get(i) == this){
                    return true;
                }

            }
        }
        return false;
    }


    public boolean sameArmyAs(Unit unit) {
        return army != null && unit.getArmy() != null && this.army == unit.getArmy();
    }

    public Unit getWarchief() {
        if(isMobilized())
            return army.getWarchief(this);
        return null;
    }


    public int getChiefCharisma() {
        int chiefCharisma = 0;
        if(isMobilized() && !army.getWarchief(this).isOutOfAction()){
            chiefCharisma = army.getWarchief(this).getAppCharisma() - Data.SQUAD_SIZE_EXCEEDANCE_CHA_MALUS * getArmy().getSquadExceedingCapacity(this);
            if(chiefCharisma < 0 )
                chiefCharisma = 0;
        }
        return chiefCharisma;
    }


    public void replenishMoral(boolean turnBeginning) {
        if(turnBeginning) {
            if(!this.isOutOfAction()) {
                this.setCurrentMoral(this.getCurrentMoral() + getChiefCharisma() + getAppBravery() / Data.BRAVERY_MORAL_RECOVERY_RATE);
            }
        }else{
            resetCurrentMoral();
        }
    }


    public int getSquadIndex() {
        int squadIndex = -1;
        if(isMobilized()){
            loop :
            {
                for (int i = 0; i < army.getAllSquads().size; i++) {
                    for (int j = 0; j < army.getAllSquads().get(i).size; j++) {
                        if (army.getAllSquads().get(i).get(j) == this) {
                            squadIndex = i;
                            break loop;
                        }
                    }
                }
            }
        }
        return squadIndex;
    }

    public boolean isWounded(boolean morally, boolean physically) {
        return (currentHitPoints < getAppHitpoints() && physically) || (currentMoral  < getAppMoral() && morally);
    }

    public boolean isOutOfAction() {
        return currentMoral == 0 || currentHitPoints == 0;
    }

    public boolean isDead() {
        return currentHitPoints == 0;
    }

    public boolean isDone() {
        return acted && moved;
    }

    public boolean isCharacter(){
        return this instanceof CharacterUnit;
    }


    public int getRecoveredHitPoints(int healPower) {
        int recoveredHP ;
        if(healPower + hitPoints > getAppHitpoints()){
            recoveredHP = getAppHitpoints();
        }else{
            recoveredHP = healPower;
        }
        return recoveredHP;
    }


    public int getRecoveredMoralPoints(int healPower) {
        int recoveredMoralPoints;
        if(healPower + hitPoints > getAppMoral()){
            recoveredMoralPoints = getAppMoral();
        }else{
            recoveredMoralPoints = healPower;
        }
        return recoveredMoralPoints;
    }


    public boolean improveCondition(int healPower, boolean boostMoral, boolean boostPhysicalCondition) {
        if(isWounded(boostMoral, boostPhysicalCondition)) {
            if(boostPhysicalCondition) {
                if (healPower + hitPoints > getAppHitpoints()) {
                    this.currentHitPoints = getAppHitpoints();
                } else {
                    this.currentHitPoints += getAppHitpoints();
                }
            }

            if(boostMoral) {
                if (healPower + currentMoral > getAppMoral()) {
                    this.currentMoral = getAppMoral();
                } else {
                    this.currentMoral += healPower;
                }
            }
            return true;
        }
        return false;
    }


    public TakeDamage takeDamage(int damageDealt, boolean ignorePhysicalDamage, boolean ignoreMoralDamage, float moralModifier){
        TakeDamage.State state = TakeDamage.State.UNDAMAGED;
        int lifeDamageTaken = 0;

        // moral damaga
        if (!has(Data.Ability.UNBREAKABLE) && !ignoreMoralDamage) {
            if(this.currentMoral > damageDealt * moralModifier) {
                state = TakeDamage.State.WOUNDED;
                this.currentMoral -= damageDealt * moralModifier;
            }else{
                state = TakeDamage.State.FLED;
                this.currentMoral = 0;

            }
        }

        // physical damage
        if(!ignorePhysicalDamage){
            if(this.currentHitPoints > damageDealt) {
                lifeDamageTaken = damageDealt;
                this.currentHitPoints -= damageDealt;
            }else{
                state = TakeDamage.State.DIED;
                lifeDamageTaken = this.currentHitPoints;
                this.currentHitPoints = 0;


            }
        }

        return new TakeDamage(this, ignorePhysicalDamage, ignoreMoralDamage, damageDealt, lifeDamageTaken, state);
    }





    //------------------------- GETTERS & SETTERS --------------------


    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }
     
    public Data.UnitTemplate getTemplate() {
        return template;
    }
     
    public Data.WeaponType getWeaponType() {
        return weaponType;
    }

    public boolean isShielbearer() { return shielbearer; }

    public boolean isHorseman() {
        return horseman;
    }

    public void setHorseman(boolean horseman) { this.horseman = horseman; }

    public boolean isHorsemanUponPromotion() { return horsemanUponPromotion; }


    public void setHorsemanUponPromotion(boolean horseman) {
        this.horsemanUponPromotion = horseman;
        if(isPromoted())
            this.horseman = horseman;
    }

    public boolean isRightHanded() {
        return rightHanded;
    }
     
    public boolean setRightHanded(boolean righthanded) {
        return this.rightHanded = righthanded;
    }
     

    public Array<Weapon> getWeapons() {
        return weapons;
    }
     
    public int getExperience() {
        return experience;
    }

    /**
     *
     * @param experience
     * @return an array of exp gained between each level up (ex: 255 = [100, 100, 55]
     */
     
    public int[] setExperience(int experience) {
        // get the number of lvl gained and the resulting exp array
        int levelsGained = experience / Data.EXP_REQUIRED_LD_LEVEL_UP;
        int[] exps = new int[ 1 + levelsGained];

        // fill the exps array
        for(int i = 0; i < exps.length; i++)
            exps[i] = Data.EXP_REQUIRED_LD_LEVEL_UP;
        exps[exps.length - 1] = experience % Data.EXP_REQUIRED_LEVEL_UP;

        // set the new experience value
        this.experience = exps[exps.length - 1];

        return exps;
    }

     
    public int getLeadershipExperience() {
        return commandmentExperience;
    }


    public void setLeadershipExperience(int experience) {
        this.commandmentExperience = experience % Data.EXP_REQUIRED_LD_LEVEL_UP;
    }

    public MilitaryForce getArmy() {
        return army;
    }

    public void setArmy(MilitaryForce army) {
        this.army = army;
    }


    public int getMobility() {
        return mobility;
    }

    public int getCharisma() {
        return charisma;
    }

    public int getLeadership() {
        return leadership;
    }

    public void setLeadership(int leadership) {
        this.leadership = leadership;
    }

    public int getHitpoints() {
        return hitPoints;
    }
     
    public int getStrength() {
        return strength;
    }

    public int getArmor(Data.DamageType damageType) {
        int armor = 0;
        switch(damageType){
            case BLUNT: armor = bluntArmor; break;
            case EDGED: armor = edgedArmor; break;
            case PIERCING: armor = piercinfArmor;break;
        }
        return armor;
    }

    public int getDexterity() {
        return dexterity;
    }
     
    public int getAgility() {
        return agility;
    }

    public int getSkill() {
        return skill;
    }

    public int getBravery() {
        return bravery;
    }

    public int getCurrentHitPoints() {
        return currentHitPoints;
    }

    public void setCurrentHitPoints(int hitPoints) {
        if(0 <= hitPoints){
            this.currentHitPoints = ( hitPoints > getAppHitpoints())? getAppHitpoints() : hitPoints;
        }
    }


    public int getCurrentMoral() {
        return currentMoral;
    }


    public void setCurrentMoral(int moral) {
        if(0 <= moral){
            this.currentMoral = ( moral > getAppMoral())? getAppMoral() : moral;
        }
    }

    public void setActionPoints(int ap) {
        this.actionPoints = (ap > 0) ? ap : 0;
    }

    public int getActionPoints() {
        return this.actionPoints;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled)  {
        this.disabled = disabled;
    }

    public boolean isCrippled() {
        return crippled;
    }


    public void setCrippled(boolean crippled) {
        this.crippled = crippled;
    }

    public Array<Equipment> getEquipments() {
        return equipments;
    }

    public Banner getBanner() {
        return banner;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public void setBehaviour(Data.Behaviour behaviour) {
        this.behaviour = behaviour;
    }

     
    public Behaviour getBehaviour() {
        return behaviour;
    }

    public boolean isActed() {
        return acted;
    }

     
    public boolean isMoved() {
        return moved;
    }

    public void setActed(boolean acted) {
        this.acted = acted;
    }

     
    public void setMoved(boolean moved) {
        this.moved = moved;
    }

     
    public String toString() {
        return getName();
    }

    public String statToString(boolean apparent){
        String str = "\nname : "+getName();

        str +="\nhitpoints  : ";
        str += (apparent) ? getAppHitpoints() : getHitpoints();
        str +="\nbravery    : ";
        str += (apparent) ? getAppBravery() : getBravery();
        str +="\ncharisma   : ";
        str += (apparent) ? getAppCharisma() : getCharisma();
        str +="\ndexterity: : ";
        str += (apparent) ? getAppDexterity() : getDexterity();
        str +="\nskill      : ";
        str += (apparent) ? getAppSkill() : getSkill();
        str +="\n_________________: ";
        str += "\nstrength  : ";
        str += (apparent) ? getAppStrength() : getStrength();
        str +="\ndefense    ";
        str +="\n   piercing : ";
        str += (apparent) ? getAppArmor(DamageType.PIERCING) : getArmor(DamageType.PIERCING);
        str +="\n   blunt    : ";
        str += (apparent) ? getAppArmor(DamageType.BLUNT) : getArmor(DamageType.BLUNT);
        str +="\n   edged : ";
        str += (apparent) ? getAppArmor(DamageType.EDGED) : getArmor(DamageType.EDGED);
        str +="\nagility    : ";
        str += (apparent) ? getAppAgility() : getAgility();

        return str;
    }



    static class CharacterUnit extends Unit{
        private String title;

        public CharacterUnit(String name, String title, UnitTemplate template, int level, WeaponType weaponType, boolean shielbearer, boolean horseman, boolean horsemanUponPromotion, boolean homogeneousLevelsup) {
            super(name, template, level, weaponType, shielbearer, horseman, horsemanUponPromotion, homogeneousLevelsup);
            this.title = title;
        }

        public CharacterUnit(String name, UnitTemplate template, WeaponType weaponType){
            super(name, template, weaponType);
            this.title = "no title";

        }
         
        public String getTitle(I18NBundle bundle) {
            return bundle.get(title);
        }
    }
}
