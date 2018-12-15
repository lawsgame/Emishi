package com.lawsgame.emishitactics.core.phases.battle.interactions.tempo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.CoveringFireCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.GuardCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.WalkCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.atomic.MoveCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.event.EarthquakeEvent;
import com.lawsgame.emishitactics.core.phases.battle.commands.event.TrapEvent;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.renderers.IsoBFR;
import com.lawsgame.emishitactics.core.phases.battle.renderers.IsoTileRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.TileRenderer;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.interfaces.ActionInfoPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.interfaces.ChoicePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.tempo.TempoAIP;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;
import com.lawsgame.emishitactics.engine.rendering.Animation;

import java.util.LinkedList;

public class TestBIS extends BattleInteractionState implements Observer, ChoicePanel.CommandReceiver {

    Unit sltdUnit;

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
    ChoicePanel.CommandChoicePanel ccp;



    public TestBIS(BattleInteractionMachine bim) {
        super(bim, true, true, true, true, false);

        IsoBFR bfr = ((IsoBFR)bim.bfr);
        sltdUnit = bim.player.getArmy().getWarlord();
        sltdUnit.addNativeAbility(Data.Ability.BUILD);
        sltdUnit.addNativeAbility(Data.Ability.HEAL);
        bim.player.getArmy().getSquad(sltdUnit, true).get(1).takeDamage(0, false, false, 1f);



        // -------***<<< ANIMATION tests >>>***------------------------

        sprites = bfr.assetProvider.genSpriteTree.getSpriteSet(false, false, false, Data.UnitTemplate.SOLAR_KNIGHT, Data.WeaponType.SWORD, Data.Orientation.WEST, false, Data.AnimSpriteSetId.HEAL);
        for(int i =0; i < sprites.size; i++){
            sprites.get(i).setPosition(1, 1);
            sprites.get(i).setSize(1, 2);
        }
        animation = new Animation(sprites.size, 0.3f, true, false, false);
        animation.play();



        // -------***<<< CUSTOMED COMMAND related tests >>>***------------------------

        customedCommand = new WalkCommand(bim.bfr, bim.scheduler, bim.player.getInventory());
        customedCommand.setFree(true);

        walkCommand = new WalkCommand(bim.bfr, bim.scheduler, bim.player.getInventory());
        walkCommand.setFree(true);

        ccActionArea = new Area(bim.bfr.getModel(), Data.AreaType.MOVE_AREA);
        ccImpactArea = new Area(bim.bfr.getModel(), Data.AreaType.FOE_ACTION_AREA);
        ccTargets = new Area(bim.bfr.getModel(), Data.AreaType.DEPLOYMENT_AREA);
        bim.bfr.addAreaRenderer(ccImpactArea);
        bim.bfr.addAreaRenderer(ccActionArea);
        bim.bfr.addAreaRenderer(ccTargets);

        // INITIATE ACTION PANEL

        final Skin skin = bim.asm.get(Assets.SKIN_UI);
        panel = ActionInfoPanel.create(bim.uiStage.getViewport(), skin, bim.bfr, TempoAIP.class);
        bim.uiStage.addActor(panel);

        // AREA ADDING

        // set guarded area
        Unit randomFoe = bim.bfr.getModel().getUnit(13,8);
        randomFoe.addNativeAbility(Data.Ability.GUARD);
        int[] randomFoePos = bim.bfr.getModel().getUnitPos(randomFoe);
        GuardCommand guardCommand = new GuardCommand(bim.bfr, bim.scheduler, bim.player.getInventory());
        guardCommand.setFree(true);
        if(!guardCommand.apply(randomFoePos[0], randomFoePos[1])){ }

        // set covered area
        randomFoe = bim.bfr.getModel().getUnit(13,5);
        randomFoePos = bim.bfr.getModel().getUnitPos(randomFoe);
        CoveringFireCommand cfc = new CoveringFireCommand(bim.bfr, bim.scheduler, bim.player.getInventory());
        cfc.setFree(true);
        if(!cfc.apply(randomFoePos[0], randomFoePos[1])){
            System.out.println("command failed to be applied");
            System.out.println("    initiator ? : " + walkCommand.isInitiatorValid());
            System.out.println("    target ?    : " + walkCommand.isTargetValid());
        }

        /*
        SwitchWeaponCommand command = new SwitchWeaponCommand(bim.bfr, bim.scheduler, bim.player.getInventory(), 1);
        int[] sltdPos = bim.bfr.getModel().getUnitPos(sltdUnit);
        command.setInitiator(sltdPos[0], sltdPos[1]);
        command.setTarget(sltdPos[0], sltdPos[1]);
        panel.setContent(command);
        */

        //panel.show();





        // -------***<<< EVENT related tests >>>***------------------------

        TrapEvent.addTrigger(bim, 11, 4, 3);
        event = EarthquakeEvent.addTrigger(bim.bfr, bim.scheduler,  bim.player.getInventory(), bim.bfr.getGCM(), -1);
        //bim.bfr.getModel().getTile(10, 7).setFragile(true);
        event.getTargetTileTree().addChild(10, 7,1);
        event.getTargetTileTree().addChild(13, 13, 1f);



        // -------***<<< ACP and CCP tests >>>***------------------------

        int[] actorPos = bim.bfr.getModel().getUnitPos(sltdUnit);

        // TEST 0

        /*
        for(Data.Ability a : sltdUnit.getAbilities())
            System.out.println(a.name());

        bim.pp.choicePanel.attach(this);
        bim.pp.choicePanel.setContent(actorPos[0], actorPos[1], bim.bcm, new Stack<ActorCommand>());
        bim.pp.choicePanel.show();
        */

        //TEST 1


        /*
        ccp = new TempoCommandCP(bim.uiStage.getViewport(),skin);

        //SlidingPanel gp = new SlidingPanel(bim.uiStage.getViewport(),0.5f , 0,0 ,0 , 0, true, true) {};
        //SlidingPanel gp = new SlidingPanel(bim.uiStage.getViewport(),0.5f , 10,0 ,0 , 0, true, true) {};
        //SlidingPanel gp = new SlidingPanel(bim.uiStage.getViewport(),0.5f , 10,10 ,0 , 0, true, true) {};
        SlidingPanel gp = new SlidingPanel(bim.uiStage.getViewport(),0.5f , 10,10 ,150 , 0, true, true) {};
        //SlidingPanel gp = new SlidingPanel(bim.uiStage.getViewport(),0.5f , 10,10 ,150 , 0, true, false) {};
        //SlidingPanel gp = new TempoActionCP(bim.uiStage.getViewport(), skin);
        bim.uiStage.addActor(gp);
        gp.addActor(ccp);

        //bim.uiStage.addActor(ccp);
        //ccp.attach(this);

        //choicePanel.addActor(ccp);
        //ccp.attach(choicePanel);

        ccp.setButtonIndex(0);
        ccp.setContent(actorPos[0], actorPos[1], bim.bcm, Data.ActionChoice.BUILD);
        ccp.show();


        System.out.println(bim.uiStage.getViewport().getWorldHeight());
        System.out.println(ccp.getY());
        */



        // -------***<<< WINDROSE >>>***------------------------

        bim.windrose.initialize(bim.bfr.getUnitRenderer(sltdUnit));

        /*
        TextureAtlas atlas = bim.asm.get(Assets.ATLAS_BATTLE_ICONS);
        regions = atlas.findRegions(Assets.getWindroseArrowTexture(true, true));
        //regions = atlas.findRegions("arrow_north_active");
        System.out.println("  region size : "+regions.size);
        animation = new Animation(regions.size, 0.2f, true, true, false);
        animation.play();
        */



        // -------***<<< SPARKLE ANIM TEST >>>***------------------------

        Sprite sprite;
        Array<TextureRegion> sparkleTR = bfr.assetProvider.sparkleTR.get(Data.SparkleType.TRAP);
        sprites = new Array<Sprite>();
        for(int i = 0; i < sparkleTR.size; i++){
            sprite = new Sprite(sparkleTR.get(i));
            sprite.setSize(2, 2 );
            sprite.setPosition(2, 2);
            sprites.add(sprite);
        }
        animation = new Animation(sprites.size, Data.ANIMATION_NORMAL_SPEED, true, true, false);
        animation.play();
    }


    @Override
    public void end() {

    }

    @Override
    public void renderAhead(SpriteBatch batch) {
        //sprites.get(animation.getCurrentFrame()).draw(batch);
    }


    @Override
    public boolean handleTouchInput(float xTouch, float yTouch) {
        return false;
    }

    @Override
    public boolean handleTouchInput(int row, int col) {

        System.out.println("input : "+row+" "+col);
        int[] actorPos = bim.bfr.getModel().getUnitPos(sltdUnit);
        //bim.moveCameraTo(row, col, true);


        // PANEL

        /*
        if(bim.pp.choicePanel.isHiding()){
            bim.pp.choicePanel.show();
        }else {
            bim.pp.choicePanel.resetPanel(true);
        }
        */


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






        return true;
    }

    @Override
    public void update60(float dt) {
        animation.update(dt);
        bim.windrose.update(dt);


        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            event.setDecoupled(false);
            event.apply();
            event.setDecoupled(true);
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.H)){
            TileRenderer tr = bim.bfr.getTileRenderer(15, 5);
            tr.setRevealed(!tr.isRevealed(), Data.SparkleType.LOOT);
            System.out.println("sparkle revealed ? "+tr.isRevealed());
        }
    }


    @Override
    public void init() {

    }

    @Override
    public void getNotification(Observable sender, Object data) {
        if(sender == data) {
            sender.detach(this);
        }else if(data instanceof Data.Orientation){
            System.out.println(data);
        }
    }

    @Override
    public void getChoicePanelNotification(ActorCommand choice) {
        System.out.println(choice.toShortString());
    }
}
