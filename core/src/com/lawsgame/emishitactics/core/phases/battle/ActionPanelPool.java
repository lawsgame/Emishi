package com.lawsgame.emishitactics.core.phases.battle;

import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.ActionChoice;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.widgets.TempoActionPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ActionPanel;

import java.util.HashMap;

public class ActionPanelPool {
    private HashMap<ActionChoice, ActionPanel> panels;

    public ActionPanelPool(Viewport UIport){
        panels = new HashMap<ActionChoice, ActionPanel>();
        panels.put(ActionChoice.ATTACK, new TempoActionPanel.AttackPanel(UIport));
        panels.put(ActionChoice.HEAL, new TempoActionPanel.HealPanel(UIport));
        panels.put(ActionChoice.SWITCH_WEAPON, new TempoActionPanel.SwitchWeaponPanel(UIport));
        panels.put(ActionChoice.STEAL, new TempoActionPanel.StealPanel(UIport));
        panels.put(ActionChoice.BUILD, new TempoActionPanel.BuildPanel(UIport));
        panels.put(ActionChoice.NONE, new TempoActionPanel.InvisiblePanel(UIport));
    }

    public boolean isPanelAvailable(BattleCommand command){
        return panels.get(command.getActionChoice()) != null;
    }

    public ActionPanel getPanel(BattleCommand command){
        ActionPanel panel = panels.get(command.getActionChoice());
        if(panel == null)
            panel = panels.get(ActionChoice.NONE);
        panel.set(command);
        return panel;
    }

}
