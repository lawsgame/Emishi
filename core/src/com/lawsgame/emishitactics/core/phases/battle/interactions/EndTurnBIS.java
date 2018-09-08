package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.interfaces.IArmy;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.ChooseOrientationCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.EndTurnCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.helpers.tasks.StandardTask;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.engine.GameRenderableEntity;

import java.util.HashMap;

public class EndTurnBIS extends BattleInteractionState {
    private int colSltdUnit;
    private int rowSltdUnit;
    private WindRoseWidget windRoseWidget;

    public EndTurnBIS(BattleInteractionMachine bim, int rowSltdUnit, int colSltdUnit) {
        super(bim, true, false, true);
        this.rowSltdUnit = rowSltdUnit;
        this.colSltdUnit = colSltdUnit;
        this.windRoseWidget = new WindRoseWidget(bim.asm);
    }

    @Override
    public void init() {
        System.out.println("END TURN : "+rowSltdUnit+" "+colSltdUnit+" => "+bim.battlefield.getUnit(rowSltdUnit, colSltdUnit).getName());

        windRoseWidget.setLocation(rowSltdUnit, colSltdUnit);
        bim.focusOn(rowSltdUnit, colSltdUnit, true, false, false,true, false);
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        return true;
    }

    @Override
    public boolean handleRawTouchInput(float x, float y) {
        boolean handled = windRoseWidget.handleTouch(x, y, rowSltdUnit, colSltdUnit, bim);
        if(handled){

            if(bim.battlefield.isTileOccupied(rowSltdUnit, colSltdUnit) && bim.battlefield.getUnit(rowSltdUnit, colSltdUnit).isMobilized()){

                IUnit sltdUnit =  bim.battlefield.getUnit(rowSltdUnit, colSltdUnit);
                IArmy currentArmy = sltdUnit.getArmy();
                if(currentArmy.isDone()){
                    bim.end(currentArmy);
                    bim.scheduler.addTask(new StandardTask(bim.bfr.getUnitRenderer(sltdUnit), Notification.Done.get(false)));
                    bim.replace(new AiBIS(bim));
                }else{

                    bim.replace(new SelectActorBIS(bim, rowSltdUnit, colSltdUnit, false));
                }
            }
        }
        return handled;
    }

    @Override
    public void renderAhead(SpriteBatch batch) {
        windRoseWidget.render(batch);
    }

    public static class WindRoseWidget implements GameRenderableEntity {
        protected HashMap<Data.Orientation, OrientationArrowWidget> arrows;

        public WindRoseWidget(AssetManager asm){
            arrows = new HashMap<Data.Orientation, OrientationArrowWidget>();
            arrows.put(Data.Orientation.NORTH, new OrientationArrowWidget(asm, Data.Orientation.NORTH));
            arrows.put(Data.Orientation.EAST, new OrientationArrowWidget(asm, Data.Orientation.EAST));
            arrows.put(Data.Orientation.WEST, new OrientationArrowWidget(asm, Data.Orientation.WEST));
            arrows.put(Data.Orientation.SOUTH, new OrientationArrowWidget(asm, Data.Orientation.SOUTH));

        }

        public boolean handleTouch(float gameX, float gameY, int rowActor, int colActor, final BattleInteractionMachine bim){
            boolean handled = false;
            int rowTouch = bim.bfr.getRowFrom(gameX, gameY);
            int colTouch = bim.bfr.getColFrom(gameX, gameY);

            if(rowTouch  == rowActor){
                if(colTouch  == colActor + 1){
                    arrows.get(Data.Orientation.EAST).handleTouch(rowActor, colActor, bim);
                    handled = true;
                }else if(colTouch == colActor - 1){
                    arrows.get(Data.Orientation.WEST).handleTouch(rowActor, colActor, bim);
                    handled = true;
                }
            }else if(colTouch == colActor){
                if(rowTouch  == rowActor + 1){
                    arrows.get(Data.Orientation.NORTH).handleTouch(rowActor, colActor, bim);
                    handled = true;
                }else if(rowTouch == rowActor - 1){
                    arrows.get(Data.Orientation.SOUTH).handleTouch(rowActor, colActor, bim);
                    handled = true;
                }
            }
            return handled;

        }

        public void setLocation(int row, int  col){
            arrows.get(Data.Orientation.NORTH).arrowSprite.setPosition(col, row +1);
            arrows.get(Data.Orientation.EAST).arrowSprite.setPosition(col+1, row);
            arrows.get(Data.Orientation.WEST).arrowSprite.setPosition(col-1, row);
            arrows.get(Data.Orientation.SOUTH).arrowSprite.setPosition( col, row -1);
        }

        @Override
        public void render(SpriteBatch batch) {
            arrows.get(Data.Orientation.NORTH).render(batch);
            arrows.get(Data.Orientation.EAST).render(batch);
            arrows.get(Data.Orientation.WEST).render(batch);
            arrows.get(Data.Orientation.SOUTH).render(batch);

        }
    }



    /**
     * arrow of the wind rose compass
     */
    public static class OrientationArrowWidget implements GameRenderableEntity{
        protected Data.Orientation orientation;
        protected Sprite arrowSprite;

        public OrientationArrowWidget(AssetManager asm, Data.Orientation or) {
            this.orientation = or;
            TextureAtlas atlas = asm.get(Assets.ATLAS_TEMPO_UI);
            arrowSprite = new Sprite(atlas.findRegion(Assets.UI_ARROW));
            arrowSprite.setSize(1, 1);
            arrowSprite.setPosition(0, 0);
            arrowSprite.setOriginCenter();

            switch (or){
                case WEST: arrowSprite.rotate(180); break;
                case NORTH: arrowSprite.rotate(90); break;
                case SOUTH: arrowSprite.rotate(270); break;
            }

        }

        private void handleTouch(int rowActor, int colActor, final BattleInteractionMachine bim){
            BattleCommand command = new ChooseOrientationCommand(bim.bfr, bim.scheduler, orientation);
            command.apply(rowActor, colActor);
            command = new EndTurnCommand(bim.bfr, bim.scheduler);
            command.apply(rowActor, colActor);
            //bim.replace(new SelectActorBIS(bim, rowActor, colActor, false));
        }

        @Override
        public void render(SpriteBatch batch) {
            arrowSprite.draw(batch);
        }
    }
}
