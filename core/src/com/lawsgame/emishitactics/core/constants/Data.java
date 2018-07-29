package com.lawsgame.emishitactics.core.constants;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Battlefield;

import java.util.Random;

public class Data {

    static Random r = new Random();
    public static int rand(int n){
        return r.nextInt(n);
    }


    public static final float GAME_PORT_WIDTH = 15f;

    public static final int MOBILITY_BONUS_PROMOTED = 1;
    public static final int PROMOTION_LEVEL = 10;
    public static final int MAX_LEVEL = 30;
    public static final int HEAL_BASE_POWER = 3;
    public static final float MAX_UNITS_UNDER_WARLORD = 6; // including the warlord himself / herself
    public static final float MAX_UNITS_UNDER_WAR_CHIEF = 5; // including the war chief himself / herself
    public static final int OA_CHARGING_BAR_MAX_VALUE = 100;
    public static final int NB_BUILDING_MAX = 2;
    public static final int DEX_FACTOR_ATT_ACC = 3;
    public static final int DEX_FACTOR_AVO = 4;
    public static final int DEX_FACTOR_DROP = 2;
    public static final int HIT_RATE_BACK_ACC_BONUS = 20;
    public static final int MAX_WEAPON_CARRIED = 2;
    public static final int MAX_WEAPON_CARRIED_UPON_PROMOTION = 3;
    public static final int MAX_ITEM_CARRIED = 1;
    public static final int MAX_ITEM_CARRIED_UPON_PROMOTION = 2;

    // RENDER parameters
    public static final float SPEED_WALK = 3f;  //tile/s
    public static final float SPEED_PUSHED = 8f;

    // EXP parameter
    public static final double EXP_LVL_GAP_FACTOR_1 = 0.35;
    public static final double EXP_LVL_GAP_FACTOR_2 = 5;
    public static final double EXP_WOUNDED_ONLY_FACTOR = 1.0/3.0;
    public static final int EXP_REQUIRED_LEVEL_UP = 100;
    public static final int EXP_REQUIRED_LD_LEVEL_UP = 100;

    //UI parameters
    public static final float PANEL_SLIDE_SPEED = 600;


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

    public enum AreaType {
        SELECTED_UNIT,
        MOVE_RANGE,
        ACTION_RANGE,
        GUARD_RANGE,
        BANNER_RANGE,
        COVERING_FIRE,
        FOE_ACTION_AREA,
        DEPLOYMENT
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
        FIST(           1, 100, 1, 1, WeaponType.FIST, DamageType.BLUNT, Ability.NONE),
        SHORTSWORD(     3,  90, 1, 1, 50, WeaponType.SWORD, DamageType.EDGED, Ability.NONE),
        LANCE(          3,  95, 1, 1, 50, WeaponType.POLEARM, DamageType.PIERCING, Ability.NONE),
        BROAD_AXE(      5,  80, 1, 1, 50, WeaponType.AXE, DamageType.EDGED, Ability.NONE),
        CLUB(           4,  85, 1, 1, 50, WeaponType.MACE, DamageType.BLUNT, Ability.NONE),
        HUNTING_BOW(    3,  75, 2, 2, 50, WeaponType.BOW, DamageType.PIERCING, Ability.NONE);

        private int damage;
        private int accuracy;
        private int rangeMin;
        private int rangeMax;
        private int durabilityMax;
        private boolean unbreakable;
        private WeaponType weaponType;
        private DamageType damageType;
        private Ability ability;

        WeaponTemplate(int damage, int accuracy, int rangeMin, int rangeMax, int durability, WeaponType weaponType, DamageType damageType, Ability art) {
            this.damage = damage;
            this.accuracy = accuracy;
            this.rangeMin = rangeMin;
            this.rangeMax = rangeMax;
            this.weaponType = weaponType;
            this.damageType = damageType;
            this.ability = art;
            this.durabilityMax = durability;
            this.unbreakable = false;
        }

        WeaponTemplate(int damage, int accuracy, int rangeMin, int rangeMax, WeaponType weaponType, DamageType damageType, Ability art){
            this(damage, accuracy, rangeMin, rangeMax, 1, weaponType, damageType, art);
            this.unbreakable = true;
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
        NOTHING(Ability.NONE);

        private Ability ability;

        EquipmentTemplate(Ability passiveAbility) {
            this.ability = ability;
        }

        public Ability getAbility() {
            return ability;
        }
    }

    public enum Ability{
        //Passive
        PATHFINDER,
        SHADOW,
        VIGILANT,

        //Support
        GUARD,
        HEAL,
        STEAL,
        BUILD,
        COVER,
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

        public static Orientation getStandard(){
            return SOUTH;
        }
    }

    public enum Allegeance{
        ALLY,
        ENEMY
    }

    public enum BannerSign {
        NONE(0,3);

        private int gain;
        private int maxSignByBanner;

        BannerSign(int gain, int maxSignByBanner){
            this.gain = gain;
            this.maxSignByBanner = maxSignByBanner;
        }

        public int getGain(){
            return gain;
        }

        public int getMax() {
            return maxSignByBanner;
        }
    }

    public enum Job {
        SOLAR_KNIGHT(1, "Solar knight", 4, 5,
                3, 1, 45, 9, 3, 5, 11, 7, 8, 3, 5,
                0.10f, 0.05f, 0.55f, 0.35f, 0.20f, 0.10f, 0.10f, 0.10f, 0.15f, 0.25f, 0.45f,
                1, 4, 10, 2, 1, 0, 3, 1, 1, 3, 3,
                0.15f, 0.05f, 0.70f, 0.50f, 0.30f, 0.10f,  0.10f,  0.10f, 0.20f, 0.30f, 0.50f);

        private int startingLevel;
        private String name;
        private int footmanMob;
        private int horsemanMob;

        private int baseCha;
        private int baseLd;
        private int baseHP;
        private int baseStr;
        private int baseDex;
        private int baseAg;
        private int basePiercingArmor;
        private int baseBluntArmor;
        private int baseEgdedArmor;
        private int baseSk;
        private int baseBr;

        private float growthCha;
        private float growthLd;
        private float growthHP;
        private float growthStr;
        private float growthDex;
        private float growthAg;
        private float growthPiercingArmor;
        private float growthBluntArmor;
        private float growthEdgegArmor;
        private float growthSk;
        private float growthBr;

        private int proBoCha;
        private int proBoLd;
        private int proBoHP;
        private int proBoStr;
        private int proBoDex;
        private int proBoAg;
        private int proBoPiercingArmor;
        private int proBoBluntArmor;
        private int proBoEdgedArmor;
        private int proBoSk;
        private int proBoBr;

        private float proGrowthCha;
        private float proGrowthLd;
        private float getProGrowthHP;
        private float proGrowthStr;
        private float proGrowthDex;
        private float proGrowthAg;
        private float proGrowthPiercingArmor;
        private float proGrowthBluntArmor;
        private float proGrowthEdgedArmor;
        private float proGrowthSk;
        private float proGrowthBr;

        Job(int startingLevel, String name, int footmanMob, int horsemanMob, int baseCha, int baseLd, int baseHP, int baseStr, int baseDex, int baseAg, int basePiercingArmor, int baseBluntArmor, int baseEgdedArmor, int baseSk, int baseBr, float growthCha, float growthLd, float growthHP, float growthStr, float growthDex, float growthAg, float growthPiercingArmor, float growthBluntArmor, float growthEdgegArmor, float growthSk, float growthBr, int proBoCha, int proBoLd, int proBoHP, int proBoStr, int proBoDex, int proBoAg, int proBoPiercingArmor, int proBoBluntArmor, int proBoEdgedArmor, int proBoSk, int proBoBr, float proGrowthCha, float proGrowthLd, float getProGrowthHP, float proGrowthStr, float proGrowthDex, float proGrowthAg, float proGrowthPiercingArmor, float proGrowthBluntArmor, float proGrowthEdgedArmor, float proGrowthSk, float proGrowthBr) {
            this.startingLevel = startingLevel;
            this.name = name;
            this.footmanMob = footmanMob;
            this.horsemanMob = horsemanMob;
            this.baseCha = baseCha;
            this.baseLd = baseLd;
            this.baseHP = baseHP;
            this.baseStr = baseStr;
            this.baseDex = baseDex;
            this.baseAg = baseAg;
            this.basePiercingArmor = basePiercingArmor;
            this.baseBluntArmor = baseBluntArmor;
            this.baseEgdedArmor = baseEgdedArmor;
            this.baseSk = baseSk;
            this.baseBr = baseBr;
            this.growthCha = growthCha;
            this.growthLd = growthLd;
            this.growthHP = growthHP;
            this.growthStr = growthStr;
            this.growthDex = growthDex;
            this.growthAg = growthAg;
            this.growthPiercingArmor = growthPiercingArmor;
            this.growthBluntArmor = growthBluntArmor;
            this.growthEdgegArmor = growthEdgegArmor;
            this.growthSk = growthSk;
            this.growthBr = growthBr;
            this.proBoCha = proBoCha;
            this.proBoLd = proBoLd;
            this.proBoHP = proBoHP;
            this.proBoStr = proBoStr;
            this.proBoDex = proBoDex;
            this.proBoAg = proBoAg;
            this.proBoPiercingArmor = proBoPiercingArmor;
            this.proBoBluntArmor = proBoBluntArmor;
            this.proBoEdgedArmor = proBoEdgedArmor;
            this.proBoSk = proBoSk;
            this.proBoBr = proBoBr;
            this.proGrowthCha = proGrowthCha;
            this.proGrowthLd = proGrowthLd;
            this.getProGrowthHP = getProGrowthHP;
            this.proGrowthStr = proGrowthStr;
            this.proGrowthDex = proGrowthDex;
            this.proGrowthAg = proGrowthAg;
            this.proGrowthPiercingArmor = proGrowthPiercingArmor;
            this.proGrowthBluntArmor = proGrowthBluntArmor;
            this.proGrowthEdgedArmor = proGrowthEdgedArmor;
            this.proGrowthSk = proGrowthSk;
            this.proGrowthBr = proGrowthBr;
        }

        public int getStartingLevel() {
            return startingLevel;
        }

        public String getName() {
            return name;
        }

        public int getFootmanMob() {
            return footmanMob;
        }

        public int getHorsemanMob() {
            return horsemanMob;
        }

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
            return SOLAR_KNIGHT;
        }
    }


    public enum RangedBasedType{
        MOVE,
        WEAPON,
        ONESELF,
        SPECIFIC
    }

    /**
     *  1) Modelize the choice made by the player foa a unit to perform a given action
     *
     *
     * AreaWidget of impact :
     * ( 0, 0) = targeted tile
     * ( 1, 0) = in front of targeted tile
     * ( 0, 1) = right of targeted tile
     * ( 0,-1) = left of targeted tile
     * (-1, 0) = in the back of targeted tile
     */
    public enum ActionChoice {
        MOVE                (0, RangedBasedType.MOVE, new int[0][0]),
        SWITCH_WEAPON       (0, RangedBasedType.ONESELF, new int[0][0]),
        SWITCH_POSITION     (0, 1, 1, new int[0][0]),
        PUSH                (0, 1, 1, new int[0][0]),
        HEAL                (0, 1, 1, new int[0][0]),
        GUARD               (0, RangedBasedType.ONESELF, new int[0][0]),
        STEAL               (0, 1, 1, new int[0][0]),
        BUILD               (0, 1, 1, new int[0][0]),
        COVER               (0, RangedBasedType.ONESELF, new int[0][0]),
        ATTACK              (0, RangedBasedType.WEAPON, new int[0][0]),
        CHOOSE_ORIENTATION  (0, RangedBasedType.ONESELF, new int[0][0]),
        END_TURN            (0, RangedBasedType.ONESELF, new int[0][0]);


        // RANGE REQUIREMENT
        private int cost;
        private RangedBasedType rangeType;
        private int rangeMax;
        private int rangeMin;
        private Array<int[]> impactArea; // area on which the action is performed.


        ActionChoice(int cost, RangedBasedType type, int rangeMin, int rangeMax, int[][] impactArea) {
            this.rangeType = type;
            this.rangeMax = rangeMax;
            this.rangeMin = rangeMin;
            this.cost = cost;
            this.impactArea = new Array<int[]>();
            for(int[] relativeTileCoordinates : impactArea){
                this.impactArea.add(relativeTileCoordinates);
            }
        }

        ActionChoice(int cost, int rangeMin, int rangeMax, int[][] impactArea){
            this(cost, RangedBasedType.SPECIFIC, rangeMax, rangeMin, impactArea);
        }

        ActionChoice(int cost, RangedBasedType type, int[][] impactArea) {
            this(cost, type, -1, -1 ,impactArea);
        }

        public RangedBasedType getRangeType() { return rangeType; }

        public int getRangeMax() {
            return rangeMax;
        }

        public int getRangeMin() {
            return rangeMin;
        }

        public int getCost(){
            return cost;
        }


        /*
                                        NORTH (standard):
                                                         ( 1, 0)
                                         ( 0,-2) ( 0,-1) ( 0, 0) ( 0, 1)

                                                         (-2, 0)

                                         SOUTH: rowf = -r colf = -c
                                                          ( 2, 0)

                                                 ( 0, -1) ( 0, 0) ( 0, 1)( 0, 2)
                                                          (-1, 0)

                                         EAST: rowf = -c colf = r
                                                             ( 2, 0)
                                                             ( 1, 0)
                                                  ( 0,-2)    ( 0, 0) ( 0, 1)
                                                             (-1, 0)

                                         WEST: rowf = -c colf = -r
                                                    ( 1, 0)
                                             ( 0,-1)( 0, 0)     ( 0, 2)
                                                    (-1, 0)
                                                    ( 2, 0)


        //VALIDATED                     */
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

    }
}
