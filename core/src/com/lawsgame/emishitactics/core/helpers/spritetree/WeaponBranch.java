package com.lawsgame.emishitactics.core.helpers.spritetree;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;

public class WeaponBranch {
    public final Data.WeaponType type;
    public final Array<OrientationBranch> children;

    public WeaponBranch(Data.WeaponType type){
        this.type = type;
        this.children = new Array<OrientationBranch>();
    }

    public boolean isChildExist(boolean east){
        for(int i = 0; i < children.size; i++){
            if(children.get(i).east == east){
                return true;
            }
        }
        return false;
    }

    public void populate(boolean east, Data.SpriteSetId id, boolean done, TextureRegion[] spriteSet){
        for(int i = 0; i < children.size; i++){
            if(children.get(i).east == east){
                children.get(i).populate(id, spriteSet, done);
                return;
            }
        }
        OrientationBranch node = new OrientationBranch(east);
        node.populate(id, spriteSet, done);
        children.add(node);
    }

    public TextureRegion[] getSpriteSet(Data.Orientation or, Data.SpriteSetId id, boolean done){
        TextureRegion[] res = null;
        for(int i = 0; i < children.size; i++){
            if(children.get(i).isCorrect(or)){
                res = children.get(i).getSpriteSet(id, done);
            }
        }
        return res;
    }

    public String toString(){
        String str = "\n|||| Weapon : "+type.name();
        for(int i = 0; i < children.size;i++){
            str += children.get(i).toString();
        }
        return str;
    }
}
