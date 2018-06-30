package com.lawsgame.emishitactics.core.renderers.interfaces;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.engine.GameElement;


/**
 * there is two ways to update the rendering of a unit
 *  - An automatized way through the use of the Observer pattern when the battlefield is updated
 *  - A manual way by calling triggerAnimation()
 */
public abstract class UnitRenderer extends Renderer<Unit> implements GameElement {

    public UnitRenderer(Unit model) {
        super(model);
    }

}
