package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine.FocusOn;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand.Outcome;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask.RendererThread;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ExperiencePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.LevelUpPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.LootPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoExperiencePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoLevelUpPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoLootPanel;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;
import com.lawsgame.emishitactics.core.phases.battle.interactions.HandleOutcomeBIS.HandleOutcomeTask.HOTType;

import java.util.LinkedList;
import java.util.Stack;

public class HandleOutcomeBIS extends BattleInteractionState{
    private Stack<ActorCommand> historic;
    private Outcome outcome;

    private static Outcome emptyOutcome;

    private ExperiencePanel experiencePanel;
    private LevelUpPanel levelUpPanel;
    private LootPanel lootPanel;

    private LinkedList<HandleOutcomeTask> tasks;
    private boolean aiTurn;

    public HandleOutcomeBIS(BattleInteractionMachine bim, Stack<ActorCommand> historic, boolean aiTurn) {
        super(bim, true, false, false, false, false);

        this.historic = historic;
        if(emptyOutcome == null) emptyOutcome = new Outcome(bim.player.getInventory());
        this.outcome = (historic.size() > 0) ? historic.peek().getOutcome() : emptyOutcome;
        this.experiencePanel = new TempoExperiencePanel(bim.uiStage.getViewport());
        this.levelUpPanel = new TempoLevelUpPanel(bim.uiStage.getViewport());
        this.lootPanel = new TempoLootPanel(bim.uiStage.getViewport());

        bim.uiStage.addActor(experiencePanel);
        bim.uiStage.addActor(levelUpPanel);
        bim.uiStage.addActor(lootPanel);

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
        if(outcome.isHandled()){

            if(tasks.isEmpty()) {
                proceed();
            }
        }else {
            int[] expLvls;
            int experience;
            float xReceiverBUR;
            float yReceiverBUR;
            int rowReceiver;
            int colReceiver;
            while (!outcome.isHandled()) {

                if(!outcome.isExpHandled()){


                    ActorCommand.ExperiencePointsHolder holder = outcome.expHolders.peek();
                    if(holder.hasNext()){
                        expLvls = holder.next();
                        experience = expLvls[expLvls.length - 1];
                        xReceiverBUR = bim.bfr.getUnitRenderer(holder.receiver).getCenterX();
                        yReceiverBUR = bim.bfr.getUnitRenderer(holder.receiver).getCenterY();
                        rowReceiver = bim.bfr.getRow(xReceiverBUR, yReceiverBUR);
                        colReceiver = bim.bfr.getCol(xReceiverBUR, yReceiverBUR);

                        //A TESTER!!!!


                        HandleOutcomeTask experienceTask = new HandleOutcomeTask(HOTType.EXPERIENCE);
                        experienceTask.addThread(new StandardTask.CommandThread(new FocusOn(bim, rowReceiver, colReceiver), 0f));
                        experienceTask.addThread(new StandardTask.CommandThread(new DisplayExperiencePanel(rowReceiver, colReceiver, holder.receiver, bim, experiencePanel, levelUpPanel, lootPanel, experience), 0f));
                        tasks.add(experienceTask);

                        if(expLvls.length > 1){
                            // of the unit has leveled up
                            HandleOutcomeTask levelUpTask = new HandleOutcomeTask(HOTType.LEVELUP);
                            levelUpTask.addThread(new RendererThread(bim.bfr.getUnitRenderer(holder.receiver), Data.AnimId.LEVELUP));
                            if (holder.receiver.isMobilized() || holder.receiver.getArmy().isPlayerControlled()) {
                                levelUpTask.addThread(new StandardTask.CommandThread(new DisplayLevelupPanel(holder.receiver, bim.mainI18nBundle, experiencePanel, levelUpPanel, lootPanel, expLvls), 0f));
                            }
                            tasks.add(levelUpTask);
                        }
                    }else{
                        outcome.expHolders.pop();
                    }

                }else {
                    ActorCommand.DroppedItemHolder holder = outcome.droppedItemHolders.pop();
                    HandleOutcomeTask lootTask = new HandleOutcomeTask(HOTType.LOOT);
                    lootTask.addThread(new StandardTask.CommandThread(new DisplayLootPanel(holder.droppedItem, bim.mainI18nBundle, experiencePanel, levelUpPanel, lootPanel), 0f));
                    tasks.offer(lootTask);
                }
            }
        }

        if(!tasks.isEmpty())
            bim.scheduler.addTask(tasks.pop());
    }

    @Override
    public void end() {
        super.end();
        experiencePanel.remove();
        levelUpPanel.remove();
        lootPanel.remove();
    }

    private void proceed(){
        /*
        the order of this if/else if/else is WEIRD
        indeed, it checks if the BIS is called by AIBIS before checking if the battle is over.

        Here is the explanation :
        Technically the condition :im.battlefield.getSolver().isBattleOver() can be true way before all the outcomes of the actions
        taken by the AI are displayed since it check the condition model wise and not render wise.
        Therefore, it needs to handle the battle is over condition indepedently within the AIBIS class.
        (cf AIBIS.proceed())
         */
        if(aiTurn) {
             bim.rollback();
         }else if(bim.battlefield.getSolver().isBattleOver()) {
            bim.replace(new BattleOverBIS(bim));
        }else{
            if(!historic.isEmpty()&& historic.peek().getInitiator() != null) {

                IUnit actor = historic.peek().getInitiator();
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



    //----------------- HELPER CLASS ---------------------------------------

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


    static class DisplayExperiencePanel extends SimpleCommand {
        private IUnit receiver;
        private int rowReceiver;
        private int colReceiver;
        private BattleInteractionMachine bim;
        private ExperiencePanel experiencePanel;
        private LevelUpPanel levelUpPanel;
        private LootPanel lootPanel;
        private int experience;

        public DisplayExperiencePanel(int rowReceiver, int colReceiver, IUnit receiver, BattleInteractionMachine bim, ExperiencePanel experiencePanel, LevelUpPanel levelUpPanel, LootPanel lootPanel, int experience) {
            this.bim = bim;
            this.receiver = receiver;
            this.rowReceiver= rowReceiver;
            this.colReceiver = colReceiver;
            this.experiencePanel = experiencePanel;
            this.levelUpPanel = levelUpPanel;
            this.lootPanel = lootPanel;
            this.experience = experience;

        }

        @Override
        public void apply() {
            bim.focusOn(rowReceiver, colReceiver, true, false, false, true, false);
            experiencePanel.set(bim.mainI18nBundle, receiver.getExperience(), experience);
            experiencePanel.show();
            levelUpPanel.hide();
            lootPanel.hide();
        }

    }

    static class DisplayLevelupPanel extends SimpleCommand{
        private IUnit receiver;
        private I18NBundle bundle;
        private ExperiencePanel experiencePanel;
        private LevelUpPanel levelUpPanel;
        private LootPanel lootPanel;
        private int[] statGain;

        public DisplayLevelupPanel(IUnit receiver, I18NBundle bundle, ExperiencePanel experiencePanel, LevelUpPanel levelUpPanel, LootPanel lootPanel, int[] statGain) {
            this.receiver = receiver;
            this.bundle = bundle;
            this.experiencePanel = experiencePanel;
            this.levelUpPanel = levelUpPanel;
            this.lootPanel = lootPanel;
            this.statGain = statGain;
        }

        @Override
        public void apply() {
            experiencePanel.hide();
            levelUpPanel.set(bundle, receiver, statGain);
            levelUpPanel.show();
            lootPanel.hide();
        }
    }

    static class DisplayLootPanel extends SimpleCommand{
        private Item droppedItem;
        private I18NBundle bundle;
        private ExperiencePanel experiencePanel;
        private LevelUpPanel levelUpPanel;
        private LootPanel lootPanel;

        public DisplayLootPanel(Item droppedItem, I18NBundle bundle, ExperiencePanel experiencePanel, LevelUpPanel levelUpPanel, LootPanel lootPanel) {
            this.droppedItem = droppedItem;
            this.bundle = bundle;
            this.experiencePanel = experiencePanel;
            this.levelUpPanel = levelUpPanel;
            this.lootPanel = lootPanel;
        }

        @Override
        public void apply() {
            experiencePanel.hide();
            levelUpPanel.hide();
            lootPanel.set(droppedItem, bundle);
            lootPanel.show();
        }
    }


}
