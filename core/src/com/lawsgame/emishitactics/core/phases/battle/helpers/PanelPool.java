package com.lawsgame.emishitactics.core.phases.battle.helpers;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels.ActionInfoPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels.ExperiencePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels.LevelUpPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels.LongTilePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels.LongUnitPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels.LootPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels.ShortTilePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.panels.ShortUnitPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoAIP;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoEP;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoLP;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoLUP;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoLongTilePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoLongUnitPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoSTP;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoSUP;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;

import java.util.HashMap;

/**
 *
 */
public class PanelPool implements Disposable {
    private Skin uiskin;
    private Viewport uiport;
    private Group panelGroup;

    public final ShortTilePanel shortTilePanel;
    public final LongTilePanel longTilePanel;
    public final ShortUnitPanel shortUnitPanel;
    public final LongUnitPanel longUnitPanel;

    public final SimpleCommand showSTP;
    public final SimpleCommand hideSTP;
    public final SimpleCommand showSUP;
    public final SimpleCommand hideSUP;

    public final ExperiencePanel experiencePanel;
    public final LevelUpPanel levelUpPanel;
    public final LootPanel lootPanel;
    //public final ChoicePanel choicePanel;

    private HashMap<Data.ActionChoice, Class<? extends ActionInfoPanel>> actionPanelTypes;

    public PanelPool(Stage uiStage, Skin uiskin, I18NBundle localization){
        this.uiskin = uiskin;
        this.uiport = uiStage.getViewport();
        this.panelGroup = new Group();
        uiStage.addActor(panelGroup);

        // main panels imp
        this.shortTilePanel = TempoSTP.create(uiport, this.uiskin);
        this.shortUnitPanel = TempoSUP.create(uiport, this.uiskin);
        this.longUnitPanel = TempoLongUnitPanel.create(uiport, this.uiskin);
        this.longTilePanel = TempoLongTilePanel.create(uiport, this.uiskin);

        this.experiencePanel = TempoEP.create(uiport, uiskin);
        this.levelUpPanel = TempoLUP.create(uiport, uiskin);
        this.lootPanel = TempoLP.create(uiport, uiskin);
        //this.choicePanel = null;

        panelGroup.addActor(shortTilePanel);
        panelGroup.addActor(shortUnitPanel);
        panelGroup.addActor(longUnitPanel);
        panelGroup.addActor(longTilePanel);
        panelGroup.addActor(experiencePanel);
        panelGroup.addActor(levelUpPanel);
        panelGroup.addActor(lootPanel);
        //panelGroup.addActor(choicePanel);


        // panel action choice imp
        actionPanelTypes = new HashMap<Data.ActionChoice, Class<? extends ActionInfoPanel>>();
        actionPanelTypes.put(Data.ActionChoice.ATTACK, TempoAIP.class);
        actionPanelTypes.put(Data.ActionChoice.HEAL, TempoAIP.class);
        actionPanelTypes.put(Data.ActionChoice.SWITCH_WEAPON, TempoAIP.class);
        actionPanelTypes.put(Data.ActionChoice.STEAL, TempoAIP.class);
        actionPanelTypes.put(Data.ActionChoice.BUILD, TempoAIP.class);


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
                shortTilePanel.update(bfr.getModel().getTile(rowTarget, colTarget).getType());
                shortTilePanel.show();
            }

            if (bfr.getModel().isTileOccupied(rowTarget, colTarget)) {
                if (focusUnit == null || focusUnit != bfr.getModel().getUnit(rowTarget, colTarget) || shortUnitPanel.isHiding()) {
                    focusUnit = bfr.getModel().getUnit(rowTarget, colTarget);
                    shortUnitPanel.hide();
                    shortUnitPanel.update(bfr.getModel().getUnit(rowTarget, colTarget));
                    shortUnitPanel.show();
                }
            } else {
                shortUnitPanel.hide();
            }
        }

    }


    //----------------------- ACTION PAN MGMT -----------------------------------

    public boolean isActionPanelAvailable(Data.ActionChoice choice){
        return actionPanelTypes.get(choice) != null;
    }

    /**
     * Two important remarks:
     *  1) getActionPanel MUST BE called right before applying the associated command (the BFR state must be the same)
     *  2) the action info panel is automatically attached to the stage and must be removed manually
     *
     */
    public ActionInfoPanel getActionPanel(ActorCommand command){
        ActionInfoPanel panel = null;
        if(command != null) {
            panel = ActionInfoPanel.create(uiport, uiskin, command.getBFR(), actionPanelTypes.get(command.getActionChoice()));
            panel.setContent(command);
            panelGroup.addActor(panel);
        }
        return panel;
    }



    @Override
    public void dispose() {
        panelGroup.clear();
        panelGroup.remove();
    }
}
