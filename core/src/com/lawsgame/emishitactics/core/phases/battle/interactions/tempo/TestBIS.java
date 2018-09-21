package com.lawsgame.emishitactics.core.phases.battle.interactions.tempo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.constants.Utils;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Army;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.models.Weapon;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.AttackCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.BuildCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.ChooseOrientationCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.EndTurnCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.GuardCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.HealCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.MoveCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.PushCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.StealCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.SwitchPositionCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.SwitchWeaponCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.interactions.EndTurnBIS;
import com.lawsgame.emishitactics.core.phases.battle.interactions.HandleOutcomeBIS;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.AreaWidget;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ActionPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.ExperiencePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.LevelUpPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.LootPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoExperiencePanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoLevelUpPanel;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoLootPanel;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;
import com.lawsgame.emishitactics.engine.rendering.Animation;

import java.util.Stack;

public class TestBIS extends BattleInteractionState {
    ActionPanel actionPanel;
    AreaWidget areaWidget;
    ExperiencePanel experiencePanel;
    LevelUpPanel levelUpPanel;
    LootPanel lootPanel;

    private Animation animation;
    private Array<Sprite> sprites;
    private TextureRegion[] spriteSet;

    private Texture madeupTexture = null;

    IUnit sltdUnit;
    int index;
    boolean switchmode;

    Stack<BattleCommand> historic = new Stack<BattleCommand>();
    BattleCommand command = null;

    public TestBIS(BattleInteractionMachine bim) {
        super(bim, true, true, true);


        sltdUnit = bim.player.getArmy().getWarlord();
        index = 1;
        switchmode = false;

        //createTexture();

        //sprites = bim.spriteProvider.genSpriteTree.getSpriteSet(true, false, false,  Data.UnitTemplate.SOLAR_KNIGHT, Data.WeaponType.SWORD, Data.Orientation.WEST, false, false, Data.SpriteSetId.WALK_FLEE_SWITCHPOSITION);
        sprites = bim.spriteProvider.charaSpriteTree.getSpriteSet(false, Data.UnitTemplate.SOLAIRE, Data.WeaponType.SWORD, Data.Orientation.SOUTH, true, false, Data.SpriteSetId.PUSH);
        for(int i = 0; i < sprites.size; i++){
            sprites.get(i).setSize((sprites.get(i).getRegionHeight() == sprites.get(i).getRegionWidth() ? 2 : 1), 2);
            sprites.get(i).setPosition(2 + (sprites.get(i).getRegionHeight() == sprites.get(i).getRegionWidth() ? 0 : 0.5f), 2);

        }
        System.out.println(sprites.size);
        animation = new Animation(sprites.size, Data.ANIMATION_NORMAL_SPEED, true, false);
        animation.play();
    }

    public void createTexture(){
        Array<Sprite> as = bim.spriteProvider.genSpriteTree.getSpriteSet(false, false, false, Data.UnitTemplate.SOLAR_KNIGHT, Data.WeaponType.SWORD, Data.Orientation.WEST, false, false, Data.SpriteSetId.REST);
        madeupTexture = as.get(0).getTexture();
        madeupTexture.getTextureData().prepare();
        Pixmap pixmap = madeupTexture.getTextureData().consumePixmap();
        int colorInt;
        int average;
        int[] colorArray;
        for(int r = 0; r < madeupTexture.getHeight(); r++){
            for(int c = 0; c < madeupTexture.getWidth(); c++){
                colorInt = pixmap.getPixel(r, c);
                colorArray = Utils.getRGBA(colorInt);
                average = (colorArray[0] + colorArray[1] + colorArray[2])/3;
                colorInt = Utils.getColor32Bits(average, average, average, colorArray[3]);
                pixmap.drawPixel(r, c, colorInt);
            }
        }
        pixmap.getPixels().rewind();
        madeupTexture = new Texture(pixmap);
        pixmap.dispose();
    }


    @Override
    public void end() {

    }

    @Override
    public void renderAhead(SpriteBatch batch) {
        /*
        if(madeupTexture != null)
            batch.draw(madeupTexture, 0, 0, 19, 19 );
        if(sprites != null)
            sprites.get(animation.getCurrentFrame()).draw(batch);
            */
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        // command test

        System.out.println("input : "+row+" "+col);
        //bim.moveCamera(row, col, true);
        //bim.bfr.getUnitRenderer(sltdUnit).setPos(row, col);

        // TEST FINAL


        if(switchmode && bim.battlefield.isTileOccupiedByAlly(row, col, Data.Affiliation.ALLY)) {
            sltdUnit = bim.battlefield.getUnit(row, col);

        }else{
            if(bim.battlefield.isTileOccupied(row, col)){

                switch (index){

                    case 1 : command = new AttackCommand(bim.bfr, bim.scheduler);break;
                    case 2 : command = new HealCommand(bim.bfr, bim.scheduler);break;
                    case 3 : command = new PushCommand(bim.bfr, bim.scheduler); break;
                    case 4 : command = new SwitchPositionCommand(bim.bfr, bim.scheduler);break;
                    case 5 : command = new GuardCommand(bim.bfr, bim.scheduler); break;
                    case 6 : command = new StealCommand(bim.bfr, bim.scheduler);break;
                    case 7 : command =  new SwitchWeaponCommand(bim.bfr, bim.scheduler, 1); break;
                    case 8 : command = new ChooseOrientationCommand(bim.bfr, bim.scheduler, Data.Orientation.NORTH);break;
                    case 9 : command = new EndTurnCommand(bim.bfr, bim.scheduler);break;
                    default: command = new AttackCommand(bim.bfr, bim.scheduler);
                }
            } else {
                command = (index != 9) ? new MoveCommand(bim.bfr, bim.scheduler) : new BuildCommand(bim.bfr, bim.scheduler, Data.TileType.BRIDGE);

            }

            int[] unitPos = bim.battlefield.getUnitPos( sltdUnit);
            if (command.setActor(unitPos[0], unitPos[1])) {
                //command.setDecoupled(true);

                command.setTarget(row, col);
                if (command.isTargetValid()) {

                    command.blink(true);
                    if(bim.app.isPanelAvailable(command)){


                        final ActionPanel actionPanel = bim.app.getPanel(command);

                        bim.scheduler.addTask(new StandardTask(new SimpleCommand() {
                            @Override
                            public void apply() {
                                bim.uiStage.addActor(actionPanel);
                                actionPanel.hide();
                                actionPanel.show();
                            }
                        }, 0f));
                        bim.scheduler.wait(3.5f);
                        bim.scheduler.addTask(new StandardTask(new SimpleCommand() {
                            @Override
                            public void apply() {
                                actionPanel.hide();
                            }
                        }, 0f));
                        bim.scheduler.wait(0.2f);
                        bim.scheduler.addTask(new StandardTask(new SimpleCommand() {
                            @Override
                            public void apply() {
                                actionPanel.remove();
                            }
                        }, 0f));

                    }



                    bim.scheduler.addTask(new StandardTask(new SimpleCommand() {
                        @Override
                        public void apply() {
                            command.apply();
                        }
                    }, 0f));

                    historic.push(command);
                }
            }
        }


        return true;
    }

    @Override
    public void update60(float dt) {
        if(animation != null)
            animation.update(dt);

        if(Gdx.input.isKeyJustPressed(Input.Keys.U) && !historic.isEmpty()){
            BattleCommand command = historic.peek();
            if(command.getActionChoice().isUndoable()){
                command.undo();
                historic.pop();
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)){
            switchmode = !switchmode;
            System.out.println("switch between units : "+switchmode);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)){
            System.out.println("action command : attack");
            index = 1;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)){
            System.out.println("action command : heal");
            index = 2;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)){
            System.out.println("action command : push");
            index = 3;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)){
            System.out.println("action command : switch");
            index = 4;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)){
            System.out.println("action command : guard");
            index = 5;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)){
            System.out.println("action command : steal");
            index = 6;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)){
            System.out.println("action command : switch weapon");
            index = 7;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)){
            System.out.println("action command : choose orientation => NORTH");
            index = 8;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_9)){
            System.out.println("action command : end turn / build");
            index = 9;
        }


        if(Gdx.input.isKeyJustPressed(Input.Keys.A)) bim.bfr.getUnitRenderer(sltdUnit).display(Data.AnimId.SPECIAL_MOVE);
        if(Gdx.input.isKeyJustPressed(Input.Keys.Z)) bim.bfr.getUnitRenderer(sltdUnit).display(Data.AnimId.ATTACK);
        if(Gdx.input.isKeyJustPressed(Input.Keys.E)) bim.bfr.getUnitRenderer(sltdUnit).display(Data.AnimId.HEAL);
        if(Gdx.input.isKeyJustPressed(Input.Keys.R)) bim.bfr.getUnitRenderer(sltdUnit).display(Data.AnimId.BUILD);
        if(Gdx.input.isKeyJustPressed(Input.Keys.T)) bim.bfr.getUnitRenderer(sltdUnit).display(Data.AnimId.WALK);
        if(Gdx.input.isKeyJustPressed(Input.Keys.Y)) bim.bfr.getUnitRenderer(sltdUnit).display(Data.AnimId.DODGE);
        if(Gdx.input.isKeyJustPressed(Input.Keys.C)) bim.bfr.getUnitRenderer(sltdUnit).setOrientation(Data.Orientation.WEST);
        if(Gdx.input.isKeyJustPressed(Input.Keys.V)) bim.bfr.getUnitRenderer(sltdUnit).setOrientation(Data.Orientation.NORTH);
        if(Gdx.input.isKeyJustPressed(Input.Keys.N)) bim.bfr.getUnitRenderer(sltdUnit).setOrientation(Data.Orientation.SOUTH);
        if(Gdx.input.isKeyJustPressed(Input.Keys.B)) bim.bfr.getUnitRenderer(sltdUnit).setOrientation(Data.Orientation.EAST);
        if(Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            this.targeted = ! targeted;
            bim.bfr.getUnitRenderer(sltdUnit).setTargeted(targeted);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            this.done = ! done;
            bim.bfr.getUnitRenderer(sltdUnit.getArmy().getAllSquads().get(0).get(0)).setDone(done);
        }

    }
    private boolean targeted = false;
    private boolean done = false;

    /*
    @Override
    public void renderBetween(SpriteBatch batch) {
        areaWidget.render(batch);
    }
    */

    @Override
    public void init() {

    }
}
