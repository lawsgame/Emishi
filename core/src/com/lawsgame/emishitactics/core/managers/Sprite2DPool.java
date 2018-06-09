package com.lawsgame.emishitactics.core.managers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.constants.Props;
import com.lawsgame.emishitactics.core.managers.interfaces.ISpritePool;

import java.util.HashMap;

/*
 TODO:
  - add unit texture regions!
 */
public class Sprite2DPool implements ISpritePool {
    HashMap<Props.TileType, TextureRegion> tileSprites;

    public Sprite2DPool(TextureAtlas tileAtlas){
        this.tileSprites = new HashMap<Props.TileType, TextureRegion>();
        TextureRegion region;
        String regionName;
        for(Props.TileType tileType: Props.TileType.values()){
            regionName = Assets.getRegionTile(tileType);
            region = tileAtlas.findRegion(regionName);
            if(region != null) {
                this.tileSprites.put(tileType, region);

            }

        }

    }


    @Override
    public TextureRegion getTileTextureRegion(Props.TileType tileType) {
        return tileSprites.get(tileType);
    }

    @Override
    public Array<TextureRegion> getUnitSprites(Props.ActionState actionState, Props.UnitTemplate template, boolean wieldShield) {
        return new Array<TextureRegion>();
    }
}
