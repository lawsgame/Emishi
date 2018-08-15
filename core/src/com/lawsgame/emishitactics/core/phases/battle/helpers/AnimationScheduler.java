package com.lawsgame.emishitactics.core.phases.battle.helpers;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.engine.GameUpdatableEntity;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.rendering.Renderer;
import com.lawsgame.emishitactics.engine.timers.CountDown;

import java.util.LinkedList;

/**
 * helper which allows
 * - to posepone  animations triggering
 * - to synchronize animations triggering
 */
public class AnimationScheduler implements GameUpdatableEntity{
    private LinkedList<Task> tasks;

    public AnimationScheduler(){
        tasks = new LinkedList<Task>();
    }

    @Override
    public void update(float dt) {

        if(!tasks.isEmpty()){
            if(!tasks.peek().isInitiazed()){
                tasks.peek().init();
            }

            //update the threads of the current task
            for(int i = 0; i < tasks.peek().parallelThreads.size; i++){
                tasks.peek().parallelThreads.get(i).update(dt);
            }

            // launch the next task
            if(tasks.peek().isCompleted()){
                tasks.pop();
            }
        }
    }

    public boolean isWaiting(){
        return tasks.isEmpty();
    }

    public void addTask(Task task){
        if(!task.isEmpty())
            tasks.offer(task);
    }

    public String toString(){
        String str = "\nScheduler\n";
        for(int i = 0; i < tasks.size(); i++){
            str += "\nTask "+i+" : \n"+tasks.get(i).toString();
        }
        return str;
    }


    /**
     * task class
     */
    public static class Task {
        protected Array<Thread> parallelThreads;
        protected boolean initiazed = false;

        public Task(Renderer renderer, Object dataBundle){
            this();
            Thread thread = new Thread(renderer);
            thread.addQuery(dataBundle);
            parallelThreads.add(thread);
        }

        public Task(Observable sender, Renderer executer, Object dataBundle){
            this();
            Thread thread = new Thread(executer);
            thread.addQuery(sender, dataBundle);
            parallelThreads.add(thread);
        }

        public Task(){
            parallelThreads = new Array<Thread>();
        }

        boolean isCompleted() {
            for(int i = 0; i < parallelThreads.size; i++){
                if(parallelThreads.get(i).isCompleted()){
                    return true;
                }
            }
            return false;
        }

        void init() {
            initiazed = true;
            for(int i = 0; i < parallelThreads.size; i++){
                parallelThreads.get(i).init();
            }
        }

        public void addThread(Thread thread){
            parallelThreads.add(thread);
        }

        public void addllThreads(Array<Thread> threads){
            parallelThreads.addAll(threads);
        }


        public boolean isInitiazed() {
            return initiazed;
        }

        public String toString(){
            String str = "";
            for(int i = 0; i < parallelThreads.size; i++) {


                if(parallelThreads.get(i).executer != null)
                    str += "\n    Thread : executer = " + parallelThreads.get(i).executer.toString();
                else
                    str += "\n    Thread : executer = null";



                for (int j = 0; j < parallelThreads.get(i).bundles.size(); j++) {
                    if(parallelThreads.get(i).tag.equals(""))
                        str += "\n        " + parallelThreads.get(i).bundles.get(j) + " => " + parallelThreads.get(i).senders.get(j);
                    else
                        str += "\n        "+parallelThreads.get(i).tag;
                }
            }
            return str;
        }

        public boolean isEmpty() {
            for(int i = 0; i < parallelThreads.size; i++){
                if(parallelThreads.get(i).bundles.size() > 0){
                    return false;
                }
            }
            return true;
        }
    }



    /**
     * thread class
     */
    public static class Thread {
        protected Renderer executer;
        protected LinkedList<Observable> senders;
        protected LinkedList<Object> bundles;
        protected CountDown countDown;
        protected String tag = "";

        public Thread(Renderer executer, float delay){
            this.executer = executer;
            this.countDown = new CountDown(delay);
            this.bundles = new LinkedList<Object>();
            this.senders = new LinkedList<Observable>();

        }
        public Thread(Renderer executer){
            this(executer, 0);

        }

        public Thread(Renderer executer, Observable sender, Object dataBundle){
            this(executer);
            addQuery(sender, dataBundle);
        }

        public Thread(Renderer renderer, Object dataBundle){
            this(renderer);
            addQuery(renderer.getModel(), dataBundle);
        }

        void init() {
            countDown.run();
        }

        boolean isCompleted() {
            return bundles.isEmpty() && !executer.isExecuting();
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
            if(countDown.isFinished() && !bundles.isEmpty() && !executer.isExecuting()){
                senders.pop().notifyAllObservers(bundles.pop());
            }
        }

        public void setTag(String tag){
            this.tag = tag;
        }
    }
}
