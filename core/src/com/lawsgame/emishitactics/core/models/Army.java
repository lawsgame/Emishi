package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data.Affiliation;
import com.lawsgame.emishitactics.core.models.interfaces.MilitaryForce;
import com.lawsgame.emishitactics.core.models.Unit;

public class Army extends MilitaryForce {


    /**
     *  mob troups =
     *  [ WL S S ... ]
     *  [ WC S S ... ]
     *  [ WC S S ... ]
     *  [ WC S S ... ]
     *
     */

    private static int ids = 0;

    private int id;
    private boolean playerControlled;
    private final Data.Affiliation affiliation;
    private Data.Allegiance allegiance;
    private Array<Array<Unit>> mobilizedTroups;
    private Array<Unit> nonMobTroups;
    private boolean ldCondEnabled;

    public Army(Affiliation affiliation){
        this.id = ids++;
        this.affiliation = affiliation;
        this.playerControlled = false;
        this.mobilizedTroups = new Array<Array<Unit>>();
        this.nonMobTroups = new Array<Unit>();
        this.ldCondEnabled = false;
        this.allegiance = Data.Allegiance.getStandard();
    }

    public static Army createPlayerArmyTemplate(){
        Army playerArmy = new Army(Affiliation.ALLY);
        playerArmy.playerControlled = true;
        playerArmy.ldCondEnabled = true;
        return playerArmy;
    }

    @Override
    public int getId() {
        return id;
    }



    @Override
    public Unit getWarlord() {
        if(this.mobilizedTroups.size > 0 && this.mobilizedTroups.get(0).size > 0){
            return this.mobilizedTroups.get(0).get(0);
        }
        return null;
    }

    @Override
    public Unit getWarchief(Unit unit) {
        if(isUnitMobilized(unit)){
            Array<Array<Unit>> squads = getAllSquads();
            for(int i = 0; i < squads.size; i++){
                for(int j = 0; j < squads.get(i).size; j++){
                    if(unit == squads.get(i).get(j)){
                        return squads.get(i).get(0);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Unit getWarchief(int squadIndex) {
        if(isSquadIndexValid(squadIndex)){
            return mobilizedTroups.get(squadIndex).get(0);
        }
        return null;
    }

    @Override
    public Array<Unit> getWarChiefs() {
        Array<Unit> warChiefs = new Array<Unit>();
        for(int i = 0; i < mobilizedTroups.size; i++){
            if(mobilizedTroups.get(i).size > 0) {
                warChiefs.add(mobilizedTroups.get(i).get(0));
            }
        }
        return warChiefs;
    }

    @Override
    public Array<Unit> getSquad(Unit unit, boolean stillFighting) {
        Array<Unit> squad = new Array<Unit>();
        int squadIndex = -1;
        loop:
        {
            for (int i = 0; i < mobilizedTroups.size; i++) {
                for (int j = 0; j < mobilizedTroups.get(i).size; j++) {
                    if (mobilizedTroups.get(i).get(j) == unit) {
                        squadIndex = i;
                        break loop;
                    }

                }
            }
        }
        if(squadIndex != -1){
            for(int i = 0; i < mobilizedTroups.get(squadIndex).size; i++){
                if(!mobilizedTroups.get(squadIndex).get(i).isOutOfAction() || !stillFighting) {
                    squad.add(mobilizedTroups.get(squadIndex).get(i));
                }
            }

        }
        return squad;
    }

    @Override
    public Array<Array<Unit>> getAllSquads() {
        return mobilizedTroups;
    }

    @Override
    public Array<Unit> getMobilizedUnits(boolean stillFighting) {
        Array<Unit> res = new Array<Unit>();
        for(int i = 0; i < mobilizedTroups.size; i++){
            for(int j = 0; j < mobilizedTroups.get(i).size; j++){
                if(!stillFighting || !mobilizedTroups.get(i).get(j).isOutOfAction())
                    res.add(mobilizedTroups.get(i).get(j));
            }
        }
        return res;
    }

    @Override
    public Array<Unit> getNonMobilizedUnits() {
        return nonMobTroups;
    }

    @Override
    public int getNbOfSquads(){
        return mobilizedTroups.size;
    }

    @Override
    public int getSquadSize(Unit unit, boolean stillFighting) {
        for (int i = 0; i < mobilizedTroups.size; i++) {
            for (int j = 0; j < mobilizedTroups.get(i).size; j++) {
                if (mobilizedTroups.get(i).get(j) == unit) {
                    if (stillFighting) {

                        int squadSize = 0;
                        for (int k = 0; k < mobilizedTroups.get(i).size; k++) {
                            if(!mobilizedTroups.get(i).get(k).isOutOfAction()){
                                squadSize++;
                            }
                        }
                        return squadSize;
                    } else {

                        return mobilizedTroups.get(i).size;
                    }
                }

            }
        }

        return 0;
    }

    @Override
    public boolean isWarlord(Unit unit) {
        if(unit != null) {
            if (this.mobilizedTroups.size > 0 && this.mobilizedTroups.get(0).size > 0) {
                return this.mobilizedTroups.get(0).get(0) == unit;
            }
        }
        return false;
    }

    @Override
    public boolean isWarChief(Unit unit) {
        if(unit != null) {
            for (int i = 0; i < mobilizedTroups.size; i++) {
                if (this.mobilizedTroups.get(i).size > 0 && this.mobilizedTroups.get(i).get(0) == unit) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public boolean isUnitMobilized(Unit unit){
        if(unit != null) {
            for (int i = 0; i < getNbOfSquads(); i++) {
                if (mobilizedTroups.get(i).contains(unit, true)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isUnitReserve(Unit unit) {
        if((unit != null))
            return nonMobTroups.contains(unit, true);
        return false;
    }

    @Override
    public boolean contains(Unit unit){
        return isUnitReserve(unit)|| isUnitMobilized(unit);
    }

    @Override
    public boolean isSquadIndexValid(int squadIndex) {
        return squadIndex < mobilizedTroups.size && 0 <= squadIndex && mobilizedTroups.get(squadIndex).size > 0;
    }


    @Override
    public Affiliation getAffiliation() {
        return affiliation;
    }

    @Override
    public Data.Allegiance getAllegiance() {
        return allegiance;
    }

    @Override
    public void setAllegiance(Data.Allegiance a) {
        this.allegiance = a;
    }

    @Override
    public boolean isAlliedWith(Affiliation affiliation) {
        return this.affiliation == affiliation;
    }

    @Override
    public boolean isPlayerControlled(){
        return playerControlled;
    }

    @Override
    public Banner getSquadBanner(Unit Unit, boolean stillFighting){
        if(Unit != null) {
            Array<Unit> squad = getSquad(Unit, stillFighting);
            for (int i = 0; i < squad.size; i++) {
                if (squad.get(i).isStandardBearer()) {
                    return squad.get(i).getBanner();
                }
            }
        }
        return null;
    }

    @Override
    public int getBannerRange(int squadIndex){
        boolean validIndex = 0 <= squadIndex && squadIndex < getNbOfSquads();
        return (validIndex) ? (int) Math.sqrt(mobilizedTroups.get(squadIndex).get(0).getAppCharisma()) : 0;
    }

    @Override
    public int getBannerRange(Unit unit){
        return (isUnitMobilized(unit)) ? getBannerRange(unit.getSquadIndex()) : 0;
    }

    @Override
    public boolean isDeployedTroopsStillFighting(Battlefield battlefield) {
        for(int r = 0; r < battlefield.getNbRows(); r++){
            for(int c = 0; c < battlefield.getNbColumns(); c++){
                if(battlefield.isTileOccupied(r, c)
                        && isUnitMobilized(battlefield.getUnit(r,c ))
                        && !battlefield.getUnit(r,c ).isOutOfAction()){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void setDone(boolean done, boolean notifyObservers) {
        for(int i = 0; i < mobilizedTroups.size; i++){
            for(int j = 0; j < mobilizedTroups.get(i).size; j++){
                mobilizedTroups.get(i).get(j).setActed(done);
                mobilizedTroups.get(i).get(j).setMoved(done);
                if(notifyObservers){
                    mobilizedTroups.get(i).get(j).notifyAllObservers(Notification.Done.get(done));
                }
            }
        }
    }

    @Override
    public boolean isDone() {
        for(int i = 0; i < mobilizedTroups.size; i++){
            for(int j = 0; j < mobilizedTroups.get(i).size; j++){
                if(!mobilizedTroups.get(i).get(j).isDone() && !mobilizedTroups.get(i).get(j).isOutOfAction()){

                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public boolean hasSquadStandardBearer(int squadId, boolean stillFighting) {
        Unit squadMember;
        if(0 < squadId && squadId < getNbOfSquads()) {
            for (int i = 0; i < mobilizedTroups.get(squadId).size; i++){
                squadMember = mobilizedTroups.get(squadId).get(i);
                if(squadMember.isStandardBearer() && (!squadMember.isOutOfAction() || !stillFighting)){
                    return true;
                }
            }
        }
        return false;
    }


    // -------------- ARMY MANAGEMENT ---------------------------------------------------

    /**
     *  if not yet contain in the army
     *      0) remove the Unit for its former army if relevant.
     *      1) addExpGained Unit to non-mobilized troops
     *      2) addExpGained army attribute of the Unit
     *
     * @param unit
     * @return
     */
    @Override
    public boolean add(Unit unit) {
        boolean successfullytAdded = false;
        if( unit != null && !contains(unit) ){
            if (unit.getArmy() != null) {
                MilitaryForce army = unit.getArmy();
                army.remove(unit);
            }
            unit.setArmy(this);
            nonMobTroups.add(unit);
            successfullytAdded = true;
        }
        return successfullytAdded ;
    }

    /**
     * IF the unit :
     *  - is not null
     *  - belongs to this army
     *
     *  THEN:
     *  - the unit is first disengage from the mobilized troop if mobilized
     *  - the unit.army attribute is addExpGained to NULL
     *  - finally, the unit is removed from non-mobilized troops
     *
     * @param unit to be removed
     */
    @Override
    public void remove(Unit unit) {
        if(unit != null && contains(unit)) {
            if(isUnitMobilized(unit)) disengage(unit);
            unit.setArmy(null);
            nonMobTroups.removeValue(unit, true);
        }
    }

    @Override
    public void appointWarLord(Unit warlord) {
        if(isUnitReserve(warlord)) {
            disbandAllSquads();
            nonMobTroups.removeValue(warlord, true);
            mobilizedTroups.add(new Array<Unit>());
            mobilizedTroups.get(0).add(warlord);
            replenishMoral(false);
        }
    }

    /**
     *  IF :
     *   - the army has a warlord
     *   - the unit is NOT NULL
     *   - the unit is yet a warchief
     *   - the unit belongs to this army
     *
     *   THEN:
     *
     *   IF :  squadID == 0 => appoint as warlord
     *   ELSE IF : a warchief slot is available OR the squadID is one of the currentWCs
     *
     *   THEN:
     *      remove the unit from any army
     *      addExpGained / raplace the old squad with : the new squad with the unit as war chief
     *
     * @param unit
     * @return
     */
    @Override
    public void appointWarChief(Unit unit, int squadIndex){

        if(isUnitReserve(unit)) {
            if (squadIndex == 0) {
                appointWarLord(unit);
            } else if(getWarlord() != null){
                if ((0 < squadIndex && squadIndex < mobilizedTroups.size)
                        || getNbOfSquads() <= getWarlord().getMaxWarChiefs()
                        || ldCondEnabled){
                    nonMobTroups.removeValue(unit, true);
                    if (0 < squadIndex && squadIndex < mobilizedTroups.size) {
                        //reset an older squad
                        if (mobilizedTroups.get(squadIndex).size > 0){
                            disengage(mobilizedTroups.get(squadIndex).get(0));
                        }
                        mobilizedTroups.get(squadIndex).add(unit);
                    } else {
                        //addExpGained a new squad
                        Array<Unit> newSquad = new Array<Unit>();
                        newSquad.add(unit);
                        mobilizedTroups.add(newSquad);
                    }
                    replenishMoral(false);
                }
            }
        }
    }

    @Override
    public void appointWarChief(Unit Unit) {
        appointWarChief(Unit, -1);
    }

    /**
     *
     *   the unit aims to be appointed
     *    - warlord
     *    - war chief
     *    - mere soldier
     *
     *
     * @param soldier
     * @return return true if the squad is over capacity
     */
    @Override
    public boolean appointSoldier(Unit soldier, int squadID, int unitID) {
        boolean squadOversized = false;
        if(isUnitReserve(soldier)) {
            if (unitID == 0) {
                if (squadID == 0) {
                    appointWarLord(soldier);
                } else {
                    appointWarChief(soldier, squadID);
                }
            } else {
            /*
            the squadID and the unitID are those of a mere soldier, the request can then be improveCondition here.
            IF:  the squad ID is invalid =>  the request is canceled
             */
                if (0 <= squadID && squadID < getNbOfSquads()) {
                /*
                THEN two cases are to be considered :
                 - CASE 1 :the unit replace another one
                 - CASE 2 : the unit is simply added to the already mobilized troops
                 */

                    if (0 <= unitID && unitID < mobilizedTroups.get(squadID).size) {
                    /*CASE 1 :
                    IF : the unitID belongs to a existing soldier
                     - the former member of the squad is demobilized
                     - the newbie is added
                     */
                        Unit formerUnit = this.mobilizedTroups.get(squadID).removeIndex(unitID);
                        this.nonMobTroups.add(formerUnit);
                        this.mobilizedTroups.get(squadID).insert(unitID, soldier);
                        nonMobTroups.removeValue(soldier, true);

                    } else  {
                    /*CASE 2 :
                    IF : the squad has a slot available for a new recruit
                     - the newbie is added
                     */

                        this.mobilizedTroups.get(squadID).add(soldier);
                        nonMobTroups.removeValue(soldier, true);
                        squadOversized = mobilizedTroups.get(squadID).size > getWarchief(squadID).getMaxSoldiersAs(squadID == 0);
                    }
                    replenishMoral(false);

                }
            }
        }
        return squadOversized;
    }

    @Override
    public boolean appointSoldier(Unit unit, int squadIndex) {
        return appointSoldier(unit, squadIndex, -1);
    }


    /**
     * update composition if one of members sees change in critical stat, notably leadership
     */
    @Override
    public void checkComposition() {
        Array<Array<Unit>> squads = getAllSquads();
        Array<Unit> squad;

        // the warlord leadership is too low => disband the army
        if(squads.size > getWarlord().getMaxWarChiefs() + 1)
            disbandAllSquads();

        for(int i = 0; i < squads.size; i++){
            squad = squads.get(i);
            if( squad.get(0).getMaxSoldiersAs(i == 0) < squad.size){
                // the warchief leadership is too low => disband the squad
                disengage(squad.get(0));
            }

        }
    }

    @Override
    public void setLeadershipConditionEnabled(boolean enabled) {
        this.ldCondEnabled = enabled;
    }



    /**
     * disengagement consist in removing a Unit form the mobilized army
     * 1) FIRST we performEvent that the given Unit is:
     *  - not null
     *  - mobilized
     *  - not a warlord
     *
     * 2) Then the method fetch the squad's Unit and the Unit indexes
     *
     * 3) then IF those indexes have been founded, 2 cases could rise:
     *      IF the Unit is a war chief THEN the whole squad is demobilized
     *      ELSE the Unit is demobilized
     *
     *
     * @param unit
     * @return
     */
    @Override
    public boolean disengage(Unit unit) {
        if(isUnitMobilized(unit)) {

            int squadId = -1;
            int unitId = -1;
            for (int i = 0; i < mobilizedTroups.size; i++) {
                for (int j = 0; j < mobilizedTroups.get(i).size; j++) {
                    if (unit == mobilizedTroups.get(i).get(j)) {
                        squadId = i;
                        unitId = j;
                    }
                }
            }

            if (squadId != -1 && unitId != -1) {
                if(isWarlord(unit)) {
                    disbandAllSquads();
                } else if (isWarChief(unit)) {
                    Array<Unit> squad = getSquad(unit, false);
                    mobilizedTroups.removeIndex(squadId);
                    nonMobTroups.addAll(squad);
                } else {
                    mobilizedTroups.get(squadId).removeValue(unit, true);
                    nonMobTroups.add(unit);

                }
                return true;
            }
        }
        return false;
    }

    /**
     * Re-integrate all mobilized troops in the on-mobilized array and reset the former.
     */
    @Override
    public void disbandAllSquads() {
        Unit unit;
        for(int i = 0 ; i <  mobilizedTroups.size; i++){
            for(int j = 0; j <  mobilizedTroups.get(i).size; j++){
                unit = mobilizedTroups.get(i).removeIndex(j);
                nonMobTroups.add(unit);
                j--;
            }
        }
        mobilizedTroups.clear();
    }

    @Override
    public void replenishMoral(boolean turnBeginning) {
        for(int i = 0; i < mobilizedTroups.size; i++){
            for(int j = 0; j < mobilizedTroups.get(i).size; j++){
                mobilizedTroups.get(i).get(j).replenishMoral(turnBeginning);
            }
        }
    }

    @Override
    public void updateActionPoints() {
        for(int i = 0; i < mobilizedTroups.size; i++){
            for(int j = 0; j < mobilizedTroups.get(i).size; j++){
                if(!mobilizedTroups.get(i).get(j).isOutOfAction()) {
                    mobilizedTroups.get(i).get(j).addActionPoints(Data.AP_REGEN);
                }
            }
        }
    }

    @Override
    public int getSquadExceedingCapacity(Unit unit) {
        int exceedance = 0;
        Unit warchief = getWarchief(unit);
        if(warchief != null){
            exceedance = getSquadSize(unit, false) - warchief.getMaxSoldiersAs(warchief.isWarlord());
        }
        return (exceedance > 0 ) ? exceedance : 0;
    }

    @Override
    public boolean isSquadOversized(Unit unit) {
        return getSquadExceedingCapacity(unit) > 0;
    }

    @Override
    public String toLongString(){
        String str = "CURRENT ARMY";
        str += "\nLeader : "+getWarlord();
        str += "\nMobilized troops : "+getMobilizedUnits(true).size;
        str += "\nReserve : "+getNonMobilizedUnits().size+"\n";
        for(int i = 0 ; i <  mobilizedTroups.size; i++){
            for(int j = 0; j <  mobilizedTroups.get(i).size; j++){
                if(j == 0){
                    if(i == 0){
                        str += "\n|> ";
                    }else{
                        str += "\n|--> ";
                    }
                }else{
                    str += "\n|----> ";
                }
                str += mobilizedTroups.get(i).get(j).getName();
            }
        }
        str += "\n|\n|RESERVE ARMY";
        for(int i = 0; i <  nonMobTroups.size; i++){
            str += "\n|  "+nonMobTroups.get(i).getName();
        }
        return str+"\n";
    }

    @Override
    public String toString(){
        return "Army of "+((getWarlord() != null) ? getWarlord().getName() : "");
    }
}

