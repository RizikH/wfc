package com.rizikh.wfc.model;

import com.rizikh.wfc.core.Direction;

public enum RoadTile {

    // Grass tile (no roads) closed in all directions
    BLANK(0b0000),

    // T-shaped roads
    UP(0b1011),
    RIGHT(0b0111),
    DOWN(0b1110),
    LEFT(0b1101);

    private final int bitmask;

    RoadTile(int bitmask) {
        this.bitmask = bitmask;
    }

    /**
     * Returns true if the road tile is open in the given side.
     * Assumes Direction ordinal matches bitmask indexing. (NORTH=0, EAST=1, SOUTH=2, WEST=3)
     * 
     * @param dir
     * @return
     */
    public boolean isOpen(Direction dir) {
        int mask = 1 << dir.ordinal();
        return (bitmask & mask) != 0;
    }
}
