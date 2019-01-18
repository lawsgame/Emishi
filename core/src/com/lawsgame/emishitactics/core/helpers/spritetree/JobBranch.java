package com.lawsgame.emishitactics.core.helpers.spritetree;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;

public class JobBranch {

    public final Data.UnitTemplate template;
    public final Array<WeaponBranch> children;


    public JobBranch(Data.UnitTemplate template){
        this.template = template;
        this.children = new Array<WeaponBranch>();
    }

    public boolean isChildExist(Data.WeaponType weaponType){
        for(int i = 0; i < children.size; i++){
            if(children.get(i).type == weaponType){
                return true;
            }
        }
        return false;
    }

    public WeaponBranch getChild(Data.WeaponType type){
        for(int i = 0; i < children.size; i++){
            if(children.get(i).type == type){
                return children.get(i);
            }
        }
        return null;
    }

    public void populate(Data.WeaponType type, boolean east, Data.AnimUnitSSId id, TextureRegion[] spriteSet, boolean done){
        for(int i = 0; i < children.size; i++){
            if(children.get(i).type == type){
                children.get(i).populate(east, id, done, spriteSet);
                return;
            }
        }
        WeaponBranch node = new WeaponBranch(type);
        node.populate(east, id, done, spriteSet);
        children.add(node);
    }

    public TextureRegion[] getSpriteSet(Data.WeaponType type, Data.Orientation or, boolean done, Data.AnimUnitSSId id){
        TextureRegion[] res = null;
        for(int i = 0; i < children.size; i++){
            if(children.get(i).type == type){
                res = children.get(i).getSpriteSet(or, id, done);
            }
        }
        return res;
    }

    public String toString(){
        String str = "\n||| Job : "+template.name();
        for(int i = 0; i < children.size;i++){
            str += children.get(i).toString();
        }
        return str;
    }

}
