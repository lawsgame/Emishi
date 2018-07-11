package com.lawsgame.emishitactics.core.helpers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.constants.Data;

import java.util.HashMap;

/*
 singleton to easily get access to required texture region for rendering purpose
 */
public class TempoSprite2DPool {


    private static TempoSprite2DPool sprite2DPool = null;

    public static TempoSprite2DPool get(){
        if(sprite2DPool == null){
            sprite2DPool = new TempoSprite2DPool();
        }
        return sprite2DPool;
    }

    private HashMap<Data.TileType, TextureRegion> tileSprites;
    private HashMap<Assets.HighlightedTile, TextureRegion> uiSprites;
    private TextureRegion blackBGSprite;
    private HashMap<Data.AnimationId, TextureRegion> unitSprites;
    private HashMap<Data.AnimationId, TextureRegion> foeSprites;
    private TextureRegion unitDoneSprite;
    private HashMap<Data.Weapon, TextureRegion> weaponSprites;
    private HashMap<Data.Orientation, TextureRegion> orientationSprites;
    private HashMap<Data.OffensiveAbility, TextureRegion> offensiveAbilitySprites;
    private TextureRegion shieldSprite;
    private TextureRegion mountedSprite;
    private TextureRegion bridgeInConstruction;
    private TextureRegion towerInConstruction;


    private TempoSprite2DPool(){
        this.tileSprites = new HashMap<Data.TileType, TextureRegion>();
        this.uiSprites = new HashMap<Assets.HighlightedTile, TextureRegion>();
        this.unitSprites = new HashMap<Data.AnimationId, TextureRegion>();
        this.foeSprites = new HashMap<Data.AnimationId, TextureRegion>();
        this.weaponSprites = new HashMap<Data.Weapon, TextureRegion>();
        this.orientationSprites = new HashMap<Data.Orientation, TextureRegion>();
        this.offensiveAbilitySprites = new HashMap<Data.OffensiveAbility, TextureRegion>();
        shieldSprite = null;
        mountedSprite = null;
        unitDoneSprite = null;


    }

    public void set(AssetManager asm){
        if(asm != null) {
            TextureRegion region;
            String regionName;
            TextureAtlas atlas;
            if(asm.isLoaded(Assets.ATLAS_TILES)) {
                atlas = asm.get(Assets.ATLAS_TILES);

                for (Data.TileType tileType : Data.TileType.values()) {
                    regionName = Assets.getRegionTile(tileType);
                    region = atlas.findRegion(regionName);
                    if (region != null) {
                        this.tileSprites.put(tileType, region);

                    }
                }

                bridgeInConstruction = atlas.findRegion("future_bridge");
                towerInConstruction = atlas.findRegion("future_watch_tower");
            }

            if(asm.isLoaded(Assets.ATLAS_UNITS)) {
                atlas = asm.get(Assets.ATLAS_UNITS);
                region = atlas.findRegion(Assets.REGION_UNIT_SPRITES);
                TextureRegion[][] unitRegions = region.split(32,32);
                TextureRegion[][] iconRegions = region.split(8,8);
                TextureRegion[][] offabbRegions = region.split(32,8);

                unitSprites.put(Data.AnimationId.ATTACK, unitRegions[3][0]);
                unitSprites.put(Data.AnimationId.PUSH, unitRegions[4][0]);
                unitSprites.put(Data.AnimationId.HEAL, unitRegions[5][0]);
                unitSprites.put(Data.AnimationId.LEVELUP, unitRegions[5][0]);
                unitSprites.put(Data.AnimationId.TREATED, unitRegions[5][0]);
                unitSprites.put(Data.AnimationId.STEAL, unitRegions[6][0]);
                unitSprites.put(Data.AnimationId.BUILD, unitRegions[7][0]);
                unitSprites.put(Data.AnimationId.WALK, unitRegions[8][0]);
                unitSprites.put(Data.AnimationId.DODGE, unitRegions[9][0]);
                unitSprites.put(Data.AnimationId.BACKSTABBED, unitRegions[12][0]);
                unitSprites.put(Data.AnimationId.PUSHED, unitRegions[12][0]);
                unitSprites.put(Data.AnimationId.TAKE_HIT, unitRegions[13][0]);
                unitSprites.put(Data.AnimationId.DIE, unitRegions[14][0]);
                unitSprites.put(Data.AnimationId.GUARD, unitRegions[15][0]);
                unitSprites.put(Data.AnimationId.REST, unitRegions[0][0]);

                foeSprites.put(Data.AnimationId.ATTACK, unitRegions[3][4]);
                foeSprites.put(Data.AnimationId.PUSH, unitRegions[4][4]);
                foeSprites.put(Data.AnimationId.HEAL, unitRegions[5][4]);
                foeSprites.put(Data.AnimationId.LEVELUP, unitRegions[5][4]);
                foeSprites.put(Data.AnimationId.TREATED, unitRegions[5][4]);
                foeSprites.put(Data.AnimationId.STEAL, unitRegions[6][4]);
                foeSprites.put(Data.AnimationId.BUILD, unitRegions[7][4]);
                foeSprites.put(Data.AnimationId.WALK, unitRegions[8][4]);
                foeSprites.put(Data.AnimationId.DODGE, unitRegions[9][4]);
                foeSprites.put(Data.AnimationId.BACKSTABBED, unitRegions[12][4]);
                foeSprites.put(Data.AnimationId.PUSHED, unitRegions[12][4]);
                foeSprites.put(Data.AnimationId.TAKE_HIT, unitRegions[13][4]);
                foeSprites.put(Data.AnimationId.DIE, unitRegions[14][4]);
                foeSprites.put(Data.AnimationId.GUARD, unitRegions[15][4]);
                foeSprites.put(Data.AnimationId.REST, unitRegions[0][4]);

                unitDoneSprite = unitRegions[0][6];

                weaponSprites.put(Data.Weapon.KATANA, iconRegions[0][12]);
                weaponSprites.put(Data.Weapon.WARABITE, iconRegions[0][12]);
                weaponSprites.put(Data.Weapon.YARI, iconRegions[0][13]);
                weaponSprites.put(Data.Weapon.SAI, iconRegions[0][14]);
                weaponSprites.put(Data.Weapon.KANABO, iconRegions[0][15]);
                weaponSprites.put(Data.Weapon.BO, iconRegions[1][12]);
                weaponSprites.put(Data.Weapon.YUMI, iconRegions[1][13]);
                weaponSprites.put(Data.Weapon.NODACHI, iconRegions[1][14]);
                weaponSprites.put(Data.Weapon.NAGINATA, iconRegions[1][15]);

                shieldSprite = iconRegions[2][12];
                mountedSprite = iconRegions[2][13];

                orientationSprites.put(Data.Orientation.NORTH, iconRegions[3][12]);
                orientationSprites.put(Data.Orientation.EAST, iconRegions[3][13]);
                orientationSprites.put(Data.Orientation.WEST, iconRegions[3][14]);
                orientationSprites.put(Data.Orientation.SOUTH, iconRegions[3][15]);

                offensiveAbilitySprites.put(Data.OffensiveAbility.FOCUSED_BLOW,    offabbRegions[0][8]);
                offensiveAbilitySprites.put(Data.OffensiveAbility.CRIPPLING_BLOW,  offabbRegions[1][8]);
                offensiveAbilitySprites.put(Data.OffensiveAbility.SWIRLING_BLOW,   offabbRegions[2][8]);
                offensiveAbilitySprites.put(Data.OffensiveAbility.SWIFT_BLOW,      offabbRegions[3][8]);
                offensiveAbilitySprites.put(Data.OffensiveAbility.HEAVY_BLOW,      offabbRegions[4][8]);
                offensiveAbilitySprites.put(Data.OffensiveAbility.CRUNCHING_BLOW,  offabbRegions[5][8]);
                offensiveAbilitySprites.put(Data.OffensiveAbility.WAR_CRY,         offabbRegions[6][8]);
                offensiveAbilitySprites.put(Data.OffensiveAbility.POISONOUS_ATTACK, offabbRegions[7][8]);
                offensiveAbilitySprites.put(Data.OffensiveAbility.GUARD_BREAK,     offabbRegions[8][8]);
                offensiveAbilitySprites.put(Data.OffensiveAbility.LINIENT_BLOW,    offabbRegions[9][8]);
                offensiveAbilitySprites.put(Data.OffensiveAbility.FURY,            offabbRegions[10][8]);
            }

            if(asm.isLoaded(Assets.ATLAS_UI)) {
                atlas = asm.get(Assets.ATLAS_UI);
                for(Assets.HighlightedTile id : Assets.HighlightedTile.values()){
                    region = atlas.findRegion(Assets.getTileHighlighted(id));
                    if(region != null )
                        uiSprites.put(id, region);
                }

                blackBGSprite = atlas.findRegion(Assets.UI_BLACK_BACKGROUND);
            }

        }
    }


    public TextureRegion getTileSprite(Data.TileType tileType) {
        return tileSprites.get(tileType);
    }

    public TextureRegion getUISprite(Assets.HighlightedTile id) {
        return uiSprites.get(id);
    }

    public TextureRegion getMountedSprite() {
        return mountedSprite;
    }

    public TextureRegion getShieldSprite() {
        return shieldSprite;
    }

    public TextureRegion getUnitSprite(Data.AnimationId animationId, boolean ally){
        TextureRegion res;
        if(ally){
            res = unitSprites.get(animationId);
        }else{
            res = foeSprites.get(animationId);
        }
        return res;
    }

    public TextureRegion getDoneUnitSprite(){ return unitDoneSprite;}

    public TextureRegion getWeaponSprite(Data.Weapon weapon){
        return weaponSprites.get(weapon);
    }

    public TextureRegion getOrientationSprite(Data.Orientation orientation) {
        return orientationSprites.get(orientation);
    }

    public TextureRegion getOffensiveAbbSprite(Data.OffensiveAbility offensiveAbility){
        return offensiveAbilitySprites.get(offensiveAbility);
    }

    public TextureRegion getBridgeInConstruction() {
        return bridgeInConstruction;
    }

    public TextureRegion getTowerInConstruction() {
        return towerInConstruction;
    }

    public TextureRegion getBlackBGSprite() {
        return blackBGSprite;
    }
}
