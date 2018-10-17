package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;

public abstract class BattleCommand extends Observable implements Observer {
    protected final BattlefieldRenderer bfr;

    protected final AnimationScheduler scheduler;
    private boolean decoupled;
    private Array<Task> renderTasks;                // ids which allows to certify that the rendering of the command is executing / completed AND usefull for decoupling view and model updates
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

    public boolean isDecoupled() {
        return decoupled;
    }

    public void setDecoupled(boolean decoupled) {
        this.decoupled = decoupled;
    }

    public String toShortString(){
        return toString();
    }

}
