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
import com.lawsgame.emishitactics.engine.rendering.Renderer;

import java.util.LinkedList;

public abstract class BattlefieldRenderer extends Renderer<Battlefield> {

    private LinkedList<SimpleCommand> notificationQueue;
    protected Array<AreaRenderer> areaRenderers;
    protected Data.Weather renderedWeather;

    public BattlefieldRenderer(Battlefield model) {

        super(model);
        this.areaRenderers = new Array<AreaRenderer>();
        this.notificationQueue = new LinkedList<SimpleCommand>();
    }

    public abstract void prerender();
    public abstract void render(SpriteBatch batch);

    public abstract int getRowFrom(float gameX, float gameY);
    public abstract int getColFrom(float gameX, float gameY);
    public abstract void displayDeploymentAreas(boolean visible);
    public abstract void setGameCamParameters(CameraManager cameraManager);

    public abstract BattleUnitRenderer getUnitRenderer(IUnit model);
    public abstract AreaRenderer getAreaRenderer(Area area);
    public abstract void addTileRenderer(int row, int col, Data.TileType type);
    public abstract void addUnitRenderer(int row, int col, IUnit model);
    public abstract void addAreaRenderer(Area area);
    public abstract void removeUnitRenderer(IUnit model);
    public abstract void removeAreaRenderer(Area model);
    protected abstract boolean isUnitRendererCreated(IUnit model);
    protected abstract void removeAreaRenderersAssociatedWith(IUnit model);

    protected abstract boolean isCurrentTaskCompleted();
    protected abstract void setBuildTask(Notification.Build build);

    public final void offerTask(SimpleCommand command){
        if(command != null)
            notificationQueue.offer(command);
    }

    @Override
    public void update(float dt) {
        if(!isCurrentTaskCompleted() && ! notificationQueue.isEmpty()){
            notificationQueue.pop().apply();
        }

        for(int i = 0; i < areaRenderers.size; i++) {
            areaRenderers.get(i).update(dt);
        }
    }


    /**
     *
     *
     * @param data
     */
    @Override
    public final void getNotification(Object data) {
        if (data instanceof IUnit) {

            removeUnitRenderer((IUnit) data);
        } else if(data instanceof Data.Weather){

            this.renderedWeather = (Data.Weather) data;
        } else if(data instanceof Notification.SetUnit){

            final Notification.SetUnit notif = (Notification.SetUnit)data;
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
        } else if (data instanceof Notification.SwitchPosition) {

            final Notification.SwitchPosition notif = (Notification.SwitchPosition)data;
            final BattleUnitRenderer bur1 = getUnitRenderer(notif.unit1);
            final BattleUnitRenderer bur2 = getUnitRenderer(notif.unit2);
            if (bur1 != null && bur2 != null) {
                switch (notif.mode) {
                    case GUARDIAN:
                    case WALK :


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
                        break;
                    case INSTANT:
                        bur1.getNotification(new SimpleCommand() {

                            @Override
                            public void apply() {
                                bur1.setX(notif.colUnit2);
                                bur1.setY(notif.rowUnit2);
                                removeAreaRenderersAssociatedWith(bur1.getModel());
                            }
                        });
                        bur2.getNotification(new SimpleCommand() {

                            @Override
                            public void apply() {
                                bur2.setX(notif.colUnit1);
                                bur2.setY(notif.rowUnit1);
                                removeAreaRenderersAssociatedWith(bur2.getModel());
                            }
                        });
                        break;
                }
            }
        }else if(data instanceof Notification.Walk){

            final Notification.Walk notif = (Notification.Walk)data;
            final BattleUnitRenderer bur = getUnitRenderer(notif.unit);
            bur.getNotification(new SimpleCommand() {
                @Override
                public void apply() {
                    bur.displayWalk(notif.path);
                    removeAreaRenderersAssociatedWith(bur.getModel());
                }
            });
        }else if(data instanceof Notification.SetTile){

            if(data instanceof Notification.Build){

                final Notification.Build notif= (Notification.Build)data;
                setBuildTask(notif);
            }else {

                Notification.SetTile notif = (Notification.SetTile)data;
                addTileRenderer(notif.row, notif.col, notif.tileType);
            }
        }else if(data instanceof Area){

            Area area = (Area)data;
            boolean areaRemoved = false;
            for(int i = 0; i < areaRenderers.size; i++){
                if(area == areaRenderers.get(i).getModel()){
                    removeAreaRenderer(area);
                    areaRemoved = true;
                    break;
                }
            }
            if(!areaRemoved){
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
