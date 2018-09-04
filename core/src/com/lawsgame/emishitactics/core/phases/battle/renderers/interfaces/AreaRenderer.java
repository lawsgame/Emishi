package com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces;

import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.engine.GameRenderableEntity;
import com.lawsgame.emishitactics.engine.rendering.Renderer;

public abstract class AreaRenderer extends Renderer<Area> implements GameRenderableEntity{
    public AreaRenderer(Area model) {
        super(model);
    }

}
