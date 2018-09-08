package com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.engine.GameElement;
import com.lawsgame.emishitactics.engine.patterns.command.Command;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;
import com.lawsgame.emishitactics.engine.rendering.Renderer;

import java.util.LinkedList;

public abstract class BattleUnitRenderer extends Renderer<IUnit> implements GameElement {

    private LinkedList<Object> notificationQueue;

    public BattleUnitRenderer(IUnit model) {
        super(model);
        this.notificationQueue = new LinkedList<Object>();
    }

    protected void launchNextAnimation(){
        if(!isAnimationRolling()){
            if(!notificationQueue.isEmpty()) {
                Object query = notificationQueue.pop();

                if (query instanceof Notification.ApplyDamage) {
                    Notification.ApplyDamage notification = (Notification.ApplyDamage) query;
                    displayTakeHit(notification.moralOnly, notification.damageTaken, notification.critical, notification.backstab);
                } else if(query instanceof Notification.Done) {
                    setDone(((Notification.Done) query).done);
                } else if(query instanceof Notification.Blink) {
                    setTargeted(((Notification.Blink) query).targeted);
                } else if(query instanceof Integer) {
                    displayTreated((Integer)query);
                }  else if (query instanceof Data.AnimId) {
                    display((Data.AnimId) query);
                } else if (query instanceof Notification.Pushed) {
                    Notification.Pushed notif = (Notification.Pushed)query;
                    displayPushed(notif.orientation);
                } else if (query instanceof Notification.Fled) {
                    Notification.Fled notif = (Notification.Fled)query;
                    displayFlee(notif.orientation);
                }else if (query instanceof Data.Orientation) {
                    setOrientation((Data.Orientation)query);
                } else if (query instanceof Command) {
                    Command customQuery = (Command) query;
                    customQuery.apply();
                } else if (query instanceof Array) {
                    if (((Array) query).size > 0 && ((Array) query).get(0) instanceof int[]) {
                        Array<int[]> path = (Array<int[]>) query;
                        displayWalk(path);
                    }
                } else if(query instanceof Data.WeaponType){
                    setWeaponType((Data.WeaponType)query);
                } else if (query instanceof Notification.Horseman){
                    setHorseman(((Notification.Horseman)query).horseman);
                } else if (query instanceof Notification.Visible){
                    setVisible(((Notification.Visible)query).visible);
                }else {
                    launchNextAnimation();
                }

            }
        }
    }

    @Override
    public boolean isExecuting() {
        return isAnimationRolling() || notificationQueue.size() > 0;
    }

    public abstract void setX(float x);
    public abstract void setY(float y);
    public abstract float getX();
    public abstract float getY();
    public abstract void setDone(boolean done);
    public abstract void setTargeted(boolean targeted);
    public abstract boolean isAnimationRolling();

    public abstract void displayWalk(Array<int[]> path);
    public abstract void displayTakeHit(boolean moralOnly, int damageTaken, boolean critical, boolean backstab);
    public abstract void displayTreated(int healedHP);
    public abstract void displayPushed(Data.Orientation pushedTowards);
    public abstract void displayFlee(Data.Orientation fleeingDirection);
    public abstract void display(Data.AnimId id);
    public abstract void setOrientation(Data.Orientation or);
    public abstract void setWeaponType(Data.WeaponType type);
    public abstract void setHorseman(boolean horseman);


    @Override
    public void update(float dt){
        // shall be called last to directly launched the next animation if isAnimationRolling = false
        launchNextAnimation();
    }

    /**
     * treat the input data and push animationQueries in the animaition Queue, ready to be rendered
     * @param data
     */
    @Override
    public void getNotification(final Object data){
        notificationQueue.offer(data);
        if(data instanceof Notification.ApplyDamage){
            Notification.ApplyDamage notif = (Notification.ApplyDamage)data;
            if(notif.state == Notification.ApplyDamage.State.DIED) notificationQueue.offer(Data.AnimId.DIE);
            if(notif.state == Notification.ApplyDamage.State.FLED) notificationQueue.offer(Notification.Fled.get(notif.fleeingOrientation));
            if(notif.state != Notification.ApplyDamage.State.WOUNDED){
                notificationQueue.offer(new SimpleCommand() {
                    @Override
                    public void apply() {
                        setVisible(false);
                    }
                });
            }
        }
    }
}
