package com.lawsgame.emishitactics.engine;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.engine.patterns.Observable;

import static com.lawsgame.emishitactics.engine.GamePhase.getAspRatio;

public class CameraManager extends Observable {
    protected OrthographicCamera camera;     // level camera
    protected float worldWidth;               // defines the rectangle within the camera is allowed to move.
    protected float worldHeight;              // defines the rectangle within the camera is allowed to move.
    private Viewport viewport;              // defines the dimension of the frame through the player see the level

    private Rectangle clipBounds;           // specific

    public CameraManager(float worldWidth, float worldHeight, float portWidth){
        this.worldHeight = worldHeight;
        this.worldWidth = worldWidth;

        float gamePortHeight = portWidth*getAspRatio();
        this.clipBounds = new Rectangle(0,0,portWidth, gamePortHeight);
        this.setCameraBoundaries(worldWidth, worldHeight);


        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(portWidth, gamePortHeight, camera);
        this.camera.setToOrtho(false, portWidth, gamePortHeight);
        this.camera.update();

    }

    public CameraManager(float portWidth){
        this(1, 1, portWidth);
    }

    public void setCameraBoundaries(float w, float h) {
        if( w < clipBounds.width) w =  clipBounds.width;
        if( h < clipBounds.height) h = clipBounds.height;
        this.worldWidth = w;
        this.worldHeight = h;
    }


    /**
     * 1) check whether or not the new camera position is inside the world boundaries
     * 2) modify the camera position consequently
     * 3) update the camera to take the changes into account
     * 4) update the clip bounds and the components instances to follow the camera accordingly
     *
     * @return  whether or not the camera has moved
     */
    public boolean translateGameCam(float dx, float dy){
        float oldCamPosX = camera.position.x;
        float oldCamPosY = camera.position.y;

        if(camera.position.x - clipBounds.width/2 + dx < 0){
            dx = 0;
            camera.position.x = clipBounds.width/2;
        }
        if(camera.position.y - clipBounds.height/2 + dy < 0){
            dy = 0;
            camera.position.y = clipBounds.height/2;
        }
        if(camera.position.x + clipBounds.width/2 + dx > worldWidth){
            dx = 0;
            camera.position.x =  worldWidth - clipBounds.width/2;
        }
        if(camera.position.y + clipBounds.height/2 + dy > worldHeight){
            dy = 0;
            camera.position.y =  worldHeight - clipBounds.height/2;
        }

        camera.translate(dx, dy);
        camera.update();
        clipBounds.x += camera.position.x - oldCamPosX;
        clipBounds.y += camera.position.y - oldCamPosY;

        notifyAllObservers(null);
        return !((dx == 0f) && (dy == 0f));

    }

    public boolean setGameCamPos(float x, float y){
        boolean changed = false;
        if(x < clipBounds.width/2f) x = clipBounds.width/2;
        if(x > worldWidth - clipBounds.width/2f) x = worldWidth - clipBounds.width/2;
        if(y < clipBounds.height/2f) y = clipBounds.height/2f;
        if(y > worldHeight - clipBounds.height/2f) y = worldHeight - clipBounds.height/2f;

        if(camera.position.x != x || camera.position.y != y) {
            changed = true;
            camera.position.x = x;
            camera.position.y = y;
            clipBounds.x = x - clipBounds.width / 2f;
            clipBounds.y = y - clipBounds.height / 2f;
            camera.update();
        }
        return changed;
    }

    // -------------------- GETTERS & SETTERS

    public float getWorldWidth() {
        return worldWidth;
    }

    public float getWorldHeight() {
        return worldHeight;
    }

    public Viewport getPort(){ return viewport; }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Rectangle getClipBounds() {
        return clipBounds;
    }
}
