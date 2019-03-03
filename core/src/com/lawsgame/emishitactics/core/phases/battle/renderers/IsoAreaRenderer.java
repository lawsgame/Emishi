package com.lawsgame.emishitactics.core.phases.battle.renderers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.helpers.interfaces.SpriteProvider.AreaSpriteType;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Data.AreaColor;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.AreaRenderer;
import com.lawsgame.emishitactics.engine.rendering.Animation;

public class IsoAreaRenderer extends AreaRenderer {
    private static final float SPEED = 0.2f;
    private static final int LENGTH = 1;

    protected Animation animation;
    protected Array<Array<Sprite>> sprites;
    protected IsoBFR bfr;

    public IsoAreaRenderer(Area model, IsoBFR bfr) {
        super(model);
        this.sprites = new Array<Array<Sprite>>();
        this.animation = new Animation();
        this.animation.set(LENGTH, SPEED, true, true, false);
        this.bfr = bfr;
        change();
    }

    @Override
    protected void renderArea(SpriteBatch batch) {
        for(int i = 0; i < sprites.size; i++){
            sprites.get(i).get(animation.getCurrentFrame()).draw(batch);
        }
    }

    @Override
    public void change() {
        sprites.clear();
        if(getModel().getCheckmap() != null){
            Array<Sprite> sprites;
            float xCenter;
            float yCenter;
            boolean ntc, etc, otc, wtc, stc;
            boolean[][] checkmap = getModel().getCheckmap();
            AreaColor color = getModel().getType().getColor();
            for(int r = getModel().getRowInit(); r < getModel().getRowInit() + checkmap.length ; r++) {
                for (int c = getModel().getColInit(); c < getModel().getColInit() + checkmap[0].length; c++) {

                    //for each buildingType to highlight
                    if (checkmap[r - getModel().getRowInit()][c - getModel().getColInit()]) {
                        xCenter = bfr.getCenterX(r, c);
                        yCenter = bfr.getCenterY(r, c);


                        // south-west corner

                        otc = getModel().contains(r-1, c-1);   // opposite tile covered
                        wtc = getModel().contains(r, c-1);          // west ...
                        stc = getModel().contains(r-1, c);          // south ...

                        if(otc){
                            if(wtc){
                                if(!stc){
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_SIDE);
                                    for(int i = 0; i<sprites.size; i++) {
                                        sprites.get(i).setFlip(true, false);
                                        sprites.get(i).setPosition(xCenter - 0.25f, yCenter - 0.25f);
                                    }
                                    this.sprites.add(sprites);
                                }
                            }else{
                                if(stc){
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_SIDE);
                                }else{

                                    if(getModel().getType().isRectangular()){
                                        sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.CORNER_OBTUSE);
                                    }else{
                                        sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_OUTSIDE_ACUTE);
                                        for(int i = 0; i<sprites.size; i++) {
                                            sprites.get(i).setPosition(xCenter - 0.5f, yCenter - 0.375f);
                                            sprites.get(i).setFlip(false, false);
                                        }
                                        this.sprites.add(sprites);
                                        sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_SIDE);
                                        for(int i = 0; i<sprites.size; i++) {
                                            sprites.get(i).setFlip(true, false);
                                            sprites.get(i).setPosition(xCenter - 0.25f, yCenter - 0.25f);
                                        }
                                        this.sprites.add(sprites);
                                        sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_SIDE);
                                    }
                                }
                                for(int i = 0; i < sprites.size; i++) {
                                    sprites.get(i).setPosition(xCenter - 0.25f, yCenter - 0.25f);
                                }
                                this.sprites.add(sprites);
                            }
                        }else{
                            if(wtc){
                                if(stc){
                                    if(!this.getModel().getType().isRectangular()) {
                                        sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_OUTSIDE_OBTUSE);
                                        for(int i =0; i < sprites.size; i++) {
                                            sprites.get(i).setPosition(xCenter - 0.25f, yCenter - 0.5f);
                                            sprites.get(i).setFlip(false, true);
                                        }
                                        this.sprites.add(sprites);
                                    }
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_INSIDE_OBTUSE);
                                    for(int i = 0; i < sprites.size; i++)
                                        sprites.get(i).setFlip(false, true);
                                }else{
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.BORDER);
                                    for(int i = 0; i < sprites.size; i++)
                                        sprites.get(i).setFlip(true, true);
                                }
                            }else{
                                if(stc){
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.BORDER);
                                    for(int i = 0; i < sprites.size; i++)
                                        sprites.get(i).setFlip(false, true);
                                }else{
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.CORNER_OBTUSE);

                                }
                            }
                            for(int i = 0; i < sprites.size; i++)
                                sprites.get(i).setPosition(xCenter - 0.25f, yCenter - 0.25f );
                            this.sprites.add(sprites);
                        }


                        // north-west corner

                        otc = getModel().contains(r+1, c-1);
                        wtc = getModel().contains(r, c-1);
                        ntc = getModel().contains(r+1, c); // up ...


                        if(otc){
                            if(wtc){

                                if(!ntc){
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_SIDE_UPSIDE_DOWN);
                                    for(int i = 0; i < sprites.size; i++) {
                                        sprites.get(i).setFlip(false, true);
                                        sprites.get(i).setPosition(xCenter - 0.5f, yCenter - 0.125f);
                                    }
                                    this.sprites.add(sprites);
                                }
                            }else{

                                if(ntc){

                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_SIDE_UPSIDE_DOWN);
                                    for(int i = 0; i < sprites.size; i++)
                                        sprites.get(i).setFlip(false, false);
                                }else{

                                    if(getModel().getType().isRectangular()){
                                        sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.CORNER_ACUTE);
                                    }else {

                                        sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_OUTSIDE_OBTUSE);
                                        for(int i = 0; i < sprites.size; i++) {
                                            sprites.get(i).setPosition(xCenter - 0.75f, yCenter - 0.25f);
                                            sprites.get(i).setFlip(false, true);
                                        }
                                        this.sprites.add(sprites);

                                        sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_SIDE_UPSIDE_DOWN);
                                        for(int i = 0; i < sprites.size; i++) {
                                            sprites.get(i).setFlip(false, true);
                                            sprites.get(i).setPosition(xCenter - 0.5f, yCenter - 0.125f);
                                        }
                                        this.sprites.add(sprites);
                                        sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_SIDE_UPSIDE_DOWN);

                                    }

                                }
                                for(int i = 0; i < sprites.size; i++)
                                    sprites.get(i).setPosition(xCenter - 0.5f, yCenter - 0.125f );
                                this.sprites.add(sprites);

                            }
                        }else{
                            if(wtc){
                                if(ntc){
                                    if(!this.getModel().getType().isRectangular()) {
                                        sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_OUTSIDE_ACUTE);
                                        for(int i = 0; i < sprites.size; i++)
                                            sprites.get(i).setPosition(xCenter - 1f, yCenter - 0.125f);
                                        this.sprites.add(sprites);
                                    }
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_INSIDE_ACUTE);
                                }else{
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.BORDER);
                                }
                            }else{
                                if(ntc){
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.BORDER);
                                    for(int i = 0; i < sprites.size; i++)
                                        sprites.get(i).setFlip(false, true);
                                }else{
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.CORNER_ACUTE);
                                }
                            }
                            for(int i = 0; i < sprites.size; i++)
                                sprites.get(i).setPosition(xCenter - 0.5f, yCenter - 0.125f );
                            this.sprites.add(sprites);
                        }



                        // south-east corner

                        otc = getModel().contains(r-1, c+1); // opposite buildingType covered
                        etc = getModel().contains(r, c+1); // right ...
                        stc = getModel().contains(r-1, c); // down ...

                        if(otc){
                            if(etc){
                                if(!stc){
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_SIDE_UPSIDE_DOWN);
                                    for(int i = 0; i < sprites.size; i++) {
                                        sprites.get(i).setFlip(true, false);
                                        sprites.get(i).setPosition(xCenter, yCenter - 0.125f);
                                    }
                                    this.sprites.add(sprites);
                                }
                            }else{
                                if(stc){
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_SIDE_UPSIDE_DOWN);
                                    for(int i = 0; i < sprites.size; i++)
                                        sprites.get(i).setFlip(true, true);
                                }else{

                                    if(getModel().getType().isRectangular()){
                                        sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.CORNER_ACUTE);
                                        for(int i = 0; i < sprites.size; i++)
                                            sprites.get(i).setFlip(true, false);
                                    }else {
                                        sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_OUTSIDE_OBTUSE);
                                        for(int i = 0; i < sprites.size; i++)
                                            sprites.get(i).setPosition(xCenter + 0.25f, yCenter);
                                        this.sprites.add(sprites);
                                        sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_SIDE_UPSIDE_DOWN);
                                        for(int i = 0; i < sprites.size; i++) {
                                            sprites.get(i).setFlip(true, true);
                                            sprites.get(i).setPosition(xCenter, yCenter - 0.125f);
                                        }
                                        this.sprites.add(sprites);
                                        sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_SIDE_UPSIDE_DOWN);
                                        for(int i = 0; i < sprites.size; i++)
                                            sprites.get(i).setFlip(true, false);
                                    }
                                }
                                for(int i = 0; i < sprites.size; i++)
                                    sprites.get(i).setPosition(xCenter, yCenter - 0.125f );
                                this.sprites.add(sprites);
                            }
                        }else{
                            if(etc){
                                if(stc){
                                    if(!this.getModel().getType().isRectangular()) {
                                        sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_OUTSIDE_ACUTE);
                                        for(int i = 0; i < sprites.size; i++) {
                                            sprites.get(i).setPosition(xCenter + 0.5f, yCenter - 0.125f);
                                            sprites.get(i).setFlip(true, false);
                                        }
                                        this.sprites.add(sprites);
                                    }
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_INSIDE_ACUTE);
                                    for(int i = 0; i < sprites.size; i++)
                                        sprites.get(i).setFlip(true, false);

                                }else{
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.BORDER);
                                    for(int i = 0; i < sprites.size; i++)
                                        sprites.get(i).setFlip(true, true);
                                }
                            }else{
                                if(stc){
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.BORDER);
                                    for(int i = 0; i < sprites.size; i++)
                                        sprites.get(i).setFlip(true, false);
                                }else{
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.CORNER_ACUTE);
                                    for(int i = 0; i < sprites.size; i++)
                                        sprites.get(i).setFlip(true, false);
                                }
                            }
                            for(int i = 0; i < sprites.size; i++)
                                sprites.get(i).setPosition(xCenter, yCenter - 0.125f );
                            this.sprites.add(sprites);
                        }

                        // north-east corner

                        otc = getModel().contains(r+1, c+1); // opposite buildingType covered
                        etc = getModel().contains(r, c+1); // right ...
                        ntc = getModel().contains(r+1, c); // up ...

                        if(otc){
                            if(etc){
                                if(!ntc){
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_SIDE);
                                    for(int i = 0; i < sprites.size; i++) {
                                        sprites.get(i).setFlip(false, true);
                                        sprites.get(i).setPosition(xCenter - 0.25f, yCenter);
                                    }
                                    this.sprites.add(sprites);
                                }
                            }else{
                                if(ntc){
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_SIDE);
                                    for(int i = 0; i < sprites.size; i++)
                                        sprites.get(i).setFlip(true, true);
                                }else{

                                    if(getModel().getType().isRectangular()){
                                        sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.CORNER_OBTUSE);
                                        for(int i = 0; i < sprites.size; i++)
                                            sprites.get(i).setFlip(false, true);
                                    }else {
                                        sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_OUTSIDE_ACUTE);
                                        for(int i = 0; i < sprites.size; i++) {
                                            sprites.get(i).setPosition(xCenter, yCenter + 0.125f);
                                            sprites.get(i).setFlip(true, false);
                                        }
                                        this.sprites.add(sprites);
                                        sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_SIDE);
                                        for(int i = 0; i < sprites.size; i++) {
                                            sprites.get(i).setFlip(false, true);
                                            sprites.get(i).setPosition(xCenter - 0.25f, yCenter);
                                        }
                                        this.sprites.add(sprites);
                                        sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_SIDE);
                                        for(int i = 0; i < sprites.size; i++)
                                            sprites.get(i).setFlip(true, true);
                                    }
                                }
                                for(int i = 0; i < sprites.size; i++)
                                    sprites.get(i).setPosition(xCenter - 0.25f, yCenter );
                                this.sprites.add(sprites);
                            }
                        }else{
                            if(etc){
                                if(ntc){

                                    if(!this.getModel().getType().isRectangular()) {
                                        sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_OUTSIDE_OBTUSE);
                                        for(int i = 0; i < sprites.size; i++)
                                            sprites.get(i).setPosition(xCenter - 0.25f, yCenter + 0.25f);
                                        this.sprites.add(sprites);
                                    }
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.ANTI_INSIDE_OBTUSE);
                                    for(int i = 0; i < sprites.size; i++)
                                        sprites.get(i).setFlip(false,false);

                                }else{
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.BORDER);
                                    for(int i = 0; i < sprites.size; i++)
                                        sprites.get(i).setFlip(false, false);
                                }
                            }else{
                                if(ntc){
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.BORDER);
                                    for(int i = 0; i < sprites.size; i++)
                                        sprites.get(i).setFlip(true, false);
                                }else{
                                    sprites = bfr.spriteProvider.getAreaSS(color, AreaSpriteType.CORNER_OBTUSE);
                                    for(int i = 0; i < sprites.size; i++)
                                        sprites.get(i).setFlip(false, true);
                                }
                            }
                            for(int i = 0; i < sprites.size; i++)
                                sprites.get(i).setPosition(xCenter - 0.25f, yCenter );
                            this.sprites.add(sprites);
                        }

                    }
                }
            }
        }
    }

}
