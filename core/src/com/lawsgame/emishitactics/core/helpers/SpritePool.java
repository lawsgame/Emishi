package com.lawsgame.emishitactics.core.helpers;

import com.badlogic.gdx.assets.AssetManager;
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
import com.lawsgame.emishitactics.core.models.Player;
import com.lawsgame.emishitactics.engine.datastructures.Map4;
import com.lawsgame.emishitactics.engine.datastructures.Map5;

import java.util.HashMap;

public class SpritePool {

    public SpritePool(){ }

    //----------------- UPDATED ----------------------

    public TextureRegion undefinedTileSprite = null;
    public HashMap<Data.TileType,  TextureRegion> tileSprites = new HashMap<Data.TileType, TextureRegion>();

    private Map5<Job, WeaponType, Allegeance, AnimId, Orientation, Array<TextureRegion>> unshieldedFootmanRecruitSprites =      new Map5<Job, WeaponType, Allegeance, AnimId, Orientation, Array<TextureRegion>>();
    private Map5<Job, WeaponType, Allegeance, AnimId, Orientation, Array<TextureRegion>> shieldedFootmanRecruitSprites =        new Map5<Job, WeaponType, Allegeance, AnimId, Orientation, Array<TextureRegion>>();
    private Map5<Job, WeaponType, Allegeance, AnimId, Orientation, Array<TextureRegion>> unshieldedHorsemanRecruitSprites =     new Map5<Job, WeaponType, Allegeance, AnimId, Orientation, Array<TextureRegion>>();
    private Map5<Job, WeaponType, Allegeance, AnimId, Orientation, Array<TextureRegion>> shieldedHorsemanRecruitSprites =       new Map5<Job, WeaponType, Allegeance, AnimId, Orientation, Array<TextureRegion>>();
    private Map5<Job, WeaponType, Allegeance, AnimId, Orientation, Array<TextureRegion>> unshieldedFootmanPromotedSprites =     new Map5<Job, WeaponType, Allegeance, AnimId, Orientation, Array<TextureRegion>>();
    private Map5<Job, WeaponType, Allegeance, AnimId, Orientation, Array<TextureRegion>> shieldedFootmanPromotedSprites =       new Map5<Job, WeaponType, Allegeance, AnimId, Orientation, Array<TextureRegion>>();
    private Map5<Job, WeaponType, Allegeance, AnimId, Orientation, Array<TextureRegion>> unshieldedHorsemanPromotedSprites =    new Map5<Job, WeaponType, Allegeance, AnimId, Orientation, Array<TextureRegion>>();
    private Map5<Job, WeaponType, Allegeance, AnimId, Orientation, Array<TextureRegion>> shieldedHorsemanPromotedSprites =      new Map5<Job, WeaponType, Allegeance, AnimId, Orientation, Array<TextureRegion>>();
    private Map4<Job, WeaponType, Orientation, AnimId, Array<TextureRegion>> characterRecruitSprites =   new Map4<Job, WeaponType, Orientation, AnimId, Array<TextureRegion>>();
    private Map4<Job, WeaponType, Orientation, AnimId, Array<TextureRegion>> characterPromotedSprites =  new Map4<Job, WeaponType, Orientation, AnimId, Array<TextureRegion>>();


    public void set(Battlefield battlefield, Player player, AssetManager asm){
        if(asm != null) {
            TextureRegion region;
            String regionName;
            TextureAtlas atlas;

            // set tiles sprite pool
            if (asm.isLoaded(Assets.ATLAS_TILES)) {
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


        }
    }

    // ------------------ UPDATED -------------------------

    public Array<TextureRegion> getUnitSpriteSet(boolean promoted, boolean horseman, boolean shielded, Job job, WeaponType weaponType, Allegeance allegeance, Orientation or, AnimId id){
        Array<TextureRegion> unitSprites = null;
        if(promoted){
            if(horseman){
                unitSprites = (shielded) ?
                        shieldedHorsemanPromotedSprites.get(job, weaponType, allegeance, id, or):
                        unshieldedHorsemanPromotedSprites.get(job, weaponType, allegeance, id, or);
            }else{
                unitSprites = (shielded) ?
                        shieldedFootmanPromotedSprites.get(job, weaponType, allegeance, id, or):
                        unshieldedFootmanPromotedSprites.get(job, weaponType, allegeance, id, or);
            }
        }else{
            if(horseman){
                unitSprites = (shielded) ?
                        shieldedHorsemanRecruitSprites.get(job, weaponType, allegeance, id, or):
                        unshieldedHorsemanRecruitSprites.get(job, weaponType, allegeance, id, or);
            }else{
                unitSprites = (shielded) ?
                        shieldedFootmanRecruitSprites.get(job, weaponType, allegeance, id, or):
                        unshieldedFootmanRecruitSprites.get(job, weaponType, allegeance, id, or) ;
            }
        }
        return unitSprites;
    }

    public Array<TextureRegion> getCharacterSpriteSet(boolean promoted, Job job, Orientation or, WeaponType weaponType, AnimId id){
        return (promoted) ?
                characterPromotedSprites.get(job, weaponType, or, id) :
                characterRecruitSprites.get(job, weaponType, or, id);
    }
}
