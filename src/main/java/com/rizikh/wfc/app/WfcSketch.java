package com.rizikh.wfc.app;

import processing.core.PApplet;
import processing.core.PImage;

import com.rizikh.wfc.model.Grid;
import com.rizikh.wfc.model.Cell;
import com.rizikh.wfc.rules.Ruleset;
import com.rizikh.wfc.solver.WfcSolver;
import com.rizikh.wfc.tiles.RoadTile;

import java.util.EnumMap;

/**
 * Processing-based runner for the Wave Function Collapse solver.
 * Handles visualization and stepping; contains no solver logic.
 */
public class WfcSketch extends PApplet {

    private static final int GRID_WIDTH  = 20;
    private static final int GRID_HEIGHT = 20;
    private static final int CELL_SIZE   = 32;

    private Grid grid;
    private Ruleset ruleset;
    private WfcSolver solver;

    private EnumMap<RoadTile, PImage> tileImages;

    private int stepsPerFrame = 1;

    public static void main(String[] args) {
        PApplet.main(WfcSketch.class);
    }

    @Override
    public void settings() {
        size(GRID_WIDTH * CELL_SIZE, GRID_HEIGHT * CELL_SIZE);
    }

    @Override
    public void setup() {
        frameRate(60);

        ruleset = new Ruleset();
        grid = new Grid(GRID_WIDTH, GRID_HEIGHT, ruleset.tileCount());
        solver = new WfcSolver(grid, ruleset);

        loadTileImages();

        textAlign(CENTER, CENTER);
        textSize(14);
    }

    @Override
    public void draw() {
        background(220);

        if (solver.getStatus() == WfcSolver.Status.RUNNING) {
            for (int i = 0; i < stepsPerFrame; i++) {
                solver.step();
            }
        }

        drawGrid();
        drawStatus();
    }

    private void loadTileImages() {
        tileImages = new EnumMap<>(RoadTile.class);

        for (RoadTile tile : RoadTile.values()) {
            String filename = "tiles/" + tile.name().toLowerCase() + ".png";
            PImage img = loadImage(filename);

            if (img == null) {
                throw new RuntimeException("Failed to load image: " + filename);
            }

            tileImages.put(tile, img);
        }
    }

    private void drawGrid() {
        pushStyle();

        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {

                int px = x * CELL_SIZE;
                int py = (grid.getHeight() - 1 - y) * CELL_SIZE;

                Cell cell = grid.getCell(x, y);

                noStroke();
                noFill();
                rect(px, py, CELL_SIZE, CELL_SIZE);

                if (cell.isEmpty()) {
                    fill(200, 50, 50);
                    rect(px, py, CELL_SIZE, CELL_SIZE);
                    continue;
                }

                if (cell.isCollapsed()) {
                    drawTile(cell.getOnlyOptionId(), px, py);
                } else {
                    fill(0);
                    text(
                        cell.optionsCount(),
                        px + CELL_SIZE / 2f,
                        py + CELL_SIZE / 2f
                    );
                }
            }
        }

        popStyle();
    }

    private void drawTile(int tileId, int px, int py) {
        RoadTile tile = RoadTile.values()[tileId];
        image(tileImages.get(tile), px, py, CELL_SIZE, CELL_SIZE);
    }

    private void drawStatus() {
        fill(0);
        text(
            solver.getStatus().toString(),
            width / 2f,
            14
        );
    }
}
