package com.lawsgame.emishitactics.core.phases.battle.interactions.tempo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Army;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.Weapon;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.AreaWidget;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ActionInfoPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ExperiencePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.LevelUpPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.LootPanel;
import com.lawsgame.emishitactics.engine.rendering.Animation;

import java.util.LinkedList;
import java.util.Stack;

public class TestBIS extends BattleInteractionState {
    ActionInfoPanel actionInfoPanel;
    AreaWidget areaWidget;
    ExperiencePanel experiencePanel;
    LevelUpPanel levelUpPanel;
    LootPanel lootPanel;

    private Animation animation;
    private Array<Sprite> sprites;
    private TextureRegion[] spriteSet;

    private AreaWidget moveAW;
    private AreaWidget actionAW;

    private LinkedList<Array<int[]>> awUpdates;
    private AreaWidget subaddAW;

    private Army army;
    IUnit sltdUnit;
    int index;
    boolean switchmode;

    Stack<ActorCommand> historic = new Stack<ActorCommand>();
    ActorCommand command = null;

    public TestBIS(BattleInteractionMachine bim) {
        super(bim, true, true, true, true, false);
        setArmy();

        sltdUnit = army.getWarlord();
        index = 1;
        switchmode = false;

        /*sprites = bim.spriteProvider.genSpriteTree.getSpriteSet(
                false,
                false,
                false,
                Data.UnitTemplate.SOLAR_KNIGHT,
                Data.WeaponType.SWORD,
                Data.Orientation.WEST,
                true,
                Data.SpriteSetId.REST);*/
        sprites = bim.spriteProvider.charaSpriteTree.getSpriteSet(true, Data.UnitTemplate.SOLAIRE, Data.WeaponType.SWORD, Data.Orientation.SOUTH, true, Data.SpriteSetId.REST);
        for(int i = 0; i < sprites.size; i++){
            sprites.get(i).setSize((sprites.get(i).getRegionHeight() == sprites.get(i).getRegionWidth() ? 2 : 1), 2);
            sprites.get(i).setPosition(2 + (sprites.get(i).getRegionHeight() == sprites.get(i).getRegionWidth() ? 0 : 0.5f), 2);

        }
        animation = new Animation(sprites.size, Data.ANIMATION_NORMAL_SPEED, true, false, false);
        animation.play();

        moveAW = new AreaWidget(bim.battlefield, Data.AreaType.SQUAD_MEMBER);
        actionAW = new AreaWidget(bim.battlefield, Data.AreaType.ACTION_AREA);



        subaddAW = new AreaWidget(bim.battlefield, Data.AreaType.FOE_ACTION_AREA);
        this.awUpdates = new LinkedList<Array<int[]>>();

        Array<int[]> tiles = new Array<int[]>();
        tiles.add(new int[]{5,5});
        tiles.add(new int[]{5,6});
        tiles.add(new int[]{6,5});
        subaddAW.setTiles(tiles, true);

        tiles = new Array<int[]>();
        tiles.add(new int[]{6,5});
        tiles.add(new int[]{6,6});
        tiles.add(new int[]{6,7});
        awUpdates.offer(tiles);

        tiles = new Array<int[]>();
        tiles.add(new int[]{6,5});
        tiles.add(new int[]{6,6});
        tiles.add(new int[]{7,6});
        awUpdates.offer(tiles);

    }

    private void setArmy(){
        army = Army.createPlayerArmyTemplate();

        Unit warlord = new Unit("Aterui", Data.UnitTemplate.SOLAIRE, 18, Data.WeaponType.BOW, true, false, false, false, false);
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
        actionAW.render(batch);
        moveAW.render(batch);
        //subaddAW.render(batch);
        //if(sprites != null) sprites.get(animation.getCurrentFrame()).draw(batch);
        //batch.draw(bim.spriteProvider.portraits.get("solar_knight_ai"), 1, 4, 2, 2);

    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        // command test

        System.out.println("input : "+row+" "+col);
        //bim.moveCamera(row, col, true);


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
                command = (index != 9) ? new MoveCommand(bim.bfr, bim.scheduler, bim.player.getInventory()) : new BuildCommand(bim.bfr, bim.scheduler, bim.player.getInventory(), Data.TileType.BRIDGE);

            }

            int[] unitPos = bim.battlefield.getUnitPos( sltdUnit);
            if (command.setInitiator(unitPos[0], unitPos[1])) {
                //command.setDecoupled(true);

                command.setTarget(row, col);
                if (command.isTargetValid()) {

                    command.blink(true);
                    command.apply();
                    historic.push(command);
                }
            }
        }*/


        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            int[] actorPos = bim.battlefield.getUnitPos(sltdUnit);
            bim.battlefield.moveUnit(actorPos[0], actorPos[1], row, col, true);

            Array<int[]> tiles = bim.battlefield.getMoveArea(row, col);
            moveAW.setTiles(tiles, true);
            tiles = bim.battlefield.getActionArea(row, col);
            actionAW.setTiles(tiles, true);
        }



        return true;
    }

    @Override
    public void update60(float dt) {
        if(animation != null)
            animation.update(dt);

        if(Gdx.input.isKeyJustPressed(Input.Keys.U) && !historic.isEmpty()){
            ActorCommand command = historic.peek();
            if(command.getActionChoice().isUndoable()){
                command.undo();
                historic.pop();
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)){
            switchmode = !switchmode;
            System.out.println("switch between units : "+switchmode);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)){
            System.out.println("action command : attack");
            index = 1;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)){
            System.out.println("action command : heal");
            index = 2;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)){
            System.out.println("action command : push");
            index = 3;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)){
            System.out.println("action command : switch");
            index = 4;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)){
            System.out.println("action command : guard");
            index = 5;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)){
            System.out.println("action command : steal");
            index = 6;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)){
            System.out.println("action command : switch weapon");
            index = 7;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)){
            System.out.println("action command : choose orientation => NORTH");
            index = 8;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)){
            System.out.println("action command : endTurn turn / build");
            index = 9;
        }


        if(Gdx.input.isKeyJustPressed(Input.Keys.A)) bim.bfr.getUnitRenderer(sltdUnit).display(Data.AnimId.SPECIAL_MOVE);
        if(Gdx.input.isKeyJustPressed(Input.Keys.Z)) bim.bfr.getUnitRenderer(sltdUnit).display(Data.AnimId.ATTACK);
        if(Gdx.input.isKeyJustPressed(Input.Keys.E)) bim.bfr.getUnitRenderer(sltdUnit).display(Data.AnimId.HEAL);
        if(Gdx.input.isKeyJustPressed(Input.Keys.R)) bim.bfr.getUnitRenderer(sltdUnit).display(Data.AnimId.BUILD);
        if(Gdx.input.isKeyJustPressed(Input.Keys.T)) bim.bfr.getUnitRenderer(sltdUnit).display(Data.AnimId.WALK);
        if(Gdx.input.isKeyJustPressed(Input.Keys.Y)) bim.bfr.getUnitRenderer(sltdUnit).display(Data.AnimId.DODGE);
        if(Gdx.input.isKeyJustPressed(Input.Keys.C)) bim.bfr.getUnitRenderer(sltdUnit).setOrientation(Data.Orientation.WEST);
        if(Gdx.input.isKeyJustPressed(Input.Keys.V)) bim.bfr.getUnitRenderer(sltdUnit).setOrientation(Data.Orientation.NORTH);
        if(Gdx.input.isKeyJustPressed(Input.Keys.N)) bim.bfr.getUnitRenderer(sltdUnit).setOrientation(Data.Orientation.SOUTH);
        if(Gdx.input.isKeyJustPressed(Input.Keys.B)) bim.bfr.getUnitRenderer(sltdUnit).setOrientation(Data.Orientation.EAST);
        if(Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            this.targeted = ! targeted;
            bim.bfr.getUnitRenderer(sltdUnit).setTargeted(targeted);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            this.done = ! done;
            bim.bfr.getUnitRenderer(sltdUnit.getArmy().getAllSquads().get(0).get(0)).setDone(done);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.H)){
            subaddAW.getModel().add(awUpdates.peek(), true);
            awUpdates.offer(awUpdates.pop());
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.J)){
            subaddAW.getModel().substract(awUpdates.peek(), true);
            awUpdates.offer(awUpdates.pop());
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.G)){
            subaddAW.getModel().clear(true);
        }

    }
    private boolean targeted = false;
    private boolean done = false;

    /*
    @Override
    public void renderBetween(SpriteBatch batch) {
        areaWidget.render(batch);
    }
    */

    @Override
    public void init() {

    }
}
