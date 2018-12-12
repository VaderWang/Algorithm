package leetcode;

class Solution200 {

    private int d[][] = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    private boolean[][] visited;
    private int m, n;

    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0)
            return 0;
        m = grid.length;
        n = grid[0].length;
        visited = new boolean[grid.length][grid[0].length];
        int numberOfIslands = 0;
        for (int i = 0; i < grid.length; i ++) {
            for (int j = 0; j < grid[0].length; j ++) {
                if (!visited[i][j] && grid[i][j] == '1') {
                    dfs(grid, i, j);
                    numberOfIslands ++;
                }
            }
        }
        return numberOfIslands;
    }

    private void dfs(char[][] grid, int startX, int startY) {
        visited[startX][startY] = true;
        for (int i = 0; i < 4; i ++) {
            int newX = startX + d[i][0];
            int newY = startY + d[i][1];
            if (inArea(newX, newY) && !visited[newX][newY] && grid[newX][newY] == '1') {
                dfs(grid, newX, newY);
            }

        }
    }

    private boolean inArea(int x, int y) {
        return x >= 0 && x < m && y >= 0 && y < n;
    }

    public static void main(String[] args) {
        char grid1[][] = {
                {'1','1','1','1','0'},
                {'1','1','0','1','0'},
                {'1','1','0','0','0'},
                {'0','0','0','0','0'}
        };
        System.out.println((new Solution200()).numIslands(grid1));
        //1
        char grid2[][] = {
                {'1','1','0','0','0'},
                {'1','1','0','0','0'},
                {'0','0','1','0','0'},
                {'0','0','0','1','1'}
        };
        System.out.println((new Solution200()).numIslands(grid2));
        //3
    }
}
