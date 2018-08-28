package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine.FocusOn;
import com.lawsgame.emishitactics.core.phases.battle.commands.EndTurnCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand.EncounterOutcome;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler.Task;
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

import java.util.LinkedList;
import java.util.Stack;

public class HandleOutcomeBIS extends BattleInteractionState{
    private Stack<BattleCommand> historic;
    private EncounterOutcome outcome;

    private static EncounterOutcome emptyOutcome;

    private EndTurnCommand endTurnCommand;
    private ExperiencePanel experiencePanel;
    private LevelUpPanel levelUpPanel;
    private LootPanel lootPanel;

    private LinkedList<Task> tasks;

    public HandleOutcomeBIS(BattleInteractionMachine bim, Stack<BattleCommand> historic) {
        super(bim, true, false, false);


        this.historic = historic;
        if(outcome == null) emptyOutcome = new EncounterOutcome();
        this.outcome = (historic.size() > 0) ? historic.peek().getOutcome() : emptyOutcome;

        this.endTurnCommand = new EndTurnCommand(bim.bfr, bim.scheduler);
        this.endTurnCommand.setFree(true);
        this.experiencePanel = new TempoExperiencePanel(bim.uiStage.getViewport());
        this.levelUpPanel = new TempoLevelUpPanel(bim.uiStage.getViewport());
        this.lootPanel = new TempoLootPanel(bim.uiStage.getViewport());

        bim.uiStage.addActor(experiencePanel);
        bim.uiStage.addActor(levelUpPanel);
        bim.uiStage.addActor(lootPanel);

        this.tasks = new LinkedList<Task>();

    }

    @Override
    public void init() {
        System.out.println("HANDLE OUTCOME : initiator = "+historic.peek().getActor().getName()+"\n" +outcome.toString());

        if(outcome.isHandled()){
            if(tasks.isEmpty()) {
                moveForward();
            }
        }else {

            while (!outcome.isHandled()) {

                if(!outcome.isExperienceDistributed()){
                    final IUnit receiver = outcome.receivers.pop();
                    final int experience = outcome.experienceGained.pop();

                    if(!receiver.isOutOfAction()) {
                        final int[] receiverPos = bim.battlefield.getUnitPos(receiver);
                        tasks.offer(new StandardTask(new DisplayExperiencePanel(receiver, bim, experiencePanel, levelUpPanel, lootPanel, experience), 0f));

                        final int[] statGain = receiver.addExpPoints(experience);
                        for (int i = 0; i < statGain.length; i++) {
                            if (statGain[i] > 0) {

                                // of the unit has leveled up
                                StandardTask levelUpTask = new StandardTask();
                                levelUpTask.addThread(new RendererThread(bim.bfr.getUnitRenderer(receiver), Data.AnimationId.LEVELUP));
                                levelUpTask.addThread(new StandardTask.CommandThread(new FocusOn(bim, receiverPos[0], receiverPos[1]), 0f));

                                if (receiver.getArmy().isPlayerControlled()) {
                                    levelUpTask.addThread(new StandardTask.CommandThread(new DisplayLevelupPanel(receiver, bim.mainI18nBundle, experiencePanel, levelUpPanel, lootPanel, statGain), 0f));
                                }
                                tasks.add(levelUpTask);
                                break;
                            }
                        }

                    }

                }else if(!outcome.isLootedItemsReclaimed()){
                    final Item droppedItem = outcome.droppedItems.pop();

                    tasks.offer(new StandardTask(new DisplayLootPanel(droppedItem, bim.mainI18nBundle, experiencePanel, levelUpPanel, lootPanel), 0f));
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

    private void moveForward(){
        try {
            if(!historic.isEmpty()) {
                if(historic.peek().getActor() != null) {

                    int[] actorPos = bim.battlefield.getUnitPos(historic.peek().getActor());
                    if (!historic.peek().getActor().isDone()) {
                        bim.replace(new SelectActionBIS(bim, actorPos[0], actorPos[1], historic));
                    } else {
                        endTurnCommand.apply(actorPos[0], actorPos[1]);
                        bim.replace(new SelectActorBIS(bim));
                    }
                }else{
                    throw new BISException("historic top command has no actor set up");
                }
            }else{
                throw new BISException("historic empty");
            }
        } catch (BISException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        if(tasks.isEmpty()){
            moveForward();
        }else{
            bim.scheduler.addTask(tasks.pop());
        }
        return true;
    }

    @Override
    public void update60(float dt) {

    }

    @Override
    public void prerender(SpriteBatch batch) {

    }

    @Override
    public void renderBetween(SpriteBatch batch) {

    }

    @Override
    public void renderAhead(SpriteBatch batch) {

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

        public DisplayExperiencePanel(IUnit receiver, BattleInteractionMachine bim, ExperiencePanel experiencePanel, LevelUpPanel levelUpPanel, LootPanel lootPanel, int experience) {
            this.bim = bim;
            this.receiver = receiver;
            int[] receiverPos = bim.battlefield.getUnitPos(receiver);
            this.rowReceiver = receiverPos[0];
            this.colReceiver = receiverPos[1];
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
