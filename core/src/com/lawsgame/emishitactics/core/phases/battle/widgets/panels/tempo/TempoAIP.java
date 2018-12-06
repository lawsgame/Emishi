package com.lawsgame.emishitactics.core.phases.battle.widgets.panels.tempo;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.Weapon;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.AttackCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.BuildCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.HealCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.StealCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.SwitchWeaponCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.atomic.HitCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.interfaces.ActionInfoPanel;

public class TempoAIP extends ActionInfoPanel {
    private static float X_PADDING = 15f;
    private static float Y_PADDING = 15f;
    private static int WIDTH = 270;
    private static int HEIGHT = 320;
    private static float SLIDE_DURATION = 0.5f;

    protected String tag;
    private int actorIndex;
    private int targetIndex;
    protected Array<int[]> actorPositions;
    protected Array<Array<int[]>> targetPositions;
    protected Array<Array<Label>> labels;

    protected Skin skin;
    protected ActorCommand command;
    private InputListener listener;

    public TempoAIP(Viewport uiport, Skin skin, final BattlefieldRenderer bfr) {
        super(uiport, SLIDE_DURATION, X_PADDING, Y_PADDING, WIDTH, HEIGHT, false, false);
        this.tag = "";
        this.skin = skin;
        this.actorIndex = 0;
        this.targetIndex = 0;
        this.labels = new Array<Array<Label>>();
        this.actorPositions = new Array<int[]>();
        this.targetPositions = new Array<Array<int[]>>();
        this.listener = new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(multipleSubPanels()) {
                    hide();
                    next(true, bfr);
                    show();
                }
                return true;
            }
        };

    }

    private boolean multipleSubPanels(){
        return labels.size > 1 || (labels.size == 1 && labels.get(0).size > 1);
    }


    @Override
    protected final int[] updatePanel(boolean nextTarget) {
        int[] pos = null;

        // set the indexes
        if(labels.size > 0 && labels.get(0).size > 0) {
            if (nextTarget) {
                targetIndex = (targetIndex + 1) % labels.get(actorIndex).size;
            } else {
                actorIndex = (actorIndex + 1) % labels.size;
            }
            pos = (nextTarget) ? targetPositions.get(actorIndex).get(targetIndex) : actorPositions.get(actorIndex);
        }

        //reset panel content accordingly
        setPanel();

        return pos;
    }

    protected void setPanel(){

        clear();

        // set visible content
        setBackground(skin.getDrawable("black_background"));
        if(labels.size > 0 && labels.get(0).size > 0) {
            add(labels.get(actorIndex).get(targetIndex)).center();
        }else{
            add(new Label("ACTION", skin, "default")).center();
            try{
                throw new PanelException("Content of the action panel is not set properly or at all");
            }catch (PanelException e){
                e.printStackTrace();
            }
        }

        // add listener
        setTouchable(Touchable.enabled);
        addListener(listener);


        // display action panel data structure
        System.out.println(this);

    }


    protected void add(int[] actorPos, int[] targetPos, Label label){

        if(actorPos != null && targetPos != null && label != null) {

            // fetch actor index and store actor position if not yet done
            int actorIndex = -1;
            if (!Utils.arrayContains(actorPositions, actorPos)) {
                actorPositions.add(actorPos);
                targetPositions.add(new Array<int[]>());
                labels.add(new Array<Label>());
                actorIndex = actorPositions.size - 1;
            } else {
                for (int i = 0; i < actorPositions.size; i++) {
                    if (actorPositions.get(i)[0] == actorPos[0] && actorPositions.get(i)[1] == actorPos[1]) {
                        actorIndex = i;
                    }
                }
            }

            // remove previous label / target pos if a label has been stored for this tuple (actorPos, targetPos)
            if (Utils.arrayContains(targetPositions.get(actorIndex), targetPos)) {
                int removedTargetIndex = Utils.arrayRemove(targetPositions.get(actorIndex), targetPos[0], targetPos[1]);
                labels.get(actorIndex).removeIndex(removedTargetIndex);
            }

            // update targetPositions
            targetPositions.get(actorIndex).add(targetPos);
            labels.get(actorIndex).add(label);
        }
    }



    @Override
    public void setContent(ActorCommand command) {

        if(command instanceof AttackCommand){
            if(command.isApplicable()){

                AttackCommand currentCommand = (AttackCommand)command;

                StringBuilder builder = new StringBuilder();
                if(currentCommand.getDefenderData().size > 0) {
                    HitCommand.DefenderData data = currentCommand.getDefenderData().get(0);


                    builder.append(data.attacker.getName());
                    builder.append(" => ");
                    builder.append(data.defenderRenderer.getModel().getName());
                    if (data.isTargetGuarded()) {
                        builder.append(" [ ");
                        builder.append(data.targetRenderer.getModel().getName());
                        builder.append(" ] ");
                    }
                    builder.append("\nMoral / HP : ");
                    builder.append(data.defenderRenderer.getModel().getCurrentMoral());
                    builder.append(" - ");
                    builder.append(data.defenderRenderer.getModel().getCurrentHitPoints());
                    builder.append("\nPhysical damage : " + data.damageDealt[0] + " - " + data.damageDealt[1]);
                    builder.append("\nMoral damage : " + data.damageDealt[0] * data.moralModifier + " - " + data.damageDealt[1] * data.moralModifier);
                    builder.append("\nHit rate : " + data.hitrate + "%");
                    builder.append("\nAP cost : " + data.APCost);
                    builder.append("\nLoot rate : " + data.lootRate + "%");

                    if (currentCommand.getRetalationBlow().isApplicable()
                            && currentCommand.getRetalationBlow().getDefenderData().size > 0) {
                        data = currentCommand.getRetalationBlow().getDefenderData().get(0);

                        builder.append("\n\n");
                        builder.append(data.attacker.getName());
                        builder.append(" => ");
                        builder.append(data.defenderRenderer.getModel().getName());
                        if (data.isTargetGuarded()) {
                            builder.append(" [ ");
                            builder.append(data.targetRenderer.getModel().getName());
                            builder.append(" ] ");
                        }
                        builder.append("\nMoral / HP : ");
                        builder.append(data.defenderRenderer.getModel().getCurrentMoral());
                        builder.append(" - ");
                        builder.append(data.defenderRenderer.getModel().getCurrentHitPoints());
                        builder.append("\nPhysical damage : " + data.damageDealt[0] + " - " + data.damageDealt[1]);
                        builder.append("\nMoral damage : " + data.damageDealt[0] * data.moralModifier + " - " + data.damageDealt[1] * data.moralModifier);
                        builder.append("\nHit rate : " + data.hitrate + "%");
                        builder.append("\nAP cost : " + data.APCost);
                        builder.append("\nLoot rate : " + data.lootRate + "%");

                    }
                }

                int[] initiatorPos = new int[]{command.getRowinitiator(), command.getColInitiator()};
                int[] targetPos = new int[]{command.getRowTarget(), command.getColTarget()};
                add(initiatorPos, targetPos, new Label(builder.toString(), skin, "default"));


            }
        } else if(command instanceof SwitchWeaponCommand){
            if(command.isApplicable()){

                // SWITCH WEAPON

                SwitchWeaponCommand swc = (SwitchWeaponCommand)command;

                Weapon cw = command.getInitiator().getCurrentWeapon();
                Weapon nw = swc.getNewWeapon();
                StringBuilder builder = new StringBuilder();
                builder.append(command.getInitiator().getName());
                builder.append("\nName        : "+cw+" => "+nw);
                builder.append("\nMight       : "+cw.getTemplate().getDamageMin()+" - "+cw.getTemplate().getDamageMax()+" => "+nw.getTemplate().getDamageMin()+" - "+nw.getTemplate().getDamageMax());
                builder.append("\nAccuracy    : "+cw.getTemplate().getAccuracy()+" => "+nw.getTemplate().getAccuracy());
                builder.append("\nRange       : "+cw.getTemplate().getRangeMin()+" - "+cw.getTemplate().getRangeMax()+" => "+nw.getTemplate().getRangeMin()+" - "+nw.getTemplate().getRangeMax());
                builder.append("\nDurability  : "+cw.getDurability()+"/"+cw.getTemplate().getDurabilityMax()+" => "+nw.getDurability()+"/"+cw.getTemplate().getDurabilityMax());
                builder.append("\nDamage type : "+cw.getTemplate().getDamageType()+" => "+nw.getTemplate().getDamageType());
                builder.append("\nability     : "+cw.getTemplate().getAbility().name().toLowerCase()+" => "+nw.getTemplate().getAbility().name().toLowerCase());

                int[] initiatorPos = new int[]{command.getRowinitiator(), command.getColInitiator()};
                add(initiatorPos, initiatorPos, new Label(builder.toString(), skin, "default"));

            }
        }else if(command instanceof  StealCommand){
            if(command.isApplicable()) {

                // STEAL

                StealCommand sc = (StealCommand) command;
                Unit target = sc.getTarget();
                StringBuilder builder = new StringBuilder();
                builder.append(command.getInitiator().getName() + " => " + command.getTarget().getName());
                builder.append("\nSteal rate : " + sc.getStealRate() + "%");
                builder.append("\nStealable items :");
                Array<Item> items = target.getStealableItems();
                for (int i = 0; i < items.size; i++)
                    builder.append("\n  " + items.get(i).toString());

                int[] stealerPos = new int[]{command.getRowinitiator(), command.getColInitiator()};
                int[] stolenPos = new int[]{command.getRowTarget(), command.getColTarget()};
                add(stealerPos, stolenPos, new Label(builder.toString(), skin, "default"));
            }
        }else if(command instanceof BuildCommand){
            if(command.isApplicable()) {

                // BUILD

                BuildCommand bc = (BuildCommand) command;
                StringBuilder builder = new StringBuilder();
                builder.append("\n"+command.getInitiator().getName());
                builder.append("\n"+bc.getTargetTile() + " => " + bc.getBuildingType());

                int[] builderPos = new int[]{command.getRowinitiator(), command.getColInitiator()};
                int[] targetedTilePos = new int[]{command.getRowTarget(), command.getColTarget()};
                add(builderPos, targetedTilePos, new Label(builder.toString(), skin, "default"));
            }
        }else if(command instanceof HealCommand) {

            System.out.println("HEAL COMMAND RECOGN");
            System.out.println("    initiator valid ? "+command.isInitiatorValid());
            System.out.println("    target valid ? "+command.isTargetValid());

            if(command.isApplicable()){

                System.out.println("APPLICABLE");

                // HEAL

                int[] healerPos = new int[]{command.getRowinitiator(), command.getColInitiator()};
                HealCommand hc = (HealCommand)command;
                StringBuilder builder = new StringBuilder();

                Unit patient;
                int[] patientPos;
                int HPAfterHealing;
                int MoralAfterHealing;
                for(int i = 0; i < hc.getPatients().length; i++){
                    patient = hc.getPatients()[i];
                    System.out.println("recovered HP : " +hc.getRecoveredHitPoints()[i]);
                    HPAfterHealing = patient.getCurrentHitPoints() + hc.getRecoveredHitPoints()[i];
                    MoralAfterHealing = patient.getCurrentMoral() + hc.getRecoveredMoralPoints()[i];

                    builder.append(command.getInitiator().getName()+" => "+patient.getName());
                    builder.append("\nHP : "+patient.getCurrentHitPoints()+" -> "+HPAfterHealing+ " /"+patient.getAppHitpoints());
                    builder.append("\nMP : "+patient.getCurrentMoral()+" -> "+MoralAfterHealing+" /"+patient.getAppMoral());

                    patientPos = command.getBattlefield().getUnitPos(patient);
                    add(healerPos, patientPos, new Label(builder.toString(), skin, "default"));
                    builder.setLength(0);
                }
            }
        }


        setPanel();
    }

    public String toString(){
        String str = "\nACTION PANEL : "+tag;
        for(int i = 0; i < targetPositions.size; i++){
            str += "\n     actor pos : ("+actorPositions.get(i)[0]+" "+actorPositions.get(i)[1]+")";
            for(int j = 0; j < targetPositions.get(i).size; j++){
                str += "\n         target pos : ("+targetPositions.get(i).get(j)[0]+" "+targetPositions.get(i).get(j)[1]+")";
                str += " => label stored ?"+ (labels.get(i).get(j) != null);
            }
        }
        return str;
    }


    // -------------- IMPLEMENTATION -----------------------------------------

    public static class TestTempoAIP extends TempoAIP{


        public TestTempoAIP(Viewport uiport, Skin skin, BattlefieldRenderer bfr) {
            super(uiport, skin, bfr);
        }

        @Override
        public void setContent(ActorCommand command) {

            add(new int[]{11, 11}, new int[]{11, 12}, new Label("Target 1", skin, "default"));
            add(new int[]{11, 11}, new int[]{10, 11}, new Label("Target 2", skin, "default"));
            add(new int[]{11, 11}, new int[]{12, 11}, new Label("Removed Target", skin, "default"));
            add(new int[]{11, 11}, new int[]{12, 11}, new Label("Target 3", skin, "default"));

            setPanel();
        }
    }

}
