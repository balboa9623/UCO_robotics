//package com.baeldung.algorithms.maze.solver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Maze {
    private static final int ROAD = 0;
    private static final int WALL = 1;
    private static final int START = 2;
    private static final int EXIT = 4;
    private static final int PATH = 3;

    protected int[][] maze;
    private boolean[][] visited;
    private Coordinate start;
    private Coordinate end;

    public Maze(int[][] maze) throws FileNotFoundException {
//        String fileText = "";
//        try (Scanner input = new Scanner(maze)) {
//            while (input.hasNextLine()) {
//                fileText += input.nextLine() + "\n";
//            }
//        }
        initializeMaze(maze);
    }

    private void initializeMaze(int[][] localMaze) {
//        if (text == null || (text = text.trim()).length() == 0) {
//            throw new IllegalArgumentException("empty lines data");
//        }
//
//        String[] lines = text.split("[\r]?\n");
        maze = localMaze;
        visited = new boolean[localMaze.length][localMaze[0].length];

        for (int row = 0; row < getHeight(); row++) {
//            if (maze[row].length() != getWidth()) {
//                throw new IllegalArgumentException("line " + (row + 1) + " wrong length (was " + lines[row].length() + " but should be " + getWidth() + ")");
//            }

            for (int col = 0; col < getWidth(); col++) {
                if (maze[row][col] == 1)
                    maze[row][col] = WALL;
                else if (maze[row][col] == 2) {
                    maze[row][col] = START;
                    start = new Coordinate(row, col);
                } else if (maze[row][col] == 4) {
                    maze[row][col] = EXIT;
                    end = new Coordinate(row, col);
                } else
                    maze[row][col] = ROAD;
            }
        }
    }

    public int getHeight() {
        return maze.length;
    }

    public int getWidth() {
        return maze[0].length;
    }

    public Coordinate getEntry() {
        return start;
    }

    public Coordinate getExit() {
        return end;
    }

    public boolean isExit(int x, int y) {
        return x == end.getX() && y == end.getY();
    }

    public boolean isStart(int x, int y) {
        return x == start.getX() && y == start.getY();
    }

    public boolean isExplored(int row, int col) {
        return visited[row][col];
    }

    public boolean isWall(int row, int col) {
        return maze[row][col] == WALL;
    }

    public void setVisited(int row, int col, boolean value) {
        visited[row][col] = value;
    }

    public boolean isValidLocation(int row, int col) {
        if (row < 0 || row >= getHeight() || col < 0 || col >= getWidth()) {
            return false;
        }
        return true;
    }
    
    public int[][] makePath(List<Coordinate> path) {
        int[][] tempMaze = maze;
        for (Coordinate coordinate : path) {
            if (isStart(coordinate.getX(), coordinate.getY()) || isExit(coordinate.getX(), coordinate.getY())) {
                continue;
            }
            tempMaze[coordinate.getX()][coordinate.getY()] = PATH;
        }
//        System.out.println(toString(tempMaze));
        
        return tempMaze;
    }

    public void printPath(List<Coordinate> path) {
        int[][] tempMaze =maze;
        for (Coordinate coordinate : path) {
            if (isStart(coordinate.getX(), coordinate.getY()) || isExit(coordinate.getX(), coordinate.getY())) {
                continue;
            }
            tempMaze[coordinate.getX()][coordinate.getY()] = PATH;
        }
        System.out.println(toString(tempMaze));
    }

    public String toString(int[][] maze) {
        StringBuilder result = new StringBuilder(getWidth() * (getHeight() + 1));
        for (int row = 0; row < getHeight(); row++) {
            for (int col = 0; col < getWidth(); col++) {
                if (maze[row][col] == ROAD) {
                    result.append('0');
                } else if (maze[row][col] == WALL) {
                    result.append('1');
                } else if (maze[row][col] == START) {
                    result.append('0');
                } else if (maze[row][col] == EXIT) {
                    result.append('3');
                } else {
                    result.append('3');
                }
            }
            result.append('\n');
        }
        return result.toString();
    }

    public void reset() {
        for (int i = 0; i < visited.length; i++)
            Arrays.fill(visited[i], false);
    }
}