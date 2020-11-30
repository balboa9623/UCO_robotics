import java.util.Arrays;

public class MazeSolver{
    final static int TRIED = 2;
    final static int PATH = 3;
    int endX;
    int endY;

    // @formatter:off
   
    // @formatter:off

//    public static void main(String[] args) {
//    	int[][] map = new int[30][30];
//    	for (int i = 0; i < map.length; i++) {
//			for (int j = 0; j < map[i].length; j++) {
//				map[i][j] = 0;
//			}
//		}
//        MazeSolver maze = new MazeSolver(map);
//        boolean solved = maze.solve(29,29,0,0);
//        System.out.println("Solved: " + solved);
//        System.out.println(maze.toString());
//    }

    public int[][] grid;
    private int height;
    private int width;

    int[][] map;

    public MazeSolver(int[][] grid) {
        this.grid = grid;
        this.height = grid.length;
        this.width = grid[0].length;

        this.map = new int[height][width];
    }

    public boolean solve(int x, int y,int endXL, int endYL) {
        this.endX=endXL;
        this.endY=endYL;
        return traverse(x,y);
    }

    private boolean traverse(int i, int j) {
        if (!isValid(i,j)) {
            return false;
        }

        if ( isEnd(i, j) ) {
            map[i][j] = PATH;
            return true;
        } else {
            map[i][j] = TRIED;
        }

        // North
        if (traverse(i - 1, j)) {
            map[i-1][j] = PATH;
            return true;
        }
        // East
        else if (traverse(i, j + 1)) {
            map[i][j + 1] = PATH;
            return true;
        }
        // South
        else if (traverse(i + 1, j)) {
            map[i + 1][j] = PATH;
            return true;
        }
        // West
        else if (traverse(i, j - 1)) {
            map[i][j - 1] = PATH;
            return true;
        }

        return false;
    }

    private boolean isEnd(int i, int j) {
        return i == endX && j == endY;
    }

    private boolean isValid(int i, int j) {
        if (inRange(i, j) && isOpen(i, j) && !isTried(i, j)) {
            return true;
        }
        else {
        	return false;
        }
    }

    private boolean isOpen(int i, int j) {
        return grid[i][j] == 0;
    }

    private boolean isTried(int i, int j) {
        return map[i][j] == TRIED;
    }

    private boolean inRange(int i, int j) {
        return inHeight(i) && inWidth(j);
    }

    private boolean inHeight(int i) {
        return i >= 0 && i < height;
    }

    private boolean inWidth(int j) {
        return j >= 0 && j < width;
    }

    public String toString() {
        String s = "";
        for (int[] row : map) {
            s += Arrays.toString(row) + "\n";
        }

        return s;
    }

}

