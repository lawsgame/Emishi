package com.lawsgame.emishitactics.core.phases.battle.renderers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.helpers.TempoSprite2DPool;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Battlefield.BuildMessage;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;

/*
 * TODO: clipping
 */

public class TempoBattlefield2DRenderer extends BattlefieldRenderer {
    protected Array<BattleUnitRenderer> unitRenderers;
    protected TextureRegion[][] tileRenderers;
    protected TempoSprite2DPool sprite2DPool;
    protected boolean executing;

    public TempoBattlefield2DRenderer(Battlefield battlefield, AssetManager asm) {
        super(battlefield);
        this.unitRenderers = new Array<BattleUnitRenderer>();
        this.sprite2DPool = TempoSprite2DPool.get();
        this.sprite2DPool.set(asm);

        // pre calculate tile coords and texture region to render to prevent extra calculus each game loop.
        this.tileRenderers = new TextureRegion[battlefield.getNbRows()][battlefield.getNbColumns()];
        for (int r = 0; r < battlefield.getNbRows(); r++) {
            for (int c = 0; c < battlefield.getNbColumns(); c++) {
                addTileRenderer(r, c);
                addUnitRenderer(r, c);
            }
        }

    }


    @Override
    public void renderTiles(SpriteBatch batch) {
        for (int r = 0; r < model.getNbRows(); r++) {
            for (int c = 0; c < model.getNbColumns(); c++) {
                if (model.isTileExisted(r, c)) {
                    batch.draw(tileRenderers[r][c], c, r, 1, 1);
                }
            }
        }
    }

    @Override
    public void renderUnits(SpriteBatch batch) {
        for(int i = 0; i < unitRenderers.size; i++){
            unitRenderers.get(i).render(batch);
        }
    }

    @Override
    public void update(float dt) {
        executing = false;
        for(int i = 0; i < unitRenderers.size; i++){
            unitRenderers.get(i).update(dt);
            executing = executing || unitRenderers.get(i).isExecuting();
        }

    }

    private void addTileRenderer(int r, int c){
        TextureRegion tileTR;
        try{
            if (model.isTileExisted(r, c)) {
                tileTR = sprite2DPool.getTileSprite(model.getTile(r, c));
                if (tileTR != null) {
                    tileRenderers[r][c] = tileTR;
                } else {
                    tileRenderers[r][c] = sprite2DPool.getTileSprite(Data.TileType.getStandard());
                    throw new BFRendererException("expected tile type can not be rendered :"+model.getTile(r, c)+", try checking the /textures/tiles files and the ISpritePool implementation used");
                }
            }
        }catch (BFRendererException e){
            e.printStackTrace();
        }
    }

    public void addUnitRenderer(int r, int c) {
        if(model.isTileOccupied(r,c)) {
            Unit unit = model.getUnit(r, c);
            if(!isUnitRendererCreated(unit)) {
                unitRenderers.add(new TempoUnitRenderer(r, c, unit, this.model));
            }
        }
    }

    private boolean removeUnitRenderer(Unit unit) {
        if(unit != null) {
            for(int i = 0; i< unitRenderers.size; i++){
                if(unitRenderers.get(i).getModel() == unit){
                    unit.detach(unitRenderers.get(i));
                    return unitRenderers.removeValue(unitRenderers.get(i), true);
                }
            }
        }
        return false;
    }

    public boolean isUnitRendererCreated(Unit unit) {
        for(BattleUnitRenderer ur : unitRenderers){
            if(ur.getModel() == unit){
                return true;
            }
        }
        return false;
    }

    public BattleUnitRenderer getUnitRenderer(Unit model) {
        for(int i = 0; i < unitRenderers.size; i++){
            if(unitRenderers.get(i).getModel() == model)
                return unitRenderers.get(i);
        }
        return null;
    }

    @Override
    public boolean isExecuting() {
        return executing;
    }

    @Override
    public void dispose(){
        super.dispose();
        for(int i =0; i < unitRenderers.size; i++){
            unitRenderers.get(i).dispose();
        }
    }

    /**
     *
     *
     * @param data
     */
    @Override
    public void getNotification(Object data) {
        int[] coords;

        if (data instanceof Unit) {
            // remove the sent unit
            removeUnitRenderer((Unit) data);

        } else if(data instanceof int[]) {
            coords = (int[]) data;
            if (coords.length == 2) {

                if(model.isTileOccupied(coords[0], coords[1])){
                    // add a unit renderer to a newly deployed unit
                    Unit unit = model.getUnit(coords[0], coords[1]);
                    if(isUnitRendererCreated(unit)){
                        BattleUnitRenderer bur = getUnitRenderer(unit);
                        bur.setX(coords[1]);
                        bur.setY(coords[0]);
                    }else {
                        addUnitRenderer(coords[0], coords[1]);
                    }
                }else {

                    // change tile renderer
                    addTileRenderer(coords[0], coords[1]);
                }
            } else if (coords.length == 4) {

                //switch units position
                Array<int[]> path = new Array<int[]>();
                if (model.isTileOccupied(coords[0], coords[1]) && model.isTileOccupied(coords[2], coords[3])) {
                    BattleUnitRenderer ur1 = getUnitRenderer(model.getUnit(coords[0], coords[1]));
                    BattleUnitRenderer ur2 = getUnitRenderer(model.getUnit(coords[2], coords[3]));
                    path.add(new int[]{coords[2], coords[3]});
                    ur1.displayWalk(path);
                    path.clear();
                    path.add(new int[]{coords[0], coords[1]});
                    ur2.displayWalk(path);
                }
            }

        }else if(data instanceof Array){
            if(((Array)data).size > 0 && ((Array)data).get(0) instanceof int[]){
                Array<int[]> path = (Array<int[]>) data;
                int[] unitCoords = path.removeIndex(0);
                if(unitCoords.length >= 2 && model.isTileOccupied(unitCoords[0], unitCoords[1])){
                    Unit unit = model.getUnit(unitCoords[0], unitCoords[1]);
                    getUnitRenderer(unit).displayWalk(path);
                }

            }
        }else if(data instanceof BuildMessage){
            BuildMessage msg = (BuildMessage)data;
            addTileRenderer(msg.row , msg.col);
        }
    }



    public static class BFRendererException extends Exception{
        public BFRendererException(String msg){
            super(msg);
        }
    }
}
