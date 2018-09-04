package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Random;

import static com.lawsgame.emishitactics.core.models.Data.BonusType.ATTACKMIGHT;

public class Data {


    public static final float GAME_PORT_WIDTH = 15f;

    //MODEL parameters
    public static final int MOBILITY_BONUS_PROMOTED = 1;
    public static final int PROMOTION_LEVEL = 10;
    public static final int MAX_LEVEL = 30;
    public static final int HEAL_BASE_POWER = 3;
    public static final float MAX_UNITS_UNDER_WARLORD = 6; // including the warlord himself / herself
    public static final float MAX_UNITS_UNDER_WAR_CHIEF = 5; // including the war chief himself / herself
    public static final int MAX_ACTION_POINTS = 100;
    public static final int NB_BUILDING_MAX = 2;
    public static final int DEX_FACTOR_ATT_ACC = 3;
    public static final int DEX_FACTOR_AVO = 4;
    public static final int HIT_RATE_BACK_ACC_BONUS = 20;
    public static final int MAX_WEAPON_CARRIED = 2;
    public static final int MAX_WEAPON_CARRIED_UPON_PROMOTION = 3;
    public static final int MAX_ITEM_CARRIED = 1;
    public static final int MAX_ITEM_CARRIED_UPON_PROMOTION = 2;
    public static final int BASE_DROP_RATE = 5;
    public static final int GUARD_REACTION_RANGE_MIN = 1;
    public static final int GUARD_REACTION_RANGE_MAX = 1;
    public static final int MAX_BANNER_SIGNS_PER_BANNER = 5;

    // RENDER parameters
    public static final float SPEED_WALK = 3f;  //buildingType/s
    public static final float SPEED_PUSHED = 8f;

    // EXP parameter
    public static final double EXP_ALPHA = 0.15;
    public static final double EXP_LVL_GAP_FACTOR = 3;
    public static final double EXP_WOUNDED_ONLY_FACTOR = 0.33333;
    public static final int EXP_REQUIRED_LEVEL_UP = 100;
    public static final int EXP_REQUIRED_LD_LEVEL_UP = 100;

    //UI parameters
    public static final float PANEL_SLIDE_SPEED = 600;


    static Random r = new Random();
    public static int rand(int n){
        return r.nextInt(n);
    }

    /**
     * for animation / rendering purposes, use to define:
     *  - ids of animation sprite sets.
     *  - ids of animations (optional)
     */
    public enum AnimationId {
        WALK,
        SWITCH_WEAPON,
        //SWITCH_POSITION,
        PUSH,
        HEAL,
        STEAL,
        BUILD,
        GUARD,
        COVER,
        LEVELUP,
        REST,
        ATTACK,
        DODGE,
        PUSHED,
        TREATED,
        BACKSTABBED,
        TAKE_HIT,
        FLEE,
        DIE,
        GUARDED

    }

    public enum RangedBasedType{
        MOVE,
        WEAPON,
        SPECIFIC
    }

    public enum ActionChoice{
        MOVE                (1, 0, true, false, RangedBasedType.MOVE, false, new int[0][0]),
        ATTACK              (1, 0, false, true, RangedBasedType.WEAPON, false, new int[0][0]),
        SWITCH_POSITION     (1, 0, true, false, 1, 1, false, new int[0][0]),
        PUSH                (1, 0, false, true, 1, 1, false, new int[0][0]),
        SWITCH_WEAPON       (1, 0, false, true, 0, 0, false, new int[0][0]),
        CHOOSE_ORIENTATION  (0, 0, false, true, 0, 0, true, new int[0][0]),
        HEAL                (1, 10, false, true, 1, 1, false, new int[0][0]),
        GUARD               (1, 10, false, true, 0, 0, false, new int[0][0]),
        STEAL               (1, 10, false, true, 1, 1, false, new int[0][0]),
        BUILD               (1, 10, false, true, 1, 1, false, new int[0][0]),
        END_TURN            (0, 0, false, true, 0, 0, false, new int[0][0]);

        private int cost;
        private int experience;
        private  boolean undoable;
        private  boolean actedBased;
        private int rangeMin;
        private int rangeMax;                  // if true, command that turn actedBased to true is executed, moved otherwise.
        private RangedBasedType rangedBasedType;
        private boolean endTurnActionOnly;
        /**
         * AreaWidget of impact :
         * ( 0, 0) = targeted buildingType
         * ( 1, 0) = in front of targeted buildingType
         * ( 0, 1) = right of targeted buildingType
         * ( 0,-1) = left of targeted buildingType
         * (-1, 0) = in the back of targeted buildingType
         */
        protected Array<int[]> impactArea;

        ActionChoice(int cost, int experience, boolean undoable, boolean actedBased, int rangeMin, int rangeMax, RangedBasedType rangedBasedType, boolean endTurnActionOnly, int[][] impactArea) {
            this.cost = cost;
            this.experience = experience;
            this.rangeMin = rangeMin;
            this.rangeMax = rangeMax;
            this.undoable = undoable;
            this.actedBased = actedBased;
            this.rangedBasedType = rangedBasedType;
            this.endTurnActionOnly = endTurnActionOnly;
            this.impactArea = new Array<int[]>();
            this.impactArea.addAll(impactArea);
        }

        ActionChoice(int cost, int experience, boolean undoable, boolean actedBased, int rangeMin, int rangeMax, boolean endTurnActionOnly, int[][] impactArea){
            this(cost, experience, undoable, actedBased, rangeMin, rangeMax, RangedBasedType.SPECIFIC, endTurnActionOnly, impactArea);
        }

        ActionChoice(int cost, int experience, boolean undoable, boolean actedBased, RangedBasedType rangedBasedType, boolean endTurnActionOnly, int[][] impactArea){
            this(cost, experience, undoable, actedBased, -1, -1, rangedBasedType, endTurnActionOnly, impactArea);
        }

        public int getCost() {
            return cost;
        }

        public int getExperience() {
            return experience;
        }

        public boolean isUndoable() {
            return undoable;
        }

        public boolean isActedBased() {
            return actedBased;
        }

        public int getRangeMin() {
            return rangeMin;
        }

        public int getRangeMax() {
            return rangeMax;
        }

        public RangedBasedType getRangedType() {
            return rangedBasedType;
        }

        public boolean isEndTurnActionOnly() {
            return endTurnActionOnly;
        }

        public boolean isActorIsTarget(){
            return rangeMin == 0 && rangeMax == 0;
        }

        public Array<int[]> getOrientedImpactArea(Data.Orientation orientation) {
            Array<int[]> orientedArea = new Array<int[]>();
            switch (orientation){
                case WEST:
                    for(int i = 0; i < impactArea.size; i++){
                        orientedArea.add(new int[]{impactArea.get(i)[1], -impactArea.get(i)[0]});
                    }
                    break;
                case NORTH:
                    for(int i = 0; i < impactArea.size; i++) {
                        orientedArea.addAll(new int[]{impactArea.get(i)[0], impactArea.get(i)[1]});
                    }
                    break;
                case SOUTH:
                    for(int i = 0; i < impactArea.size; i++){
                        orientedArea.add(new int[]{-impactArea.get(i)[0], -impactArea.get(i)[1]});
                    }
                    break;
                case EAST:
                    for(int i = 0; i < impactArea.size; i++){
                        orientedArea.add(new int[]{-impactArea.get(i)[1], impactArea.get(i)[0]});
                    }
                    break;
            }


            return orientedArea;
        }

        public Array<int[]> getOrientedImpactArea(Data.Orientation orientation, int rowActor, int colActor, Battlefield battlefield) {
            Array<int[]> orientedArea = getOrientedImpactArea(orientation);
            int r;
            int c;
            for(int i = 0; i < orientedArea.size; i++){
                r = orientedArea.get(i)[0] + rowActor;
                c  = orientedArea.get(i)[0] + colActor;
                if(battlefield.isTileExisted(r, c)){
                    orientedArea.get(i)[0] = r;
                    orientedArea.get(i)[1] = c;
                }
            }
            return orientedArea;
        }


        public int getImpactAreaSize(){
            return impactArea.size + 1;
        }

        public String getName(I18NBundle mainI18nBundle) {
            return mainI18nBundle.get(name());
        }
    }

    public enum AreaType {
        TOUCHED_TILE,
        SELECTED_UNIT,
        SQUAD_MEMBER,
        MOVE_AREA,
        ACTION_AREA,
        GUARD_AREA,
        DEPLOYMENT_AREA,
        VANGUARD_DEPLOYMENT_AREA,
        FOE_ACTION_AREA
        //FOE_SQUAD_MEMBER
    }

    public enum TileType {
        VILLAGE(        "village", 204, 143, 37,                    true, true, true, true,         5, 0, 1, 0, 0, false),
        SANCTUARY(      "sanctuary", 228, 56, 56,                   true, true, true, true,         5, 0, 1, 0, 0, false),
        STOCKADE(       "stockade", 204, 112, 37,                   true, true, false, true,        5, 0, 2, 0, 0, false),
        CASTLE(         "castle", 204, 73 ,37 ,                     true, true, false, true,        5, 0, 3, 20, 0, false),
        ANCIENT_SITE(   "ancient site", 38 ,67, 47,                 true, true, true, true,         0, 1, 0, 0, 0, true),
        RUINS(          "ruins", 152, 152, 152,                     true, false, false, true,       0, 0, 1, 0, 0, false),
        MOUNTAINS(      "mountain", 101, 91, 16,                    false, false, false, false,     0, 0, 0, 0, 0, false),
        FOREST(         "deep forest", 5, 96, 34,                   false, false, false, false,     0, 0, 0, 0, 0, false),
        OCEAN(          "deep waters", 25, 157, 197,                false, false, false, false,     0, 0, 0, 0, 0, false),
        SHALLOWS(       "shallows", 30, 211, 227,                   false, false, false, false,     0, 0, 0, 0, 0, false),
        HILLS(          "hill", 146, 134, 40,                       false, false, false, true,      0, 1, 0, 0, 0, true),
        WOODS(          "woods", 10, 158, 57,                       false, false, false, true,      0, 0, 0, 15, 0, false),
        SWAMP(          "swamp", 112, 155, 80,                      false, false, false, true,      0, 0, 0, 0, -15, false),
        PLAIN(          "plain", 17, 215, 80,                       false, false, false, true,      0, 0, 0, 0, 0, false),
        BRIDGE(         "wooden bridge", 85, 96, 134,               false, false, false, true,      0, 0, 0, 0, 0, false),
        BROKEN_BRIDGE(  "broken wooden bridge", 95, 101, 124,       false, false, false, false,     0, 0, 0, 0, 0, false),
        WATCH_TOWER(    "future bridge", 193, 26, 137,              true, true, false, true,      3, 0, 1, 0, 0, true);

        private String name;

        private int r;
        private int g;
        private int b;

        private boolean urbanArea;          // the unit standing on cannot be backstab
        private boolean plunderable;        // can be turn into ruins
        private boolean lootable;           // possess a valuable element
        private boolean reachable;          // can be traversable by standard unit

        private int healPower;
        private int attackMightBonus;
        private int defenseBonus;
        private int avoidBonus;
        private int attackAccBonus;
        private boolean rangeBonus;

        TileType(String name, int r, int g, int b, boolean urbanArea, boolean plunderable, boolean lootable, boolean reachable, int healPower, int attackMightBonus, int defenseBonus, int avoidBonus, int attackAccBonus, boolean rangeBonus) {
            this.name = name;
            this.r = r;
            this.g = g;
            this.b = b;
            this.urbanArea = urbanArea;
            this.plunderable = plunderable;
            this.lootable = lootable;
            this.healPower = healPower;
            this.reachable = reachable;
            this.attackMightBonus = attackMightBonus;
            this.defenseBonus = defenseBonus;
            this.avoidBonus = avoidBonus;
            this.attackAccBonus = attackAccBonus;
            this.rangeBonus = rangeBonus;

        }

        public int getR() {
            return r;
        }

        public int getG() {
            return g;
        }

        public int getB() {
            return b;
        }

        public String getName() {
            return name;
        }

        public int getAttackMightBonus(){
            return attackMightBonus;
        }

        public boolean isUrbanArea(){
            return urbanArea;
        }

        public boolean isPlunderable() {
            return plunderable;
        }

        public boolean isLootable() {
            return lootable;
        }

        public boolean isReachable() {
            return reachable;
        }

        public int getHealPower() { return  healPower;}

        public int getDefenseBonus() {
            return defenseBonus;
        }

        public int getAvoidBonus() {
            return avoidBonus;
        }

        public int getAttackAccBonus() {
            return attackAccBonus;
        }

        public boolean enhanceRange() {
            return rangeBonus;
        }

    }

    public enum DamageType{
        BLUNT,
        EDGED,
        PIERCING
    }

    public enum WeaponType{
        SWORD,
        AXE,
        BOW,
        MACE,
        POLEARM,
        FIST
    }

    /**
     * SWORD
     *  - shortsword
     *  - arming sword
     *  - broadsword
     *  - falchion
     *  - bastard sward (blunt)
     *  - long sword
     *  - claymore
     *  - flamberge
     *  - estoc
     *  - rapier
     *  - zweihander
     *  - knife
     *  - dagger
     *
     *  POLEARM
     *  - pike
     *  - lance
     *  - spear
     *  - javelin
     *  - Fauchard
     *  - Voulge
     *  - Guisarme
     *  - Partisan
     *  - Corseque
     *  - lucerne hammer
     *
     *  AXE
     *  - broad axe
     *  - Francish axe
     *  - Shepherd's axe
     *  - battle axe
     *  - sagaris
     *  - danish axe
     *  - great double-edged axe
     *  - halberd
     *  - bardiche
     *  - Lochhaber axe
     *
     *  MACE & HAMMER
     *  - club
     *  - spicked club
     *  - mace
     *  - Morning star
     *  - Pernach
     *  - flail
     *  - war hammer
     *
     *  BOW:
     *  - Hunting bow
     *  - Recurve bow
     *  - horse bow
     *  - flat bow
     *  - composite bow
     *  - Cable-backed bow
     *  - longbow
     *  - crossbow
     *  - Great bow
     */
    public enum WeaponTemplate{
        FIST(           1, 100, 1, 1, 0, WeaponType.FIST, DamageType.BLUNT, Ability.NONE),
        SHORTSWORD(     3,  90, 1, 1, 20, 50, WeaponType.SWORD, DamageType.EDGED, Ability.NONE),
        LANCE(          3,  95, 1, 1, 20, 50, WeaponType.POLEARM, DamageType.PIERCING, Ability.NONE),
        BROAD_AXE(      5,  80, 1, 1, 20, 50, WeaponType.AXE, DamageType.EDGED, Ability.NONE),
        CLUB(           4,  85, 1, 1, 20, 50, WeaponType.MACE, DamageType.BLUNT, Ability.NONE),
        HUNTING_BOW(    3,  75, 2, 2, 20, 50, WeaponType.BOW, DamageType.PIERCING, Ability.NONE);

        private int damage;
        private int accuracy;
        private int rangeMin;
        private int rangeMax;
        private int dropRate;
        private int durabilityMax;
        private boolean unbreakable;
        private WeaponType weaponType;
        private DamageType damageType;
        private Ability ability;

        WeaponTemplate(int damage, int accuracy, int rangeMin, int rangeMax, int dropRate, int durability, WeaponType weaponType, DamageType damageType, Ability art) {
            this.damage = damage;
            this.accuracy = accuracy;
            this.rangeMin = rangeMin;
            this.rangeMax = rangeMax;
            this.weaponType = weaponType;
            this.damageType = damageType;
            this.ability = art;
            this.dropRate = dropRate;
            this.durabilityMax = durability;
            this.unbreakable = false;
        }

        WeaponTemplate(int damage, int accuracy, int rangeMin, int rangeMax, int dropRate, WeaponType weaponType, DamageType damageType, Ability art){
            this(damage, accuracy, rangeMin, rangeMax, dropRate,1, weaponType, damageType, art);
            this.unbreakable = true;
        }

        public int getDropRate() {
            return dropRate;
        }

        public WeaponType getWeaponType() {
            return weaponType;
        }

        public int getDamage() {
            return damage;
        }

        public int getAccuracy() {
            return accuracy;
        }

        public int getRangeMin() {
            return rangeMin;
        }

        public int getRangeMax() {
            return rangeMax;
        }

        public DamageType getDamageType() {
            return damageType;
        }

        public Ability getAbility() {
            return ability;
        }

        public boolean isMelee() { return rangeMin == 1;}

        public boolean isRange() { return rangeMax > 1; }

        public int getDurabilityMax() {
            return durabilityMax;
        }

        public boolean isUnbreakable() {
            return unbreakable;
        }
    }

    public enum EquipmentTemplate{
        ;

        private Ability ability;
        private int dropRate;

        EquipmentTemplate(int dropRate, Ability passiveAbility) {
            this.dropRate = dropRate;
            this.ability = ability;
        }

        public Ability getAbility() {
            return ability;
        }

        public int getDropRate() {
            return dropRate;
        }
    }

    public enum Ability{
        //Passive
        PATHFINDER,
        UNBREAKABLE,
        SHADOW,
        VIGILANT,

        //Support
        GUARD,
        HEAL,
        STEAL,
        BUILD,

        NONE
    }

    public enum Behaviour{
        PLAYER,
        PASSIVE
    }

    public enum  Orientation{
        WEST,
        NORTH,
        SOUTH,
        EAST;

        public boolean isOpposedTo(Orientation or){
            return (or == WEST && this == EAST )|| (or == SOUTH && this == NORTH);
        }

        public Orientation getOpposite() {
            Orientation opposite = null;
            if(this == NORTH) opposite = SOUTH;
            if(this == SOUTH) opposite = NORTH;
            if(this == EAST) opposite = WEST;
            if(this == WEST) opposite = EAST;
            return opposite;
        }
    }

    public enum Allegeance{
        ALLY,
        ENEMY
    }

    public enum BonusType{
        ATTACKACCURACY,
        ATTACKMIGHT,
        AVOIDANCE,
        PIERCINGDEFENSE,
        BLUNTDEFENSE,
        EDGEDDEFENSE,
        CHARGINGOARATE,
        RANGE
    }

    public enum BannerSignTemplate {
        WARRIOR(ATTACKMIGHT, 1 ,2, 20);

        private BonusType bonusType;
        private int amount;
        private int maxSignByBanner;
        private int dropFactor;

        BannerSignTemplate(BonusType bonusType, int amount, int maxSignByBanner, int dropFactor) {
            this.bonusType = bonusType;
            this.amount = amount;
            this.maxSignByBanner = maxSignByBanner;
            this.dropFactor = dropFactor;
        }

        public BonusType getBonusType() {
            return bonusType;
        }

        public int getAmount() {
            return amount;
        }

        public int getMaxSignByBanner() {
            return maxSignByBanner;
        }

        public int getDropFactor() {
            return dropFactor;
        }

        public String getName(I18NBundle bundle){
            return bundle.get(name());
        }
    }

    public enum Job {
        SERGEANT(1, 4, 6, new int[]{0, -1, -1, -1, 3, 2}, new int[]{-1, 0, -1, 5, 0, 1}, new Ability[]{},
                3, 8, 6, 4, 7, 9, 3, 33, 6, 4, 3,
                0, 2, 2, 1, 2, 2, 1, 9, 2, 2, 2,
                0.00f, 0.15f, 0.10f, 0.10f, 0.10f, 0.15f, 0.15f, 0.55f, 0.40f, 0.40f, 0.45f,
                0.00f, 0.20f, 0.15f, 0.10f, 0.15f, 0.20f, 0.15f, 0.65f, 0.50f, 0.45f, 0.50f),
        KNIGHT(1, 4, 6, new int[]{0, -1, -1, -1, 3, 2}, new int[]{-1, 0, -1, 5, 0, 1}, new Ability[]{},
                3, 8, 9, 4, 11, 7, 3, 35, 7, 4, 3,
                0, 2, 2, 1, 2, 2, 1, 12, 3, 2, 2,
                0.00f, 0.15f, 0.15f, 0.10f, 0.15f, 0.15f, 0.15f, 0.65f, 0.45f, 0.40f, 0.45f,
                0.00f, 0.25f, 0.15f, 0.10f, 0.20f, 0.15f, 0.15f, 0.90f, 0.60f, 0.50f, 0.60f);

        private int startingLevel;
        private int footmanMob;
        private int horsemanMob;
        private int[] horsemanBonus;       // str, dex, agi, piercing, blunt, edged
        private int[] shieldBearerBonus;   // str, dex, agi, piercing, blunt, edged
        private Ability[] nativeAbilities;

        private int baseLd;
        private int baseStr;
        private int basePiercingArmor;
        private int baseBluntArmor;
        private int baseEgdedArmor;
        private int baseAg;
        private int baseCha;
        private int baseHP;
        private int baseDex;
        private int baseSk;
        private int baseBr;

        private int proBoLd;
        private int proBoStr;
        private int proBoPiercingArmor;
        private int proBoBluntArmor;
        private int proBoEdgedArmor;
        private int proBoAg;
        private int proBoCha;
        private int proBoHP;
        private int proBoDex;
        private int proBoSk;
        private int proBoBr;

        private float growthLd;
        private float growthStr;
        private float growthPiercingArmor;
        private float growthBluntArmor;
        private float growthEdgegArmor;
        private float growthAg;
        private float growthCha;
        private float growthHP;
        private float growthDex;
        private float growthSk;
        private float growthBr;

        private float proGrowthLd;
        private float proGrowthStr;
        private float proGrowthPiercingArmor;
        private float proGrowthBluntArmor;
        private float proGrowthEdgedArmor;
        private float proGrowthDex;
        private float proGrowthCha;
        private float getProGrowthHP;
        private float proGrowthAg;
        private float proGrowthSk;
        private float proGrowthBr;

        Job(int startingLevel, int footmanMob, int horsemanMob, int[] horsemanBonus, int[] shieldBearerBonus, Ability[] nativeAbilities, int baseLd, int baseStr, int basePiercingArmor, int baseBluntArmor, int baseEgdedArmor, int baseAg, int baseCha, int baseHP, int baseDex, int baseSk, int baseBr, int proBoLd, int proBoStr, int proBoPiercingArmor, int proBoBluntArmor, int proBoEdgedArmor, int proBoAg, int proBoCha, int proBoHP, int proBoDex, int proBoSk, int proBoBr, float growthLd, float growthStr, float growthPiercingArmor, float growthBluntArmor, float growthEdgegArmor, float growthAg, float growthCha, float growthHP, float growthDex, float growthSk, float growthBr, float proGrowthLd, float proGrowthStr, float proGrowthPiercingArmor, float proGrowthBluntArmor, float proGrowthEdgedArmor, float proGrowthDex, float proGrowthCha, float getProGrowthHP, float proGrowthAg, float proGrowthSk, float proGrowthBr) {
            this.startingLevel = startingLevel;
            this.footmanMob = footmanMob;
            this.horsemanMob = horsemanMob;
            this.horsemanBonus = horsemanBonus;
            this.shieldBearerBonus = shieldBearerBonus;
            this.nativeAbilities = nativeAbilities;
            this.baseLd = baseLd;
            this.baseStr = baseStr;
            this.basePiercingArmor = basePiercingArmor;
            this.baseBluntArmor = baseBluntArmor;
            this.baseEgdedArmor = baseEgdedArmor;
            this.baseAg = baseAg;
            this.baseCha = baseCha;
            this.baseHP = baseHP;
            this.baseDex = baseDex;
            this.baseSk = baseSk;
            this.baseBr = baseBr;
            this.proBoLd = proBoLd;
            this.proBoStr = proBoStr;
            this.proBoPiercingArmor = proBoPiercingArmor;
            this.proBoBluntArmor = proBoBluntArmor;
            this.proBoEdgedArmor = proBoEdgedArmor;
            this.proBoAg = proBoAg;
            this.proBoCha = proBoCha;
            this.proBoHP = proBoHP;
            this.proBoDex = proBoDex;
            this.proBoSk = proBoSk;
            this.proBoBr = proBoBr;
            this.growthLd = growthLd;
            this.growthStr = growthStr;
            this.growthPiercingArmor = growthPiercingArmor;
            this.growthBluntArmor = growthBluntArmor;
            this.growthEdgegArmor = growthEdgegArmor;
            this.growthAg = growthAg;
            this.growthCha = growthCha;
            this.growthHP = growthHP;
            this.growthDex = growthDex;
            this.growthSk = growthSk;
            this.growthBr = growthBr;
            this.proGrowthLd = proGrowthLd;
            this.proGrowthStr = proGrowthStr;
            this.proGrowthPiercingArmor = proGrowthPiercingArmor;
            this.proGrowthBluntArmor = proGrowthBluntArmor;
            this.proGrowthEdgedArmor = proGrowthEdgedArmor;
            this.proGrowthDex = proGrowthDex;
            this.proGrowthCha = proGrowthCha;
            this.getProGrowthHP = getProGrowthHP;
            this.proGrowthAg = proGrowthAg;
            this.proGrowthSk = proGrowthSk;
            this.proGrowthBr = proGrowthBr;
        }

        public int getHorsemanStrengthBonus() {
            return horsemanBonus[0];
        }

        public int getShieldBearerStrengthBonus() {
            return shieldBearerBonus[0];
        }

        public int getHorsemanDexBonus() {
            return horsemanBonus[1];
        }

        public int getShieldBearerDexBonus() {
            return shieldBearerBonus[1];
        }

        public int getHorsemanAgilityBonus() {
            return horsemanBonus[2];
        }

        public int getShieldBearerAgilityBonus() {
            return shieldBearerBonus[2];
        }

        public int getHorsemanArmorPBonus() {
            return horsemanBonus[3];
        }

        public int getShieldBearerArmorPBonus() {
            return shieldBearerBonus[3];
        }

        public int getHorsemanArmorBBonus() {
            return horsemanBonus[4];
        }

        public int getShieldBearerArmorBBonus() {
            return shieldBearerBonus[4];
        }

        public int getHorsemanArmorEBonus() {
            return horsemanBonus[5];
        }

        public int getShieldBearerArmorEBonus() {
            return shieldBearerBonus[5];
        }

        public int getStartingLevel() {
            return startingLevel;
        }

        public String getName(I18NBundle bundle) {
            return bundle.get(name());
        }

        public int getFootmanMob() {
            return footmanMob;
        }

        public int getHorsemanMob() {
            return horsemanMob;
        }

        public Ability[] getNativeAbilities() { return nativeAbilities; }

        public int getBaseCha() {
            return baseCha;
        }

        public int getBaseLd() {
            return baseLd;
        }

        public int getBaseHP() {
            return baseHP;
        }

        public int getBaseStr() {
            return baseStr;
        }

        public int getBaseDex() {
            return baseDex;
        }

        public int getBaseAg() {
            return baseAg;
        }

        public int getBasePiercingArmor() {
            return basePiercingArmor;
        }

        public int getBaseBluntArmor() {
            return baseBluntArmor;
        }

        public int getBaseEgdedArmor() {
            return baseEgdedArmor;
        }

        public int getBaseSk() {
            return baseSk;
        }

        public int getBaseBr() {
            return baseBr;
        }

        public float getGrowthCha() {
            return growthCha;
        }

        public float getGrowthLd() {
            return growthLd;
        }

        public float getGrowthHP() {
            return growthHP;
        }

        public float getGrowthStr() {
            return growthStr;
        }

        public float getGrowthDex() {
            return growthDex;
        }

        public float getGrowthAg() {
            return growthAg;
        }

        public float getGrowthPiercingArmor() {
            return growthPiercingArmor;
        }

        public float getGrowthBluntArmor() {
            return growthBluntArmor;
        }

        public float getGrowthEdgegArmor() {
            return growthEdgegArmor;
        }

        public float getGrowthSk() {
            return growthSk;
        }

        public float getGrowthBr() {
            return growthBr;
        }

        public int getProBoCha() {
            return proBoCha;
        }

        public int getProBoLd() {
            return proBoLd;
        }

        public int getProBoHP() {
            return proBoHP;
        }

        public int getProBoStr() {
            return proBoStr;
        }

        public int getProBoDex() {
            return proBoDex;
        }

        public int getProBoAg() {
            return proBoAg;
        }

        public int getProBoPiercingArmor() {
            return proBoPiercingArmor;
        }

        public int getProBoBluntArmor() {
            return proBoBluntArmor;
        }

        public int getProBoEdgedArmor() {
            return proBoEdgedArmor;
        }

        public int getProBoSk() {
            return proBoSk;
        }

        public int getProBoBr() {
            return proBoBr;
        }

        public float getProGrowthCha() {
            return proGrowthCha;
        }

        public float getProGrowthLd() {
            return proGrowthLd;
        }

        public float getGetProGrowthHP() {
            return getProGrowthHP;
        }

        public float getProGrowthStr() {
            return proGrowthStr;
        }

        public float getProGrowthDex() {
            return proGrowthDex;
        }

        public float getProGrowthAg() {
            return proGrowthAg;
        }

        public float getProGrowthPiercingArmor() {
            return proGrowthPiercingArmor;
        }

        public float getProGrowthBluntArmor() {
            return proGrowthBluntArmor;
        }

        public float getProGrowthEdgedArmor() {
            return proGrowthEdgedArmor;
        }

        public float getProGrowthSk() {
            return proGrowthSk;
        }

        public float getProGrowthBr() {
            return proGrowthBr;
        }

        public static Job getStandard(){
            return SERGEANT;
        }
    }

}
