package com.lawsgame.emishitactics.core.phases.battle.renderers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.helpers.interfaces.SpriteProvider.AreaSpriteType;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Data.AreaColor;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.AreaRenderer;

public class IsoAreaRenderer extends AreaRenderer {
    protected Array<Sprite> sprites;
    protected IsoBFR bfr;

    public IsoAreaRenderer(Area model, IsoBFR bfr) {
        super(model);
        this.sprites = new Array<Sprite>();
        this.bfr = bfr;
        change();
    }

    @Override
    protected void renderArea(SpriteBatch batch) {
        for(int i = 0; i < sprites.size; i++){
            sprites.get(i).draw(batch);
        }
    }

    @Override
    public void change() {
        sprites.clear();

        if(getModel().getCheckmap() != null){

            Sprite sprite = null;
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
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_SIDE));
                                    sprite.setFlip(true, false);
                                    sprite.setPosition(xCenter - 0.25f, yCenter - 0.25f );
                                    sprites.add(sprite);
                                }
                            }else{
                                if(stc){
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_SIDE));
                                }else{

                                    if(getModel().getType().isRectangular()){
                                        sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.CORNER_OBTUSE));
                                    }else{
                                        sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_OUTSIDE_ACUTE));
                                        sprite.setPosition(xCenter - 0.5f, yCenter - 0.375f );
                                        sprite.setFlip(false, false);
                                        sprites.add(sprite);
                                        sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_SIDE));
                                        sprite.setFlip(true, false);
                                        sprite.setPosition(xCenter - 0.25f, yCenter - 0.25f );
                                        sprites.add(sprite);
                                        sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_SIDE));
                                    }
                                }
                                sprite.setPosition(xCenter - 0.25f, yCenter - 0.25f );
                                sprites.add(sprite);
                            }
                        }else{
                            if(wtc){
                                if(stc){
                                    if(!this.getModel().getType().isRectangular()) {
                                        sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_OUTSIDE_OBTUSE));
                                        sprite.setPosition(xCenter - 0.25f, yCenter - 0.5f);
                                        sprite.setFlip(false, true);
                                        sprites.add(sprite);
                                    }
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_INSIDE_OBTUSE));
                                    sprite.setFlip(false, true);
                                }else{
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.BORDER));
                                    sprite.setFlip(true, true);
                                }
                            }else{
                                if(stc){
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.BORDER));
                                    sprite.setFlip(false, true);
                                }else{
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.CORNER_OBTUSE));

                                }
                            }
                            sprite.setPosition(xCenter - 0.25f, yCenter - 0.25f );
                            sprites.add(sprite);
                        }


                        // north-west corner

                        otc = getModel().contains(r+1, c-1);
                        wtc = getModel().contains(r, c-1);
                        ntc = getModel().contains(r+1, c); // up ...


                        if(otc){
                            if(wtc){

                                if(!ntc){
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_SIDE_UPSIDE_DOWN));
                                    sprite.setFlip(false, true);
                                    sprite.setPosition(xCenter - 0.5f, yCenter - 0.125f );
                                    sprites.add(sprite);
                                }
                            }else{

                                if(ntc){

                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_SIDE_UPSIDE_DOWN));
                                    sprite.setFlip(false, false);
                                }else{

                                    if(getModel().getType().isRectangular()){
                                        sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.CORNER_ACUTE));
                                    }else {

                                        sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_OUTSIDE_OBTUSE));
                                        sprite.setPosition(xCenter - 0.75f, yCenter - 0.25f);
                                        sprite.setFlip(false, true);
                                        sprites.add(sprite);

                                        sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_SIDE_UPSIDE_DOWN));
                                        sprite.setFlip(false, true);
                                        sprite.setPosition(xCenter - 0.5f, yCenter - 0.125f);
                                        sprites.add(sprite);
                                        sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_SIDE_UPSIDE_DOWN));

                                    }

                                }
                                sprite.setPosition(xCenter - 0.5f, yCenter - 0.125f );
                                sprites.add(sprite);

                            }
                        }else{
                            if(wtc){
                                if(ntc){
                                    if(!this.getModel().getType().isRectangular()) {
                                        sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_OUTSIDE_ACUTE));
                                        sprite.setPosition(xCenter - 1f, yCenter - 0.125f);
                                        sprites.add(sprite);
                                    }
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_INSIDE_ACUTE));
                                }else{
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.BORDER));
                                }
                            }else{
                                if(ntc){
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.BORDER));
                                    sprite.setFlip(false, true);
                                }else{
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.CORNER_ACUTE));
                                }
                            }
                            sprite.setPosition(xCenter - 0.5f, yCenter - 0.125f );
                            sprites.add(sprite);
                        }



                        // south-east corner

                        otc = getModel().contains(r-1, c+1); // opposite buildingType covered
                        etc = getModel().contains(r, c+1); // right ...
                        stc = getModel().contains(r-1, c); // down ...

                        if(otc){
                            if(etc){
                                if(!stc){
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_SIDE_UPSIDE_DOWN));
                                    sprite.setFlip(true, false);
                                    sprite.setPosition(xCenter, yCenter - 0.125f );
                                    sprites.add(sprite);
                                }
                            }else{
                                if(stc){
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_SIDE_UPSIDE_DOWN));
                                    sprite.setFlip(true, true);
                                }else{

                                    if(getModel().getType().isRectangular()){
                                        sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.CORNER_ACUTE));
                                        sprite.setFlip(true, false);
                                    }else {
                                        sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_OUTSIDE_OBTUSE));
                                        sprite.setPosition(xCenter + 0.25f, yCenter);
                                        sprites.add(sprite);
                                        sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_SIDE_UPSIDE_DOWN));
                                        sprite.setFlip(true, true);
                                        sprite.setPosition(xCenter, yCenter - 0.125f);
                                        sprites.add(sprite);
                                        sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_SIDE_UPSIDE_DOWN));
                                        sprite.setFlip(true, false);
                                    }
                                }
                                sprite.setPosition(xCenter, yCenter - 0.125f );
                                sprites.add(sprite);
                            }
                        }else{
                            if(etc){
                                if(stc){
                                    if(!this.getModel().getType().isRectangular()) {
                                        sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_OUTSIDE_ACUTE));
                                        sprite.setPosition(xCenter + 0.5f, yCenter - 0.125f);
                                        sprite.setFlip(true, false);
                                        sprites.add(sprite);
                                    }
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_INSIDE_ACUTE));
                                    sprite.setFlip(true, false);

                                }else{
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.BORDER));
                                    sprite.setFlip(true, true);
                                }
                            }else{
                                if(stc){
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.BORDER));
                                    sprite.setFlip(true, false);
                                }else{
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.CORNER_ACUTE));
                                    sprite.setFlip(true, false);
                                }
                            }
                            sprite.setPosition(xCenter, yCenter - 0.125f );
                            sprites.add(sprite);
                        }

                        // north-east corner

                        otc = getModel().contains(r+1, c+1); // opposite buildingType covered
                        etc = getModel().contains(r, c+1); // right ...
                        ntc = getModel().contains(r+1, c); // up ...

                        if(otc){
                            if(etc){
                                if(!ntc){
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_SIDE));
                                    sprite.setFlip(false, true);
                                    sprite.setPosition(xCenter - 0.25f, yCenter );
                                    sprites.add(sprite);
                                }
                            }else{
                                if(ntc){
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_SIDE));
                                    sprite.setFlip(true, true);
                                }else{

                                    if(getModel().getType().isRectangular()){
                                        sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.CORNER_OBTUSE));
                                        sprite.setFlip(false, true);
                                    }else {
                                        sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_OUTSIDE_ACUTE));
                                        sprite.setPosition(xCenter, yCenter + 0.125f);
                                        sprite.setFlip(true, false);
                                        sprites.add(sprite);
                                        sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_SIDE));
                                        sprite.setFlip(false, true);
                                        sprite.setPosition(xCenter - 0.25f, yCenter);
                                        sprites.add(sprite);
                                        sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_SIDE));
                                        sprite.setFlip(true, true);
                                    }
                                }
                                sprite.setPosition(xCenter - 0.25f, yCenter );
                                sprites.add(sprite);
                            }
                        }else{
                            if(etc){
                                if(ntc){

                                    if(!this.getModel().getType().isRectangular()) {
                                        sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_OUTSIDE_OBTUSE));
                                        sprite.setPosition(xCenter - 0.25f, yCenter + 0.25f);
                                        sprites.add(sprite);
                                    }
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.ANTI_INSIDE_OBTUSE));
                                    sprite.setFlip(false,false);

                                }else{
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.BORDER));
                                    sprite.setFlip(false, false);
                                }
                            }else{
                                if(ntc){
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.BORDER));
                                    sprite.setFlip(true, false);
                                }else{
                                    sprite = new Sprite(bfr.assetProvider.getAreaSprite(color, AreaSpriteType.CORNER_OBTUSE));
                                    sprite.setFlip(false, true);
                                }
                            }
                            sprite.setPosition(xCenter - 0.25f, yCenter );
                            sprites.add(sprite);
                        }

                    }
                }
            }
        }
    }

}
