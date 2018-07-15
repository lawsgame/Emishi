package com.lawsgame.emishitactics.core.phases.battle.widgets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.phases.battle.renderers.TempoAreaRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.AreaRenderer;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.AreaWidget;

public class TempoAreaWidget extends AreaWidget {
    public Area model;
    public AreaRenderer renderer;


    public TempoAreaWidget(Battlefield battlefield, Assets.AreaColor color){
        this(battlefield, color, new Array<int[]>());
    }


    public TempoAreaWidget(Battlefield battlefield, Assets.AreaColor color, Array<int[]> tiles){
        model = new Area(battlefield, tiles);
        renderer = new TempoAreaRenderer(model, color);
    }


    @Override
    public void render(SpriteBatch batch) {
        renderer.render(batch);
    }

    @Override
    public void addTile(int r, int c) {
        model.addTile(r , c);
    }

    @Override
    public void setTile(int row, int col) {
        model.set(row, col);
    }

    @Override
    public void setTiles(Array<int[]> area) {
        model.set(area);
    }


    @Override
    public boolean contains(int r, int c) {
        return model.contains(r, c);
    }

    @Override
    public void setVisible(boolean visible) {
        renderer.setVisible(visible);
    }

    @Override
    public Area getModel() {
        return model;
    }

    @Override
    public AreaRenderer getRenderer() {
        return renderer;
    }
}
