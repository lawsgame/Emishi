package com.lawsgame.emishitactics.core.models.interfaces;

import com.badlogic.gdx.utils.I18NBundle;

public interface Item {
    String getName(I18NBundle bundle);
    boolean isStealable();
    int getDropRate();
}
