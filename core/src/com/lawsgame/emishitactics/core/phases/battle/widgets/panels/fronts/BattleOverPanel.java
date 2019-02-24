package com.lawsgame.emishitactics.core.phases.battle.widgets.panels.fronts;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.interfaces.FadingPanel;

public abstract class BattleOverPanel extends FadingPanel {
    public BattleOverPanel(Viewport stageUIViewport, float fadingDuration, int width, int height) {
        super(stageUIViewport, fadingDuration, width, height);
    }

    public void update(final Array<Unit> warchiefs, final int[] expOld, final int[] expNew) {
        final BattleOverPanel p = this;
        this.awaitingActions.add(new Action() {
            @Override
            public boolean act(float delta) {
                p.setContent(warchiefs, expOld, expNew);
                return true;
            }
        });
    }

    protected abstract void setContent(Array<Unit> warchiefs, int[] expOld, int[] expNew);
}
