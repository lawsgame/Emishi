package com.lawsgame.emishitactics.core.helpers.spritetree;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.renderers.IsoBFR;

public class CharaST {
    public final Array<PromotedBranch> children;

    public CharaST(){
        this.children = new Array<PromotedBranch>();
    }

    boolean isChildExist(boolean promoted){
        for(int i = 0; i < children.size; i++){
            if(children.get(i).promoted == promoted){
                return true;
            }
        }
        return false;
    }

    PromotedBranch getChild(boolean promoted){
        for(int i = 0; i < children.size; i++){
            if(children.get(i).promoted == promoted){
                return children.get(i);
            }
        }
        return null;
    }

    public WeaponBranch initSpriteSheet(boolean promoted, Data.UnitTemplate template, Data.WeaponType type){

        PromotedBranch promotedNode;
        JobBranch templateNode;
        WeaponBranch weaponTypeNode;


        if(isChildExist(promoted)){
            promotedNode = getChild(promoted);
        }else{
            promotedNode = new PromotedBranch(promoted);
            children.add(promotedNode);
        }

        if(promotedNode.isChildExist(template)){
            templateNode = promotedNode.getChild(template);
        }else{
            templateNode = new JobBranch(template);
            promotedNode.children.add(templateNode);
        }

        if(templateNode.isChildExist(type)){
            weaponTypeNode = templateNode.getChild(type);
        }else{
            weaponTypeNode = new WeaponBranch(type);
            templateNode.children.add(weaponTypeNode);
        }

        return weaponTypeNode;
    }


    public void populate(
            boolean promoted,
            Data.UnitTemplate template,
            Data.WeaponType type,
            boolean east,
            Data.SpriteSetId id,
            boolean done,
            TextureRegion[] spriteSet){
        for(int i = 0; i < children.size; i++){
            if(children.get(i).promoted == promoted){
                children.get(i).populate(template, type, east, id, spriteSet, done);
                return;
            }
        }
        PromotedBranch node = new PromotedBranch(promoted);
        node.populate(template, type, east, id, spriteSet, done);
        children.add(node);
    }

    public void populate(boolean promoted, Data.UnitTemplate template, Data.WeaponType type, boolean east, Data.SpriteSetId id, TextureRegion[] spriteSet){
        populate(promoted, template, type, east, id, false, spriteSet);
    }

    public Array<Sprite> getSpriteSet(boolean promoted, Data.UnitTemplate template, Data.WeaponType type, Data.Orientation or, boolean done, Data.SpriteSetId id){

        Array<Sprite> spriteset;
        TextureRegion[] tra = null;
        for(int i = 0; i < children.size; i++){
            if(children.get(i).promoted == promoted){
                tra = children.get(i).getSpriteSet(template, type, or, done, id);
            }
        }

        if(tra == null) {
            spriteset = getSpriteSet(promoted, or, done, id);
        }else {
            spriteset = new Array<Sprite>();
            if(id.isRest0()) spriteset.add(getSpriteSet(promoted, template, type, or, done, Data.SpriteSetId.REST).get(0));
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

    private Array<Sprite> getSpriteSet(boolean promoted, Data.Orientation or, boolean done, Data.SpriteSetId id) {
        return getSpriteSet(promoted, Data.UnitTemplate.SOLAIRE, Data.WeaponType.SWORD, or, done, id);
    }

    public String toString(){
        String str = "\nCHARAC TREE : ";
        for(int i = 0; i < children.size;i++){
            str += children.get(i).toString();
        }
        return str;
    }
}
