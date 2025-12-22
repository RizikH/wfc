package com.rizikh.wfc.catalog;

public record TileType(int id, String name, double weight) {

    /**
     * 
     * @param id
     * @param name
     * @param weight
     * 
     * @throws IllegalArgumentException if id is negative, weight is non-positive, or name is null/empty
     */
    public TileType{
        if (id < 0) {
            throw new IllegalArgumentException("ID must be non-negative");
        }
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must be non-empty");
        }
    }
}
