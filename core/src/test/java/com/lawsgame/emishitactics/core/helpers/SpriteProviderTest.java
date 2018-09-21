package com.lawsgame.emishitactics.core.helpers;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lawsgame.emishitactics.core.models.Data;

import org.junit.Before;
import org.junit.Test;

public class SpriteProviderTest {

    SpriteProvider.CharacterSpriteTree tree;

    @Before
    public void before(){
        tree = new SpriteProvider.CharacterSpriteTree();

        tree.populate(true, Data.UnitTemplate.SOLAIRE, Data.WeaponType.SWORD, true, Data.SpriteSetId.REST, false, false, new TextureRegion[]{});
        tree.populate(true, Data.UnitTemplate.SOLAIRE, Data.WeaponType.SWORD, true, Data.SpriteSetId.REST, false, false, new TextureRegion[]{});
        tree.populate(true, Data.UnitTemplate.SOLAIRE, Data.WeaponType.SWORD, false, Data.SpriteSetId.REST, false, false, new TextureRegion[]{});
        tree.populate(true, Data.UnitTemplate.SOLAIRE, Data.WeaponType.SWORD, false, Data.SpriteSetId.REGULAR_ATTACK, false, false, new TextureRegion[]{});
        tree.populate(true, Data.UnitTemplate.SOLAIRE, Data.WeaponType.MACE, false, Data.SpriteSetId.REGULAR_ATTACK, false, false, new TextureRegion[]{});

        //System.out.println(tree.toString());

    }

    @Test
    public void test(){

    }
}
