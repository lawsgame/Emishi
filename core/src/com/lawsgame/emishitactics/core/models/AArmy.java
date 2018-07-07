package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;

public abstract class AArmy extends Observable{


    public abstract int getId();
    public abstract void setArmyType(Data.ArmyType type);
    public abstract Unit getWarlord();
    public abstract Unit getWarchief(Unit unit);
    public abstract Array<Unit> getWarChiefs();
    public abstract Array<Unit> getSquad(Unit unit);
    public abstract Array<Array<Unit>> getAllSquads();
    public abstract Array<Unit> getMobilizedUnits();
    public abstract Array<Unit> getNonMobilizedUnits();
    public abstract boolean isAlly();
    public abstract boolean isPlayerControlled();

    public abstract void appointWarLord(Unit unit);
    public abstract Data.UnitAppointmentErrorMsg appointWarChief(Unit unit);
    public abstract Data.UnitAppointmentErrorMsg appointSoldier(Unit unit, int squadId);
    public abstract boolean disengage(Unit unit);
    public abstract void disbandAllSquads();
    public abstract boolean isUnitMobilized(Unit unit);

    public abstract boolean contains(Unit unit);
    public abstract boolean add(Unit unit);
    public abstract void remove(Unit unit);

    public abstract void checkComposition();
    public abstract Banner getSquadBanner(Unit unit);
    public abstract int getBannerRange();
}
