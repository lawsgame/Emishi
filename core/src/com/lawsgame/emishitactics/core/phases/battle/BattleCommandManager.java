package com.lawsgame.emishitactics.core.phases.battle;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Unit;
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

    public Array<Data.ActionChoice> getChoices(Unit actor, Array<BattleCommand> history){
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
    public boolean canActionbePerformedBy(Unit actor, Data.ActionChoice choice){
        // check ABILITY REQUIREMENTS
        switch (choice){
            case WALK:                  break;
            case SWITCH_WEAPON:         if(!actor.isPromoted()) return false; break;
            case SWITCH_POSITION:       break;
            case PUSH:                  if(actor.isHorseman()) return false; break;
            case PRAY:                  if(!actor.has(Data.Ability.PRAY)) return false; break;
            case HEAL:                  if(!actor.has(Data.Ability.HEAL)) return false; break;
            case GUARD:                 if(!actor.has(Data.Ability.GUARD)) return false; break;
            case STEAL:                 if(!actor.has(Data.Ability.STEAL)) return false; break;
            case BUILD:                 if(!actor.has(Data.Ability.BUILD) || !(actor.getRemainigBuildingResources() > 0)) return false; break;
            case ATTACK:                break;
            case CHOOSE_ORIENTATION:    break;
            case USE_FOCUSED_BLOW:      if(!actor.has(Data.Ability.FOCUSED_BLOW)) return false; break;
            case USE_CRIPPLING_BLOW:    if(!actor.has(Data.Ability.CRIPPLING_BLOW)) return false; break;
            case USE_SWIRLING_BLOW:     if(!actor.has(Data.Ability.SWIRLING_BLOW)) return false; break;
            case USE_SWIFT_BLOW:        if(!actor.has(Data.Ability.SWIFT_BLOW)) return false; break;
            case USE_HEAVY_BLOW:        if(!actor.has(Data.Ability.HEAVY_BLOW)) return false; break;
            case USE_CRUNCHING_BLOW:    if(!actor.has(Data.Ability.CRUNCHING_BLOW)) return false; break;
            case USE_WAR_CRY:           if(!actor.has(Data.Ability.WAR_CRY)) return false; break;
            case USE_POISONOUS_ATTACK:  if(!actor.has(Data.Ability.POISONOUS_ATTACK)) return false; break;
            case USE_HARASS:            if(!actor.has(Data.Ability.HARASS)) return false; break;
            case USE_LINIENT_BLOW:      if(!actor.has(Data.Ability.LINIENT_BLOW)) return false; break;
            case USE_FURY:              if(!actor.has(Data.Ability.FURY)) return false; break;
            default: return false;
        }

        // check EQUIPEMENT & WEAPON REQUIREMENTS
        if((choice.getDamageTypeRequired() != Data.DamageType.NONE  && actor.getCurrentWeapon().getType() != choice.getDamageTypeRequired())
                || (!actor.getCurrentWeapon().isMeleeW() &&  choice.isMeleeOnly())
                || (!actor.getCurrentWeapon().isRangedW() &&  choice.isRangeOnly())) {
            return false;
        }

        return true;
    }

}
