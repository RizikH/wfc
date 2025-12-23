package com.rizikh.wfc.tiles;

public enum TerrainTile {

    // ---- Base tiles (6)
    DEEPWATER,        // 0
    WATER,            // 1
    SAND,             // 2
    GRASS,            // 3
    GRASS_TREES,      // 4
    FOREST_TREES,     // 5

    // ---- Deepwater <-> Water (edges)
    WATER_DEEP_N,     // 6
    WATER_DEEP_E,     // 7
    WATER_DEEP_S,     // 8
    WATER_DEEP_W,     // 9

    // ---- Water <-> Sand (edges)
    SAND_WATER_N,     // 10
    SAND_WATER_E,     // 11
    SAND_WATER_S,     // 12
    SAND_WATER_W,     // 13

    // ---- Water <-> Sand (corners)
    SAND_WATER_NE,    // 14
    SAND_WATER_NW,    // 15
    SAND_WATER_SE,    // 16
    SAND_WATER_SW,    // 17

    // ---- Sand <-> Grass (edges)
    GRASS_SAND_N,     // 18
    GRASS_SAND_E,     // 19
    GRASS_SAND_S,     // 20
    GRASS_SAND_W,     // 21

    // ---- Sand <-> Grass (corners)
    GRASS_SAND_NE,    // 22
    GRASS_SAND_NW,    // 23
    GRASS_SAND_SE,    // 24
    GRASS_SAND_SW,    // 25

    // ---- Grass <-> Trees (edges)
    GRASS_TREES_N,    // 26
    GRASS_TREES_E,    // 27
    GRASS_TREES_S,    // 28
    GRASS_TREES_W,    // 29

    // ---- Forest edges
    FOREST_EDGE_LIGHT, // 30
    FOREST_EDGE_DENSE; // 31

    public static int count() {
        return values().length;
    }
}
