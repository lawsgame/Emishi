package com.lawsgame.emishitactics.core.constants;

import com.badlogic.gdx.utils.Array;
import com.lawsgame.emishitactics.core.models.Battlefield;

public class Utils {

    /**
     *
     * @param row
     * @param col
     * @param rowTarget
     * @param colTarget
     * @return the number of tiles between the two given tiles including the target one
     */
    public static int dist(int row, int col, int rowTarget, int colTarget){
        return Math.abs(row  - rowTarget) + Math.abs(col - colTarget);
    }

    public static boolean arrayContains(Array<int[]> intArray, int[] coords){
        boolean coordsFound;
        if(intArray != null && coords != null) {
            for (int i = 0; i < intArray.size; i++) {
                if (intArray.get(i).length == coords.length) {
                    coordsFound = true;
                    for (int j = 0; j < coords.length; j++) {
                        coordsFound = coordsFound && coords[j] == intArray.get(i)[j];
                    }
                    if (coordsFound) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     *
     *        NORTH
     *          ^
     *          |
     WEST  -- (0,0) -> EAST
     *          |
     *          |
     *        SOUTH
     *
     *
     * @param rowI
     * @param colI
     * @param rowTarget
     * @param colTarget
     * @return the general orientation of the vector (rowT - rowI, colT - colI)
     */
    public static Props.Orientation getOrientationFromCoords(float rowI, float colI, float rowTarget, float colTarget){
        Props.Orientation resOr;
        float deltaR = rowTarget - rowI;
        float deltaC = colTarget - colI; // light factor to prioritized an orientation over another when both are equally valid option.

        if(deltaR > 0){
            if(deltaC > 0){
                if(deltaR > deltaC) {
                    resOr = Props.Orientation.NORTH;
                }else{
                    resOr = Props.Orientation.EAST;
                }
            }else{
                if(deltaR > -deltaC) {
                    resOr = Props.Orientation.NORTH;
                }else{
                    resOr = Props.Orientation.WEST;
                }
            }
        }else{
            if(deltaC > 0){
                if(-deltaR > deltaC) {
                    resOr = Props.Orientation.SOUTH;
                }else{
                    resOr = Props.Orientation.EAST;
                }
            }else{
                if(-deltaR > -deltaC) {
                    resOr = Props.Orientation.SOUTH;
                }else{
                    resOr = Props.Orientation.WEST;
                }
            }
        }


        return resOr;
    }

    public static int getColor32Bits(int r, int g, int b) {
        return 255 + 256*b + 256*256*g + 256*256*256*r;
    }

    public static int[] getRGBA(int color32bits){
        int r = (color32bits)&0xFF;
        int g = (color32bits>>8)&0xFF;
        int b = (color32bits>>16)&0xFF;
        int a = (color32bits>>24)&0xFF;
        return new int[]{a,b,g,r};
    }
}
