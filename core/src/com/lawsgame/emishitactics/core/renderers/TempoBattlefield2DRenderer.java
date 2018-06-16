package com.lawsgame.emishitactics.core.renderers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Props;
import com.lawsgame.emishitactics.core.managers.TempoSprite2DPool;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.renderers.interfaces.UnitRenderer;

/*
TODO: clipping
 */

/**
 *
 */
public class TempoBattlefield2DRenderer extends BattlefieldRenderer {
    protected Array<UnitRenderer> unitRenderers;
    protected TextureRegion[][] tileRenderers;
    protected TempoSprite2DPool sprite2DPool;


    public TempoBattlefield2DRenderer(Battlefield battlefield, AssetManager asm) {
        super(battlefield);
        this.unitRenderers = new Array<UnitRenderer>();
        this.sprite2DPool = TempoSprite2DPool.get();
        this.sprite2DPool.set(asm);

        // pre calculate tile coords and texture region to render to prevent extra calculus each game loop.
        this.tileRenderers = new TextureRegion[battlefield.getNbRows()][battlefield.getNbColumns()];
        for (int r = 0; r < battlefield.getNbRows(); r++) {
            for (int c = 0; c < battlefield.getNbColumns(); c++) {
                _setTileRenderer(r, c);
                if(model.isTileOccupied(r, c)){
                    unitRenderers.add(new TempoUnitRenderer(r, c, model.getUnit(r, c)));
                }
            }
        }

    }

    private void _setTileRenderer(int r, int c){
        TextureRegion tileTR;
        try{
            if (model.isTileExisted(r, c)) {
                tileTR = sprite2DPool.getTileSprite(model.getTile(r, c));
                if (tileTR != null) {
                    tileRenderers[r][c] = tileTR;
                } else {
                    tileRenderers[r][c] = sprite2DPool.getTileSprite(Props.TileType.getStandard());
                    throw new BFRendererException("expected tile type can not be rendered :"+model.getTile(r, c)+", try checking the /textures/tiles files and the ISpritePool implementation used");
                }
            }
        }catch (BFRendererException e){
            e.printStackTrace();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        for(int r = 0; r < model.getNbRows(); r++){
            for(int c = 0; c < model.getNbColumns(); c++){
                if(model.isTileExisted(r, c)) {
                    batch.draw(tileRenderers[r][c], c, r, 1, 1);
                }
            }
        }
        for(int i = 0; i < unitRenderers.size; i++){
            unitRenderers.get(i).render(batch);
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
        if(data instanceof int[]){
            int[] coordinates = (int[])data;
            if(coordinates.length == 2){
                _setTileRenderer(coordinates[0], coordinates[1]);
            }else if(coordinates.length == 4){

                // TODO:update unit renderer

            }
        }
    }

    @Override
    public UnitRenderer getUnitRenderer(Unit model) {
        for(int i = 0; i < unitRenderers.size; i++){
            if(unitRenderers.get(i).getModel() == model)
                return unitRenderers.get(i);
        }
        return null;
    }

    public static class BFRendererException extends Exception{
        public BFRendererException(String msg){
            super(msg);
        }
    }
}
