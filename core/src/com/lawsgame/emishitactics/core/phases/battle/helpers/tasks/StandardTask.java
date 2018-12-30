package com.lawsgame.emishitactics.core.phases.battle.helpers.tasks;


import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.rendering.Renderer;
import com.lawsgame.emishitactics.engine.timers.CountDown;

import java.util.LinkedList;

public class StandardTask extends Task {
    protected Array<SubTask> parallelSubTasks;
    protected boolean initiazed = false;
    protected String tag = "";


    public StandardTask(){

        this.parallelSubTasks = new Array<SubTask>();
        this.initiazed = false;
        this.tag = "";
    }


    public StandardTask(Renderer renderer, Object dataBundle){
        this();
        RendererSubTaskQueue rendererThread = new RendererSubTaskQueue(renderer);
        rendererThread.addQuery(dataBundle);
        parallelSubTasks.add(rendererThread);
    }

    public StandardTask(Observable sender, Renderer executer, Object dataBundle){
        this();
        RendererSubTaskQueue rendererThread = new RendererSubTaskQueue(executer);
        rendererThread.addQuery(sender, dataBundle);
        parallelSubTasks.add(rendererThread);
    }

    public StandardTask(final SimpleCommand command, float delay){
        this();
        addParallelSubTask(new CommandSubTask(delay){

            @Override
            public void run() {
                command.apply();
            }
        });
    }



    // -------------- METHODS ----------------------

    public void update(float dt){
        for(int i = 0; i < parallelSubTasks.size; i++){
            parallelSubTasks.get(i).update(dt);
        }
    }

    public boolean isCompleted() {
        for(int i = 0; i < parallelSubTasks.size; i++){
            if(!parallelSubTasks.get(i).isCompleted()){
                return false;
            }
        }
        return true;
    }

    public void init() {
        initiazed = true;
        for(int i = 0; i < parallelSubTasks.size; i++){
            parallelSubTasks.get(i).init();
        }
    }

    public void addParallelSubTask(SubTask subTask){
        parallelSubTasks.add(subTask);
    }

    public <T extends SubTask> void addllThreads(Array<T> threads){
        parallelSubTasks.addAll(threads);
    }


    public boolean isInitiazed() {
        return initiazed;
    }

    public String toString(){
        String str = "STANDARD TASK ";
        if(!getTag().equals("")) str += "["+getTag()+"] ";
        str += ": ";
        for(int i = 0; i < parallelSubTasks.size; i++) {
            str +="\n   "+ parallelSubTasks.get(i).toString();
        }
        return str;
    }

    /**
     * convenient method
     * usefull to perform if adding that task to the queue is relevant.
     * @return
     */
    @Override
    public boolean isIrrelevant() {
        for(int i = 0; i < parallelSubTasks.size; i++){
            if(!parallelSubTasks.get(i).isEmpty()){
                return false;
            }
        }
        return true;
    }

    /**
     *
     * this function allows to merge two StandardTask
     * there is two type of merging operations:
     *  - horizontal one, by adding sub tasks
     *  - vertizontal one, by update an already existing tasks
     *
     *  the merging can occur only if :
     *  0) {@link this} is empty;
     *  1) both tasks only possess one {@link RendererSubTaskQueue} with the same renderer as target
     *  2) both tasks has multiple {@link RendererSubTaskQueue} with no renderer in common, the subtask of param task are then adding tp those the task the method is called upon.
     *
     * @param task : task to merge with, by default this task is considered to be occuring after the one on which merge() have been called
     * @return true if the merging is been success
     */
    @Override
    public Task merge(Task task) {
        if (task instanceof StandardTask && !initiazed && !((StandardTask) task).initiazed) {
            StandardTask standardTask = (StandardTask) task;
            // CASE 1 : task content is wholy merged into the empty this
            if (this.isIrrelevant()) {
                this.parallelSubTasks.clear();
                this.addllThreads(standardTask.parallelSubTasks);
                standardTask.parallelSubTasks.clear();
                return this;
            }
            // CASE 2
            RendererSubTaskQueue thisQueue;
            RendererSubTaskQueue taskQueue;
            if(this.parallelSubTasks.size == 1
                    && standardTask.parallelSubTasks.size == 1
                    && this.parallelSubTasks.get(0) instanceof  RendererSubTaskQueue
                    && this.parallelSubTasks.get(0) instanceof  RendererSubTaskQueue){
                thisQueue = (RendererSubTaskQueue)this.parallelSubTasks.get(0);
                taskQueue = (RendererSubTaskQueue) standardTask.parallelSubTasks.get(0);
                if(thisQueue.executer == taskQueue.executer){
                    thisQueue.senders.addAll(taskQueue.senders);
                    thisQueue.bundles.addAll(taskQueue.bundles);
                    taskQueue.bundles.clear();
                    taskQueue.senders.clear();
                    return this;
                }
            }
            // CASE 3
            boolean noExecuterInCommon = true;
            for (int i = 0; i < parallelSubTasks.size; i++) {
                for (int j = 0; j < standardTask.parallelSubTasks.size; j++) {
                    if (parallelSubTasks.get(i) instanceof RendererSubTaskQueue
                            && standardTask.parallelSubTasks.get(j) instanceof RendererSubTaskQueue){
                        thisQueue = (RendererSubTaskQueue)this.parallelSubTasks.get(i);
                        taskQueue = (RendererSubTaskQueue) standardTask.parallelSubTasks.get(j);
                        if(thisQueue.executer == taskQueue.executer){
                            noExecuterInCommon = false;
                            break;
                        }
                    }else{
                        noExecuterInCommon = false;
                        break;
                    }
                }
            }
            if(noExecuterInCommon){
                this.addllThreads(standardTask.parallelSubTasks);
                standardTask.parallelSubTasks.clear();
                return this;
            }
        }
        return this;
    }

    public int getNumberOfSubTasks(){
        return parallelSubTasks.size;
    }


    public void tag(String tag){
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }









    public static abstract class SubTask {
        private String tag = "";

        abstract void init();
        abstract boolean isCompleted();
        abstract boolean isEmpty();
        abstract void update(float dt);


        public void setTag(String tag){
            this.tag = tag;
        }
        public String getTag(){ return this.tag; }
    }







    //------------------- THREAD IMP --------------------------------

    public static abstract class CommandSubTask extends SubTask {
        CountDown countDown;
        private boolean completed = false;

        public CommandSubTask(float delay){
            countDown = new CountDown(delay);
        }

        public abstract void run();

        @Override
        void init() {
            countDown.run();
        }

        @Override
        boolean isCompleted() {
            return completed;
        }

        @Override
        boolean isEmpty() {
            return false;
        }

        @Override
        void update(float dt) {
            countDown.update(dt);
            if(countDown.isFinished() && !completed){
                completed = true;
                run();
            }
        }

        @Override
        public String toString(){
            return "   Command thread ["+getTag()+"]";
        }
    }












    public static class RendererSubTaskQueue extends SubTask {
        protected Renderer executer;
        protected boolean bundlesSent;
        protected LinkedList<Observable> senders;
        protected LinkedList<Object> bundles;
        protected CountDown countDown;


        public RendererSubTaskQueue(Renderer executer, float delay){
            this.bundlesSent = false;
            this.executer = executer;
            this.countDown = new CountDown(delay);
            this.bundles = new LinkedList<Object>();
            this.senders = new LinkedList<Observable>();

        }

        public RendererSubTaskQueue(Renderer executer){
            this(executer, 0);

        }

        public RendererSubTaskQueue(Renderer executer, Observable sender, Object dataBundle){
            this(executer);
            addQuery(sender, dataBundle);
        }

        public RendererSubTaskQueue(Renderer renderer, Object dataBundle){
            this(renderer);
            addQuery(renderer.getModel(), dataBundle);
        }

        void init() {
            countDown.run();
        }

        boolean isCompleted() {
            return bundlesSent && !executer.isExecuting();
        }

        @Override
        boolean isEmpty() {
            return bundles.size() == 0;
        }

        public void addQuery(Observable receiver, Object dataBundle){
            senders.offer(receiver);
            bundles.offer(dataBundle);
        }

        public void addQuery(Object dataBundle){
            senders.offer(executer.getModel());
            bundles.offer(dataBundle);
        }

        void update(float dt) {
            countDown.update(dt);
            if(countDown.isFinished()){
                countDown.reset();
                bundlesSent = true;
                while(senders.size() > 0) {
                    senders.pop().notifyAllObservers(bundles.pop());
                }
            }
        }

        @Override
        public String toString() {
            String str = "   RendererSubTaskQueue : executer = "+((executer != null) ? executer.toString(): "null");
            for (int j = 0; j < bundles.size(); j++) {
                str += "\n        Notif => sender : " + bundles.get(j) + " => " + senders.get(j);
            }
            return str;
        }
    }








    public static class DelaySubTask extends SubTask {
        protected CountDown countDown;

        public DelaySubTask(float delay){
            countDown = new CountDown(delay);
        }

        @Override
        void init() {
            countDown.run();
        }

        @Override
        boolean isCompleted() {
            return countDown.isFinished();
        }

        @Override
        boolean isEmpty() {
            return countDown.getDelay() == 0;
        }

        @Override
        void update(float dt) {
            countDown.update(dt);
        }

        @Override
        public String toString() {
            return "   DelaySubTask : duration = "+countDown.getDelay();
        }
    }
}
