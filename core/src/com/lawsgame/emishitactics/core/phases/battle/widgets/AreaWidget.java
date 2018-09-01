package com.lawsgame.emishitactics.core.phases.battle.widgets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.renderers.tempo.TempoAreaRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.AreaRenderer;

public class AreaWidget {
    public Area model;
    public AreaRenderer renderer;


    public AreaWidget(Battlefield battlefield, Data.AreaType color){
        this(battlefield, color, new Array<int[]>());
    }


    public AreaWidget(Battlefield battlefield, Data.AreaType areaType, Array<int[]> tiles){
        model = new Area(battlefield, areaType, tiles);
        renderer = new TempoAreaRenderer(model);
    }

    public AreaWidget(Area area){
        model = area;
        renderer = new TempoAreaRenderer(area);
    }


    public void render(SpriteBatch batch) {
        renderer.render(batch);
    }

    public void addTile(int r, int c, boolean notifyObservers) { model.addTile(r , c, notifyObservers); }

    public void setTile(int row, int col, boolean notifyObservers) {
        model.set(row, col, notifyObservers);
    }

    public void setTile(int row, int col, Data.AreaType type, boolean notifyObservers) {
        model.set(row, col, type, notifyObservers);
    }

    public void setTiles(Array<int[]> area, boolean notifyObservers) {
        model.set(area, notifyObservers);
    }

    public void setTiles(Array<int[]> area, Data.AreaType type, boolean notifyObservers) {
        model.set(area, type, notifyObservers);
    }

    public boolean contains(int r, int c) {
        return model.contains(r, c);
    }

    public void setVisible(boolean visible) {
        renderer.setVisible(visible);
    }

    public Area getModel() {
        return model;
    }

    public AreaRenderer getRenderer() {
        return renderer;
    }
}
