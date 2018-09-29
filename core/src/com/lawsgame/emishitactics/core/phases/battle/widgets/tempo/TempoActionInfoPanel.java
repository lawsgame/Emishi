package com.lawsgame.emishitactics.core.phases.battle.widgets.tempo;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.BattlePhase;
import com.lawsgame.emishitactics.core.phases.battle.commands.AttackCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.BuildCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.HealCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.StealCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.SwitchWeaponCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ActionInfoPanel;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;

public abstract class TempoActionInfoPanel extends ActionInfoPanel {


    private static float X_OFFSET = 15f;
    private static float Y_OFFSET = 15f;
    private static float X_TEXT_OFFSET = 8f;
    private static float Y_TEXT_OFFSET = 8f;
    private static float PANEL_WIDTH = 270;
    private static float PANEL_HEIGHT = 180;

    protected String description;
    protected float slideDuration;
    protected StringBuilder builder = new StringBuilder("");

    public TempoActionInfoPanel(Viewport stageViewport, ActionChoice choice){
        super(stageViewport, choice);
        setWidth(PANEL_WIDTH);
        setHeight(PANEL_HEIGHT);
        setX(stageViewport.getWorldWidth());
        setY(Y_OFFSET);
        setVisible(true);

        this.slideDuration = (X_OFFSET + getWidth())/ Data.PANEL_SLIDE_SPEED;
        this.description = "";
    }

    @Override
    public void show() {
        awaitingActions.offer(moveTo(stageViewport.getWorldWidth() - X_OFFSET - getWidth(),Y_OFFSET, slideDuration));
    }

    @Override
    public void hide() {
        awaitingActions.offer(moveTo( stageViewport.getWorldWidth(),Y_OFFSET, slideDuration));
    }

    @Override
    public boolean isHiding(){
        return getX() == stageViewport.getWorldWidth();
    }

    @Override
    public float getHidingTime() {
        return slideDuration;
    }

    @Override
    public float getShowingTime() {
        return getHidingTime();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(getX() == stageViewport.getWorldWidth()){
            description = builder.toString();
        }

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(TempoSpritePool.get().getBlackBGSprite(),getX(), getY(), getWidth(), getHeight() );
        BattlePhase.testFont.draw(batch, description, getX() + X_TEXT_OFFSET, getY() + getHeight() - Y_TEXT_OFFSET);
    }

    // ------------- GETTERS -------------------


    public StringBuilder getBuilder() {
        return builder;
    }

    public static class AttackInfoPanel extends TempoActionInfoPanel {

        public AttackInfoPanel(Viewport stageViewport) {
            super(stageViewport, ActionChoice.ATTACK);
        }


        @Override
        public void set(ActorCommand command) {

            if(command != null && command instanceof AttackCommand && command.isTargetValid()) {
                setHeight(220);

                AttackCommand currentCommand = (AttackCommand)command;
                builder = new StringBuilder();
                builder.append(command.getInitiator().getName()+" => "+currentCommand.getTargetDefender().getName());
                builder.append("\nMoral / HP : " + currentCommand.getTargetDefender().getCurrentMoral()+" / "+currentCommand.getTargetDefender().getCurrentHP());
                builder.append("\nDamage : " + currentCommand.getDealtDamage(false));
                builder.append("\nHit rate : " + currentCommand.getHitRate(false)+"%");
                builder.append("\nLoot rate : " + currentCommand.getLootRate(false)+"%");
                builder.append("\n\n"+command.getTarget().getName()+" => "+currentCommand.getInitiatorDefender().getName());
                builder.append("\nMoral / HP : " + currentCommand.getInitiatorDefender().getCurrentMoral()+" / "+currentCommand.getInitiatorDefender().getCurrentHP());
                builder.append("\nDamage : " + currentCommand.getDealtDamage(true));
                builder.append("\nHit rate : " + currentCommand.getHitRate(true)+"%");
                builder.append("\nLoot rate : " + currentCommand.getLootRate(true)+"%");
            }
        }
    }



    public static class HealInfoPanel extends TempoActionInfoPanel {

        public HealInfoPanel(Viewport stageViewport) {
            super(stageViewport, ActionChoice.HEAL);
        }

        @Override
        public void set(ActorCommand command) {
            if(command != null && command instanceof HealCommand && command.isTargetValid()) {
                HealCommand currentCommand = (HealCommand)command;
                IUnit target = currentCommand.getTarget();
                builder = new StringBuilder();
                builder.append(command.getInitiator().getName()+" => "+command.getTarget().getName());
                builder.append("\nHP : "+target.getCurrentHP()+" -> "+currentCommand.getRecoveredHitPoints());
                builder.append("\nMP : "+target.getCurrentMoral()+" -> "+currentCommand.getRecoveredMoralPoints());
            }
        }
    }

    public static class SwitchWeaponInfoPanel extends TempoActionInfoPanel {

        public SwitchWeaponInfoPanel(Viewport stageViewport) {
            super(stageViewport, ActionChoice.SWITCH_WEAPON);
        }

        @Override
        public void set(ActorCommand command) {
            if(command != null && command instanceof SwitchWeaponCommand && command.isTargetValid()){
                SwitchWeaponCommand swc = (SwitchWeaponCommand)command;
                builder = new StringBuilder();
                builder.append(command.getInitiator().getName());
                builder.append("\n"+command.getInitiator().getCurrentWeapon().toString()+" => "+swc.getInitiator().getWeapon(swc.getWeaponIndex()));
            }
        }
    }

    public static class StealInfoPanel extends TempoActionInfoPanel {

        public StealInfoPanel(Viewport stageViewport) {
            super(stageViewport, ActionChoice.STEAL);
        }

        @Override
        public void set(ActorCommand command) {
            if(command != null && command instanceof StealCommand && command.isTargetValid()){
                StealCommand sc = (StealCommand)command;
                IUnit target = sc.getTarget();
                builder = new StringBuilder();
                builder.append(command.getInitiator().getName()+" => "+command.getTarget().getName());
                builder.append("\nSteal rate : "+sc.getStealRate()+"%");
                builder.append("\nStealable items :");
                Array<Item> items = target.getStealableItems();
                for(int i = 0; i < items.size; i++)
                    builder.append("\n  "+items.get(i).toString());
            }
        }
    }

    public static class BuildInfoPanel extends TempoActionInfoPanel {

        public BuildInfoPanel(Viewport stageViewport) {
            super(stageViewport, ActionChoice.BUILD);
        }

        @Override
        public void set(ActorCommand command) {
            if(command != null && command instanceof BuildCommand && command.isTargetValid()){
                BuildCommand bc = (BuildCommand)command;
                builder = new StringBuilder();
                builder.append(command.getInitiator().getName());
                builder.append(bc.getTargetTile()+" => "+bc.getBuildingType());
            }
        }
    }
}