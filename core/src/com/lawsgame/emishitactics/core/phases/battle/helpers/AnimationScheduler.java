package com.lawsgame.emishitactics.core.phases.battle.helpers;

import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.WaitTask;
import com.lawsgame.emishitactics.engine.GameUpdatableEntity;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;

import java.util.LinkedList;

/**
 * helper which allows
 * - to posepone  animations triggering
 * - to synchronize animations triggering
 */
public class AnimationScheduler implements GameUpdatableEntity{

    private LinkedList<Task> tasks;
    private boolean paused;

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
            tasks.peek().update(dt);

            // launch the next task
            if(tasks.peek().isCompleted()){
                tasks.peek().notifyAllObservers(tasks.peek());
                tasks.pop();
            }
        }
    }

    public boolean isEmpty(){
        return tasks.size() == 0;
    }

    public void addTask(Task task){
        if(task.isIrrelevant()) {
            task.notifyAllObservers(task);
        }else{
            tasks.offer(task);
        }
    }

    public String toString(){
        String str = "\nScheduler\n";
        for(int i = 0; i < tasks.size(); i++){
            str += "\n"+i+" = ";
            str += tasks.get(i).toString();
        }
        return str+"\n";
    }

    public void wait(float delay) {
        addTask(new WaitTask(delay));
    }



    public static abstract class Task extends Observable implements GameUpdatableEntity{

        public abstract void init();
        public abstract boolean isInitiazed();
        public abstract boolean isCompleted();
        public abstract boolean isIrrelevant();


    }

}
