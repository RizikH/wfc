package com.rizikh.wfc.model;

import java.util.BitSet;

public class Domain {
    private final BitSet possible;

    private Domain(BitSet possible) {
        this.possible = possible;
    }

    /**
     * Creates a full domain with all options available.
     * 
     * @param TileSize
     * @return A new Domain instance with all options set.
     */
    public static Domain full(int TileSize) {
        BitSet bits = new BitSet();
        bits.set(0, TileSize);
        return new Domain(bits);
    }

    /**
     * Returns the number of possible options in this domain.
     * 
     * @return The count of possible options.
     */
    public int size() {
        return possible.cardinality();
    }

    /**
     * Checks if the domain has no possible options.
     * 
     * @return True if the domain is empty, false otherwise.
     */
    public boolean isEmpty() {
        return possible.isEmpty();
    }

    /**
     * Checks if the domain is collapsed to a single option.
     * 
     * @return True if the domain has exactly one possible option, false otherwise.
     */
    public boolean isCollapsed() {
        return size() == 1;
    }

    /**
     * Retrieves the only possible option in a collapsed domain.
     * 
     * @return The ID of the only possible option.
     * @throws IllegalStateException if the domain is not collapsed.
     */
    public int getOnlyOptionId() {
        if (!isCollapsed()) {
            throw new IllegalStateException("Domain is not collapsed");
        }
        return possible.nextSetBit(0);
    }

    /**
     * Restricts the domain to only the allowed options.
     * 
     * @param allowed A BitSet representing the allowed options.
     * @return True if the domain was modified, false otherwise.
     */
    public boolean restrictTo(BitSet allowed) {
        BitSet before = (BitSet) possible.clone();
        possible.and(allowed);
        return !possible.equals(before);
    }

    /**
     * Creates a copy of this domain.
     * Used to preserve state before modifications and for backtracking.
     * 
     * @return A new Domain instance that is a copy of this one.
     */
    public Domain copy() {
        return new Domain((BitSet) possible.clone());
    }

    /**
     * Returns a copy of the internal BitSet representing possible options.
     * 
     * @return A copy of the BitSet of possible options.
     */
    public BitSet asBitSetCopy() {
        return (BitSet) possible.clone();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;

        // Iterate through all set bits and append their indices to the string
        for (int i = possible.nextSetBit(0); i >= 0; i = possible.nextSetBit(i + 1)) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(i);
            first = false;
        }
        return sb.toString();
    }
}
