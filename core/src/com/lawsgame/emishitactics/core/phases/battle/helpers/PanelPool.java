package com.lawsgame.emishitactics.core.phases.battle.helpers;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.commands.ActorCommand;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.widgets.WidgetFactory;
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
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;

/**
 *
 * Optional panel provider :
 * - it provide an easy access to the required panels like the widgetFactory
 * - but it also serves as a pool of panel to keep the number instanciation of panel objects as low as possible
 * - it provide also some of the most common commands appliable on some panels, ready to be sent to the scheduler
 * - it provide also usefuls methods to manipulate its panels
 */
public class PanelPool implements Disposable {
    private WidgetFactory wf;
    private Group panelGroup;

    // panel instances
    public final ShortTilePanel shortTilePanel;
    public final LongTilePanel longTilePanel;
    public final ShortUnitPanel shortUnitPanel;
    public final LongUnitPanel longUnitPanel;
    public final ExperiencePanel experiencePanel;
    public final LevelUpPanel levelUpPanel;
    public final LootPanel lootPanel;
    public final ChoicePanel.ActionChoicePanel choicePanel;
    public final BattleOverPanel overPanel;

    // common commands
    public final SimpleCommand showSTP;
    public final SimpleCommand hideSTP;
    public final SimpleCommand showSUP;
    public final SimpleCommand hideSUP;




    public PanelPool(WidgetFactory widgetFactory, I18NBundle localization){
        this.wf = widgetFactory;
        this.panelGroup = new Group();
        wf.getUiStage().addActor(panelGroup);

        // main panels imp
        this.shortTilePanel = wf.getShortTilePanel();
        this.shortUnitPanel = wf.getShortUnitPanel();
        this.longUnitPanel = wf.getLongUnitPanel();
        this.longTilePanel = wf.getLongTilePanel();
        this.experiencePanel = wf.getExperiencePanel();
        this.levelUpPanel = wf.getLevelUpPanel();
        this.lootPanel = wf.getLootPanel();
        this.choicePanel = wf.getActionChoicePanel();
        this.overPanel = wf.getBattleOverPanel();


        panelGroup.addActor(shortTilePanel);
        panelGroup.addActor(shortUnitPanel);
        panelGroup.addActor(longUnitPanel);
        panelGroup.addActor(longTilePanel);
        panelGroup.addActor(experiencePanel);
        panelGroup.addActor(levelUpPanel);
        panelGroup.addActor(lootPanel);
        panelGroup.addActor(choicePanel);
        panelGroup.addActor(choicePanel.getCommandChoicePanel());
        panelGroup.addActor(overPanel);


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
        return wf.isActionPanelAvailable(choice);
    }

    /**
     * Two important remarks:
     *  1) getActionPanel MUST BE called right before applying the associated command (the BFR state must be the same)
     *  2) the action info panel is automatically attached to the stage and MUST BE removed manually
     *
     */
    public ActionInfoPanel getActionPanel(ActorCommand command){
        ActionInfoPanel panel = null;
        if(command != null) {
            panel = wf.getActionInfoPanel(command.getActionChoice());
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
