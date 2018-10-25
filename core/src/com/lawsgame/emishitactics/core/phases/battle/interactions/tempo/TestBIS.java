package com.lawsgame.emishitactics.core.phases.battle.interactions.tempo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Army;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.Weapon;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.GuardCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.SwitchPositionCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.atomic.HitCommand;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;
import com.lawsgame.emishitactics.engine.rendering.Animation;

import java.util.LinkedList;

public class TestBIS extends BattleInteractionState implements Observer{


    private Army army;
    IUnit sltdUnit;

    Array<Sprite> sprites;
    Animation animation;

    LinkedList<ActorCommand> historic = new LinkedList<ActorCommand>();

    ActorCommand customedCommand = null;
    Area ccActionArea;
    Area ccImpactArea;
    Area ccTargets;


    public TestBIS(BattleInteractionMachine bim) {
        super(bim, true, true, true, true, false);
        setArmy();

        sltdUnit = army.getWarlord();

        sprites = bim.assetProvider.genSpriteTree.getSpriteSet(false, false, false, Data.UnitTemplate.SOLAR_KNIGHT, Data.WeaponType.SWORD, Data.Orientation.WEST, false, Data.SpriteSetId.HEAL);
        for(int i =0; i < sprites.size; i++){
            sprites.get(i).setPosition(1, 1);
            sprites.get(i).setSize(1, 2);
        }
        animation = new Animation(sprites.size, 0.3f, true, false, false);
        animation.play();


        // TEST CUSTOMED COMMAND

        //customedCommand = new SwitchPositionCommand(bim.bfr, bim.scheduler, bim.player.getInventory());
        customedCommand = new HitCommand(bim.bfr, Data.ActionChoice.ATTACK, bim.scheduler, bim.player.getInventory(), 1, 50);
        customedCommand.setFree(true);
        ccActionArea = new Area(bim.battlefield, Data.AreaType.MOVE_AREA);
        ccImpactArea = new Area(bim.battlefield, Data.AreaType.FOE_ACTION_AREA);
        ccTargets = new Area(bim.battlefield, Data.AreaType.DEPLOYMENT_AREA);
        bim.bfr.addAreaRenderer(ccImpactArea);
        bim.bfr.addAreaRenderer(ccActionArea);
        bim.bfr.addAreaRenderer(ccTargets);

        IUnit randomFoe = bim.battlefield.getUnit(13,8);//bim.bfr.getModel().findUnit(Data.Affiliation.ENEMY_0).random();
        randomFoe.addNativeAbility(Data.Ability.GUARD);
        int[] randomFoePos = bim.bfr.getModel().getUnitPos(randomFoe);
        GuardCommand guardCommand = new GuardCommand(bim.bfr, bim.scheduler, bim.player.getInventory());
        guardCommand.setFree(true);
        guardCommand.apply(randomFoePos[0], randomFoePos[1]);
    }

    private void setArmy(){
        army = Army.createPlayerArmyTemplate();

        Unit warlord = new Unit("Aterui", Data.UnitTemplate.SOLAIRE, 1, Data.WeaponType.BOW, true, false, false, false, false);
        warlord.addWeapon(new Weapon(Data.WeaponTemplate.HUNTING_BOW));
        warlord.addWeapon(new Weapon(Data.WeaponTemplate.HUNTING_BOW));
        warlord.addWeapon(new Weapon(Data.WeaponTemplate.HUNTING_BOW));
        warlord.setLeadership(19);
        Unit soldier1 = new Unit("Taro", Data.UnitTemplate.SOLAR_KNIGHT, 5, Data.WeaponType.SWORD, false, false, false, false, false);
        soldier1.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));

        Unit soldier2 = new Unit("Maro", Data.UnitTemplate.SOLAR_KNIGHT, 5, Data.WeaponType.SWORD, false, false, false, false, false);
        soldier2.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));
        Unit soldier3 = new Unit("Ken", Data.UnitTemplate.SOLAR_KNIGHT, 5, Data.WeaponType.SWORD, false, false, false, false, false);
        soldier3.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));

        Unit soldier4 = new Unit("Loth", Data.UnitTemplate.SOLAR_KNIGHT, 5, Data.WeaponType.SWORD, false, false, false, false, false);
        soldier4.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));
        Unit soldier5 = new Unit("Caro", Data.UnitTemplate.SOLAR_KNIGHT, 5, Data.WeaponType.SWORD, false, false, false, false, false);
        soldier5.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));


        Unit warchief1 = new Unit("Azamaru", Data.UnitTemplate.SOLAR_KNIGHT, 5, Data.WeaponType.SWORD, false, false, false, false, false);
        warchief1.addWeapon(new Weapon(Data.WeaponTemplate.SHORTSWORD));
        warchief1.setLeadership(15);
        warchief1.setExperience(98);
        warchief1.setCurrentHitPoints(3);


        army.add(warlord);
        army.add(soldier1);
        army.add(soldier4);
        army.add(soldier5);
        army.add(warchief1);
        army.add(soldier2);
        army.add(soldier3);

        army.appointWarLord(warlord);
        army.appointSoldier(soldier1, 0);
        army.appointSoldier(soldier4, 0);
        army.appointSoldier(soldier5, 0);
        army.appointWarChief(warchief1);
        army.appointSoldier(soldier2, 1);
        army.appointSoldier(soldier3, 1);

        bim.battlefield.deploy(11,11, soldier1, true);
        bim.battlefield.deploy(10,11, soldier2, true);
        bim.battlefield.deploy(9,11, soldier3, true);
        bim.battlefield.deploy(10,12, warchief1, true);

        bim.battlefield.deploy(14,4, soldier4, true);
        bim.battlefield.deploy(15,4, soldier5, true);

        bim.battlefield.deploy(11, 4, warlord, true);
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


        // TEST CUSTOMED COMMAND

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
            if(customedCommand.isInitiatorValid()) {

                Array<int[]> impact = customedCommand.getImpactArea(actorPos[0], actorPos[1], row, col);
                ccImpactArea.setTiles(impact, true);
                ccActionArea.setTiles(customedCommand.getActionArea(), true);
                ccTargets.setTiles(customedCommand.getTargetsAtRange(), true);

            }
        }

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
        System.out.println(sender+"\n");
        if(sender == data)
            sender.detach(this);
    }
}
