package com.lawsgame.emishitactics.engine.inputs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.lawsgame.emishitactics.engine.GameUpdatableEntity;
import com.lawsgame.emishitactics.engine.patterns.statemachine.State;

public abstract class InteractionState implements GameUpdatableEntity, State, GestureDetector.GestureListener{
    OrthographicCamera gameCam;
    private Vector3 vector3 = new Vector3();
    private Vector3 oldVector3 = new Vector3();

    public InteractionState(OrthographicCamera gameCam) {
        this.gameCam = gameCam;
    }

    public abstract void onLongTouch(float gameX, float gameY);
    public abstract void onTouch(float gameX, float gameY);
    public abstract void pan(float gameDX, float gameDY);


    // ------------------- GESTURELISTENER IMPLEMENTATIONS -------------------------

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {

        vector3.x = x;
        vector3.y = y;
        vector3.z = 0;
        vector3 = gameCam.unproject(vector3);
        onTouch(vector3.x, vector3.y);

        return true;
    }

    @Override
    public boolean longPress(float x, float y) {
        vector3.x = x;
        vector3.y = y;
        vector3.z = 0;
        vector3 = gameCam.unproject(vector3);
        onLongTouch(vector3.x, vector3.y);
        return true;
    }


    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        vector3.x = x;
        vector3.y = y;
        vector3.z = 0;
        vector3 = gameCam.unproject(vector3);

        oldVector3.x = x - deltaX;
        oldVector3.y = y - deltaY;
        oldVector3.z = 0;
        oldVector3 = gameCam.unproject(oldVector3);


        pan(-vector3.x + oldVector3.x, - vector3.y + oldVector3.y);

        //System.out.println(xCenter + " "+ yCenter+" <= +" + deltaX + " & +"+deltaY);
        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) { return false;    }

    @Override
    public void pinchStop() {    }
}

