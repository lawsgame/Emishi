package com.lawsgame.emishitactics.core.phases.battle.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.TacticsGame;
import com.lawsgame.emishitactics.core.helpers.SpriteProvider;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.interfaces.IUnit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.AreaRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.CameraManager;

public class IsoBFR extends BattlefieldRenderer {
    public static final float RATIO = 0.5f; // <1 : ratio between the the great and little dialogue
    public static final float SPRITE_STD_SIZE = 1.25f;
    private static final float CAM_VELOCITY = 45.0f;
    private static float X_CAM_BOUNDS_OFFSET = 1f;
    private static float Y_CAM_BOUNDS_OFFSET = RATIO;
    private CameraManager gcm;

    private boolean visible;
    protected SpriteProvider spriteProvider;
    private Array<Array<Sprite>> lowerTileSprites;
    private Array<Array<BattleUnitRenderer>> unitRenderers;

    private ShapeRenderer backkgroundRenderer;


    public IsoBFR(Battlefield battlefield, SpriteProvider spriteProvider){
        this(battlefield, spriteProvider,false);
    }

    public IsoBFR(Battlefield battlefield, SpriteProvider spriteProvider, boolean test) {
        super(battlefield);
        int depth = 2*(battlefield.getNbRows() + battlefield.getNbColumns()) - 3;
        this.lowerTileSprites = new Array<Array<Sprite>>();
        this.unitRenderers = new Array<Array<BattleUnitRenderer>>();
        for(int i = 0; i < depth; i++) {
            lowerTileSprites.add(new Array<Sprite>());
            unitRenderers.add(new Array<BattleUnitRenderer>());
        }

        this.visible = true;
        this.spriteProvider = spriteProvider;

        // required for the junit testes
        if (test) {

            X_CAM_BOUNDS_OFFSET = 0;
            Y_CAM_BOUNDS_OFFSET = 0;
        } else {

            this.backkgroundRenderer = new ShapeRenderer();

            // pre calculate buildingType coords and texture region to render to prevent extra calculus each game loop.
            for (int r = 0; r < battlefield.getNbRows(); r++) {
                for (int c = 0; c < battlefield.getNbColumns(); c++) {
                    addTileRenderer(r, c, getModel().getTile(r, c));
                    if (battlefield.isTileOccupied(r, c)) {
                        addUnitRenderer(r, c, getModel().getUnit(r, c));
                    }
                }
            }

            // build up area renderers
            for (int i = 0; i < getModel().getUnitAttachedAreas().size ; i++) {
                addAreaRenderer(getModel().getUnitAttachedAreas().get(i));
            }
            for (int i = 0; i < battlefield.getDeploymentAreas().size; i++) {
                addAreaRenderer(battlefield.getDeploymentAreas().get(i));
            }

            //build weather renderer
            this.renderedWeather = getModel().getWeather();
        }
    }


    @Override
    public void setGameCamParameters(CameraManager cameraManager) {
        float width = 2*X_CAM_BOUNDS_OFFSET + (getModel().getNbRows() + getModel().getNbColumns()) / 2.0f;
        float height = 2*Y_CAM_BOUNDS_OFFSET + (getModel().getNbRows() + getModel().getNbColumns()) * RATIO / 2.0f;
        cameraManager.setCameraBoundaries(width, height);
        cameraManager.setCameraVelocity(CAM_VELOCITY);
        this.gcm = cameraManager;
    }

    @Override
    public void prerender() {
        backkgroundRenderer.begin(ShapeRenderer.ShapeType.Filled);
        backkgroundRenderer.rect(0,0,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight(),
                renderedWeather.getLowerColor(),
                renderedWeather.getLowerColor(),
                renderedWeather.getUpperColor(),
                renderedWeather.getUpperColor());
        backkgroundRenderer.end();
    }




    @Override
    public void render(SpriteBatch batch) {
        if(visible) {
            for(int i = lowerTileSprites.size - 1; i >= 0 ; i--){
                for(int j = 0; j < lowerTileSprites.get(i).size; j++){
                    if(isSpriteWithinFrame(lowerTileSprites.get(i).get(j))) {
                        lowerTileSprites.get(i).get(j).draw(batch);
                    }
                }

            }
            for(int i = unitRenderers.size - 1; i >= 0 ; i--){
                for(int j = 0; j < unitRenderers.get(i).size; j++){
                    unitRenderers.get(i).get(j).render(batch);
                }

            }
        }
    }

    protected boolean isSpriteWithinFrame(Sprite sprite) {
        return gcm.getClipBounds().contains(sprite.getX(), sprite.getY())
                || gcm.getClipBounds().contains(sprite.getX() + sprite.getWidth(), sprite.getY() + sprite.getHeight())
                || gcm.getClipBounds().contains(sprite.getX(), sprite.getY() + sprite.getHeight())
                || gcm.getClipBounds().contains(sprite.getX() + sprite.getWidth(), sprite.getY()) ;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        for(int i = 0; i < unitRenderers.size; i++){
            for(int j = 0; j < unitRenderers.get(i).size; j++){
                unitRenderers.get(i).get(j).update(dt);
            }
        }
    }

    @Override
    public int getRow(float gameX, float gameY) {
        float x = gameX - X_CAM_BOUNDS_OFFSET;
        float y = gameY - Y_CAM_BOUNDS_OFFSET;
        return (int)(y/RATIO - x + getModel().getNbRows()/2.0f);
    }

    @Override
    public int getCol(float gameX, float gameY) {
        float x = gameX - X_CAM_BOUNDS_OFFSET;
        float y = gameY - Y_CAM_BOUNDS_OFFSET;
        return (int) (y/RATIO + x - getModel().getNbColumns()/2.0f);
    }

    @Override
    public float getCenterX(int row, int col) {
        return (col - row + getModel().getNbRows()) / 2.0f + X_CAM_BOUNDS_OFFSET;
    }

    @Override
    public float getCenterY(int row, int col) {
        return (col + row + 1) * RATIO / 2.0f + Y_CAM_BOUNDS_OFFSET;
    }

    public float getRenderXFrom(int row, int col) {
        return getCenterX(row, col) - SPRITE_STD_SIZE * 0.5f;
    }

    public float getRenderYFrom(int row, int col) {
        return getCenterY(row, col)- RATIO*SPRITE_STD_SIZE * 1.5f;
    }

    @Override
    public void addTileRenderer(int row, int col, Data.TileType type) {
        if(spriteProvider != null && getModel().checkIndexes(row, col)){
            Sprite tileSprite = spriteProvider.getTileSprite(type).get(0);
            tileSprite.setPosition(getRenderXFrom(row, col), getRenderYFrom(row, col));
            lowerTileSprites.get(2*row + 2*col).add(tileSprite);
        }
    }

    @Override
    public void displayDeploymentAreas(boolean visible) {

    }

    @Override
    public BattleUnitRenderer getUnitRenderer(IUnit model) {
        for(int i = 0; i < unitRenderers.size; i++){
            for(int j = 0; j < unitRenderers.get(i).size; j++){
                if(unitRenderers.get(i).get(j).getModel() == model){
                    return unitRenderers.get(i).get(j);
                }
            }
        }
        return null;
    }

    @Override
    public AreaRenderer getAreaRenderer(Area area) {
        for(int i = 0; i < areaRenderers.size; i++){
            if(areaRenderers.get(i).getModel() == area){
                return areaRenderers.get(i);
            }
        }
        return null;
    }

    @Override
    public void addUnitRenderer(int row, int col, IUnit model) {
        if(!isUnitRendererCreated(model)){
            unitRenderers.get(2*row + 2*col).add( new IsoUnitRenderer(row, col, model, this) );
        }
    }

    @Override
    public void addAreaRenderer(Area area) {
        if(!isAreaRendererCreated(area)){
            areaRenderers.add(new IsoAreaRenderer(area));
        }
    }

    @Override
    public void removeUnitRenderer(IUnit model) {
        loop:
        {
            for (int i = 0; i < unitRenderers.size; i++) {
                for (int j = 0; j < unitRenderers.get(i).size; j++) {
                    if (unitRenderers.get(i).get(j).getModel() == model) {
                        unitRenderers.get(i).removeIndex(j);
                        break loop;
                    }
                }
            }
        }
    }

    protected void updateBURRenderCall(IsoUnitRenderer unitRenderer){
        removeUnitRenderer(unitRenderer.getModel());
        int row = getRow(unitRenderer.getCenterX(), unitRenderer.getCenterY());
        int col = getCol(unitRenderer.getCenterX(), unitRenderer.getCenterY());
        if(getModel().checkIndexes(row, col))
            unitRenderers.get(2*row + 2*col).add(unitRenderer);
    }

    protected void updateBURRenderCall(int rowTarget, int colTarget, IsoUnitRenderer unitRenderer){
        removeUnitRenderer(unitRenderer.getModel());
        int row = getRow(unitRenderer.getCenterX(), unitRenderer.getCenterY());
        int col = getCol(unitRenderer.getCenterX(), unitRenderer.getCenterY());
        if(getModel().checkIndexes(row, col) && getModel().checkIndexes(rowTarget, colTarget))
            unitRenderers.get(row + col + rowTarget + colTarget).add(unitRenderer);
    }

    @Override
    public void removeAreaRenderer(Area model) {

    }

    @Override
    protected boolean isUnitRendererCreated(IUnit model) {
        for(int i = 0; i < unitRenderers.size; i++){
            for(int j = 0; j < unitRenderers.get(i).size; j++){
                if(unitRenderers.get(i).get(j).getModel() == model){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isAreaRendererCreated(Area model){
        for(int i = 0; i < areaRenderers.size; i++){
            if(areaRenderers.get(i).getModel() == model){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void removeAreaRenderersAssociatedWith(IUnit model) {

    }

    @Override
    protected boolean isCurrentTaskCompleted() {
        return false;
    }

    @Override
    protected void setBuildTask(Notification.Build build) {

    }

    @Override
    public boolean isExecuting() {
        return false;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void dispose() {
        super.dispose();
    }

}
