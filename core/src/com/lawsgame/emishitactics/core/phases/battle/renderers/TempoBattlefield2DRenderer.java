package com.lawsgame.emishitactics.core.phases.battle.renderers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Notification.Build;
import com.lawsgame.emishitactics.core.models.Notification.SetTile;
import com.lawsgame.emishitactics.core.models.Notification.SetUnit;
import com.lawsgame.emishitactics.core.models.Notification.SwitchPosition;
import com.lawsgame.emishitactics.core.models.Notification.Walk;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.AreaRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;
import com.lawsgame.emishitactics.engine.timers.CountDown;

import java.util.LinkedList;

/*
 * TODO: clipping
 */

public class TempoBattlefield2DRenderer extends BattlefieldRenderer {
    protected Array<BattleUnitRenderer> unitRenderers;
    protected Array<AreaRenderer> areaRenderers;
    protected TextureRegion[][] tileRenderers;
    protected TempoSpritePool sprite2DPool;

    protected CountDown countDown = new CountDown(2f);
    private LinkedList<SimpleCommand> notificationQueue = new LinkedList<SimpleCommand>();

    public TempoBattlefield2DRenderer(Battlefield battlefield, AssetManager asm) {
        super(battlefield);
        this.unitRenderers = new Array<BattleUnitRenderer>();
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

        // addExpGained up area renderers
        this.areaRenderers = new Array<AreaRenderer>();
        for(Data.Allegeance a : Data.Allegeance.values()){
            for (int i = 0; i < getModel().getGuardedAreas().get(a).size; i++) {
                addaAreaRenderer(getModel().getGuardedAreas().get(a).get(i));
            }
        }
    }


    @Override
    public void renderTiles(SpriteBatch batch) {
        for (int r = 0; r < getModel().getNbRows(); r++) {
            for (int c = 0; c < getModel().getNbColumns(); c++) {
                if (getModel().isTileExisted(r, c)) {
                    batch.draw(tileRenderers[r][c], c, r, 1, 1);
                }
            }
        }


    }

    @Override
    public void renderAreas(SpriteBatch batch) {
        for(int i = 0; i < areaRenderers.size; i++) {
            areaRenderers.get(i).render(batch);
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
        countDown.update(dt);
        if(!countDown.isRunning() && !notificationQueue.isEmpty()){
            if(countDown.isFinished())
                countDown.reset();
            notificationQueue.pop().apply();
        }

        for(int i = 0; i < unitRenderers.size; i++) {
            unitRenderers.get(i).update(dt);
        }
    }

    private void addTileRenderer(int r, int c, Data.TileType tileType){
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

    public void addUnitRenderer(int r, int c, IUnit unit) {
        if(!isUnitRendererCreated(unit)) {
            unitRenderers.add(new TempoUnitRenderer(r, c, unit));
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
        if (data instanceof IUnit) {

            // remove the sent unit
            removeUnitRenderer((IUnit) data);
        }  else if(data instanceof SetUnit){

            final SetUnit notif = (SetUnit)data;
            if (isUnitRendererCreated(notif.unitModel)) {
                final BattleUnitRenderer bur = getUnitRenderer(notif.unitModel);
                bur.getNotification(new SimpleCommand() {

                    @Override
                    public void apply() {
                        bur.setX(notif.col);
                        bur.setY(notif.row);
                        removeAreaRenderersAssociatedWith(bur.getModel());
                    }
                });
            }else{
                addUnitRenderer(notif.row, notif.col, notif.unitModel);
            }
        } else if (data instanceof SwitchPosition) {

            final SwitchPosition notif = (SwitchPosition)data;
            final BattleUnitRenderer bur1 = getUnitRenderer(notif.unit1);
            final BattleUnitRenderer bur2 = getUnitRenderer(notif.unit2);


            if(bur1 != null && bur2 != null) {

                bur1.getNotification(new SimpleCommand() {

                    @Override
                    public void apply() {
                        removeAreaRenderersAssociatedWith(bur1.getModel());
                        Array<int[]> path = new Array<int[]>();
                        path.add(new int[]{notif.rowUnit2, notif.colUnit2});
                        bur1.displayWalk(path);
                    }
                });
                bur2.getNotification(new SimpleCommand() {

                    @Override
                    public void apply() {
                        removeAreaRenderersAssociatedWith(bur2.getModel());
                        Array<int[]> path = new Array<int[]>();
                        path.add(new int[]{notif.rowUnit1, notif.colUnit1});
                        bur2.displayWalk(path);
                    }
                });
            }
        }else if(data instanceof Walk){

            final Walk notif = (Walk)data;
            final BattleUnitRenderer bur = getUnitRenderer(notif.unit);
            bur.getNotification(new SimpleCommand() {
                @Override
                public void apply() {
                    bur.displayWalk(notif.path);
                    removeAreaRenderersAssociatedWith(bur.getModel());
                }
            });
        }else if(data instanceof SetTile){

            if(data instanceof Build){

                final Build notif= (Build)data;
                notificationQueue.offer(new SimpleCommand() {
                    @Override
                    public void apply() {
                        countDown.run();
                        TextureRegion tr = TempoSpritePool.getInstance().getBuildInConstructionSprite(notif.tileType);
                        if(tr != null)
                            tileRenderers [notif.row][notif.col] = tr;
                        getUnitRenderer(notif.builder).getNotification(Data.AnimationId.BUILD);
                    }
                });
                notificationQueue.offer(new SimpleCommand() {
                    @Override
                    public void apply() {
                        addTileRenderer(notif.row, notif.col, notif.tileType);
                    }
                });
            }else {

                SetTile notif = (SetTile)data;
                addTileRenderer(notif.row, notif.col, notif.tileType);
            }
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

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public void setVisible(boolean visible) {

    }


    public static class BFRendererException extends Exception{
        public BFRendererException(String msg){
            super(msg);
        }
    }
}
