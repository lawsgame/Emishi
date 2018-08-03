package com.lawsgame.emishitactics.core.phases.battle;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.models.ActionChoice;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.AttackCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.BuildCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.ChooseOrientationCommand;
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


/*
 */
public class BattleCommandManager {
    private HashMap<ActionChoice, Array<BattleCommand>> commandPool;

    public BattleCommandManager(BattlefieldRenderer bfr, AnimationScheduler scheduler){
        commandPool = new HashMap<ActionChoice, Array<BattleCommand>>();

        setChoice(new AttackCommand(bfr, scheduler));
        setChoice(new BattleCommand[]{
                new BuildCommand(bfr, scheduler, Data.TileType.WATCH_TOWER),
                new BuildCommand(bfr, scheduler, Data.TileType.BRIDGE)
        });
        setChoice(new BattleCommand[]{
                new ChooseOrientationCommand(bfr, scheduler, Data.Orientation.NORTH),
                new ChooseOrientationCommand(bfr, scheduler, Data.Orientation.SOUTH),
                new ChooseOrientationCommand(bfr, scheduler, Data.Orientation.EAST),
                new ChooseOrientationCommand(bfr, scheduler, Data.Orientation.WEST),
        });
        setChoice(new GuardCommand(bfr, scheduler));
        setChoice(new HealCommand(bfr, scheduler));
        setChoice(new MoveCommand(bfr, scheduler));
        setChoice(new PushCommand(bfr, scheduler));
        setChoice(new StealCommand(bfr, scheduler));
        setChoice(new SwitchPositionCommand(bfr, scheduler));

        Array<BattleCommand>  commands = new Array<BattleCommand>();
        for(int i = 1; i < Data.MAX_WEAPON_CARRIED_UPON_PROMOTION; i++){
            commands.add(new SwitchWeaponCommand(bfr, scheduler, i));
            if(commands.get(0) != null)
                commandPool.put(commands.get(0).getActionChoice(), commands);
        }

    }


    private void setChoice(BattleCommand command){
        Array<BattleCommand>  commands = new Array<BattleCommand>();
        commands.add(command);
        commandPool.put(command.getActionChoice(), commands);
    }

    private void setChoice(BattleCommand[] battleCommands){
        Array<BattleCommand>  commands = new Array<BattleCommand>();
        commands.addAll(battleCommands);
        commandPool.put(battleCommands[0].getActionChoice(), commands);
    }

    public Array<ActionChoice> getAvailableChoices(IUnit actor, Array<BattleCommand> history){
        Array<ActionChoice> choices = new Array<ActionChoice>();
        for (ActionChoice choice : commandPool.keySet()) {
            if (!choice.isEndTurnActionOnly()
                    && choice.canbePerformedBy(actor)
                    && Utils.arrayContainsAtLeastOneElementOf(history, commandPool.get(choice), true)) {
                choices.add(choice);
            }
        }
        return choices;
    }

    public boolean isChoiceDiversified(ActionChoice choice){
        return commandPool.get(choice) != null && commandPool.get(choice).size > 1;
    }

    public Array<BattleCommand> getAvailableCommands(IUnit actor, ActionChoice choice){
        Array<BattleCommand> commands = new Array<BattleCommand>();
        if(commandPool.get(choice) != null) {
            for(int i = 0; i <commandPool.get(choice).size; i++){
                if(commandPool.get(choice).get(i).canbePerformedBy(actor)){
                    commands.add(commandPool.get(choice).get(i));
                }
            }
        }
        return commands;
    }

    // optional method
    public void setBattlefield(BattlefieldRenderer battlefieldRenderer){
        for(ActionChoice choice : commandPool.keySet()){
            for(int i = 0; i < commandPool.get(choice).size; i++) {
                commandPool.get(choice).get(i).setBattlefield(battlefieldRenderer);
            }
        }
    }


}
