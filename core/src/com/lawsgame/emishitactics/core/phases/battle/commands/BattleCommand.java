package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;

public abstract class BattleCommand extends Observable implements Observer {
    protected final BattlefieldRenderer bfr;

    protected final AnimationScheduler scheduler;
    private boolean decoupled;
    private Array<Task> renderTasks;                // ids which allows to certify that the rendering of the command is bundlesSent / completed AND usefull for decoupling view and model updates
    private boolean tasksScheduled;


    public BattleCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler){
        this.bfr = bfr;
        this.scheduler = scheduler;
        this.renderTasks = new Array<Task>();
        this.tasksScheduled = false;
        this.decoupled = false;
    }

    public boolean apply() {
        if(isApplicable()) {
            execute();
            return true;
        }
        return false;
    }

    public boolean undo(){
        if(isUndoable()){
            unexecute();
            return true;
        }
        return false;
    }

    protected abstract void execute();
    protected abstract void unexecute();
    public abstract boolean isApplicable();
    public abstract boolean isUndoable();

    public final boolean isExecuting(){
        return tasksScheduled && renderTasks.size > 0;
    }

    protected final void scheduleRenderTask(Task task){
        if(!task.isIrrelevant()) {
            renderTasks.add(task);
            if(!decoupled) {
                this.tasksScheduled = true;
                task.attach(this);
                scheduler.addTask(task);
            }
        }
    }

    protected final void scheduleMultipleRenderTasks(Array<Task> tasks){
        for(int i = 0; i < tasks.size; i++)
            scheduleRenderTask(tasks.get(i));
    }

    public final boolean pushRenderTasks(){

        if(decoupled && !tasksScheduled && renderTasks.size > 0) {

            this.tasksScheduled = true;
            for (int i = 0; i < renderTasks.size; i++) {
                renderTasks.get(i).attach(this);
                scheduler.addTask(renderTasks.get(i));
            }
            return true;
        }
        return false;
    }

    public final Array<Task> confiscateTasks(){
        Array<Task> tasks = renderTasks;
        renderTasks = new Array<Task>();
        return tasks;
    }

    @Override
    public void getNotification(Observable sender, Object data) {



        if (data instanceof Task) {

            Task completedTask = (Task) data;
            completedTask.detach(this);
            renderTasks.removeValue(completedTask, true);

            // notify that the command is done : the model is updated AND the render tasks are completed
            if (renderTasks.size == 0) {
                notifyAllObservers(this);
                tasksScheduled = false;
            }
        }
    }


    protected boolean isAnyEventTriggerable(Object data, Array<int[]> area) {
        Battlefield bf = bfr.getModel();
        boolean eventTrig = false;

        if(bf.isAnyEventTriggerable(data)){
            return true;
        }

        for(int i = 0; i < bf.armyTurnOrder.size(); i++){
            if(bf.armyTurnOrder.get(i).isAnyEventTriggerable(data)) {
                return true;
            }
        }

        for(int i = 0; i< area.size; i++){
            eventTrig = isAnyEventTriggerable(data, area.get(i)[0], area.get(i)[1]);
        }

        return eventTrig;
    }


    protected boolean isAnyEventTriggerable(Object data, int row, int col){
        Battlefield bf = bfr.getModel();

        if(bf.isTileExisted(row, col)) {
            // check unit
            if(bf.isTileOccupied(row, col)){
                if(bf.getUnit(row, col).isAnyEventTriggerable(data)) {
                    return true;
                }
            }

            // check tile
            if(bf.getTile(row, col).isAnyEventTriggerable(data)) {
                return true;
            }

            // check areas
            for (int j = 0; j < bf.getUnitAreas().size; j++) {
                if (Utils.arrayContains(bf.getUnitAreas().get(j).getTiles(), row, col)
                        && bf.getUnitAreas().get(j).isAnyEventTriggerable(data)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * handle triggered events within the given area in this order:
     *  1) battlefield related
     *  2) army related
     *  THEN
     *  3.1) unit related
     *  3.2) tile related
     *  3.3) area related
     *
     *
     *
     * @param data : notif to give to the triggers
     * @param area : tiles to check for event to trigger
     * @return if a event has been triggered
     */
    protected void handleEvents(Object data, Array<int[]> area) {

        scheduleMultipleRenderTasks(bfr.getModel().performEvents(data));

        for(int i = 0; i < bfr.getModel().armyTurnOrder.size(); i++)
            scheduleMultipleRenderTasks(bfr.getModel().armyTurnOrder.get(i).performEvents(data));

        for(int i = 0; i< area.size; i++)
            handleEvents(data, area.get(i)[0], area.get(i)[1]);
    }


    protected void handleEvents(Object data, int row, int col){

        if(bfr.getModel().isTileExisted(row, col)) {
            // check unit
            if(bfr.getModel().isTileOccupied(row, col)){
                scheduleMultipleRenderTasks(bfr.getModel().getUnit(row, col).performEvents(data));
            }

            // check tile
            scheduleMultipleRenderTasks(bfr.getModel().getTile(row, col).performEvents(data));


            // check areas
            for (int j = 0; j < bfr.getModel().getUnitAreas().size; j++) {
                if (Utils.arrayContains(bfr.getModel().getUnitAreas().get(j).getTiles(), row, col)) {
                    scheduleMultipleRenderTasks(bfr.getModel().getUnitAreas().get(j).performEvents(data));
                }
            }
        }
    }

    protected void removeOutOfActionUnits(){
        Array<IUnit> OOAUnits = bfr.getModel().getOOAUnits();
        bfr.getModel().removeOOAUnits(false);
        StandardTask removeOOAUnitTask = new StandardTask();
        for(int i = 0; i < OOAUnits.size; i++)
            removeOOAUnitTask.addThread(new StandardTask.RendererThread(bfr, OOAUnits.get(i)));
        removeOOAUnitTask.tag("remove OOA units");
        scheduleRenderTask(removeOOAUnitTask);
    }

    public void setDecoupled(boolean decoupled) {
        this.decoupled = decoupled;
    }

    public String toShortString(){
        return toString();
    }

}
