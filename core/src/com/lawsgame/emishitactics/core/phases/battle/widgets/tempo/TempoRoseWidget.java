package com.lawsgame.emishitactics.core.phases.battle.widgets.tempo;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.core.constants.Assets;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.interactions.EndTurnBIS.OrientationArrowWidget;
import com.lawsgame.emishitactics.core.phases.battle.interactions.EndTurnBIS.WindRoseWidget;

import static com.lawsgame.emishitactics.core.models.Data.Orientation.EAST;
import static com.lawsgame.emishitactics.core.models.Data.Orientation.NORTH;
import static com.lawsgame.emishitactics.core.models.Data.Orientation.SOUTH;
import static com.lawsgame.emishitactics.core.models.Data.Orientation.WEST;

public class TempoRoseWidget extends WindRoseWidget {
    protected float arrowSize;

    public TempoRoseWidget(Viewport gamePort, Stage uiStage, AssetManager asm) {
        super();
        this.arrowSize = uiStage.getViewport().getWorldWidth()/gamePort.getWorldWidth();
        setPosition(uiStage.getWidth()/2 - 1.5f * arrowSize, uiStage.getHeight()/2 - 1.5f * arrowSize);

        TextureAtlas uiAtlas = asm.get(Assets.ATLAS_UI);
        Skin skin = new Skin(uiAtlas);
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = skin.getDrawable(Assets.UI_ARROW);
        style.imageChecked = skin.getDrawable(Assets.UI_ARROW);
        style.imageDown = skin.getDrawable(Assets.UI_ARROW);

        OrientationArrowWidget arrowWidget;
        Data.Orientation or;
        for(int i = 0; i < Data.Orientation.values().length; i++) {
            or = Data.Orientation.values()[i];
            arrowWidget = new OrientationArrowWidget(style, or);
            arrowWidget.setTransform(true);
            arrowWidget.setSize(arrowSize, arrowSize);
            arrowWidget.setOrigin(Align.center);

            if(or == EAST){
                arrowWidget.setPosition(arrowSize * 2, arrowSize);
                arrowWidget.rotateBy(0);
            }
            if(or == NORTH){
                arrowWidget.setPosition(arrowSize , arrowSize*2);
                arrowWidget.rotateBy(90);
            }
            if(or == SOUTH){
                arrowWidget.setPosition(arrowSize , 0);
                arrowWidget.rotateBy(270);
            }
            if(or == WEST){
                arrowWidget.setPosition(0, arrowSize);
                arrowWidget.rotateBy(180);
            }

            addActor(arrowWidget);
            arrows.add(arrowWidget);
        }
    }

    @Override
    public void setLocation(int row, int col) {

    }
}
