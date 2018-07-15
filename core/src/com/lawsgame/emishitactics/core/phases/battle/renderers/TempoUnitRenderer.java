package com.lawsgame.emishitactics.core.phases.battle.renderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.helpers.TempoSprite2DPool;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.engine.patterns.command.Command;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;
import com.lawsgame.emishitactics.engine.timers.CountDown;

import java.util.LinkedList;

import static com.lawsgame.emishitactics.core.constants.Data.SPEED_PUSHED;
import static com.lawsgame.emishitactics.core.constants.Data.SPEED_WALK;

public class TempoUnitRenderer extends BattleUnitRenderer {
    protected float x;
    protected float y;
    protected boolean visible;

    private TextureRegion unitTexture;
    private TextureRegion weapontTexture;
    private TextureRegion mountedTexture;
    private TextureRegion orientationTexture;
    private TextureRegion offabbTexture = null;

    private boolean targeted;
    private boolean executing;

    private CountDown countDown = new CountDown(2f){
      @Override
      public void run(){
          super.run();
          executing = true;
      }

      @Override
      public void reset(){
          super.reset();
          executing = false;
      }
    };

    //walk (& switch position / pushed) attributes
    private float dl;
    private boolean updatePoint = false;
    private boolean pushed = false;
    private Array<int[]> remainingPath = new Array<int[]>(); // array of (r, c) <=> (y, x)

    private LinkedList<Object> animationQueue;


    public TempoUnitRenderer(int row, int col, Unit model) {
        super(model);
        this.x = col;
        this.y = row;
        this.executing = false;
        this.executing = false;
        this.visible = true;
        this.animationQueue = new LinkedList<Object>();

        display(Data.AnimationId.REST);
        getNotification(null);
    }


    //------------------ GAME ELEMENT METHODS -------------------------------------------

    @Override
    public void update(float dt) {

        countDown.update(dt);
        if (countDown.isFinished()) {
            offabbTexture = null;
            countDown.reset();
            if(animationQueue.isEmpty())
                display(Data.AnimationId.REST);

        }

        handleWalkAnimation(dt);
        launchNextAnimation();
    }

    public void launchNextAnimation(){
        if(executing == false){
            if(!animationQueue.isEmpty()) {
                Object query = animationQueue.pop();

                if (query instanceof Unit.DamageNotification) {
                    Unit.DamageNotification notification = (Unit.DamageNotification) query;
                    displayTakeHit(notification.moralOnly, notification.damageTaken, notification.critical);
                } else if (query instanceof int[]) {
                    int[] array = (int[]) query;
                    if (array.length == 2) {
                        int[] oldHpts = (int[]) query;
                        displayTreated(oldHpts);
                    } else if (array.length == 10) {
                        int[] gainLvl = (int[]) query;
                        displayLevelup(gainLvl);
                    }

                } else if (query instanceof Data.AnimationId) {
                    display((Data.AnimationId) query);

                } else if (query instanceof Data.Orientation) {
                    displayPushed((Data.Orientation) query);

                } else if (query instanceof Command){
                    Command customQuery = (Command)query;
                    customQuery.execute();

                } else {
                    launchNextAnimation();
                }
            }else if(model.isOutOfCombat()){
                //setVisible(false);
            }
        }
    }

    private void handleWalkAnimation(float dt){
        // handle walk animation
        if (remainingPath.size > 0) {
            dl = dt * ((pushed) ? SPEED_PUSHED: SPEED_WALK);
            if (x == remainingPath.get(0)[1]) {
                if ((y < remainingPath.get(0)[0] && y + dl >= remainingPath.get(0)[0])
                        || (y > remainingPath.get(0)[0] && y + dl <= remainingPath.get(0)[0])) {
                    y = remainingPath.get(0)[0];
                    updatePoint = true;
                } else if (y < remainingPath.get(0)[0]) {
                    y += dl;
                } else if (y > remainingPath.get(0)[0]) {
                    y -= dl;
                }
            }
            if (y == remainingPath.get(0)[0]) {
                if ((x < remainingPath.get(0)[1] && x + dl >= remainingPath.get(0)[1])
                        || (x > remainingPath.get(0)[1] && x + dl <= remainingPath.get(0)[1])) {
                    x = remainingPath.get(0)[1];
                    updatePoint = true;
                } else if (x < remainingPath.get(0)[1]) {
                    x += dl;
                } else if (x > remainingPath.get(0)[1])
                    x -= dl;
            }

            if (updatePoint) {
                updatePoint = false;
                remainingPath.removeIndex(0);

                if (remainingPath.size == 0) {
                    pushed = false;
                    executing = false;
                    display(Data.AnimationId.REST);
                } else {
                    model.setOrientation(Utils.getOrientationFromCoords(y, x, remainingPath.get(0)[0], remainingPath.get(0)[1]));

                }
            }
        }
    }


    @Override
    public void render(SpriteBatch batch) {
        if(visible) {
            batch.draw(unitTexture, x, y, 1, 1);
            batch.draw(weapontTexture, x, y, 0.25f, 0.25f);
            batch.draw(orientationTexture, x + 0.75f, y + 0.75f, 0.25f, 0.25f);
            if (mountedTexture != null) batch.draw(mountedTexture, x + 0.75f, y + 0f, 0.25f, 0.25f);
            if (offabbTexture != null) batch.draw(offabbTexture, x, y + 1, 1, 0.25f);
        }
    }



    // ---------------- TRIGGER ANIMATION METHODS ---------------------------------------





    @Override
    public void displayWalk(Array<int[]> path) {
        boolean validPath = true;
        int[] oldCoords = new int[]{(int)y, (int)x};

        /*
        CHECK PATH VALIDITY
        for the path to be explotable, it is required to collect tile coords of tiles which each of them are neighbours of its predecessor and follower in the list.
         */
        for(int[] coords: path) {
            if(coords.length < 2 || 1 != Utils.dist(oldCoords[0], oldCoords[1], coords[0], coords[1])) {
                validPath = false;
                continue;
            }
            oldCoords = coords;
        }
        if(validPath) {
            this.remainingPath.addAll(path);

                /*
            CLEAN PATH
            remove unnecesary entries for the walk position algorithm use in  UnitRenderer.update(dt)
             */
            if(path.size > 0) {
                int[] pPreviousEntry;
                int[] previousEntry;
                int[] entry;
                for (int i = 2; i < remainingPath.size; i++) {
                    entry = remainingPath.get(i);
                    previousEntry = remainingPath.get(i - 1);
                    pPreviousEntry = remainingPath.get(i - 2);

                    if ( (entry[0] == previousEntry[0] && previousEntry [0] == pPreviousEntry[0]) || (entry[1] == previousEntry[1] && previousEntry [1] == pPreviousEntry[1])){
                        remainingPath.removeIndex(i-1);
                        i--;
                    }

                }

                Data.Orientation or = Utils.getOrientationFromCoords(y,x,path.get(0)[0], path.get(0)[1]);
                model.setOrientation(or);
                unitTexture = TempoSprite2DPool.get().getUnitSprite(Data.AnimationId.WALK, model.getArmy().isAlly());
                executing = true;

            }
        }



    }

    @Override
    public void displayTakeHit(boolean moralOnly, int damageTaken, boolean critical) {
        unitTexture = TempoSprite2DPool.get().getUnitSprite(Data.AnimationId.TAKE_HIT, model.getArmy().isAlly());
        countDown.run();
    }


    @Override
    public void displayLevelup(int[] gainlvl) {
        unitTexture = TempoSprite2DPool.get().getUnitSprite(Data.AnimationId.LEVELUP, model.getArmy().isAlly());
        countDown.run();
    }

    @Override
    public void displayTreated(int[] oldHtpsAndMoral) {
        unitTexture = TempoSprite2DPool.get().getUnitSprite(Data.AnimationId.TREATED, model.getArmy().isAlly());
        countDown.run();
    }

    @Override
    public void displayPushed(Data.Orientation pushedTowards){
        model.setOrientation(pushedTowards);
        unitTexture = TempoSprite2DPool.get().getUnitSprite(Data.AnimationId.PUSHED, model.getArmy().isAlly());
        pushed = true;
        switch (pushedTowards){
            case WEST: remainingPath.add(new int[]{(int)y, (int)x - 1}); break;
            case NORTH: remainingPath.add(new int[]{(int)y + 1, (int)x}); break;
            case SOUTH: remainingPath.add(new int[]{(int)y - 1, (int)x}); break;
            case EAST: remainingPath.add(new int[]{(int)y, (int)x + 1}); break;
        }

    }

    @Override
    public void display(Data.AnimationId id) {
        switch (id){
            case WALK:
            case SWITCH_POSITION:
            case LEVELUP:
            case TREATED:
            case TAKE_HIT:
            case PUSHED:
                try{
                    throw new IllegalArgumentException("the following animation order "+id.name()+" required calling a specific method as it demands extra parameters to be conducted successfully");
                }catch (IllegalArgumentException e){
                    e.printStackTrace();
                }
                break;
            case ATTACK:
            case PUSH:
            case HEAL:
            case STEAL:
            case BUILD:
            case GUARD:
            case PRAY:
            case DIE:
            case DODGE:
            case BACKSTABBED:
            case COVER:
                unitTexture = TempoSprite2DPool.get().getUnitSprite(id, model.getArmy().isAlly());
                countDown.run();
            case REST:
                unitTexture = TempoSprite2DPool.get().getUnitSprite(id, model.getArmy().isAlly());
                break;
            case SWITCH_WEAPON:
                weapontTexture = TempoSprite2DPool.get().getWeaponSprite(model.getCurrentWeapon());
                break;
            case FLEE:
                unitTexture = TempoSprite2DPool.get().getUnitSprite(Data.AnimationId.WALK, model.getArmy().isAlly());
                orientationTexture = TempoSprite2DPool.get().getOrientationSprite(model.getOrientation().getOpposite());
                countDown.run();
                break;
            case GUARDED:
                //TODO:
                break;
            case FOCUSED_BLOW:
            case CRIPPLING_BLOW:
            case SWIRLING_BLOW:
            case SWIFT_BLOW:
            case HEAVY_BLOW:
            case CRUNCHING_BLOW:
            case WAR_CRY:
            case POISONOUS_ATTACK:
            case HARASS:
            case LINIENT_BLOW:
            case FURY:
                unitTexture = TempoSprite2DPool.get().getUnitSprite(Data.AnimationId.ATTACK, model.getArmy().isAlly());
                for(Data.Ability ability : Data.Ability.values()) {
                    if(ability.name().equals(id.name()))
                        offabbTexture = TempoSprite2DPool.get().getOffensiveAbbSprite(ability);
                }
                countDown.run();
                break;
                default:
        }
    }


    // ---------------- OTHERS METHODS ---------------------------------------------------



    @Override
    public boolean isExecuting() {
        return countDown.isRunning();
    }

    @Override
    public void setTargeted(boolean targeted) {
        this.targeted = targeted;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }


    /**
     * treat the input data and push animationQueries in the animaition Queue, ready to be rendered
     * @param data
     */
    @Override
    public void getNotification(Object data){
        if(data == null) {
            // update weapon
            weapontTexture = TempoSprite2DPool.get().getWeaponSprite(model.getCurrentWeapon());
            // update orientation
            orientationTexture = TempoSprite2DPool.get().getOrientationSprite(model.getOrientation());
            if (model.isHorseman()) {
                // update soldier mount rendering
                mountedTexture = TempoSprite2DPool.get().getMountedSprite();
            }
        }else{
            animationQueue.offer(data);
            if(data instanceof Unit.DamageNotification){
                if(model.isOutOfCombat()) {
                    if (model.isDead()) {
                        animationQueue.offer(Data.AnimationId.DIE);
                    } else if (model.isOutOfCombat()) {
                        animationQueue.offer(Data.AnimationId.FLEE);
                    }
                    animationQueue.offer(new SimpleCommand() {
                        @Override
                        public void execute() {
                            setVisible(true);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

}
