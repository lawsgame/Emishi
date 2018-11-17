package com.lawsgame.emishitactics.core.phases.battle.renderers.tempo;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Notification.Build;
import com.lawsgame.emishitactics.core.models.Tile;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.AreaRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.CameraManager;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;
import com.lawsgame.emishitactics.engine.timers.CountDown;

/*
 * TODO: clipping
 */

public class TempoBattlefield2DRenderer extends BattlefieldRenderer {
    private static final float CAM_VELOCITY = 15.0f;

    private Array<BattleUnitRenderer> unitRenderers;
    protected TextureRegion[][] tileRenderers;
    protected TempoSpritePool sprite2DPool;
    protected boolean visible;
    protected Array<AreaRenderer> areaRenderers;

    protected CountDown countDown = new CountDown(2f);

    public TempoBattlefield2DRenderer(Battlefield battlefield, TempoSpritePool spritePool) {
        super(battlefield);
        this.unitRenderers = new Array<BattleUnitRenderer>();
        this.areaRenderers = new Array<AreaRenderer>();

        this.visible = true;
        this.sprite2DPool = spritePool;

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
        for (int i = 0; i < getModel().getUnitAreas().size ; i++) {
            addAreaRenderer(getModel().getUnitAreas().get(i));
        }
        for(int i = 0; i < battlefield.getDeploymentAreas().size; i++){
           addAreaRenderer(battlefield.getDeploymentAreas().get(i));
        }
    }


    @Override
    public void prerender() {

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

        for(int i = 0; i < areaRenderers.size; i++) {
            areaRenderers.get(i).update(dt);
        }
        for(int i =0; i < unitRenderers.size; i++){
            unitRenderers.get(i).update(dt);
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
                TextureRegion tr = sprite2DPool.getBuildInConstructionSprite(notif.tile.getType());
                if(tr != null)
                    tileRenderers [notif.row][notif.col] = tr;
                notif.builder.notifyAllObservers(Data.AnimId.BUILD);
            }
        });
        offerTask(new SimpleCommand() {
            @Override
            public void apply() {
                addTileRenderer(notif.row, notif.col, notif.tile);
            }
        });
    }

    public void addTileRenderer(int r, int c, Tile tile){
        try{
            if (getModel().isTileExisted(r, c)) {
                TextureRegion tileTR = sprite2DPool.getTileSprite(tile.getType());
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

    public void removeAreaRenderersAssociatedWith(Unit unit){
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

    public BattleUnitRenderer addUnitRenderer(int r, int c, Unit unit) {
        if(!isUnitRendererCreated(unit)) {
            BattleUnitRenderer bur = new TempoUnitRenderer(r, c, unit);
            unitRenderers.add(bur);
            return bur;
        }
        return null;
    }

    public void removeUnitRenderer(Unit unit) {
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

    public boolean isUnitRendererCreated(Unit unit) {
        for (BattleUnitRenderer ur : unitRenderers) {
            if (ur.getModel() == unit) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean isAreaRendererCreated(Area model) {
        boolean existed = false;
        for(int i = 0; i < areaRenderers.size; i++){
            if(areaRenderers.get(i).getModel() == model)
                return true;
        }
        return existed;
    }


    @Override
    public BattleUnitRenderer getUnitRenderer(Unit model) {
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
    public int getRow(float gameX, float gameY) {
        return (int)gameY;
    }

    @Override
    public int getCol(float gameX, float gameY) {
        return (int)gameX;
    }

    @Override
    public float getCenterX(int row, int col) {
        return col + 0.5f;
    }

    @Override
    public float getCenterY(int row, int col) {
        return row + 0.5f;
    }


    @Override
    public void setGameCamParameters(CameraManager cameraManager) {
        cameraManager.setCameraBoundaries(getModel().getNbColumns(), getModel().getNbRows());
        cameraManager.setCameraVelocity(CAM_VELOCITY);
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
