package com.lawsgame.emishitactics.core.helpers.spritetree;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;

public class ShieldedBranch {

    public final boolean shieldbearer;
    public final Array<HorsemanBranch> children;


    public ShieldedBranch(boolean shieldbearer){
        this.shieldbearer = shieldbearer;
        this.children = new Array<HorsemanBranch>();
    }

    public boolean isChildExist(boolean horseman){
        for(int i = 0; i < children.size; i++){
            if(children.get(i).horseman == horseman){
                return true;
            }
        }
        return false;
    }

    public HorsemanBranch getChild(boolean horseman){
        for(int i = 0; i < children.size; i++){
            if(children.get(i).horseman == horseman){
                return children.get(i);
            }
        }
        return null;
    }

    public void populate(boolean horseman,
                         Data.UnitTemplate template,
                         Data.WeaponType type,
                         boolean east,
                         Data.AnimSpriteSetId id,
                         TextureRegion[] spriteSet,
                         boolean done){
        for(int i = 0; i < children.size; i++){
            if(children.get(i).horseman == horseman){
                children.get(i).populate(template, type, east, id, spriteSet, done);
                return;
            }
        }
        HorsemanBranch node = new HorsemanBranch(horseman);
        node.populate(template, type, east, id, spriteSet, done);
        children.add(node);
    }

    public TextureRegion[] getSpriteSet(boolean horseman, Data.UnitTemplate template, Data.WeaponType type, Data.Orientation or, boolean done, Data.AnimSpriteSetId id){
        TextureRegion[] res = null;
        for(int i = 0; i < children.size; i++){
            if(children.get(i).horseman == horseman){
                res = children.get(i).getSpriteSet(template, type, or, done, id);
            }
        }
        return res;
    }

    public String toString(){
        String str = "\n| Shielded : "+ shieldbearer;
        for(int i = 0; i < children.size; i++){
            str += children.get(i).toString();
        }
        return str;
    }
}
