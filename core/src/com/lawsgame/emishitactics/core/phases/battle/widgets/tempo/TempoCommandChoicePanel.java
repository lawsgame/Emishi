package com.lawsgame.emishitactics.core.phases.battle.widgets.tempo;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Align;

public class TempoCommandChoicePanel extends TempoChoicePanel {
    protected int buttonIndex;

    public TempoCommandChoicePanel(AssetManager asm, int buttonIndex) {
        super(asm);
        this.buttonIndex = 0;
        this.buttonIndex = buttonIndex;
    }

    @Override
    public void setLayout() {
        setFillParent(true);
        align(Align.right | Align.top);
        padTop(15f + buttonIndex*30f);
        padRight(15f);
    }

}
