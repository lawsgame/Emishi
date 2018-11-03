package com.lawsgame.emishitactics.core.helpers.spritetree;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;

public class PromotedBranch {
    public final boolean promoted;
    public final Array<JobBranch> children;


    public PromotedBranch(boolean promoted){
        this.promoted = promoted;
        this.children = new Array<JobBranch>();
    }

    public boolean isChildExist(Data.UnitTemplate template){
        for(int i = 0; i < children.size; i++){
            if(children.get(i).template == template){
                return true;
            }
        }
        return false;
    }

    public JobBranch getChild(Data.UnitTemplate template){
        for(int i = 0; i < children.size; i++){
            if(children.get(i).template == template){
                return children.get(i);
            }
        }
        return null;
    }

    public void populate(Data.UnitTemplate template, Data.WeaponType type, boolean east, Data.AnimSpriteSetId id, TextureRegion[] spriteSet, boolean done){
        for(int i = 0; i < children.size; i++){
            if(children.get(i).template == template){
                children.get(i).populate(type, east, id, spriteSet, done);
                return;
            }
        }
        JobBranch node = new JobBranch(template);
        node.populate(type, east, id, spriteSet, done);
        children.add(node);
    }

    public TextureRegion[] getSpriteSet(Data.UnitTemplate template, Data.WeaponType type, Data.Orientation or, boolean done, Data.AnimSpriteSetId id){
        TextureRegion[] res = null;
        for(int i = 0; i < children.size; i++){
            if(children.get(i).template == template){
                res = children.get(i).getSpriteSet(type, or, done, id);
            }
        }
        return res;
    }

    public String toString(){
        String str = "\n|| Promoted : "+promoted;
        for(int i = 0; i < children.size;i++){
            str += children.get(i).toString();
        }
        return str;
    }
}
