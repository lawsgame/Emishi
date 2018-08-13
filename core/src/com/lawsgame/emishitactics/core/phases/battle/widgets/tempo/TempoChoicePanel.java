package com.lawsgame.emishitactics.core.phases.battle.widgets.tempo;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.models.ActionChoice;
import com.lawsgame.emishitactics.core.phases.battle.BattlePhase;

public class TempoChoicePanel extends Table {

    public TempoChoicePanel(AssetManager asm){

        TextureAtlas uiAtlas = asm.get(Assets.ATLAS_UI);
        Skin skin = new Skin(uiAtlas);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = skin.getDrawable(Assets.UI_BUTTON_UP);
        style.down = skin.getDrawable(Assets.UI_BUTTON_DOWN);
        style.font = BattlePhase.testFont;
        setFillParent(true);
        align(Align.right | Align.top);
        padTop(5f);
        padRight(15f);
        TextButton button;
        for(int i = 0; i < ActionChoice.values().length - 1; i++){
            button = new TextButton(ActionChoice.values()[i].getKey().toLowerCase(), style);
            add(button).prefWidth(150f);
            row();
        }

    }

}
