package com.lawsgame.emishitactics.core.phases.battle.widgets;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.lawsgame.emishitactics.core.constants.Data;
import com.lawsgame.emishitactics.core.helpers.TempoSprite2DPool;
import com.lawsgame.emishitactics.core.phases.battle.widgets.interfaces.Panel;

public class TileShortPanel extends Panel{
    Data.TileType tileType;

    public static BitmapFont mainFont = new BitmapFont();

    public TileShortPanel(){
        setX(0);
        setY(0);
        setWidth(30f);
        setHeight(30f);
        setVisible(true);
    }

    public void set(Data.TileType tileType){
        this.tileType = tileType;
    }

    @Override
    public void show(){
        setVisible(true);
    }

    @Override
    public void hide(){
        setVisible(false);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(TempoSprite2DPool.get().getBlackBGSprite(),getX(), getY(), getWidth(), getHeight() );
        TileShortPanel.mainFont.draw(batch, "font test", 50, 50);
    }

}
