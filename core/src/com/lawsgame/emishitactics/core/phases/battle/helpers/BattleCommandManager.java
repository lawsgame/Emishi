package com.lawsgame.emishitactics.core.phases.battle.helpers;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.AttackCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.BuildCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.ChangeTactic;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.ChooseOrientationCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.CoveringFireCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.EndUnitTurnCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.GuardCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.HealCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.ScanAreaCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.WalkCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.PushCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.StealCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.SwitchPositionCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.actor.SwitchWeaponCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

import java.util.HashMap;
import java.util.Stack;


/*
 */
public class BattleCommandManager {
    private final Array<Array<ActorCommand>> commandPool;

    public BattleCommandManager(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, TileHighlighter thl){
        commandPool = new Array<Array<ActorCommand>>();
        setChoice(new WalkCommand(bfr, scheduler, playerInventory));
        setChoice(new AttackCommand(bfr, scheduler, playerInventory));
        setChoice(new PushCommand(bfr, scheduler, playerInventory));
        setChoice(new SwitchPositionCommand(bfr, scheduler, playerInventory));
        setChoice(new CoveringFireCommand(bfr, scheduler, playerInventory));
        setChoice(new GuardCommand(bfr, scheduler, playerInventory));
        setChoice(new HealCommand(bfr, scheduler, playerInventory));
        setChoice(new StealCommand(bfr, scheduler, playerInventory));
        setChoice(new ScanAreaCommand(bfr, scheduler, playerInventory));
        Array<ActorCommand>  commands = new Array<ActorCommand>();
        for(int i = 0; i < Data.MAX_WEAPON_CARRIED_UPON_PROMOTION; i++){
            commands.add(new SwitchWeaponCommand(bfr, scheduler, playerInventory, i));
        }
        if(commands.size > 0 && commands.get(0) != null)
            commandPool.add(commands);
        setChoice(new ActorCommand[]{
                new ChooseOrientationCommand(bfr, scheduler, playerInventory, Data.Orientation.NORTH),
                new ChooseOrientationCommand(bfr, scheduler, playerInventory, Data.Orientation.SOUTH),
                new ChooseOrientationCommand(bfr, scheduler, playerInventory, Data.Orientation.EAST),
                new ChooseOrientationCommand(bfr, scheduler, playerInventory, Data.Orientation.WEST),
        });
        setChoice(new ActorCommand[]{
                new BuildCommand(bfr, scheduler, playerInventory, Data.TileType.WATCH_TOWER),
                new BuildCommand(bfr, scheduler, playerInventory, Data.TileType.BRIDGE)
        });
        setChoice(new ActorCommand[]{
                new ChangeTactic(bfr, scheduler, playerInventory, Data.BBMode.OFFENSIVE),
                new ChangeTactic(bfr, scheduler, playerInventory, Data.BBMode.DEFENSIVE)
        });
        setChoice(new EndUnitTurnCommand(bfr, scheduler, playerInventory, thl));

    }


    private void setChoice(ActorCommand command){
        Array<ActorCommand>  commands = new Array<ActorCommand>();
        commands.add(command);
        commandPool.add(commands);
    }

    private void setChoice(ActorCommand[] actorCommands){
        Array<ActorCommand>  commands = new Array<ActorCommand>();
        commands.addAll(actorCommands);
        commandPool.add(commands);
    }


    // -----------------------------------------------------------------------




    public Array<ActionChoice> getAvailableChoices(int rowActor, int colActor, Stack<ActorCommand> history){
        Array<ActionChoice> choices = new Array<ActionChoice>();
        ActionChoice choice;
        if(history != null){
            for (int  i = 0; i < commandPool.size; i++) {
                if(commandPool.get(i).size > 0) {
                    choice = commandPool.get(i).get(0).getActionChoice();
                    if(!choice.isEndTurnActionOnly() && (choice.isInfinitlyDoable() ||!Utils.stackContainsAtLeastOneElementOf(history, commandPool.get(i)))) {
                        for (int j = 0; j < commandPool.get(i).size; j++) {
                            commandPool.get(i).get(j).init();
                            commandPool.get(i).get(j).setInitiator(rowActor, colActor);
                            if (commandPool.get(i).get(j).isInitiatorValid()) {
                                choices.add(choice);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return choices;
    }

    public Array<ActorCommand> getAvailableCommands(int rowActor, int colActor, ActionChoice choice, boolean checkInitiatorValidy){
        Array<ActorCommand> availableCommands = new Array<ActorCommand>();
        for(int i = 0; i < commandPool.size ;i++) {
            if(commandPool.get(i).size > 0 && commandPool.get(i).get(0).getActionChoice() == choice) {
                Array<ActorCommand> chosenCommands = commandPool.get(i);
                for (int j = 0; j < chosenCommands.size; j++) {
                    chosenCommands.get(j).setInitiator(rowActor, colActor);
                    if (!checkInitiatorValidy || chosenCommands.get(j).isInitiatorValid()) {
                        chosenCommands.get(j).init();
                        availableCommands.add(chosenCommands.get(j));
                    }
                }
            }
        }
        return availableCommands;
    }

    public HashMap<ActionChoice, Array<ActorCommand>> getAllCommands(int rowActor, int colActor, Stack<ActorCommand> history, boolean checkInitiatorValidy) {
        HashMap<ActionChoice, Array<ActorCommand>> commands = new HashMap<ActionChoice, Array<ActorCommand>>();
        if(history != null) {
            Array<ActionChoice> availableChoices = getAvailableChoices(rowActor, colActor, history);
            for (int i = 0; i < availableChoices.size; i++) {
                commands.put(availableChoices.get(i), getAvailableCommands(rowActor, colActor, availableChoices.get(i), checkInitiatorValidy));
            }
        }
        return commands;
    }

    //-------------------------  GETTERS & SETTERS ---------------------------


    public String toString(int rowActor, int colActor, Stack<ActorCommand> history, boolean checkInitiatorValidy){
        HashMap<ActionChoice, Array<ActorCommand>> allcommands = getAllCommands(rowActor, colActor, history, checkInitiatorValidy);
        String str = "Availables commands : \n";
        Array<ActorCommand> commands;
        for (ActionChoice choice : allcommands.keySet()) {
            commands = allcommands.get(choice);
            for(ActorCommand bc : commands){
                str += "\n"+bc.toString();
            }
        }
        return str+"\n";
    }

    public String toString(){
        String str = "command pool : \n";
        Array<ActorCommand> commands;
        for (int i = 0; i < commandPool.size; i++) {
            commands = commandPool.get(i);
            for(ActorCommand bc : commands){
                str += "\n"+bc.toString();
            }
        }
        return str+"\n";
    }
}
