package com.lawsgame.emishitactics.core.models;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.StringKey;
import com.lawsgame.emishitactics.core.models.Data.Affiliation;
import com.lawsgame.emishitactics.core.models.Data.Allegiance;
import com.lawsgame.emishitactics.core.models.interfaces.MilitaryForce;

public class Army extends MilitaryForce {


    /**
     * Struture :
     *  > affiliation : which sides the army fights, typically, an alliance
     *  -> allegeance : entity under which the army fights, typically, an nation;
     *
     *  mob troups =
     *  [ WL S S ... ]
     *  [ WC S S ... ]
     *  [ WC S S ... ]
     *  [ WC S S ... ]
     *
     */

    private String keyName;
    private boolean playerControlled;
    private final Affiliation affiliation;
    private Allegiance allegiance;
    private Array<Array<Unit>> regularTroops;
    private Array<Unit> skirmishers;
    private Array<Unit> reserve;
    private boolean ldCondEnabled;

    public Army(Affiliation affiliation, String keyName){
        this.keyName = keyName;
        this.affiliation = affiliation;
        this.playerControlled = false;
        this.regularTroops = new Array<Array<Unit>>();
        this.skirmishers = new Array<Unit>();
        this.reserve = new Array<Unit>();
        this.ldCondEnabled = false;
        this.allegiance = Data.Allegiance.getStandard();
    }

    public static Army createPlayerArmyTemplate(){
        Army playerArmy = new Army(Affiliation.ALLY, StringKey.PLAYER_ARMY_NAME);
        playerArmy.playerControlled = true;
        playerArmy.ldCondEnabled = true;
        playerArmy.allegiance = Allegiance.PLAYER_ALLIGIANCE;
        return playerArmy;
    }

    @Override
    public String getName() {
        return keyName;
    }

    @Override
    public Unit getWarlord() {
        if(this.regularTroops.size > 0 && this.regularTroops.get(0).size > 0){
            return this.regularTroops.get(0).get(0);
        }
        return null;
    }

    @Override
    public Unit getWarchief(Unit unit) {
        Array<Array<Unit>> squads = getAllSquads();
        for(int i = 0; i < squads.size; i++){
            for(int j = 0; j < squads.get(i).size; j++){
                if(unit == squads.get(i).get(j)){
                    return squads.get(i).get(0);
                }
            }
        }
        return null;
    }

    @Override
    public Unit getWarchief(int squadIndex) {
        if(isIndexSquadValid(squadIndex)){
            return regularTroops.get(squadIndex).get(0);
        }
        return null;
    }

    @Override
    public Array<Unit> getWarChiefs() {
        Array<Unit> warChiefs = new Array<Unit>();
        for(int i = 0; i < regularTroops.size; i++){
            if(regularTroops.get(i).size > 0) {
                warChiefs.add(regularTroops.get(i).get(0));
            }
        }
        return warChiefs;
    }

    @Override
    public Array<Unit> getSquad(Unit unit, boolean stillFighting) {
        Array<Unit> squad = new Array<Unit>();
        for (int i = 0; i < regularTroops.size; i++) {
            for (int j = 0; j < regularTroops.get(i).size; j++) {
                if (regularTroops.get(i).get(j) == unit) {
                    if(stillFighting){
                        for(int k = 0; k < regularTroops.get(i).size; k++){
                            if(!regularTroops.get(i).get(k).isOutOfAction()){
                                squad.add(regularTroops.get(i).get(k));
                            }
                        }
                    }else {
                        squad.addAll(regularTroops.get(i));
                    }
                    break;
                }
            }
        }
        return squad;
    }

    @Override
    public Array<Array<Unit>> getAllSquads() {
        return regularTroops;
    }

    @Override
    public Array<Unit> getRegularTroops(boolean stillFighting) {
        Array<Unit> res = new Array<Unit>();
        for(int i = 0; i < regularTroops.size; i++){
            for(int j = 0; j < regularTroops.get(i).size; j++){
                if(!stillFighting || !regularTroops.get(i).get(j).isOutOfAction()) {
                    res.add(regularTroops.get(i).get(j));
                }
            }
        }
        for(int i = 0; i < skirmishers.size; i++){
            if(!stillFighting || !skirmishers.get(i).isOutOfAction()) {
                res.add(skirmishers.get(i));
            }
        }
        return res;
    }


    @Override
    public Array<Unit> getAllSkirmisher(boolean stillFighting) {
        Array<Unit> res = new Array<Unit>();
        for(int i = 0; i < skirmishers.size; i++){
            if(!stillFighting || !skirmishers.get(i).isOutOfAction()) {
                res.add(skirmishers.get(i));
            }
        }
        return res;
    }

    @Override
    public Array<Unit> getMobilizedUnits(boolean stillFighting) {
        Array<Unit> res = getRegularTroops(stillFighting);
        res.addAll(getAllSkirmisher(stillFighting));
        return res;
    }

    @Override
    public Array<Unit> getReserve() {
        return reserve;
    }

    @Override
    public int getNbOfSquads(){
        return regularTroops.size;
    }

    @Override
    public int getSquadSize(Unit unit, boolean stillFighting) {
        for (int i = 0; i < regularTroops.size; i++) {
            for (int j = 0; j < regularTroops.get(i).size; j++) {
                if (regularTroops.get(i).get(j) == unit) {
                    if (stillFighting) {
                        int squadSize = 0;
                        for (int k = 0; k < regularTroops.get(i).size; k++) {
                            if(!regularTroops.get(i).get(k).isOutOfAction()){
                                squadSize++;
                            }
                        }
                        return squadSize;
                    } else {
                        return regularTroops.get(i).size;
                    }
                }

            }
        }

        return 0;
    }

    @Override
    public boolean isWarlord(Unit unit) {
        if(unit != null) {
            if (this.regularTroops.size > 0 && this.regularTroops.get(0).size > 0) {
                return this.regularTroops.get(0).get(0) == unit;
            }
        }
        return false;
    }

    @Override
    public boolean isWarChief(Unit unit) {
        if(unit != null) {
            for (int i = 0; i < regularTroops.size; i++) {
                if (this.regularTroops.get(i).size > 0 && this.regularTroops.get(i).get(0) == unit) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isUnitMobilized(Unit unit){
        return isUnitSkirmisher(unit) || isUnitRegular(unit);
    }

    @Override
    public boolean isUnitRegular(Unit unit){
        for (int i = 0; i < getNbOfSquads(); i++) {
            if (regularTroops.get(i).contains(unit, true)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isUnitSkirmisher(Unit unit) {
        return skirmishers.contains(unit, true);
    }

    @Override
    public boolean isUnitReserve(Unit unit) {
        return reserve.contains(unit, true);
    }

    @Override
    public boolean isIndexSquadValid(int squadIndex) {
        return squadIndex < regularTroops.size
                && 0 <= squadIndex
                && regularTroops.get(squadIndex).size > 0;
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
    public boolean isAlliedWith(Affiliation a) {
        return this.affiliation == a;
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
        return (validIndex) ? (int) Math.sqrt(regularTroops.get(squadIndex).get(0).getAppStat(Data.UnitStat.LEADERSHIP)) : 0;
    }

    @Override
    public int getBannerRange(Unit unit){
        return (isUnitRegular(unit)) ? getBannerRange(unit.getSquadIndex()) : 0;
    }

    @Override
    public void setDone(boolean done, boolean notifyObservers) {
        for(int i = 0; i < regularTroops.size; i++){
            for(int j = 0; j < regularTroops.get(i).size; j++){
                regularTroops.get(i).get(j).setActed(done);
                regularTroops.get(i).get(j).setMoved(done);
                if(notifyObservers){
                    regularTroops.get(i).get(j).notifyAllObservers(Notification.Done.get(done));
                }
            }
        }
        for(int i = 0; i < skirmishers.size ; i++){
            skirmishers.get(i).setActed(done);
            skirmishers.get(i).setMoved(done);
            if(notifyObservers){
                skirmishers.get(i).notifyAllObservers(Notification.Done.get(done));
            }
        }
    }

    @Override
    public boolean isDone() {
        for(int i = 0; i < regularTroops.size; i++){
            for(int j = 0; j < regularTroops.get(i).size; j++){
                if(!regularTroops.get(i).get(j).isDone() && !regularTroops.get(i).get(j).isOutOfAction()){
                    return false;
                }
            }
        }
        return true;
    }

    // -------------- ARMY MANAGEMENT ---------------------------------------------------

    /**
     *  if not yet contain in the army
     *      0) remove the unit for its former army if relevant.
     *      1) addExpGained unit to non-mobilized troops
     *      2) addExpGained army attribute of the unit
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
            reserve.add(unit);
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
            if(isUnitRegular(unit) || isUnitSkirmisher(unit))
                disengage(unit);
            unit.setArmy(null);
            reserve.removeValue(unit, true);
        }
    }

    @Override
    public void appointWarLord(Unit warlord) {
        if(isUnitReserve(warlord)) {
            disbandAllSquads();
            reserve.removeValue(warlord, true);
            regularTroops.add(new Array<Unit>());
            regularTroops.get(0).add(warlord);
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
                if ((0 < squadIndex && squadIndex < regularTroops.size)
                        || getNbOfSquads() <= getWarlord().getMaxWarChiefs()
                        || ldCondEnabled){
                    reserve.removeValue(unit, true);
                    if (0 < squadIndex && squadIndex < regularTroops.size) {
                        //reset an older squad
                        if (regularTroops.get(squadIndex).size > 0){
                            disengage(regularTroops.get(squadIndex).get(0));
                        }
                        regularTroops.get(squadIndex).add(unit);
                    } else {
                        //addExpGained a new squad
                        Array<Unit> newSquad = new Array<Unit>();
                        newSquad.add(unit);
                        regularTroops.add(newSquad);
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
     * @return return true if the unit is succesfully inserted
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
            the squadID and the unitID are those of a mere soldier, the request  is handled below.
            IF:  the squad ID is invalid =>  the request is canceled
             */
                if (0 <= squadID && squadID < getNbOfSquads()) {
                /*
                THEN two cases are to be considered :
                 - CASE 1 :the unit replace another one
                 - CASE 2 : the unit is simply added to the already mobilized troops
                 */

                    if (0 <= unitID && unitID < regularTroops.get(squadID).size) {
                    /*CASE 1 :
                    IF : the unitID belongs to a existing soldier
                     - the former member of the squad is demobilized
                     - the newbie is added
                     */
                        Unit formerUnit = this.regularTroops.get(squadID).removeIndex(unitID);
                        this.reserve.add(formerUnit);
                        this.regularTroops.get(squadID).insert(unitID, soldier);
                        reserve.removeValue(soldier, true);

                    } else  if( unitID < getWarchief(squadID).getMaxSoldiersAs(squadID == 0)){
                    /*CASE 2 :
                    IF : the squad has a slot available for a new recruit
                     - the newbie is added
                     */
                        this.regularTroops.get(squadID).add(soldier);
                        reserve.removeValue(soldier, true);
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

    @Override
    public void appointSkirmisher(Unit unit) {
        if(isUnitReserve(unit)){
            skirmishers.add(unit);
            reserve.removeValue(unit, true);
        }
    }


    /**
     * disengagement consist in removing a unit form the mobilized army
     * 1) FIRST we performEvent that the given unit is:
     *  - not null
     *  - mobilized
     *  - not a warlord
     *
     * 2) Then the method fetch the squad's unit and the unit indexes
     *
     * 3) then IF those indexes have been founded, 2 cases could rise:
     *      IF the unit is a war chief THEN the whole squad is demobilized
     *      ELSE the unit is demobilized
     *
     *
     * @param unit
     * @return
     */
    @Override
    public boolean disengage(Unit unit) {

        if(isUnitRegular(unit)) {



            int squadId = -1;
            for (int i = 0; i < regularTroops.size; i++) {
                for (int j = 0; j < regularTroops.get(i).size; j++) {
                    if (unit == regularTroops.get(i).get(j)) {
                        squadId = i;
                    }
                }
            }

            if (squadId != -1) {
                if(isWarlord(unit)) {
                    disbandAllSquads();
                } else if (isWarChief(unit)) {
                    Array<Unit> squad = getSquad(unit, false);
                    regularTroops.removeIndex(squadId);
                    reserve.addAll(squad);
                } else {
                    regularTroops.get(squadId).removeValue(unit, true);
                    reserve.add(unit);
                }
                return true;
            }


        }else if(isUnitSkirmisher(unit)){
            skirmishers.removeValue(unit, true);
            reserve.add(unit);
            return true;
        }
        return false;
    }

    /**
     * Re-integrate all mobilized troops in the on-mobilized array and reset the former.
     */
    @Override
    public void disbandAllSquads() {
        Unit unit;
        for(int i = 0; i <  regularTroops.size; i++){
            for(int j = 0; j <  regularTroops.get(i).size; j++){
                unit = regularTroops.get(i).removeIndex(j);
                reserve.add(unit);
                j--;
            }
        }
        reserve.addAll(skirmishers);
        skirmishers.clear();
        // to remove empty squad arraies
        regularTroops.clear();
    }

    @Override
    public void replenishMoral(boolean turnBeginning) {
        for(int i = 0; i < regularTroops.size; i++){
            for(int j = 0; j < regularTroops.get(i).size; j++){
                regularTroops.get(i).get(j).replenishMoral(turnBeginning);
            }
        }
        for(int i = 0; i < skirmishers.size ; i++){
            skirmishers.get(i).replenishMoral(turnBeginning);
        }
    }

    @Override
    public void updateActionPoints() {
        for(int i = 0; i < regularTroops.size; i++){
            for(int j = 0; j < regularTroops.get(i).size; j++){
                if(!regularTroops.get(i).get(j).isOutOfAction()) {
                    regularTroops.get(i).get(j).addActionPoints(Data.AP_REGEN);
                }
            }
        }
        for(int i = 0; i < skirmishers.size ; i++){
            skirmishers.get(i).addActionPoints(Data.AP_REGEN);
        }
    }

    @Override
    public String toLongString(){
        String str = "CURRENT ARMY";
        str += "\nLeader : "+getWarlord();
        str += "\nRegulars : "+(getMobilizedUnits(true).size - getAllSkirmisher(true).size);
        str += "\nSkirmishers : "+getAllSkirmisher(true).size;
        str += "\nReserve : "+ getReserve().size+"\n";
        str += "\n|REGULAR ARMY";
        for(int i = 0; i <  regularTroops.size; i++){
            for(int j = 0; j <  regularTroops.get(i).size; j++){
                if(j == 0){
                    if(i == 0){
                        str += "\n|> ";
                    }else{
                        str += "\n|--> ";
                    }
                }else{
                    str += "\n|----> ";
                }
                str += regularTroops.get(i).get(j).getName();
            }
        }
        str += "\n|\n|SKIRMISHER SQUAD";
        for(int i = 0; i <  skirmishers.size; i++){
            str += "\n|  "+ skirmishers.get(i).getName();
        }
        str += "\n|\n|RESERVE";
        for(int i = 0; i <  reserve.size; i++){
            str += "\n|  "+ reserve.get(i).getName();
        }
        return str+"\n";
    }

    @Override
    public String toString(){
        return "Army of "+((getWarlord() != null) ? getWarlord().getName() : "");
    }
}

