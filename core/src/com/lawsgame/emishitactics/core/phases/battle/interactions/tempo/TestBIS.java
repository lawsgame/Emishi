package com.lawsgame.emishitactics.core.phases.battle.interactions.tempo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.interfaces.Model;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.AttackCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.GuardCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.WalkCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.atomic.MoveCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.event.EarthquakeEvent;
import com.lawsgame.emishitactics.core.phases.battle.commands.event.TrapEvent;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels.ActionInfoPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels.ChoicePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoAIP;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoActionChoicePanel;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;
import com.lawsgame.emishitactics.engine.rendering.Animation;

import java.util.LinkedList;

public class TestBIS extends BattleInteractionState implements Observer{

    Unit sltdUnit;
    Unit foeWL;

    Array<Sprite> sprites;
    Animation animation;

    LinkedList<ActorCommand> historic = new LinkedList<ActorCommand>();

    MoveCommand moveCommand = null;
    ActorCommand customedCommand = null;
    WalkCommand walkCommand = null;
    EarthquakeEvent event = null;


    Area ccActionArea;
    Area ccImpactArea;
    Area ccTargets;

    ActionInfoPanel panel;
    ChoicePanel choicePanel;


    public TestBIS(BattleInteractionMachine bim) {
        super(bim, true, true, true, true, false);

        sltdUnit = bim.player.getArmy().getWarlord();
        sltdUnit.addNativeAbility(Data.Ability.BUILD);
        sltdUnit.addNativeAbility(Data.Ability.HEAL);
        bim.player.getArmy().getSquad(sltdUnit, true).get(1).takeDamage(0, false, false, 1f);





        // ANIMATION TEST

        sprites = bim.provider.genSpriteTree.getSpriteSet(false, false, false, Data.UnitTemplate.SOLAR_KNIGHT, Data.WeaponType.SWORD, Data.Orientation.WEST, false, Data.AnimSpriteSetId.HEAL);
        for(int i =0; i < sprites.size; i++){
            sprites.get(i).setPosition(1, 1);
            sprites.get(i).setSize(1, 2);
        }
        animation = new Animation(sprites.size, 0.3f, true, false, false);
        animation.play();






        // TEST CUSTOMED COMMAND

        customedCommand = new AttackCommand(bim.bfr, bim.scheduler, bim.player.getInventory());
        customedCommand.setFree(true);

        walkCommand = new WalkCommand(bim.bfr, bim.scheduler, bim.player.getInventory());
        walkCommand.setFree(true);

        ccActionArea = new Area(bim.bfr.getModel(), Data.AreaType.MOVE_AREA);
        ccImpactArea = new Area(bim.bfr.getModel(), Data.AreaType.FOE_ACTION_AREA);
        ccTargets = new Area(bim.bfr.getModel(), Data.AreaType.DEPLOYMENT_AREA);
        bim.bfr.addAreaRenderer(ccImpactArea);
        bim.bfr.addAreaRenderer(ccActionArea);
        bim.bfr.addAreaRenderer(ccTargets);

        // set guarded area
        Unit randomFoe = bim.bfr.getModel().getUnit(13,8);
        randomFoe.addNativeAbility(Data.Ability.GUARD);
        int[] randomFoePos = bim.bfr.getModel().getUnitPos(randomFoe);
        GuardCommand guardCommand = new GuardCommand(bim.bfr, bim.scheduler, bim.player.getInventory());
        guardCommand.setFree(true);
        if(!guardCommand.apply(randomFoePos[0], randomFoePos[1])){ }

        // initiate action panel
        Skin skin = bim.asm.get(Assets.SKIN_UI);
        panel = ActionInfoPanel.create(bim.uiStage.getViewport(), skin, bim.bfr, TempoAIP.class);

        /*
        SwitchWeaponCommand command = new SwitchWeaponCommand(bim.bfr, bim.scheduler, bim.player.getInventory(), 1);
        int[] sltdPos = bim.bfr.getModel().getUnitPos(sltdUnit);
        command.setInitiator(sltdPos[0], sltdPos[1]);
        command.setTarget(sltdPos[0], sltdPos[1]);
        panel.setContent(command);
        */

        bim.uiStage.addActor(panel);
        //panel.show();





        // EVENT !!

        Model.Trigger trigger;
        for(int r = 0; r < bim.bfr.getModel().getNbRows(); r++){
            for(int c = 0; c < bim.bfr.getModel().getNbColumns(); c++){
                if(bim.bfr.getModel().isTileOccupied(r, c)){
                    final BattleUnitRenderer woundedRenderer = bim.bfr.getUnitRenderer(bim.bfr.getModel().getUnit(r, c));
                    trigger = new Model.Trigger(true, new BattleCommand(bim.bfr, bim.scheduler, bim.player.getInventory()) {

                        @Override
                        protected void execute() {
                            System.out.println("Remember the name of the one you killed that day : "+woundedRenderer.getModel().getName());
                        }

                        @Override
                        protected void unexecute() {

                        }

                        @Override
                        public boolean isApplicable() {
                            return true;
                        }

                        @Override
                        public boolean isUndoable() {
                            return false;
                        }
                    }) {


                        @Override
                        public boolean isTriggerable(Object data) {
                            return woundedRenderer.getModel().isOutOfAction();
                        }
                    };
                    woundedRenderer.getModel().add(trigger);

                    if(woundedRenderer.getModel().isWarlord() && woundedRenderer.getModel().getArmy().getAffiliation() == Data.Affiliation.ENEMY_0){
                        foeWL = woundedRenderer.getModel();
                    }
                }
            }
        }

        TrapEvent.addTrigger(bim, 11, 4, 3);

        for(int r = 0; r < bim.bfr.getModel().getNbRows(); r++){
            for(int c = 0; c < bim.bfr.getModel().getNbColumns(); c++){
                if(bim.bfr.getModel().getTile(r,c).getType().isUrbanArea()){
                    bim.bfr.getModel().getTile(r,c).setFragile(true);
                }
            }
        }

        event = EarthquakeEvent.addTrigger(bim.bfr, bim.scheduler,  bim.player.getInventory(), bim.bfr.getGCM(), 0);


        choicePanel = new TempoActionChoicePanel(bim.uiStage.getViewport(), skin);
        choicePanel.setContent(new ChoicePanel.ButtonHandler() {
            @Override
            public Array<TextButton> getButtons(Skin skin) {
                Array<TextButton> buttons = new Array<TextButton>();
                buttons.add(new TextButton("button 1", skin, "default"));
                buttons.add(new TextButton("button 2", skin, "default"));
                buttons.add(new TextButton("button 3", skin, "default"));
                buttons.add(new TextButton("button 4", skin, "default"));
                return buttons;
            }
        });
        choicePanel.show();
        bim.uiStage.addActor(choicePanel);



    }

    @Override
    public void end() {

    }

    @Override
    public void renderAhead(SpriteBatch batch) {
        //sprites.get(animation.getCurrentFrame()).draw(batch);
        choicePanel.getX();
    }


    @Override
    public boolean handleTouchInput(int row, int col) {

        System.out.println("input : "+row+" "+col);
        int[] actorPos = bim.bfr.getModel().getUnitPos(sltdUnit);
        //bim.moveCameraTo(row, col, true);



        //WALK UNIT

        //bim.bfr.getModel().moveUnit(actorPos[0], actorPos[1], row, col, true);

        /*
        if(!bim.bfr.getModel().isTileOccupied(row, col)){
            moveCommand.setPath(bim.bfr.getModel().getShortestPath(actorPos[0], actorPos[1], row, col, false, sltdUnit.getArmy().getAffiliation()));
            moveCommand.setReveal(false);
            if(!moveCommand.apply(actorPos[0], actorPos[1])){
                System.out.println("command failed to be applied");
                System.out.println("    initiator ? : "+customedCommand.isInitiatorValid());
                System.out.println("    target ?    : "+customedCommand.isTargetValid());
            }
            //System.out.println(bim.scheduler);
        }
        */


        /*
        if(!bim.bfr.getModel().isTileOccupied(row, col)) {
            if (!walkCommand.apply(actorPos[0], actorPos[1], row, col)) {
                System.out.println("command failed to be applied");
                System.out.println("    initiator ? : " + walkCommand.isInitiatorValid());
                System.out.println("    target ?    : " + walkCommand.isTargetValid());
            }
        }
        */




        // TEST CUSTOMED COMMAND

        /*
        customedCommand.init();
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){

            // MOVE SLTD UNIT

            if(bim.bfr.getModel().isTileOccupied(row, col)){
                sltdUnit = bim.bfr.getModel().getUnit(row, col);
            }else{
                bim.bfr.getModel().moveUnit(actorPos[0], actorPos[1], row, col, true);
            }
        }else if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){

            // APPLY CUSTOMED COMMAND

            // set parameters
            customedCommand.setInitiator(actorPos[0], actorPos[1]);
            customedCommand.setTarget(row, col);

            // update and display action pan
            if(bim.pp.isActionPanelAvailable(customedCommand.getActionChoice())) {
                panel.hide();
                panel.setContent(customedCommand);
                panel.show();
            }

            // apply command
            if(!customedCommand.apply()){
                System.out.println("command failed to be applied");
                System.out.println("    initiator ? : "+customedCommand.isInitiatorValid());
                System.out.println("    target ?    : "+customedCommand.isTargetValid());
            }
        }else{

            // SHOW INFO ABOUT COMMAND

            customedCommand.setInitiator(actorPos[0], actorPos[1]);
            //customedCommand.setFree(true);
            if(customedCommand.isInitiatorValid()) {
                //customedCommand.setFree(false);
                Array<int[]> impact = customedCommand.getImpactArea(actorPos[0], actorPos[1], row, col);
                ccImpactArea.setTiles(impact, true);
                ccActionArea.setTiles(customedCommand.getActionArea(), true);
                ccTargets.setTiles(customedCommand.getTargetsAtRange(), true);

            }
        }
        */





        return true;
    }

    @Override
    public void update60(float dt) {
        animation.update(dt);

        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)){
            event.setDecoupled(false);
            if(!event.apply()){
                System.out.println("fail to be applied "+event.isApplicable());
            }else{
                System.out.println("applied");
            }

        }


    }


    @Override
    public void init() {

    }

    @Override
    public void getNotification(Observable sender, Object data) {
        if(sender == data)
            sender.detach(this);
    }
}
