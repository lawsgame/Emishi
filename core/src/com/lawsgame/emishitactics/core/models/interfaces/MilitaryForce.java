package com.lawsgame.emishitactics.core.models.interfaces;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;
import com.lawsgame.emishitactics.core.models.Banner;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;

public abstract class MilitaryForce {

    public abstract String getName();
    public abstract Unit getWarlord();
    public abstract Unit getWarchief(Unit unit);
    public abstract Unit getWarchief(int squadIndex);
    public abstract Array<Unit> getWarChiefs();
    public abstract Array<Unit> getSquad(Unit unit, boolean stillFighting);
    public abstract Array<Array<Unit>> getAllSquads();
    public abstract Array<Unit> getRegularTroops(boolean stillFighring);
    public abstract Array<Unit> getMobilizedUnits(boolean stillFighting);
    public abstract Array<Unit> getAllSkirmisher(boolean stillFighting);
    public abstract Array<Unit> getReserve();
    public abstract int getNbOfSquads();
    public abstract int getSquadSize(Unit unit, boolean stillFighting);
    public abstract boolean isWarlord(Unit unit);
    public abstract boolean isWarChief(Unit unit);

    public boolean contains(Unit unit){
        return isUnitReserve(unit) || isUnitRegular(unit) || isUnitSkirmisher(unit);
    }

    public abstract boolean isUnitRegular(Unit unit);
    public abstract boolean isUnitSkirmisher(Unit unit);
    public abstract boolean isUnitReserve(Unit unit);
    public abstract boolean isIndexSquadValid(int squadIndex);

    public abstract Data.Affiliation getAffiliation();
    public abstract Data.Allegiance getAllegiance();
    public abstract void setAllegiance(Data.Allegiance a);
    public abstract boolean isAlliedWith(Data.Affiliation affiliation);
    public abstract boolean isPlayerControlled();
    public abstract Banner getSquadBanner(Unit unit, boolean stillFighting);
    public abstract int getBannerRange(int squadIndex);
    public abstract int getBannerRange(Unit unit);
    public abstract void setDone(boolean done, boolean notifyObserves);
    public abstract boolean isDone();

    public abstract boolean add(Unit unit);
    public abstract void remove(Unit unit);
    public abstract void appointWarLord(Unit unit);
    public abstract void appointWarChief(Unit unit, int squadId);
    public abstract void appointWarChief(Unit unit);
    public abstract boolean appointSoldier(Unit unit, int squadId, int unitId);
    public abstract boolean appointSoldier(Unit unit, int squadId);
    public abstract void appointSkirmisher(Unit unit);
    public abstract boolean disengage(Unit unit);
    public abstract void disbandAllSquads();

    public abstract void replenishMoral(boolean turnBeginning);
    public abstract void updateActionPoints();

    public abstract String toLongString();
}
