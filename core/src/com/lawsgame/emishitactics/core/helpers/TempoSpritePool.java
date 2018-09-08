package com.lawsgame.emishitactics.core.helpers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.SpriteSetId;

import java.util.HashMap;

/*
 singleton to easily getInstance access to required texture region for rendering purpose
 */
public class TempoSpritePool {

    private static TempoSpritePool spritePool;

    public static TempoSpritePool getInstance(){
        if(spritePool == null)
            spritePool = new TempoSpritePool();
        return spritePool;
    }

    private TempoSpritePool(){
        this.tileSprites = new HashMap<Data.TileType, TextureRegion>();
        this.areaSprites = new HashMap<Data.AreaType, TextureRegion>();
        this.unitSprites = new HashMap<Data.SpriteSetId, TextureRegion>();
        this.foeSprites = new HashMap<Data.SpriteSetId, TextureRegion>();
        this.weaponSprites = new HashMap<Data.WeaponType, TextureRegion>();
        this.orientationSprites = new HashMap<Data.Orientation, TextureRegion>();
    }

    private HashMap<Data.TileType, TextureRegion> tileSprites;
    private TextureRegion bridgeInConstruction = null;
    private TextureRegion towerInConstruction = null;
    private HashMap<Data.AreaType, TextureRegion> areaSprites;
    private TextureRegion blackBGSprite = null;
    public TextureRegion buttonUp = null;
    public TextureRegion buttonDown = null;
    private HashMap<Data.SpriteSetId, TextureRegion> unitSprites;
    private HashMap<Data.SpriteSetId, TextureRegion> foeSprites;
    private TextureRegion unitDoneSprite = null;
    private HashMap<Data.WeaponType, TextureRegion> weaponSprites;
    private HashMap<Data.Orientation, TextureRegion> orientationSprites;
    private TextureRegion shieldSprite = null;
    private TextureRegion mountedSprite = null;

    public HashMap<Data.AreaType,Sprite> topLeftCorner = new HashMap<Data.AreaType, Sprite>();
    public HashMap<Data.AreaType,Sprite> topRightCorner = new HashMap<Data.AreaType, Sprite>();
    public HashMap<Data.AreaType,Sprite> bottomLeftCorner = new HashMap<Data.AreaType, Sprite>();
    public HashMap<Data.AreaType,Sprite> bottomRightCorner = new HashMap<Data.AreaType, Sprite>();
    public HashMap<Data.AreaType,Sprite> middle = new HashMap<Data.AreaType, Sprite>();
    public HashMap<Data.AreaType,Sprite> westStraight = new HashMap<Data.AreaType, Sprite>();
    public HashMap<Data.AreaType,Sprite> northStraight = new HashMap<Data.AreaType, Sprite>();
    public HashMap<Data.AreaType,Sprite> southStraight = new HashMap<Data.AreaType, Sprite>();
    public HashMap<Data.AreaType,Sprite> eastStraight = new HashMap<Data.AreaType, Sprite>();
    public HashMap<Data.AreaType,Sprite> topLeftAnticorner = new HashMap<Data.AreaType, Sprite>();
    public HashMap<Data.AreaType,Sprite> topRightAnticorner = new HashMap<Data.AreaType, Sprite>();
    public HashMap<Data.AreaType,Sprite> bottomLeftAnticorner = new HashMap<Data.AreaType, Sprite>();
    public HashMap<Data.AreaType,Sprite> bottomRightAnticorner = new HashMap<Data.AreaType, Sprite>();


    public void set(AssetManager asm){
        if(asm != null) {
            TextureRegion region;
            String regionName;
            TextureAtlas atlas;


            if(asm.isLoaded(Assets.ATLAS_TEMPO_TILES)) {
                atlas = asm.get(Assets.ATLAS_TEMPO_TILES);
                for (Data.TileType tileType : Data.TileType.values()) {
                    regionName = Assets.getRegionTile(tileType);
                    region = atlas.findRegion(regionName);
                    if (region != null) {
                        this.tileSprites.put(tileType, region);
                    }else{
                        this.tileSprites.put(tileType, atlas.findRegion(Assets.REGION_TERRAINS_UNDEFINED));
                    }
                }
            }


            if(asm.isLoaded(Assets.ATLAS_TEMPO_UNITS)) {
                atlas = asm.get(Assets.ATLAS_TEMPO_UNITS);
                region = atlas.findRegion(Assets.REGION_UNIT_SPRITES);
                TextureRegion[][] unitRegions = region.split(32,32);
                TextureRegion[][] iconRegions = region.split(8,8);

                unitSprites.put(SpriteSetId.REGULAR_ATTACK, unitRegions[3][0]);
                unitSprites.put(SpriteSetId.PUSH, unitRegions[4][0]);
                unitSprites.put(SpriteSetId.HEAL, unitRegions[5][0]);
                unitSprites.put(SpriteSetId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED, unitRegions[5][0]);
                unitSprites.put(SpriteSetId.STEAL, unitRegions[6][0]);
                unitSprites.put(SpriteSetId.BUILD, unitRegions[7][0]);
                unitSprites.put(SpriteSetId.WALK_FLEE_SWITCHPOSITION, unitRegions[8][0]);
                unitSprites.put(SpriteSetId.DODGE, unitRegions[9][0]);
                unitSprites.put(SpriteSetId.BACKSTABED_PUSHED, unitRegions[12][0]);
                unitSprites.put(SpriteSetId.TAKE_HIT, unitRegions[13][0]);
                unitSprites.put(SpriteSetId.DIE, unitRegions[14][0]);
                unitSprites.put(SpriteSetId.REST, unitRegions[0][0]);

                foeSprites.put(SpriteSetId.REGULAR_ATTACK, unitRegions[3][4]);
                foeSprites.put(SpriteSetId.PUSH, unitRegions[4][4]);
                foeSprites.put(SpriteSetId.HEAL, unitRegions[5][4]);
                foeSprites.put(SpriteSetId.LEVELUP_HEALED_SWITHWEAPON_GUARD_GUARDED, unitRegions[5][4]);
                foeSprites.put(SpriteSetId.STEAL, unitRegions[6][4]);
                foeSprites.put(SpriteSetId.BUILD, unitRegions[7][4]);
                foeSprites.put(SpriteSetId.WALK_FLEE_SWITCHPOSITION, unitRegions[8][4]);
                foeSprites.put(SpriteSetId.DODGE, unitRegions[9][4]);
                foeSprites.put(SpriteSetId.BACKSTABED_PUSHED, unitRegions[12][4]);
                foeSprites.put(SpriteSetId.TAKE_HIT, unitRegions[13][4]);
                foeSprites.put(SpriteSetId.DIE, unitRegions[14][4]);
                foeSprites.put(SpriteSetId.REST, unitRegions[0][4]);

                unitDoneSprite = unitRegions[0][6];

                weaponSprites.put(Data.WeaponType.SWORD, iconRegions[0][12]);
                weaponSprites.put(Data.WeaponType.POLEARM, iconRegions[0][13]);
                weaponSprites.put(Data.WeaponType.FIST, iconRegions[0][14]);
                weaponSprites.put(Data.WeaponType.MACE, iconRegions[0][15]);
                weaponSprites.put(Data.WeaponType.AXE, iconRegions[1][12]);
                weaponSprites.put(Data.WeaponType.BOW, iconRegions[1][13]);

                shieldSprite = iconRegions[2][12];
                mountedSprite = iconRegions[2][13];

                orientationSprites.put(Data.Orientation.NORTH, iconRegions[3][12]);
                orientationSprites.put(Data.Orientation.EAST, iconRegions[3][13]);
                orientationSprites.put(Data.Orientation.WEST, iconRegions[3][14]);
                orientationSprites.put(Data.Orientation.SOUTH, iconRegions[3][15]);
            }

            if(asm.isLoaded(Assets.ATLAS_TEMPO_UI)) {
                atlas = asm.get(Assets.ATLAS_TEMPO_UI);
                for(Data.AreaType id : Data.AreaType.values()){
                    region = atlas.findRegion(Assets.getTileHighlighted(id));
                    if(region != null )
                        areaSprites.put(id, region);
                }

                blackBGSprite = atlas.findRegion(Assets.UI_BLACK_BACKGROUND);
                buttonDown = atlas.findRegion(Assets.UI_BUTTON_DOWN);
                buttonUp = atlas.findRegion(Assets.UI_BUTTON_UP);

                // SET AREA COLOR SPRITE
                Sprite sprite;
                for(Data.AreaType id: Data.AreaType.values()){
                    region = atlas.findRegion(Assets.getTileHighlighted(id));
                    TextureRegion[][] assets = region.split(region.getRegionWidth()/2, region.getRegionHeight()/2);

                    sprite = new Sprite(assets[0][0]);
                    sprite.setSize(0.5f,0.5f);
                    topLeftCorner.put(id, sprite);
                    sprite = new Sprite(assets[0][0]);
                    sprite.setSize(0.5f,0.5f);
                    sprite.rotate90(true);
                    topRightCorner.put(id, sprite);
                    sprite = new Sprite(assets[0][0]);
                    sprite.rotate90(false);
                    sprite.setSize(0.5f,0.5f);
                    bottomLeftCorner.put(id, sprite);
                    sprite = new Sprite(assets[0][0]);
                    sprite.setSize(0.5f,0.5f);
                    sprite.rotate90(true);
                    sprite.rotate90(true);
                    bottomRightCorner.put(id, sprite);



                    sprite = new Sprite(assets[1][0]);
                    sprite.setSize(0.5f,0.5f);
                    middle.put(id, sprite);

                    sprite = new Sprite(assets[0][1]);
                    sprite.setSize(0.5f,0.5f);
                    westStraight.put(id, sprite);
                    sprite = new Sprite(assets[0][1]);
                    sprite.setSize(0.5f,0.5f);
                    sprite.rotate90(true);
                    northStraight.put(id, sprite);
                    sprite = new Sprite(assets[0][1]);
                    sprite.rotate90(false);
                    sprite.setSize(0.5f,0.5f);
                    southStraight.put(id, sprite);
                    sprite = new Sprite(assets[0][1]);
                    sprite.setSize(0.5f,0.5f);
                    sprite.rotate90(true);
                    sprite.rotate90(true);
                    eastStraight.put(id, sprite);

                    sprite = new Sprite(assets[1][1]);
                    sprite.setSize(0.5f,0.5f);
                    topLeftAnticorner.put(id, sprite);
                    sprite = new Sprite(assets[1][1]);
                    sprite.setSize(0.5f,0.5f);
                    sprite.rotate90(true);
                    topRightAnticorner.put(id, sprite);
                    sprite = new Sprite(assets[1][1]);
                    sprite.rotate90(false);
                    sprite.setSize(0.5f,0.5f);
                    bottomLeftAnticorner.put(id, sprite);
                    sprite = new Sprite(assets[1][1]);
                    sprite.setSize(0.5f,0.5f);
                    sprite.rotate90(true);
                    sprite.rotate90(true);
                    bottomRightAnticorner.put(id, sprite);
                }
            }

        }
    }


    public TextureRegion getTileSprite(Data.TileType tileType) {
        return tileSprites.get(tileType);
    }

    public TextureRegion getUISprite(Data.AreaType id) {
        return areaSprites.get(id);
    }

    public TextureRegion getMountedSprite() {
        return mountedSprite;
    }

    public TextureRegion getShieldSprite() {
        return shieldSprite;
    }

    public TextureRegion getUnitSprite(Data.SpriteSetId id, Data.Allegeance allegeance){
        TextureRegion res;
        if(allegeance == Data.Allegeance.ALLY){
            res = unitSprites.get(id);
        }else{
            res = foeSprites.get(id);
        }
        return res;
    }

    public TextureRegion getDoneUnitSprite(){ return unitDoneSprite;}

    public TextureRegion getWeaponSprite(Data.WeaponType weaponType){
        return weaponSprites.get(weaponType);
    }

    public TextureRegion getOrientationSprite(Data.Orientation orientation) {
        return orientationSprites.get(orientation);
    }

    public TextureRegion getBuildInConstructionSprite(Data.TileType tile) {
        switch (tile){

            case WATCH_TOWER: return towerInConstruction;
             case BRIDGE: return bridgeInConstruction;
        }
        return null;
    }

    public TextureRegion getBlackBGSprite() {
        return blackBGSprite;
    }
}
