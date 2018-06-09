package com.lawsgame.emishitactics.core.renderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Props;
import com.lawsgame.emishitactics.core.managers.interfaces.ISpritePool;
import com.lawsgame.emishitactics.core.models.AArmy;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.renderers.interfaces.ABattlefieldRenderer;
import com.lawsgame.emishitactics.core.renderers.interfaces.AUnitRenderer;

/*
TODO:
- clipping
- update tiles
 */

/**
 *
 */
public class Battlefield2DRenderer extends ABattlefieldRenderer {
    protected Array<AUnitRenderer> unitRenderers;
    protected TextureRegion[][] TRToRender;
    protected ISpritePool sprite2DPool;


    public Battlefield2DRenderer(Battlefield battlefield, ISpritePool sprite2DPool) {
        super(battlefield);
        this.unitRenderers = new Array<AUnitRenderer>();
        this.sprite2DPool = sprite2DPool;

        // pre calculate tile coords and texture region to render to prevent extra calculus each game loop.
        this.TRToRender = new TextureRegion[battlefield.getNbRows()][battlefield.getNbColumns()];
        TextureRegion tileTR;
        try {
            for (int r = 0; r < battlefield.getNbRows(); r++) {
                for (int c = 0; c < battlefield.getNbColumns(); c++) {

                    if (battlefield.isTileExisted(r, c)) {
                        tileTR = sprite2DPool.getTileTextureRegion(battlefield.getTile(r, c));
                        if (tileTR != null) {
                            TRToRender[r][c] = tileTR;
                        } else {
                            TRToRender[r][c] = sprite2DPool.getTileTextureRegion(Props.TileType.getStandard());
                            throw new BFRendererException("expected tile type can not be rendered :"+battlefield.getTile(r, c)+", try checking the /textures/tiles files and the ISpritePool implementation used");
                        }
                    }
                }
            }
        }catch (BFRendererException e){
            e.printStackTrace();
        }
    }

    @Override
    public void addArmyRenderer(AArmy army) {
        AUnitRenderer renderer;
        Array<Array<Unit>> squads = army.getAllSquads();
        for(Array<Unit> squad: squads){
            for(Unit squadMember : squad){
                renderer = new SimpleUnitRenderer(squadMember);
                unitRenderers.add(renderer);
            }
        }
    }

    @Override
    public AUnitRenderer getUnitRenderer(Unit unit) {
        for(int i = 0; i < unitRenderers.size; i++){
            if(unitRenderers.get(i).getModel() == unit){
                return unitRenderers.get(i);
            }
        }
        return null;
    }

    @Override
    public void render(SpriteBatch batch) {
        for(int i = 0; i < unitRenderers.size; i++){
            unitRenderers.get(i).render(batch);
        }
        for(int r = 0; r < model.getNbRows(); r++){
            for(int c = 0; c < model.getNbColumns(); c++){
                batch.draw(TRToRender[r][c], c, r, 1, 1);
            }
        }
    }

    @Override
    public void update(float dt) {
        for(int i = 0; i < unitRenderers.size; i++){
            unitRenderers.get(i).update(dt);
        }
    }

    @Override
    public void getNotification(Object data) {

    }

    public static class BFRendererException extends Exception{

        public BFRendererException(String msg){
            super(msg);
        }
    }
}
