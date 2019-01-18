package com.lawsgame.emishitactics.core.helpers.spritetree;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.AnimUnitSSId;

public class STLeaf {

    protected final Data.AnimUnitSSId id;
    protected TextureRegion[] spriteset;

    public STLeaf(Data.AnimUnitSSId id, TextureRegion[] spriteset) {
        this.id = id;
        this.spriteset = spriteset;
    }

    public static STLeaf create(AnimUnitSSId id, TextureRegion[] spriteset, boolean done){
        return (id == Data.AnimUnitSSId.REST || id == Data.AnimUnitSSId.BANNER) ?
                new RestSpriteSetLeaf( id, spriteset, done) :
                new STLeaf(id, spriteset);
    }

    public boolean isCorrect(Data.AnimUnitSSId id, boolean done){
        return this.id == id;
    }

    public TextureRegion[] getSpriteSet() {
        return spriteset;
    }

    public String toString(){
        return "\n|||||| ID : "+id;
    }






    // ----------- STLeaf for IDLE animation -----------------

    static class RestSpriteSetLeaf extends STLeaf {
        protected boolean done;

        public RestSpriteSetLeaf(Data.AnimUnitSSId id, TextureRegion[] spriteset, boolean done) {
            super(id, spriteset);
            this.done = done;
        }

        public boolean isCorrect(AnimUnitSSId id, boolean done){
            return this.id == id && this.done == done;
        }

        public String toString(){
            String str = super.toString();
            str += (done) ? " done" : " active";
            return str;
        }

    }
}
