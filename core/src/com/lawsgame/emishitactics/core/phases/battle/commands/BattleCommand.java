package com.lawsgame.emishitactics.core.phases.battle.commands;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification.OOAReport;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.interfaces.Item;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler.Task;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;
import com.lawsgame.emishitactics.engine.patterns.observer.Observer;

import java.util.Stack;

public abstract class BattleCommand extends Observable implements Observer {
    protected final BattlefieldRenderer bfr;
    protected final AnimationScheduler scheduler;

    private boolean decoupled;
    private Array<Task> renderTasks;                // ids which allows to certify that the rendering of the command is bundlesSent / completed AND usefull for decoupling view and model updates
    private boolean tasksScheduled;

    protected Outcome outcome;


    public BattleCommand(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory){
        this.bfr = bfr;
        this.scheduler = scheduler;
        this.renderTasks = new Array<Task>();
        this.tasksScheduled = false;
        this.decoupled = false;
        this.outcome = new Outcome(playerInventory);
    }

    public boolean apply() {
        // Outcome and animation scheduler cleared.
        this.outcome.reset();

        if(isApplicable()) {
            execute();
            return true;
        }
        return false;
    }

    public boolean undo(){
        if(isUndoable()){
            unexecute();
            return true;
        }
        return false;
    }

    protected abstract void execute();
    protected abstract void unexecute();
    public abstract boolean isApplicable();
    public abstract boolean isUndoable();



    // ---------------- TASK SCHEDULING -------------------------

    public final boolean isExecuting(){
        return tasksScheduled && renderTasks.size > 0;
    }

    protected final void scheduleRenderTask(Task task){
        if(!task.isIrrelevant()) {
            renderTasks.add(task);
            if(!decoupled) {
                this.tasksScheduled = true;
                task.attach(this);
                scheduler.addTask(task);
            }
        }
    }

    protected final void scheduleMultipleRenderTasks(Array<Task> tasks){
        for(int i = 0; i < tasks.size; i++)
            scheduleRenderTask(tasks.get(i));
    }

    public final boolean pushRenderTasks(){

        if(decoupled && !tasksScheduled && renderTasks.size > 0) {

            this.tasksScheduled = true;
            for (int i = 0; i < renderTasks.size; i++) {
                renderTasks.get(i).attach(this);
                scheduler.addTask(renderTasks.get(i));
            }
            return true;
        }
        return false;
    }

    public final Array<Task> confiscateTasks(){
        Array<Task> tasks = renderTasks;
        renderTasks = new Array<Task>();
        return tasks;
    }

    @Override
    public void getNotification(Observable sender, Object data) {



        if (data instanceof Task) {

            Task completedTask = (Task) data;
            completedTask.detach(this);
            renderTasks.removeValue(completedTask, true);

            // notify that the command is done : the model is updated AND the render tasks are completed
            if (renderTasks.size == 0) {
                notifyAllObservers(this);
                tasksScheduled = false;
            }
        }
    }


    /**
     * 1) remove all OOA units model and view wise.
     * 2) setup report to keep track of the OOA units, usefull for event handling purposes.
     *
     * @return the report
     */
    protected final OOAReport removeOutOfActionUnits(){
        Array<Unit> OOAUnits = bfr.getModel().getOOAUnits();

        OOAReport report = new OOAReport();
        for(int i = 0; i < OOAUnits.size; i++){
            report.OOAUnits.add(OOAUnits.get(i));
            report.OOACoords.add(bfr.getModel().getUnitPos(OOAUnits.get(i)));
        }

        bfr.getModel().removeOOAUnits();

        StandardTask removeOOAUnitTask = new StandardTask();
        for(int i = 0; i < OOAUnits.size; i++)
            removeOOAUnitTask.addParallelSubTask(new StandardTask.RendererSubTaskQueue(bfr, OOAUnits.get(i)));
        //removeOOAUnitTask.tag("remove OOA units");
        scheduleRenderTask(removeOOAUnitTask);

        return report;
    }

    public boolean isDecoupled() {
        return decoupled;
    }

    public String showTask(){
        String str = "\nPUSHABLE RENDER TASKS\n";
        for(Task task: renderTasks){
            str += "\n"+task.toString();
        }
        return str;
    }


    // --------------- EVENT HANDLING ----------------------------------


    protected final boolean isAnyEventTriggerable(Object data, Array<int[]> area) {
        Battlefield bf = bfr.getModel();
        boolean eventTrig = false;

        if(bf.isAnyEventTriggerable(data)){
            return true;
        }

        for(int i = 0; i < bf.armyTurnOrder.size(); i++){
            if(bf.armyTurnOrder.get(i).isAnyEventTriggerable(data)) {
                return true;
            }
        }

        for(int i = 0; i< area.size; i++){
            eventTrig = isAnyEventTriggerable(data, area.get(i)[0], area.get(i)[1], true, true);
        }

        return eventTrig;
    }


    protected final boolean isAnyEventTriggerable(Object data, int row, int col){
        return isAnyEventTriggerable(data, row, col, false, false);
    }


    private boolean isAnyEventTriggerable(Object data, int row, int col, boolean ignoreBFEvents, boolean ignoreArmieEvents){
        Battlefield bf = bfr.getModel();

        if(bf.isTileExisted(row, col)) {
            // check unit
            if(bf.isTileOccupied(row, col)){
                if(bf.getUnit(row, col).isAnyEventTriggerable(data)) {
                    return true;
                }
            }

            // check tile
            if(bf.getTile(row, col).isAnyEventTriggerable(data)) {
                return true;
            }

            // check areas
            for (int j = 0; j < bf.getUnitAreas().size; j++) {
                if (Utils.arrayContains(bf.getUnitAreas().get(j).getTiles(), row, col)
                        && bf.getUnitAreas().get(j).isAnyEventTriggerable(data)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * handle triggered events within the given area in this order:
     *  1) battlefield related
     *  2) army related
     *  THEN
     *  3.1) unit related
     *  3.2) tile related
     *  3.3) area related
     *
     *
     *
     * @param data : notif to give to the triggers
     * @param area : tiles to check for event to trigger
     * @return if a event has been triggered
     */
    protected final void handleEvents(Object data, Array<int[]> area) {

        scheduleMultipleRenderTasks(bfr.getModel().performEvents(data, outcome));
        for(int i = 0; i < bfr.getModel().armyTurnOrder.size(); i++)
            scheduleMultipleRenderTasks(bfr.getModel().armyTurnOrder.get(i).performEvents(data, outcome));
        for(int i = 0; i< area.size; i++)
            handleEvents(data, area.get(i)[0], area.get(i)[1], true, true);
    }


    protected final void handleEvents(Object data, int row, int col){
        handleEvents(data, row, col, false, false);
    }


    private void handleEvents(Object data, int row, int col, boolean ignoreBFEvents, boolean ignoreArmieEvents){

        if(ignoreBFEvents)
            scheduleMultipleRenderTasks(bfr.getModel().performEvents(data, outcome));

        if(ignoreArmieEvents) {
            for (int i = 0; i < bfr.getModel().armyTurnOrder.size(); i++) {
                scheduleMultipleRenderTasks(bfr.getModel().armyTurnOrder.get(i).performEvents(data, outcome));
            }
        }

        if(bfr.getModel().isTileExisted(row, col)) {
            // check unit
            if(bfr.getModel().isTileOccupied(row, col))
                scheduleMultipleRenderTasks(bfr.getModel().getUnit(row, col).performEvents(data, outcome));

            // check tile
            scheduleMultipleRenderTasks(bfr.getModel().getTile(row, col).performEvents(data, outcome));


            // check areas
            for (int j = 0; j < bfr.getModel().getUnitAreas().size; j++) {
                if (Utils.arrayContains(bfr.getModel().getUnitAreas().get(j).getTiles(), row, col)) {
                    scheduleMultipleRenderTasks(bfr.getModel().getUnitAreas().get(j).performEvents(data, outcome));
                }
            }
        }
    }

    public void reactiveTriggers(){
        bfr.getModel().setAllTriggersActive(true);
    }

    public final void setDecoupled(boolean decoupled) {
        this.decoupled = decoupled;
    }

    public String toShortString(){
        return toString();
    }



    // ----------------- Encounter outcome CLASS -----------------


    public final Outcome getOutcome(){
        return outcome;
    }

    public BattlefieldRenderer getBFR(){
        return bfr;
    }

    public static class Outcome {
        public Array<ExperiencePointsHolder> expHolders;
        public Array<DroppedItemHolder> droppedItemHolders;
        public Inventory playerInventory;
        boolean resolved;
        boolean relevant;                                   // was not empty while being solved

        public Outcome(Inventory playerInventory) {
            this.playerInventory = playerInventory;
            this.expHolders = new Array<ExperiencePointsHolder>();
            this.droppedItemHolders = new Array<DroppedItemHolder>();
            this.resolved = false;
        }

        public void reset(){
            this.droppedItemHolders.clear();
            this.expHolders.clear();
            this.resolved = false;
            this.relevant = false;

        }

        public void add(Unit receiver, int experience){
            expHolders.add(new ExperiencePointsHolder(receiver, experience));
        }

        public void add(Item droppedItem, boolean playerOwned){
            droppedItemHolders.add(new DroppedItemHolder(droppedItem, playerOwned));
        }

        public void merge(Outcome outcome){
            if(outcome != null && !outcome.resolved) {
                expHolders.addAll(outcome.expHolders);
                droppedItemHolders.addAll(outcome.droppedItemHolders);
            }
        }

        public boolean isExpHandled(){
            return expHolders.size == 0;
        }

        public boolean isHandled(){
            return expHolders.size == 0 && droppedItemHolders.size == 0;
        }

        void clean(){
            // remove OOA units
            for (int i = 0; i < expHolders.size; i++) {
                if (expHolders.get(i).experience == 0 || expHolders.get(i).receiver.isOutOfAction()) {
                    expHolders.removeIndex(i);
                    i--;
                }
            }

            // merges identical receivers and sums the associated exp gains
            for (int i = 0; i < expHolders.size; i++) {
                for (int j = i + 1; j < expHolders.size; j++) {
                    if (expHolders.get(i).receiver == expHolders.get(j).receiver) {
                        if(expHolders.get(i).experience < expHolders.get(j).experience)
                            expHolders.get(i).experience = expHolders.get(j).experience;
                        expHolders.removeIndex(j);
                        j--;
                    }
                }
            }
        }


        /**
         * distribute exp points and add looted item a to the player inventory
         */
        void resolve(){
            if(!resolved) {
                resolved = true;
                for (int i = 0; i < expHolders.size; i++) {
                    expHolders.get(i).solve();
                }

                for (int i = 0; i < droppedItemHolders.size; i++) {
                    if (droppedItemHolders.get(i).playerOwned)
                        playerInventory.storeItem(droppedItemHolders.get(i).droppedItem);
                }
            }
        }

        @Override
        public String toString(){
            String str = "\nOUTCOME\n";
            for(int i = 0; i < expHolders.size; i++){
                str += "\nReceiver : "+ expHolders.get(i).receiver.getName()+" ("+((expHolders.get(i).receiver.isOutOfAction() ? "OOA": "FIGHTING")+") => experience gained : "+ expHolders.get(i).experience);
            }
            for(int i = 0; i < droppedItemHolders.size; i++){
                str += "\nStolen item : "+ droppedItemHolders.get(i).toString();
            }
            return str+"\n";
        }
    }

    public static class ExperiencePointsHolder {
        public final Unit receiver;
        public final int initialEXP;
        int experience;
        public int[] experiences;
        private Stack<int[]> statGained; // the 12 first entries are statistic inscreased while the last entry is experience gained before leveling up

        public ExperiencePointsHolder(Unit receiver, int experience){
            this.receiver = receiver;
            this.initialEXP = receiver.getExperience();
            this.experience = experience;
            this.statGained = new Stack<int[]>();
        }

        public boolean hasNext(){
            return statGained.size() > 0;
        }

        public int[] next(){
            return statGained.pop();
        }

        void solve(){
            statGained = receiver.addExpPoints(experience);
        }

    }


    public static class DroppedItemHolder {
        public Item droppedItem;
        public boolean playerOwned;

        public DroppedItemHolder(Item droppedItem, boolean playerOwned) {
            this.droppedItem = droppedItem;
            this.playerOwned = playerOwned;
        }
    }


}
