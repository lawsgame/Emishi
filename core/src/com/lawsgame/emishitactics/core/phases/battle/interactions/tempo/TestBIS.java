package com.lawsgame.emishitactics.core.phases.battle.interactions.tempo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.models.interfaces.Trigger;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.EventCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.AttackCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.GuardCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.atomic.MoveCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.renderers.IsoBFR;
import com.lawsgame.emishitactics.core.phases.battle.renderers.IsoUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.trigger.UponDisappearingTrigger;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;
import com.lawsgame.emishitactics.engine.rendering.Animation;

import java.util.LinkedList;

public class TestBIS extends BattleInteractionState implements Observer{

    IUnit sltdUnit;
    IUnit foeWL;

    Array<Sprite> sprites;
    Animation animation;

    LinkedList<ActorCommand> historic = new LinkedList<ActorCommand>();

    MoveCommand moveCommand = null;
    ActorCommand customedCommand = null;
    Area ccActionArea;
    Area ccImpactArea;
    Area ccTargets;


    public TestBIS(BattleInteractionMachine bim) {
        super(bim, true, true, true, true, false);

        sltdUnit = bim.player.getArmy().getWarlord();
        sltdUnit.applyDamage(4, false);

        sprites = bim.assetProvider.genSpriteTree.getSpriteSet(false, false, false, Data.UnitTemplate.SOLAR_KNIGHT, Data.WeaponType.SWORD, Data.Orientation.WEST, false, Data.AnimSpriteSetId.HEAL);
        for(int i =0; i < sprites.size; i++){
            sprites.get(i).setPosition(1, 1);
            sprites.get(i).setSize(1, 2);
        }
        animation = new Animation(sprites.size, 0.3f, true, false, false);
        animation.play();


        // TEST CUSTOMED COMMAND

        //customedCommand = new SwitchPositionCommand(bim.bfr, bim.scheduler, bim.player.getInventory());

        /*
        HitCommand hitCommand = new HitCommand(bim.bfr, Data.ActionChoice.TEST_CHOICE, bim.scheduler, bim.player.getInventory());
        sltdUnit.setActionPoints(100);
        hitCommand.setFree(true);
        //hitCommand.setRepeatableOnKill(true);
        hitCommand.setResetOrientation(true);
        hitCommand.setSpecialmove(true);
        //hitCommand.setMoralDamage(true);
        //hitCommand.setHealingFromDamage(true);
        //hitCommand.setRetaliation(true);
        customedCommand = hitCommand;
        */

        customedCommand = new AttackCommand(bim.bfr, bim.scheduler, bim.player.getInventory());
        customedCommand.setFree(true);

        moveCommand = new MoveCommand(bim.bfr, bim.scheduler, bim.player.getInventory());
        //bim.bfr.getUnitRenderer(sltdUnit).setVisible(false);

        ccActionArea = new Area(bim.battlefield, Data.AreaType.MOVE_AREA);
        ccImpactArea = new Area(bim.battlefield, Data.AreaType.FOE_ACTION_AREA);
        ccTargets = new Area(bim.battlefield, Data.AreaType.DEPLOYMENT_AREA);
        bim.bfr.addAreaRenderer(ccImpactArea);
        bim.bfr.addAreaRenderer(ccActionArea);
        bim.bfr.addAreaRenderer(ccTargets);

        IUnit randomFoe = bim.battlefield.getUnit(13,8);
        randomFoe.addNativeAbility(Data.Ability.GUARD);
        int[] randomFoePos = bim.bfr.getModel().getUnitPos(randomFoe);
        GuardCommand guardCommand = new GuardCommand(bim.bfr, bim.scheduler, bim.player.getInventory());
        guardCommand.setFree(true);
        if(!guardCommand.apply(randomFoePos[0], randomFoePos[1])){ }

        /*
        randomFoe = bim.battlefield.getUnit(12,9);
        randomFoe.addNativeAbility(Data.Ability.GUARD);
        randomFoePos = bim.bfr.getModel().getUnitPos(randomFoe);
        guardCommand = new GuardCommand(bim.bfr, bim.scheduler, bim.player.getInventory());
        guardCommand.setFree(true);
        guardCommand.apply(randomFoePos[0], randomFoePos[1]);
        */


        Trigger trigger;
        for(int r = 0; r < bim.battlefield.getNbRows(); r++){
            for(int c = 0; c < bim.battlefield.getNbColumns(); c++){
                if(bim.battlefield.isTileOccupied(r, c)){
                    final BattleUnitRenderer woundedRenderer = bim.bfr.getUnitRenderer(bim.battlefield.getUnit(r, c));
                    trigger = new UponDisappearingTrigger(true, woundedRenderer.getModel());
                    trigger.addEvent(new EventCommand(bim.bfr, bim.scheduler) {

                        @Override
                        protected void execute() {
                            System.out.println("Remember the name of the one you killed that day : "+woundedRenderer.getModel().getName());
                        }

                        @Override
                        public boolean isApplicable() {
                            return true;
                        }
                    });
                    woundedRenderer.getModel().add(trigger);

                    if(woundedRenderer.getModel().isWarlord() && woundedRenderer.getModel().getArmy().getAffiliation() == Data.Affiliation.ENEMY_0){
                        foeWL = woundedRenderer.getModel();
                    }
                }
            }
        }
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

        //WALK UNIT

        //bim.battlefield.moveUnit(actorPos[0], actorPos[1], row, col, true);

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



        // TEST CUSTOMED COMMAND

        /*
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
            customedCommand.setFree(true);
            if(customedCommand.isInitiatorValid()) {
                customedCommand.setFree(false);
                Array<int[]> impact = customedCommand.getImpactArea(actorPos[0], actorPos[1], row, col);
                ccImpactArea.setTiles(impact, true);
                ccActionArea.setTiles(customedCommand.getActionArea(), true);
                ccTargets.setTiles(customedCommand.getTargetsAtRange(), true);

            }
        }
        /*


        // TEST FINAL

        /*
        if(switchmode && bim.battlefield.isTileOccupiedByAlly(row, col, Data.Affiliation.ALLY)) {
            sltdUnit = bim.battlefield.getUnit(row, col);
        }else{
            if(bim.battlefield.isTileOccupied(row, col)){

                switch (index){

                    case 1 : command = new AttackCommand(bim.bfr, bim.scheduler, bim.player.getInventory());break;
                    case 2 : command = new HealCommand(bim.bfr, bim.scheduler, bim.player.getInventory());break;
                    case 3 : command = new PushCommand(bim.bfr, bim.scheduler, bim.player.getInventory()); break;
                    case 4 : command = new SwitchPositionCommand(bim.bfr, bim.scheduler, bim.player.getInventory());break;
                    case 5 : command = new GuardCommand(bim.bfr, bim.scheduler, bim.player.getInventory()); break;
                    case 6 : command = new StealCommand(bim.bfr, bim.scheduler, bim.player.getInventory());break;
                    case 7 : command =  new SwitchWeaponCommand(bim.bfr, bim.scheduler, bim.player.getInventory(), 1); break;
                    case 8 : command = new ChooseOrientationCommand(bim.bfr, bim.scheduler, bim.player.getInventory(), Data.Orientation.NORTH);break;
                    case 9 : command = new EndUnitTurnCommand(bim.bfr, bim.scheduler, bim.player.getInventory());break;
                    default: command = new AttackCommand(bim.bfr, bim.scheduler, bim.player.getInventory());
                }
            } else {
                command = (index != 9) ? new WalkCommand(bim.bfr, bim.scheduler, bim.player.getInventory()) : new BuildCommand(bim.bfr, bim.scheduler, bim.player.getInventory(), Data.TileType.BRIDGE);

            }

            int[] unitPos = bim.battlefield.getUnitPos( sltdUnit);
            if (command.setInitiator(unitPos[0], unitPos[1])) {
                command.setDecoupled(true);
                command.attach(this);

                command.setTarget(row, col);
                if (command.isTargetValid()) {
                    command.apply();
                    historic.offer(command);
                }
            }
        }
        */


        return false;
    }

    @Override
    public void update60(float dt) {
        animation.update(dt);

        if(Gdx.input.isKeyJustPressed(Input.Keys.U) && !historic.isEmpty()){
            ActorCommand command = historic.peek();
            if(command.isUndoable()){
                command.undo();
                historic.pop();
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            if(!historic.isEmpty())
                historic.pop().pushRenderTasks();
            customedCommand.pushRenderTasks();
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.R)){

            int[] sltdUnitPos = bim.battlefield.getUnitPos(sltdUnit);
            Array<int[]> path = new Array<int[]>();
            path.add(new int[]{sltdUnitPos[0], sltdUnitPos[1] + 1});
            /*
            bim.bfr.getUnitRenderer(sltdUnit).displayWalk(path, false);
            bim.scheduler.addTask(new StandardTask(bim.battlefield, bim.bfr.getUnitRenderer(sltdUnit), new Notification.Walk(sltdUnit, path)));
            */

            /*
            bim.bfr.getUnitRenderer(sltdUnit).getNotification(sltdUnit, Data.AnimId.REGULAR_ATTACK);
            bim.bfr.getUnitRenderer(sltdUnit).getNotification(sltdUnit, Data.Orientation.WEST);
            bim.bfr.getUnitRenderer(sltdUnit).getNotification(sltdUnit, Data.AnimId.BACKSTAB);
            bim.bfr.getUnitRenderer(sltdUnit).getNotification(sltdUnit, new Notification.Walk(sltdUnit, path));
            */

            /*
            bim.scheduler.addTask(new StandardTask(bim.bfr.getUnitRenderer(sltdUnit), Data.AnimId.REGULAR_ATTACK));
            bim.scheduler.addTask(new StandardTask(bim.bfr.getUnitRenderer(sltdUnit), Data.Orientation.WEST));
            bim.scheduler.addTask(new StandardTask(bim.bfr.getUnitRenderer(sltdUnit), Data.AnimId.BACKSTAB));
            bim.scheduler.addTask(new StandardTask(bim.battlefield, bim.bfr.getUnitRenderer(sltdUnit), new Notification.Walk(sltdUnit, path)));
            */



            StandardTask task = new StandardTask();

            StandardTask.RendererThread thread0 = new StandardTask.RendererThread(bim.bfr.getUnitRenderer(sltdUnit));
            thread0.addQuery(Data.AnimId.REGULAR_ATTACK);
            thread0.addQuery(Data.Orientation.WEST);
            thread0.addQuery(Data.AnimId.BACKSTAB);

            StandardTask.RendererThread thread1 = new StandardTask.RendererThread(bim.bfr.getUnitRenderer(foeWL));
            thread1.addQuery(Data.AnimId.SPECIAL_MOVE);

            task.addThread(thread0);
            task.addThread(thread1);
            bim.scheduler.addTask(task);

            bim.scheduler.addTask(new StandardTask(bim.bfr.getUnitRenderer(sltdUnit), new Notification.Walk(sltdUnit, path, false)));


            /*
            int[] foePos = bim.battlefield.getUnitPos(foeWL);
            path.get(0)[0] = foePos[0];
            path.get(0)[1] = foePos[1] + 1;
            bim.scheduler.addTask(new StandardTask(bim.bfr.getUnitRenderer(foeWL), new Notification.Walk(foeWL, path)));
            */

            //bim.bfr.removeUnitRenderer(sltdUnit);
        }
    }


    /*
    @Override
    public void renderBetween(SpriteBatch batch) {
        tempoAreaWidget.render(batch);
    }
    */

    @Override
    public void init() {

    }

    @Override
    public void getNotification(Observable sender, Object data) {
        if(sender == data)
            sender.detach(this);
    }
}
