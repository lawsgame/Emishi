package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Army;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.Weapon;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.AttackCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.ChooseOrientationCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.GuardCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.HealCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.MoveCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.PushCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.StealCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.SwitchPositionCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.SwitchWeaponCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.SimpleAreaWidget;
import com.lawsgame.emishitactics.core.phases.battle.widgets.TempoActionPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ActionPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.AreaWidget;

import java.util.Stack;

public class TestBIS extends BattleInteractionState {
    Unit warlord;
    Unit warchief;
    Unit soldier;
    ActionPanel panel;
    AreaWidget areaWidget;

    IUnit sltdUnit;
    int index;
    boolean switchmode;
    int weaponTick = 0;

    Stack<BattleCommand> historic = new Stack<BattleCommand>();
    BattleCommand command = null;

    public TestBIS(BattleInteractionMachine bim) {
        super(bim);

        areaWidget = new SimpleAreaWidget(bim.battlefield, Data.AreaType.FOE_ACTION_AREA);


        warlord = new Unit("Phillipe", Data.Job.SOLAR_KNIGHT, 7, Data.WeaponType.AXE, false, false, false, true);
        warchief = new Unit("Oscar", Data.Job.SOLAR_KNIGHT, 6, Data.WeaponType.BOW, false, false, false, true);
        soldier = new Unit("Jim", Data.Job.SOLAR_KNIGHT, 5, Data.WeaponType.MACE, false, false, false, true);
        warlord.addWeapon(new Weapon(Data.WeaponTemplate.BROAD_AXE));
        warlord.addWeapon(new Weapon(Data.WeaponTemplate.TEST_AXE));
        warlord.setLeadership(15);
        warchief.addWeapon(new Weapon(Data.WeaponTemplate.HUNTING_BOW));
        warchief.setLeadership(10);
        soldier.addWeapon(new Weapon(Data.WeaponTemplate.CLUB));

        IArmy army = new Army(Data.Allegeance.ALLY, true);
        army.add(warlord);
        army.add(warchief);
        army.add(soldier);
        army.appointWarLord(warlord);
        army.appointSoldier(soldier, 0);
        army.appointWarChief(warchief);

        bim.battlefield.deployUnit(6,7,warlord, true);
        bim.battlefield.deployUnit(6,8,warchief, true);
        bim.battlefield.deployUnit(6,9,soldier, true);

        Area.UnitArea area = bim.battlefield.addGuardedArea(4, 8);
        bim.bfr.getNotification(area);

        //UI
        panel = new TempoActionPanel.AttackPanel(bim.UIStage.getViewport(), 3, 80);
        panel.set();
        bim.UIStage.addActor(panel);
        panel.hide();

        sltdUnit = warlord;
        index = 1;
        switchmode = false;

    }

    @Override
    public void end() {

    }


    @Override
    public void handleTouchInput(int row, int col) {
        // command test

        System.out.println("input : "+row+" "+col);

        //TEST 0
        //System.out.println(bim.battlefield.isTileGuarded(row, col, Data.Allegeance.ENEMY));

        //TEST 1
        /*
        Array<IUnit> coverers = bim.battlefield.getUnitsCoveringTile(row, col, Data.Allegeance.ENEMY);
        System.out.println("COVERERS : ");
        for(int i = 0; i <  coverers.size; i++){
            System.out.println(coverers.get(i).getName());
        }
        */

        //TEST 2
        /*
        command = new TestCommand(bim.bfr, bim.scheduler);
        unitPos = bim.battlefield.getUnitPos( warlord);
        command.setActor(unitPos[0], unitPos[1]);
        command.setTarget(row, col);
        areaWidget.setTiles(command.getImpactArea());
        */

        //TEST 3
        /*
        Notification.Build notif = new Notification.Build(row, col,
                Data.TileType.WATCH_TOWER,
                warlord);
        bim.bfr.getNotification(notif);
        */

        // TEST FINAL

        if(switchmode && bim.battlefield.isTileOccupiedByAlly(row, col, Data.Allegeance.ALLY)) {
            sltdUnit = bim.battlefield.getUnit(row, col);

        }else{
            if(bim.battlefield.isTileOccupied(row, col)){

                switch (index){
                    case 1 : command = new AttackCommand(bim.bfr, bim.scheduler, true);break;
                    case 2 : command = new HealCommand(bim.bfr, bim.scheduler);break;
                    case 3 : command = new PushCommand(bim.bfr, bim.scheduler); break;
                    case 4 : command = new SwitchPositionCommand(bim.bfr, bim.scheduler);break;
                    case 5 : command = new GuardCommand(bim.bfr, bim.scheduler); break;
                    case 6 : command = new StealCommand(bim.bfr, bim.scheduler);break;
                    case 7 : command =  new SwitchWeaponCommand(bim.bfr, bim.scheduler, 1); break;
                    case 8 : command = new ChooseOrientationCommand(bim.bfr, bim.scheduler, Data.Orientation.NORTH);break;
                    default: command = new AttackCommand(bim.bfr, bim.scheduler, true);

                }
            } else {

                command = new MoveCommand(bim.bfr, bim.scheduler);

            }

            int[] unitPos = bim.battlefield.getUnitPos( sltdUnit);
            command.init();
            if (command.setActor(unitPos[0], unitPos[1])) {
                command.setTarget(row, col);
                if (command.isTargetValid()) {
                    command.apply();
                    historic.push(command);
                }
            }
        }

    }

    @Override
    public void update60(float dt) {

        if(Gdx.input.isKeyJustPressed(Input.Keys.U) && !historic.isEmpty()){
            BattleCommand command = historic.peek();
            if(command.isUndoable()){
                command.undo();
                historic.pop();
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)){
            panel.show();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
            panel.hide();
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
            System.out.println("action command : switch weapon => "+(weaponTick%2)+" /"+weaponTick);
            index = 7;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)){
            System.out.println("action command : choose orientation => NORTH");
            index = 8;
        }


    }

    @Override
    public void prerender(SpriteBatch batch) {

    }

    @Override
    public void renderBetween(SpriteBatch batch) {
        areaWidget.render(batch);
    }

    @Override
    public void renderAhead(SpriteBatch batch) {

    }

    @Override
    public void init() {

    }
}
