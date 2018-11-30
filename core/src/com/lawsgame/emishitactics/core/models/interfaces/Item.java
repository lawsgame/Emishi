package com.lawsgame.emishitactics.core.models.interfaces;

import com.badlogic.gdx.utils.I18NBundle;

public interface Item {
    boolean isStealable();
    boolean isDroppable();
    int getDropRate();
    String getName(I18NBundle bundle);
    String getName();
}
