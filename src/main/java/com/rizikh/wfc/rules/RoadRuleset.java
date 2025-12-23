package com.rizikh.wfc.rules;

import java.util.BitSet;

import com.rizikh.wfc.core.Direction;
import com.rizikh.wfc.tiles.RoadTile;

/**
 * Road rules: edges must match open/closed.
 */
public final class RoadRuleset implements Ruleset {

    private final BitSet[][] allowed;

    public RoadRuleset() {
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

    private boolean isCompatible(RoadTile a, RoadTile b, Direction d) {
        return a.isOpen(d) == b.isOpen(d.opposite());
    }

    @Override
    public int tileCount() {
        return RoadTile.values().length;
    }

    @Override
    public BitSet allowedMaskRef(int tileId, Direction dir) {
        return allowed[tileId][dir.ordinal()];
    }
}
