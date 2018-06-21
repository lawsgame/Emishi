package com.lawsgame.emishitactics.core.renderers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Props;
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
                    tileRenderers[r][c] = sprite2DPool.getTileSprite(Props.TileType.getStandard());
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
                    unitRenderers.removeValue(unitRenderers.get(i), true);
                    return true;
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


    /*
     * Useful for some overlapping animations to be performed by before and / or after like attacking by instance.
     * @param models : array of models of which their UR should be rendered in the given order (first before)
    */
    public void rearrangeURRenderingOrder(Array<Unit> models){
        UnitRenderer[] urs = new UnitRenderer[models.size];
        for(int m = 0; m < models.size; m++) {
            for (int i = 0; i < unitRenderers.size; i++) {
                if (unitRenderers.get(i).getModel() == models.get(m)){
                    urs[m] = unitRenderers.removeIndex(i);
                    i--;
                }
            }
        }

        for(int m = 0; m < models.size; m++) {
            if(urs[m] != null){
                unitRenderers.add(urs[m]);
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

    @Override
    public void triggerBuildAnimation(int row, int col, Props.TileType type, Unit builder){
        if(model.checkIndexes(row, col) && (type == Props.TileType.BRIDGE || type == Props.TileType.WATCH_TOWER) ){
            rBuild = row;
            cBuild = col;
            getUnitRenderer(builder).triggerAnimation(Props.AnimationId.BUILD);
            constructionCountDown.run();
            buildingInContructionTR = (type == Props.TileType.BRIDGE) ? TempoSprite2DPool.get().getBridgeInConstruction() : TempoSprite2DPool.get().getTowerInConstruction();

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
        }
    }



    public static class BFRendererException extends Exception{
        public BFRendererException(String msg){
            super(msg);
        }
    }
}
