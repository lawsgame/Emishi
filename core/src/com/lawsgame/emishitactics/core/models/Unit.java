package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.Data.Behaviour;
import com.lawsgame.emishitactics.core.models.Data.UnitStat;
import com.lawsgame.emishitactics.core.models.Data.DamageType;
import com.lawsgame.emishitactics.core.models.Data.Orientation;
import com.lawsgame.emishitactics.core.models.Data.UnitTemplate;
import com.lawsgame.emishitactics.core.models.Data.WeaponType;
import com.lawsgame.emishitactics.core.models.Notification.TakeDamage;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.models.interfaces.MilitaryForce;
import com.lawsgame.emishitactics.core.models.interfaces.Model;

import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.Stack;

public class Unit extends Model {

    protected final String name;
    protected int level;
    protected final UnitTemplate template;
    protected final WeaponType weaponType;
    protected boolean horseman;
    protected boolean horsemanUponPromotion;

    protected Array<Weapon> weapons;
    protected int experience = 0;
    protected int commandmentExperience = 0;
    private MilitaryForce army = null;

    protected HashMap<Data.UnitStat, Integer> baseStats;
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
            boolean horseman,
            boolean horsemanUponPromotion,
            boolean homogeneousLevelsup){

        this.name = name;
        this.template = template;
        this.level = template.getStartingLevel();
        this.weaponType = weaponType;
        this.horseman = horseman;
        this.horsemanUponPromotion = horsemanUponPromotion;

        baseStats = new HashMap<UnitStat, Integer>();
        for(UnitStat stat : UnitStat.values()){
            baseStats.put(stat, template.getBaseStat(stat));
        }

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

        this.currentHitPoints = getAppStat(UnitStat.HIT_POINTS);
        resetCurrentMoral();
        resetActionPoints();
    }

    public Unit(String name, UnitTemplate template, WeaponType weaponType){
        this(name, template, template.getStartingLevel(), weaponType, false, false, true);
    }

   public static Unit createGenericUnit(
           String name,
           UnitTemplate template,
           int level,
           WeaponType weaponType,
           boolean horseman,
           boolean horsemanUponPromotion,
           boolean homogeneousLevelsup){
        return new Unit(name, template, level, weaponType, horseman, horsemanUponPromotion, homogeneousLevelsup);
   }

    public static Unit createCharacterUnit(
            String name,
            String title,
            UnitTemplate template,
            int level,
            WeaponType weaponType,
            boolean horseman,
            boolean horsemanUponPromotion){
        return new CharacterUnit(name, title, template, level, weaponType, horseman, horsemanUponPromotion, true);
    }


    //------------------------- PROCESSING -------------------------

    public String getName(I18NBundle bundle) {
        return bundle.get(name);
    }


    public int[] levelup(){
        int[] gainedStats = new int[UnitStat.values().length];
        if(this.level < Data.MAX_LEVEL) {
            this.level++;
            float growthRate;
            UnitStat stat;
            if (Data.PROMOTION_LEVEL != level ) {
               for(int i = 0; i < UnitStat.values().length; i++){
                   stat = UnitStat.values()[i];
                   if(stat != UnitStat.LEADERSHIP) {
                       growthRate = (template.getStatGrowth(stat, isPromoted()));
                       if (MathUtils.random() < growthRate) {
                           baseStats.put(stat, baseStats.get(stat) + 1);
                           gainedStats[i] = 1;
                       }
                   }
               }
            }else{
                this.horseman = horsemanUponPromotion;
                int bonusFromPromotion;
                for(int i = 0; i < UnitStat.values().length; i++){
                    stat = UnitStat.values()[i];
                    if(stat != UnitStat.LEADERSHIP) {
                        bonusFromPromotion = template.getProBoStat(stat);
                        baseStats.put(stat, baseStats.get(stat) + bonusFromPromotion);
                        gainedStats[i] = bonusFromPromotion;
                    }
                }
            }
        }
        //new int[]{ (int)mob, (int)cha, (int)hpt, (int)bra, (int)str, (int)armorP, (int)armorB, (int)armorE, (int)agi, (int)dex, (int)ski, (int)luc};
        return gainedStats;
    }

    public int[] growup(int upto){
        int gainLvl = upto - getLevel();

        float mob = 0;
        float cha = 0;
        float hpt = 0;
        float str = 0;
        float armorE = 0;
        float armorP = 0;
        float armorB = 0;
        float dex = 0;
        float agi = 0;
        float ski = 0;
        float bra = 0;
        float luc = 0;


        if(gainLvl > 0){
            int gainBeforePromotion = (Data.PROMOTION_LEVEL <= upto) ? Data.PROMOTION_LEVEL - getLevel() - 1 : upto - getLevel();
            int gainAfterPromotion = upto - Data.PROMOTION_LEVEL;

            if(gainBeforePromotion > 0){
                cha += template.getStatGrowth(UnitStat.CHARISMA, false) * gainBeforePromotion;
                hpt += template.getStatGrowth(UnitStat.HIT_POINTS, false) * gainBeforePromotion;
                bra += template.getStatGrowth(UnitStat.BRAVERY, false)  * gainBeforePromotion;
                str += template.getStatGrowth(UnitStat.STRENGTH, false) * gainBeforePromotion;
                armorE += template.getStatGrowth(UnitStat.ARMOR_EDGED, false)  * gainBeforePromotion;
                armorB += template.getStatGrowth(UnitStat.ARMOR_BLUNT, false)  * gainBeforePromotion;
                armorP += template.getStatGrowth(UnitStat.ARMOR_PIERCING, false)  * gainBeforePromotion;
                agi += template.getStatGrowth(UnitStat.AGILITY, false) * gainBeforePromotion;
                dex += template.getStatGrowth(UnitStat.DEXTERITY, false)  * gainBeforePromotion;
                ski += template.getStatGrowth(UnitStat.SKILL, false) * gainBeforePromotion;
                luc += template.getStatGrowth(UnitStat.LUCK, false) * gainBeforePromotion;
            }

            if(getLevel() < Data.PROMOTION_LEVEL && Data.PROMOTION_LEVEL <= upto){

                mob = template.getProBoStat(UnitStat.MOBILITY);
                cha = template.getProBoStat(UnitStat.CHARISMA);
                hpt = template.getProBoStat(UnitStat.HIT_POINTS);
                str = template.getProBoStat(UnitStat.STRENGTH);
                bra = template.getProBoStat(UnitStat.BRAVERY);
                armorP = template.getProBoStat(UnitStat.ARMOR_PIERCING);
                armorE = template.getProBoStat(UnitStat.ARMOR_EDGED);
                armorB = template.getProBoStat(UnitStat.ARMOR_BLUNT);
                agi = template.getProBoStat(UnitStat.AGILITY);
                dex = template.getProBoStat(UnitStat.DEXTERITY);
                ski = template.getProBoStat(UnitStat.SKILL);
                luc = template.getProBoStat(UnitStat.LUCK);
            }

            if(gainAfterPromotion > 0){

                cha += template.getStatGrowth(UnitStat.CHARISMA, true) * gainAfterPromotion;
                hpt += template.getStatGrowth(UnitStat.HIT_POINTS, true) * gainAfterPromotion;
                bra += template.getStatGrowth(UnitStat.BRAVERY, true)  * gainAfterPromotion;
                str += template.getStatGrowth(UnitStat.STRENGTH, true) * gainAfterPromotion;
                armorE += template.getStatGrowth(UnitStat.ARMOR_EDGED, true)  * gainAfterPromotion;
                armorB += template.getStatGrowth(UnitStat.ARMOR_BLUNT, true)  * gainAfterPromotion;
                armorP += template.getStatGrowth(UnitStat.ARMOR_PIERCING, true)  * gainAfterPromotion;
                dex += template.getStatGrowth(UnitStat.DEXTERITY, true)  * gainAfterPromotion;
                agi += template.getStatGrowth(UnitStat.AGILITY, true) * gainAfterPromotion;
                ski += template.getStatGrowth(UnitStat.SKILL, true) * gainAfterPromotion;
                luc += template.getStatGrowth(UnitStat.LUCK, true) * gainAfterPromotion;
            }

            this.level = upto;

            baseStats.put(UnitStat.MOBILITY, baseStats.get(UnitStat.MOBILITY) + (int)mob);
            baseStats.put(UnitStat.CHARISMA, baseStats.get(UnitStat.CHARISMA) + (int)cha);
            baseStats.put(UnitStat.HIT_POINTS, baseStats.get(UnitStat.HIT_POINTS) + (int)hpt);
            baseStats.put(UnitStat.BRAVERY, baseStats.get(UnitStat.BRAVERY) + (int)bra);
            baseStats.put(UnitStat.STRENGTH, baseStats.get(UnitStat.STRENGTH) + (int)str);
            baseStats.put(UnitStat.ARMOR_PIERCING, baseStats.get(UnitStat.ARMOR_PIERCING) + (int)armorP);
            baseStats.put(UnitStat.ARMOR_EDGED, baseStats.get(UnitStat.ARMOR_EDGED) + (int)armorE);
            baseStats.put(UnitStat.ARMOR_BLUNT, baseStats.get(UnitStat.ARMOR_BLUNT) + (int)armorB);
            baseStats.put(UnitStat.DEXTERITY, baseStats.get(UnitStat.DEXTERITY) + (int)dex);
            baseStats.put(UnitStat.AGILITY, baseStats.get(UnitStat.AGILITY) + (int)agi);
            baseStats.put(UnitStat.SKILL, baseStats.get(UnitStat.SKILL) + (int)ski);
            baseStats.put(UnitStat.LUCK, baseStats.get(UnitStat.LUCK) + (int)luc);
        }

        return new int[]{ (int)mob, 0, (int)cha, (int)hpt, (int)bra, (int)str, (int)armorP, (int)armorB, (int)armorE, (int)agi, (int)dex, (int)ski, (int)luc};
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
        if (0 <= index && index < weapons.size) {
            weapon = weapons.get(index);
        }
        return weapon;
    }

    public boolean isStandardBearer() {
        return isWarChief();
    }

    public int getBaseStat(UnitStat stat){
        return baseStats.get(stat);
    }

    public int getAppStat(UnitStat stat){
        return getBaseStat(stat);
    }


    public void resetCurrentMoral() {
        this.currentMoral = getAppMoral();
        if(this.currentMoral > this.currentHitPoints)
            this.currentMoral = currentHitPoints;
    }


    public int getAppMoral() {
        return getAppStat(UnitStat.BRAVERY);
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

    public void setLeadership(int leadership){
        baseStats.put(UnitStat.LEADERSHIP,leadership);
    }

    public int addLdExpPoints(int exp) {
        int ldLevelGained = 0;
        this.commandmentExperience += exp;
        if(commandmentExperience > Data.EXP_REQUIRED_LD_LEVEL_UP){
            ldLevelGained = commandmentExperience / Data.EXP_REQUIRED_LD_LEVEL_UP;
            this.setLeadership(this.getBaseStat(UnitStat.LEADERSHIP) + ldLevelGained);
            this.commandmentExperience = this.commandmentExperience % Data.EXP_REQUIRED_LD_LEVEL_UP;
        }
        return ldLevelGained;
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
            if(weapons.get(i).getTemplate().getAbility() != Data.Ability.NONE){
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


    public int getAppAttackAccuracy() {
        return getCurrentWeapon().getTemplate().getAccuracy() + Data.DEX_FACTOR_ATT_ACC * getAppStat(UnitStat.DEXTERITY) + Data.WC_CHARISMA_BONUS_ATT_ACC* getChiefCharisma();
    }


    public int[] getAppAttackMight() {
        return new int[]{getCurrentWeapon().getTemplate().getDamageMin() + getAppStat(UnitStat.STRENGTH), getCurrentWeapon().getTemplate().getDamageMax() + getAppStat(UnitStat.STRENGTH)};
    }

    public int getAppDefense(DamageType damageType){

        switch(damageType){

            case BLUNT: return getAppStat(UnitStat.ARMOR_BLUNT);
            case EDGED: return getAppStat(UnitStat.ARMOR_EDGED);
            case PIERCING: return getAppStat(UnitStat.ARMOR_PIERCING);
        }
        return 0;
    }


    public int getAppAvoidance() {
        return Data.DEX_FACTOR_AVO * getAppStat(UnitStat.AGILITY);
    }

    public void resetActionPoints() {
        this.actionPoints = getAppStat(UnitStat.SKILL);
    }

    public void addActionPoints(int points) {
        this.actionPoints += points;
        if(actionPoints < 0 ) {
            actionPoints = 0;
        }

    }

    public boolean isMobilized() { return isRegular() || isSkirmisher(); }

    public boolean isRegular(){
        return (army != null && getArmy().isUnitRegular(this));
    }

    public boolean isSkirmisher(){
        return (army != null && getArmy().isUnitSkirmisher(this));
    }

    public boolean isReverse(){
        return (army != null && getArmy().isUnitReserve(this));
    }

    public boolean belongToAnArmy(){
        return army != null;
    }


    public boolean isWarChief() {
        return isRegular() && army.getWarChiefs().contains(this, true);
    }


    public boolean isWarlord() {
        return isRegular() && this == army.getWarlord();
    }


    public int getMaxSoldiersAs(boolean warlord) {
        int maxSoldiers;
        if(warlord){
            maxSoldiers = 2 + (this.getAppStat(UnitStat.LEADERSHIP) + 2) / 5;
            if(maxSoldiers > Data.MAX_UNITS_UNDER_WARLORD){
                maxSoldiers = 5;
            }
        }else{
            maxSoldiers = 1 + (this.getAppStat(UnitStat.LEADERSHIP) + 1) / 5;
            if(maxSoldiers > Data.MAX_UNITS_UNDER_WAR_CHIEF){
                maxSoldiers = 4;
            }
        }
        return maxSoldiers;
    }

    public int getMaxWarChiefs() {
        int maxWC = this.getAppStat(UnitStat.LEADERSHIP) / 6;
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
        return army != null && unit.belongToAnArmy() && this.army == unit.getArmy();
    }

    public Unit getWarchief() {
        return army.getWarchief(this);
    }


    public int getChiefCharisma() {
        int chiefCharisma = 0;
        if(isRegular() && !army.getWarchief(this).isOutOfAction()){
            chiefCharisma = army.getWarchief(this).getAppStat(UnitStat.CHARISMA);
        }
        return chiefCharisma;
    }


    public void replenishMoral(boolean turnBeginning) {
        if(turnBeginning) {
            if(!this.isOutOfAction()) {
                this.setCurrentMoral(this.getCurrentMoral() + getChiefCharisma() + getAppStat(UnitStat.BRAVERY) / Data.BRAVERY_MORAL_RECOVERY_RATE);
            }
        }else{
            resetCurrentMoral();
        }
    }


    public int getSquadIndex() {
        int squadIndex = -1;
        if(isRegular()){
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
        return (currentHitPoints < getAppStat(UnitStat.HIT_POINTS) && physically) || (currentMoral  < getAppMoral() && morally);
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
        if(healPower + currentHitPoints > getAppStat(UnitStat.HIT_POINTS)){
            recoveredHP = getAppStat(UnitStat.HIT_POINTS) - currentHitPoints;
        }else{
            recoveredHP = healPower;
        }
        return recoveredHP;
    }


    public int getRecoveredMoralPoints(int healPower) {
        int recoveredMoralPoints;
        if(healPower + currentMoral > getAppMoral()){
            recoveredMoralPoints = getAppMoral() - currentMoral;
        }else{
            recoveredMoralPoints = healPower;
        }
        return recoveredMoralPoints;
    }


    public boolean improveCondition(int boostMoral, int boostPhysical) {
        if(isWounded(boostMoral > 0, boostPhysical> 0)) {
            if(boostPhysical > 0) {
                if (boostPhysical + currentHitPoints > getAppStat(UnitStat.HIT_POINTS)) {
                    this.currentHitPoints = getAppStat(UnitStat.HIT_POINTS);
                } else {
                    this.currentHitPoints += getAppStat(UnitStat.HIT_POINTS);
                }
            }

            if(boostMoral > 0) {
                if (boostMoral + currentMoral > getAppMoral()) {
                    this.currentMoral = getAppMoral();
                } else {
                    this.currentMoral += boostMoral;
                }
            }
            return true;
        }
        return false;
    }


    public TakeDamage takeDamage(int damageDealt, boolean ignorePhysicalDamage, float moralModifier){
        TakeDamage.State state = TakeDamage.State.UNDAMAGED;
        int lifeDamageTaken = 0;

        // moral damaga
        int moralDamageTaken = (int) (moralModifier*damageDealt);
        if (!has(Data.Ability.UNBREAKABLE) &&  moralDamageTaken > 0) {
            if(this.currentMoral > moralDamageTaken) {
                state = TakeDamage.State.WOUNDED;
                this.currentMoral -= moralDamageTaken;
            }else{
                state = TakeDamage.State.FLED;
                moralDamageTaken = this.currentMoral;
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
        return new TakeDamage(this, lifeDamageTaken, moralDamageTaken, state);
    }

    public TakeDamage kill(boolean notifyObservers) {
        final int lostHP =  currentHitPoints;
        final int lostMoral = currentMoral;
        this.currentHitPoints = 0;
        this.currentMoral = 0;
        TakeDamage takeDamage = new Notification.TakeDamage(
                this,
                lostHP,
                lostMoral,
                TakeDamage.State.DIED
        );
        takeDamage.set(false, false, 0, false, false, getOrientation().getOpposite());
        if(notifyObservers){
            notifyAllObservers(takeDamage);
        }
        return takeDamage;
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

    public int getCurrentHitPoints() {
        return currentHitPoints;
    }

    public void setCurrentHitPoints(int hitPoints) {
        if(0 <= hitPoints){
            this.currentHitPoints = ( hitPoints > getAppStat(UnitStat.HIT_POINTS))? getAppStat(UnitStat.HIT_POINTS) : hitPoints;
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

        for(UnitStat stat: UnitStat.values()){
            str += stat.name().toLowerCase()+" : "+getAppStat(stat)+" ("+baseStats.get(stat)+")";
        }

        return str;
    }

    static class CharacterUnit extends Unit{
        private String title;

        public CharacterUnit(String name, String title, UnitTemplate template, int level, WeaponType weaponType, boolean horseman, boolean horsemanUponPromotion, boolean homogeneousLevelsup) {
            super(name, template, level, weaponType, horseman, horsemanUponPromotion, homogeneousLevelsup);
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
