package com.lawsgame.emishitactics.core.phases.battle.interactions;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.BattleInteractionMachine;
import com.lawsgame.emishitactics.core.phases.battle.commands.ChooseOrientationCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.EndTurnCommand;
import com.lawsgame.emishitactics.core.phases.battle.commands.interfaces.BattleCommand;
import com.lawsgame.emishitactics.core.phases.battle.interactions.interfaces.BattleInteractionState;
import com.lawsgame.emishitactics.core.phases.battle.widgets.tempo.TempoRoseWidget;

public class EndTurnBIS extends BattleInteractionState {
    private int colSltdUnit;
    private int rowSltdUnit;
    private WindRoseWidget windRoseWidget;

    public EndTurnBIS(BattleInteractionMachine bim, int rowSltdUnit, int colSltdUnit) {
        super(bim, true, false, false);
        this.rowSltdUnit = rowSltdUnit;
        this.colSltdUnit = colSltdUnit;
        this.windRoseWidget = new TempoRoseWidget(bim.gcm.getPort(), bim.uiStage, bim.asm);
    }

    @Override
    public void init() {
        bim.uiStage.addActor(windRoseWidget);
        bim.focusOn(rowSltdUnit, colSltdUnit, true, false, false,true, false);
    }

    @Override
    public void end() {
        super.end();
        windRoseWidget.remove();
    }

    @Override
    public boolean handleTouchInput(int row, int col) {
        return true;
    }

    @Override
    public void update60(float dt) {

    }

    @Override
    public void prerender(SpriteBatch batch) {

    }

    @Override
    public void renderBetween(SpriteBatch batch) {

    }

    @Override
    public void renderAhead(SpriteBatch batch) {

    }

    public static abstract class WindRoseWidget extends Group {
        protected Array<OrientationArrowWidget> arrows;

        public WindRoseWidget(){
            arrows = new Array<OrientationArrowWidget>();
        }

        public void setListeners(BattleInteractionMachine bim, final int rowActor, final int colActor){
            for(int i = 0; i < arrows.size; i++){
                arrows.get(i).setListener(bim, rowActor, colActor);
            }
        }

        public abstract void setLocation(int row, int  col);
    }



    /**
     * arrow of the wind rose compass
     */
    public static class OrientationArrowWidget extends ImageButton {
        Data.Orientation orientation;

        public OrientationArrowWidget(ImageButtonStyle style, Data.Orientation or) {
            super(style);
            this.orientation = or;
        }

        private void setListener(final BattleInteractionMachine bim, final int rowActor, final int colActor){
            addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    BattleCommand command = new ChooseOrientationCommand(bim.bfr, bim.scheduler, orientation);
                    command.apply(rowActor, colActor);
                    command = new EndTurnCommand(bim.bfr, bim.scheduler);
                    command.apply(rowActor, colActor);
                    bim.replace(new SelectActorBIS(bim, rowActor, colActor));

                }
            });

        }
    }
}
