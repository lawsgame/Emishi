package com.lawsgame.emishitactics.core.phases.battle.oldpan.tempo;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.phases.battle.BattlePhase;
import com.lawsgame.emishitactics.core.phases.battle.oldpan.interfaces.ChoicePanel;

public class TempoChoicePanel extends ChoicePanel {

    public TempoChoicePanel(AssetManager asm) {
        super();
        TextureAtlas uiAtlas = asm.get(Assets.ATLAS_TEMPO_UI);
        Skin skin = new Skin(uiAtlas);

        //setTiles button style
        style = new TextButton.TextButtonStyle();
        style.up = skin.getDrawable(Assets.UI_BUTTON_UP);
        style.down = skin.getDrawable(Assets.UI_BUTTON_DOWN);
        style.font = BattlePhase.testFont;

    }

    @Override
    public void setLayout() {
        setFillParent(true);
        align(Align.right | Align.top);
        padTop(10f);
        padRight(25f);
    }

    public void addButton(TextButton button){
        add(button).width(150f).height(30);
        row();
    }
}
