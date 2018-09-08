package com.lawsgame.emishitactics.core.helpers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.Allegeance;
import com.lawsgame.emishitactics.core.models.Data.AnimId;
import com.lawsgame.emishitactics.core.models.Data.Job;
import com.lawsgame.emishitactics.core.models.Data.Orientation;
import com.lawsgame.emishitactics.core.models.Data.WeaponType;
import com.lawsgame.emishitactics.engine.datastructures.Map3;
import com.lawsgame.emishitactics.engine.datastructures.Map5;

import java.util.HashMap;

public class SpritePool {

    public SpritePool(){ }

    //----------------- UPDATED ----------------------

    public TextureRegion undefinedTileSprite = null;
    public HashMap<Data.TileType,  TextureRegion> tileSprites = new HashMap<Data.TileType, TextureRegion>();

    private Map5<Allegeance, Job, Orientation, WeaponType, AnimId, Array<TextureRegion>> unshieldedFootmanRecruitSprites =      new Map5<Allegeance, Job, Orientation, WeaponType, AnimId, Array<TextureRegion>>();
    private Map5<Allegeance, Job, Orientation, WeaponType, AnimId, Array<TextureRegion>> shieldedFootmanRecruitSprites =        new Map5<Allegeance, Job, Orientation, WeaponType, AnimId, Array<TextureRegion>>();
    private Map5<Allegeance, Job, Orientation, WeaponType, AnimId, Array<TextureRegion>> unshieldedHorsemanRecruitSprites =     new Map5<Allegeance, Job, Orientation, WeaponType, AnimId, Array<TextureRegion>>();
    private Map5<Allegeance, Job, Orientation, WeaponType, AnimId, Array<TextureRegion>> shieldedHorsemanRecruitSprites =       new Map5<Allegeance, Job, Orientation, WeaponType, AnimId, Array<TextureRegion>>();
    private Map5<Allegeance, Job, Orientation, WeaponType, AnimId, Array<TextureRegion>> unshieldedFootmanPromotedSprites =     new Map5<Allegeance, Job, Orientation, WeaponType, AnimId, Array<TextureRegion>>();
    private Map5<Allegeance, Job, Orientation, WeaponType, AnimId, Array<TextureRegion>> shieldedFootmanPromotedSprites =       new Map5<Allegeance, Job, Orientation, WeaponType, AnimId, Array<TextureRegion>>();
    private Map5<Allegeance, Job, Orientation, WeaponType, AnimId, Array<TextureRegion>> unshieldedHorsemanPromotedSprites =    new Map5<Allegeance, Job, Orientation, WeaponType, AnimId, Array<TextureRegion>>();
    private Map5<Allegeance, Job, Orientation, WeaponType, AnimId, Array<TextureRegion>> shieldedHorsemanPromotedSprites =      new Map5<Allegeance, Job, Orientation, WeaponType, AnimId, Array<TextureRegion>>();
    private Map3<Orientation, WeaponType, AnimId, Array<TextureRegion>> characterRecruitSprites =   new Map3<Orientation, WeaponType, AnimId, Array<TextureRegion>>();
    private Map3<Orientation, WeaponType, AnimId, Array<TextureRegion>> characterPromotedSprites =  new Map3<Orientation, WeaponType, AnimId, Array<TextureRegion>>();




    //------------------ FROM TEST --------------------------

    public TextureRegion blackBGSprite = null;
    public TextureRegion buttonUp = null;
    public TextureRegion buttonDown = null;

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

    public void set(Battlefield battlefield, AssetManager asm){
        if(asm != null) {
            TextureRegion region;
            String regionName;
            TextureAtlas atlas;

            // set tiles sprite pool
            if(asm.isLoaded(Assets.ATLAS_TILES)) {
                atlas = asm.get(Assets.ATLAS_TILES);
                this.undefinedTileSprite = atlas.findRegion(Assets.REGION_TERRAINS_UNDEFINED);
                for (Data.TileType tileType : Data.TileType.values()) {
                    regionName = Assets.getRegionTile(tileType);
                    region = atlas.findRegion(regionName);
                    if (region != null) {
                        this.tileSprites.put(tileType, region);
                    }
                }
            }









            // OLD


            if(asm.isLoaded(Assets.ATLAS_TEMPO_UI)) {

                atlas = asm.get(Assets.ATLAS_TEMPO_UI);
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

    // ------------------ UPDATED -------------------------

    public Array<TextureRegion> getUnitSpriteSet(boolean promoted, boolean horseman, boolean shielded, Allegeance allegeance, Job job, Orientation or, WeaponType weaponType, AnimId id){
        Array<TextureRegion> unitSprites = null;
        if(promoted){
            if(horseman){
                unitSprites = (shielded) ?
                        shieldedHorsemanPromotedSprites.get(allegeance, job, or, weaponType, id):
                        unshieldedHorsemanPromotedSprites.get(allegeance, job, or, weaponType, id);
            }else{
                unitSprites = (shielded) ?
                        shieldedFootmanPromotedSprites.get(allegeance, job, or, weaponType, id):
                        unshieldedFootmanPromotedSprites.get(allegeance, job, or, weaponType, id);
            }
        }else{
            if(horseman){
                unitSprites = (shielded) ?
                        shieldedHorsemanRecruitSprites.get(allegeance, job, or, weaponType, id):
                        unshieldedHorsemanRecruitSprites.get(allegeance, job, or, weaponType, id);
            }else{
                unitSprites = (shielded) ?
                        shieldedFootmanRecruitSprites.get(allegeance, job, or, weaponType, id):
                        unshieldedFootmanRecruitSprites.get(allegeance, job, or, weaponType, id) ;
            }
        }
        return unitSprites;
    }

    public Array<TextureRegion> getCharacterSpriteSet(boolean promoted, Orientation or, WeaponType weaponType, AnimId id){
        return (promoted) ?
                characterPromotedSprites.get(or, weaponType, id) :
                characterRecruitSprites.get(or, weaponType, id);
    }
}
