package com.lawsgame.emishitactics.core.constants;

import com.badlogic.gdx.utils.Array;

import java.util.Random;

public class Data {

    public static final int MOBILITY_BONUS_PROMOTED = 1;
    public static final int MOBILITY_BONUS_HORSEMAN = 2;
    public static final int PROMOTION_LEVEL = 15;
    public static final int MAX_LEVEL = 30;
    public static final int HEAL_BASE_POWER = 5;
    public static final float MAX_UNITS_UNDER_WARLORD = 5f; // including the warlord himself / herself
    public static final float MAX_UNITS_UNDER_WAR_CHIEF = 4f; // including the war chief himself / herself
    public static final float SPEED_WALK = 1.5f;  //tile/s
    public static final float COUNTER_ATTACK_DAMAGE_MODIFIER = 1f;

    // item bonus
    public static final int DEF_BONUS_YAYOI_SHIELD = 2;
    public static final int DEF_BONUS_GREAT_SHIELD = 3;
    public static final int DEF_BONUS_OYOROI = 2;
    public static final int DEF_BONUS_TANKO = 2;
    public static final int DEF_BONUS_KEIKO = 3;
    public static final int DEX_BONUS_YAYOI_SHIELD = 3;
    public static final int DEX_BONUS_TANKO = 3;
    public static final int DEX_BONUS_EMISHI_LEGGINS = 3;
    public static final int DEX_BONUS_YAMATO_TROUSERS = 5;
    public static final float CRITICAL_DAMAGE_MODIFIER = 1.5f;
    public static final float CRIT_BONUS_IMP_GAUNLET = 0.5f;
    public static final float UNIQUE_EQUIPMENT_HIGH_GROWTH_BONUS = 0.2f;
    public static final float UNIQUE_EQUIPMENT_LOW_GROWTH_BONUS = 0.1f;
    public static final int UNIQUE_EQUIPMENT_FIXE_STD_BONUS = 1;
    public static final int CHARM_HEAL_BONUS = 4;

    // weapon parameters
    public static final int STR_FIXE_BONUS_YARI_1 = 3;
    public static final int STR_FIXE_BONUS_YARI_2 = 2;
    public static final int STR_FIXE_BONUS_NAGINATA_1 = 2;
    public static final int STR_FIXE_BONUS_NAGINATE_2 = 3;
    public static final int AGI_DODGE_FACTOR = 4;
    public static final float DEX_PARRY_FACTOR = 1f;
    public static final int DEX_HIT_FACTOR = 3;
    public static final int ACCURACY_BONUS_CRIT = 15;
    public static final float BLOCK_REDUCTION_DAMAGE = 0.5f;
    public static final int BLOCK_RAW_REDUCTION_DAMAGE = 3;
    public static final int AB_TRIGGER_RATE_SKILL_FACTOR = 2;
    public static final int DEX_FAC_DROP_RATE = 2;

    // experience parameter
    public static final double LVL_GAP_FACTOR = 0.35;
    public static final int EXP_BASE_MODIFIER = 50;
    public static final int EXP_REQUIRED_LD_LEVEL_UP = 100;


    public enum Behaviour {
        CONTROLLED_BY_PLAYER,
        PASSIVE;

        public static Behaviour getStandard(){
            return PASSIVE;
        }
    }

    public enum Ethnicity{
        JAPANESE, AINU;

        public static Ethnicity getStandard(){
            return JAPANESE;
        }
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

    public enum Allegeance {
        ALLY, ENEMY, NEUTRAL
    }

    public enum Stat {
        CHARISMA,
        LEADERSHIP,
        HIT_POINTS,
        MOBILITY,
        STRENGTH,
        DEFENSE,
        DEXTERITY,
        AGILITY,
        SKILL,
        BRAVERY,
        PRIMARY_WEAPON_RANGE_MIN,
        PRIMARY_WEAPON_RANGE_MAX,
        SECONDARY_WEAPON_RANGE_MIN,
        SECONDARY_WEAPON_RANGE_MAX,
        CURRENT_WEAPON_RANGE_MIN,
        CURRENT_WEAPON_RANGE_MAX

    }

    public enum UnitAppointmentErrorMsg{
        SUCCESSFULLY_INSERTED,
        NO_WC_SLOT_AVAILABLE,
        IS_ALREADY_A_WC,
        HAS_ALREADY_STANDARD_BEARER,
        ALREADY_PART_OF_THIS_SQUAD,
        SELECTED_SQUAD_DOES_NOT_EXIST,
        NO_SLOT_AVAILABLE,
        NULL_ARG
    }

    public enum EquipMsg{
        SUCCESSFULLY_EQUIPED,
        TYPE_ALREADY_EQUIPED,
        JOB_DOES_NOT_ALLOW_SHIELD_BEARING,
        STANDARD_BEARER_CANNOT_EQUIP_SHIELD
    }


    public enum BannerSign {
        NONE(0,3),
        IZANAGI(15,1),    // ABILITY
        AMATERASU(1,1),  // PO
        HACHIMAN(1,3),   // DEFENSE
        APEHUCI(1,3),    // STRENGTH
        TUNTU(10,2),      // EXPERIENCE
        SHIRAMBA(1,3);   // DEXTERITY

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

    public enum Item {
        YAYOI_SHIELD("yayoi shield", ItemType.SHIELD),
        GREAT_SHIELD("great shield", ItemType.SHIELD),
        TANKO_ARMOR("tanko armor", ItemType.PLASTRON),
        KEIKO_ARMOR("keiko armor", ItemType.PLASTRON),
        OYOROI_ARMOR("o yoroi armor", ItemType.PLASTRON),
        EMISHI_LEGGINGS("emishi leggings", ItemType.LEGS),
        YAMATO_TROUSERS("yamato trousers", ItemType.LEGS),
        ARMBAND("armband", ItemType.UNIQUE),
        GAUNLET("gaunlet", ItemType.UNIQUE),
        MASTER_BELT("master belt", ItemType.UNIQUE),
        KABUTO("war chief kabuto", ItemType.UNIQUE),
        WAR_CHIEF_CLOAK("war chief cloak", ItemType.UNIQUE),
        WEI_BOOTS("wei boots", ItemType.UNIQUE),
        IMPERIAL_ARMBAND("imperial armband", ItemType.UNIQUE),
        IMPERIAL_GAUNTLET("imperial gauntlet", ItemType.UNIQUE),
        EMISHI_RING("emishi ring", ItemType.UNIQUE),
        DEAR_MANTLE("emishi dear mantle", ItemType.UNIQUE),
        THIEF_RING("thief ring", ItemType.UNIQUE),
        SEISMOMETER("seismometer", ItemType.UNIQUE),
        CHARM("charm", ItemType.UNIQUE),
        NONE("nothing", ItemType.NOTHING);

        private String name;
        private ItemType itemType;

        Item(String name, ItemType itemType) {
            this.name = name;
            this.itemType = itemType;
        }

        public String getName() {
            return name;
        }

        public ItemType getItemType() {
            return itemType;
        }

        public static Item getStandard(){
            return NONE;
        }
    }

    public enum ItemType {
        SHIELD,
        PLASTRON,
        LEGS,
        UNIQUE,
        NOTHING
    }

    public enum TileType {
        VILLAGE(    "village", 204, 143, 37,                    true, true, true, true,         5, 0, 1, 0, 0, false),
        SANCTUARY(  "sanctuary", 228, 56, 56,                   true, true, true, true,         5, 0, 1, 0, 0, false),
        STOCKADE(   "stockade", 204, 112, 37,                   true, true, false, true,        5, 0, 2, 0, 0, false),
        CASTLE(     "castle", 204, 73 ,37 ,                     true, true, false, true,        5, 0, 3, 20, 0, false),
        FORGE(      "forge", 176, 104, 104,                     true, true, true, true,         0, 0, 1, 0, 0, false),
        TOMB(       "kofun burial mount", 38 ,67, 47,           true, true, true, true,         0, 1, 0, 0, 0, true),
        RUINS(      "ruins", 152, 152, 152,                     true, false, false, true,       0, 0, 1, 0, 0, false),
        MOUNTAINS(   "mountain", 101, 91, 16,                   false, false, false, false,     0, 0, 0, 0, 0, false),
        FOREST(     "deep forest", 5, 96, 34,                   false, false, false, false,     0, 0, 0, 0, 0, false),
        OCEAN(      "deep waters", 25, 157, 197,                false, false, false, false,     0, 0, 0, 0, 0, false),
        SHALLOWS(   "shallows", 30, 211, 227,                   false, false, false, false,     0, 0, 0, 0, 0, false),
        HILLS(      "hill", 146, 134, 40,                       false, false, false, true,      0, 1, 0, 0, 0, true),
        WOODS(      "woods", 10, 158, 57,                       false, false, false, true,      0, 0, 0, 15, 0, false),
        SWAMP(      "swamp", 112, 155, 80,                      false, false, false, true,      0, 0, 0, 0, -15, false),
        PLAIN(      "plain", 17, 215, 80,                       false, false, false, true,      0, 0, 0, 0, 0, false),
        BRIDGE(      "wooden bridge", 85, 96, 134,              false, false, false, true,      0, 0, 0, 0, 0, false),
        BROKEN_BRIDGE("broken wooden bridge", 95, 101, 124,     false, false, false, false,     0, 0, 0, 0, 0, false),
        WATCH_TOWER("future bridge", 193, 26, 137,              true, true, false, true,      3, 0, 1, 0, 0, true);

        private String name;

        private int r;
        private int g;
        private int b;

        private boolean urbanArea;          // the unit standing on cannot be backstab
        private boolean plunderable;        // can be turn into ruins
        private boolean lootable;           // possess a valuable element
        private boolean reachable;          // can be traversable by standard unit

        private int healPower;
        private int strengthBonus;
        private int defenseBonus;
        private int avoidBonus;
        private int attackAccBonus;
        private boolean rangeBonus;

        TileType(String name, int r, int g, int b, boolean urbanArea, boolean plunderable, boolean lootable, boolean reachable, int healPower, int strengthBonus, int defenseBonus, int avoidBonus, int attackAccBonus, boolean rangeBonus) {
            this.name = name;
            this.r = r;
            this.g = g;
            this.b = b;
            this.urbanArea = urbanArea;
            this.plunderable = plunderable;
            this.lootable = lootable;
            this.healPower = healPower;
            this.reachable = reachable;
            this.strengthBonus = strengthBonus;
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

        public int getStrengthBonus(){
            return strengthBonus;
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

        public static TileType getStandard(){
            return PLAIN;
        }
    }

    public enum OffensiveAbility {
        FOCUSED_BLOW,
        CRIPPLING_BLOW,
        SWIRLING_BLOW,
        SWIFT_BLOW,
        HEAVY_BLOW,
        CRUNCHING_BLOW,
        WAR_CRY,
        POISONOUS_ATTACK,
        GUARD_BREAK,
        LINIENT_BLOW,
        FURY,
        NONE
    }

    public enum PassiveAbility{
        NONE,
        PRAYER,             // OK
        HEALER,             // OK
        PATHFINDER,         // OK
        THIEF,              // OK
        BEAST,
        SHADOW,
        VIGILANT,
        GUARDIAN,
        ENGINEER            // OK
    }

    /**
     * for animation / rendering purposes, use to define:
     *  - ids of animation sprite sets.
     *  - ids of animations
     */
    public enum AnimationId {
        WALK,               //automatized
        SWITCH_WEAPON,      //automatized
        SWITCH_POSITION,    //automatized
        PUSH,
        HEAL,
        PRAY,               //use HEAL animation
        STEAL,
        BUILD,
        GUARD,
        LEVEL_UP,           //automatized, partially at least, the chosen secondary and level-up panels are displayed separately to distinguish between player and AI's units
        HEALED,
        REST,
        ATTACK,
        PARRIED_ATTACK,
        DODGE,
        BLOCK,
        PARRY,
        BACKSTABBED,
        TAKE_HIT,           //automatized
        FLEE,               //automatized, use WALK animation
        DIE,                //automatized
        FOCUSED_BLOW,
        CRIPPLING_BLOW,
        SWIRLING_BLOW,
        SWIFT_BLOW,
        HEAVY_BLOW,
        CRUNCHING_BLOW,
        WAR_CRY,
        POISONOUS_ATTACK,
        GUARD_BREAK,
        LINIENT_BLOW,
        FURY
    }


    public enum TargetType{
        SPECIFIC,
        ONE_SELF,
        ALLY,
        FOOTMAN_ALLY,
        WOUNDED_ALLY,
        ENEMY
    }

    /**
     *  1) Modelize the choice made by the player foa a unit to perform a given action
     *
     *
     * Area of impact :
     * ( 0, 0) = targeted tile
     * ( 1, 0) = in front of targeted tile
     * ( 0, 1) = right of targeted tile
     * ( 0,-1) = left of targeted tile
     * (-1, 0) = in the back of targeted tile
     */
    public enum ActionChoice {
        WALK                            (0, 0, false, false, DamageType.NONE, TargetType.SPECIFIC, new int[][]{{}}),
        SWITCH_WEAPON                   (0, 0, false, false, DamageType.NONE, TargetType.ONE_SELF, new int[][]{{}}),
        SWITCH_POSITION                 (1, 1, false, false, DamageType.NONE, TargetType.ALLY, new int[][]{{}}),
        PUSH                            (1, 1, false, false, DamageType.NONE, TargetType.FOOTMAN_ALLY, new int[][]{{}}),
        PRAY                            (1, 1, false, false, DamageType.NONE, TargetType.WOUNDED_ALLY, new int[][]{{}}),
        HEAL                            (0, 0, false, false, DamageType.NONE, TargetType.ONE_SELF,new int[][]{{1,0}, {-1,0}, {0,1},{0,-1}}),
        STEAL                           (1, 1, false, false, DamageType.NONE, TargetType.ENEMY,new int[][]{{}}),
        BUILD                           (1, 1, false, false, DamageType.NONE, TargetType.SPECIFIC, new int[][]{{}}),
        ATTACK                          (0, 0, true, false, DamageType.NONE, TargetType.ENEMY, new int[][]{{}}),
        CHOOSE_ORIENTATION              (0, 0, false, false, DamageType.NONE, TargetType.ONE_SELF, new int[][]{{}}),
        CHOOSE_STANCE                   (0, 0, false, false, DamageType.NONE, TargetType.ONE_SELF, new int[][]{{}}),
        USE_FOCUSED_BLOW                (0, 0, true, false, DamageType.PIERCING, TargetType.ENEMY, new int[][]{{}}),
        USE_CRIPPLING_BLOW              (0, 0, true, false, DamageType.PIERCING, TargetType.ENEMY, new int[][]{{}}),
        USE_SWIRLING_BLOW               (1, 1, false, true, DamageType.EDGED, TargetType.ENEMY, new int[][]{{-1,1}, {-1,-1}}),
        USE_SWIFT_BLOW                  (0, 0, true, false, DamageType.EDGED, TargetType.ENEMY, new int[][]{}),
        USE_HEAVY_BLOW                  (0, 0, true, false, DamageType.BLUNT, TargetType.ENEMY, new int[][]{}),
        USE_CRUNCHING_BLOW              (1, 1, false, true, DamageType.BLUNT, TargetType.ENEMY, new int[][]{{1, 0}, {-1, 0}}),
        USE_WAR_CRY                     (0, 0, true, false, DamageType.NONE, TargetType.ENEMY, new int[][]{{}}),
        USE_POISONOUS_ATTACK            (0, 0, true, false, DamageType.NONE, TargetType.ENEMY, new int[][]{{}}),
        USE_GUARD_BREAK                 (0, 0, true, false, DamageType.NONE, TargetType.ENEMY, new int[][]{{}}),
        USE_LINIENT_BLOW                (0, 0, true, false, DamageType.NONE, TargetType.ENEMY, new int[][]{{}}),
        USE_FURY                        (0, 0, true, false, DamageType.NONE, TargetType.ENEMY, new int[][]{{}});

        private int rangeMax;
        private int rangeMin;
        private boolean weaponBasedRange;
        private boolean meleeWeaponEquipedRequired;
        private DamageType damageTypeRequired;
        private TargetType targetType;
        private Array<int[]> impactArea;


        ActionChoice(int rangeMax, int rangeMin, boolean weaponBasedRange, boolean meleeWeaponEquipedRequired, DamageType damageTypeRequired, TargetType targetType, int[][] impactArea) {
            this.rangeMax = rangeMax;
            this.rangeMin = rangeMin;
            this.weaponBasedRange = weaponBasedRange;
            this.meleeWeaponEquipedRequired = meleeWeaponEquipedRequired;
            this.damageTypeRequired = damageTypeRequired;
            this.targetType = targetType;
            this.impactArea = new Array<int[]>();
            for(int[] relativeTileCoordinates : impactArea){
                this.impactArea.add(relativeTileCoordinates);
            }

        }

        public int getRangeMax() {
            return rangeMax;
        }

        public int getRangeMin() {
            return rangeMin;
        }

        public boolean isWeaponBasedRange() {
            return weaponBasedRange;
        }

        public boolean isMeleeWeaponEquipedRequired() {
            return meleeWeaponEquipedRequired;
        }

        public DamageType getDamageTypeRequired() { return damageTypeRequired; }

        public TargetType getTargetType() { return targetType; }

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


                                         */
        public Array<int[]> getOrientedArea(Data.Orientation orientation) {
            Array<int[]> orientedArea = new Array<int[]>();
            switch (orientation){
                case WEST:
                    for(int i = 0; i < impactArea.size; i++){
                        orientedArea.add(new int[]{-impactArea.get(i)[1], -impactArea.get(i)[0]});
                    }
                    break;
                case NORTH:
                    orientedArea.addAll(impactArea);
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

        public int getImpactAreaSize(){
            return impactArea.size;
        }

    }

    public enum DamageType {
        EDGED,
        PIERCING,
        BLUNT,
        NONE
    }

    public enum WeaponArt{
        NONE,
        KENJUTSU,
        CRITICAL_PARRY,
        ARMOR_SLAYER,
        HORSEMAN_SLAYER,
        COVERING_FIRE,
        FIRST_STRIKE,
        NODACHI_JUSTU,
        NAGINATA_JUTSU
    }

    public enum Weapon{
        NONE(false, false,      0, 0, 0, 0, 0, 0, DamageType.EDGED, WeaponArt.NONE),
        KATANA(false, false,    3, 70, 6, 5, 1, 1, DamageType.EDGED, WeaponArt.KENJUTSU),
        WARABITE(false, false,  4, 70, 5, 5, 1, 1, DamageType.EDGED, WeaponArt.KENJUTSU),
        SAI(true, true,         2, 90, 8, 3, 1, 1, DamageType.PIERCING, WeaponArt.CRITICAL_PARRY),
        NAGINATA(true, false,   4, 65, 4, 4, 1, 1, DamageType.EDGED, WeaponArt.NAGINATA_JUTSU),
        NODACHI(true, true,     5, 60, 4, 2, 1, 1, DamageType.BLUNT, WeaponArt.NODACHI_JUSTU ),
        YARI(false, false,      3, 80, 3, 3, 1, 1, DamageType.PIERCING, WeaponArt.HORSEMAN_SLAYER),
        YUMI(true, false,       4, 60, 0, 1, 2, 2, DamageType.PIERCING, WeaponArt.COVERING_FIRE),
        BO(true, true,          3, 65, 3, 5, 1, 1, DamageType.BLUNT, WeaponArt.FIRST_STRIKE),
        KANABO(true, true,      5, 55, 1, 1, 1, 1, DamageType.BLUNT, WeaponArt.ARMOR_SLAYER);

        private boolean dualWieldingRequired;
        private boolean footmanOnly;
        private int damage;
        private int accuracy;
        private int parryCapacity;
        private int parryVulnerability;
        private int rangeMin;
        private int rangeMax;
        private DamageType type;
        private WeaponArt art;

        Weapon(boolean dualWieldingRequired, boolean footmanOnly, int damage, int accuracy, int parryCapacity, int parryVulnerability, int rangeMin, int rangeMax, DamageType type, WeaponArt art) {
            this.dualWieldingRequired = dualWieldingRequired;
            this.footmanOnly = footmanOnly;
            this.damage = damage;
            this.accuracy = accuracy;
            this.parryCapacity = parryCapacity;
            this.parryVulnerability = parryVulnerability;
            this.rangeMin = rangeMin;
            this.rangeMax = rangeMax;
            this.type = type;
            this.art = art;
        }

        public boolean isRangedW(){
            return getRangeMax() > 1;
        }

        public boolean isDualWieldingRequired() {
            return dualWieldingRequired;
        }

        public boolean isFootmanOnly() {
            return footmanOnly;
        }

        public int getDamage() {
            return damage;
        }

        public int getAccuracy() {
            return accuracy;
        }

        public int getParryCapacity() {
            return parryCapacity;
        }

        public int getParryVulnerability() {
            return parryVulnerability;
        }

        public int getRangeMin() {
            return rangeMin;
        }

        public int getRangeMax() {
            return rangeMax;
        }

        public DamageType getType() {
            return type;
        }

        public WeaponArt getArt() {
            return art;
        }

        private static float highAverageParry = 0;

        public static float getHighAverageParryVulnerabilty(){
            if(highAverageParry == 0){
                for(Weapon wpn: Weapon.values()){
                    if(wpn != NONE) {
                        highAverageParry += wpn.parryVulnerability;
                    }
                }
                highAverageParry /= Weapon.values().length;
            }
            return highAverageParry;
        }
    }


    public enum JobTemplate {
        CONSCRIPT("conscript", "bushi",  4,             false, true, false, new Weapon[]{Weapon.YARI, Weapon.YUMI, Weapon.KATANA}, new Weapon[]{Weapon.YARI, Weapon.YUMI, Weapon.KATANA, Weapon.NODACHI, Weapon.NAGINATA}),
        EMISHI( "emishi warrior", "emishi warrior", 4,  false, true, false, new Weapon[]{Weapon.WARABITE, Weapon.YUMI, Weapon.YARI}, new Weapon[]{Weapon.WARABITE, Weapon.YUMI, Weapon.YARI, Weapon.KANABO});

        private String recruitName;
        private String promotionName;
        private int mobility;
        private boolean allowadToBePromotedHorseman;
        private boolean allowedWieldShield;
        private boolean standardBearerJob;
        private Weapon[] availableWeapons;
        private Weapon[] availableWeaponsAfterPromotion;

        JobTemplate(String recruitName, String promotionName, int mobility, boolean allowadToBePromotedHorseman, boolean allowedWieldShield, boolean standardBearer, Weapon[] availableWeapons, Weapon[] availableWeaponsAfterPromotion) {
            this.recruitName = recruitName;
            this.promotionName = promotionName;
            this.mobility = mobility;
            this.allowadToBePromotedHorseman = allowadToBePromotedHorseman;
            this.allowedWieldShield = allowedWieldShield;
            this.standardBearerJob = standardBearer;
            this.availableWeapons = availableWeapons;
            this.availableWeaponsAfterPromotion = availableWeaponsAfterPromotion;
        }

        public String getRecruitName(){
            return recruitName;
        }

        public String getPromotionName() {
            return promotionName;
        }

        public int getMobility() {
            return mobility;
        }

        public boolean isAllowadToBePromotedHorseman() {
            return allowadToBePromotedHorseman;
        }

        public boolean isAllowedWieldShield() {return allowedWieldShield;}

        public Weapon[] getAvailableWeapons() {
            return availableWeapons;
        }

        public Weapon[] getAvailableWeaponsAfterPromotion() {
            return availableWeaponsAfterPromotion;
        }

        public boolean couldBeStandardBearerJob() {
            return standardBearerJob;
        }
    }

    public enum UnitTemplate {
        CONSCRIPT(1, JobTemplate.CONSCRIPT,
                1, 1, 39, 11, 3, 3, 5, 3, 1,    0.20f, 0.00f, 0.70f, 0.15f, 0.50f, 0.35f, 0.20f, 0.30f, 0.35f,
                0, 1, 10, 2, 3, 1, 3, 2, 3,     0.30f, 0.00f, 0.85f, 0.30f, 0.60f, 0.35f, 0.25f, 0.50f, 0.55f),
        EMISHI_TRIBESMAN(1, JobTemplate.EMISHI,
                1, 1, 33, 13, 4, 3, 2, 3, 3,    0.20f, 0.00f, 0.50f, 0.25f, 0.55f, 0.50f, 0.15f, 0.30f, 0.40f,
                0, 1, 6, 2, 2, 2, 2, 2, 2,      0.30f, 0.00f, 0.60f, 0.30f, 0.60f, 0.55f, 0.15f, 0.50f, 0.50f);

        private int startLevel;
        private JobTemplate job;

        private int baseCha;
        private int baseLd;
        private int baseHP;
        private int baseStr;
        private int baseDex;
        private int baseAg;
        private int baseDef;
        private int baseSk;
        private int baseBr;

        private float growthCha;
        private float growthLd;
        private float growthHP;
        private float growthStr;
        private float growthDex;
        private float growthAg;
        private float growthDef;
        private float growthSk;
        private float growthBr;

        private int proBoCha;
        private int proBoLd;
        private int proBoHP;
        private int proBoStr;
        private int proBoDex;
        private int proBoAg;
        private int proBoDef;
        private int proBoSk;
        private int proBoBr;

        private float proGrowthCha;
        private float proGrowthLd;
        private float getProGrowthHP;
        private float proGrowthStr;
        private float proGrowthDex;
        private float proGrowthAg;
        private float proGrowthDef;
        private float proGrowthSk;
        private float proGrowthBr;

        UnitTemplate(int startLevel, JobTemplate job, int baseCha, int baseLd, int baseHP, int baseStr, int baseDex, int baseAg, int baseDef, int baseSk, int baseBr, float growthCha, float growthLd, float growthHP, float growthStr, float growthDex, float growthAg, float growthDef, float growthSk, float growthBr, int proBoCha, int proBoLd, int proBoHP, int proBoStr, int proBoDex, int proBoAg, int proBoDef, int proBoSk, int proBoBr, float proGrowthCha, float proGrowthLd, float getProGrowthHP, float proGrowthStr, float proGrowthDex, float proGrowthAg, float proGrowthDef, float proGrowthSk, float proGrowthBr) {
            this.startLevel = startLevel;
            this.job = job;
            this.baseCha = baseCha;
            this.baseLd = baseLd;
            this.baseHP = baseHP;
            this.baseStr = baseStr;
            this.baseDex = baseDex;
            this.baseAg = baseAg;
            this.baseDef = baseDef;
            this.baseSk = baseSk;
            this.baseBr = baseBr;
            this.growthCha = growthCha;
            this.growthLd = growthLd;
            this.growthHP = growthHP;
            this.growthStr = growthStr;
            this.growthDex = growthDex;
            this.growthAg = growthAg;
            this.growthDef = growthDef;
            this.growthSk = growthSk;
            this.growthBr = growthBr;
            this.proBoCha = proBoCha;
            this.proBoLd = proBoLd;
            this.proBoHP = proBoHP;
            this.proBoStr = proBoStr;
            this.proBoDex = proBoDex;
            this.proBoAg = proBoAg;
            this.proBoDef = proBoDef;
            this.proBoSk = proBoSk;
            this.proBoBr = proBoBr;
            this.proGrowthCha = proGrowthCha;
            this.proGrowthLd = proGrowthLd;
            this.getProGrowthHP = getProGrowthHP;
            this.proGrowthStr = proGrowthStr;
            this.proGrowthDex = proGrowthDex;
            this.proGrowthAg = proGrowthAg;
            this.proGrowthDef = proGrowthDef;
            this.proGrowthSk = proGrowthSk;
            this.proGrowthBr = proGrowthBr;
        }

        public int getBaseLd() {
            return baseLd;
        }

        public float getGrowthLd() {
            return growthLd;
        }

        public int getProBoLd() {
            return proBoLd;
        }

        public float getProGrowthLd() {
            return proGrowthLd;
        }

        public int getStartLevel() {
            return startLevel;
        }

        public JobTemplate getJob() {
            return job;
        }

        public int getBaseCha() {
            return baseCha;
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

        public int getBaseDef() {
            return baseDef;
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

        public float getGrowthDef() {
            return growthDef;
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

        public int getProBoDef() {
            return proBoDef;
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

        public float getProGrowthHP() {
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

        public float getProGrowthDef() {
            return proGrowthDef;
        }

        public float getProGrowthSk() {
            return proGrowthSk;
        }

        public float getProGrowthBr() {
            return proGrowthBr;
        }

        public  static UnitTemplate getStandard(){
            return CONSCRIPT;
        }
    }

    public enum DefensiveStance {
        PARRY,
        DODGE,
        BLOCK
    }

    public static class R {
        private static R r;
        private Random rand;

        private R(){
            rand = new Random();
        }

        public static Random getR() {
            if (r == null) r = new R();
            return r.rand;
        }
    }
}
