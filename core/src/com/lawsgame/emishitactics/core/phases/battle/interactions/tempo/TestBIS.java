package com.lawsgame.emishitactics.core.phases.battle.interactions.tempo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Army;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.Weapon;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.AttackCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.BuildCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.ChooseOrientationCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.EndTurnCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.GuardCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.HealCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.MoveCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.PushCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.StealCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.SwitchPositionCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.SwitchWeaponCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.interactions.EndTurnBIS;
import com.lawsgame.emishitactics.core.phases.battle.interactions.HandleOutcomeBIS;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.AreaWidget;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ActionPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ExperiencePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.LevelUpPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.LootPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoExperiencePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoLevelUpPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoLootPanel;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;

import java.util.Stack;

public class TestBIS extends BattleInteractionState {
    Unit warlord;
    Unit warchief;
    Unit soldier;
    //ActionPanel panel;
    AreaWidget areaWidget;
    ExperiencePanel experiencePanel;
    LevelUpPanel levelUpPanel;
    LootPanel lootPanel;
    EndTurnBIS.WindRoseWidget windRoseWidget;

    IUnit sltdUnit;
    int index;
    boolean switchmode;

    Stack<BattleCommand> historic = new Stack<BattleCommand>();
    BattleCommand command = null;

    public TestBIS(BattleInteractionMachine bim) {
        super(bim, true, true, true);

        areaWidget = new AreaWidget(bim.battlefield, Data.AreaType.FOE_ACTION_AREA);


        warlord = new Unit("Phillipe", Data.Job.SERGEANT, 11, Data.WeaponType.AXE, false, false, false, true);
        warchief = new Unit("Oscar", Data.Job.SERGEANT, 6, Data.WeaponType.BOW, false, false, false, true);
        soldier = new Unit("Jim", Data.Job.SERGEANT, 5, Data.WeaponType.MACE, false, false, false, true);
        warlord.addWeapon(new Weapon(Data.WeaponTemplate.BROAD_AXE));
        warlord.setLeadership(15);
        warlord.setExperience(90);
        warchief.addWeapon(new Weapon(Data.WeaponTemplate.HUNTING_BOW));
        warchief.setLeadership(10);
        warchief.applyDamage(1, false);
        soldier.addWeapon(new Weapon(Data.WeaponTemplate.CLUB));

        IArmy army = Army.getPlayerArmy();
        army.add(warlord);
        army.add(warchief);
        army.add(soldier);
        army.appointWarLord(warlord);
        army.appointSoldier(soldier, 0);
        army.appointWarChief(warchief);

        bim.battlefield.deploy(6,7,warlord, true);
        bim.battlefield.deploy(6,8,warchief, true);
        bim.battlefield.deploy(6,9,soldier, true);

        Area.UnitArea area = bim.battlefield.addGuardedArea(4, 8);
        bim.bfr.getNotification(area);

        sltdUnit = warlord;
        index = 1;
        switchmode = false;

        experiencePanel = new TempoExperiencePanel(bim.uiStage.getViewport());
        experiencePanel.set(bim.mainI18nBundle, 20, 35);
        bim.uiStage.addActor(experiencePanel);
        levelUpPanel = new TempoLevelUpPanel(bim.uiStage.getViewport());
        levelUpPanel.set(bim.mainI18nBundle, warlord, warlord.levelup());
        bim.uiStage.addActor(levelUpPanel);
        lootPanel = new TempoLootPanel(bim.uiStage.getViewport());
        lootPanel.set(new Weapon(Data.WeaponTemplate.SHORTSWORD), bim.mainI18nBundle);
        bim.uiStage.addActor(lootPanel);

        //EXP TEST
        /*
        System.out.println("lvl : "+warlord.getLevel() + " exp : "+warlord.getExperience());
        int[] gain = warlord.addExpPoints(960);
        levelUpPanel.setTiles(bim.mainI18nBundle, warlord, gain);
        levelUpPanel.show();
        System.out.println("lvl : "+warlord.getLevel() + " exp : "+warlord.getExperience());
        */


        // SCHEDULER TEST
        /*
        Array<int[]> validPath = bim.battlefield.getShortestPath(6, 7, 8, 9, warlord.has(Data.Ability.PATHFINDER), warlord.getAllegeance(), false);

        bim.shortTilePanel.setTiles(Data.TileType.PLAIN);
        bim.shortUnitPanel.setTiles(warlord);
        bim.scheduler.addTask(new StandardTask(bim.showSTP, 0f));
        bim.scheduler.addTask(new StandardTask(bim.battlefield, bim.bfr.getUnitRenderer(warlord), new Notification.Walk(warlord, validPath)));
        bim.scheduler.addTask(new StandardTask(bim.showSUP, 0f));
        System.out.println(bim.scheduler);
        */

        // CHOICE TEST
        /*
        warlord.addWeapon(new Weapon(Data.WeaponTemplate.BROAD_AXE));
        warlord.setMoved(true);
        Array<BattleCommand> historic = new Array<BattleCommand>();
        Array<ActionChoice> choices = bim.bcm.getAvailableChoices(warlord, historic);
        System.out.println("Available choices for "+warlord.getName());
        for(int i = 0; i < choices.size; i++){
            System.out.println(choices.get(i).getKey());
        }

        System.out.println(bim.bcm.toString(warlord, new Array<BattleCommand>(), true));
        */

        //choicePanel = new TempoChoicePanel(bim.asm);
        //bim.uiStage.addActor(choicePanel);

    }

    @Override
    public void end() {

    }


    @Override
    public boolean handleTouchInput(int row, int col) {
        // command test

        System.out.println("input : "+row+" "+col);

        //TEST 0
        //System.out.println(bim.battlefield.isTileGuarded(rowInit, colInit, Data.Allegeance.ENEMY_0));

        //TEST 1
        /*
        Array<IUnit> coverers = bim.battlefield.getUnitsCoveringTile(rowInit, colInit, Data.Allegeance.ENEMY_0);
        System.out.println("COVERERS : ");
        for(int i = 0; i <  coverers.size; i++){
            System.out.println(coverers.getInstance(i).getName());
        }
        */

        //TEST 2
        /*
        command = new TestCommand(bim.bfr, bim.scheduler);
        unitPos = bim.battlefield.getUnitPos( warlord);
        command.setActor(unitPos[0], unitPos[1]);
        command.setTarget(rowInit, colInit);
        areaWidget.setTiles(command.getImpactArea());
        */

        //TEST 3
        /*
        Notification.Build notif = new Notification.Build(rowInit, colInit,
                Data.TileType.WATCH_TOWER,
                warlord);
        bim.bfr.getNotification(notif);
        */

        // TEST FINAL

        /**
        if(switchmode && bim.battlefield.isTileOccupiedByAlly(row, col, Data.Allegeance.ALLY)) {
            sltdUnit = bim.battlefield.getUnit(row, col);

        }else{
            if(bim.battlefield.isTileOccupied(row, col)){

                switch (index){

                    case 1 : command = new AttackCommand(bim.bfr, bim.scheduler);break;
                    case 2 : command = new HealCommand(bim.bfr, bim.scheduler);break;
                    case 3 : command = new PushCommand(bim.bfr, bim.scheduler); break;
                    case 4 : command = new SwitchPositionCommand(bim.bfr, bim.scheduler);break;
                    case 5 : command = new GuardCommand(bim.bfr, bim.scheduler); break;
                    case 6 : command = new StealCommand(bim.bfr, bim.scheduler);break;
                    case 7 : command =  new SwitchWeaponCommand(bim.bfr, bim.scheduler, 1); break;
                    case 8 : command = new ChooseOrientationCommand(bim.bfr, bim.scheduler, Data.Orientation.NORTH);break;
                    case 9 : command = new EndTurnCommand(bim.bfr, bim.scheduler);break;
                    default: command = new AttackCommand(bim.bfr, bim.scheduler);
                }
            } else {
                command = (index != 9) ? new MoveCommand(bim.bfr, bim.scheduler) : new BuildCommand(bim.bfr, bim.scheduler, Data.TileType.BRIDGE);

            }

            int[] unitPos = bim.battlefield.getUnitPos( sltdUnit);
            if (command.setActor(unitPos[0], unitPos[1])) {
                command.setDecoupled(true);

                command.setTarget(row, col);
                if (command.isTargetValid()) {

                    command.blink(true);
                    if(bim.app.isPanelAvailable(command)){


                        final ActionPanel panel = bim.app.getPanel(command);

                        bim.scheduler.addTask(new StandardTask(new SimpleCommand() {
                            @Override
                            public void apply() {
                                bim.uiStage.addActor(panel);
                                panel.hide();
                                panel.show();
                            }
                        }, 0f));
                        bim.scheduler.wait(3.5f);
                        bim.scheduler.addTask(new StandardTask(new SimpleCommand() {
                            @Override
                            public void apply() {
                                panel.hide();
                            }
                        }, 0f));
                        bim.scheduler.wait(0.2f);
                        bim.scheduler.addTask(new StandardTask(new SimpleCommand() {
                            @Override
                            public void apply() {
                                panel.remove();
                            }
                        }, 0f));

                    }



                    bim.scheduler.addTask(new StandardTask(new SimpleCommand() {
                        @Override
                        public void apply() {
                            command.apply();
                        }
                    }, 0f));

                    historic.push(command);
                }
            }
        }
        */

        return true;
    }

    @Override
    public void update60(float dt) {

        if(Gdx.input.isKeyJustPressed(Input.Keys.U) && !historic.isEmpty()){
            BattleCommand command = historic.peek();
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
            System.out.println("action command : end turn");
            index = 9;
        }


        if(Gdx.input.isKeyJustPressed(Input.Keys.P)){

            switch (panelIndex%3){
                case 0:
                    lootPanel.hide();
                    experiencePanel.show();
                    break;
                case 1:
                    experiencePanel.hide();
                    levelUpPanel.show();
                    break;
                case 2:
                    levelUpPanel.hide();
                    lootPanel.show();
                    break;
            }
            panelIndex++;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.C)){
            Stack<BattleCommand> historic = new Stack<BattleCommand>();
            ChooseOrientationCommand command = new ChooseOrientationCommand(bim.bfr, bim.scheduler, Data.Orientation.NORTH);
            int[] wlPos = bim.battlefield.getUnitPos(warlord);
            command.setActor(wlPos[0], wlPos[1]);
            command.getOutcome().receivers.add(warlord);
            command.getOutcome().experienceGained.add(60);
            command.getOutcome().droppedItems.add(new Weapon(Data.WeaponTemplate.HUNTING_BOW));
            historic.add(command);
            bim.replace(new HandleOutcomeBIS(bim, historic));
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.T)){
            if(historic.peek() != null) {
                historic.peek().pushRenderTasks();
            }
        }
    }
    int panelIndex = 0;


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
