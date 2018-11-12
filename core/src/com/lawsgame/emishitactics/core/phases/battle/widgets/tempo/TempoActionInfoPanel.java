package com.lawsgame.emishitactics.core.phases.battle.widgets.tempo;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.helpers.TempoSpritePool;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Weapon;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.BattlePhase;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.AttackCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.BuildCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.HealCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.StealCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.SwitchWeaponCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.atomic.HitCommand;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ActionInfoPanel;

import java.util.LinkedList;

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



    public static class AttackInfoPanel extends TempoActionInfoPanel {

        private LinkedList<HitCommand.DefenderData> initiatorAsAttacker;
        private Array<HitCommand.DefenderData> initiatorAsTarget;

        public AttackInfoPanel(Viewport stageViewport) {
            super(stageViewport, ActionChoice.ATTACK);
            this.initiatorAsAttacker = new LinkedList<HitCommand.DefenderData>();
            this.initiatorAsTarget = new Array<HitCommand.DefenderData>();

            setTouchable(Touchable.enabled);
            addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    hide();
                    next();
                    show();
                    return true;
                }
            });
        }


        @Override
        public void set(ActorCommand command) {
            initiatorAsAttacker.clear();
            initiatorAsTarget.clear();

            if(command != null && command instanceof AttackCommand && command.isApplicable()) {
                setHeight(220);

                AttackCommand currentCommand = (AttackCommand)command;
                for(int i = 0; i < currentCommand.getInitialBlow().getDefenderData().size; i++){
                    initiatorAsAttacker.offer(currentCommand.getInitialBlow().getDefenderData().get(i));
                }
                for(int i = 0; i < currentCommand.getRetalationBlows().size; i++){
                    if(currentCommand.getRetalationBlows().get(i).getDefenderData().size > 0) {
                        initiatorAsTarget.add(currentCommand.getRetalationBlows().get(i).getDefenderData().get(0));
                    }
                }

                next();

            }
        }

        public void next(){
            builder = new StringBuilder();


            if(initiatorAsAttacker.size() > 0) {

                HitCommand.DefenderData data = initiatorAsAttacker.peek();

                builder.append(data.attacker.getName());
                builder.append(" => ") ;
                builder.append(data.defenderRenderer.getModel().getName());
                if(data.isTargetGuarded()) {
                    builder.append(" [ ");
                    builder.append(data.targetRenderer.getModel().getName());
                    builder.append(" ] ");
                }
                builder.append("\nMoral / HP : ");
                builder.append(data.defenderRenderer.getModel().getCurrentMoral());
                builder.append(" - ");
                builder.append(data.defenderRenderer.getModel().getCurrentHP());
                builder.append("\nDamage : " + data.damageDealt[0]+" - "+ data.damageDealt[1]);
                builder.append("\nHit rate : " + data.hitrate+"%");
                builder.append("\nLoot rate : " + data.lootRate+"%");


                for(int i = 0; i < initiatorAsTarget.size; i++){
                    if(initiatorAsTarget.get(i).attacker == data.targetRenderer.getModel()){

                        data = initiatorAsTarget.get(i);

                        builder.append("\n\n");
                        builder.append(data.attacker.getName());
                        builder.append(" => ") ;
                        builder.append(data.defenderRenderer.getModel().getName());
                        if(data.isTargetGuarded()) {
                            builder.append(" [ ");
                            builder.append(data.targetRenderer.getModel().getName());
                            builder.append(" ] ");
                        }
                        builder.append("\nMoral / HP : ");
                        builder.append(data.defenderRenderer.getModel().getCurrentMoral());
                        builder.append(" - ");
                        builder.append(data.defenderRenderer.getModel().getCurrentHP());
                        builder.append("\nDamage : " + data.damageDealt[0]+" - "+ data.damageDealt[1]);
                        builder.append("\nHit rate : " + data.hitrate+"%");
                        builder.append("\nLoot rate : " + data.lootRate+"%");

                        break;
                    }
                }

                initiatorAsAttacker.offer(initiatorAsAttacker.pop());
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

                Weapon cw = command.getInitiator().getCurrentWeapon();
                Weapon nw = swc.getInitiator().getWeapon(swc.getWeaponIndex());
                builder = new StringBuilder();
                builder.append(command.getInitiator().getName());
                builder.append("\nName        : "+cw+" => "+nw);
                builder.append("\nMight       : "+cw.getTemplate().getDamageMin()+" - "+cw.getTemplate().getDamageMax()+" => "+nw.getTemplate().getDamageMin()+" - "+nw.getTemplate().getDamageMax());
                builder.append("\nAccuracy    : "+cw.getTemplate().getAccuracy()+" => "+nw.getTemplate().getAccuracy());
                builder.append("\nRange       : "+cw.getTemplate().getRangeMin()+" - "+cw.getTemplate().getRangeMax()+" => "+nw.getTemplate().getRangeMin()+" - "+nw.getTemplate().getRangeMax());
                builder.append("\nDurability  : "+cw.getDurability()+"/"+cw.getTemplate().getDurabilityMax()+" => "+nw.getDurability()+"/"+cw.getTemplate().getDurabilityMax());
                builder.append("\nDamage type : "+cw.getTemplate().getDamageType()+" => "+nw.getTemplate().getDamageType());
                builder.append("\nability     : "+cw.getTemplate().getAbility().name().toLowerCase()+" => "+nw.getTemplate().getAbility().name().toLowerCase());
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