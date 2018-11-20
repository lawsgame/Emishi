package com.lawsgame.emishitactics.core.phases.battle.helpers;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Area;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.models.Unit;
import com.lawsgame.emishitactics.core.phases.battle.renderers.interfaces.BattlefieldRenderer;
import com.lawsgame.emishitactics.engine.patterns.command.SimpleCommand;

public class TileHighlighter {
    private Area selectedTile;
    private Area touchedTile;
    private Area warchiefBannerCoveredArea;
    public final FoeActionArea ffa;

    private BattlefieldRenderer bfr;



    public TileHighlighter(BattlefieldRenderer bfr){
        this.bfr = bfr;

        this.selectedTile = new Area(bfr.getModel(), Data.AreaType.SELECTED_UNIT);
        this.touchedTile = new Area(bfr.getModel(), Data.AreaType.TOUCHED_TILE);
        this.warchiefBannerCoveredArea = new Area(bfr.getModel(), Data.AreaType.BANNER_AREA);
        bfr.addAreaRenderer(selectedTile);
        bfr.addAreaRenderer(touchedTile);
        bfr.addAreaRenderer(warchiefBannerCoveredArea);
        bfr.getAreaRenderer(touchedTile).setVisible(false);
        bfr.getAreaRenderer(selectedTile).setVisible(false);
        bfr.getAreaRenderer(warchiefBannerCoveredArea).setVisible(false);

        this.ffa = new FoeActionArea(bfr);
    }




    private int rowSelectedTile = -1, colSelectedTile = -1;
    /**
     *
     * @param touchedRow : row of tile to highlight
     * @param touchedCol : col of tile to highlight
     * @param squadInfoShown : show banner range of the WC if still fighting
     */
    public void highlight(int touchedRow, int touchedCol, final boolean squadInfoShown, int selectedTileRow, int selectedTileCol){

        if(bfr.getModel().isTileExisted(touchedRow, touchedCol)) {

            // touch feed back
            touchedTile.setTiles(touchedRow, touchedCol, true);
            bfr.getAreaRenderer(touchedTile).setVisible(true);

            // update selected tile coords in memory
            rowSelectedTile = selectedTileRow;
            colSelectedTile = selectedTileCol;


            // show the current selected tile if the last touched tile highlighted was a different one.
            if(bfr.getModel().isTileExisted(rowSelectedTile, colSelectedTile) && (touchedRow != rowSelectedTile || touchedCol != colSelectedTile)){
                selectedTile.setTiles(selectedTileRow, selectedTileCol, true);
                bfr.getAreaRenderer(selectedTile).setVisible(true);
            } else if(bfr.getModel().checkIndexes(selectedTileRow, selectedTileCol)){
                bfr.getAreaRenderer(selectedTile).setVisible(false);
            }

            // show warchief positon and his banner covered area
            boolean SquadActuallyShown = false;
            if (squadInfoShown && bfr.getModel().isTileOccupied(touchedRow, touchedCol)) {
                Unit warchief = bfr.getModel().getUnit(touchedRow, touchedCol).getWarchief();
                if (warchief != bfr.getModel().getUnit(touchedRow, touchedCol)
                        && bfr.getModel().isUnitDeployed(warchief)
                        && !warchief.isOutOfAction()) {

                    SquadActuallyShown = true;
                    warchiefBannerCoveredArea.setTiles(bfr.getUnitRenderer(warchief).getCurrentRow(), bfr.getUnitRenderer(warchief).getCurrentCol(), 0, warchief.getArmy().getBannerRange(warchief), true);
                    bfr.getAreaRenderer(warchiefBannerCoveredArea).setVisible(true);

                }
            }

            if(!SquadActuallyShown){
                bfr.getAreaRenderer(warchiefBannerCoveredArea).setVisible(false);
            }

        }
    }

    public enum SltdUpdateMode{
        MATCH_TOUCHED_TILE,
        ERASE_SLTD_TILE_MEMORY,
        UNCHANGED
    }

    public void highlight(int touchedRow, int touchedCol, final boolean squadInfoShown, SltdUpdateMode mode){

        switch (mode){
            case MATCH_TOUCHED_TILE:
                highlight(touchedRow, touchedCol, squadInfoShown, touchedRow, touchedCol);
                break;
            case ERASE_SLTD_TILE_MEMORY:
                highlight(touchedRow, touchedCol, squadInfoShown, -1, -1);
                break;
            case UNCHANGED:
                highlight(touchedRow, touchedCol, squadInfoShown, rowSelectedTile, colSelectedTile);
                break;
        }

    }

    public void removeTileHighlighting(boolean exceptSelectedTile, boolean eraseMemory){

        if(eraseMemory){
            rowSelectedTile = -1;
            colSelectedTile = -1;
        }

        bfr.getAreaRenderer(touchedTile).setVisible(false);
        bfr.getAreaRenderer(warchiefBannerCoveredArea).setVisible(false);
        if(!exceptSelectedTile) bfr.getAreaRenderer(selectedTile).setVisible(false);
    }

    public SimpleCommand generateTileHighlightingEndTurnCommand(){
        return new SimpleCommand() {
            @Override
            public void apply() {
                rowSelectedTile = -1;
                colSelectedTile = -1;
                bfr.getAreaRenderer(selectedTile).setVisible(false);

            }
        };
    }




    //--------------------- FAA CLASS- -------------------------------------

    public static class FoeActionArea{
        private Area actionArea;
        private Array<Unit> foes;

        public FoeActionArea(BattlefieldRenderer bfr){
            this.actionArea = new Area(bfr.getModel(), Data.AreaType.FOE_ACTION_AREA);
            this.foes = new Array<Unit>();
            bfr.addAreaRenderer(this.actionArea);
        }

        public void update(int rowFoe, int colFoe){
            Battlefield bf = actionArea.getBattlefield();
            if(bf.isTileOccupied(rowFoe, colFoe)) {
                Unit foe  = bf.getUnit(rowFoe, colFoe);
                if (foes.contains(foe, true)) {

                    this.foes.removeValue(foe, true);
                    this.actionArea.clear(false);
                    int[] foePos;
                    Array<int[]> tiles = new Array<int[]>();
                    for(int i = 0; i < foes.size; i++) {
                        foePos = bf.getUnitPos(foes.get(i));
                        tiles.addAll(bf.getActionArea(foePos[0], foePos[1]));
                    }
                    actionArea.add(tiles, true);
                } else {

                    foes.add(foe);
                    actionArea.add(bf.getActionArea(rowFoe, colFoe), true);
                }
            }
        }

        public void clear(){
            actionArea.clear(true);
            foes.clear();
        }
    }
}
