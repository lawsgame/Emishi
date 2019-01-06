package com.lawsgame.emishitactics.core.helpers.implementations;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.helpers.interfaces.SpriteProvider;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;

import java.util.HashMap;

public class SpriteProviderImp implements SpriteProvider {
    private static String EXT_PACK = ".pack";

    // dimension of the standard  unit /tile sprite
    private float spriteStdSize;

    public SpriteProviderImp(float spriteStdSize){
        this.spriteStdSize = spriteStdSize;
    }

    @Override
    public void load(AssetManager asm, Battlefield battlefield) {

    }

    @Override
    public Skin getUISkin() {
        return null;
    }

    @Override
    public Sprite getPortrait(Unit unit) {
        return null;
    }

    @Override
    public Array<Sprite> getUnitAnimationSS(BattleUnitRenderer unit, Data.AnimId id) {
        return null;
    }

    @Override
    public Array<Sprite> getTileSS(Data.TileType type, boolean upper) {
        return null;
    }

    @Override
    public Array<Sprite> getSparkleSS(Data.SparkleType type) {
        return null;
    }

    @Override
    public HashMap<AreaSpriteType, Array<Sprite>> getAreaSS(Data.AreaColor color) {
        return null;
    }


}
