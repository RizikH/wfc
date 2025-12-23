package com.rizikh.wfc.rules;

import java.util.BitSet;

import com.rizikh.wfc.core.Direction;

/**
 * Generic adjacency rules for WFC.
 * The solver only needs:
 *  - how many tile types exist
 *  - for a given tileId + direction, which neighbor tileIds are allowed
 */
public interface Ruleset {
    int tileCount();

    /**
     * Fast reference to the allowed-neighbor mask for tileId in direction dir.
     * IMPORTANT: callers must NOT mutate the returned BitSet.
     */
    BitSet allowedMaskRef(int tileId, Direction dir);
}
