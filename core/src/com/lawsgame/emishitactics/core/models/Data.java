package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Random;

public class Data {


    //MODEL parameters
    public static final int PROMOTION_LEVEL = 10;
    public static final int MAX_LEVEL = 30;
    public static final int HEAL_BASE_POWER = 3;
    public static final float MAX_UNITS_UNDER_WARLORD = 6; // including the warlord himself / herself
    public static final float MAX_UNITS_UNDER_WAR_CHIEF = 5; // including the war chief himself / herself
    public static final int AP_REGEN = 1;
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
    public static final int BRAVERY_MORAL_RECOVERY_RATE = 3;
    public static final float CHANCE_OF_COLLAPSING_FROM_FRAGILE_TILES = 1f; //over 1
    public static final int TRAP_DAMAGE = 3;
    public static final int SCAN_AREA_RANGE = 3;

    // EXP parameter
    public static final double EXP_ALPHA = 0.15;
    public static final double EXP_LVL_GAP_FACTOR = 3;
    public static final double EXP_WOUNDED_ONLY_FACTOR = 0.33333;
    public static final int EXP_REQUIRED_LEVEL_UP = 100;
    public static final int EXP_REQUIRED_LD_LEVEL_UP = 100;

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
    public static final float CAM_WAITING_TIME_BEFORE_PROCEEDING_TO_THE_NEXT_ACTION = 0.3f;

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

        public static Environment getDefaultValue(){
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

        public static Weather getDefaultValue(){
            return SUNNY;
        }
    }


    /**
     * !!! ATTENTION !!!
     *
     * modifying there names can lead to a asset provider error!
     */
    public enum AnimUnitSSId {
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

        AnimUnitSSId(boolean rest0) {
            this.rest0 = rest0;
        }

        public boolean isRest0() {
            return rest0;
        }
    }


    public enum AnimId {
        WALK(           AnimUnitSSId.WALK_FLEE_SWITCHPOSITION, AnimUnitSSId.WALK_FLEE_SWITCHPOSITION, true, true, ANIMATION_WALK_SPEED, false, false),
        BACKSTAB(       AnimUnitSSId.PUSHED_BACKSTABBED),
        TREATED(        AnimUnitSSId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED),
        PUSHED(         AnimUnitSSId.PUSHED_BACKSTABBED),
        FLEE(           AnimUnitSSId.WALK_FLEE_SWITCHPOSITION, AnimUnitSSId.WALK_FLEE_SWITCHPOSITION, true, true, ANIMATION_FLEE_SPEED, true, false),
        PUSH(           AnimUnitSSId.PUSH),
        HEAL(           AnimUnitSSId.HEAL),
        STEAL(          AnimUnitSSId.STEAL),
        BUILD(          AnimUnitSSId.BUILD),
        GUARD(          AnimUnitSSId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED),
        IDLE(           AnimUnitSSId.REST, AnimUnitSSId.BANNER, true, true, ANIMATION_NORMAL_SPEED, false, true),
        REGULAR_ATTACK( AnimUnitSSId.ATTACK),
        PLUNDER(        AnimUnitSSId.ATTACK),
        COVER(          AnimUnitSSId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED),
        DODGE(          AnimUnitSSId.DODGE),
        DIE(            AnimUnitSSId.DIE),
        GUARDED(        AnimUnitSSId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED),
        WOUNDED(        AnimUnitSSId.WOUNDED),
        LEVELUP(        AnimUnitSSId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED),
        SWITCH_WEAPON(  AnimUnitSSId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED),
        SPECIAL_MOVE(   AnimUnitSSId.SPECIAL_MOVE),
        CHANGE_STRATEGY(AnimUnitSSId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED),
        PICK_LOOT (     AnimUnitSSId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED),
        SCAN_AREA(      AnimUnitSSId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED),
        BOOST(  AnimUnitSSId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED);

        AnimUnitSSId soldierId;
        AnimUnitSSId warchiefId;
        boolean loop;
        boolean backnforth;
        float speed;
        boolean timeLimited;
        boolean randomlyStarted;

        AnimId(AnimUnitSSId soldierId, AnimUnitSSId warchiefId, boolean loop, boolean backnforth, float speed, boolean timeLimited, boolean randomlyStarted) {
            this.soldierId = soldierId;
            this.warchiefId = warchiefId;
            this.loop = loop;
            this.backnforth = backnforth;
            this.speed = speed;
            this.timeLimited = timeLimited;
            this.randomlyStarted = randomlyStarted;
        }

        AnimId(AnimUnitSSId id) {
            this(id, id, false, false, Data.ANIMATION_NORMAL_SPEED, true, false);
        }

        public AnimUnitSSId getSpriteSetId(boolean warchief) {
            return (warchief) ?  warchiefId : soldierId;
        }

        public AnimUnitSSId getSpriteSetId(){ return soldierId; }

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
        ATTACK              (1, 0, false, true, false, RangedBasedType.WEAPON, false, new int[][]{{0, 0}}),
        SWITCH_POSITION     (0, 0, true, false, false, 1, 1, false, new int[][]{{0, 0}}),
        PUSH                (0, 0, false, true, false, 1, 1, false, new int[][]{{0, 0}}),
        SWITCH_WEAPON       (0, 0, true, true, true, 0, 0, false, new int[][]{{0, 0}}),
        CHOOSE_ORIENTATION  (0, 0, false, true, false, 0, 0, true, new int[][]{{0, 0}}),
        HEAL                (1, 10, false, true, false, 0, 0, false, new int[][]{{1, 0}, {0, 1}, {-1, 0},{0, -1}}),
        GUARD               (1, 10, false, true, false, 0, 0, false, new int[][]{{0, 0}}),
        STEAL               (1, 10, false, true, false, 1, 1, false, new int[][]{{0, 0}}),
        BUILD               (3, 10, false, true, false, 1, 1, false, new int[][]{{0, 0}}),
        PLUNDER             (1, 10, false, true, false, 0, 0, false, new int[][]{{0, 0}}),
        COVER_AREA          (1, 10, false, true, false, 0, 0, false, new int[][]{{0, 0}}),
        SCAN_AREA           (1, 20, false, true, false, 0, 0, false, new int[][]{{0, 0}}),
        CHANGE_TACTIC       (0, 0, true, true, true, 0, 0, false, new int[][]{{0, 0}}),
        PICK_LOOT           (0, 0, false, true, false, 0, 0, false, new int[][]{{0, 0}}),
        VISIT(0, 0, false, true, false, 0, 0, false, new int[][]{{0, 0}}),
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
            int costBannerBonus = (int) Formulas.getCurrentUnitBannerBonus(actor, rowActor, colActor, battlefield, BannerBonus.AP_COST);
            return (costBannerBonus <= cost) ? cost - costBannerBonus : 0;
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
        UNDEFINED       (false),
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
        TRAP(           "trap", 99, 26, 137,                true, true, true,                       0, 0, 0, 0, 0, true,      TileSpriteSetId.TRAP, null);

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

        public static TileType getDefaultValue(){
            return PLAIN;
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
        FIST;

        public static WeaponType getDefaultValue(){
            return FIST;
        }
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
        SPOON(0,Ability.NONE);

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
        EXPLORE,

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
        NO_ALLIGEANCE,
        PLAYER_ALLIGIANCE;

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

    public enum UnitStat{
        MOBILITY,
        LEADERSHIP,
        CHARISMA,
        HIT_POINTS,
        BRAVERY,
        STRENGTH,
        ARMOR_PIERCING,
        ARMOR_BLUNT,
        ARMOR_EDGED,
        AGILITY,
        DEXTERITY,
        SKILL,
        LUCK

    }

    public enum UnitTemplate {
        SOLAIRE(1, new Ability[]{Ability.HEAL},
                4, 3, 3, 36, 9, 8, 5, 4, 6, 7, 3, 2, 2,
                1, 0, 2, 9,  1, 2, 2, 1, 1, 2, 2, 2, 3,
                0.00f, 0.15f, 0.40f, 0.60f, 0.40f, 0.15f, 0.15f, 0.15f, 0.40f, 0.40f, 0.40f, 0.40f,
                0.00f, 0.20f, 0.50f, 0.70f, 0.50f, 0.20f, 0.20f, 0.20f, 0.50f, 0.50f, 0.50f, 0.50f),
        SOLAR_KNIGHT(1, new Ability[]{Ability.GUARD},
                4, 3, 3, 25, 5, 8, 5, 4, 6, 7, 3, 2, 2,
                1, 0, 2, 9,  1, 2, 2, 1, 1, 2, 2, 2, 3,
                0.00f, 0.15f, 0.40f, 0.60f, 0.40f, 0.15f, 0.15f, 0.15f, 0.40f, 0.40f, 0.40f, 0.40f,
                0.00f, 0.20f, 0.50f, 0.70f, 0.50f, 0.20f, 0.20f, 0.20f, 0.50f, 0.50f, 0.50f, 0.50f);


        private int startingLevel;
        private Ability[] nativeAbilities;

        private int baseMob;
        private int baseLd;
        private int baseCha;
        private int baseHP;
        private int baseBr;
        private int baseStr;
        private int basePiercingArmor;
        private int baseBluntArmor;
        private int baseEgdedArmor;
        private int baseAg;
        private int baseDex;
        private int baseSk;
        private int baseLuck;

        private int proBoMob;
        private int proBoLd;
        private int proBoCha;
        private int proBoHP;
        private int proBoBr;
        private int proBoStr;
        private int proBoPiercingArmor;
        private int proBoBluntArmor;
        private int proBoEdgedArmor;
        private int proBoAg;
        private int proBoDex;
        private int proBoSk;
        private int proBoLuck;

        private float growthLd;
        private float growthCha;
        private float growthHP;
        private float growthBr;
        private float growthStr;
        private float growthPiercingArmor;
        private float growthBluntArmor;
        private float growthEdgegArmor;
        private float growthAg;
        private float growthDex;
        private float growthSk;
        private float growthLuck;

        private float proGrowthLd;
        private float proGrowthCha;
        private float proGrowthHP;
        private float proGrowthBr;
        private float proGrowthStr;
        private float proGrowthPiercingArmor;
        private float proGrowthBluntArmor;
        private float proGrowthEdgedArmor;
        private float proGrowthAg;
        private float proGrowthDex;
        private float proGrowthSk;
        private float proGrowthLuck;

        UnitTemplate(int startingLevel, Ability[] nativeAbilities, int baseMob, int baseLd, int baseCha, int baseHP, int baseBr, int baseStr, int basePiercingArmor, int baseBluntArmor, int baseEgdedArmor, int baseAg, int baseDex, int baseSk, int baseLuck, int proBoMob, int proBoLd, int proBoCha, int proBoHP, int proBoBr, int proBoStr, int proBoPiercingArmor, int proBoBluntArmor, int proBoEdgedArmor, int proBoAg, int proBoDex, int proBoSk, int proBoLuck, float growthLd, float growthCha, float growthHP, float growthBr, float growthStr, float growthPiercingArmor, float growthBluntArmor, float growthEdgegArmor, float growthAg, float growthDex, float growthSk, float growthLuck, float proGrowthLd, float proGrowthCha, float proGrowthHP, float proGrowthBr, float proGrowthStr, float proGrowthPiercingArmor, float proGrowthBluntArmor, float proGrowthEdgedArmor, float proGrowthAg, float proGrowthDex, float proGrowthSk, float proGrowthLuck) {
            this.startingLevel = startingLevel;
            this.nativeAbilities = nativeAbilities;
            this.baseMob = baseMob;
            this.baseLd = baseLd;
            this.baseCha = baseCha;
            this.baseHP = baseHP;
            this.baseBr = baseBr;
            this.baseStr = baseStr;
            this.basePiercingArmor = basePiercingArmor;
            this.baseBluntArmor = baseBluntArmor;
            this.baseEgdedArmor = baseEgdedArmor;
            this.baseAg = baseAg;
            this.baseDex = baseDex;
            this.baseSk = baseSk;
            this.baseLuck = baseLuck;
            this.proBoMob = proBoMob;
            this.proBoLd = proBoLd;
            this.proBoCha = proBoCha;
            this.proBoHP = proBoHP;
            this.proBoBr = proBoBr;
            this.proBoStr = proBoStr;
            this.proBoPiercingArmor = proBoPiercingArmor;
            this.proBoBluntArmor = proBoBluntArmor;
            this.proBoEdgedArmor = proBoEdgedArmor;
            this.proBoAg = proBoAg;
            this.proBoDex = proBoDex;
            this.proBoSk = proBoSk;
            this.proBoLuck = proBoLuck;
            this.growthLd = growthLd;
            this.growthCha = growthCha;
            this.growthHP = growthHP;
            this.growthBr = growthBr;
            this.growthStr = growthStr;
            this.growthPiercingArmor = growthPiercingArmor;
            this.growthBluntArmor = growthBluntArmor;
            this.growthEdgegArmor = growthEdgegArmor;
            this.growthAg = growthAg;
            this.growthDex = growthDex;
            this.growthSk = growthSk;
            this.growthLuck = growthLuck;
            this.proGrowthLd = proGrowthLd;
            this.proGrowthCha = proGrowthCha;
            this.proGrowthHP = proGrowthHP;
            this.proGrowthBr = proGrowthBr;
            this.proGrowthStr = proGrowthStr;
            this.proGrowthPiercingArmor = proGrowthPiercingArmor;
            this.proGrowthBluntArmor = proGrowthBluntArmor;
            this.proGrowthEdgedArmor = proGrowthEdgedArmor;
            this.proGrowthAg = proGrowthAg;
            this.proGrowthDex = proGrowthDex;
            this.proGrowthSk = proGrowthSk;
            this.proGrowthLuck = proGrowthLuck;
        }

        public static UnitTemplate getDefaultValue(boolean character) {
            return (character) ? SOLAIRE : SOLAR_KNIGHT;
        }


        public int getStartingLevel() {
            return startingLevel;
        }

        public String getName(I18NBundle bundle) {
            return bundle.get(name());
        }

        public Ability[] getNativeAbilities() { return nativeAbilities; }

        public int getBaseStat(UnitStat stat){
            switch (stat){

                case MOBILITY: return baseMob;
                case LEADERSHIP: return baseLd;
                case CHARISMA: return baseCha;
                case HIT_POINTS: return baseHP;
                case BRAVERY: return baseBr;
                case STRENGTH: return baseStr;
                case ARMOR_PIERCING: return basePiercingArmor;
                case ARMOR_BLUNT: return  baseBluntArmor;
                case ARMOR_EDGED: return baseEgdedArmor;
                case AGILITY: return baseAg;
                case DEXTERITY: return baseDex;
                case SKILL: return baseSk;
                case LUCK: return baseLuck;
            }
            return 0;
        }

        public int getProBoStat(UnitStat stat){
            switch (stat){

                case MOBILITY: return proBoMob;
                case LEADERSHIP: return proBoLd;
                case CHARISMA: return proBoCha;
                case HIT_POINTS: return proBoHP;
                case BRAVERY: return proBoBr;
                case STRENGTH: return proBoStr;
                case ARMOR_PIERCING: return proBoPiercingArmor;
                case ARMOR_BLUNT: return  proBoBluntArmor;
                case ARMOR_EDGED: return proBoEdgedArmor;
                case AGILITY: return proBoAg;
                case DEXTERITY: return proBoDex;
                case SKILL: return proBoSk;
                case LUCK: return proBoLuck;
            }
            return 0;
        }


        public float getStatGrowth(UnitStat stat, boolean promoted){
            switch (stat){

                case MOBILITY: return 0;
                case LEADERSHIP: return (promoted) ? proGrowthLd : growthLd;
                case CHARISMA: return (promoted) ? proGrowthCha : growthCha;
                case HIT_POINTS: return (promoted) ? proGrowthHP : growthHP;
                case BRAVERY: return (promoted) ? proGrowthBr : growthBr;
                case STRENGTH: return (promoted) ? proGrowthStr : growthStr;
                case ARMOR_PIERCING: return (promoted) ? proGrowthPiercingArmor : growthPiercingArmor;
                case ARMOR_BLUNT: return  (promoted) ? proGrowthBluntArmor : growthBluntArmor;
                case ARMOR_EDGED: return (promoted) ? proGrowthEdgedArmor : growthEdgegArmor;
                case AGILITY: return (promoted) ? proGrowthAg : growthAg;
                case DEXTERITY: return (promoted) ? proGrowthDex : growthDex;
                case SKILL: return (promoted) ? proGrowthSk : growthSk;
                case LUCK: return (promoted) ? proGrowthLuck : growthLuck;
            }
            return 0;
        }
    }

}
