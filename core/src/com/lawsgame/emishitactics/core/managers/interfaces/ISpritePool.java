package com.lawsgame.emishitactics.core.managers.interfaces;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Props;

public interface ISpritePool {

    TextureRegion getTileTextureRegion(Props.TileType tileType);
    Array<TextureRegion> getUnitSprites(Props.ActionState actionState, Props.UnitTemplate template, boolean wieldShield);
}
