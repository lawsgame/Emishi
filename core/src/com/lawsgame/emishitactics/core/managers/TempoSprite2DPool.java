package com.lawsgame.emishitactics.core.managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.constants.Props;

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

    private HashMap<Props.TileType, TextureRegion> tileSprites;
    private HashMap<Assets.UIAssetId, TextureRegion> uiSprites;
    private HashMap<Props.ActionState, TextureRegion> unitSprites;
    private HashMap<Props.ActionState, TextureRegion> foeSprites;
    private HashMap<Props.ActionState, TextureRegion> unitDoneSprites;
    private HashMap<Props.DefensiveStance, TextureRegion> unitStanceSprites;
    private HashMap<Props.DefensiveStance, TextureRegion> foeStanceSprites;
    private HashMap<Props.DefensiveStance, TextureRegion> unitStanceDoneSprites;
    private HashMap<Props.Weapon, TextureRegion> weaponSprites;
    private HashMap<Props.Orientation, TextureRegion> orientationSprites;
    private TextureRegion shieldSprite;
    private TextureRegion mountedSprite;


    private TempoSprite2DPool(){
        this.tileSprites = new HashMap<Props.TileType, TextureRegion>();
        this.uiSprites = new HashMap<Assets.UIAssetId, TextureRegion>();
        this.unitSprites = new HashMap<Props.ActionState, TextureRegion>();
        this.foeSprites = new HashMap<Props.ActionState, TextureRegion>();
        this.unitDoneSprites = new HashMap<Props.ActionState, TextureRegion>();
        this.unitStanceSprites = new HashMap<Props.DefensiveStance, TextureRegion>();
        this.foeStanceSprites = new HashMap<Props.DefensiveStance, TextureRegion>();
        this.unitStanceDoneSprites = new HashMap<Props.DefensiveStance, TextureRegion>();
        this.weaponSprites = new HashMap<Props.Weapon, TextureRegion>();
        this.orientationSprites = new HashMap<Props.Orientation, TextureRegion>();
        shieldSprite = null;
        mountedSprite = null;
    }

    public void set(AssetManager asm){
        //TODO:

        if(asm != null) {
            TextureRegion region;
            String regionName;
            TextureAtlas atlas;
            if(asm.isLoaded(Assets.ATLAS_TILES)) {
                atlas = asm.get(Assets.ATLAS_TILES);

                for (Props.TileType tileType : Props.TileType.values()) {
                    regionName = Assets.getRegionTile(tileType);
                    region = atlas.findRegion(regionName);
                    if (region != null) {
                        this.tileSprites.put(tileType, region);

                    }
                }
            }

            if(asm.isLoaded(Assets.ATLAS_UNITS)) {
                atlas = asm.get(Assets.ATLAS_UNITS);
                region = atlas.findRegion(Assets.REGION_UNIT_SPRITES);
                TextureRegion[][] unitRegions = region.split(32,32);
                TextureRegion[][] iconRegions = region.split(8,8);

                unitSprites.put(Props.ActionState.ATTACK, unitRegions[3][0]);
                unitSprites.put(Props.ActionState.PUSH, unitRegions[4][0]);
                unitSprites.put(Props.ActionState.HEAL, unitRegions[5][0]);
                unitSprites.put(Props.ActionState.STEAL, unitRegions[6][0]);
                unitSprites.put(Props.ActionState.BUILD, unitRegions[7][0]);
                unitSprites.put(Props.ActionState.WALK, unitRegions[8][0]);
                unitSprites.put(Props.ActionState.DODGE, unitRegions[9][0]);
                unitSprites.put(Props.ActionState.BLOCK, unitRegions[10][0]);
                unitSprites.put(Props.ActionState.PARRY, unitRegions[11][0]);
                unitSprites.put(Props.ActionState.BACKSTAB, unitRegions[12][0]);
                unitSprites.put(Props.ActionState.WOUNDED, unitRegions[13][0]);
                unitSprites.put(Props.ActionState.DIE, unitRegions[14][0]);
                unitStanceSprites.put(Props.DefensiveStance.DODGE, unitRegions[0][0]);
                unitStanceSprites.put(Props.DefensiveStance.BLOCK, unitRegions[1][0]);
                unitStanceSprites.put(Props.DefensiveStance.PARRY, unitRegions[2][0]);

                foeSprites.put(Props.ActionState.ATTACK, unitRegions[3][4]);
                foeSprites.put(Props.ActionState.PUSH, unitRegions[4][4]);
                foeSprites.put(Props.ActionState.HEAL, unitRegions[5][4]);
                foeSprites.put(Props.ActionState.STEAL, unitRegions[6][4]);
                foeSprites.put(Props.ActionState.BUILD, unitRegions[7][4]);
                foeSprites.put(Props.ActionState.WALK, unitRegions[8][4]);
                foeSprites.put(Props.ActionState.DODGE, unitRegions[9][4]);
                foeSprites.put(Props.ActionState.BLOCK, unitRegions[10][4]);
                foeSprites.put(Props.ActionState.PARRY, unitRegions[11][4]);
                foeSprites.put(Props.ActionState.BACKSTAB, unitRegions[12][4]);
                foeSprites.put(Props.ActionState.WOUNDED, unitRegions[13][4]);
                foeSprites.put(Props.ActionState.DIE, unitRegions[14][4]);
                foeStanceSprites.put(Props.DefensiveStance.DODGE, unitRegions[0][4]);
                foeStanceSprites.put(Props.DefensiveStance.BLOCK, unitRegions[1][4]);
                foeStanceSprites.put(Props.DefensiveStance.PARRY, unitRegions[2][4]);

                unitDoneSprites.put(Props.ActionState.ATTACK, unitRegions[3][6]);
                unitDoneSprites.put(Props.ActionState.PUSH, unitRegions[4][6]);
                unitDoneSprites.put(Props.ActionState.HEAL, unitRegions[5][6]);
                unitDoneSprites.put(Props.ActionState.STEAL, unitRegions[6][6]);
                unitDoneSprites.put(Props.ActionState.BUILD, unitRegions[7][6]);
                unitDoneSprites.put(Props.ActionState.WALK, unitRegions[8][6]);
                unitDoneSprites.put(Props.ActionState.DODGE, unitRegions[9][6]);
                unitDoneSprites.put(Props.ActionState.BLOCK, unitRegions[10][6]);
                unitDoneSprites.put(Props.ActionState.PARRY, unitRegions[11][6]);
                unitDoneSprites.put(Props.ActionState.BACKSTAB, unitRegions[12][6]);
                unitDoneSprites.put(Props.ActionState.WOUNDED, unitRegions[13][6]);
                unitDoneSprites.put(Props.ActionState.DIE, unitRegions[14][6]);
                unitStanceDoneSprites.put(Props.DefensiveStance.DODGE, unitRegions[0][6]);
                unitStanceDoneSprites.put(Props.DefensiveStance.BLOCK, unitRegions[1][6]);
                unitStanceDoneSprites.put(Props.DefensiveStance.PARRY, unitRegions[2][6]);

                weaponSprites.put(Props.Weapon.KATANA, iconRegions[0][12]);
                weaponSprites.put(Props.Weapon.YARI, iconRegions[0][13]);
                weaponSprites.put(Props.Weapon.SAI, iconRegions[0][14]);
                weaponSprites.put(Props.Weapon.KANABO, iconRegions[0][15]);
                weaponSprites.put(Props.Weapon.BO, iconRegions[1][12]);
                weaponSprites.put(Props.Weapon.YUMI, iconRegions[1][13]);
                weaponSprites.put(Props.Weapon.NODACHI, iconRegions[1][14]);
                weaponSprites.put(Props.Weapon.NAGINATA, iconRegions[1][15]);

                shieldSprite = iconRegions[2][12];
                mountedSprite = iconRegions[2][13];

                orientationSprites.put(Props.Orientation.NORTH, iconRegions[3][12]);
                orientationSprites.put(Props.Orientation.EAST, iconRegions[3][13]);
                orientationSprites.put(Props.Orientation.WEST, iconRegions[3][14]);
                orientationSprites.put(Props.Orientation.SOUTH, iconRegions[3][15]);
            }

            if(asm.isLoaded(Assets.ATLAS_UI)) {
                atlas = asm.get(Assets.ATLAS_UI);
                for(Assets.UIAssetId id : Assets.UIAssetId.values()){
                    region = atlas.findRegion(Assets.getRegionUI(id));
                    if(region != null )
                        uiSprites.put(id, region);
                }
            }

        }
    }


    public TextureRegion getTileSprite(Props.TileType tileType) {
        return tileSprites.get(tileType);
    }


    public TextureRegion getUISprite(Assets.UIAssetId id) {
        return uiSprites.get(id);
    }

    public TextureRegion getMountedSprite() {
        return mountedSprite;
    }

    public TextureRegion getShieldSprite() {
        return shieldSprite;
    }

    public TextureRegion getUnitSprite(Props.ActionState actionState, boolean foe, boolean done){
        TextureRegion res;
        if(done){
            res = unitDoneSprites.get(actionState);
        }else{
            if(foe){
                res = foeSprites.get(actionState);
            }else{
                res = unitSprites.get(actionState);
            }
        }
        return res;
    }

    public TextureRegion getUnitSprite(Props.DefensiveStance stance, boolean foe, boolean done){
        TextureRegion res;
        if(done){
            res = unitStanceDoneSprites.get(stance);
        }else{
            if(foe){
                res = foeStanceSprites.get(stance);
            }else{
                res = unitStanceSprites.get(stance);
            }
        }
        return res;
    }

    public TextureRegion getWeaponSprite(Props.Weapon weapon){
        return weaponSprites.get(weapon);
    }

    public TextureRegion getOrientationSprite(Props.Orientation orientation) {
        return orientationSprites.get(orientation);
    }
}
