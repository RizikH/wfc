package com.rizikh.wfc.rules;

import java.util.BitSet;

import com.rizikh.wfc.core.Direction;
import com.rizikh.wfc.tiles.RoadTile;

public final class Ruleset {

    private final BitSet[][] allowed;

    public Ruleset() {
        RoadTile[] tiles = RoadTile.values();
        Direction[] dirs = Direction.values();

        int tileCount = tiles.length;
        int dirCount = dirs.length;

        this.allowed = new BitSet[tileCount][dirCount];

        for (RoadTile a : tiles) {
            int aIndex = a.ordinal();

            for (Direction d : dirs) {
                int dIndex = d.ordinal();

                BitSet mask = new BitSet(tileCount);

                for (RoadTile b : tiles) {
                    if (isCompatible(a, b, d)) {
                        mask.set(b.ordinal());
                    }
                }

                allowed[aIndex][dIndex] = mask;
            }
        }
    }

    /**
     * Checks if two road tiles are compatible in the given direction.
     * 
     * @param a The first road tile.
     * @param b The second road tile.
     * @param d The direction from tile 'a' to tile 'b'.
     * @return True if the tiles are compatible, false otherwise.
     */
    private boolean isCompatible(RoadTile a, RoadTile b, Direction d) {
        return a.isOpen(d) == b.isOpen(d.opposite());
    }

    /**
     * Returns a BitSet of allowed neighboring tiles for tile 'a' in direction 'd'.
     * 
     * @param a The reference tile.
     * @param d The direction to check.
     * @return A BitSet where each set bit represents an allowed neighboring tile.
     */
    public BitSet allowedNeighbors(RoadTile a, Direction d) {
        return (BitSet) allowed[a.ordinal()][d.ordinal()].clone();
    }

    /**
     * Checks if tile 'b' is allowed as a neighbor of tile 'a' in direction 'd'.
     * 
     * @param a The reference tile.
     * @param d The direction to check.
     * @param b The neighboring tile to verify.
     * @return True if 'b' is allowed as a neighbor of 'a' in direction 'd', false
     *         otherwise.
     */
    public boolean isAllowed(RoadTile a, Direction d, RoadTile b) {
        return allowed[a.ordinal()][d.ordinal()].get(b.ordinal());
    }

    /**
     * Returns the total number of tile types in this ruleset.
     * 
     * @return The count of tile types.
     */
    public int tileCount() {
        return RoadTile.values().length;
    }
}
