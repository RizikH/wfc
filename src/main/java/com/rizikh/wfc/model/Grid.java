package com.rizikh.wfc.model;

public class Grid {
    private final int width;
    private final int height;
    private final int tileCount;
    private final Cell[][] cells;

    /**
     * Constructs a Grid with the specified dimensions and tile count.
     * 
     * @param width     The width of the grid.
     * @param height    The height of the grid.
     * @param tileCount The number of different tile types.
     */
    public Grid(int width, int height, int tileCount) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive integers.");
        }
        if (tileCount <= 0) {
            throw new IllegalArgumentException("Tile count must be a positive integer.");
        }

        this.width = width;
        this.height = height;
        this.tileCount = tileCount;
        this.cells = new Cell[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new Cell(tileCount);
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTileCount() {
        return tileCount;
    }

    public boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public Cell getCell(int x, int y) {
        if (!inBounds(x, y)) {
            throw new IndexOutOfBoundsException("Coordinates out of bounds: (" + x + ", " + y + ")");
        }
        return cells[x][y];
    }

    public boolean isCollapsed(int x, int y) {
        return getCell(x, y).isCollapsed();
    }

    public int optionsCount(int x, int y) {
        return getCell(x, y).optionsCount();
    }
}
