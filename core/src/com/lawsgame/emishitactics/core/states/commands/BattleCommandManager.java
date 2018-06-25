package com.lawsgame.emishitactics.core.states.commands;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Unit;

import java.util.HashMap;

/*
 * add a command ? => see the constructor below
 */
public class BattleCommandManager {
    private HashMap<Data.ActionChoice, BattleCommand> commandPool;

    public BattleCommandManager(Battlefield battlefield){
        commandPool = new HashMap<Data.ActionChoice, BattleCommand>();

        /*
         add BC entries here :
          : commandPool.put(command.getActionChoice(), command);
          */

        setBattlefield(battlefield);
    }

    public Array<Data.ActionChoice> getPossibleActionChoices(Battlefield bf, int rowActor, int colActor, Array<BattleCommand> history){
        Array<Data.ActionChoice> choices = new Array<Data.ActionChoice>();
        Unit actor;

        if(bf.isTileOccupied(rowActor, colActor)) {
            actor = bf.getUnit(rowActor, colActor);
            for (Data.ActionChoice choice : commandPool.keySet()) {
                if (!commandPool.get(choice).isEndTurnCommand() && !history.contains(commandPool.get(choice), true) && bf.canActionbePerformed(actor, rowActor, colActor, choice)) {
                    choices.add(choice);
                }
            }
        }
        return choices;
    }

    public BattleCommand get(Data.ActionChoice choice){
        return commandPool.get(choice);
    }

    // optional method
    public void setBattlefield(Battlefield battlefield){
        for(Data.ActionChoice choice : commandPool.keySet()){
            commandPool.get(choice).setBattlefield(battlefield);
        }
    }

}
