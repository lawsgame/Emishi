package com.lawsgame.emishitactics.core.phases.battle.helpers;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.engine.GameUpdatableEntity;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;
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
        return tasks.size() == 0;
    }

    public void addTask(Task task){
        System.out.println("TASK REQUIRED");
        if(!task.isEmpty()) {
            System.out.println("TASK ADDED");
            tasks.offer(task);
        }
    }

    public String toString(){
        String str = "\nScheduler\n";
        for(int i = 0; i < tasks.size(); i++){
            str += "\nTask "+i+" : \n"+tasks.get(i).toString();
        }
        return str+"\n";
    }


    /**
     * task class
     */
    public static class Task {
        protected Array<Thread> parallelThreads;
        protected boolean initiazed = false;

        public Task(Renderer renderer, Object dataBundle){
            this();
            ViewThread viewThread = new ViewThread(renderer);
            viewThread.addQuery(dataBundle);
            parallelThreads.add(viewThread);
        }

        public Task(Observable sender, Renderer executer, Object dataBundle){
            this();
            ViewThread viewThread = new ViewThread(executer);
            viewThread.addQuery(sender, dataBundle);
            parallelThreads.add(viewThread);
        }

        public Task(){
            parallelThreads = new Array<Thread>();
        }

        public Task(SimpleCommand command, float delay){
           this();
           addThread(new  CommandThread(command, delay));
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

        public <T extends Thread> void addllThreads(Array<T> threads){
            parallelThreads.addAll(threads);
        }


        public boolean isInitiazed() {
            return initiazed;
        }

        public String toString(){
            String str = "";
            ViewThread viewThread;
            CommandThread commandThread;
            for(int i = 0; i < parallelThreads.size; i++) {

                if(parallelThreads.get(i) instanceof ViewThread) {

                    viewThread = (ViewThread)parallelThreads.get(i);
                    if (viewThread.executer != null)
                        str += "\n    ViewThread : executer = " + viewThread.executer.toString();
                    else
                        str += "\n    ViewThread : executer = null";


                    for (int j = 0; j < viewThread.bundles.size(); j++) {
                        if (viewThread.tag.equals(""))
                            str += " : " + viewThread.bundles.get(j) + " => " + viewThread.senders.get(j);
                        else
                            str += " : " + viewThread.tag;
                    }
                } else if(parallelThreads.get(i) instanceof CommandThread){
                    commandThread = (CommandThread)parallelThreads.get(i);
                    str +="\n   Command thread : "+commandThread.tag;
                }
            }
            return str;
        }

        /**
         * convient method
         * usefull to check if adding that task to the queue is relevant.
         * @return
         */
        boolean isEmpty() {
            for(int i = 0; i < parallelThreads.size; i++){
                if(!parallelThreads.get(i).isEmpty()){
                    return false;
                }
            }
            return true;
        }
    }

    public static abstract class Thread {
        protected String tag = "";

        abstract void init();
        abstract boolean isCompleted();
        abstract boolean isEmpty();
        abstract void update(float dt);


        public void setTag(String tag){
            this.tag = tag;
        }
    }


    /**
     * command thread
     */
    public static class CommandThread extends Thread{
        LinkedList<SimpleCommand> commands;
        LinkedList<Float> delays;
        CountDown countDown;

        public CommandThread(){
            commands = new LinkedList<SimpleCommand>();
            delays = new LinkedList<Float>();
            countDown = new CountDown(0);
        }

        public CommandThread(SimpleCommand command, float delay){
            this();
            addQuery(command, delay);
        }

        public void addQuery(SimpleCommand command, float delay){
            commands.offer(command);
            delays.offer(delay);
        }

        @Override
        void init() {
            launch();
        }

        private void launch(){
            if(!isEmpty()) {
                commands.peek().apply();
                countDown.reset(delays.peek());
                countDown.run();
            }
        }

        @Override
        boolean isCompleted() {
            return countDown.isFinished() && commands.size() == 0;
        }

        @Override
        boolean isEmpty() {
            return commands.size() == 0;
        }

        @Override
        void update(float dt) {
            countDown.update(dt);
            if(countDown.isFinished()){
                commands.pop();
                delays.pop();
                launch();
            }
        }
    }


    /**
     * view thread class
     */
    public static class ViewThread extends Thread{
        protected Renderer executer;
        protected LinkedList<Observable> senders;
        protected LinkedList<Object> bundles;
        protected CountDown countDown;


        public ViewThread(Renderer executer, float delay){
            this.executer = executer;
            this.countDown = new CountDown(delay);
            this.bundles = new LinkedList<Object>();
            this.senders = new LinkedList<Observable>();

        }
        public ViewThread(Renderer executer){
            this(executer, 0);

        }

        public ViewThread(Renderer executer, Observable sender, Object dataBundle){
            this(executer);
            addQuery(sender, dataBundle);
        }

        public ViewThread(Renderer renderer, Object dataBundle){
            this(renderer);
            addQuery(renderer.getModel(), dataBundle);
        }

        void init() {
            countDown.run();
        }

        boolean isCompleted() {
            return bundles.size() == 0 && !executer.isExecuting();
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
            if(countDown.isFinished() && !isEmpty() && !executer.isExecuting()){
                senders.pop().notifyAllObservers(bundles.pop());
            }
        }
    }
}
