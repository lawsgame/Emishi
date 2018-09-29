package com.lawsgame.emishitactics.core.phases.battle.commands.interfaces;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.patterns.command.Command;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;

public abstract class BattleCommand extends Observable implements Command, Observer {
    protected final Battlefield battlefield;
    protected final BattlefieldRenderer bfr;

    private final AnimationScheduler scheduler;
    private boolean decoupled;
    private boolean launched;
    private Array<Task> renderTasks;                // ids which allows to certify that the rendering of the command is executing / completed AND usefull for decoupling view and model updates
    private boolean tasksScheduled;


    public BattleCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler){
        this.bfr = bfr;
        this.battlefield = bfr.getModel();
        this.scheduler = scheduler;
        this.renderTasks = new Array<Task>();

        this.launched = false;
        this.tasksScheduled = false;
        this.decoupled = false;
    }

    @Override
    public void apply() {
        this.launched = true;
        this.tasksScheduled = false;
        this.renderTasks.clear();

        execute();
    }

    protected abstract void execute();

    public final boolean isExecuting(){
        return launched && renderTasks.size > 0;
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

    public final void pushRenderTasks(){
        if(containPushableRenderTasks()) {
            this.tasksScheduled = true;
            for (int i = 0; i < renderTasks.size; i++) {
                renderTasks.get(i).attach(this);
                scheduler.addTask(renderTasks.get(i));
            }
        }
    }

    @Override
    public void getNotification(Observable sender, Object data) {
        if (data instanceof Task) {

            Task completedTask = (Task) data;
            completedTask.detach(this);
            renderTasks.removeValue(completedTask, true);

            // notify that the command is done : the model is updated AND the render tasks are completed
            if (renderTasks.size == 0) {
                launched = false;
                notifyAllObservers(this);
            }
        }
    }

    public boolean isDecoupled() {
        return decoupled;
    }

    public void setDecoupled(boolean decoupled) {
        this.decoupled = decoupled;
    }

    public boolean containPushableRenderTasks() {
        return decoupled && !tasksScheduled && renderTasks.size > 0;
    }
}
