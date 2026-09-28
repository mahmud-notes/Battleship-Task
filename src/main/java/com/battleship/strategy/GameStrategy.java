package com.battleship.strategy;

import com.battleship.utils.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GameStrategy {

    private static final int SIZE = Settings.BOARD_SIZE;
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    private final CellState[][] board = new CellState[SIZE][SIZE];
    private final List<Cell> currentHits = new ArrayList<>();
    private final Random random = new Random();

    public GameStrategy() {
        for (CellState[] row : board) {
            Arrays.fill(row, CellState.UNKNOWN);
        }
    }

    public Cell nextMove() {
        if (!currentHits.isEmpty()) {
            Cell target = currentHits.size() == 1
                    ? probeAround(currentHits.get(0))
                    : continueLine();
            if (target != null) {
                return target;
            }
            finishShip();
        }
        return hunt();
    }

    public void registerResult(Cell cell, ShotResult result) {
        if (result == ShotResult.MISS) {
            board[cell.row()][cell.col()] = CellState.MISS;
            return;
        }
        board[cell.row()][cell.col()] = CellState.HIT;
        currentHits.add(cell);
        markDiagonals(cell);
    }

    private Cell hunt() {
        List<Cell> candidates = new ArrayList<>();
        List<Cell> parityCells = new ArrayList<>();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] == CellState.UNKNOWN) {
                    candidates.add(new Cell(r, c));
                    if ((r + c) % 2 == 0) {
                        parityCells.add(new Cell(r, c));
                    }
                }
            }
        }
        List<Cell> pool = parityCells.isEmpty() ? candidates : parityCells;
        return pool.get(random.nextInt(pool.size()));
    }

    private Cell probeAround(Cell hit) {
        List<int[]> dirs = new ArrayList<>(Arrays.asList(DIRECTIONS));
        Collections.shuffle(dirs, random);
        for (int[] d : dirs) {
            Cell neighbour = new Cell(hit.row() + d[0], hit.col() + d[1]);
            if (isUnknown(neighbour)) {
                return neighbour;
            }
        }
        return null;
    }

    private Cell continueLine() {
        boolean horizontal = currentHits.get(0).row() == currentHits.get(1).row();
        int dr = horizontal ? 0 : 1;
        int dc = horizontal ? 1 : 0;

        Comparator<Cell> byPosition = Comparator.comparingInt(c -> c.row() + c.col());
        Cell start = currentHits.stream().min(byPosition).orElseThrow();
        Cell end = currentHits.stream().max(byPosition).orElseThrow();

        Cell afterEnd = new Cell(end.row() + dr, end.col() + dc);
        if (isUnknown(afterEnd)) {
            return afterEnd;
        }
        Cell beforeStart = new Cell(start.row() - dr, start.col() - dc);
        if (isUnknown(beforeStart)) {
            return beforeStart;
        }
        return null;
    }

    private void finishShip() {
        for (Cell hit : currentHits) {
            board[hit.row()][hit.col()] = CellState.SUNK;
            markAround(hit, true);
        }
        currentHits.clear();
    }

    private void markDiagonals(Cell cell) {
        markAround(cell, false);
    }

    private void markAround(Cell cell, boolean includeSides) {
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                boolean diagonal = dr != 0 && dc != 0;
                if (!diagonal && !includeSides) {
                    continue;
                }
                Cell near = new Cell(cell.row() + dr, cell.col() + dc);
                if (isUnknown(near)) {
                    board[near.row()][near.col()] = CellState.MISS;
                }
            }
        }
    }

    private boolean isUnknown(Cell cell) {
        return cell.row() >= 0 && cell.row() < SIZE
                && cell.col() >= 0 && cell.col() < SIZE
                && board[cell.row()][cell.col()] == CellState.UNKNOWN;
    }

    public void markUnknownCell(Cell cell) {
        if (isUnknown(cell)) {
            board[cell.row()][cell.col()] = CellState.MISS;
        }
    }
}