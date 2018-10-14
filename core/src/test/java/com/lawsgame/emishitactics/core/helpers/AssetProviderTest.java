package com.lawsgame.emishitactics.core.helpers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lawsgame.emishitactics.core.helpers.spritetree.CharaST;
import com.lawsgame.emishitactics.core.models.Data;

import org.junit.Before;
import org.junit.Test;

public class AssetProviderTest {

    CharaST tree;

    @Before
    public void before(){
        tree = new CharaST();

        tree.populate(true, Data.UnitTemplate.SOLAIRE, Data.WeaponType.SWORD, true, Data.SpriteSetId.REST, false, new TextureRegion[]{});
        tree.populate(true, Data.UnitTemplate.SOLAIRE, Data.WeaponType.SWORD, true, Data.SpriteSetId.REST, false, new TextureRegion[]{});
        tree.populate(true, Data.UnitTemplate.SOLAIRE, Data.WeaponType.SWORD, false, Data.SpriteSetId.REST, false, new TextureRegion[]{});
        tree.populate(true, Data.UnitTemplate.SOLAIRE, Data.WeaponType.SWORD, false, Data.SpriteSetId.ATTACK, false, new TextureRegion[]{});
        tree.populate(true, Data.UnitTemplate.SOLAIRE, Data.WeaponType.MACE, false, Data.SpriteSetId.ATTACK, false, new TextureRegion[]{});


    }

    @Test
    public void test(){

    }
}
