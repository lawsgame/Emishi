package com.lawsgame.emishitactics.core.states.commands;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Props;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Unit;

import java.util.HashMap;

/*
 * add a command ? => see the constructor below
 */
public class BattleCommandManager {
    private HashMap<Props.ActionChoice, BattleCommand> commandPool;

    public BattleCommandManager(Battlefield battlefield){

        /*
         add BC entries here :
          : commandPool.put(command.getActionChoice(), command);
          */

        setBattlefield(battlefield);
    }

    public Array<Props.ActionChoice> getPossibleActionChoices(Unit actor, Array<BattleCommand> history){
        Array<Props.ActionChoice> choices = new Array<Props.ActionChoice>();
        for(Props.ActionChoice choice : commandPool.keySet()){
            if(!commandPool.get(choice).isEndTurnCommand() && !history.contains(commandPool.get(choice), true) && commandPool.get(choice).canBeExecuted(actor)){
                choices.add(choice);
            }
        }
        return choices;
    }

    public BattleCommand get(Props.ActionChoice choice){
        return commandPool.get(choice);
    }

    // optional method
    public void setBattlefield(Battlefield battlefield){
        for(Props.ActionChoice choice : commandPool.keySet()){
            commandPool.get(choice).setBattlefield(battlefield);
        }
    }

}
