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

    public Array<Data.ActionChoice> getPossibleActionChoices(Unit actor, Array<BattleCommand> history){
        Array<Data.ActionChoice> choices = new Array<Data.ActionChoice>();
        for (Data.ActionChoice choice : commandPool.keySet()) {
            if (!commandPool.get(choice).isEndTurnCommand() && !history.contains(commandPool.get(choice), true) && canActionbePerformedBy(actor, choice)) {
                choices.add(choice);
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

    /**
     * there is 3 types of requirements for an action to be performable by an actor
     *  - abiility type
     *  - equipement type (weapon mainly)
     *  - target / battlefield type (checked by the method: Battlefield.atActionRange() and Battlefield.isTargetValid())
     *
     * @return whether or not an action can be performed by the actor regardless the actor's history or target availability.
     */
    public boolean canActionbePerformedBy(Unit actor, Data.ActionChoice choice){
        // check ABILITY REQUIREMENTS
        switch (choice){
            case WALK:                  break;
            case SWITCH_WEAPON:         if(!actor.isPromoted()) return false; break;
            case SWITCH_POSITION:       break;
            case PUSH:                  if(actor.isHorseman()) return false; break;
            case PRAY:                  if(!actor.has(Data.PassiveAbility.PRAYER)) return false; break;
            case HEAL:                  if(!actor.has(Data.PassiveAbility.HEALER)) return false; break;
            case STEAL:                 if(!actor.has(Data.PassiveAbility.THIEF)) return false; break;
            case BUILD:                 if(!actor.has(Data.PassiveAbility.ENGINEER) && !actor.isBuildingResourcesConsumed()) return false; break;
            case ATTACK:                break;
            case CHOOSE_ORIENTATION:    break;
            case CHOOSE_STANCE:         break;
            case USE_FOCUSED_BLOW:      if(!actor.has(Data.OffensiveAbility.FOCUSED_BLOW)) return false; break;
            case USE_CRIPPLING_BLOW:    if(!actor.has(Data.OffensiveAbility.CRIPPLING_BLOW)) return false; break;
            case USE_SWIRLING_BLOW:     if(!actor.has(Data.OffensiveAbility.SWIRLING_BLOW)) return false; break;
            case USE_SWIFT_BLOW:        if(!actor.has(Data.OffensiveAbility.SWIFT_BLOW)) return false; break;
            case USE_HEAVY_BLOW:        if(!actor.has(Data.OffensiveAbility.HEAVY_BLOW)) return false; break;
            case USE_CRUNCHING_BLOW:    if(!actor.has(Data.OffensiveAbility.CRUNCHING_BLOW)) return false; break;
            case USE_WAR_CRY:           if(!actor.has(Data.OffensiveAbility.WAR_CRY)) return false; break;
            case USE_POISONOUS_ATTACK:  if(!actor.has(Data.OffensiveAbility.POISONOUS_ATTACK)) return false; break;
            case USE_GUARD_BREAK:       if(!actor.has(Data.OffensiveAbility.GUARD_BREAK)) return false; break;
            case USE_LINIENT_BLOW:      if(!actor.has(Data.OffensiveAbility.LINIENT_BLOW)) return false; break;
            case USE_FURY:              if(!actor.has(Data.OffensiveAbility.FURY)) return false; break;
            default:

                return false;
        }

        // check EQUIPEMENT & WEAPON REQUIREMENTS
        if((choice.getDamageTypeRequired() != Data.DamageType.NONE  && actor.getCurrentWeapon().getType() != choice.getDamageTypeRequired())
                || (actor.getCurrentWeapon().isRangedW() &&  choice.isMeleeWeaponEquipedRequired())) {
            return false;
        }

        return true;
    }

}
