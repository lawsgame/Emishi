package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.constants.Data.Allegeance;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;

public class Army extends IArmy{


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
    private int buildingResources;
    private boolean playerControlled;
    private Allegeance allegeance;
    private Array<Array<IUnit>> mobilizedTroups;
    private Array<IUnit> nonMobTroups;

    public Army(Allegeance allegeance, boolean playerControlled){
        this.id = ids++;
        this.allegeance = allegeance;
        this.playerControlled = playerControlled;
        this.mobilizedTroups = new Array<Array<IUnit>>();
        this.nonMobTroups = new Array<IUnit>();
    }

    @Override
    public int getId() {
        return id;
    }



    @Override
    public IUnit getWarlord() {
        if(this.mobilizedTroups.size > 0 && this.mobilizedTroups.get(0).size > 0){
            return this.mobilizedTroups.get(0).get(0);
        }
        return null;
    }

    @Override
    public IUnit getWarchief(IUnit unit) {
        if(isUnitMobilized(unit)){
            Array<Array<IUnit>> squads = getAllSquads();
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
    public Array<IUnit> getWarChiefs() {
        Array<IUnit> warChiefs = new Array<IUnit>();
        for(int i = 0; i < mobilizedTroups.size; i++){
            if(mobilizedTroups.get(i).size > 0) {
                warChiefs.add(mobilizedTroups.get(i).get(0));
            }
        }
        return warChiefs;
    }

    @Override
    public Array<IUnit> getSquad(IUnit unit) {
        for(int i = 0; i < mobilizedTroups.size; i++){
            for(int j = 0; j < mobilizedTroups.get(i).size; j++){
                if(mobilizedTroups.get(i).get(j) == unit){
                    return  mobilizedTroups.get(i);
                }

            }
        }
        return new Array<IUnit>();
    }

    @Override
    public Array<Array<IUnit>> getAllSquads() {
        return mobilizedTroups;
    }

    @Override
    public Array<IUnit> getMobilizedUnits() {
        Array<IUnit> res = new Array<IUnit>();
        for(int i = 0; i < mobilizedTroups.size; i++){
            for(int j = 0; j < mobilizedTroups.get(i).size; j++){
                res.add(mobilizedTroups.get(i).get(j));
            }
        }
        return res;
    }

    @Override
    public Array<IUnit> getNonMobilizedUnits() {
        return nonMobTroups;
    }

    @Override
    public int getNbOfSquads(){
        return mobilizedTroups.size;
    }

    @Override
    public boolean isWarlord(IUnit unit) {
        if(unit != null) {
            if (this.mobilizedTroups.size > 0 && this.mobilizedTroups.get(0).size > 0) {
                return this.mobilizedTroups.get(0).get(0) == unit;
            }
        }
        return false;
    }

    @Override
    public boolean isWarChief(IUnit unit) {
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
    public boolean isUnitMobilized(IUnit unit){
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
    public boolean isUnitReserve(IUnit unit) {
        if((unit != null))
            return nonMobTroups.contains(unit, true);
        return false;
    }

    @Override
    public boolean contains(IUnit unit){
        return isUnitReserve(unit)|| isUnitMobilized(unit);
    }



    @Override
    public Allegeance getAllegeance() {
        return allegeance;
    }

    @Override
    public boolean isAlliedWith(Allegeance allegeance) {
        return this.allegeance == allegeance;
    }

    @Override
    public boolean isPlayerControlled(){
        return playerControlled;
    }

    @Override
    public Banner getSquadBanner(IUnit IUnit){
        if(IUnit != null) {
            Array<IUnit> squad = getSquad(IUnit);
            for (IUnit squadMember : squad) {
                if (squadMember.isStandardBearer()) {
                    return squadMember.getBanner();
                }
            }
        }
        return null;
    }

    @Override
    public int getBannerRange(){
        return (int) Math.sqrt(getWarlord().getAppLeadership());
    }

    @Override
    public int getBuildingResources() {
        return buildingResources;
    }

    @Override
    public boolean remainBuildingResources() {
        return buildingResources > 0;
    }

    @Override
    public void decrementBuildingResources() {
        buildingResources--;
    }

    @Override
    public void resetBuildingResources() {
        buildingResources = Data.NB_BUILDING_MAX;
    }


    @Override
    public boolean hasSquadStandardBearer(int squadId) {
        if(0 < squadId && squadId < getNbOfSquads()) {
            for (int i = 0; i < mobilizedTroups.get(squadId).size; i++){
                if(mobilizedTroups.get(squadId).get(i).isStandardBearer()){
                    return true;
                }
            }
        }
        return false;
    }


    // -------------- ARMY MANAGEMENT ---------------------------------------------------

    /**
     *  if not yet contain in the army
     *      0) remove the IUnit for its former army if relevant.
     *      1) add IUnit to non-mobilized troops
     *      2) set army attribute of the IUnit
     *
     * @param unit
     * @return
     */
    @Override
    public boolean add(IUnit unit) {
        boolean successfullytAdded = false;
        if( unit != null && !contains(unit) ){
            if (unit.getArmy() != null) {
                IArmy army = unit.getArmy();
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
     *  - the unit.army attribute is set to NULL
     *  - finally, the unit is removed from non-mobilized troops
     *
     * @param unit to be removed
     */
    @Override
    public void remove(IUnit unit) {
        if(unit != null && contains(unit)) {
            if(isUnitMobilized(unit)) disengage(unit);
            unit.setArmy(null);
            nonMobTroups.removeValue(unit, true);
        }
    }

    @Override
    public void appointWarLord(IUnit warlord) {
        if(isUnitReserve(warlord)) {
            disbandAllSquads();
            nonMobTroups.removeValue(warlord, true);
            mobilizedTroups.add(new Array<IUnit>());
            mobilizedTroups.get(0).add(warlord);
            updateMobilizedTroopMoral();
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
     *      add / raplace the old squad with : the new squad with the unit as war chief
     *
     * @param unit
     * @return
     */
    @Override
    public void appointWarChief(IUnit unit, int squadIndex){

        if(isUnitReserve(unit)) {
            if (squadIndex == 0) {
                appointWarLord(unit);
            } else if(getWarlord() != null){
                if (0 < squadIndex && squadIndex < mobilizedTroups.size || getNbOfSquads() < getWarlord().getMaxWarChiefs() + 1){
                    nonMobTroups.removeValue(unit, true);
                    if (0 < squadIndex && squadIndex < mobilizedTroups.size) {
                        //reset an older squad
                        if (mobilizedTroups.get(squadIndex).size > 0){
                            disengage(mobilizedTroups.get(squadIndex).get(0));
                        }
                        mobilizedTroups.get(squadIndex).add(unit);
                    } else {
                        //add a new squad
                        Array<IUnit> newSquad = new Array<IUnit>();
                        newSquad.add(unit);
                        mobilizedTroups.add(newSquad);
                    }
                    updateMobilizedTroopMoral();
                }
            }
        }
    }

    @Override
    public void appointWarChief(IUnit IUnit) {
        appointWarChief(IUnit, -1);
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
     * @return
     */
    @Override
    public void appointSoldier(IUnit soldier, int squadID, int unitID) {
        if(isUnitReserve(soldier)) {
            if (unitID == 0) {
                if (squadID == 0) {
                    appointWarLord(soldier);
                } else {
                    appointWarChief(soldier, squadID);
                }
            } else {
            /*
            the squadID and the unitID are those of a mere soldier, the request can then be treated here.
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
                        IUnit formerUnit = this.mobilizedTroups.get(squadID).removeIndex(unitID);
                        this.nonMobTroups.add(formerUnit);
                        this.mobilizedTroups.get(squadID).insert(unitID, soldier);
                        nonMobTroups.removeValue(soldier, true);

                    } else if (mobilizedTroups.get(squadID).size < mobilizedTroups.get(squadID).get(0).getMaxSoldiersAs(squadID == 0)) {
                    /*CASE 2 :
                    IF : the squad has a slot available for a new recruit
                     - the newbie is added
                     */
                        this.mobilizedTroups.get(squadID).add(soldier);
                        nonMobTroups.removeValue(soldier, true);
                    }

                    updateMobilizedTroopMoral();

                }
            }
        }
    }

    @Override
    public void appointSoldier(IUnit unit, int squadIndex) {
        appointSoldier(unit, squadIndex, -1);
    }


    /**
     * update composition if one of members sees changes in critical stat, notably leadership
     */
    @Override
    public void checkComposition() {
        Array<Array<IUnit>> squads = getAllSquads();
        Array<IUnit> squad;

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


    /**
     * disengagement consist in removing a IUnit form the mobilized army
     * 1) FIRST we check that the given IUnit is:
     *  - not null
     *  - mobilized
     *  - not a warlord
     *
     * 2) Then the method fetch the squad's IUnit and the IUnit indexes
     *
     * 3) then IF those indexes have been founded, 2 cases could rise:
     *      IF the IUnit is a war chief THEN the whole squad is demobilized
     *      ELSE the IUnit is demobilized
     *
     *
     * @param unit
     * @return
     */
    @Override
    public boolean disengage(IUnit unit) {
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
                    Array<IUnit> squad = getSquad(unit);
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
     * Re-integrate all mobilized troops in the on-mobilized array and clear the former.
     */
    @Override
    public void disbandAllSquads() {
        IUnit unit;
        for(int i = 0 ; i <  mobilizedTroups.size; i++){
            for(int j = 0; j <  mobilizedTroups.get(i).size; j++){
                unit = mobilizedTroups.get(i).removeIndex(j);
                nonMobTroups.add(unit);
                j--;
            }
        }
        mobilizedTroups.clear();
    }

    private void updateMobilizedTroopMoral(){
        for(int i = 0; i < mobilizedTroups.size; i++){
            for(int j = 0; j < mobilizedTroups.get(0).size; j++){
                mobilizedTroups.get(i).get(j).resetCurrentMoral();
            }
        }
    }


    @Override
    public String toString(){
        String str = "\n|CURRENT ARMY";
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
}

