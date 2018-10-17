package com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.engine.CameraManager;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.rendering.Renderer;

import java.util.LinkedList;

public abstract class BattlefieldRenderer extends Renderer<Battlefield> {

    private LinkedList<SimpleCommand> notificationQueue;
    protected Data.Weather renderedWeather;

    public BattlefieldRenderer(Battlefield model) {

        super(model);
        this.notificationQueue = new LinkedList<SimpleCommand>();
    }

    public abstract void updateAreaRenderers(float dt);
    public abstract void prerender();
    public abstract void render(SpriteBatch batch);

    public abstract int getRow(float gameX, float gameY);
    public abstract int getCol(float gameX, float gameY);
    public abstract float getCenterX(int row, int col);
    public abstract float getCenterY(int row, int col);
    public abstract void setGameCamParameters(CameraManager cameraManager);

    public abstract BattleUnitRenderer getUnitRenderer(IUnit model);
    public abstract AreaRenderer getAreaRenderer(Area area);
    public abstract void addTileRenderer(int row, int col, Data.TileType type);
    public abstract void addUnitRenderer(int row, int col, IUnit model);
    public abstract void addAreaRenderer(Area area);
    public abstract void removeUnitRenderer(IUnit model);
    public abstract void removeAreaRenderer(Area model);
    protected abstract boolean isUnitRendererCreated(IUnit model);
    protected abstract boolean isAreaRendererCreated(Area model);
    protected abstract void removeAreaRenderersAssociatedWith(IUnit model);


    protected abstract boolean isCurrentTaskCompleted();
    protected abstract void setBuildTask(Notification.Build build);


    public void displayDeploymentAreas(boolean visible) {
        Array<Area> deploymentArea = getModel().getDeploymentAreas();
        for(int i = 0; i< deploymentArea.size; i++){
            getAreaRenderer(deploymentArea.get(i)).setVisible(visible);
        }
    }

    public final void offerTask(SimpleCommand command){
        if(command != null)
            notificationQueue.offer(command);
    }

    @Override
    public void update(float dt) {
        if(!isCurrentTaskCompleted() && ! notificationQueue.isEmpty()){
            notificationQueue.pop().apply();
        }
        updateAreaRenderers(dt);
    }


    @Override
    public final void getNotification(Observable sender, Object data) {
        if (data instanceof IUnit) {

            removeUnitRenderer((IUnit) data);
        } else if(data instanceof Data.Weather){

            this.renderedWeather = (Data.Weather) data;
        } else if(data instanceof Notification.SetTile){

            if(data instanceof Notification.Build){

                final Notification.Build notif= (Notification.Build)data;
                setBuildTask(notif);
            }else {

                Notification.SetTile notif = (Notification.SetTile)data;
                addTileRenderer(notif.row, notif.col, notif.tileType);
            }
        }else if(data instanceof Notification.SetUnit){

            final Notification.SetUnit notif = (Notification.SetUnit)data;
            if (isUnitRendererCreated(notif.unitModel)) {
                final BattleUnitRenderer bur = getUnitRenderer(notif.unitModel);
                notif.unitModel.notifyAllObservers(new SimpleCommand() {

                    @Override
                    public void apply() {
                        bur.setPos(notif.row, notif.col);
                        removeAreaRenderersAssociatedWith(bur.getModel());
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
                    bur.displayWalk(notif.path, false);
                    removeAreaRenderersAssociatedWith(bur.getModel());
                }
            });
        }else if(data instanceof Area){

            Area area = (Area)data;
            if(isAreaRendererCreated(area)){
                removeAreaRenderer(area);
            }else{
                addAreaRenderer(area);
            }
        }
    }


    public static class BFRendererException extends Exception{
        public BFRendererException(String msg){
            super(msg);
        }
    }
}
