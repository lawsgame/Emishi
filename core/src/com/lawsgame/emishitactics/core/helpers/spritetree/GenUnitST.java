package com.lawsgame.emishitactics.core.helpers.spritetree;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.SpriteSetId;
import com.lawsgame.emishitactics.core.models.Data.Orientation;
import com.lawsgame.emishitactics.core.models.Data.UnitTemplate;
import com.lawsgame.emishitactics.core.models.Data.WeaponType;
import com.lawsgame.emishitactics.core.phases.battle.renderers.IsoBFR;

public class GenUnitST {
    public final Array<SideBranch> children;


    public GenUnitST(){
        this.children = new Array<SideBranch>();
    }

    boolean isChildExist(boolean playerControlled){
        for(int i = 0; i < children.size; i++){
            if(children.get(i).pc == playerControlled){
                return true;
            }
        }
        return false;
    }

    SideBranch getChild(boolean playerControlled){
        for(int i = 0; i < children.size; i++){
            if(children.get(i).pc == playerControlled){
                return children.get(i);
            }
        }
        return null;
    }

    public void populate(boolean playerControlled,
                  boolean shield,
                  boolean horseman,
                  Data.UnitTemplate template,
                  Data.WeaponType type,
                  boolean east,
                  Data.SpriteSetId spriteSetId,
                  boolean done,
                  TextureRegion[] spriteSet){
        for(int i = 0; i < children.size; i++){
            if(children.get(i).pc == playerControlled){
                children.get(i).populate(shield, horseman, template, type, east, spriteSetId, done, spriteSet);
                return;
            }
        }
        SideBranch node = new SideBranch(shield);
        node.populate(shield, horseman, template, type, east, spriteSetId, done, spriteSet);
        children.add(node);
    }

    public void populate(boolean playerControlled, boolean shield, boolean horseman,
                         UnitTemplate template,
                         WeaponType type, boolean east,
                         SpriteSetId spriteSetId,
                         TextureRegion[] spriteSet){
        populate(playerControlled, shield, horseman, template, type, east, spriteSetId, true, spriteSet);
    }


    public WeaponBranch initSpriteSheet(boolean playerControlled, boolean shield, boolean horseman,
                                        UnitTemplate template,
                                        WeaponType type){

        SideBranch sideNode;
        ShieldedBranch shieldNode;
        HorsemanBranch horsemanNode;
        JobBranch templateNode;
        WeaponBranch weaponTypeNode;

        if(isChildExist(playerControlled)){
            sideNode = getChild(playerControlled);
        }else{
            sideNode = new SideBranch(playerControlled);
            children.add(sideNode);
        }

        if(sideNode.isChildExist(shield)){
            shieldNode = sideNode.getChild(shield);
        }else{
            shieldNode = new ShieldedBranch(shield);
            sideNode.children.add(shieldNode);
        }

        if(shieldNode.isChildExist(horseman)){
            horsemanNode = shieldNode.getChild(horseman);
        }else{
            horsemanNode = new HorsemanBranch(horseman);
            shieldNode.children.add(horsemanNode);
        }

        if(horsemanNode.isChildExist(template)){
            templateNode = horsemanNode.getChild(template);
        }else{
            templateNode = new JobBranch(template);
            horsemanNode.children.add(templateNode);
        }

        if(templateNode.isChildExist(type)){
            weaponTypeNode = templateNode.getChild(type);
        }else{
            weaponTypeNode = new WeaponBranch(type);
            templateNode.children.add(weaponTypeNode);
        }

        return weaponTypeNode;
    }


    public Array<Sprite> getSpriteSet(boolean playerControlled, boolean shield, boolean horseman,
                                      UnitTemplate template,
                                      WeaponType type,
                                      Orientation or,
                                      boolean done,
                                      SpriteSetId id){
        Array<Sprite> spriteset;
        TextureRegion[] tra = null;
        for(int i = 0; i < children.size; i++){
            if(children.get(i).pc == playerControlled){
                tra = children.get(i).getSpriteSet(shield, horseman, template, type, or, done, id);
            }
        }
        if(tra == null) {

            spriteset = getSpriteSet(playerControlled, or, done, id);
        }else {

            spriteset = new Array<Sprite>();
            if(id.isRest0()) spriteset.add(getSpriteSet(playerControlled, shield, horseman, template, type, or, done, SpriteSetId.REST).get(0));
            Sprite sprite;
            float spriteWidth;
            for (int i = 0; i < tra.length; i++) {
                sprite = new Sprite(tra[i]);
                spriteWidth = (tra[i].getRegionWidth() != tra[i].getRegionHeight()) ? IsoBFR.SPRITE_STD_SIZE / 2f : IsoBFR.SPRITE_STD_SIZE;
                sprite.setSize(spriteWidth, IsoBFR.SPRITE_STD_SIZE);
                if (or == Data.Orientation.WEST || or == Data.Orientation.NORTH) {
                    sprite.flip(true, false);
                }
                spriteset.add(sprite);
            }
        }
        return spriteset;
    }

    private Array<Sprite> getSpriteSet(boolean playerControlled, Data.Orientation or, boolean done, Data.SpriteSetId id) {
        return getSpriteSet(playerControlled,false, false, Data.UnitTemplate.SOLAR_KNIGHT, Data.WeaponType.SWORD, or, done, id);
    }


    public String toString(){
        String str = "\nGENERIC UNIT TREE : ";
        for(int i = 0; i < children.size; i++){
            str += children.get(i).toString();
        }
        return str;
    }
}
