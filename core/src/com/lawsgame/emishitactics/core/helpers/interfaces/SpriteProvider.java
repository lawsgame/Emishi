package com.lawsgame.emishitactics.core.helpers.interfaces;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;

import java.util.HashMap;

public interface SpriteProvider {

    enum AreaSpriteType{
        ANTI_INSIDE_ACUTE,
        ANTI_INSIDE_OBTUSE,
        ANTI_OUTSIDE_ACUTE,
        ANTI_OUTSIDE_OBTUSE,
        ANTI_SIDE,
        ANTI_SIDE_UPSIDE_DOWN,
        BORDER,
        CORNER_ACUTE,
        CORNER_OBTUSE
    }


    Sprite getPortrait(Unit unit);
    Skin getUISkin();
    Array<Sprite> getUnitAnimationSS(BattleUnitRenderer unit, Data.AnimId id);
    Array<Sprite> getTileSS(Data.TileType type, boolean upper);
    Array<Sprite> getSparkleSS(Data.SparkleType type);
    HashMap<AreaSpriteType, Array<Sprite>> getAreaSS(Data.AreaColor color);
    void load(AssetManager asm, Battlefield battlefield);


}
