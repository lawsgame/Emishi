package com.lawsgame.emishitactics.core.phases.battle.commands.event;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Inventory;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Tile;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.interfaces.Model;
import com.lawsgame.emishitactics.core.phases.battle.commands.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.AnimationScheduler;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.CameraManager;
import com.lawsgame.emishitactics.engine.math.functions.VectorialFunction;

public class EarthquakeEvent extends BattleCommand {
    private float earthquakeDuration;
    /**
     *  allow to control which tile should be able to collapse in relation with the other targets
     *  allow to avoid, by instance, that all bridges collapse at once, rendering the mission impossible to complete.
     *  see {@link EarthquakeEvent.Node} for futher explanations.
     */
    private Node targetTileTree;

    public EarthquakeEvent(BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, float earthquakeDuration) {
        super(bfr, scheduler, playerInventory);
        this.earthquakeDuration = earthquakeDuration;
        this.targetTileTree = new Node();
    }


    public static EarthquakeEvent addTrigger(final BattlefieldRenderer bfr, AnimationScheduler scheduler, Inventory playerInventory, final int turn){
        EarthquakeEvent event = new EarthquakeEvent(bfr, scheduler, playerInventory, Data.EARTHQUAKE_DURATION);
        Model.Trigger trigger = new Model.Trigger(true, event) {
            @Override
            public boolean isTriggerable(Object data) {
                boolean triggered = data instanceof Notification.BeginArmyTurn
                        & bfr.getModel().getTurn() == turn
                        && ((Notification.BeginArmyTurn) data).army.isPlayerControlled();
                return triggered;
            }
        };
        bfr.getModel().add(trigger);
        return event;
    }

    public Node getTargetTileTree(){
        return targetTileTree;
    }

    @Override
    protected void execute() {
        StandardTask task = new StandardTask();
        // update tile and handle wounded units
        Unit victim;
        Notification.TakeDamage notif;
        Array<int[]> tilesTurnIntoRuins = getAllDestroyedTiles();
        int rowRuins;
        int colRuins;
        for(int i = 0; i < tilesTurnIntoRuins.size; i++){
            rowRuins = tilesTurnIntoRuins.get(i)[0];
            colRuins = tilesTurnIntoRuins.get(i)[1];
            bfr.getModel().setTile(rowRuins, colRuins, bfr.getModel().getTile(rowRuins,colRuins).getType().getDamagedType(), false);
            task.addParallelSubTask(new StandardTask.RendererSubTaskQueue(bfr, new Notification.SetTile(rowRuins, colRuins, bfr.getModel().getTile(rowRuins, colRuins))));

            if(bfr.getModel().isTileOccupied(rowRuins, colRuins)){
                victim = bfr.getModel().getUnit(rowRuins,colRuins);
                if(!victim.isOutOfAction()){
                    notif = victim.takeDamage(3, false, 0f);
                    notif.set(true, false, 0, false, false, victim.getOrientation().getOpposite());
                    victim.setCrippled(true);
                    notif.fleeingOrientation = victim.getOrientation().getOpposite();
                    task.addParallelSubTask(new StandardTask.RendererSubTaskQueue(bfr.getUnitRenderer(victim), notif));
                }
            }
        }
        // add unit prostrating animation
        Battlefield bf = bfr.getModel();
        for(int r = 0; r < bf.getNbRows(); r++){
            for(int c = 0; c < bf.getNbColumns(); c++){
                if(bf.isTileOccupied(r, c) && !bf.getUnit(r, c).isOutOfAction()){
                    task.addParallelSubTask(new StandardTask.RendererSubTaskQueue(bfr.getUnitRenderer(bf.getUnit(r, c)), Data.AnimId.WOUNDED));
                }
            }
        }
        // add camera shaking
        StandardTask.CommandSubTask commandThread = new StandardTask.CommandSubTask(0){
            @Override
            public void run() {
                bfr.getGCM().move(new ShakeVF(earthquakeDuration), earthquakeDuration);
            }
        };
        commandThread.setTag("shake camera");
        task.addParallelSubTask(commandThread);
        task.addParallelSubTask(new StandardTask.DelaySubTask(earthquakeDuration));
        // conclude
        scheduleRenderTask(task);
        Notification.OOAReport report = removeOutOfActionUnits();
        handleEvents(report, tilesTurnIntoRuins);
    }


    private Array<int[]>  getAllDestroyedTiles(){
        Array<int[]> destroyed = new Array<int[]>();
        // get fragile tiles
        Tile tile;
        for(int r = 0; r < bfr.getModel().getNbRows(); r++) {
            for (int c = 0; c < bfr.getModel().getNbColumns(); c++) {
                if(bfr.getModel().isTileExisted(r,c)){
                    tile = bfr.getModel().getTile(r, c);
                    if(tile.getType().isDestructible() && tile.isFragile() && MathUtils.random() <= Data.CHANCE_OF_COLLAPSING_FROM_FRAGILE_TILES){
                        destroyed.add(new int[]{r, c});
                    }
                }
            }
        }
        // get programmed tiles
        destroyed.addAll(targetTileTree.getProgrammedDestroyedTiles(bfr.getModel()));
        return destroyed;
    }


    @Override
    public boolean isApplicable() {
        return true;
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    protected void unexecute() { }



    // ----------------- VF HELPER CLASS -------------------

    public static class ShakeVF implements VectorialFunction{
        float xZero = 0;
        float yZero = 0;
        float duration;

        public ShakeVF(float duration){
            this.duration = duration;
        }

        @Override
        public float getX(float t) {
            return xZero + 0.15f*(1f - (float)Math.exp(t - duration))* MathUtils.sin(14*t)*MathUtils.cos(60*t);
        }

        @Override
        public float getY(float t) {
            return yZero;// + MathUtils.sin(t);
        }

        @Override
        public void setTZero(float xZero, float yZero) {
            this.xZero = xZero - getX(0);
            this.yZero = yZero - getY(0);
        }
    }



    // ------------------ PROGRAMMED TREE -----------------------

    public static class Node {
        protected int[] coords;
        protected float proba; // of collapsing
        protected Node parent;
        protected Array<Node>  children;

        public Node(int row, int col, float proba){
            this.coords = new int[]{row, col};
            this.proba = proba;
            this.parent = null;
            this.children = new Array<Node>();
        }

        public Node() {
            this(-1,-1,0);
        }

        public Node addChild(int row, int col, float proba){
            Node chilfNode = new Node(row, col, proba);
            chilfNode.parent = this;
            this.children.add(chilfNode);
            return chilfNode;
        }

        /**
         * if the tile stores at a node collapsed, then its children would not!
         *
         * @param bf : battlefield
         * @return all destroyed tile coords
         */
        public Array<int[]> getProgrammedDestroyedTiles(Battlefield bf){
            Array<int[]> destroyed = new Array<int[]>();
            if(bf.isTileExisted(coords[0], coords[1])
                    && bf.getTile(coords[0], coords[1]).getType().isDestructible()
                    && MathUtils.random() <= proba) {
                destroyed.add(coords);
            }else{
                for(int i = 0; i < children.size ; i++) {
                    destroyed.addAll(children.get(i).getProgrammedDestroyedTiles(bf));
                }
            }
            return destroyed;
        }
    }

}
