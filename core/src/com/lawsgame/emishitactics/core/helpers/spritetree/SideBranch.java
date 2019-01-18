package com.lawsgame.emishitactics.core.helpers.spritetree;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;

public class SideBranch {
    public boolean pc;
    public final Array<ShieldedBranch> children;


    SideBranch(boolean pc){
        this.pc = pc;
        this.children = new Array<ShieldedBranch>();
    }

    boolean isChildExist(boolean shield){
        for(int i = 0; i < children.size; i++){
            if(children.get(i).shieldbearer == shield){
                return true;
            }
        }
        return false;
    }

    ShieldedBranch getChild(boolean shield){
        for(int i = 0; i < children.size; i++){
            if(children.get(i).shieldbearer == shield){
                return children.get(i);
            }
        }
        return null;
    }

    void populate(boolean shield,
                  boolean horseman,
                  Data.UnitTemplate template,
                  Data.WeaponType type,
                  boolean east,
                  Data.AnimUnitSSId animUnitSSId,
                  boolean done,
                  TextureRegion[] spriteSet){
        for(int i = 0; i < children.size; i++){
            if(children.get(i).shieldbearer == shield){
                children.get(i).populate(horseman, template, type, east, animUnitSSId, spriteSet, done);
                return;
            }
        }
        ShieldedBranch node = new ShieldedBranch(shield);
        node.populate(horseman, template, type, east, animUnitSSId, spriteSet, done);
        children.add(node);
    }

    void populate(boolean shield, boolean horseman, Data.UnitTemplate template, Data.WeaponType type, boolean east, Data.AnimUnitSSId animUnitSSId, TextureRegion[] spriteSet){
        populate(shield, horseman, template, type, east, animUnitSSId, true, spriteSet);
    }

    public TextureRegion[] getSpriteSet(boolean shield, boolean horseman, Data.UnitTemplate template, Data.WeaponType type, Data.Orientation or, boolean done, Data.AnimUnitSSId id){
        TextureRegion[] res = null;
        for(int i = 0; i < children.size; i++){
            if(children.get(i).shieldbearer == shield){
                res = children.get(i).getSpriteSet(horseman, template, type, or, done, id);
            }
        }
        return res;
    }

    public String toString(){
        String str = "\nPC ? : "+pc;
        for(int i = 0; i < children.size; i++){
            str += children.get(i).toString();
        }
        return str;
    }
}
