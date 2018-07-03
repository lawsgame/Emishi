package com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.engine.GameElement;

public interface AreaRenderer extends GameElement{

    void addTile( int r, int c);
    void addTiles(Array<int[]> area);
    void reset();
}
