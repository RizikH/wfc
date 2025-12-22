package com.rizikh.wfc.solver;

import com.rizikh.wfc.core.Direction;
import com.rizikh.wfc.model.Grid;
import com.rizikh.wfc.rules.Ruleset;
import com.rizikh.wfc.solver.Pos;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

public class WfcSolver {
    public enum Status {
        RUNNING,
        SOLVED,
        CONTRADICTION
    }

    private final Grid grid;
    private final Ruleset ruleset;
    private final Random rng = new Random();
    private Status status;

    /**
     * Constructs a WfcSolver with the specified Grid and Ruleset.
     * 
     * @param grid    The Grid instance representing the WFC grid.
     * @param ruleset The Ruleset instance defining tile adjacency rules.
     * @throws IllegalArgumentException if grid or ruleset is null, or if their
     *                                  tile counts do not match.
     */
    public WfcSolver(Grid grid, Ruleset ruleset) {
        if (grid == null) {
            throw new IllegalArgumentException("Grid cannot be null");
        }
        if (ruleset == null) {
            throw new IllegalArgumentException("Ruleset cannot be null");
        }

        if (grid.getTileCount() != ruleset.tileCount()) {
            throw new IllegalArgumentException(
                    "Grid tileCount (" + grid.getTileCount() +
                            ") does not match Ruleset tileCount (" + ruleset.tileCount() + ")");
        }
        this.grid = grid;
        this.ruleset = ruleset;
        this.status = Status.RUNNING;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isSolved() {
        return status == Status.SOLVED;
    }

    public boolean hasContradiction() {
        return status == Status.CONTRADICTION;
    }

    public Grid getGrid() {
        return grid;
    }

    /**
     * Checks if the entire grid is solved.
     * 
     * @return True if all cells in the grid are collapsed, false otherwise.
     */
    private boolean checkSolved() {
        for (int x = 0; x < grid.getWidth(); ++x) {
            for (int y = 0; y < grid.getHeight(); ++y) {
                if (!grid.isCollapsed(x, y)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Checks if there is any contradiction in the grid.
     * 
     * @return True if a contradiction is found, false otherwise.
     */
    private boolean checkContradiction() {
        for (int x = 0; x < grid.getWidth(); ++x) {
            for (int y = 0; y < grid.getHeight(); ++y) {
                if (grid.getCell(x, y).isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean step() {
        if (status != Status.RUNNING) {
            return false;
        }

        if (checkContradiction()) {
            status = Status.CONTRADICTION;
            return true;
        }

        if (checkSolved()) {
            status = Status.SOLVED;
            return true;
        }

        Pos pos = pickMinEntropyCellRandomTie();

        if (pos == null) {
            status = Status.SOLVED;
            return true;
        }

        collapseAt(pos.x, pos.y);
        propagateFrom(pos.x, pos.y);

        if (checkContradiction()) {
            status = Status.CONTRADICTION;
        } else if (checkSolved()) {
            status = Status.SOLVED;
        }

        return true;
    }

    private void collapseAt(int x, int y) {
        if (!grid.inBounds(x, y)) {
            throw new IndexOutOfBoundsException("Coordinates out of bounds: (" + x + ", " + y + ")");
        }

        var cell = grid.getCell(x, y);
        BitSet domain = cell.asBitSetCopy();

        if (domain.isEmpty()) {
            throw new IllegalStateException("Cannot collapse an empty domain at (" + x + ", " + y + ")");
        }

        if (cell.isCollapsed()) {
            return;
        }

        int optionCount = domain.cardinality();
        int r = rng.nextInt(optionCount);

        int chosenTileId = -1;

        for (int bit = domain.nextSetBit(0); bit >= 0; bit = domain.nextSetBit(bit + 1)) {
            if (r == 0) {
                chosenTileId = bit;
                break;
            }
            r--;
        }

        if (chosenTileId < 0) {
            throw new IllegalStateException("Failed to select a tile at (" + x + ", " + y + ")");
        }

        BitSet oneHot = new BitSet(grid.getTileCount());
        oneHot.set(chosenTileId);

        cell.restrictTo(oneHot);
    }

    public void propagateFrom(int startX, int startY) {
        if (!grid.inBounds(startX, startY)) {
            throw new IndexOutOfBoundsException("Coordinates out of bounds: (" + startX + ", " + startY + ")");
        }

        ArrayDeque<Pos> queue = new ArrayDeque<>();

        boolean[][] inQueue = new boolean[grid.getWidth()][grid.getHeight()];

        queue.addLast(new Pos(startX, startY));
        inQueue[startX][startY] = true;

        BitSet support = new BitSet(ruleset.tileCount());

        while (!queue.isEmpty()) {
            Pos p = queue.removeFirst();
            inQueue[p.x][p.y] = false;

            var sourceCell = grid.getCell(p.x, p.y);
            BitSet sourceDomain = sourceCell.asBitSetCopy();

            if (sourceDomain.isEmpty()) {
                return;
            }

            for (var dir : Direction.values()) {
                int nx = p.x + dir.dx;
                int ny = p.y + dir.dy;

                if (!grid.inBounds(nx, ny)) {
                    continue;
                }

                var neighborCell = grid.getCell(nx, ny);

                if (neighborCell.isEmpty()) {
                    return;
                }

                support.clear();

                for (int t = sourceDomain.nextSetBit(0); t >= 0; t = sourceDomain.nextSetBit(t + 1)) {
                    support.or(ruleset.allowedMaskRef(t, dir));
                }

                boolean changed = neighborCell.restrictTo(support);

                if (neighborCell.isEmpty()) {
                    return;
                }

                if(changed && !inQueue[nx][ny]) {
                    queue.addLast(new Pos(nx, ny));
                    inQueue[nx][ny] = true;
                }
            }
        }
    }

    private Pos pickMinEntropyCellRandomTie() {
        int minEntropy = Integer.MAX_VALUE;
        List<Pos> candidates = new ArrayList<>();

        for (int x = 0; x < grid.getWidth(); ++x) {
            for (int y = 0; y < grid.getHeight(); ++y) {
                var cell = grid.getCell(x, y);
                int entropy = cell.optionsCount();

                if (entropy > 1) {
                    if (entropy < minEntropy) {
                        minEntropy = entropy;
                        candidates.clear();
                        candidates.add(new Pos(x, y));
                    } else if (entropy == minEntropy) {
                        candidates.add(new Pos(x, y));
                    }
                }
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        int r = rng.nextInt(candidates.size());
        return candidates.get(r);
    }
}
