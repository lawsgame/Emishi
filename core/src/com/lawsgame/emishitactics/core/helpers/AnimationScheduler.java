package com.lawsgame.emishitactics.core.helpers;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.engine.GameUpdatableEntity;
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
            //update the threads of the current task
            for(int i = 0; i < tasks.peek().parallelThreads.size; i++){
                tasks.peek().parallelThreads.get(i).update(dt);
            }

            // launch the next task
            if(tasks.peek().isCompleted()){
                tasks.pop();
                if(!tasks.isEmpty())
                    tasks.peek().init();
            }
        }
    }

    public void addTask(Task task){
        tasks.offer(task);
    }

    public static class Task {
        protected Array<Thread> parallelThreads;

        public Task(Renderer renderer, Object dataBundle){
            this();
            Thread thread = new Thread(renderer, 0f);
            thread.addQuery(dataBundle);
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
            for(int i = 0; i < parallelThreads.size; i++){
                parallelThreads.get(i).init();
            }
        }

        public void addThread(Thread thread){
            parallelThreads.add(thread);
        }


    }

    public static class Thread {
        protected Renderer renderer;
        protected LinkedList<Object> bundles;
        protected CountDown countDown;

        public Thread(Renderer renderer, float delay){
            this.renderer = renderer;
            this.countDown = new CountDown(delay);
            this.bundles = new LinkedList<Object>();

        }

        void init() {
            countDown.run();
        }

        boolean isCompleted() {
            return bundles.isEmpty() && !renderer.isExecuting();
        }

        public void addQuery(Object dataBundle){
            bundles.offer(dataBundle);
        }

        void update(float dt) {
            countDown.update(dt);
            if(countDown.isFinished()&& !renderer.isExecuting()){
                renderer.getNotification(bundles.pop());
            }
        }
    }
}
