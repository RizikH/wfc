package com.rizikh.wfc.model;

import java.util.BitSet;

public class Cell {
    private final Domain domain;

    /**
     * Constructs a Cell with the given Domain.
     * 
     * @param domain The Domain instance representing possible options for this
     *               cell.
     */
    public Cell(Domain domain) {
        if (domain == null) {
            throw new IllegalArgumentException("Domain cannot be null");
        }

        this.domain = domain;
    }

    /**
     * Constructs a Cell with a full Domain of the specified size.
     * 
     * @param tileCount The number of possible options for this cell.
     */
    public Cell(int tileCount) {
        this.domain = Domain.full(tileCount);
    }

    /**
     * Returns the number of possible options in this cell's domain.
     * 
     * @return The count of possible options.
     */
    public int optionsCount() {
        return domain.size();
    }

    /**
     * Checks if the cell's domain has no possible options.
     * 
     * @return True if the domain is empty, false otherwise.
     */
    public boolean isEmpty() {
        return domain.isEmpty();
    }

    /**
     * Checks if the cell's domain is collapsed to a single option.
     * 
     * @return True if the domain has exactly one possible option, false otherwise.
     */
    public boolean isCollapsed() {
        return domain.isCollapsed();
    }

    /**
     * Retrieves the only possible option in a collapsed domain.
     * 
     * @return The ID of the only possible option.
     * @throws IllegalStateException if the domain is not collapsed.
     */
    public int getOnlyOptionId() {
        return domain.getOnlyOptionId();
    }

    /**
     * Creates and returns a copy of this cell's domain.
     * 
     * @return A new Domain instance that is a copy of this cell's domain.
     */
    public Domain domainCopy() {
        return domain.copy();
    }

    /**
     * Returns a copy of the BitSet representing the possible options in this cell's
     * domain.
     * 
     * @return A BitSet copy of the possible options.
     */
    public BitSet asBitSetCopy() {
        return domain.asBitSetCopy();
    }

    public boolean restrictTo(BitSet allowed) {
        return domain.restrictTo(allowed);
    }
    
}
