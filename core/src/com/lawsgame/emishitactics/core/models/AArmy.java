package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Props;
import com.lawsgame.emishitactics.engine.patterns.Observable;

public abstract class AArmy extends Observable{


    public abstract int getId();
    public abstract boolean getAligment();
    public abstract void setAligment(boolean aligment);
    public abstract Unit getWarlord();
    public abstract Array<Unit> getWarChiefs();
    public abstract Array<Unit> getSquad(Unit unit);
    public abstract Array<Array<Unit>> getAllSquads();
    public abstract Array<Unit> getNonMobilizedUnits();
    public abstract boolean isAlly();

    public abstract void appointWarLord(Unit unit);
    public abstract Props.UnitAppointmentErrorMsg appointWarChief(Unit unit);
    public abstract Props.UnitAppointmentErrorMsg appointSoldier(Unit unit, int squadId);
    public abstract boolean disengage(Unit unit);
    public abstract void resetComposition();

    public abstract boolean mobilize(Unit unit);
    public abstract boolean contains(Unit unit);
    public abstract boolean add(Unit unit);
    public abstract void remove(Unit unit);

    public abstract Banner getSquadBanner(Unit unit);
    public abstract int getBannerRange();
}
