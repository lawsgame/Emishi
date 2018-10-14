package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.ai.AggressiveAI;
import com.lawsgame.emishitactics.core.phases.battle.ai.interfaces.AI;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask.CommandThread;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ActionInfoPanel;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;

import java.util.LinkedList;
import java.util.Stack;

public class AiBIS extends BattleInteractionState implements Observer {

    private AI ai;
    private Thread threadAI;
    private LinkedList<AI.CommandBundle> bundleQueue;
    private Stack<ActorCommand> trackrecord;
    private boolean waitForCommand;

    public AiBIS(BattleInteractionMachine bim) {
        super(bim, true, false, false, true, false);

        this.ai = new AggressiveAI(bim.bfr, bim.scheduler, bim.app, bim.player.getInventory());
        this.threadAI = new Thread(ai, "AI Thread");
        this.bundleQueue = new LinkedList<AI.CommandBundle>();
        this.trackrecord = new Stack<ActorCommand>();
        this.waitForCommand = true;

        this.threadAI.start();
        this.ai.attach(this);
    }

    @Override
    public  void init() {
        System.out.println("AI BIS");

        super.init();
        next();
    }

    private  void next(){
        System.out.println("    > next()");
        if(isFinished()){
            System.out.println("    > next.proceed()");
            proceed();
        }else {
            System.out.println("    > next.executeCommand()");
            waitForCommand = executeNextCommand();
        }
    }

    @Override
    public void update60(float dt) {
        super.update60(dt);
        if(waitForCommand){
            System.out.println("    > update60.executeCommand()");
            waitForCommand = executeNextCommand();

        }

    }

    private boolean isFinished(){
        return bundleQueue.isEmpty() && !threadAI.isAlive();
    }

    private void proceed(){
        if(bim.battlefield.getSolver().isBattleOver()){
            bim.replace(new BattleOverBIS(bim));
        }else{
            bim.replace(new EndArmyTurnBIS(bim));
        }
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        return true;
    }

    @Override
    public synchronized void getNotification(Observable sender, Object data) {


        if(sender instanceof AI) {
            System.out.println("        > getNotification : "+sender);

            if (data instanceof AI.CommandBundle) {

                storeCommandBundle((AI.CommandBundle) data);
                System.out.println("        > getNotification.storeBundle()");

            } else if (data == sender) {

                System.out.println("        > getNotification.ai_thread_finished");
                this.ai.detach(this);
            }
        }else if(sender instanceof BattleCommand){
            System.out.println("    > getNotification : "+sender);

            if(data instanceof ActorCommand) {

                System.out.println("    > getNotification.handleOutcome()");
                handleOutcome((ActorCommand) data);
            }else{

                System.out.println("    > getNotification.next()");
                next();
            }
        }
    }


    private synchronized void storeCommandBundle(AI.CommandBundle bundle){
        this.bundleQueue.offer(bundle);
    }


    private boolean executeNextCommand(){
        boolean noCommandAvailable = true;
        System.out.println("    > AIBIS.waitForCommand ? "+bundleQueue.isEmpty());
        if(!bundleQueue.isEmpty()) {

            noCommandAvailable = false;

            AI.CommandBundle bundle = bundleQueue.peek();
            if (bundle.isEmpty()) {

                bundleQueue.pop();
                System.out.println("    > executeCommand.next() : bundle is empty");
                next();
            }else{


                BattleCommand command = bundle.commands.pop();
                final ActionInfoPanel panel = bundle.panels.pop();
                if(bundleQueue.peek().isEmpty()){
                    bundleQueue.pop();
                }

                System.out.println("    > executeCommand.command : "+command);

                // focus the camera on the target and show the action pan
                if(command instanceof ActorCommand) {
                    final ActorCommand actorCommand = (ActorCommand)command;
                    StandardTask showAction = new StandardTask();
                    CommandThread commandThread = new CommandThread();

                    commandThread.addQuery(new SimpleCommand() {
                        @Override
                        public void apply() {
                            bim.focusOn(actorCommand.getRowActor(), actorCommand.getColActor(), true, false, false, true, false);
                            if (panel != null) {
                                bim.uiStage.addActor(panel);
                                panel.show();
                            }
                        }
                    }, (panel != null) ? Data.AIBIS_ACTION_PANEL_DURATION_APPEARANCE : Data.AIBIS_DELAY_CAMERA_FOCUS);

                    if(panel != null) {

                        commandThread.addQuery(new SimpleCommand() {
                            @Override
                            public void apply() {
                                panel.hide();
                            }
                        }, panel.getHidingTime());

                        commandThread.addQuery(new SimpleCommand() {
                            @Override
                            public void apply() {
                                panel.remove();
                            }
                        },0);
                    }
                    showAction.addThread(commandThread);
                    bim.scheduler.addTask(showAction);
                }

                // push the action render task OR trigger
                if(command.containPushableRenderTasks()) {

                    System.out.println("    > executeCommand.pushRenderTasks()");

                    command.pushRenderTasks();
                    command.attach(this);
                }else{

                    System.out.println("    > executeCommand.next() : no pushable tasks");
                    next();
                }
            }
        }
        return noCommandAvailable;
    }


    private void handleOutcome(ActorCommand command){
        command.detach(this);
        trackrecord.push(command);
        bim.push(new HandleOutcomeBIS(bim, trackrecord, true));
    }
}
