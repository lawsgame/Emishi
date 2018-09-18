package com.lawsgame.emishitactics.core.renderers;

import com.lawsgame.emishitactics.core.helpers.SpriteProvider;
import com.lawsgame.emishitactics.core.models.Battlefield;
import com.lawsgame.emishitactics.core.models.Data;
import com.lawsgame.emishitactics.core.phases.battle.renderers.IsoBFR;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class IsoBattlefieldRendererTest {
    private IsoBFR bfr0;

    @Before
    public void before(){
        int nbRows = 3;
        int nbCols = 4;

        Battlefield battlefield = new Battlefield(nbRows,nbCols);
        for(int r = 0; r < nbRows; r++){
            for(int c = 0; c < nbCols; c++){
                battlefield.getTiles()[r][c] = Data.TileType.PLAIN;
            }
        }
        bfr0 = new IsoBFR(battlefield, new SpriteProvider(), true);

    }

    @Test
    public void testGetTileCoordsFromGameCoords(){
        float xCenter = 1.5f;
        float yCenter = 0.25f;

        // r = 0 & c = 0
        assertTrue(bfr0.getRow(xCenter, yCenter) == 0);
        assertTrue(bfr0.getCol(xCenter, yCenter) == 0);

        xCenter = 2.5f;
        yCenter = 0.75f;

        // r = 0 & c = 2
        assertTrue(bfr0.getRow(xCenter, yCenter) == 0);
        assertTrue(bfr0.getCol(xCenter, yCenter) == 2);

        xCenter = 2.0f;
        yCenter = 1.0f;

        assertTrue(bfr0.getRow(xCenter, yCenter) == 1);
        assertTrue(bfr0.getCol(xCenter, yCenter) == 2);
    }

    @Test
    public void testGetXYCenterCoordsFromTileCoords(){
        int row = 0;
        int col = 0;

        // r = 0 & c = 0

        assertTrue(bfr0.getCenterX(row, col) == 1.5f);
        assertTrue(bfr0.getCenterY(row, col) == 0.25f);

        row = 0;
        col = 2;

        // r = 0 & c = 2
        assertTrue(bfr0.getCenterX(row, col) == 2.5f);
        assertTrue(bfr0.getCenterY(row, col) == 0.75f);

        row = 1;
        col = 2;

        assertTrue(bfr0.getCenterX(row, col) == 2.0f);
        assertTrue(bfr0.getCenterY(row, col) == 1.0f);

    }

}
