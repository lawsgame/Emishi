package com.lawsgame.emishitactics.core.phases.battle.helpers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.helpers.AssetProvider;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.interfaces.ActionInfoPanel;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.interfaces.ChoicePanel;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.interfaces.ExperiencePanel;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.interfaces.LevelUpPanel;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.interfaces.LootPanel;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.interfaces.TilePanel;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.interfaces.UnitPanel;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.tempo.TempoActionInfoPanel;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.tempo.TempoLongTilePanel;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.tempo.TempoChoicePanel;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.tempo.TempoLongUnitPanel;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.tempo.TempoShortTilePanel;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.tempo.TempoShortUnitPanel;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.tempo.TempoExperiencePanel;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.tempo.TempoLevelUpPanel;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.tempo.TempoLootPanel;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;

import java.util.HashMap;

/**
 *
 */
public class PanelPool implements Disposable {
    private Group panelGroup;

    public final TilePanel shortTilePanel;
    public final TilePanel longTilePanel;
    public final UnitPanel shortUnitPanel;
    public final UnitPanel longUnitPanel;

    public final SimpleCommand showSTP;
    public final SimpleCommand hideSTP;
    public final SimpleCommand showSUP;
    public final SimpleCommand hideSUP;

    public final ExperiencePanel experiencePanel;
    public final LevelUpPanel levelUpPanel;
    public final LootPanel lootPanel;
    public final ChoicePanel choicePanel;

    private HashMap<Data.ActionChoice, ActionInfoPanel> panels;

    public PanelPool(Stage uiStage, AssetManager asm, AssetProvider provider, I18NBundle localization){
        this.panelGroup = new Group();
        uiStage.addActor(panelGroup);

        // main panels imp
        this.shortTilePanel = new TempoShortTilePanel(uiStage.getViewport());
        this.shortUnitPanel = new TempoShortUnitPanel(uiStage.getViewport());
        this.longUnitPanel = new TempoLongUnitPanel(uiStage.getViewport(), localization);
        this.longTilePanel = new TempoLongTilePanel(uiStage.getViewport());

        this.experiencePanel = new TempoExperiencePanel(uiStage.getViewport());
        this.levelUpPanel = new TempoLevelUpPanel(uiStage.getViewport());
        this.lootPanel = new TempoLootPanel(uiStage.getViewport());
        this.choicePanel = new TempoChoicePanel(asm);

        panelGroup.addActor(shortTilePanel);
        panelGroup.addActor(shortUnitPanel);
        panelGroup.addActor(longUnitPanel);
        panelGroup.addActor(longTilePanel);
        panelGroup.addActor(experiencePanel);
        panelGroup.addActor(levelUpPanel);
        panelGroup.addActor(lootPanel);
        panelGroup.addActor(choicePanel);


        // panel action choice imp
        panels = new HashMap<Data.ActionChoice, ActionInfoPanel>();

        ActionInfoPanel attackAIP = new TempoActionInfoPanel.AttackInfoPanel(uiStage.getViewport());
        ActionInfoPanel healAIP = new TempoActionInfoPanel.HealInfoPanel(uiStage.getViewport());
        ActionInfoPanel switchWeaponAIP = new TempoActionInfoPanel.SwitchWeaponInfoPanel(uiStage.getViewport());
        ActionInfoPanel stealAIP = new TempoActionInfoPanel.StealInfoPanel(uiStage.getViewport());
        ActionInfoPanel buildAIP = new TempoActionInfoPanel.BuildInfoPanel(uiStage.getViewport());

        panels.put(Data.ActionChoice.ATTACK, attackAIP);
        panels.put(Data.ActionChoice.HEAL, healAIP);
        panels.put(Data.ActionChoice.SWITCH_WEAPON, switchWeaponAIP);
        panels.put(Data.ActionChoice.STEAL, stealAIP);
        panels.put(Data.ActionChoice.BUILD, buildAIP);

        panelGroup.addActor(attackAIP);
        panelGroup.addActor(healAIP);
        panelGroup.addActor(switchWeaponAIP);
        panelGroup.addActor(stealAIP);
        panelGroup.addActor(buildAIP);


        // utility command for scheduler
        this.showSTP = new SimpleCommand() {
            @Override
            public void apply() {
                shortTilePanel.show();
            }
        };
        this.hideSTP = new SimpleCommand() {
            @Override
            public void apply() {
                shortTilePanel.hide();
            }
        };
        this.showSUP = new SimpleCommand() {
            @Override
            public void apply() {
                shortUnitPanel.show();
            }
        };
        this.hideSUP = new SimpleCommand() {
            @Override
            public void apply() {
                shortUnitPanel.hide();
            }
        };
    }

    //----------------------- UPDATE SHORT PANELS --------------------------------------------------------------------------------


    /**
     * rowFocus : row of the tile on which the camera focus on
     * colFocus : col of the tile on which the camera focus on
     */
    private int rowFocus = -1, colFocus = -1;
    private Unit focusUnit = null;
    public void updateShortPanels(BattlefieldRenderer bfr, int rowTarget, int colTarget, boolean erasePanelMemory){
        if(erasePanelMemory){
            rowFocus = -1;
            colFocus = -1;
            focusUnit = null;
        }

        if(bfr.getModel().isTileExisted(rowTarget, colTarget)){
            if(rowTarget != rowFocus || colTarget != colFocus || shortTilePanel.isHiding()) {
                rowFocus = rowTarget;
                colFocus = colTarget;
                shortTilePanel.hide();
                shortTilePanel.set(bfr.getModel().getTile(rowTarget, colTarget).getType());
                shortTilePanel.show();
            }

            if (bfr.getModel().isTileOccupied(rowTarget, colTarget)) {
                if (focusUnit == null || focusUnit != bfr.getModel().getUnit(rowTarget, colTarget) || shortUnitPanel.isHiding()) {
                    focusUnit = bfr.getModel().getUnit(rowTarget, colTarget);
                    shortUnitPanel.hide();
                    shortUnitPanel.set(bfr.getModel().getUnit(rowTarget, colTarget));
                    shortUnitPanel.show();
                }
            } else {
                shortUnitPanel.hide();
            }
        }

    }


    //----------------------- ACTION PAN MGMT -----------------------------------

    public boolean isActionPanelAvailable(Data.ActionChoice choice){
        return panels.get(choice) != null;
    }

    public ActionInfoPanel getActionPanel(ActorCommand command){
        ActionInfoPanel panel = panels.get(command.getActionChoice());
        if(panel != null) {
            panel.set(command);
        }
        return panel;
    }



    @Override
    public void dispose() {
        panelGroup.clear();
        panelGroup.remove();
    }
}
