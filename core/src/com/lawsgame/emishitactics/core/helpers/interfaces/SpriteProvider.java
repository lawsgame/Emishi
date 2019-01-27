package com.lawsgame.emishitactics.core.helpers.interfaces;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.AnimId;
import com.lawsgame.emishitactics.core.models.Data.AreaColor;
import com.lawsgame.emishitactics.core.models.Data.Orientation;
import com.lawsgame.emishitactics.core.models.Data.SparkleType;
import com.lawsgame.emishitactics.core.models.Data.UnitTemplate;
import com.lawsgame.emishitactics.core.models.Data.WeaponType;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;

/**
 * service class which provides:
 * - well-sized sprite set
 *  -skins
 */
public interface SpriteProvider extends Disposable {

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

    enum Flavor {
        NORMAL, WHITEBG, DONE
    }

    Sprite getPortrait(BattleUnitRenderer bur);
    Sprite getPortrait(UnitTemplate template, boolean promoted);
    Sprite getPortrait(boolean playerControlled, UnitTemplate template);
    Array<Sprite> getTileSS(Data.TileType type, boolean upper);
    Array<Sprite> getTileSS(Data.TileSpriteSetId id);
    Array<Sprite> getUnitAnimationSS(BattleUnitRenderer unit, AnimId id, Flavor flavor);
    Array<Sprite> getUnitAnimationSS(UnitTemplate template, WeaponType weaponType, boolean character, boolean promoted, boolean playerControlled, Orientation or, AnimId id, boolean warchief, Flavor param);
    Array<TextureAtlas.AtlasRegion> getStandardUnitAnimationSS(boolean character);
    Array<Sprite> getSparkleSS(SparkleType type);
    Array<Sprite> getAreaSS(AreaColor color, AreaSpriteType type);
    void load(AssetManager asm, Battlefield battlefield);


}
