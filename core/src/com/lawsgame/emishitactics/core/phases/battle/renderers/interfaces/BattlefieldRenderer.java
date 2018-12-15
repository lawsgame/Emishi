package com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Tile;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.event.TrapEvent;
import com.lawsgame.emishitactics.engine.CameraManager;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.rendering.Renderer;

import java.util.LinkedList;

public abstract class BattlefieldRenderer extends Renderer<Battlefield> {

    private LinkedList<Object> notificationQueue;
    protected Data.Weather renderedWeather;
    protected CameraManager gcm;

    public BattlefieldRenderer(Battlefield model, CameraManager gcm) {

        super(model);
        this.notificationQueue = new LinkedList<Object>();
        this.gcm = gcm;
        if(gcm != null) {
            setGameCamParameters();
        }
    }

    protected void launchNextAnimation(){

        if(isIdling() && notificationQueue.size() > 0) {
            Object data = notificationQueue.pop();

            if (data instanceof Unit) {

                removeUnitRenderer((Unit) data, false);
            } else if(data instanceof Data.Weather){

                this.renderedWeather = (Data.Weather) data;
            } else if(data instanceof Notification.SetTile){

                Notification.SetTile notif = (Notification.SetTile)data;
                switch (notif.type){
                    case PLUNDERED:
                        displayPlundering(notif.row, notif.col, notif.newTile, notif.oldTile);
                        break;
                    case COLLAPSED:
                        displayCollapsing(notif.row, notif.col, notif.newTile, notif.oldTile);
                        break;
                    case BUILT:
                        displayBuilding(notif.row, notif.col, notif.newTile);
                        break;
                    case INSTANTANEOUSLY:
                        addTileRenderer(notif.row, notif.col, notif.newTile);
                        break;
                }
            }else if(data instanceof Notification.SetUnit){

                final Notification.SetUnit notif = (Notification.SetUnit)data;
                if (isUnitRendererCreated(notif.unitModel)) {
                    final BattleUnitRenderer bur = getUnitRenderer(notif.unitModel);
                    notif.unitModel.notifyAllObservers(new SimpleCommand() {

                        @Override
                        public void apply() {
                            bur.setPos(notif.row, notif.col);
                            removeAreaRenderersAssociatedWith(bur.getModel(), true);
                        }
                    });
                }else{
                    addUnitRenderer(notif.row, notif.col, notif.unitModel);
                }
            } else if(data instanceof Notification.Walk){

                final Notification.Walk notif = (Notification.Walk)data;
                final BattleUnitRenderer bur = getUnitRenderer(notif.unit);
                notif.unit.notifyAllObservers(new SimpleCommand() {
                    @Override
                    public void apply() {
                        bur.displayWalk(notif.path, notif.reveal);
                        removeAreaRenderersAssociatedWith(bur.getModel(), true);
                    }
                });
            }else if(data instanceof Area){

                Area area = (Area)data;
                if(isAreaRendererCreated(area)){
                    removeAreaRenderer(area);
                }else{
                    addAreaRenderer(area);
                }
            }else if(data instanceof Notification.igniteSparkles){
                Notification.igniteSparkles ignite = (Notification.igniteSparkles)data;
                TileRenderer tileRenderer;
                for (int i = 0; i < ignite.tiles.size; i++) {
                    if (i < ignite.sparkleType.size) {
                        tileRenderer = getTileRenderer(ignite.tiles.get(i)[0], ignite.tiles.get(i)[1]);
                        tileRenderer.setRevealed(true, ignite.sparkleType.get(i));
                    }
                }
            }else if(data instanceof Notification.ExtinguishSparkle){
                Notification.ExtinguishSparkle extinguishSparkle = (Notification.ExtinguishSparkle) data;
                getTileRenderer(extinguishSparkle.rowTile, extinguishSparkle.colTile).setRevealed(false, null);
            }
            launchNextAnimation();
        }
    }

    public abstract boolean isIdling();
    protected abstract void displayBuilding(int rowTile, int colTile, Tile buildingTile);
    protected abstract void displayCollapsing(int rowTile, int colTile, Tile buildingTile, Tile oldbuildingTile);
    protected abstract void displayPlundering(int rowTile, int colTile, Tile buildingTile, Tile oldbuildingTile);

    @Override
    public boolean isExecuting() {
        return !isIdling() || notificationQueue.size() > 0;
    }


    public abstract void prerender();
    public abstract void render(SpriteBatch batch);

    public abstract int getRow(float gameX, float gameY);
    public abstract int getCol(float gameX, float gameY);
    public abstract float getCenterX(int row, int col);
    public abstract float getCenterY(int row, int col);
    public abstract BattleUnitRenderer getUnitRenderer(Unit model);
    public abstract AreaRenderer getAreaRenderer(Area area);
    public abstract TileRenderer getTileRenderer(int row, int col);
    public abstract void addTileRenderer(int row, int col, Tile model);
    public abstract BattleUnitRenderer addUnitRenderer(int row, int col, Unit model);
    public abstract void addAreaRenderer(Area area);
    public abstract void removeUnitRenderer(Unit model, boolean uponMoving);
    public abstract void removeAreaRenderer(Area model);
    protected abstract boolean isUnitRendererCreated(Unit model);
    protected abstract boolean isAreaRendererCreated(Area model);
    protected abstract void removeAreaRenderersAssociatedWith(Unit model, boolean uponMoving);
    public abstract Data.Orientation getOrientationFromPos(float xCenter, float yCenter, float xTarget, float yTarget);

    public void displayDeploymentAreas(boolean visible) {
        Array<Area> deploymentArea = getModel().getDeploymentAreas();
        for(int i = 0; i< deploymentArea.size; i++){
            getAreaRenderer(deploymentArea.get(i)).setVisible(visible);
        }
    }

    public void displayAllTraps(){
        TileRenderer tr;
        for(int r = 0; r < getModel().getNbRows(); r++){
            for(int c = 0; c  < getModel().getNbColumns(); c++){
                tr = getTileRenderer(r,c);
                if(tr != null && tr.getModel().searchForEventType(TrapEvent.class)){
                    tr.setRevealed(true, Data.SparkleType.TRAP);
                    tr.getModel().setRevealed(true);
                }
            }
        }
    }


    @Override
    public final void getNotification(Observable sender, Object data) {
        notificationQueue.add(data);
        launchNextAnimation();
    }

    public String toLongShort(){
        return toString();
    }



    //------------------ CAMERA MANAGEMENT -----------------------------

    public abstract void setGameCamParameters();

    public void moveCameraTo(int rowTarget, int colTarget, boolean smoothly){
        gcm.moveTo(getCenterX(rowTarget, colTarget), getCenterY(rowTarget, colTarget) , smoothly);
    }

    public CameraManager getGCM(){
        return gcm;
    }




    //----------------- EXCEPTION DECLARATION ---------------------------

    public static class BFRendererException extends Exception{
        public BFRendererException(String msg){
            super(msg);
        }
    }


}
