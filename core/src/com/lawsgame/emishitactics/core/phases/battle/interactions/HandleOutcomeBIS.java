package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand.Outcome;
import com.lawsgame.emishitactics.core.phases.battle.helpers.TileHighlighter;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.interactions.HandleOutcomeBIS.HandleOutcomeTask.HOTType;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;

import java.util.LinkedList;
import java.util.Stack;

public class HandleOutcomeBIS extends BattleInteractionState{

    private Stack<ActorCommand> historic;
    private Outcome outcome;

    private LinkedList<HandleOutcomeTask> tasks;
    private boolean aiTurn;

    public HandleOutcomeBIS(BattleInteractionMachine bim, Stack<ActorCommand> historic, boolean aiTurn) {
        super(bim, true, false, false, false, false);

        this.historic = historic;
        this.outcome = historic.peek().getOutcome();
        this.tasks = new LinkedList<HandleOutcomeTask>();
        this.aiTurn = aiTurn;

    }

    @Override
    public void init() {
        System.out.println("HANDLE OUTCOME : initiator = "+historic.peek().getInitiator().getName());
        System.out.println(outcome);

        /*
         * BUG HANDLE OUTCOME:
         * handle outcome has been implemented for handling player interaction first
         * Not to handle outcome of AI-Driven interaction. Therefore, if a unit yet alive at the end of an action of the currently displyad outcome.
         * An error can arive since if the unit dies later during the AIs turn and the action resulting in this death has already been applied to the model.
         * Nasty bug!!
         */

        super.init();
        if(outcome != null && !outcome.isHandled()){
            float xReceiverBUR;
            float yReceiverBUR;
            while (!outcome.isHandled()) {

                if(!outcome.isExpHandled()){


                    ActorCommand.ExperiencePointsHolder holder = outcome.expHolders.peek();
                    if(holder.hasNext()){
                        final Unit receiver = holder.receiver;
                        final int[] expLvls = holder.next();
                        final int experience = expLvls[expLvls.length - 1];
                        xReceiverBUR = bim.bfr.getUnitRenderer(holder.receiver).getCenterX();
                        yReceiverBUR = bim.bfr.getUnitRenderer(holder.receiver).getCenterY();
                        final int rowReceiver = bim.bfr.getRow(xReceiverBUR, yReceiverBUR);
                        final int colReceiver = bim.bfr.getCol(xReceiverBUR, yReceiverBUR);

                        //A TESTER!!!!

                        HandleOutcomeTask experienceTask = new HandleOutcomeTask(HOTType.EXPERIENCE);
                        experienceTask.addParallelSubTask(new StandardTask.CommandSubTask(0f){
                            @Override
                            public void run() {
                                bim.focusOn(rowReceiver, colReceiver, true, true, false, TileHighlighter.SltdUpdateMode.MATCH_TOUCHED_TILE, true);
                                bim.pp.experiencePanel.update(experience);
                                bim.pp.experiencePanel.show();
                                bim.pp.levelUpPanel.hide();
                                bim.pp.lootPanel.hide();
                            }
                        });
                        tasks.add(experienceTask);

                        if(expLvls.length > 1){
                            // of the unit has leveled up
                            HandleOutcomeTask levelUpTask = new HandleOutcomeTask(HOTType.LEVELUP);
                            levelUpTask.addParallelSubTask(new StandardTask.RendererSubTaskQueue(bim.bfr.getUnitRenderer(holder.receiver), Data.AnimId.LEVELUP));
                            if (holder.receiver.belongToAnArmy() || holder.receiver.getArmy().isPlayerControlled()) {
                                //levelUpTask.addParallelSubTask(new StandardTask.CommandSubTask(new DisplayLevelupPanel(holder.receiver, expLvls, bim), 0f));
                                levelUpTask.addParallelSubTask(new StandardTask.CommandSubTask(0f){
                                    @Override
                                    public void run() {
                                        bim.pp.experiencePanel.hide();
                                        bim.pp.levelUpPanel.update(receiver, expLvls);
                                        bim.pp.levelUpPanel.show();
                                        bim.pp.lootPanel.hide();
                                    }
                                });
                            }
                            tasks.add(levelUpTask);
                        }
                    }else{
                        outcome.expHolders.pop();
                    }

                }else {
                    final ActorCommand.DroppedItemHolder holder = outcome.droppedItemHolders.pop();
                    HandleOutcomeTask lootTask = new HandleOutcomeTask(HOTType.LOOT);
                    //lootTask.addParallelSubTask(new StandardTask.CommandSubTask(new DisplayLootPanel(holder.droppedItem, !aiTurn, bim), 0f));
                    lootTask.addParallelSubTask(new StandardTask.CommandSubTask(0f){
                        @Override
                        public void run() {
                            bim.pp.experiencePanel.hide();
                            bim.pp.levelUpPanel.hide();
                            bim.pp.lootPanel.update(holder.droppedItem, !aiTurn);
                            bim.pp.lootPanel.show();
                        }
                    });

                    tasks.offer(lootTask);
                }
            }
        }

        if(!tasks.isEmpty()) {
            bim.scheduler.addTask(tasks.pop());
        }else{
            proceed();
        }
    }

    private void proceed(){
        /*
        the order of this if/else if/else is WEIRD
        indeed, it checks if the BIS is called by AIBIS before checking if the battle is over.

        Here is the explanation :
        Technically the condition :im.bfr.getModel().getSolver().isBattleOver() can be true way before all the outcomes of the actions
        taken by the AI are displayed since it check the condition model wise and not render wise.
        Therefore, it needs to handle the battle is over condition indepedently within the AIBIS class.
        (cf AIBIS.proceed())
         */
        if(aiTurn) {
             bim.rollback();
         }else if(bim.bfr.getModel().getBattleSolver().isBattleOver()) {
            bim.replace(new BattleOverBIS(bim));
        }else{
            if(!historic.isEmpty()&& historic.peek().getInitiator() != null) {

                Unit actor = historic.peek().getInitiator();
                if(actor.isOutOfAction() || actor.isDone()) {
                        bim.replace(new EndUnitTurnBIS(bim, actor));
                }else{
                    bim.replace(new SelectActionBIS(bim, actor, historic));
                }
            }else{
                try {
                    throw new BISException("historic empty or initiator null");
                } catch (BISException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        if(tasks.isEmpty()){
            proceed();
        }else{
            if(aiTurn){
                while(!tasks.isEmpty() && !(tasks.peek().type == HOTType.LEVELUP)){
                    bim.scheduler.addTask(tasks.pop());
                }
                if(!tasks.isEmpty()){
                    bim.scheduler.addTask(tasks.pop());
                }
            }else {
                bim.scheduler.addTask(tasks.pop());
            }
        }
        return true;
    }

    @Override
    public void end() {
        super.end();
        bim.pp.levelUpPanel.hide();;
        bim.pp.experiencePanel.hide();
        bim.pp.lootPanel.hide();
    }

    //----------------- HELPER CLASS ---------------------------------------

    /**
     * render task wrapper which holds useful informations to be processed later by the HandleOutcomeBIS
     */
    static class HandleOutcomeTask extends StandardTask{
        public enum HOTType{
            EXPERIENCE,
            LEVELUP,
            LOOT
        }
        public final HOTType type;

        public HandleOutcomeTask(HOTType type){
            super();
            this.type = type;

        }
    }

}
