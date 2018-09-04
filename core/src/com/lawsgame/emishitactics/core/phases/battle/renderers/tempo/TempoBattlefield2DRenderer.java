package com.lawsgame.emishitactics.core.phases.battle.renderers.tempo;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Notification.Build;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.AreaRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;
import com.lawsgame.emishitactics.engine.timers.CountDown;

/*
 * TODO: clipping
 */

public class TempoBattlefield2DRenderer extends BattlefieldRenderer {

    protected TextureRegion[][] tileRenderers;
    protected TempoSpritePool sprite2DPool;
    protected boolean visible;

    protected CountDown countDown = new CountDown(2f);

    public TempoBattlefield2DRenderer(Battlefield battlefield, AssetManager asm) {
        super(battlefield);

        this.visible = true;
        this.sprite2DPool = TempoSpritePool.getInstance();
        this.sprite2DPool.set(asm);

        // pre calculate buildingType coords and texture region to render to prevent extra calculus each game loop.
        this.tileRenderers = new TextureRegion[battlefield.getNbRows()][battlefield.getNbColumns()];
        for (int r = 0; r < battlefield.getNbRows(); r++) {
            for (int c = 0; c < battlefield.getNbColumns(); c++) {
                addTileRenderer(r, c, getModel().getTile(r, c));
                if(battlefield.isTileOccupied(r, c)) {
                    addUnitRenderer(r, c, getModel().getUnit(r, c));
                }
            }
        }

        // setTiles up area renderers
        for(Data.Allegeance a : Data.Allegeance.values()){
            for (int i = 0; i < getModel().getGuardedAreas().get(a).size; i++) {
                addAreaRenderer(getModel().getGuardedAreas().get(a).get(i));
            }
        }
        for(int i = 0; i < battlefield.getDeploymentAreas().size; i++){
           addAreaRenderer(battlefield.getDeploymentAreas().get(i));
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if(visible) {
            for (int r = 0; r < getModel().getNbRows(); r++) {
                for (int c = 0; c < getModel().getNbColumns(); c++) {
                    if (getModel().isTileExisted(r, c)) {
                        batch.draw(tileRenderers[r][c], c, r, 1, 1);
                    }
                }
            }
            for (int i = 0; i < areaRenderers.size; i++) {
                areaRenderers.get(i).render(batch);
            }
            for (int i = 0; i < unitRenderers.size; i++) {
                unitRenderers.get(i).render(batch);
            }
        }
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        countDown.update(dt);
        if(countDown.isFinished()) {
            countDown.reset();
        }
    }

    @Override
    public boolean isCurrentTaskCompleted(){
        return !countDown.isRunning();
    }

    @Override
    protected void setBuildTask(final Build notif) {
        offerTask(new SimpleCommand() {
            @Override
            public void apply() {
                countDown.run();
                TextureRegion tr = TempoSpritePool.getInstance().getBuildInConstructionSprite(notif.tileType);
                if(tr != null)
                    tileRenderers [notif.row][notif.col] = tr;
                getUnitRenderer(notif.builder).getNotification(Data.AnimationId.BUILD);
            }
        });
        offerTask(new SimpleCommand() {
            @Override
            public void apply() {
                addTileRenderer(notif.row, notif.col, notif.tileType);
            }
        });
    }

    public void addTileRenderer(int r, int c, Data.TileType tileType){
        try{
            if (getModel().isTileExisted(r, c)) {
                TextureRegion tileTR = sprite2DPool.getTileSprite(tileType);
                if (tileTR != null) {
                    tileRenderers[r][c] = tileTR;
                } else {
                    tileRenderers[r][c] = sprite2DPool.getTileSprite(Data.TileType.PLAIN);
                    throw new BFRendererException("expected buildingType type can not be rendered :"+getModel().getTile(r, c)+", try checking the /textures/tiles files and the ISpritePool implementation used");
                }
            }
        }catch (BFRendererException e){
            e.printStackTrace();
        }
    }

    public void addAreaRenderer(Area model){
        areaRenderers.add(new TempoAreaRenderer(model));
    }

    public void removeAreaRenderer(Area model){
        for(int i = 0; i < areaRenderers.size; i++){
            if(areaRenderers.get(i).getModel() == model) {
                model.detach(areaRenderers.get(i));
                areaRenderers.removeIndex(i);
                break;
            }
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

    public void addUnitRenderer(int r, int c, IUnit unit) {
        if(!isUnitRendererCreated(unit)) {
            unitRenderers.add(new TempoUnitRenderer(r, c, unit));
        }

    }

    public void removeUnitRenderer(IUnit unit) {
        if(unit != null) {
            for(int i = 0; i< unitRenderers.size; i++){
                if(unitRenderers.get(i).getModel() == unit){
                    unit.detach(unitRenderers.get(i));
                    removeAreaRenderersAssociatedWith(unit);
                    unitRenderers.removeValue(unitRenderers.get(i), true);
                }
            }
        }
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
    public AreaRenderer getAreaRenderer(Area area) {
        for(int i = 0; i < areaRenderers.size; i++){
            if(areaRenderers.get(i).getModel() == area)
                return areaRenderers.get(i);
        }
        return null;
    }

    @Override
    public int getRowFrom(float gameX, float gameY) {
        return (int)gameY;
    }

    @Override
    public int getColFrom(float gameX, float gameY) {
        return (int)gameX;
    }

    @Override
    protected float getXFrom(int row, int col) { return col; }

    @Override
    protected float getYFrom(int row, int col) { return row; }

    @Override
    public void displayDeploymentAreas(boolean visible) {
        Array<Area> deploymentArea = getModel().getDeploymentAreas();
        for(int i = 0; i< deploymentArea.size; i++){
            for(int j = 0 ; j < areaRenderers.size; j++){
                if(deploymentArea.get(i) == areaRenderers.get(j).getModel()){
                    areaRenderers.get(j).setVisible(visible);
                }
            }
        }
    }

    @Override
    public boolean isExecuting() {
        if(countDown.isRunning())
            return true;
        for(int i = 0; i < unitRenderers.size; i++){
            if(unitRenderers.get(i).isExecuting()){
                return true;
            }
        }
        return false;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void dispose(){
        super.dispose();
        for(int i =0; i < unitRenderers.size; i++){
            unitRenderers.get(i).dispose();
        }
    }



}
