package com.lawsgame.emishitactics.core.phases.battle.renderers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Battlefield.BuildNotif;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.AreaRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;

/*
 * TODO: clipping
 */

public class TempoBattlefield2DRenderer extends BattlefieldRenderer {
    protected Array<BattleUnitRenderer> unitRenderers;
    protected Array<AreaRenderer> areaRenderers;
    protected TextureRegion[][] tileRenderers;
    protected TempoSpritePool sprite2DPool;

    public TempoBattlefield2DRenderer(Battlefield battlefield, AssetManager asm) {
        super(battlefield);
        this.unitRenderers = new Array<BattleUnitRenderer>();
        this.sprite2DPool = TempoSpritePool.get();
        this.sprite2DPool.set(asm);

        // pre calculate tile coords and texture region to render to prevent extra calculus each game loop.
        this.tileRenderers = new TextureRegion[battlefield.getNbRows()][battlefield.getNbColumns()];
        for (int r = 0; r < battlefield.getNbRows(); r++) {
            for (int c = 0; c < battlefield.getNbColumns(); c++) {
                addTileRenderer(r, c);
                addUnitRenderer(r, c);
            }
        }

        // addExpGained up area renderers
        this.areaRenderers = new Array<AreaRenderer>();
        for(Data.Allegeance a : Data.Allegeance.values()){
            for (int i = 0; i < model.getCoveredAreas().get(a).size; i++) {
                addaAreaRenderer(model.getCoveredAreas().get(a).get(i));
            }
            for (int i = 0; i < model.getGuardedAreas().get(a).size; i++) {
                addaAreaRenderer(model.getGuardedAreas().get(a).get(i));
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

        for(int i = 0; i < areaRenderers.size; i++)
            areaRenderers.get(i).render(batch);
    }

    @Override
    public void renderUnits(SpriteBatch batch) {

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

    private void addTileRenderer(int r, int c){
        TextureRegion tileTR;
        try{
            if (model.isTileExisted(r, c)) {
                tileTR = sprite2DPool.getTileSprite(model.getTile(r, c));
                if (tileTR != null) {
                    tileRenderers[r][c] = tileTR;
                } else {
                    tileRenderers[r][c] = sprite2DPool.getTileSprite(Data.TileType.PLAIN);
                    throw new BFRendererException("expected tile type can not be rendered :"+model.getTile(r, c)+", try checking the /textures/tiles files and the ISpritePool implementation used");
                }
            }
        }catch (BFRendererException e){
            e.printStackTrace();
        }
    }

    public void addaAreaRenderer(Area model){
        areaRenderers.add(new TempoAreaRenderer(model));
    }

    public void removeAreaRenderer(Area model){
        for(int i = 0; i < areaRenderers.size; i++){
            if(areaRenderers.get(i).getModel() == model) {
                model.detach(areaRenderers.get(i));
                areaRenderers.removeIndex(i);
            }
            continue;
        }
    }

    public void removeAreaRenderersAssociatedWith(IUnit unit){
        Area.UnitArea areaModel;
        for(int i = 0; i < areaRenderers.size; i++){
            if(areaRenderers.get(i).getModel() instanceof Area.UnitArea){
                areaModel = (Area.UnitArea)areaRenderers.get(i).getModel();
                if(areaModel.getActor() == unit){
                    areaRenderers.removeIndex(i);
                    i--;
                }
            }
        }
    }

    public void addUnitRenderer(int r, int c) {
        if(model.isTileOccupied(r,c)) {
            IUnit unit = model.getUnit(r, c);
            if(!isUnitRendererCreated(unit)) {
                unitRenderers.add(new TempoUnitRenderer(r, c, unit));
            }
        }
    }

    private boolean removeUnitRenderer(IUnit unit) {
        if(unit != null) {
            for(int i = 0; i< unitRenderers.size; i++){
                if(unitRenderers.get(i).getModel() == unit){
                    unit.detach(unitRenderers.get(i));
                    removeAreaRenderersAssociatedWith(unit);
                    return unitRenderers.removeValue(unitRenderers.get(i), true);
                }
            }
        }
        return false;
    }

    public boolean isUnitRendererCreated(IUnit unit) {
        for (BattleUnitRenderer ur : unitRenderers) {
            if (ur.getModel() == unit) {
                return true;
            }
        }
        return false;
    }

    @Override
    public BattleUnitRenderer getUnitRenderer(IUnit model) {
        for(int i = 0; i < unitRenderers.size; i++){
            if(unitRenderers.get(i).getModel() == model)
                return unitRenderers.get(i);
        }
        return null;
    }

    @Override
    public boolean areRenderersUpdated() {
        //TODO:




        return false;
    }

    @Override
    public boolean isExecuting() {
        for(int i = 0; i < unitRenderers.size; i++){
            if(unitRenderers.get(i).isExecuting()){
                return true;
            }
        }
        return false;
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
        final int[] coords;
        if (data instanceof IUnit) {
            // remove the sent unit
            removeUnitRenderer((IUnit) data);

        } else if(data instanceof int[]) {
            coords = (int[]) data;
            if (coords.length == 2) {

                if(model.isTileOccupied(coords[0], coords[1])){
                    // addExpGained a unit receiver to a newly deployed unit
                    IUnit unit = model.getUnit(coords[0], coords[1]);
                    if(isUnitRendererCreated(unit)){
                        final BattleUnitRenderer bur = getUnitRenderer(unit);
                        bur.getNotification(new SimpleCommand(){

                            @Override
                            public void apply() {
                                bur.setX(coords[1]);
                                bur.setY(coords[0]);
                                removeAreaRenderersAssociatedWith(bur.getModel());
                            }
                        });

                    }else {
                        addUnitRenderer(coords[0], coords[1]);
                    }
                }else {

                    // change tile receiver
                    addTileRenderer(coords[0], coords[1]);
                }
            } else if (coords.length == 4) {
                //switch units position or move unit
                Array<int[]> path = new Array<int[]>();
                if (model.isTileOccupied(coords[0], coords[1]) && model.isTileOccupied(coords[2], coords[3])) {
                    BattleUnitRenderer bur1 = getUnitRenderer(model.getUnit(coords[0], coords[1]));
                    BattleUnitRenderer bur2 = getUnitRenderer(model.getUnit(coords[2], coords[3]));
                    if(bur1 != null && bur2 != null) {
                        removeAreaRenderersAssociatedWith(bur1.getModel());
                        removeAreaRenderersAssociatedWith(bur2.getModel());
                        path.add(new int[]{coords[2], coords[3]});
                        bur1.displayWalk(path);
                        path.clear();
                        path.add(new int[]{coords[0], coords[1]});
                        bur2.displayWalk(path);
                    }
                }
            }

        }else if(data instanceof Array){
            if(((Array)data).size > 0 && ((Array)data).get(0) instanceof int[]){
                Array<int[]> path = (Array<int[]>) data;
                int[] unitCoords = path.get(path.size - 1);
                if(unitCoords.length >= 2 && model.isTileOccupied(unitCoords[0], unitCoords[1])){
                    IUnit unit = model.getUnit(unitCoords[0], unitCoords[1]);
                    unit.notifyAllObservers(path);
                    removeAreaRenderersAssociatedWith(unit);
                }

            }
        }else if(data instanceof BuildNotif){
            BuildNotif msg = (BuildNotif)data;
            addTileRenderer(msg.row , msg.col);
        }else if(data instanceof Area){
            Area area = (Area)data;

            boolean areaRemoved = false;
            for(int i = 0; i < areaRenderers.size; i++){
                if(area == areaRenderers.get(i).getModel()){
                    removeAreaRenderer(area);
                    areaRemoved = true;
                    continue;
                }
            }

            if(!areaRemoved){
                addaAreaRenderer(area);
            }
        }
    }



    public static class BFRendererException extends Exception{
        public BFRendererException(String msg){
            super(msg);
        }
    }
}
