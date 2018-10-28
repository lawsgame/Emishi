package com.lawsgame.emishitactics.core.models.interfaces;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Banner;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;

public abstract class IArmy extends Model{


    public abstract int getId();

    public abstract IUnit getWarlord();
    public abstract IUnit getWarchief(IUnit unit);
    public abstract IUnit getWarchief(int squadIndex);
    public abstract Array<IUnit> getWarChiefs();
    public abstract Array<IUnit> getSquad(IUnit unit, boolean stillFighting);
    public abstract Array<Array<IUnit>> getAllSquads();
    public abstract Array<IUnit> getMobilizedUnits(boolean stillFighting);
    public abstract Array<IUnit> getNonMobilizedUnits();
    public abstract int getNbOfSquads();
    public abstract int getSquadSize(IUnit unit, boolean stillFighting);
    public abstract boolean isWarlord(IUnit unit);
    public abstract boolean isWarChief(IUnit unit);
    public abstract boolean isUnitMobilized(IUnit unit);
    public abstract boolean isUnitReserve(IUnit unit);
    public abstract boolean contains(IUnit unit);
    public abstract boolean isSquadIndexValid(int squadIndex);

    public abstract Data.Affiliation getAffiliation();
    public abstract Data.Allegiance getAllegiance();
    public abstract void setAllegiance(Data.Allegiance a);
    public abstract boolean isAlliedWith(Data.Affiliation affiliation);
    public abstract boolean isPlayerControlled();
    public abstract boolean hasSquadStandardBearer(int squadId, boolean stillFighting);
    public abstract Banner getSquadBanner(IUnit unit, boolean stillFighting);
    public abstract int getBannerRange(int squadIndex);
    public abstract int getBannerRange(IUnit unit);
    public abstract int getBuildingResources();
    public abstract boolean isThereStillbuildingResources();
    public abstract void decrementBuildingResources();
    public abstract void resetBuildingResources();
    public abstract boolean isDeployedTroopsStillFighting(Battlefield battlefield);
    public abstract void setDone(boolean done, boolean notifyObserves);
    public abstract boolean isDone();

    public abstract boolean add(IUnit unit);
    public abstract void remove(IUnit unit);
    public abstract void appointWarLord(IUnit unit);
    public abstract void appointWarChief(IUnit unit, int squadId);
    public abstract void appointWarChief(IUnit unit);
    public abstract boolean appointSoldier(IUnit unit, int squadId, int unitId);
    public abstract boolean appointSoldier(IUnit unit, int squadId);
    public abstract boolean disengage(IUnit unit);
    public abstract void disbandAllSquads();

    public abstract void checkComposition();
    public abstract void setLeadershipConditionEnabled(boolean enabled);

    public abstract void replenishMoral(boolean turnBeginning);
    public abstract void updateActionPoints();
    public abstract int getSquadExceedingCapacity(IUnit unit);
    public abstract boolean isSquadOversized(IUnit unit);
    public abstract Array<Notification.ApplyDamage> dealDamageUponUnitRemoval(IUnit removed);

    public abstract String toShortString();
}
