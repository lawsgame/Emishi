package com.lawsgame.emishitactics.core.phases.battle.renderers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.AreaRenderer;

public class TempoAreaRenderer extends AreaRenderer {

    private Assets.AreaColor id;                    // the id of type of sprite to use
    protected Array<float[]> spriteCoords;          // coordinates to where render the used sprites => 4 sprites = 1 tile
    protected Array<Sprite> spriteRefs;             // sprites to render associated with the coordinates above


    protected boolean visible = true;

    public TempoAreaRenderer(Area model, Assets.AreaColor color) {
        super(model);
        this.id =color;
        this.spriteCoords = new Array<float[]>();
        this.spriteRefs = new Array<Sprite>();
        build();
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public Battlefield getBattlefield() {
        return model.getBattlefield();
    }

    @Override
    public boolean isExecuting() {
        return false;
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void getNotification(Object data) {
        build();
    }

    @Override
    public void render(SpriteBatch batch) {
        for (int i = 0; i < spriteCoords.size; i++) {
            spriteRefs.get(i).setCenterX(spriteCoords.get(i)[0]);
            spriteRefs.get(i).setCenterY(spriteCoords.get(i)[1]);
            spriteRefs.get(i).draw(batch);
        }
    }

    public void build(){
        spriteRefs.clear();
        spriteCoords.clear();
        if(model.getCheckmap() != null){
            boolean[][] checkmap = model.getCheckmap();
            for(int r = model.getRowInit(); r < model.getRowInit() + checkmap.length ; r++){
                for(int c = model.getColInit(); c < model.getColInit() + checkmap[0].length ; c++){
                    //for each tile to highlight
                    if(checkmap[r - model.getRowInit()][c - model.getColInit()]){


                        boolean utc, rtc, otc, ltc, dtc;

                        // bottom left

                        spriteCoords.add(new float[]{c  + 0.25f, r + 0.25f});

                        otc = model.contains(r-1, c-1);   // opposite tile covered
                        ltc = model.contains(r, c-1);          // left ...
                        dtc = model.contains(r-1, c);          // down ...



                        if(!ltc && !dtc){
                            spriteRefs.add(TempoSpritePool.get().bottomLeftCorner.get(id));
                        }else if(!(ltc && dtc)){
                            if(ltc) {
                                spriteRefs.add(TempoSpritePool.get().southStraight.get(id));
                            }else{
                                spriteRefs.add(TempoSpritePool.get().westStraight.get(id));
                            }
                        }else{
                            if(otc){
                                spriteRefs.add(TempoSpritePool.get().middle.get(id));
                            }else{
                                spriteRefs.add(TempoSpritePool.get().bottomLeftAnticorner.get(id));
                            }
                        }



                        // top left



                        spriteCoords.add(new float[]{c  + 0.25f, r + 0.75f});

                        otc = model.contains(r+1, c-1);
                        ltc = model.contains(r, c-1);
                        utc = model.contains(r+1, c); // up ...

                        if(!ltc && !utc){
                            spriteRefs.add(TempoSpritePool.get().topLeftCorner.get(id));
                        }else if(!(ltc && utc)){
                            if(ltc) {
                                spriteRefs.add(TempoSpritePool.get().northStraight.get(id));
                            }else{
                                spriteRefs.add(TempoSpritePool.get().westStraight.get(id));
                            }
                        }else{
                            if(otc){
                                spriteRefs.add(TempoSpritePool.get().middle.get(id));
                            }else{
                                spriteRefs.add(TempoSpritePool.get().topLeftAnticorner.get(id));
                            }
                        }



                        // bottom right


                        spriteCoords.add(new float[]{c  + 0.75f, r + 0.25f});

                        otc = model.contains(r-1, c+1); // opposite tile covered
                        rtc = model.contains(r, c+1); // right ...
                        dtc = model.contains(r-1, c); // down ...

                        if(!rtc && !dtc){
                            spriteRefs.add(TempoSpritePool.get().bottomRightCorner.get(id));
                        }else if(!(rtc && dtc)){
                            if(rtc) {
                                spriteRefs.add(TempoSpritePool.get().southStraight.get(id));
                            }else{
                                spriteRefs.add(TempoSpritePool.get().eastStraight.get(id));
                            }
                        }else{
                            if(otc){
                                spriteRefs.add(TempoSpritePool.get().middle.get(id));
                            }else{
                                spriteRefs.add(TempoSpritePool.get().bottomRightAnticorner.get(id));
                            }
                        }

                        // top right


                        spriteCoords.add(new float[]{c  + 0.75f, r + 0.75f});

                        otc = model.contains(r+1, c+1); // opposite tile covered
                        rtc = model.contains(r, c+1); // right ...
                        utc = model.contains(r+1, c); // up ...

                        if(!rtc && !utc){
                            spriteRefs.add(TempoSpritePool.get().topRightCorner.get(id));
                        }else if(!(rtc && utc)){
                            if(rtc) {
                                spriteRefs.add(TempoSpritePool.get().northStraight.get(id));
                            }else{
                                spriteRefs.add(TempoSpritePool.get().eastStraight.get(id));
                            }
                        }else{
                            if(otc){
                                spriteRefs.add(TempoSpritePool.get().middle.get(id));
                            }else{
                                spriteRefs.add(TempoSpritePool.get().topRightAnticorner.get(id));
                            }
                        }
                    }
                }
            }
        }
    }


}
