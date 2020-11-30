//package com.baeldung.algorithms.maze.solver;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MazeDriver {
    public static void main(String[] args) throws Exception {
        int[][]maze1 = {{4,0,0,0,0,0,0,0},
        				{1,0,1,0,0,0,0,0},
        				{1,0,1,0,0,0,0,0},
        				{1,0,1,0,0,0,0,2}};
        //File maze2 = new File("src/main/resources/maze/maze2.txt");

       int[][] temp = execute(maze1);
       
       System.out.println(Arrays.deepToString(temp));
        
        
       // execute(maze2);
    }

    static int[][] execute(int[][] file) throws Exception {
        Maze maze = new Maze(file);
       // dfs(maze);
        return bfs(maze);
    }

    public static int[][] bfs(Maze maze) {
        BFSMazeSolver bfs = new BFSMazeSolver();
        List<Coordinate> path = bfs.solve(maze);
        maze.printPath(path);
        int [][] copy = maze.makePath(path);
        maze.reset();
        return copy;
       
       
    }

    private static void dfs(Maze maze) {
        DFSMazeSolver dfs = new DFSMazeSolver();
        List<Coordinate> path = dfs.solve(maze);
        maze.printPath(path);
        maze.reset();
    }
}
