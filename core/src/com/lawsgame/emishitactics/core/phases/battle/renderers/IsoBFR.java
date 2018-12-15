package com.lawsgame.emishitactics.core.phases.battle.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.helpers.AssetProvider;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Notification;
import com.lawsgame.emishitactics.core.models.Tile;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.AreaRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattleUnitRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.TileRenderer;
import com.lawsgame.emishitactics.engine.CameraManager;
import com.lawsgame.emishitactics.engine.rendering.Renderer;

public class IsoBFR extends BattlefieldRenderer {
    public static float RATIO = 0.5f; // <1 : ratio between the the great and little dialogue
    public static final float SPRITE_STD_SIZE = 1.25f;
    private static final float CAM_VELOCITY = 45.0f;
    private static float X_CAM_BOUNDS_OFFSET = 1f;
    private static float Y_CAM_BOUNDS_OFFSET = RATIO;

    private boolean visible;
    protected AssetManager assetManager;
    protected AssetProvider assetProvider;
    private Array<Array<IsoTileRenderer>> tileRenderers;
    private Array<Array<BattleUnitRenderer>> unitRenderers;
    private Array<Array<AreaRenderer>> areaRenderers;

    private ShapeRenderer backkgroundRenderer;


    public IsoBFR(Battlefield battlefield, CameraManager gcm, AssetProvider assetProvider, AssetManager assetManager){
        this(battlefield, gcm, assetProvider, assetManager, false);
    }

    public IsoBFR(Battlefield battlefield, CameraManager gcm, AssetProvider assetProvider, AssetManager assetManager, boolean test) {
        super(battlefield, gcm);
        this.assetManager = assetManager;
        int depth = 2*(battlefield.getNbRows() + battlefield.getNbColumns()) - 3;
        this.tileRenderers = new Array<Array<IsoTileRenderer>> ();
        this.unitRenderers = new Array<Array<BattleUnitRenderer>>();
        for(int i = 0; i < depth; i++) {
            tileRenderers.add(new Array<IsoTileRenderer>());
            unitRenderers.add(new Array<BattleUnitRenderer>());
        }

        this.visible = true;
        this.assetProvider = assetProvider;

        this.areaRenderers  = new Array<Array<AreaRenderer>>();
        this.areaRenderers.add(new Array<AreaRenderer>());
        this.areaRenderers.add(new Array<AreaRenderer>());
        this.areaRenderers.add(new Array<AreaRenderer>());
        this.areaRenderers.add(new Array<AreaRenderer>());

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

            // set up area renderers
            for (int i = 0; i < getModel().getUnitAreas().size ; i++) {
                addAreaRenderer(getModel().getUnitAreas().get(i));
            }
            for (int i = 0; i < battlefield.getDeploymentAreas().size; i++) {
                addAreaRenderer(battlefield.getDeploymentAreas().get(i));
            }

            //set weather renderer
            this.renderedWeather = getModel().getWeather();
        }
    }

    @Override
    public void setGameCamParameters() {
        float width = 2*X_CAM_BOUNDS_OFFSET + (getModel().getNbRows() + getModel().getNbColumns()) / 2.0f;
        float height = 2*Y_CAM_BOUNDS_OFFSET + (getModel().getNbRows() + getModel().getNbColumns()) * RATIO / 2.0f;

        gcm.setCameraBoundaries(width, height);
        gcm.setCameraVelocity(CAM_VELOCITY);
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
            for(int i = tileRenderers.size - 1; i >= 0 ; i--){
                for(int j = 0; j < tileRenderers.get(i).size; j++){
                    if(tileRenderers.get(i).get(j).isInFrame(this)) {
                        tileRenderers.get(i).get(j).renderLowerPart(batch);
                    }
                }
            }
            for(int i =0 ; i < areaRenderers.size ; i++){
                for(int j =0 ; j < areaRenderers.get(i).size ; j++){
                    areaRenderers.get(i).get(j).render(batch);
                }
            }
            for(int i = unitRenderers.size - 1; i >= 0 ; i--){
                for(int j = 0; j < tileRenderers.get(i).size; j++){
                    if(tileRenderers.get(i).get(j).isInFrame(this)) {
                        tileRenderers.get(i).get(j).renderUpperPart(batch);
                    }
                }
                for(int j = 0; j < unitRenderers.get(i).size; j++){
                    unitRenderers.get(i).get(j).render(batch);
                }

            }
        }
    }

    public boolean isInFrame(Sprite sprite){
        return sprite != null
                && (gcm.getClipBounds().contains(sprite.getX(), sprite.getY())
                || gcm.getClipBounds().contains(sprite.getX() + sprite.getWidth(), sprite.getY() + sprite.getHeight())
                || gcm.getClipBounds().contains(sprite.getX(), sprite.getY() + sprite.getHeight())
                || gcm.getClipBounds().contains(sprite.getX() + sprite.getWidth(), sprite.getY())) ;
    }

    @Override
    public void update(float dt) {
        for(int i = 0; i < unitRenderers.size; i++){
            for(int j = 0; j < tileRenderers.get(i).size; j++){
                tileRenderers.get(i).get(j).update(dt);
            }
            for(int j = 0; j < unitRenderers.get(i).size; j++){
                unitRenderers.get(i).get(j).update(dt);
            }
        }
        for(int i = 0; i < areaRenderers.size; i++) {
            for(int j = 0; j < areaRenderers.get(i).size; j++) {
                areaRenderers.get(i).get(j).update(dt);
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

    public float getRenderYFrom(int row, int col, boolean upper) {
        return getCenterY(row, col)- RATIO* (SPRITE_STD_SIZE * ((upper) ? 0.5f : 1.5f ));
    }

    @Override
    public void addTileRenderer(int row, int col, Tile model) {

        if(assetProvider != null && getModel().checkIndexes(row, col)){
            int renderIndex  = 2*row + 2*col;
            for(int i = 0; i < tileRenderers.get(renderIndex).size; i++){
                if(tileRenderers.get(renderIndex).get(i).row == row && tileRenderers.get(renderIndex).get(i).col == col){
                    tileRenderers.get(renderIndex).get(i).getModel().detach(tileRenderers.get(renderIndex).get(i));
                    tileRenderers.get(renderIndex).removeIndex(i);
                }
            }
            tileRenderers.get(renderIndex).add(IsoTileRenderer.create(row, col, model, this));
        }
    }

    @Override
    public BattleUnitRenderer getUnitRenderer(Unit model) {
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
            for(int j = 0; j < areaRenderers.get(i).size; j++) {
                if (areaRenderers.get(i).get(j).getModel() == area) {
                    return areaRenderers.get(i).get(j);
                }
            }
        }
        return null;
    }

    @Override
    public TileRenderer getTileRenderer(int row, int col) {
        int renderIndex = 2*row + 2*col;
        for(int j = 0; j < tileRenderers.get(renderIndex).size; j++) {
            if (tileRenderers.get(renderIndex).get(j).row == row && tileRenderers.get(renderIndex).get(j).col == col) {
                return tileRenderers.get(renderIndex).get(j);
            }
        }
        return null;
    }


    protected void updateBURRenderCall(IsoUnitRenderer unitRenderer){
        removeUnitRenderer(unitRenderer.getModel(), true);
        int row = getRow(unitRenderer.getCenterX(), unitRenderer.getCenterY());
        int col = getCol(unitRenderer.getCenterX(), unitRenderer.getCenterY());
        if(getModel().checkIndexes(row, col)) {
            addUnitRenderer(row, col, unitRenderer);
        }
    }

    protected void updateBURRenderCall(int rowTarget, int colTarget, IsoUnitRenderer unitRenderer){
        removeUnitRenderer(unitRenderer.getModel(), true);
        int row = getRow(unitRenderer.getCenterX(), unitRenderer.getCenterY());
        int col = getCol(unitRenderer.getCenterX(), unitRenderer.getCenterY());
        if(getModel().checkIndexes(row, col) && getModel().checkIndexes(rowTarget, colTarget)) {
            addUnitRenderer(row, col, rowTarget, colTarget, unitRenderer);
        }
    }

    public int getRenderCall(Renderer renderer) {
        if(renderer instanceof IsoUnitRenderer) {
            IsoUnitRenderer iur = (IsoUnitRenderer) renderer;
            for(int i = 0; i < unitRenderers.size; i++){
                for(int j = 0; j < unitRenderers.get(i).size; j++){
                    if(unitRenderers.get(i).get(j) == iur){
                        return i;
                    }
                }
            }
        }else if(renderer instanceof IsoTileRenderer) {
            IsoTileRenderer itr = (IsoTileRenderer) renderer;
            for(int i = 0; i < tileRenderers.size; i++){
                for(int j = 0; j < tileRenderers.get(i).size; j++){
                    if(tileRenderers.get(i).get(j) == itr){
                        return i;
                    }
                }
            }
        }
        return -1;
    }


    private void addUnitRenderer(int row, int col, int rowTarget, int colTarget, BattleUnitRenderer bur){
        if(!isUnitRendererCreated(bur.getModel())){
            unitRenderers.get(row + col + rowTarget + colTarget).add(bur);
        }
    }


    private void addUnitRenderer(int row, int col, BattleUnitRenderer bur){
        addUnitRenderer(row, col, row, col, bur);
    }

    @Override
    public BattleUnitRenderer addUnitRenderer(int row, int col, Unit model) {
        if(!isUnitRendererCreated(model)) {
            BattleUnitRenderer bur = new IsoUnitRenderer(row, col, model, this);
            addUnitRenderer(row, col, bur);
            return bur;
        }
        return null;
    }

    @Override
    public void removeUnitRenderer(Unit model, boolean uponMoving) {
        BattleUnitRenderer bur;
        for (int i = 0; i < unitRenderers.size; i++) {
            for (int j = 0; j < unitRenderers.get(i).size; j++) {
                if (unitRenderers.get(i).get(j).getModel() == model) {
                    bur = unitRenderers.get(i).removeIndex(j);
                    removeAreaRenderersAssociatedWith(bur.getModel(), uponMoving);
                    j--;
                }
            }
        }

    }

    @Override
    protected boolean isUnitRendererCreated(Unit model) {
        for(int i = 0; i < unitRenderers.size; i++){
            for(int j = 0; j < unitRenderers.get(i).size; j++){
                if(unitRenderers.get(i).get(j).getModel() == model){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void addAreaRenderer(Area area) {
        if(!isAreaRendererCreated(area)){
            areaRenderers.get(area.getType().getLayerIndex()).add(new IsoAreaRenderer(area, this, this.assetProvider));
        }
    }

    @Override
    public void removeAreaRenderer(Area model) {
        for(int i = 0; i < areaRenderers.size; i++){
            for(int j = 0; j < areaRenderers.get(i).size; j++){
                if(areaRenderers.get(i).get(j).getModel() == model) {
                    model.detach(areaRenderers.get(i).get(j));
                    areaRenderers.get(i).removeIndex(j);
                    break;
                }
            }
        }
    }



    @Override
    public boolean isAreaRendererCreated(Area model){
        for(int i = 0; i < areaRenderers.size; i++){
            for(int j = 0; j < areaRenderers.get(i).size; j++){
                if(areaRenderers.get(i).get(j).getModel() == model){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void removeAreaRenderersAssociatedWith(Unit model, boolean uponMoving) {
        Area.UnitArea unitArea;
        for(int i = 0; i < areaRenderers.size; i++){
            for(int j = 0; j < areaRenderers.get(i).size; j++){
                if(areaRenderers.get(i).get(j).getModel() instanceof Area.UnitArea) {

                    unitArea = (Area.UnitArea) areaRenderers.get(i).get(j).getModel();
                    if (unitArea.getActor() == model && (!uponMoving || unitArea.isRemovedUponMovingUnit())){

                        model.detach(areaRenderers.get(i).get(j));
                        areaRenderers.get(i).removeIndex(j);
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void displayBuilding(int rowTile, int colTile, Tile buildingTile) {
        addTileRenderer(rowTile, colTile, buildingTile);
    }

    @Override
    protected void displayCollapsing(int rowTile, int colTile, Tile newbuildingTile, Tile oldbuildingTile) {
        addTileRenderer(rowTile, colTile, newbuildingTile);
    }

    @Override
    protected void displayPlundering(int rowTile, int colTile, Tile newbuildingTile, Tile oldbuildingTile) {
        addTileRenderer(rowTile, colTile, newbuildingTile);
    }

    @Override
    public boolean isIdling() {
        return true;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public Data.Orientation getOrientationFromPos(float xCenter, float yCenter, float xTarget, float yTarget){
        Data.Orientation resOr;
        float deltaX = xTarget - xCenter;
        float deltaY = yTarget - yCenter;

        if(deltaX > 0){
            if(deltaY > 0){
                resOr = Data.Orientation.EAST;
            }else{
                resOr = Data.Orientation.SOUTH;
            }
        }else{
            if(deltaY > 0){
                resOr = Data.Orientation.NORTH;
            }else{
                resOr = Data.Orientation.WEST;
            }
        }


        return resOr;
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public String toLongShort(){
        String str = "\nBFR\n";
        str+= "UNITS\n";
        for(int i = 0; i < unitRenderers.size; i++){
            if(unitRenderers.get(i).size > 0) {
                str += i+") ";
                for (int j = 0; j < unitRenderers.get(i).size; j++) {
                    str += unitRenderers.get(i).get(j).getModel().getName()+" | ";
                }
                str +="\n";
            }
        }
        return str;
    }


}
