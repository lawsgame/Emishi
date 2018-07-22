package com.lawsgame.emishitactics.core.phases.battle;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;

import java.util.HashMap;

/*
 * add a command ? => see the constructor below
 */
public class BattleCommandManager {
    private HashMap<Data.ActionChoice, BattleCommand> commandPool;

    public BattleCommandManager(BattlefieldRenderer battlefieldRenderer){
        commandPool = new HashMap<Data.ActionChoice, BattleCommand>();

        /*
         add BC entries here :
          : commandPool.put(command.getActionChoice(), command);
          */

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
     *  - abiility type
     *  - equipement type (weapon mainly)
     *  - target  type (checked by the method: BattleCommand.atActionRange() and Battlefield.isTargetValid())
     *
     * @return whether or not an action can be performed by the actor regardless the actor's history or target availability.
     */
    public boolean canActionbePerformedBy(IUnit actor, Data.ActionChoice choice){
        // check ABILITY REQUIREMENTS
        switch (choice){
            case WALK:                  break;
            case SWITCH_WEAPON:         if(!actor.isPromoted()) return false; break;
            case SWITCH_POSITION:       break;
            case PUSH:                  if(actor.isHorseman()) return false; break;
            case HEAL:                  if(!actor.has(Data.SupportAbility.HEAL)) return false; break;
            case GUARD:                 if(!actor.has(Data.SupportAbility.GUARD)) return false; break;
            case STEAL:                 if(!actor.has(Data.SupportAbility.STEAL)) return false; break;
            case BUILD:                 if(!actor.has(Data.SupportAbility.BUILD) && actor.isMobilized() && actor.getArmy().remainBuildingResources()) return false; break;
            case COVER:                 if(!(actor.getCurrentWeapon().getRangeMax() > 1)) return false; break;
            case ATTACK:                break;
            case CHOOSE_ORIENTATION:    break;
            default: return false;
        }

        return true;
    }

}
