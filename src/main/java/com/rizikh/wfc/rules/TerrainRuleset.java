package com.rizikh.wfc.rules;

import com.rizikh.wfc.core.Direction;
import com.rizikh.wfc.tiles.TerrainTile;

import java.util.BitSet;
import java.util.EnumMap;

public class TerrainRuleset implements Ruleset {

    // Biome/edge categories
    private enum Edge { DEEPWATER, WATER, SAND, GRASS, GRASS_TREES, FOREST }

    private final EnumMap<TerrainTile, EnumMap<Direction, BitSet>> rules =
            new EnumMap<>(TerrainTile.class);

    // For each tile, what edge type is on each side
    private final EnumMap<TerrainTile, EnumMap<Direction, Edge>> edges =
            new EnumMap<>(TerrainTile.class);

    public TerrainRuleset() {
        // init masks
        for (TerrainTile t : TerrainTile.values()) {
            rules.put(t, new EnumMap<>(Direction.class));
            for (Direction d : Direction.values()) {
                rules.get(t).put(d, new BitSet(TerrainTile.count()));
            }
        }

        // init edges map
        for (TerrainTile t : TerrainTile.values()) {
            edges.put(t, new EnumMap<>(Direction.class));
        }

        defineEdgesForAllTiles();
        buildMasksFromEdges();
    }

    @Override
    public int tileCount() {
        return TerrainTile.count();
    }

    @Override
    public BitSet allowedMaskRef(int tileId, Direction dir) {
        return rules.get(TerrainTile.values()[tileId]).get(dir);
    }

    // ------------------------------------------------------------
    // 1) Define edges for every tile (THIS is the only "data" you maintain)
    // ------------------------------------------------------------
    private void setEdges(TerrainTile t, Edge n, Edge e, Edge s, Edge w) {
        edges.get(t).put(Direction.NORTH, n);
        edges.get(t).put(Direction.EAST,  e);
        edges.get(t).put(Direction.SOUTH, s);
        edges.get(t).put(Direction.WEST,  w);
    }

    private void defineEdgesForAllTiles() {
        // ---- Base tiles: same edge type on all sides
        setEdges(TerrainTile.DEEPWATER,   Edge.DEEPWATER, Edge.DEEPWATER, Edge.DEEPWATER, Edge.DEEPWATER);
        setEdges(TerrainTile.WATER,       Edge.WATER,     Edge.WATER,     Edge.WATER,     Edge.WATER);
        setEdges(TerrainTile.SAND,        Edge.SAND,      Edge.SAND,      Edge.SAND,      Edge.SAND);
        setEdges(TerrainTile.GRASS,       Edge.GRASS,     Edge.GRASS,     Edge.GRASS,     Edge.GRASS);
        setEdges(TerrainTile.GRASS_TREES, Edge.GRASS_TREES, Edge.GRASS_TREES, Edge.GRASS_TREES, Edge.GRASS_TREES);
        setEdges(TerrainTile.FOREST_TREES,Edge.FOREST,    Edge.FOREST,    Edge.FOREST,    Edge.FOREST);

        // ---- Water <-> Deepwater edges: "water tile with deepwater on that side"
        // WATER_DEEP_N means: north edge is DEEPWATER, other edges WATER
        setEdges(TerrainTile.WATER_DEEP_N, Edge.DEEPWATER, Edge.WATER, Edge.WATER, Edge.WATER);
        setEdges(TerrainTile.WATER_DEEP_E, Edge.WATER, Edge.DEEPWATER, Edge.WATER, Edge.WATER);
        setEdges(TerrainTile.WATER_DEEP_S, Edge.WATER, Edge.WATER, Edge.DEEPWATER, Edge.WATER);
        setEdges(TerrainTile.WATER_DEEP_W, Edge.WATER, Edge.WATER, Edge.WATER, Edge.DEEPWATER);

        // ---- Sand <-> Water edges (sand tile with water on that side)
        setEdges(TerrainTile.SAND_WATER_N, Edge.WATER, Edge.SAND, Edge.SAND, Edge.SAND);
        setEdges(TerrainTile.SAND_WATER_E, Edge.SAND, Edge.WATER, Edge.SAND, Edge.SAND);
        setEdges(TerrainTile.SAND_WATER_S, Edge.SAND, Edge.SAND, Edge.WATER, Edge.SAND);
        setEdges(TerrainTile.SAND_WATER_W, Edge.SAND, Edge.SAND, Edge.SAND, Edge.WATER);

        // ---- Sand <-> Water corners (water in that corner => two edges are water)
        setEdges(TerrainTile.SAND_WATER_NE, Edge.WATER, Edge.WATER, Edge.SAND,  Edge.SAND);
        setEdges(TerrainTile.SAND_WATER_NW, Edge.WATER, Edge.SAND,  Edge.SAND,  Edge.WATER);
        setEdges(TerrainTile.SAND_WATER_SE, Edge.SAND,  Edge.WATER, Edge.WATER, Edge.SAND);
        setEdges(TerrainTile.SAND_WATER_SW, Edge.SAND,  Edge.SAND,  Edge.WATER, Edge.WATER);

        // ---- Grass <-> Sand edges (grass tile with sand on that side)
        setEdges(TerrainTile.GRASS_SAND_N, Edge.SAND, Edge.GRASS, Edge.GRASS, Edge.GRASS);
        setEdges(TerrainTile.GRASS_SAND_E, Edge.GRASS, Edge.SAND, Edge.GRASS, Edge.GRASS);
        setEdges(TerrainTile.GRASS_SAND_S, Edge.GRASS, Edge.GRASS, Edge.SAND, Edge.GRASS);
        setEdges(TerrainTile.GRASS_SAND_W, Edge.GRASS, Edge.GRASS, Edge.GRASS, Edge.SAND);

        // ---- Grass <-> Sand corners (sand in that corner => two edges are sand)
        setEdges(TerrainTile.GRASS_SAND_NE, Edge.SAND, Edge.SAND,  Edge.GRASS, Edge.GRASS);
        setEdges(TerrainTile.GRASS_SAND_NW, Edge.SAND, Edge.GRASS, Edge.GRASS, Edge.SAND);
        setEdges(TerrainTile.GRASS_SAND_SE, Edge.GRASS,Edge.SAND,  Edge.SAND,  Edge.GRASS);
        setEdges(TerrainTile.GRASS_SAND_SW, Edge.GRASS,Edge.GRASS, Edge.SAND,  Edge.SAND);

        // ---- Grass <-> GrassTrees edges (grass tile with trees on that side)
        setEdges(TerrainTile.GRASS_TREES_N, Edge.GRASS_TREES, Edge.GRASS, Edge.GRASS, Edge.GRASS);
        setEdges(TerrainTile.GRASS_TREES_E, Edge.GRASS, Edge.GRASS_TREES, Edge.GRASS, Edge.GRASS);
        setEdges(TerrainTile.GRASS_TREES_S, Edge.GRASS, Edge.GRASS, Edge.GRASS_TREES, Edge.GRASS);
        setEdges(TerrainTile.GRASS_TREES_W, Edge.GRASS, Edge.GRASS, Edge.GRASS, Edge.GRASS_TREES);

        // ---- Forest edge tiles
        // Light edge: forest on 3 sides, grass_trees on 1 side (pick WEST as the "blend" side)
        setEdges(TerrainTile.FOREST_EDGE_LIGHT, Edge.FOREST, Edge.FOREST, Edge.FOREST, Edge.GRASS_TREES);

        // Dense edge: just forest everywhere (acts like a forest variant)
        setEdges(TerrainTile.FOREST_EDGE_DENSE, Edge.FOREST, Edge.FOREST, Edge.FOREST, Edge.FOREST);
    }

    // ------------------------------------------------------------
    // 2) Build allowed masks automatically: edges must match
    // ------------------------------------------------------------
    private void buildMasksFromEdges() {
        for (TerrainTile a : TerrainTile.values()) {
            for (Direction d : Direction.values()) {
                Edge need = edges.get(a).get(d);

                BitSet mask = rules.get(a).get(d);
                mask.clear();

                for (TerrainTile b : TerrainTile.values()) {
                    Edge bEdge = edges.get(b).get(d.opposite());
                    if (bEdge == need) {
                        mask.set(b.ordinal());
                    }
                }
            }
        }
    }
}
