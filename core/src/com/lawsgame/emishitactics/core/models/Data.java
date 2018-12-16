package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Random;

public class Data {


    //MODEL parameters
    public static final int MOBILITY_BONUS_PROMOTED = 1;
    public static final int PROMOTION_LEVEL = 10;
    public static final int MAX_LEVEL = 30;
    public static final int HEAL_BASE_POWER = 3;
    public static final float MAX_UNITS_UNDER_WARLORD = 6; // including the warlord himself / herself
    public static final float MAX_UNITS_UNDER_WAR_CHIEF = 5; // including the war chief himself / herself
    public static final int AP_REGEN = 3;
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
    public static final int WC_CHARISMA_BONUS_ATT_ACC = 3;
    public static final int SQUAD_SIZE_EXCEEDANCE_CHA_MALUS = 3;
    public static final int BRAVERY_MORAL_RECOVERY_RATE = 3;
    public static final float CHANCE_OF_COLLAPSING_FROM_FRAGILE_TILES = 1f; //over 1
    public static final int TRAP_DAMAGE = 3;
    public static final int SCAN_AREA_RANGE = 3;


    // RENDER parameters
    public static final float SPEED_WALK = 1.5f;
    public static final float SPEED_PUSHED = 1.9f;
    public static final float ANIMATION_NORMAL_SPEED = 0.35f;
    public static final float ANIMATION_FLEE_SPEED = 0.09f;
    public static final float ANIMATION_WALK_SPEED = 0.25f;
    public static final float ANIMATIONL_SPEED_SPARKLE = 0.15f;
    public static final float BLINK_PERIOD_TARGET = 1.0f * MathUtils.PI;
    public static final float BLINK_PERIOD_WOUNDED_BASE = 7.0f;
    public static final float BLINK_PERIOD_WOUNDED_AMPLITUDE = 2.0f;
    public static final float ANIMATION_DURATION = ANIMATION_NORMAL_SPEED*5;
    public static final float EARTHQUAKE_DURATION = 4.0f;


    // EXP parameter
    public static final double EXP_ALPHA = 0.15;
    public static final double EXP_LVL_GAP_FACTOR = 3;
    public static final double EXP_WOUNDED_ONLY_FACTOR = 0.33333;
    public static final int EXP_REQUIRED_LEVEL_UP = 100;
    public static final int EXP_REQUIRED_LD_LEVEL_UP = 100;

    //UI parameters
    public static final float AIBIS_ACTION_PANEL_DURATION_APPEARANCE = 3.0f;
    public static final float AIBIS_DELAY_CAMERA_FOCUS = 1.5f;


    static Random r = new Random();
    public static int rand(int n){
        return r.nextInt(n);
    }


    public enum SparkleType {
        LOOT,
        TRAP
    }


    public enum BBMode{
        OFFENSIVE,
        DEFENSIVE,
        ALL
    }


    public enum BannerBonus {
        ATTACK_MIGHT(1,     new float[]{1, 3, 5},       BBMode.OFFENSIVE),
        MORAL_SHIELD(0.1f,  new float[]{1, 2, 3, 4, 5}, BBMode.DEFENSIVE),
        LOOT_RATE   (2,     new float[]{1, 2, 3, 4, 5}, BBMode.ALL),
        AP_COST     (1,     new float[]{3, 6},          BBMode.OFFENSIVE),
        RANGE       (1,     new float[]{6},             BBMode.ALL);

        private float baseValue;
        private float[] cost;
        private BBMode mode;        // in switch mode, this bonus is enabled

        BannerBonus(float baseValue, float[] cost, BBMode mode) {
            this.baseValue = baseValue;
            this.cost = cost;
            this.mode = mode;
        }

        public float getBaseValue() {
            return baseValue;
        }

        public float[] getCost() {
            return cost;
        }

        public BBMode getMode() {
            return mode;
        }
    }


    public enum Environment{
        PRINCIPALITY;

        public static Environment getStandard(){
            return PRINCIPALITY;
        }
    }


    public enum Weather {
        SUNNY(new Color(195f/255f, 236/255f, 240/255f, 1), new Color(89f/255f, 184/255f, 219/255f, 1));

        private Color lowerColor;
        private Color upperColor;

        Weather(Color lowerColor, Color upperColor) {
            this.lowerColor = lowerColor;
            this.upperColor = upperColor;
        }

        public Color getLowerColor() {
            return lowerColor;
        }

        public Color getUpperColor() {
            return upperColor;
        }

        public static Weather getStandard(){
            return SUNNY;
        }
    }


    /**
     * !!! ATTENTION !!!
     *
     * modifying there names can lead to a asset provider error!
     */
    public enum AnimSpriteSetId{
        WALK_FLEE_SWITCHPOSITION                (false),
        LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED(true),
        PUSHED_BACKSTABBED                      (false),
        ATTACK                                  (false),
        SPECIAL_MOVE                            (false),
        WOUNDED                                 (false),
        REST                                    (false),
        BANNER                                  (false),
        DODGE                                   (false),
        STEAL                                   (false),
        BUILD                                   (false),
        PUSH                                    (false),
        HEAL                                    (true),
        DIE                                     (false);

        private boolean rest0;

        AnimSpriteSetId(boolean rest0) {
            this.rest0 = rest0;
        }

        public boolean isRest0() {
            return rest0;
        }
    }


    public enum AnimId {
        WALK(           AnimSpriteSetId.WALK_FLEE_SWITCHPOSITION, AnimSpriteSetId.WALK_FLEE_SWITCHPOSITION, true, true, ANIMATION_WALK_SPEED, false, false),
        BACKSTAB(       AnimSpriteSetId.PUSHED_BACKSTABBED),
        TREATED(        AnimSpriteSetId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED),
        PUSHED(         AnimSpriteSetId.PUSHED_BACKSTABBED),
        FLEE(           AnimSpriteSetId.WALK_FLEE_SWITCHPOSITION, AnimSpriteSetId.WALK_FLEE_SWITCHPOSITION, true, true, ANIMATION_FLEE_SPEED, true, false),
        PUSH(           AnimSpriteSetId.PUSH),
        HEAL(           AnimSpriteSetId.HEAL),
        STEAL(          AnimSpriteSetId.STEAL),
        BUILD(          AnimSpriteSetId.BUILD),
        GUARD(          AnimSpriteSetId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED),
        IDLE(           AnimSpriteSetId.REST, AnimSpriteSetId.BANNER, true, true, ANIMATION_NORMAL_SPEED, false, true),
        REGULAR_ATTACK( AnimSpriteSetId.ATTACK),
        PLUNDER(        AnimSpriteSetId.ATTACK),
        COVER(          AnimSpriteSetId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED),
        DODGE(          AnimSpriteSetId.DODGE),
        DIE(            AnimSpriteSetId.DIE),
        GUARDED(        AnimSpriteSetId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED),
        WOUNDED(        AnimSpriteSetId.WOUNDED),
        LEVELUP(        AnimSpriteSetId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED),
        SWITCH_WEAPON(  AnimSpriteSetId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED),
        SPECIAL_MOVE(   AnimSpriteSetId.SPECIAL_MOVE),
        CHANGE_STRATEGY(AnimSpriteSetId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED),
        SCAN_AREA(      AnimSpriteSetId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED);

        AnimSpriteSetId soldierId;
        AnimSpriteSetId warchiefId;
        boolean loop;
        boolean backnforth;
        float speed;
        boolean timeLimited;
        boolean randomlyStarted;

        AnimId(AnimSpriteSetId soldierId, AnimSpriteSetId warchiefId, boolean loop, boolean backnforth, float speed, boolean timeLimited, boolean randomlyStarted) {
            this.soldierId = soldierId;
            this.warchiefId = warchiefId;
            this.loop = loop;
            this.backnforth = backnforth;
            this.speed = speed;
            this.timeLimited = timeLimited;
            this.randomlyStarted = randomlyStarted;
        }

        AnimId(AnimSpriteSetId id) {
            this(id, id, false, false, Data.ANIMATION_NORMAL_SPEED, true, false);
        }

        public AnimSpriteSetId getSpriteSetId(boolean warchief) {
            return (warchief) ?  warchiefId : soldierId;
        }

        public boolean isLoop() {
            return loop;
        }

        public boolean isBacknforth() {
            return backnforth;
        }

        public float getSpeed() {
            return speed;
        }

        public boolean isTimeLimited() {
            return timeLimited;
        }

        public boolean isRandomlyStarted() {
            return randomlyStarted;
        }
    }


    public enum RangedBasedType{
        MOVE,
        WEAPON,
        SPECIFIC
    }


    public enum ActionChoice{
        //TEST_CHOICE         (0, 0, false, true, RangedBasedType.WEAPON, false, new int[][]{{0, 0}, {0, 1}, {0, -1}}),

        MOVE                (0, 0, true, false, false, 0, 0, false, new int[][]{{0, 0}}),
        WALK                (0, 0, true, false, false, RangedBasedType.MOVE, false, new int[][]{{0, 0}}),
        ATTACK              (3, 0, false, true, false, RangedBasedType.WEAPON, false, new int[][]{{0, 0}}),
        SWITCH_POSITION     (0, 0, true, false, false, 1, 1, false, new int[][]{{0, 0}}),
        PUSH                (0, 0, false, true, false, 1, 1, false, new int[][]{{0, 0}}),
        SWITCH_WEAPON       (0, 0, true, true, true, 0, 0, false, new int[][]{{0, 0}}),
        CHOOSE_ORIENTATION  (0, 0, false, true, false, 0, 0, true, new int[][]{{0, 0}}),
        HEAL                (3, 10, false, true, false, 0, 0, false, new int[][]{{1, 0}, {0, 1}, {-1, 0},{0, -1}}),
        GUARD               (3, 10, false, true, false, 0, 0, false, new int[][]{{0, 0}}),
        STEAL               (3, 10, false, true, false, 1, 1, false, new int[][]{{0, 0}}),
        BUILD               (5, 10, false, true, false, 1, 1, false, new int[][]{{0, 0}}),
        PLUNDER             (3, 10, false, true, false, 0, 0, false, new int[][]{{0, 0}}),
        COVER_AREA          (3, 10, false, true, false, 0, 0, false, new int[][]{{0, 0}}),
        SCAN_AREA           (3, 20, false, true, false, 0, 0, false, new int[][]{{0, 0}}),
        CHANGE_TACTIC       (0, 0, true, true, true, 0, 0, false, new int[][]{{0, 0}}),
        PICK_LOOT           (0, 0, false, true, false, 0, 0, false, new int[][]{{0, 0}}),
        END_TURN            (0, 0, false, true, false, 0, 0, false, new int[][]{{0, 0}});

        private int cost;
        private int experience;
        private boolean undoable;
        private  boolean actedBased;
        /**
         * Attention! if infinitlyDoable && undoable are true, then while calling unexecute(), only the last previous state registered can be re-established
         */
        private boolean infinitlyDoable;
        private int rangeMin;
        private int rangeMax;
        private RangedBasedType rangedBasedType;
        private boolean endTurnActionOnly;
        /**
         * TempoAreaWidget of impact :
         * ( 0, 0) = targeted tile
         * ( 1, 0) = in front of targeted tile
         * ( 0, 1) = right of targeted tile
         * ( 0,-1) = left of targeted tile
         * (-1, 0) = in the back of targeted tile
         */
        protected Array<int[]> impactArea;

        ActionChoice(int cost, int experience, boolean undoable, boolean actedBased, boolean infinitlyDoable, int rangeMin, int rangeMax, RangedBasedType rangedBasedType, boolean endTurnActionOnly, int[][] impactArea) {
            this.cost = cost;
            this.experience = experience;
            this.rangeMin = rangeMin;
            this.rangeMax = rangeMax;
            this.undoable = undoable;
            this.infinitlyDoable = infinitlyDoable;
            this.actedBased = actedBased;
            this.rangedBasedType = rangedBasedType;
            this.endTurnActionOnly = endTurnActionOnly;
            this.impactArea = new Array<int[]>();
            this.impactArea.addAll(impactArea);
        }

        ActionChoice(int cost, int experience, boolean undoable, boolean actedBased, boolean infinitlyDoable, int rangeMin, int rangeMax, boolean endTurnActionOnly, int[][] impactArea){
            this(cost, experience, undoable, actedBased, infinitlyDoable, rangeMin, rangeMax, RangedBasedType.SPECIFIC, endTurnActionOnly, impactArea);
        }

        ActionChoice(int cost, int experience, boolean undoable, boolean actedBased, boolean infinitlyDoable, RangedBasedType rangedBasedType, boolean endTurnActionOnly, int[][] impactArea){
            this(cost, experience, undoable, actedBased, infinitlyDoable, -1, -1, rangedBasedType, endTurnActionOnly, impactArea);
        }

        public int getCost(int rowActor, int colActor, Unit actor, Battlefield battlefield) {
            int costBannerBonus = 0;
            if(battlefield.isStandardBearerAtRange(actor, rowActor, colActor)){
                costBannerBonus -= (int) actor.getArmy().getSquadBanner(actor, true).getValue(BannerBonus.AP_COST, true);
            }
            if(costBannerBonus > cost)
                costBannerBonus = cost;
            return cost - costBannerBonus;
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

        public boolean isInfinitlyDoable() { return infinitlyDoable; }

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

        public String getName(I18NBundle mainI18nBundle) {
            return mainI18nBundle.get(name());
        }

        public String getName(){
            return name().toLowerCase();
        }
    }


    public enum AreaColor {
        LIGHT_BLUE,
        LIGHT_BROWN,
        DEEP_BLUE,
        TURQUOISE,
        ORANGE,
        YELLOW,
        RED_ORANGE,
        RED_PURPLE,
        WHITE
    }


    public enum AreaType {
        TOUCHED_TILE            (3, true, AreaColor.WHITE),
        SELECTED_UNIT           (0, true, AreaColor.LIGHT_BLUE),
        BANNER_AREA             (0, true, AreaColor.LIGHT_BROWN),
        MOVE_AREA               (1, false, AreaColor.RED_ORANGE),
        ACTION_AREA             (1, false, AreaColor.RED_ORANGE),
        GUARD_AREA              (0, true, AreaColor.DEEP_BLUE),
        DEPLOYMENT_AREA         (2, true, AreaColor.ORANGE),
        VANGUARD_DEPLOYMENT_AREA(2, true, AreaColor.YELLOW),
        FOE_ACTION_AREA         (0, true, AreaColor.RED_PURPLE),
        COVER                   (0, true, AreaColor.TURQUOISE);


        private int layerIndex;
        private boolean rectangular;
        private AreaColor color;

        AreaType(){
            this.color = AreaColor.LIGHT_BLUE;
            this.layerIndex = 0;
            this.rectangular = false;
        }

        AreaType(int layerIndex, boolean rectangular, AreaColor color){
            this.layerIndex = layerIndex;
            this.rectangular = rectangular;
            this.color = color;
        }

        public AreaColor getColor(){
            return color;
        }

        public int getLayerIndex() {
            return layerIndex;
        }

        public boolean isRectangular() {
            return rectangular;
        }
    }


    public enum TileSpriteSetId {
        VILLAGE         (true),
        SANCTUARY       (true),
        STOCKADE        (true),
        CASTLE          (true),
        ANCIENT_SITE    (true),
        RUINS           (true),
        FOREST          (true),
        WOODS           (true),
        MOUNTAINS       (false),
        WATER           (false),
        HILLS           (false),
        SWAMP           (false),
        PLAIN           (false),
        BRIDGE          (true),
        BROKEN_BRIDGE   (true),
        WATCH_TOWER     (true),
        TRAP            (false);

        private boolean upper;

        TileSpriteSetId(boolean upper) {
            this.upper = upper;
        }

        public boolean isUpper() {
            return upper;
        }
    }


    public enum TileType {
        RUINS(          "ruins", 152, 152, 152,             true, true, true,                       0, 0, 1, 0, 0, false,     TileSpriteSetId.PLAIN, TileSpriteSetId.RUINS),
        VILLAGE(        "village", 204, 143, 37,            true, true, true,TileType.RUINS,        5, 0, 1, 0, 0, false,     TileSpriteSetId.PLAIN, TileSpriteSetId.VILLAGE),
        SANCTUARY(      "sanctuary", 228, 56, 56,           true, true, true,TileType.RUINS,        5, 0, 1, 0, 0, false,     TileSpriteSetId.PLAIN, TileSpriteSetId.SANCTUARY),
        STOCKADE(       "stockade", 204, 112, 37,           true, true, true,TileType.RUINS,        5, 0, 2, 0, 0, false,     TileSpriteSetId.PLAIN, TileSpriteSetId.STOCKADE),
        CASTLE(         "castle", 204, 73 ,37 ,             true, true, true,TileType.RUINS,        5, 0, 3, 20, 0, false,    TileSpriteSetId.PLAIN, TileSpriteSetId.CASTLE),
        ANCIENT_SITE(   "ancient site", 38 ,67, 47,         true, true, true,TileType.RUINS,        0, 1, 0, 0, 0, true,      TileSpriteSetId.PLAIN, TileSpriteSetId.ANCIENT_SITE),
        MOUNTAINS(      "mountain", 101, 91, 16,            false, false, true,                     0, 0, 3, 10, 0, false,    TileSpriteSetId.MOUNTAINS, null),
        FOREST(         "deep forest", 5, 96, 34,           false, false, true,                     0, 0, 1, 30, 0, false,    TileSpriteSetId.PLAIN, TileSpriteSetId.FOREST),
        OCEAN(          "deep waters", 25, 157, 197,        false, false,false,                     0, 0, 0, 0, 0, false,     TileSpriteSetId.WATER, null),
        SHALLOWS(       "shallows", 30, 211, 227,           false, false, true,                     0, 0, 0, 0, 0, false,     TileSpriteSetId.WATER, null),
        HILLS(          "hill", 146, 134, 40,               false, true, true,                      0, 1, 0, 0, 0, true,      TileSpriteSetId.HILLS, null),
        WOODS(          "woods", 10, 158, 57,               false, true, true,                      0, 0, 0, 15, 0, false,    TileSpriteSetId.PLAIN, TileSpriteSetId.WOODS),
        SWAMP(          "swamp", 112, 155, 80,              false, true, true,                      0, 0, 0, 0, -15, false,   TileSpriteSetId.SWAMP, null),
        PLAIN(          "plain", 17, 215, 80,               false, true, true,                      0, 0, 0, 0, 0, false,     TileSpriteSetId.PLAIN, null),
        BROKEN_BRIDGE(  "broken bridge", 95, 101, 124,      false, false, false,                    0, 0, 0, 0, 0, false,     TileSpriteSetId.WATER, TileSpriteSetId.BROKEN_BRIDGE),
        BRIDGE(         "bridge", 85, 96, 134,              true, true, true,TileType.BROKEN_BRIDGE,0, 0, 0, 0, 0, false,     TileSpriteSetId.WATER, TileSpriteSetId.BRIDGE),
        WATCH_TOWER(    "watch tower", 193, 26, 137,        true, true, true,                       3, 0, 1, 0, 0, true,      TileSpriteSetId.PLAIN, TileSpriteSetId.WATCH_TOWER),
        TRAP(           "trap", 99, 26, 137,                true, true, true,                       3, 0, 1, 0, 0, true,      TileSpriteSetId.TRAP, null);

        private String name;

        private int r;
        private int g;
        private int b;

        private boolean urbanArea;
        private boolean reachable;          // can be traversable by standard unit and be used to set traps.
        private boolean reachableForPathFinder;
        private TileType damaged;

        private int healPower;
        private int attackMightBonus;
        private int defenseBonus;
        private int avoidBonus;
        private int attackAccBonus;
        private boolean rangeBonus;

        private TileSpriteSetId lowerPart;
        private TileSpriteSetId upperPart;

        TileType(String name, int r, int g, int b,
                 boolean urbanArea, boolean reachable, boolean reachableForPathFinder,
                 int healPower, int attackMightBonus, int defenseBonus, int avoidBonus, int attackAccBonus, boolean rangeBonus,
                 TileSpriteSetId lowerPart, TileSpriteSetId upperPart) {
            this.name = name;
            this.r = r;
            this.g = g;
            this.b = b;
            this.urbanArea = urbanArea;
            this.reachable = reachable;
            this.reachableForPathFinder = reachableForPathFinder;
            this.damaged = this;
            this.healPower = healPower;
            this.attackMightBonus = attackMightBonus;
            this.defenseBonus = defenseBonus;
            this.avoidBonus = avoidBonus;
            this.attackAccBonus = attackAccBonus;
            this.rangeBonus = rangeBonus;
            this.lowerPart = lowerPart;
            this.upperPart = upperPart;
        }

        TileType(String name, int r, int g, int b, boolean urbanArea, boolean reachable, boolean reachableForPathFinder,
                 TileType damaged, int healPower, int attackMightBonus, int defenseBonus, int avoidBonus, int attackAccBonus, boolean rangeBonus,
                 TileSpriteSetId lowerPart, TileSpriteSetId upperPart) {
            this.name = name;
            this.r = r;
            this.g = g;
            this.b = b;
            this.urbanArea = urbanArea;
            this.reachable = reachable;
            this.reachableForPathFinder = reachableForPathFinder;
            this.damaged = damaged;
            this.healPower = healPower;
            this.attackMightBonus = attackMightBonus;
            this.defenseBonus = defenseBonus;
            this.avoidBonus = avoidBonus;
            this.attackAccBonus = attackAccBonus;
            this.rangeBonus = rangeBonus;
            this.lowerPart = lowerPart;
            this.upperPart = upperPart;
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

        public boolean isReachable(boolean pathfinder) {
            return (pathfinder) ? reachableForPathFinder: reachable;
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

        public TileSpriteSetId getLowerPart() {
            return lowerPart;
        }

        public TileSpriteSetId getUpperPart() {
            return upperPart;
        }

        public TileType getDamagedType(){ return damaged; }

        public boolean isDestructible(){
            return this != this.getDamagedType();
        }

        public boolean isPlunderable() {
            return this.getDamagedType() == RUINS;
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
        FIST(           1, 1, 110, 1, 1, 0, WeaponType.FIST, DamageType.BLUNT, Ability.NONE),
        SHORTSWORD(     4, 4, 85, 1, 1, 20, 50, WeaponType.SWORD, DamageType.EDGED, Ability.NONE),
        SPEAR(          3, 3, 100, 1, 1, 20, 50, WeaponType.POLEARM, DamageType.PIERCING, Ability.NONE),
        BROAD_AXE(      3, 5, 95, 1, 1, 20, 50, WeaponType.AXE, DamageType.EDGED, Ability.NONE),
        CLUB(           4, 6, 80, 1, 1, 20, 50, WeaponType.MACE, DamageType.BLUNT, Ability.NONE),
        HUNTING_BOW(    4, 5, 75, 2, 2, 20, 50, WeaponType.BOW, DamageType.PIERCING, Ability.NONE);

        private int damageMin;
        private int damageMax;
        private int accuracy;
        private int rangeMin;
        private int rangeMax;
        private int dropRate;
        private int durabilityMax;
        private boolean unbreakable;
        private WeaponType weaponType;
        private DamageType damageType;
        private Ability ability;

        WeaponTemplate(int damageMin, int damageMax, int accuracy, int rangeMin, int rangeMax, int dropRate, int durability, WeaponType weaponType, DamageType damageType, Ability art) {
            this.damageMin = damageMin;
            this.damageMax = damageMax;
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

        WeaponTemplate(int damageMin, int damageMax, int accuracy, int rangeMin, int rangeMax, int dropRate, WeaponType weaponType, DamageType damageType, Ability art){
            this(damageMin, damageMax, accuracy, rangeMin, rangeMax, dropRate,1, weaponType, damageType, art);
            this.unbreakable = true;
        }

        public int getDropRate() {
            return dropRate;
        }

        public WeaponType getWeaponType() {
            return weaponType;
        }

        public int getDamageMin() { return damageMin; }

        public int getDamageMax() { return damageMax; }

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

        EquipmentTemplate(int dropRate, Ability ability) {
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
        //Offensive
        DOUBLE_ATTACK,
        MULTIPLE_ATTACK,
        IMPALEMENT,
        CRUSHING_BLOW,
        BATTERING_RAM,
        CHARGED_BLOW,
        BACKSTAB,
        CRIPPLING_BLOW,
        FOCUSED_BLOW,
        BLOOD_RAIN,
        SWIRLING_ATTACK,
        GREAT_IMPACT,
        CHARGE,
        HARASS,
        WARCRY,
        WARCALL,
        EXECUTION,

        //Passive
        PATHFINDER,
        UNBREAKABLE,
        SHADOW,
        VIGILANT,       // can not be stolen from

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

        public String getName(I18NBundle bundle) {
            return bundle.get(name());
        }

        public static Orientation random() {
            return values()[MathUtils.random(values().length)];
        }
    }


    /**
     * lore-oriented data
     * if lord the army fight for
     */
    public enum Allegiance{
        NO_ALLIGEANCE;

        public static Allegiance getStandard(){
            return NO_ALLIGEANCE;
        }
    }


    /**
     * define which units are foes with one other.
     */
    public enum Affiliation {
        ALLY,
        ENEMY_0,
        ENEMY_1
    }


    public enum UnitTemplate {
        SOLAIRE(1, 4, 5, new int[]{0, -1, -1, -1, 3, 2}, new int[]{-1, 0, -1, 5, 0, 1}, new Ability[]{Ability.HEAL},
                3, 7, 7, 4, 7, 9, 3, 33, 6, 4, 3,
                0, 2, 2, 1, 2, 2, 1, 9, 2, 2, 2,
                0.00f, 0.15f, 0.10f, 0.10f, 0.10f, 0.15f, 0.15f, 0.55f, 0.40f, 0.40f, 0.45f,
                0.00f, 0.20f, 0.15f, 0.10f, 0.15f, 0.20f, 0.15f, 0.65f, 0.50f, 0.45f, 0.50f),
        SOLAR_KNIGHT(1, 4, 5, new int[]{0, -1, -1, -1, 3, 2}, new int[]{-1, 0, -1, 5, 0, 1}, new Ability[]{Ability.GUARD},
                3, 7, 7, 4, 7, 9, 3, 33, 6, 4, 3,
                0, 2, 2, 1, 2, 2, 1, 9, 2, 2, 2,
                0.00f, 0.15f, 0.10f, 0.10f, 0.10f, 0.15f, 0.15f, 0.55f, 0.40f, 0.40f, 0.45f,
                0.00f, 0.20f, 0.15f, 0.10f, 0.15f, 0.20f, 0.15f, 0.65f, 0.50f, 0.45f, 0.50f);


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

        UnitTemplate(int startingLevel, int footmanMob, int horsemanMob, int[] horsemanBonus, int[] shieldBearerBonus, Ability[] nativeAbilities, int baseLd, int baseStr, int basePiercingArmor, int baseBluntArmor, int baseEgdedArmor, int baseAg, int baseCha, int baseHP, int baseDex, int baseSk, int baseBr, int proBoLd, int proBoStr, int proBoPiercingArmor, int proBoBluntArmor, int proBoEdgedArmor, int proBoAg, int proBoCha, int proBoHP, int proBoDex, int proBoSk, int proBoBr, float growthLd, float growthStr, float growthPiercingArmor, float growthBluntArmor, float growthEdgegArmor, float growthAg, float growthCha, float growthHP, float growthDex, float growthSk, float growthBr, float proGrowthLd, float proGrowthStr, float proGrowthPiercingArmor, float proGrowthBluntArmor, float proGrowthEdgedArmor, float proGrowthDex, float proGrowthCha, float getProGrowthHP, float proGrowthAg, float proGrowthSk, float proGrowthBr) {
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

        public static UnitTemplate getStandard(){
            return SOLAR_KNIGHT;
        }
    }

}
