package com.lawsgame.emishitactics.engine;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lawsgame.emishitactics.engine.math.geometry.Vector;
import com.lawsgame.emishitactics.engine.patterns.observer.Observable;

import static com.lawsgame.emishitactics.engine.GamePhase.getAspRatio;

public class CameraManager extends Observable implements GameUpdatableEntity {
    private static final float CAM_STD_VELOCITY = 15;
    private static final float CAM_SLOPE_FACTOR = 0.1f; 	// increase it raise the transition period

    protected OrthographicCamera camera;     // level camera
    protected float worldWidth;               // defines the rectangle within the camera is allowed to move.
    protected float worldHeight;              // defines the rectangle within the camera is allowed to move.
    private Viewport viewport;              // defines the dimension of the frame through the player see the level
    private Rectangle clipBounds;           // specific

    // smooth camera variables
    private Vector vTarget;
    private Vector vFrom;
    private Vector gamma;                   // base velocity factor
    private  float alpha;                   // slope or transition intensity factor
    private  Vector dl;
    private boolean cameraMoving;
    private float intensity;
    private float cameraVelocity;

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

        this.vTarget = new Vector(0,0);
        this.vFrom = new Vector(0,0);
        this.gamma = new Vector(0,0);
        this.dl = new Vector(0,0);
        this.cameraMoving = false;
        this.intensity = 0;
        this.cameraVelocity = CAM_STD_VELOCITY;

    }

    /**
     * create a viewport that match the dimension of the world and the screen aspect ratio, of which both 's width equals 1.
     */
    public CameraManager(int portWidth){
        this(portWidth, portWidth*getAspRatio(), portWidth);
    }


    public void setCameraBoundaries(float w, float h) {
        if( w < clipBounds.width) w =  clipBounds.width;
        if( h < clipBounds.height) h = clipBounds.height;
        this.worldWidth = w;
        this.worldHeight = h;
    }

    public void focusOn(float xTargetTile, float yTargetTile, boolean smoothly) {
        if(xTargetTile != camera.position.x || yTargetTile != camera.position.y){
            if(smoothly){
                moveTo(xTargetTile, yTargetTile);
            }else{
                setCamPos(xTargetTile  , yTargetTile);
            }
        }
    }

    public boolean isCameraMoving(){
        return cameraMoving;
    }

    private void moveTo(float xTarget, float yTarget){
        if(!cameraMoving){

            //addExpGained xTarget & yTarget, addExpGained change it if too clase to the camera frame borders
            vTarget.x = xTarget;
            vTarget.y = yTarget;
            if(0 > xTarget - getPortWidth()/2)          vTarget.x = getPortWidth()/2f;
            if(xTarget + getPortWidth()/2 > worldWidth) vTarget.x = worldWidth - getPortWidth()/2f;
            if(0 > yTarget - getPortHeight()/2)          vTarget.y = getPortHeight()/2f;
            if(yTarget + getPortHeight()/2 > worldHeight) vTarget.y = worldHeight - getPortHeight()/2f;

            vFrom.x = camera.position.x;
            vFrom.y = camera.position.y;
            gamma.x = (vTarget.x - vFrom.x);
            gamma.y = (vTarget.y - vFrom.y);
            alpha = CAM_SLOPE_FACTOR*gamma.length();
            gamma.multiply(cameraVelocity);
            cameraMoving = true;
            intensity = 0;
        }
    }

    /**
     *
     * @param dt
     */
    @Override
    public void update(float dt) {
        if(cameraMoving){
            if( (vTarget.x == camera.position.x && vTarget.y == camera.position.y)){
                cameraMoving = false;
            }else{
                // calculate how much the camera shall move during each tick
                if(Math.abs(vTarget.x - camera.position.x) < Math.abs(vFrom.x - camera.position.x)){
                    intensity += dt;
                }else{
                    intensity -=dt;
                }
                float f = (float) ((1 - 2*Math.exp(intensity /(2*alpha))) + Math.exp(intensity /alpha));
                dl.x = gamma.x*f*dt;
                dl.y = gamma.y*f*dt;

                //fix the camera ultimate translation to perfectly fit the target position
                if(0 < dl.x && vTarget.x < dl.x + camera.position.x)    dl.x = vTarget.x - camera.position.x;
                if(dl.x < 0 && vTarget.x > dl.x + camera.position.x)    dl.x = vTarget.x - camera.position.x;
                if(0 < dl.y && vTarget.y < dl.y + camera.position.y)    dl.y = vTarget.y - camera.position.y;
                if(dl.y < 0 && vTarget.y > dl.y + camera.position.y)    dl.y = vTarget.y - camera.position.y;

                camera.translate(dl.x, dl.y);
                camera.update();
                clipBounds.x += dl.x;
                clipBounds.y += dl.y;
            }
        }
    }


    /**
     * 1) check whether or not the new camera position is inside the world boundaries
     * 2) modify the camera position consequently
     * 3) update the camera to take the changes into account
     * 4) update the clip bounds and the components instances to follow the camera accordingly
     *
     * @return  if the camera has been moved
     */
    public boolean translateCam(float dx, float dy){
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

    private boolean setCamPos(float x, float y){
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
        notifyAllObservers(null);
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

    public float getPortWidth(){ return  viewport.getWorldWidth(); }

    public float getPortHeight(){ return  viewport.getWorldHeight(); }

    public void setCameraVelocity(float cameraVelocity) {
        this.cameraVelocity = cameraVelocity;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Rectangle getClipBounds() {
        return clipBounds;
    }

}
