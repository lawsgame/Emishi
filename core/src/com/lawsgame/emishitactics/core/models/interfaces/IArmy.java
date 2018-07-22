package com.lawsgame.emishitactics.core.models.interfaces;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data.Allegeance;
import com.lawsgame.emishitactics.core.models.Banner;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;

public abstract class IArmy extends Observable{


    public abstract int getId();

    public abstract IUnit getWarlord();
    public abstract IUnit getWarchief(IUnit unit);
    public abstract Array<IUnit> getWarChiefs();
    public abstract Array<IUnit> getSquad(IUnit unit);
    public abstract Array<Array<IUnit>> getAllSquads();
    public abstract Array<IUnit> getMobilizedUnits();
    public abstract Array<IUnit> getNonMobilizedUnits();
    public abstract int getNbOfSquads();
    public abstract boolean isWarlord(IUnit unit);
    public abstract boolean isWarChief(IUnit unit);
    public abstract boolean isUnitMobilized(IUnit unit);
    public abstract boolean isUnitReserve(IUnit unit);
    public abstract boolean contains(IUnit unit);

    public abstract Allegeance getAllegeance();
    public abstract boolean isAlliedWith(Allegeance allegeance);
    public abstract boolean isPlayerControlled();
    public abstract boolean hasSquadStandardBearer(int squadId);
    public abstract Banner getSquadBanner(IUnit unit);
    public abstract int getBannerRange();
    public abstract int getBuildingResources();
    public abstract boolean remainBuildingResources();
    public abstract void decrementBuildingResources();
    public abstract void resetBuildingResources();


    public abstract boolean add(IUnit unit);
    public abstract void remove(IUnit unit);
    public abstract void appointWarLord(IUnit unit);
    public abstract void appointWarChief(IUnit unit, int squadId);
    public abstract void appointWarChief(IUnit unit);
    public abstract void appointSoldier(IUnit unit, int squadId, int unitId);
    public abstract void appointSoldier(IUnit unit, int squadId);
    public abstract boolean disengage(IUnit unit);
    public abstract void disbandAllSquads();

    public abstract void checkComposition();
}
