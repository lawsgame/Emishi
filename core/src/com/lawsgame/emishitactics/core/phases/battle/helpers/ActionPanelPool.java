package com.lawsgame.emishitactics.core.phases.battle.helpers;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Data.ActionChoice;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ActionInfoPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoActionInfoPanel;

import java.util.HashMap;

public class ActionPanelPool {
    private HashMap<ActionChoice, ActionInfoPanel> panels;

    public ActionPanelPool(Viewport UIport){
        panels = new HashMap<ActionChoice, ActionInfoPanel>();
        panels.put(ActionChoice.ATTACK, new TempoActionInfoPanel.AttackInfoPanel(UIport));
        panels.put(ActionChoice.HEAL, new TempoActionInfoPanel.HealInfoPanel(UIport));
        panels.put(ActionChoice.SWITCH_WEAPON, new TempoActionInfoPanel.SwitchWeaponInfoPanel(UIport));
        panels.put(ActionChoice.STEAL, new TempoActionInfoPanel.StealInfoPanel(UIport));
        panels.put(ActionChoice.BUILD, new TempoActionInfoPanel.BuildInfoPanel(UIport));
    }

    public boolean isPanelAvailable(ActorCommand command){
        return panels.get(command.getActionChoice()) != null;
    }

    public ActionInfoPanel getPanel(ActorCommand command){
        ActionInfoPanel panel = panels.get(command.getActionChoice());
        if(panel != null)
            panel.set(command);
        return panel;
    }

}
