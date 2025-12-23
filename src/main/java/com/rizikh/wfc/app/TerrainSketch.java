package com.rizikh.wfc.app;

import processing.core.PApplet;
import processing.core.PImage;
import processing.event.MouseEvent;

import com.rizikh.wfc.model.Grid;
import com.rizikh.wfc.model.Cell;
import com.rizikh.wfc.rules.Ruleset;
import com.rizikh.wfc.rules.TerrainRuleset;      // <-- your terrain ruleset
import com.rizikh.wfc.solver.WfcSolver;
import com.rizikh.wfc.tiles.TerrainTile;

import java.util.EnumMap;

/**
 * Processing-based runner for the TERRAIN Wave Function Collapse solver.
 * Handles visualization and stepping; contains no solver logic.
 *
 * Controls:
 * - Mouse wheel: zoom centered on mouse
 * - Left-drag: pan (drag the world)
 * - Space: restart simulation
 */
public class TerrainSketch extends PApplet {

    private static final int GRID_WIDTH  = 30;
    private static final int GRID_HEIGHT = 30;
    private static final int CELL_SIZE   = 30;

    private Grid grid;
    private Ruleset ruleset;
    private WfcSolver solver;

    private EnumMap<TerrainTile, PImage> tileImages;

    private int stepsPerFrame = 100;

    // -------------------------
    // Camera (zoom + pan)
    // -------------------------
    private float zoom = 1.0f;
    private float panX = 0.0f;
    private float panY = 0.0f;
    private final float zoomMin = 0.25f;
    private final float zoomMax = 6.0f;

    // Pan dragging state
    private boolean dragging = false;
    private int lastMouseX;
    private int lastMouseY;

    public static void main(String[] args) {
        PApplet.main(TerrainSketch.class);
    }

    @Override
    public void settings() {
        size(GRID_WIDTH * CELL_SIZE, GRID_HEIGHT * CELL_SIZE);
    }

    @Override
    public void setup() {
        frameRate(60);

        resetSimulation();

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

        // World (grid) under camera transform
        pushMatrix();
        applyCamera();
        drawGrid();
        popMatrix();

        // HUD in screen space
        drawStatus();
    }

    // ------------------------------------------------------------
    // Restart on Space
    // ------------------------------------------------------------
    @Override
    public void keyPressed() {
        if (key == ' ') {
            resetSimulation();
        }
    }

    private void resetSimulation() {
        ruleset = new TerrainRuleset(); // must match TerrainTile.count()
        grid = new Grid(GRID_WIDTH, GRID_HEIGHT, ruleset.tileCount());
        solver = new WfcSolver(grid, ruleset);

        if (tileImages == null) {
            loadTerrainTileImages();
        }

        // Optional: start centered (nice default)
        centerCamera();
    }

    private void centerCamera() {
        // Center the grid in the window at the current zoom
        float worldW = GRID_WIDTH * CELL_SIZE;
        float worldH = GRID_HEIGHT * CELL_SIZE;
        panX = (width  - worldW * zoom) / 2f;
        panY = (height - worldH * zoom) / 2f;
    }

    // ------------------------------------------------------------
    // Zoom centered on mouse
    // ------------------------------------------------------------
    @Override
    public void mouseWheel(MouseEvent event) {
        float scroll = event.getCount();             // +1/-1 typically
        float factor = (scroll > 0) ? 0.9f : 1.1f;   // out vs in
        zoomAt(mouseX, mouseY, factor);
    }

    private void zoomAt(float mx, float my, float factor) {
        float oldZoom = zoom;
        float newZoom = constrain(zoom * factor, zoomMin, zoomMax);
        if (newZoom == oldZoom) return;

        // World coords under mouse BEFORE zoom
        float worldX = (mx - panX) / oldZoom;
        float worldY = (my - panY) / oldZoom;

        // Apply zoom
        zoom = newZoom;

        // Adjust pan so the same world point stays under the mouse
        panX = mx - worldX * zoom;
        panY = my - worldY * zoom;
    }

    // ------------------------------------------------------------
    // Drag-to-pan (left mouse)
    // ------------------------------------------------------------
    @Override
    public void mousePressed() {
        if (mouseButton == LEFT) {
            dragging = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }
    }

    @Override
    public void mouseDragged() {
        if (!dragging) return;

        // Mouse delta in screen space
        int dx = mouseX - lastMouseX;
        int dy = mouseY - lastMouseY;

        // Pan is screen-space translation, so we add the delta directly
        panX += dx;
        panY += dy;

        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    @Override
    public void mouseReleased() {
        dragging = false;
    }

    private void applyCamera() {
        translate(panX, panY);
        scale(zoom);
    }

    /**
     * Load Terrain tile images into the tileImages map.
     * Expects files at: data/tiles/<enum_name_lowercase>.png
     *
     * Examples:
     * - GRASS.png -> tiles/grass.png
     * - GRASS_TREES -> tiles/grass_trees.png
     */
    private void loadTerrainTileImages() {
        tileImages = new EnumMap<>(TerrainTile.class);

        for (TerrainTile tile : TerrainTile.values()) {
            String filename = "terrain/" + tile.name().toLowerCase() + ".png";
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
                    text(cell.optionsCount(), px + CELL_SIZE / 2f, py + CELL_SIZE / 2f);
                }
            }
        }

        popStyle();
    }

    private void drawTile(int tileId, int px, int py) {
        // IMPORTANT:
        // This assumes your tileId mapping matches TerrainTile.values() order.
        TerrainTile tile = TerrainTile.values()[tileId];
        image(tileImages.get(tile), px, py, CELL_SIZE, CELL_SIZE);
    }

    private void drawStatus() {
        fill(0);
        text(solver.getStatus().toString(), width / 2f, 14);
        text("Wheel: zoom | Drag: pan | Space: restart", width / 2f, 30);
    }
}
