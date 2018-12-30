package com.lawsgame.emishitactics.core.models.interfaces;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.Banner;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;

public abstract class MilitaryForce extends Model{


    public abstract int getId();
    public abstract String getName();
    public abstract Unit getWarlord();
    public abstract Unit getWarchief(Unit unit);
    public abstract Unit getWarchief(int squadIndex);
    public abstract Array<Unit> getWarChiefs();
    public abstract Array<Unit> getSquad(Unit unit, boolean stillFighting);
    public abstract Array<Array<Unit>> getAllSquads();
    public abstract Array<Unit> getMobilizedUnits(boolean stillFighting);
    public abstract Array<Unit> getNonMobilizedUnits();
    public abstract int getNbOfSquads();
    public abstract int getSquadSize(Unit unit, boolean stillFighting);
    public abstract boolean isWarlord(Unit unit);
    public abstract boolean isWarChief(Unit unit);
    public abstract boolean isUnitMobilized(Unit unit);
    public abstract boolean isUnitReserve(Unit unit);
    public abstract boolean contains(Unit unit);
    public abstract boolean isSquadIndexValid(int squadIndex);

    public abstract Data.Affiliation getAffiliation();
    public abstract Data.Allegiance getAllegiance();
    public abstract void setAllegiance(Data.Allegiance a);
    public abstract boolean isAlliedWith(Data.Affiliation affiliation);
    public abstract boolean isPlayerControlled();
    public abstract boolean hasSquadStandardBearer(int squadId, boolean stillFighting);
    public abstract Banner getSquadBanner(Unit unit, boolean stillFighting);
    public abstract int getBannerRange(int squadIndex);
    public abstract int getBannerRange(Unit unit);
    public abstract boolean isDeployedTroopsStillFighting(Battlefield battlefield);
    public abstract void setDone(boolean done, boolean notifyObserves);
    public abstract boolean isDone();

    public abstract boolean add(Unit unit);
    public abstract void remove(Unit unit);
    public abstract void appointWarLord(Unit unit);
    public abstract void appointWarChief(Unit unit, int squadId);
    public abstract void appointWarChief(Unit unit);
    public abstract boolean appointSoldier(Unit unit, int squadId, int unitId);
    public abstract boolean appointSoldier(Unit unit, int squadId);
    public abstract boolean disengage(Unit unit);
    public abstract void disbandAllSquads();

    protected abstract void checkComposition();
    public abstract void setLeadershipConditionEnabled(boolean enabled);

    public abstract void replenishMoral(boolean turnBeginning);
    public abstract void updateActionPoints();
    public abstract int getSquadExceedingCapacity(Unit unit);
    public abstract boolean isSquadOversized(Unit unit);

    public abstract String toLongString();
}
