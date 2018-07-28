package com.lawsgame.emishitactics.core.phases.battle;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.MoveCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

import java.util.HashMap;

/*
 * add a command ? => see the constructor below
 */
public class BattleCommandManager {
    private HashMap<Data.ActionChoice, BattleCommand> commandPool;

    public BattleCommandManager(BattlefieldRenderer battlefieldRenderer, AnimationScheduler scheduler){
        commandPool = new HashMap<Data.ActionChoice, BattleCommand>();

        /*
         add BC entries here :
          : commandPool.put(command.getActionChoice(), command);
          */
        commandPool.put(Data.ActionChoice.MOVE, new MoveCommand(battlefieldRenderer, scheduler));

        setBattlefield(battlefieldRenderer);
    }

    public Array<Data.ActionChoice> getChoices(IUnit actor, Array<BattleCommand> history){
        Array<Data.ActionChoice> choices = new Array<Data.ActionChoice>();
        for (Data.ActionChoice choice : commandPool.keySet()) {
            if (!commandPool.get(choice).isEndTurnCommandOnly() && !history.contains(commandPool.get(choice), true) && canActionbePerformedBy(actor, choice)) {
                choices.add(choice);
            }
        }
        return choices;
    }

    public BattleCommand get(Data.ActionChoice choice){
        BattleCommand command = commandPool.get(choice);
        if(command != null) {
            command.init();
            return command;
        }
        return null;
    }

    // optional method
    public void setBattlefield(BattlefieldRenderer battlefieldRenderer){
        for(Data.ActionChoice choice : commandPool.keySet()){
            commandPool.get(choice).setBattlefield(battlefieldRenderer);
        }
    }

    /**
     * there is 3 types of requirements for an action to be performable by an actor
     *  - history type: if unit has already moved or acted
     *  - abiility type
     *  - equipement type (weapon mainly)
     *  - target  type (checked by the method: BattleCommand.atActionRange() and Battlefield.isTargetValid())
     *
     * @return whether or not an action can be performed by the actor regardless the actor's history or target availability.
     */
    public boolean canActionbePerformedBy(IUnit actor, Data.ActionChoice choice){
        boolean actionPerformable = false;

        // check ABILITY REQUIREMENTS
        if(choice.getCost() < actor.getOAChargingBarPoints()) return false;

        switch (choice){
            case MOVE:                  if(!actor.hasMoved()) actionPerformable = true; break;
            case SWITCH_WEAPON:         if(!actor.hasActed() && actor.isPromoted()) actionPerformable = true; break;
            case SWITCH_POSITION:       actionPerformable = true; break;
            case PUSH:                  if(!actor.hasActed() && !actor.isHorseman()) actionPerformable = true; break;
            case HEAL:                  if(!actor.hasActed() && actor.has(Data.Ability.HEAL)) actionPerformable = true; break;
            case GUARD:                 if(!actor.hasActed() && actor.has(Data.Ability.GUARD)) actionPerformable = true; break;
            case STEAL:                 if(!actor.hasActed() && actor.has(Data.Ability.STEAL)) actionPerformable = true; break;
            case BUILD:                 if(!actor.hasActed() && actor.has(Data.Ability.BUILD) && actor.isMobilized() && actor.getArmy().isThereStillbuildingResources()) actionPerformable = true; break;
            case COVER:                 if(!actor.hasActed() && actor.has(Data.Ability.COVER)) return false; break;
            case ATTACK:                if(!actor.hasActed()) actionPerformable = true; break;
            case CHOOSE_ORIENTATION:    actionPerformable = true; break;
            case END_TURN:              actionPerformable = true; break;
            default:
        }

        return actionPerformable;
    }

}
