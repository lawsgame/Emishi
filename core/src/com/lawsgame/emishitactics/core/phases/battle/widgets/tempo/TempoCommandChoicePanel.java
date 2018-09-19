package com.lawsgame.emishitactics.core.phases.battle.widgets.tempo;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.phases.battle.BattlePhase;
import com.lawsgame.emishitactics.core.phases.battle.interactions.SelectActionBIS;

public class TempoCommandChoicePanel extends SelectActionBIS.CommandChoicePanel {
    protected int buttonIndex;

    public TempoCommandChoicePanel(AssetManager asm) {
        super();
        this.buttonIndex = 0;
        if(style == null) {
            TextureAtlas uiAtlas = asm.get(Assets.ATLAS_TEMPO_UI);
            Skin skin = new Skin(uiAtlas);

            //setTiles button style
            style = new TextButton.TextButtonStyle();
            style.up = skin.getDrawable(Assets.UI_BUTTON_UP);
            style.down = skin.getDrawable(Assets.UI_BUTTON_DOWN);
            style.font = BattlePhase.testFont;
        }
    }

    public void setButtonIndex(int buttonIndex) {
        this.buttonIndex = buttonIndex;
    }

    @Override
    public void setLayout() {
        setFillParent(true);
        align(Align.right | Align.top);
        padTop(15f + buttonIndex*30f);
        padRight(15f);
    }

    public void addButton(TextButton button){
        add(button).width(150f).height(30);
        row();
    }
}
