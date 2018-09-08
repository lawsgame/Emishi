package com.lawsgame.emishitactics.core.phases.battle.renderers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.lawsgame.emishitactics.TacticsGame;
import com.lawsgame.emishitactics.core.helpers.SpritePool;
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

    private static final float CAM_VELOCITY = 15.0f;
    private static float X_CAM_BOUNDS_OFFSET = 1f;
    private static float Y_CAM_BOUNDS_OFFSET = RATIO;

    private boolean visible;
    private SpritePool spritePool;
    private TextureRegion[] lowerTileSprites;
    private float[][] tileParameters; // x, y
    private BattleUnitRenderer[] unitRenderers;
    private ShapeRenderer backkgroundRenderer;

    public IsoBFR(Battlefield battlefield, SpritePool spritePool){
        this(battlefield, spritePool,false);
    }


    public IsoBFR(Battlefield battlefield, SpritePool spritePool, boolean test) {
        super(battlefield);
        this.lowerTileSprites = new TextureRegion[battlefield.getNbRows()*battlefield.getNbColumns()];
        this.tileParameters = new float[battlefield.getNbRows()*battlefield.getNbColumns()][2];
        this.unitRenderers = new BattleUnitRenderer[battlefield.getNbRows()*battlefield.getNbColumns()];

        // required for the junit testes
        if(test){
           X_CAM_BOUNDS_OFFSET = 0;
           Y_CAM_BOUNDS_OFFSET = 0;
        }else{
            this.backkgroundRenderer = new ShapeRenderer();
        }
        this.visible = true;
        this.spritePool = spritePool;

        // pre calculate buildingType coords and texture region to render to prevent extra calculus each game loop.
        for (int r = 0; r < battlefield.getNbRows(); r++) {
            for (int c = 0; c < battlefield.getNbColumns(); c++) {
                addTileRenderer(r, c, getModel().getTile(r, c));
                if(battlefield.isTileOccupied(r, c)) {
                    addUnitRenderer(r, c, getModel().getUnit(r, c));
                }
            }
        }

        // setTiles up area renderers
        for(Data.Allegeance a : Data.Allegeance.values()){
            for (int i = 0; i < getModel().getGuardedAreas().get(a).size; i++) {
                addAreaRenderer(getModel().getGuardedAreas().get(a).get(i));
            }
        }
        for(int i = 0; i < battlefield.getDeploymentAreas().size; i++){
            addAreaRenderer(battlefield.getDeploymentAreas().get(i));
        }
    }

    @Override
    public void prerender() {
        backkgroundRenderer.begin(ShapeRenderer.ShapeType.Filled);
        backkgroundRenderer.rect(0,0,
                TacticsGame.SCREEN_PIXEL_WIDTH,
                TacticsGame.SCREEN_PIXEL_HEIGHT,
                getModel().getWeather().getLowerColor(),
                getModel().getWeather().getLowerColor(),
                getModel().getWeather().getUpperColor(),
                getModel().getWeather().getUpperColor());
        backkgroundRenderer.end();
    }

    @Override
    public void render(SpriteBatch batch) {
        if(visible) {
            for(int i = 0; i < lowerTileSprites.length; i++){
                if(lowerTileSprites != null) {
                    batch.draw(lowerTileSprites[i], tileParameters[i][0], tileParameters[i][1], SPRITE_STD_SIZE, SPRITE_STD_SIZE);
                }
            }
        }
    }

    @Override
    public int getRowFrom(float gameX, float gameY) {
        float x = gameX - X_CAM_BOUNDS_OFFSET;
        float y = gameY - Y_CAM_BOUNDS_OFFSET;
        return (int)(y/RATIO - x + getModel().getNbRows()/2.0f);
    }

    @Override
    public int getColFrom(float gameX, float gameY) {
        float x = gameX - X_CAM_BOUNDS_OFFSET;
        float y = gameY - Y_CAM_BOUNDS_OFFSET;
        return (int) (y/RATIO + x - getModel().getNbColumns()/2.0f);
    }

    public float getCenterXFrom(int row, int col) {
        return (col - row + getModel().getNbRows()) / 2.0f + X_CAM_BOUNDS_OFFSET;
    }

    public float getCenterYFrom(int row, int col) {
        return (col + row + 1) * RATIO / 2.0f + Y_CAM_BOUNDS_OFFSET;
    }

    public float getRenderXFrom(int row, int col) {
        return getCenterXFrom(row, col) - SPRITE_STD_SIZE * 0.5f;
    }

    public float getRenderYFrom(int row, int col) {
        return getCenterYFrom(row, col)- RATIO*SPRITE_STD_SIZE * 1.5f;
    }

    public int getRenderingIndex(int row, int col){
        int returnedIndex = -1;

        //System.out.print("(R, C) = ("+row+", "+col+") ");

        if(getModel().isTileExisted(row, col)) {

            int currentIndex = -1;
            int sum = getModel().getNbRows() + getModel().getNbColumns() - 2;
            while (returnedIndex == -1) {
                loop :
                {
                    for (int r = 0; r < getModel().getNbRows(); r++) {
                        for (int c = 0; c < getModel().getNbColumns(); c++) {
                            if (r + c == sum) {
                                currentIndex++;
                                if (r == row && c == col) {
                                    //System.out.println(" => current index : "+currentIndex);
                                    returnedIndex = currentIndex;
                                    break loop;
                                }
                            }
                        }
                    }
                }
                sum -- ;
            }
        }
        return returnedIndex;

    }

    @Override
    public void addTileRenderer(int row, int col, Data.TileType type) {
        if(spritePool != null && getModel().checkIndexes(row, col)){
            int renderIndex = getRenderingIndex(row, col);
            TextureRegion tileTR = spritePool.tileSprites.get(type);
            lowerTileSprites[renderIndex] = (tileTR != null) ? tileTR : spritePool.undefinedTileSprite;
            float xRender = getRenderXFrom(row, col);
            float yRender = getRenderYFrom(row, col);
            tileParameters[renderIndex][0] =xRender;
            tileParameters[renderIndex][1] =yRender;
        }
    }

    @Override
    public void displayDeploymentAreas(boolean visible) {

    }

    @Override
    public void setGameCamParameters(CameraManager cameraManager) {
        float width = 2*X_CAM_BOUNDS_OFFSET + (getModel().getNbRows() + getModel().getNbColumns()) / 2.0f;
        float height = 2*Y_CAM_BOUNDS_OFFSET + (getModel().getNbRows() + getModel().getNbColumns()) * RATIO / 2.0f;
        cameraManager.setCameraBoundaries(width, height);
        cameraManager.setCameraVelocity(CAM_VELOCITY);

    }

    @Override
    public BattleUnitRenderer getUnitRenderer(IUnit model) {
        for (int i = 0; i < lowerTileSprites.length; i++) {
            if(unitRenderers[i] != null && unitRenderers[i].getModel() == model){
                return unitRenderers[i];
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
            unitRenderers[getRenderingIndex(row, col)] = new IsoUnitRenderer(row, col, model, this);
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

    }

    @Override
    public void removeAreaRenderer(Area model) {

    }

    @Override
    protected boolean isUnitRendererCreated(IUnit model) {
        for(int i = 0; i < unitRenderers.length; i++){
            if(unitRenderers[i] != null && unitRenderers[i].getModel() == model){
                return true;
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
    public void update(float dt) {
        super.update(dt);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public SpritePool getSpritePool() {
        return spritePool;
    }
}
