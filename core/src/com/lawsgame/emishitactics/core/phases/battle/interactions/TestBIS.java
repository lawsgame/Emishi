package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.models.Army;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.MoveCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;

import java.util.LinkedList;
import java.util.Stack;

public class TestBIS extends BattleInteractionState {
    Unit warlord;
    Unit warchief;
    Unit soldier;

    Stack<BattleCommand> historic = new Stack<BattleCommand>();

    public TestBIS(BattleInteractionMachine bim) {
        super(bim);

        warlord = new Unit("Phillipe", Data.Job.SOLAR_KNIGHT, 7, Data.WeaponType.AXE, false, false, false, true);
        warchief = new Unit("Oscar", Data.Job.SOLAR_KNIGHT, 6, Data.WeaponType.SWORD, false, false, false, true);
        soldier = new Unit("Jim", Data.Job.SOLAR_KNIGHT, 5, Data.WeaponType.MACE, false, false, false, true);
        warlord.addWeapon(Data.Weapon.BROAD_AXE);
        warlord.setLeadership(15);
        warchief.addWeapon(Data.Weapon.SHORTSWORD);
        warchief.setLeadership(10);
        soldier.addWeapon(Data.Weapon.CLUB);

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

    }

    @Override
    public void end() {

    }

    @Override
    public void handleTouchInput(int row, int col) {
        // command test




        int[] unitPos = bim.battlefield.getUnitPos(warlord);
        /*
        if(bim.battlefield.isTileAvailable(row, col, warlord.has(Data.PassiveAbility.PATHFINDER))){
            Array<int[]> path = bim.battlefield.getShortestPath(unitPos[0], unitPos[1], row, col, warlord.has(Data.PassiveAbility.PATHFINDER), warlord.getAllegeance());
            bim.battlefield.moveUnit(unitPos[0], unitPos[1], row, col);
            System.out.println(path.size);
            bim.bfr.getNotification(path);
        }
        */

        BattleCommand command = new MoveCommand(bim.bfr);
        if(command.setActor(unitPos[0], unitPos[1])){
           command.setTarget(row, col);
           if(command.isTargetValid()){
               command.apply();
               historic.add(command);
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
    }

    @Override
    public void prerender(SpriteBatch batch) {

    }

    @Override
    public void renderBetween(SpriteBatch batch) {

    }

    @Override
    public void renderAhead(SpriteBatch batch) {

    }

    @Override
    public void init() {

    }
}
