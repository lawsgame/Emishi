package com.lawsgame.emishitactics.core.helpers.spritetree;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lawsgame.emishitactics.core.models.Data.AnimSpriteSetId;

public class STLeaf {

    protected final AnimSpriteSetId id;
    protected TextureRegion[] spriteset;

    public STLeaf(AnimSpriteSetId id, TextureRegion[] spriteset) {
        this.id = id;
        this.spriteset = spriteset;
    }

    public static STLeaf create(AnimSpriteSetId id, TextureRegion[] spriteset, boolean done){
        return (id == AnimSpriteSetId.REST || id == AnimSpriteSetId.BANNER) ?
                new RestSpriteSetLeaf( id, spriteset, done) :
                new STLeaf(id, spriteset);
    }

    public boolean isCorrect(AnimSpriteSetId id, boolean done){
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

        public RestSpriteSetLeaf(AnimSpriteSetId id, TextureRegion[] spriteset, boolean done) {
            super(id, spriteset);
            this.done = done;
        }

        public boolean isCorrect(AnimSpriteSetId id, boolean done){
            return this.id == id && this.done == done;
        }

        public String toString(){
            String str = super.toString();
            str += (done) ? " done" : " active";
            return str;
        }

    }
}
