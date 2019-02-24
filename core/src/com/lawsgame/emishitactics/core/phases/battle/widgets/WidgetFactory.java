package com.lawsgame.emishitactics.core.phases.battle.widgets;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.fronts.ActionInfoPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.fronts.BattleOverPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.fronts.ChoicePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.fronts.ExperiencePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.fronts.LevelUpPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.fronts.LongTilePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.fronts.LongUnitPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.fronts.LootPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.fronts.ShortTilePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.fronts.ShortUnitPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.tempo.TempoAIP;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.tempo.TempoActionChoiceP;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.tempo.TempoBattleOverPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.tempo.TempoExpP;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.tempo.TempoLootP;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.tempo.TempoLevelUpP;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.tempo.TempoLongTileP;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.tempo.TempoLongUnitPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.tempo.TempoShortTileP;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.tempo.TempoShortUnitP;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 *
 *
 * MISSION
 *
 * provide the application with widgets that it instanciate for the latter.
 *
 *
 */
public class WidgetFactory {
    private Stage uiStage;
    private Skin uiSkin;
    private BattlefieldRenderer bfr;
    private HashMap<Data.ActionChoice, Class<? extends ActionInfoPanel>> actionPanelTypes;

    public WidgetFactory(Stage uiStage, Skin uiSkin, BattlefieldRenderer bfr) {
        this.uiStage = uiStage;
        this.uiSkin = uiSkin;
        this.bfr = bfr;

        // panel action choice imp
        actionPanelTypes = new HashMap<Data.ActionChoice, Class<? extends ActionInfoPanel>>();
        actionPanelTypes.put(Data.ActionChoice.ATTACK, TempoAIP.class);
        actionPanelTypes.put(Data.ActionChoice.HEAL, TempoAIP.class);
        actionPanelTypes.put(Data.ActionChoice.SWITCH_WEAPON, TempoAIP.class);
        actionPanelTypes.put(Data.ActionChoice.STEAL, TempoAIP.class);
        actionPanelTypes.put(Data.ActionChoice.BUILD, TempoAIP.class);
    }

    // -------***$$$  PANELS  $$$***-------------

    public LongTilePanel getLongTilePanel(){
        return TempoLongTileP.create(uiStage.getViewport(), uiSkin);
    }

    public ShortTilePanel getShortTilePanel(){
        return TempoShortTileP.create(uiStage.getViewport(), uiSkin);
    }

    public LongUnitPanel getLongUnitPanel(){
        return TempoLongUnitPanel.create(uiStage.getViewport(), uiSkin);
    }

    public ShortUnitPanel getShortUnitPanel(){
        return TempoShortUnitP.create(uiStage.getViewport(), uiSkin);
    }

    public LootPanel getLootPanel(){
        return TempoLootP.create(uiStage.getViewport(), uiSkin);
    }

    public ExperiencePanel getExperiencePanel(){
        return TempoExpP.create(uiStage.getViewport(), uiSkin);
    }

    public LevelUpPanel getLevelUpPanel(){
        return TempoLevelUpP.create(uiStage.getViewport(), uiSkin);
    }

    public ChoicePanel.ActionChoicePanel getActionChoicePanel(){
        return TempoActionChoiceP.create(uiStage.getViewport(), uiSkin);
    }

    public BattleOverPanel getBattleOverPanel(){
        return TempoBattleOverPanel.create(uiStage.getViewport(), uiSkin);
    }

    public boolean isActionPanelAvailable(Data.ActionChoice choice){
        return actionPanelTypes.get(choice) != null;
    }

    public ActionInfoPanel getActionInfoPanel(Data.ActionChoice choice){
        ActionInfoPanel panel = null;

        try {
            Class<? extends ActionInfoPanel> c = actionPanelTypes.get(choice);
            Class[] paramTypes = new Class[]{Viewport.class, Skin.class, BattlefieldRenderer.class};
            Constructor<? extends ActionInfoPanel> constructor = c.getConstructor(paramTypes);
            Object[] paramArray = new Object[]{uiStage.getViewport(), uiSkin, bfr};
            panel = constructor.newInstance(paramArray);


        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return panel;
    }

    public Stage getUiStage() {
        return uiStage;
    }

    public Skin getUiSkin() {
        return uiSkin;
    }
}
