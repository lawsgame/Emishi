package com.lawsgame.emishitactics.core.renderers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.helpers.TempoSprite2DPool;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.renderers.interfaces.UnitRenderer;
import com.lawsgame.emishitactics.engine.timers.CountDown;

/*
 * TODO: clipping
 */

public class TempoBattlefield2DRenderer extends BattlefieldRenderer {
    protected Array<UnitRenderer> unitRenderers;
    protected TextureRegion[][] tileRenderers;
    protected TempoSprite2DPool sprite2DPool;

    // for building animation
    private int rBuild;
    private int cBuild;
    private TextureRegion buildingInContructionTR;
    private CountDown constructionCountDown = new CountDown(2f);


    public TempoBattlefield2DRenderer(Battlefield battlefield, AssetManager asm) {
        super(battlefield);
        this.unitRenderers = new Array<UnitRenderer>();
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
        if(constructionCountDown.isRunning()){
            batch.draw(buildingInContructionTR, cBuild, rBuild, 1, 1);
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
        constructionCountDown.update(dt);
        if(constructionCountDown.isFinished()){
            constructionCountDown.reset();
        }
        for(int i = 0; i < unitRenderers.size; i++){
            unitRenderers.get(i).update(dt);
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
                unitRenderers.add(new TempoUnitRenderer(r, c, unit));
            }
        }
    }

    private boolean removeUnitRenderer(Unit unit) {
        if(unit != null) {
            for(int i = 0; i< unitRenderers.size; i++){
                if(unitRenderers.get(i).getModel() == unit){
                    return unitRenderers.removeValue(unitRenderers.get(i), true);
                }
            }
        }
        return false;
    }

    public boolean isUnitRendererCreated(Unit unit) {
        for(UnitRenderer ur : unitRenderers){
            if(ur.getModel() == unit){
                return true;
            }
        }
        return false;
    }


    @Override
    public UnitRenderer getUnitRenderer(Unit model) {
        for(int i = 0; i < unitRenderers.size; i++){
            if(unitRenderers.get(i).getModel() == model)
                return unitRenderers.get(i);
        }
        return null;
    }

    @Override
    public void triggerBuildAnimation(int row, int col, Data.TileType buildingType, Unit builder){
        if(model.checkIndexes(row, col) && (buildingType == Data.TileType.BRIDGE || buildingType == Data.TileType.WATCH_TOWER) ){
            rBuild = row;
            cBuild = col;
            getUnitRenderer(builder).triggerAnimation(Data.AnimationId.BUILD);
            constructionCountDown.run();
            buildingInContructionTR = (buildingType == Data.TileType.BRIDGE) ? TempoSprite2DPool.get().getBridgeInConstruction() : TempoSprite2DPool.get().getTowerInConstruction();

        }
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
                    addUnitRenderer(coords[0], coords[1]);
                }else {

                    // change tile renderer
                    addTileRenderer(coords[0], coords[1]);
                }
            } else if (coords.length == 4) {

                //switch units position
                Array<int[]> path = new Array<int[]>();
                if (model.isTileOccupied(coords[0], coords[1]) && model.isTileOccupied(coords[2], coords[3])) {
                    UnitRenderer ur1 = getUnitRenderer(model.getUnit(coords[0], coords[1]));
                    UnitRenderer ur2 = getUnitRenderer(model.getUnit(coords[2], coords[3]));
                    path.add(new int[]{coords[2], coords[3]});
                    ur1.triggerMoveAnimation(path);
                    path.add(new int[]{coords[0], coords[1]});
                    ur2.triggerMoveAnimation(path);
                }
            }
        }else if(data instanceof Array){
            if(((Array)data).size > 0 && ((Array)data).get(0) instanceof int[]){
                Array<int[]> path = (Array<int[]>) data;
                //TODO: trigger walk animation!!

                int[] unitCoords = path.removeIndex(0);
                if(unitCoords.length >= 2 && model.isTileOccupied(unitCoords[0], unitCoords[1])){
                    Unit unit = model.getUnit(unitCoords[0], unitCoords[1]);
                    getUnitRenderer(unit).triggerMoveAnimation(path);
                }


            }
        }
    }



    public static class BFRendererException extends Exception{
        public BFRendererException(String msg){
            super(msg);
        }
    }
}
