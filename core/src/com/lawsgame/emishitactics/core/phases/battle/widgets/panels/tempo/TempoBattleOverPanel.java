package com.lawsgame.emishitactics.core.phases.battle.widgets.panels.tempo;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.fronts.BattleOverPanel;

public class TempoBattleOverPanel extends BattleOverPanel {
    private static int WIDTH = 450;
    private static int HEIGHT = 400;
    private static float FADE_DURATION = 0.2f;

    private Label commandentLabel;

    public TempoBattleOverPanel(Viewport stageUIViewport, Skin skin, float fadingDuration, int width, int height) {
        super(stageUIViewport, fadingDuration, width, height);
        this.commandentLabel = new Label("RESULT", skin, "default");
        setBackground(skin.getDrawable(Assets.UI_BLACK_BACKGROUND));
        add(commandentLabel).center().expand();
    }

    public static BattleOverPanel create(Viewport uiPort, Skin skin){
        return new TempoBattleOverPanel(uiPort, skin, FADE_DURATION, WIDTH, HEIGHT);
    }

    @Override
    protected void setContent(Array<Unit> warchiefs, int[] expOld, int[] expNew) {
        StringBuilder builder = new StringBuilder("BATTLE OUTCOME:\n\n");
        builder.append("I - Leadership inscreases\n\n");
        if(warchiefs != null && expOld != null  && expNew != null && warchiefs.size == expNew.length && expNew.length == expOld.length) {
            int LdOld = 1;
            int LdNew = 1;
            for (int i = 0; i < warchiefs.size; i++) {
                for(int j = 0; j < Data.EXP_REQUIRED_LEADERSHIP.length; j++){
                    if(Data.EXP_REQUIRED_LEADERSHIP[j] < expOld[i]){
                        LdOld++;
                    }
                    if(Data.EXP_REQUIRED_LEADERSHIP[j] < expNew[i]){
                        LdNew++;
                    }
                }
                builder.append(warchiefs.get(i).getName());
                builder.append(" : Ld = ");
                builder.append(LdOld);
                builder.append(" (");
                builder.append(expOld[i]);
                builder.append(") => ");
                builder.append(LdNew);
                builder.append(" (");
                builder.append(expNew[i]);
                builder.append(")\n");
                LdOld = 1;
                LdNew = 1;
            }
        }
        commandentLabel.setText(builder.toString());
        builder.setLength(0);
    }
}
