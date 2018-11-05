package com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.engine.GameElement;
import com.lawsgame.emishitactics.engine.patterns.command.Command;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.rendering.Renderer;

import java.util.LinkedList;

public abstract class BattleUnitRenderer extends Renderer<IUnit> implements GameElement {

    private LinkedList<Object> notificationQueue;

    public BattleUnitRenderer(IUnit model) {
        super(model);
        this.notificationQueue = new LinkedList<Object>();
    }

    /**
     *  recurvise method which keep calling itself to iterate through the notificationQueue
     *  until the renderer is not idle any more or the notification queue gets empty.
     */

    int nb = 0;
    protected void launchNextAnimation(){

        if(isIdling() && notificationQueue.size() > 0) {

            Object query = notificationQueue.pop();
            if (query instanceof Notification.ApplyDamage) {
                Notification.ApplyDamage notification = (Notification.ApplyDamage) query;
                displayTakeHit(notification.moralOnly, notification.damageDealt, notification.critical, notification.backstab);
            } else if (query instanceof Notification.Attack) {
                displayAttack((Notification.Attack) query);
            } else if (query instanceof Notification.Done) {
                setDone(((Notification.Done) query).done);
            } else if (query instanceof Notification.Blink) {
                setTargeted(((Notification.Blink) query).targeted);
            } else if (query instanceof Integer) {
                displayTreated((Integer) query);
            } else if (query instanceof Data.AnimId) {
                display((Data.AnimId) query);
            } else if (query instanceof Notification.Pushed) {
                Notification.Pushed notif = (Notification.Pushed) query;
                displayPushed(notif.orientation);
            } else if (query instanceof Notification.Walk) {
                Notification.Walk notif = (Notification.Walk) query;
                displayWalk(notif.path, notif.reveal);
            } else if (query instanceof Notification.Fled) {
                Notification.Fled notif = (Notification.Fled) query;
                displayFlee(notif.orientation);
            } else if (query instanceof Data.Orientation) {
                setOrientation((Data.Orientation) query);
            } else if (query instanceof Array) {
                if (((Array) query).size > 0 && ((Array) query).get(0) instanceof int[]) {
                    Array<int[]> path = (Array<int[]>) query;
                    displayWalk(path, false);
                }
            } else if (query instanceof Data.WeaponType) {
                setWeaponType((Data.WeaponType) query);
            } else if (query instanceof Notification.Horseman) {
                setHorseman(((Notification.Horseman) query).horseman);
            } else if (query instanceof Notification.Visible) {
                setVisible(((Notification.Visible) query).visible);
            } else if (query instanceof Notification.Disabled) {
                setDisabled(((Notification.Disabled) query).disabled);
            } else if (query instanceof Notification.Crippled) {
                setCrippled(((Notification.Crippled) query).crippled);
            } else if (query instanceof Command) {
                Command customQuery = (Command) query;
                customQuery.apply();
            }


            launchNextAnimation();
        }
        nb = 0;
    }


    @Override
    public boolean isExecuting() {
        return !isIdling() || notificationQueue.size() > 0;
    }

    public abstract void setPos(int row, int col);
    public abstract float getCenterX();
    public abstract float getCenterY();
    public abstract void setDone(boolean done);
    public abstract void setTargeted(boolean targeted);
    public abstract void setDisabled(boolean disabled);
    public abstract void setCrippled(boolean crippled);
    public abstract void setOrientation(Data.Orientation or);
    public abstract void setWeaponType(Data.WeaponType type);
    public abstract void setHorseman(boolean horseman);
    public abstract boolean isIdling();

    public abstract void displayWalk(Array<int[]> path, boolean reinform);
    public abstract void displayAttack(Notification.Attack query);
    public abstract void displayTakeHit(boolean moralOnly, int damageTaken, boolean critical, boolean backstab);
    public abstract void displayTreated(int healedHP);
    public abstract void displayPushed(Data.Orientation pushedTowards);
    public abstract void displayFlee(Data.Orientation fleeingDirection);
    public abstract void display(Data.AnimId id);


    /**
     * treat the input data and push animationQueries in the animaition Queue, ready to be rendered
     * then launch the next animation IF the renderer is idled
     * @param data
     */
    public void getNotification(Observable sender, final Object data){
        if(data != null) {
            notificationQueue.offer(data);
            if (data instanceof Notification.ApplyDamage) {
                Notification.ApplyDamage notif = (Notification.ApplyDamage) data;
                if (notif.state == Notification.ApplyDamage.State.DIED)
                    notificationQueue.offer(Data.AnimId.DIE);
                if (notif.state == Notification.ApplyDamage.State.FLED)
                    notificationQueue.offer(Notification.Fled.get(notif.fleeingOrientation));
            }
            launchNextAnimation();
        }
    }
}
