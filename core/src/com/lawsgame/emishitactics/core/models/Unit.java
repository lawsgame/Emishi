package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.Data.Behaviour;
import com.lawsgame.emishitactics.core.models.Data.DamageType;
import com.lawsgame.emishitactics.core.models.Data.UnitTemplate;
import com.lawsgame.emishitactics.core.models.Data.Orientation;
import com.lawsgame.emishitactics.core.models.Data.TileType;
import com.lawsgame.emishitactics.core.models.Data.WeaponType;
import com.lawsgame.emishitactics.core.models.Notification.ApplyDamage;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Item;

public class Unit extends IUnit{

    protected String name;
    protected int level;
    protected UnitTemplate template;
    protected WeaponType weaponType;
    protected boolean character;
    protected boolean shielbearer;
    protected boolean horseman;
    protected boolean horsemanUponPromotion;

    protected Array<Weapon> weapons;
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
    protected int actionPoints = 0;

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
     * Can be build as well:
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
            boolean character,
            boolean shielbearer,
            boolean horseman,
            boolean horsemanUponPromotion,
            boolean homogeneousLevelsup){

        this.name = name;
        this.template = template;
        this.level = template.getStartingLevel();
        this.character = character;
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
        this.banner = new Banner();

        if(homogeneousLevelsup) {
            growup(level);
        }else{
            for(int lvl = template.getStartingLevel(); lvl < level; lvl++){
                levelup();
            }
        }

        this.currentHitPoints = getAppHitpoints();
        resetCurrentMoral();
    }

    public Unit(String name){
        this(name, Data.UnitTemplate.getStandard(), 10, WeaponType.SWORD, false, false, false, false, true);
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
        return new Unit(name, template, level, weaponType, false, shielbearer, horseman, horsemanUponPromotion, homogeneousLevelsup);
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
        return new CharacterUnit(name, title, template, level, weaponType, false, shielbearer, horseman, horsemanUponPromotion, true);
    }

    @Override
    public String getName(I18NBundle bundle) {
        return bundle.get(name);
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
    public Data.UnitTemplate getTemplate() {
        return template;
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
    public boolean isCharacter() {
        return character;
    }

    @Override
    public boolean isStandardBearer() {
        return isWarChief();
    }

    @Override
    public boolean isHorseman() {
        return horseman;
    }

    @Override
    public boolean isShielbearer() { return shielbearer; }

    @Override
    public void setHorseman(boolean horseman) { this.horseman = horseman; }

    @Override
    public boolean isHorsemanUponPromotion() { return horsemanUponPromotion; }

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
    public String getTitle(I18NBundle bundle) {
        return template.getName(bundle);
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
        if(0 <= index && index < weapons.size) {
            weaponToreturn = weapons.removeIndex(index);
        }
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
        weapons.add(Weapon.FIST);
        weapons.removeValue(Weapon.FIST, true);
        return removedWeapons;
    }

    @Override
    public Array<Weapon> getWeapons() {
        return weapons;
    }

    @Override
    public Weapon getCurrentWeapon() {
        return weapons.get(0);
    }

    @Override
    public boolean switchWeapon(int index) {
        if (0 < index && index < weapons.size) {
            weapons.swap(0, index);
            return true;
        }
        return false;
    }

    @Override
    public Weapon getWeapon(int index) {
        Weapon weapon = null;
        if (0 < index && index < weapons.size) {
            weapon = weapons.get(index);
        }
        return weapon;
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
    public float getAppGrHitpoints() {
        return  (isPromoted()) ? template.getGetProGrowthHP() : template.getGrowthHP();
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
        int[] gainLvl = new int[13];
        int tempoExp = exp;
        int[] tempoGainLvl;
        while(level < Data.MAX_LEVEL && tempoExp > 0) {
            if (this.experience + tempoExp < Data.EXP_REQUIRED_LEVEL_UP) {
                this.experience += tempoExp;
                tempoExp = 0;
            } else {
                tempoExp -= Data.EXP_REQUIRED_LD_LEVEL_UP - this.experience;
                this.experience = 0;
                tempoGainLvl = levelup();
                gainLvl[12] = 1;
                for(int i = 0; i < tempoGainLvl.length; i++)
                    gainLvl[i] += tempoGainLvl[i];
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
    public float getAppGrCharisma() {
        return (isPromoted()) ? template.getProGrowthDex() : template.getGrowthDex();
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
    public float getAppGrStrength() {
        return (isPromoted()) ? template.getProGrowthStr() : template.getGrowthStr();
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
    public float getAppGrArmor(DamageType damageType) {
        float growthrate = 0;
        switch(damageType){
            case BLUNT: growthrate = (isPromoted()) ? template.getProGrowthBluntArmor(): template.getGrowthBluntArmor(); break;
            case EDGED: growthrate = (isPromoted()) ? template.getProGrowthEdgedArmor(): template.getGrowthEdgegArmor();break;
            case PIERCING: growthrate = (isPromoted()) ? template.getProGrowthPiercingArmor(): template.getGrowthPiercingArmor();break;
        }
        return growthrate;
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
    public float getAppGrAgility() {
        return (isPromoted()) ? template.getProGrowthAg() : template.getGrowthAg();
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
    public float getAppGrDexterity() {
        return (isPromoted()) ? template.getProGrowthDex() : template.getGrowthDex();
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
    public float getAppGrSkill() {
        return (isPromoted()) ? template.getProGrowthSk() : template.getGrowthSk();
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
    public float getAppGrBravery() {
        return (isPromoted()) ? template.getProGrowthBr() : template.getGrowthBr();
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
            for(int i = 0; i < template.getNativeAbilities().length; i++){
                if(template.getNativeAbilities()[i] == ability){
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
        for(int i = 0; i < template.getNativeAbilities().length; i++){
            ability = template.getNativeAbilities()[i];
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




    // --------------- LOOT & STEAL ----------------------

    @Override
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

    @Override
    public Item getRandomlyStealableItem() {
        return getStealableItems().random();
    }

    @Override
    public Item getRandomlyDroppableItem() {
        Item droppedItem = null;

        if(!weapons.contains(Weapon.FIST, true)|| equipments.size > 0 || (isStandardBearer() && banner.isDroppable())) {
            int dropRange = 0;
            for (int i = 0; i < weapons.size; i++) {
                dropRange += weapons.get(i).getDropRate();
            }
            for (int i = 0; i < equipments.size; i++) {
                dropRange += equipments.get(i).getDropRate();
            }
            for(int i = 0; i < banner.getBannerSigns().size; i++){
                dropRange += banner.getBannerSigns().get(i).getDropRate();
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
            if(droppedItem == null) {
                for (int i = 0; i < banner.getBannerSigns().size; i++) {
                    dropRange += banner.getBannerSigns().get(i).getDropRate();
                    if(pick <= dropRange) {
                        droppedItem = banner.removeSign(i, false);
                        continue;
                    }
                }
            }
        }
        return droppedItem;
    }

    @Override
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
        for(int i =0; i < banner.getBannerSigns().size; i++){
            if(banner.getBannerSigns().get(i).isStealable()){
                stealableItems.add(banner.getBannerSigns().get(i));
            }
        }
        return stealableItems;
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
    public void setActionPoints(int barProgression) {
        this.actionPoints = barProgression % Data.MAX_ACTION_POINTS;
    }

    @Override
    public void addActionPoints(int points) {
        this.actionPoints += points;
        if(actionPoints > Data.MAX_ACTION_POINTS) actionPoints = Data.MAX_ACTION_POINTS;
        if(actionPoints < 0 ) actionPoints = 0;
    }

    @Override
    public int getActionPoints() {
        return this.actionPoints;
    }

    @Override
    public int getAppAPRecoveryRate() {
        return getAppSkill();
    }

    @Override
    public int getCurrentAPRecoveryRate(int rowUnit, int colUnit, Battlefield battlefield) {
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
    public boolean isAllyWith(Data.Affiliation affiliation) {
        return (army != null) && army.getAffiliation() == affiliation;
    }

    @Override
    public Array<IUnit> getSquad(boolean stillFighting) {
        return (army != null) ? army.getSquad(this, stillFighting) : new Array<IUnit>();
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
    public int getRecoveredHitPoints(int healPower) {
        int recoveredHP ;
        if(healPower + hitPoints > getAppHitpoints()){
            recoveredHP = getAppHitpoints();
        }else{
            recoveredHP = healPower;
        }
        return recoveredHP;
    }

    @Override
    public int getRecoveredMoralPoints(int healPower) {
        int recoveredMoralPoints;
        if(healPower + hitPoints > getAppMoral()){
            recoveredMoralPoints = getAppMoral();
        }else{
            recoveredMoralPoints = healPower;
        }
        return recoveredMoralPoints;
    }

    @Override
    public boolean treated(int healPower) {
        if(isWounded()) {
            if (healPower + hitPoints > getAppHitpoints()) {
                this.currentHitPoints = getAppHitpoints();
            }else{
                this.currentHitPoints += getAppHitpoints();
            }

            if(healPower + currentMoral > getAppMoral()){
                this.currentMoral = getAppMoral();
            }else{
                this.currentMoral += healPower;
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
            if(!has(Data.Ability.UNBREAKABLE)) {
                notification.state = ApplyDamage.State.WOUNDED;
                this.currentMoral -= damageTaken;
            }
            if(!moralDamageOnly) {
                notification.state = ApplyDamage.State.WOUNDED;
                this.currentHitPoints -= damageTaken;
            }


        }else{
            // the unit dies or flies
            if(this.currentHitPoints > damageTaken){
                if(!has(Data.Ability.UNBREAKABLE)) {
                    this.currentMoral = 0;
                    notification.state = ApplyDamage.State.FLED;
                }
                if(!moralDamageOnly)
                    this.currentHitPoints -= damageTaken;
            }else{
                if(!has(Data.Ability.UNBREAKABLE)) {
                    this.currentMoral = 0;
                    notification.state = ApplyDamage.State.FLED;
                }
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

    @Override
    public String toString() {
        return getName();
    }

    public String statToString(boolean apparent){
        String str = "\nname : "+getName();

        str +="\nhitpoints  : ";
        str += (apparent) ? getAppHitpoints() : getBaseHitpoints();
        str +="\nbravery    : ";
        str += (apparent) ? getAppBravery() : getBaseBravery();
        str +="\ncharisma   : ";
        str += (apparent) ? getAppCharisma() : getBaseCharisma();
        str +="\ndexterity: : ";
        str += (apparent) ? getAppDexterity() : getBaseDexterity();
        str +="\nskill      : ";
        str += (apparent) ? getAppSkill() : getBaseSkill();
        str +="\n_________________: ";
        str += "\nstrength  : ";
        str += (apparent) ? getAppStrength() : getBaseStrength();
        str +="\ndefense    ";
        str +="\n   piercing : ";
        str += (apparent) ? getAppArmor(DamageType.PIERCING) : getBaseArmor(DamageType.PIERCING);
        str +="\n   blunt    : ";
        str += (apparent) ? getAppArmor(DamageType.BLUNT) : getBaseArmor(DamageType.BLUNT);
        str +="\n   edged : ";
        str += (apparent) ? getAppArmor(DamageType.EDGED) : getBaseArmor(DamageType.EDGED);
        str +="\nagility    : ";
        str += (apparent) ? getAppAgility() : getBaseAgility();

        return str;
    }



    static class CharacterUnit extends Unit{
        private String title;

        public CharacterUnit(String name, String title, UnitTemplate template, int level, WeaponType weaponType, boolean character, boolean shielbearer, boolean horseman, boolean horsemanUponPromotion, boolean homogeneousLevelsup) {
            super(name, template, level, weaponType, character, shielbearer, horseman, horsemanUponPromotion, homogeneousLevelsup);
            this.title = title;
        }

        @Override
        public String getTitle(I18NBundle bundle) {
            return bundle.get(title);
        }
    }
}
