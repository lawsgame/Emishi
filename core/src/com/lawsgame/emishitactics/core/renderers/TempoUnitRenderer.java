package com.lawsgame.emishitactics.core.renderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.lawsgame.emishitactics.core.constants.Props;
import com.lawsgame.emishitactics.core.managers.TempoSprite2DPool;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.renderers.interfaces.UnitRenderer;

public class TempoUnitRenderer extends UnitRenderer {
    protected float x;
    protected float y;
    protected Props.ActionState actionState;

    private TextureRegion unitTexture;
    private TextureRegion weapontexture;
    private TextureRegion shieldTexture;
    private TextureRegion mountedTexture;
    private TextureRegion orientationTexture;


    public TempoUnitRenderer(int row, int col, Unit model) {
        super(model);
        this.setX(col);
        this.setY(row);
        this.setActionState(Props.ActionState.REST);
        this._set();
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(unitTexture, x, y, 1, 1);
        batch.draw(weapontexture, x, y, 0.25f, 0.25f);
        batch.draw(orientationTexture, x + 0.75f, y + 0.75f, 0.25f, 0.25f);
        if(shieldTexture != null) batch.draw(shieldTexture, x + 0.75f, y, 0.25f, 0.25f);
        if(mountedTexture != null) batch.draw(mountedTexture, x + 0.75f, y + 0.25f, 0.25f, 0.25f);
    }

    @Override
    public void getNotification(Object data) {
        _set();
    }


    @Override
    public void setActionState(Props.ActionState actionState) {
        if(actionState  == Props.ActionState.REST) {
            unitTexture = TempoSprite2DPool.get().getUnitSprite(model.getStance(), model.getArmy().isAlly(), model.isDone());
        }else{
            unitTexture = TempoSprite2DPool.get().getUnitSprite(actionState, model.getArmy().isAlly(), model.isDone());
        }
    }

    @Override
    public void setX(float x) {
        this.x =x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    private void _set(){
        weapontexture = TempoSprite2DPool.get().getWeaponSprite(model.getCurrentWeapon());
        orientationTexture = TempoSprite2DPool.get().getOrientationSprite(model.getCurrentOrientation());
        if(model.isUsingShield()){
            shieldTexture = TempoSprite2DPool.get().getShieldSprite();
        }
        if(model.isHorseman()){
            mountedTexture = TempoSprite2DPool.get().getMountedSprite();
        }
    }
}
