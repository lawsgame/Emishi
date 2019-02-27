package com.lawsgame.emishitactics.core.phases.battle.helpers;

import com.badlogic.gdx.assets.AssetManager;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.widgets.panels.fronts.ShortUnitPanel;

public interface BattlefieldLoader {
    Battlefield load(AssetManager asm, int bfId);
    void addEvents(AssetManager asm, BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory inventory, ShortUnitPanel sup);
}
