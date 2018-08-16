package com.lawsgame.emishitactics.core.phases.battle.renderers;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.AreaRenderer;

public class TempoAreaRenderer extends AreaRenderer {

    protected Array<float[]> spriteCoords;          // coordinates to where render the used sprites => 4 sprites = 1 buildingType
    protected Array<Sprite> spriteRefs;             // sprites to render associated with the coordinates above


    protected boolean visible = true;

    public TempoAreaRenderer(Area model) {
        super(model);
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
        return getModel().getBattlefield();
    }

    @Override
    public boolean isExecuting() {
        return false;
    }

    @Override
    public boolean isVisible() {
        return visible;
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
        if(visible) {
            for (int i = 0; i < spriteCoords.size; i++) {
                spriteRefs.get(i).setCenterX(spriteCoords.get(i)[0]);
                spriteRefs.get(i).setCenterY(spriteCoords.get(i)[1]);
                spriteRefs.get(i).draw(batch);
            }
        }
    }

    public void build(){
        spriteRefs.clear();
        spriteCoords.clear();
        if(getModel().getCheckmap() != null){
            boolean[][] checkmap = getModel().getCheckmap();
            Data.AreaType areaType = getModel().getType();
            for(int r = getModel().getRowInit(); r < getModel().getRowInit() + checkmap.length ; r++){
                for(int c = getModel().getColInit(); c < getModel().getColInit() + checkmap[0].length ; c++){
                    //for each buildingType to highlight
                    if(checkmap[r - getModel().getRowInit()][c - getModel().getColInit()]){


                        boolean utc, rtc, otc, ltc, dtc;

                        // bottom left

                        spriteCoords.add(new float[]{c  + 0.25f, r + 0.25f});

                        otc = getModel().contains(r-1, c-1);   // opposite buildingType covered
                        ltc = getModel().contains(r, c-1);          // left ...
                        dtc = getModel().contains(r-1, c);          // down ...



                        if(!ltc && !dtc){
                            spriteRefs.add(TempoSpritePool.getInstance().bottomLeftCorner.get(areaType));
                        }else if(!(ltc && dtc)){
                            if(ltc) {
                                spriteRefs.add(TempoSpritePool.getInstance().southStraight.get(areaType));
                            }else{
                                spriteRefs.add(TempoSpritePool.getInstance().westStraight.get(areaType));
                            }
                        }else{
                            if(otc){
                                spriteRefs.add(TempoSpritePool.getInstance().middle.get(areaType));
                            }else{
                                spriteRefs.add(TempoSpritePool.getInstance().bottomLeftAnticorner.get(areaType));
                            }
                        }



                        // top left



                        spriteCoords.add(new float[]{c  + 0.25f, r + 0.75f});

                        otc = getModel().contains(r+1, c-1);
                        ltc = getModel().contains(r, c-1);
                        utc = getModel().contains(r+1, c); // up ...

                        if(!ltc && !utc){
                            spriteRefs.add(TempoSpritePool.getInstance().topLeftCorner.get(areaType));
                        }else if(!(ltc && utc)){
                            if(ltc) {
                                spriteRefs.add(TempoSpritePool.getInstance().northStraight.get(areaType));
                            }else{
                                spriteRefs.add(TempoSpritePool.getInstance().westStraight.get(areaType));
                            }
                        }else{
                            if(otc){
                                spriteRefs.add(TempoSpritePool.getInstance().middle.get(areaType));
                            }else{
                                spriteRefs.add(TempoSpritePool.getInstance().topLeftAnticorner.get(areaType));
                            }
                        }



                        // bottom right


                        spriteCoords.add(new float[]{c  + 0.75f, r + 0.25f});

                        otc = getModel().contains(r-1, c+1); // opposite buildingType covered
                        rtc = getModel().contains(r, c+1); // right ...
                        dtc = getModel().contains(r-1, c); // down ...

                        if(!rtc && !dtc){
                            spriteRefs.add(TempoSpritePool.getInstance().bottomRightCorner.get(areaType));
                        }else if(!(rtc && dtc)){
                            if(rtc) {
                                spriteRefs.add(TempoSpritePool.getInstance().southStraight.get(areaType));
                            }else{
                                spriteRefs.add(TempoSpritePool.getInstance().eastStraight.get(areaType));
                            }
                        }else{
                            if(otc){
                                spriteRefs.add(TempoSpritePool.getInstance().middle.get(areaType));
                            }else{
                                spriteRefs.add(TempoSpritePool.getInstance().bottomRightAnticorner.get(areaType));
                            }
                        }

                        // top right


                        spriteCoords.add(new float[]{c  + 0.75f, r + 0.75f});

                        otc = getModel().contains(r+1, c+1); // opposite buildingType covered
                        rtc = getModel().contains(r, c+1); // right ...
                        utc = getModel().contains(r+1, c); // up ...

                        if(!rtc && !utc){
                            spriteRefs.add(TempoSpritePool.getInstance().topRightCorner.get(areaType));
                        }else if(!(rtc && utc)){
                            if(rtc) {
                                spriteRefs.add(TempoSpritePool.getInstance().northStraight.get(areaType));
                            }else{
                                spriteRefs.add(TempoSpritePool.getInstance().eastStraight.get(areaType));
                            }
                        }else{
                            if(otc){
                                spriteRefs.add(TempoSpritePool.getInstance().middle.get(areaType));
                            }else{
                                spriteRefs.add(TempoSpritePool.getInstance().topRightAnticorner.get(areaType));
                            }
                        }
                    }
                }
            }
        }
    }


}
