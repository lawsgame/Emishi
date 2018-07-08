package com.lawsgame.emishitactics.core.helpers;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.GameUpdatableEntity;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.renderers.Renderer;

import java.util.LinkedList;

public class AnimationScheduler implements GameUpdatableEntity{

    /**
     * Stack the animation to perform in order of execution.
     * Once a task is finished, if another awaits completion, it is launched.
     * And so on until tasks remains.
     */
    protected LinkedList<Task> taskQueue;
    protected BattlefieldRenderer br;

    private Task currentTask;
    private Sequence checkedSeq;

    public AnimationScheduler(BattlefieldRenderer br){
        this.taskQueue = new LinkedList<Task>();
        this.br = br;
    }

    @Override
    public void update(float dt) {
        if(currentTask.isCompleted()){
            taskQueue.pop();
        }
        if(!taskQueue.isEmpty()){
            currentTask = taskQueue.peek();
            for(int i = 0; i < currentTask.parallelThreads.size; i++){
                checkedSeq = currentTask.parallelThreads.get(i);
                if(!checkedSeq.isDone() && !checkedSeq.renderers.peek().isExecuting()){
                    checkedSeq.renderers.pop();
                    checkedSeq.dataBundles.pop();
                    if(checkedSeq.renderers.size() > 1) {
                        checkedSeq.renderers.peek().getNotification(checkedSeq.dataBundles.peek());
                    }
                }
            }
        }
    }


    public void addTask(Task task){
        task.convert(br);
        taskQueue.offer(task);
    }

    public void reset(){
        taskQueue.clear();
    }

    public boolean isExecuting(){
        return taskQueue.isEmpty();
    }



    //------------ TASK ANIMATION CLASS -------------------------------



    public static class Task {
        public Array<Sequence> parallelThreads;

        public Task(){
            this.parallelThreads = new Array<Sequence>();
        }

        public Task(Sequence sequence){
            this();
            this.parallelThreads.add(sequence);
        }

        public void set(Array<Sequence> threads){
            parallelThreads.addAll(threads);
        }

        public void add(Sequence sequence){
            this.parallelThreads.add(sequence);
        }

        public void aggregate(Task task){
            parallelThreads.addAll(task.parallelThreads);
        }

        public void clear(){
            this.parallelThreads.clear();
        }

        public void convert(BattlefieldRenderer br){
            for(int i = 0; i < parallelThreads.size; i++){
                parallelThreads.get(i).convert(br);
            }
        }

        public boolean isCompleted(){
            boolean completed = true;
            for(int i = 0; i < parallelThreads.size; i++){
                completed &= parallelThreads.get(i).isDone();
            }
            return completed;
        }

    }


    //------------ THREAD ANIMATION CLASS -------------------------------

    public static class Sequence {
        protected LinkedList<Observable> models;
        protected LinkedList<Renderer> renderers;
        protected LinkedList<Object> dataBundles;

        public Sequence(){
            models = new LinkedList<Observable>();
            renderers = new LinkedList<Renderer>();
            dataBundles = new LinkedList<Object>();
        }

        public Sequence(Observable[] models, Object[] dataBundles) {
            this();
            set(models, dataBundles);
        }

        public Sequence(Observable model, Object dataBundle){
            this();
            add(model, dataBundle);
        }

        public void set(Observable[] models, Object[] dataBundles) {
            reset();
            if(models.length == dataBundles.length) {
                for(int i = 0; i < models.length; i++) {
                    this.models.offer(models[i]);
                    this.dataBundles.offer(dataBundles[i]);
                }
            }
        }

        public void add(Observable model, Object dataBundle){
            this.models.add(model);
            this.dataBundles.add(dataBundle);
        }

        public void reset(){
            models.clear();
            dataBundles.clear();
        }

        protected void convert(BattlefieldRenderer br){
            for(int i = 0; i < models.size(); i++){
                if(models.get(i) instanceof Unit){
                    renderers.add(br.getUnitRenderer((Unit)models.get(i)));
                }else if(models.get(i) instanceof Battlefield){
                    renderers.add(br);
                }else{
                    dataBundles.remove(i);
                    i--;
                }
            }
        }

        public boolean isDone(){
            return dataBundles.size() == 0;
        }
    }
}
