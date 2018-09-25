package com.lawsgame.emishitactics.core.phases.battle.helpers.tasks;


import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.rendering.Renderer;
import com.lawsgame.emishitactics.engine.timers.CountDown;

import java.util.LinkedList;

public class StandardTask extends Task {
    protected String tag = "";
    protected Array<Thread> parallelThreads;
    protected boolean initiazed = false;

    public StandardTask(Renderer renderer, Object dataBundle){
        this();
        RendererThread rendererThread = new RendererThread(renderer);
        rendererThread.addQuery(dataBundle);
        parallelThreads.add(rendererThread);
    }

    public StandardTask(Observable sender, Renderer executer, Object dataBundle){
        this();
        RendererThread rendererThread = new RendererThread(executer);
        rendererThread.addQuery(sender, dataBundle);
        parallelThreads.add(rendererThread);
    }

    public StandardTask(){
        parallelThreads = new Array<Thread>();
    }

    public StandardTask(SimpleCommand command, float delay){
        this();
        addThread(new  CommandThread(command, delay));
    }

    public void update(float dt){
        for(int i = 0; i < parallelThreads.size; i++){
            parallelThreads.get(i).update(dt);
        }
    }

    public boolean isCompleted() {
        for(int i = 0; i < parallelThreads.size; i++){
            if(parallelThreads.get(i).isCompleted()){
                return true;
            }
        }
        return false;
    }

    public void init() {
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
        String str = "STANDARD TASK ";
        if(!getTag().equals("")) str += "["+getTag()+"] ";
        str += ": ";
        for(int i = 0; i < parallelThreads.size; i++) {
            str +="\n   "+parallelThreads.get(i).toString();
        }
        return str;
    }

    /**
     * convient method
     * usefull to check if adding that task to the queue is relevant.
     * @return
     */
    @Override
    public boolean isIrrelevant() {
        for(int i = 0; i < parallelThreads.size; i++){
            if(!parallelThreads.get(i).isEmpty()){
                return false;
            }
        }
        return true;
    }


    public void tag(String tag){
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }


    public static abstract class Thread {
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


    public static class CommandThread extends Thread {
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
            if(command != null){
                commands.offer(command);
                delays.offer(delay);
            }

        }

        @Override
        void init() {
            launch();
        }

        private void launch(){
            /*
            System.out.println("DIAGNOSIS ");
            System.out.println("commands size: "+commands.size());
            System.out.println("commands top element: "+commands.peek());
            SimpleCommand command = commands.peek();
            System.out.println("simple command peek :"+command);
            */

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

        @Override
        public String toString(){
            return "Command thread ["+getTag()+"]";
        }
    }

    public static class RendererThread extends Thread {
        protected Renderer executer;
        protected LinkedList<Observable> senders;
        protected LinkedList<Object> bundles;
        protected CountDown countDown;


        public RendererThread(Renderer executer, float delay){
            this.executer = executer;
            this.countDown = new CountDown(delay);
            this.bundles = new LinkedList<Object>();
            this.senders = new LinkedList<Observable>();

        }

        public RendererThread(Renderer executer){
            this(executer, 0);

        }

        public RendererThread(Renderer executer, Observable sender, Object dataBundle){
            this(executer);
            addQuery(sender, dataBundle);
        }

        public RendererThread(Renderer renderer, Object dataBundle){
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

        @Override
        public String toString() {
            String str = "RendererThread : executer = "+((executer != null) ? executer.toString(): "null");
            for (int j = 0; j < bundles.size(); j++) {
                str += " : " + bundles.get(j) + " => " + senders.get(j);
            }
            return str;
        }
    }
}
