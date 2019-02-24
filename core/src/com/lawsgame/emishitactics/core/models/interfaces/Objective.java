package com.lawsgame.emishitactics.core.models.interfaces;

import com.lawsgame.emishitactics.core.models.Battlefield;

public interface Objective {
    int getValue();
    boolean isMet(Battlefield battlefield);
}
