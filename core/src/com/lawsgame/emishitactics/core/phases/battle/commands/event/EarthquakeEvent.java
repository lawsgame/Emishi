package com.lawsgame.emishitactics.core.phases.battle.commands.event;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.interfaces.Model;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.CameraManager;
import com.lawsgame.emishitactics.engine.math.functions.VectorialFunction;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;

public class EarthquakeEvent extends BattleCommand {
    private float earthquakeDuration;
    private final CameraManager gcm;

    public EarthquakeEvent(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, CameraManager gcm, float earthquakeDuration) {
        super(bfr, scheduler, playerInventory);
        this.earthquakeDuration = earthquakeDuration;
        this.gcm = gcm;
    }


    public static EarthquakeEvent addTrigger(final BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, CameraManager gcm, final int turn){
        EarthquakeEvent event = new EarthquakeEvent(bfr, scheduler, playerInventory, gcm, Data.EARTHQUAKE_DURATION);

        Model.Trigger trigger = new Model.Trigger(true, event) {

            @Override
            public boolean isTriggerable(Object data) {
                return data instanceof Notification.BeginArmyTurn
                        & bfr.getModel().getTurn() == turn
                        && ((Notification.BeginArmyTurn) data).army.isPlayerControlled();

            }
        };
        bfr.getModel().add(trigger);
        return event;
    }



    @Override
    protected void execute() {
        StandardTask task = new StandardTask();
        Array<int[]> tilesTurnIntoRuins = new Array<int[]>();

        Unit victim;
        Notification.TakeDamage notif;
        Battlefield bf = bfr.getModel();
        for(int r = 0; r < bf.getNbRows(); r++){
            for(int c = 0; c < bf.getNbColumns(); c++){
                if(bf.isTileExisted(r, c) && bf.getTile(r, c).collapse()){

                    tilesTurnIntoRuins.add(new int[]{r, c});
                    bf.setTile(r, c, bf.getTile(r,c).getType().getDamagedType(), false);
                    task.addThread(new StandardTask.RendererThread(bfr, new Notification.SetTile(r, c, bf.getTile(r, c))));

                    if(bf.isTileOccupied(r, c)){
                        victim = bf.getUnit(r,c);
                        if(!victim.isOutOfAction()){
                            notif = victim.takeDamage(3, false, false, 1f);
                            victim.setCrippled(true);
                            notif.critical = false;
                            notif.crippled = true;
                            notif.disabled = false;
                            notif.backstab = false;
                            notif.fleeingOrientation = victim.getOrientation().getOpposite();
                            task.addThread(new StandardTask.RendererThread(bfr.getUnitRenderer(victim), notif));
                        }
                    }
                }else if(bf.isTileOccupied(r, c) && !bf.getUnit(r, c).isOutOfAction()){
                    task.addThread(new StandardTask.RendererThread(bfr.getUnitRenderer(bf.getUnit(r, c)), Data.AnimId.WOUNDED));
                }
            }
        }

        StandardTask.CommandThread commandThread = new StandardTask.CommandThread();
        commandThread.addQuery(new SimpleCommand() {
            @Override
            public void apply() {
                gcm.move(new ShakeVF(earthquakeDuration), earthquakeDuration);
            }
        }, 0f);
        commandThread.setTag("shake camera");
        task.addThread(commandThread);
        task.addThread(new StandardTask.DelayThread(earthquakeDuration));

        scheduleRenderTask(task);
        Notification.OOAReport report = removeOutOfActionUnits();
        handleEvents(report, tilesTurnIntoRuins);

    }

    @Override
    public boolean isApplicable() {
        return true;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    protected void unexecute() { }



    // ----------------- VF HELPER CLASS -------------------

    public static class ShakeVF implements VectorialFunction{
        float xZero = 0;
        float yZero = 0;
        float duration;

        public ShakeVF(float duration){
            this.duration = duration;
        }

        @Override
        public float getX(float t) {
            return xZero + 0.15f*(1f - (float)Math.exp(t - duration))* MathUtils.sin(14*t)*MathUtils.cos(60*t);
        }

        @Override
        public float getY(float t) {
            return yZero;// + MathUtils.sin(t);
        }

        @Override
        public void setTZero(float xZero, float yZero) {
            this.xZero = xZero - getX(0);
            this.yZero = yZero - getY(0);
        }
    }
}
