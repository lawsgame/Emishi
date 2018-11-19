package com.lawsgame.emishitactics.core.phases.battle.interactions.tempo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.interfaces.Model;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.GuardCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.HealCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.PlunderCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.WalkCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.atomic.MoveCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.event.EarthquakeEvent;
import com.lawsgame.emishitactics.core.phases.battle.commands.event.TrapEvent;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
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


    public TestBIS(BattleInteractionMachine bim) {
        super(bim, true, true, true, true, false);

        sltdUnit = bim.player.getArmy().getWarlord();
        sltdUnit.addNativeAbility(Data.Ability.BUILD);
        sltdUnit.addNativeAbility(Data.Ability.HEAL);
        bim.player.getArmy().getSquad(sltdUnit, true).get(1).takeDamage(0, false, false, 1f);

        // ANIMATION TEST

        sprites = bim.assetProvider.genSpriteTree.getSpriteSet(false, false, false, Data.UnitTemplate.SOLAR_KNIGHT, Data.WeaponType.SWORD, Data.Orientation.WEST, false, Data.AnimSpriteSetId.HEAL);
        for(int i =0; i < sprites.size; i++){
            sprites.get(i).setPosition(1, 1);
            sprites.get(i).setSize(1, 2);
        }
        animation = new Animation(sprites.size, 0.3f, true, false, false);
        animation.play();


        // TEST CUSTOMED COMMAND

        customedCommand = new HealCommand(bim.bfr, bim.scheduler, bim.player.getInventory());
        customedCommand.setFree(true);

        walkCommand = new WalkCommand(bim.bfr, bim.scheduler, bim.player.getInventory());
        walkCommand.setFree(true);

        ccActionArea = new Area(bim.battlefield, Data.AreaType.MOVE_AREA);
        ccImpactArea = new Area(bim.battlefield, Data.AreaType.FOE_ACTION_AREA);
        ccTargets = new Area(bim.battlefield, Data.AreaType.DEPLOYMENT_AREA);
        bim.bfr.addAreaRenderer(ccImpactArea);
        bim.bfr.addAreaRenderer(ccActionArea);
        bim.bfr.addAreaRenderer(ccTargets);


        Unit randomFoe = bim.battlefield.getUnit(13,8);
        randomFoe.addNativeAbility(Data.Ability.GUARD);
        int[] randomFoePos = bim.bfr.getModel().getUnitPos(randomFoe);
        GuardCommand guardCommand = new GuardCommand(bim.bfr, bim.scheduler, bim.player.getInventory());
        guardCommand.setFree(true);
        if(!guardCommand.apply(randomFoePos[0], randomFoePos[1])){ }


        // EVENT !!

        Model.Trigger trigger;
        for(int r = 0; r < bim.battlefield.getNbRows(); r++){
            for(int c = 0; c < bim.battlefield.getNbColumns(); c++){
                if(bim.battlefield.isTileOccupied(r, c)){
                    final BattleUnitRenderer woundedRenderer = bim.bfr.getUnitRenderer(bim.battlefield.getUnit(r, c));
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

        for(int r = 0; r < bim.battlefield.getNbRows(); r++){
            for(int c = 0; c < bim.battlefield.getNbColumns(); c++){
                if(bim.battlefield.getTile(r,c).getType().isUrbanArea()){
                    bim.battlefield.getTile(r,c).setFragile(true);
                }
            }
        }

        event = EarthquakeEvent.addTrigger(bim.bfr, bim.scheduler,  bim.player.getInventory(), bim.shortUnitPanel, bim.gcm, 0);



    }

    @Override
    public void end() {

    }

    @Override
    public void renderAhead(SpriteBatch batch) {
        //sprites.get(animation.getCurrentFrame()).draw(batch);
    }

    @Override
    public boolean handleTouchInput(int row, int col) {

        System.out.println("input : "+row+" "+col);
        int[] actorPos = bim.battlefield.getUnitPos(sltdUnit);
        //bim.moveCamera(row, col, true);

        // EVENT

        //WALK UNIT

        //bim.battlefield.moveUnit(actorPos[0], actorPos[1], row, col, true);

        /*
        if(!bim.battlefield.isTileOccupied(row, col)){
            moveCommand.setPath(bim.battlefield.getShortestPath(actorPos[0], actorPos[1], row, col, false, sltdUnit.getArmy().getAffiliation()));
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
        if(!bim.battlefield.isTileOccupied(row, col)) {
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
            if(bim.battlefield.isTileOccupied(row, col)){
                sltdUnit = bim.battlefield.getUnit(row, col);
            }else{
                bim.battlefield.moveUnit(actorPos[0], actorPos[1], row, col, true);
            }
        }else if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){
            if(!customedCommand.apply(actorPos[0], actorPos[1], row, col)){
                System.out.println("command failed to be applied");
                System.out.println("    initiator ? : "+customedCommand.isInitiatorValid());
                System.out.println("    target ?    : "+customedCommand.isTargetValid());
            }
        }else{
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


        return false;
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
