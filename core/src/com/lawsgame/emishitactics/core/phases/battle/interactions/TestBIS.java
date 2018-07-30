package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Army;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.Weapon;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.AttackCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.HealCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.MoveCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.PushCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.TestCommand;
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
    IUnit foe;
    ActionPanel panel;
    AreaWidget areaWidget;

    IUnit sltdUnit;
    int index;
    boolean switchmode;

    Stack<BattleCommand> historic = new Stack<BattleCommand>();
    BattleCommand command = null;

    public TestBIS(BattleInteractionMachine bim) {
        super(bim);

        areaWidget = new SimpleAreaWidget(bim.battlefield, Data.AreaType.FOE_ACTION_AREA);


        warlord = new Unit("Phillipe", Data.Job.SOLAR_KNIGHT, 7, Data.WeaponType.AXE, false, false, false, true);
        warchief = new Unit("Oscar", Data.Job.SOLAR_KNIGHT, 6, Data.WeaponType.BOW, false, false, false, true);
        soldier = new Unit("Jim", Data.Job.SOLAR_KNIGHT, 5, Data.WeaponType.MACE, false, false, false, true);
        warlord.addWeapon(new Weapon(Data.WeaponTemplate.BROAD_AXE));
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

        bim.battlefield.deployUnit(6,7,warlord);
        bim.battlefield.deployUnit(6,8,warchief);
        bim.battlefield.deployUnit(6,9,soldier);

        foe = bim.battlefield.getUnit(5, 9);
        bim.battlefield.addCoveredArea(5, 9);

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


        //TEST 1
        /*
        command = new MoveCommand(bim.bfr, bim.scheduler);
        int[] unitPos = bim.battlefield.getUnitPos( warlord);
        if (command.setActor(unitPos[0], unitPos[1])) {
            command.setTarget(row, col);
            if (command.isTargetValid()) {
                command.apply();
                historic.push(command);
            }
        }


        Array<int[]> actionTiles = new Array<int[]>();
        command = new TestCommand(bim.bfr, bim.scheduler);
        unitPos = bim.battlefield.getUnitPos( warlord);

        if(command.atActionRange(unitPos[0], unitPos[1], warlord)){
            actionTiles.add(unitPos);
        }

        for (int r = 0; r < bim.battlefield.getNbRows(); r++) {
            for (int c = 0; c < bim.battlefield.getNbColumns(); c++) {
                if (command.isTargetValid(unitPos[0], unitPos[1], r, c)){
                    actionTiles.add(new int[]{r, c});
                }

            }
        }
        areaWidget.setTiles(actionTiles);
        */

        //TEST 2
        /*
        command = new TestCommand(bim.bfr, bim.scheduler);
        unitPos = bim.battlefield.getUnitPos( warlord);
        command.setActor(unitPos[0], unitPos[1]);
        command.setTarget(row, col);
        areaWidget.setTiles(command.getImpactArea());
        */


        System.out.println(warlord.getArmy().getSquadSize(warlord, true));


        // TEST 3
        if(switchmode && bim.battlefield.isTileOccupiedByAlly(row, col, Data.Allegeance.ALLY)) {
            sltdUnit = bim.battlefield.getUnit(row, col);

        }else{
            if(bim.battlefield.isTileOccupied(row, col)){


                switch (index){
                    case 1 : command = new AttackCommand(bim.bfr, bim.scheduler); break;
                    case 2 : command = new HealCommand(bim.bfr, bim.scheduler); break;
                    case 3 : command = new PushCommand(bim.bfr, bim.scheduler); break;
                    default:
                        command = new AttackCommand(bim.bfr, bim.scheduler);
                }


            } else {
                command = new MoveCommand(bim.bfr, bim.scheduler);
            }


            int[] unitPos = bim.battlefield.getUnitPos( sltdUnit);
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

        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
            bim.battlefield.removeCoveredArea(foe);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
            int[] unitPos = bim.battlefield.getUnitPos(foe);
            bim.battlefield.addCoveredArea(unitPos[0], unitPos[1]);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)){
            panel.show();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)){
            panel.hide();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.I)){
            bim.bfr.getUnitRenderer(warlord).displayPushed(Data.Orientation.NORTH);

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
