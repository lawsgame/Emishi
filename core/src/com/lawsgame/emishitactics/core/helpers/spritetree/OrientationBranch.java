package com.lawsgame.emishitactics.core.helpers.spritetree;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;

public class OrientationBranch {
    public final boolean east;
    public final Array<STLeaf> spriteSetLeaves;

    public OrientationBranch(boolean east){
        this.east = east;
        this.spriteSetLeaves = new Array<STLeaf>();
    }

    public boolean isChildExist(Data.AnimUnitSSId id, boolean done){
        for(int i = 0; i < spriteSetLeaves.size; i++){
            if(spriteSetLeaves.get(i).isCorrect(id, done)){
                return true;
            }
        }
        return false;
    }

    public boolean isCorrect(Data.Orientation or){
        return ((or == Data.Orientation.NORTH || or == Data.Orientation.EAST) && east) || ((or == Data.Orientation.SOUTH || or == Data.Orientation.WEST) && !east);
    }

    public void populate(Data.AnimUnitSSId id, TextureRegion[] spriteSet, boolean done){
        for(int i = 0; i < spriteSetLeaves.size; i++){
            if(spriteSetLeaves.get(i).isCorrect(id, done)){
                spriteSetLeaves.get(i).spriteset = spriteSet;
                return;
            }
        }
        spriteSetLeaves.add(STLeaf.create(id, spriteSet, done));
    }

    public TextureRegion[] getSpriteSet(Data.AnimUnitSSId id, boolean done) {
        TextureRegion[] res = null;
        for(int i = 0; i < spriteSetLeaves.size; i++){
            if(spriteSetLeaves.get(i).isCorrect(id, done)){
                res = spriteSetLeaves.get(i).getSpriteSet();
            }
        }
        return res;
    }

    public String toString(){
        String str = "\n||||| Orientation : east<>north? "+ east;
        for(int i = 0; i < spriteSetLeaves.size;i++){
            str += spriteSetLeaves.get(i).toString();
        }
        return str;
    }
}
