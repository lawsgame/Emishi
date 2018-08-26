package com.lawsgame.emishitactics.core.phases.battle.helpers;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
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
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

import java.util.HashMap;
import java.util.Stack;


/*
 */
public class BattleCommandManager {
    private final Array<Array<BattleCommand>> commandPool;

    public BattleCommandManager(BattlefieldRenderer bfr, AnimationScheduler scheduler){
        commandPool = new Array<Array<BattleCommand>>();

        setChoice(new MoveCommand(bfr, scheduler));
        setChoice(new AttackCommand(bfr, scheduler));

        final Array<BattleCommand>  commands = new Array<BattleCommand>();
        for(int i = 1; i < Data.MAX_WEAPON_CARRIED_UPON_PROMOTION; i++){
            commands.add(new SwitchWeaponCommand(bfr, scheduler, i));
        }
        if(commands.size > 0 && commands.get(0) != null)
            commandPool.add(commands);

        setChoice(new PushCommand(bfr, scheduler));
        setChoice(new SwitchPositionCommand(bfr, scheduler));
        setChoice(new BattleCommand[]{
                new ChooseOrientationCommand(bfr, scheduler, Data.Orientation.NORTH),
                new ChooseOrientationCommand(bfr, scheduler, Data.Orientation.SOUTH),
                new ChooseOrientationCommand(bfr, scheduler, Data.Orientation.EAST),
                new ChooseOrientationCommand(bfr, scheduler, Data.Orientation.WEST),
        });
        setChoice(new BattleCommand[]{
                new BuildCommand(bfr, scheduler, Data.TileType.WATCH_TOWER),
                new BuildCommand(bfr, scheduler, Data.TileType.BRIDGE)
        });
        setChoice(new GuardCommand(bfr, scheduler));
        setChoice(new HealCommand(bfr, scheduler));
        setChoice(new StealCommand(bfr, scheduler));
        setChoice(new EndTurnCommand(bfr, scheduler));

    }


    private void setChoice(BattleCommand command){
        Array<BattleCommand>  commands = new Array<BattleCommand>();
        commands.add(command);
        commandPool.add(commands);
    }

    private void setChoice(BattleCommand[] battleCommands){
        Array<BattleCommand>  commands = new Array<BattleCommand>();
        commands.addAll(battleCommands);
        commandPool.add(commands);
    }


    // -----------------------------------------------------------------------




    public Array<ActionChoice> getAvailableChoices(IUnit actor, Stack<BattleCommand> history){
        Array<ActionChoice> choices = new Array<ActionChoice>();
        ActionChoice choice;
        for (int  i = 0; i < commandPool.size; i++) {
            if(commandPool.get(i).size > 0) {
                choice = commandPool.get(i).get(0).getActionChoice();
                if (!choice.isEndTurnActionOnly()
                        && choice.canbePerformedBy(actor)
                        && history != null
                        && !Utils.stackContainsAtLeastOneElementOf(history, commandPool.get(i))) {
                    choices.add(choice);
                }
            }
        }
        return choices;
    }

    public Array<BattleCommand> getAvailableCommands(IUnit actor, ActionChoice choice, boolean checkPerformable){
        Array<BattleCommand> availableCommands = new Array<BattleCommand>();
        for(int i = 0; i < commandPool.size ;i++) {
            if(commandPool.get(i).size > 0 && commandPool.get(i).get(0).getActionChoice() == choice) {
                Array<BattleCommand> chosenCommands = commandPool.get(i);
                for (int j = 0; j < chosenCommands.size; j++) {
                    if (!checkPerformable || chosenCommands.get(j).canbePerformedBy(actor)) {
                        availableCommands.add(chosenCommands.get(j));
                    }
                }
            }
        }
        return availableCommands;
    }

    public HashMap<ActionChoice, Array<BattleCommand>> getAllCommands(IUnit actor, Stack<BattleCommand> history, boolean checkFlavorPerformable) {
        HashMap<ActionChoice, Array<BattleCommand>> commands = new HashMap<ActionChoice, Array<BattleCommand>>();
        if(actor != null && history != null) {
            Array<ActionChoice> availableChoices = getAvailableChoices(actor, history);
            for (int i = 0; i < availableChoices.size; i++) {
                commands.put(availableChoices.get(i), getAvailableCommands(actor, availableChoices.get(i), checkFlavorPerformable));
            }
        }
        return commands;
    }

    //-------------------------  GETTERS & SETTERS ---------------------------


    public String toString(IUnit actor, Stack<BattleCommand> history, boolean checkFlavorPerformable){
        HashMap<ActionChoice, Array<BattleCommand>> allcommands = getAllCommands(actor, history, checkFlavorPerformable);
        String str = "Availables commands : \n";
        Array<BattleCommand> commands;
        for (ActionChoice choice : allcommands.keySet()) {
            commands = allcommands.get(choice);
            for(BattleCommand bc : commands){
                str += "\n"+bc.toString();
            }
        }
        return str+"\n";
    }

    public String toString(){
        String str = "command pool : \n";
        Array<BattleCommand> commands;
        for (int i = 0; i < commandPool.size; i++) {
            commands = commandPool.get(i);
            for(BattleCommand bc : commands){
                str += "\n"+bc.toString();
            }
        }
        return str+"\n";
    }
}
