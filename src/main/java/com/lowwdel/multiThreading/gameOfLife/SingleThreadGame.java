package com.lowwdel.multiThreading.gameOfLife;

public class SingleThreadGame {
    /*
     * 思路一，方法：处理每个细胞（细胞、下标） ，遂发现该方法每次传参需要传整个棋盘，放弃
     * 转思路二，方法：处理每个细胞（细胞周围邻居的九个元素二维数组）
     * */

    public static byte[][] processBoardWithNeighbors(byte[][] board) {
        byte[][] newBoard = new byte[board.length][board[0].length];

        byte[][] neighbors = new byte[3][3];

        byte currentCell;
        byte newCell;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                currentCell = board[i][j];
                // 填充 neighbors 数组
                neighbors[1][1] = currentCell;

                // 处理上边界
                if (i == 0) {
                    neighbors[0][0] = 0;
                    neighbors[0][1] = 0;
                    neighbors[0][2] = 0;
                } else {
                    neighbors[0][1] = board[i - 1][j];
                    neighbors[0][0] = (j > 0) ? board[i - 1][j - 1] : 0;
                    neighbors[0][2] = (j < board.length - 1) ? board[i - 1][j + 1] : 0;
                }

                // 处理左边界
                if (j == 0) {
                    neighbors[0][0] = 0;
                    neighbors[1][0] = 0;
                    neighbors[2][0] = 0;
                } else {
                    neighbors[1][0] = board[i][j - 1];
                    neighbors[0][0] = (i > 0) ? board[i - 1][j - 1] : 0;
                    neighbors[2][0] = (i < board.length - 1) ? board[i + 1][j - 1] : 0;
                }

                // 处理右边界
                if (j == board.length - 1) {
                    neighbors[0][2] = 0;
                    neighbors[1][2] = 0;
                    neighbors[2][2] = 0;
                } else {
                    neighbors[1][2] = board[i][j + 1];
                    neighbors[0][2] = (i > 0) ? board[i - 1][j + 1] : 0;
                    neighbors[2][2] = (i < board.length - 1) ? board[i + 1][j + 1] : 0;
                }

                // 处理下边界
                if (i == board.length - 1) {
                    neighbors[2][0] = 0;
                    neighbors[2][1] = 0;
                    neighbors[2][2] = 0;
                } else {
                    neighbors[2][1] = board[i + 1][j];
                    neighbors[2][0] = (j > 0) ? board[i + 1][j - 1] : 0;
                    neighbors[2][2] = (j < board.length - 1) ? board[i + 1][j + 1] : 0;
                }

                newCell = processCellWithNeighbors(neighbors);

                newBoard[i][j] = newCell;
            }

        }
        return newBoard;
    }

    public static byte processCellWithNeighbors(byte[][] neighbors) {
        /*如果一个细胞周围有3个细胞为生，则该细胞为生（即该细胞若原先为死，则转为生，若原先为生，则保持不变） 。
        如果一个细胞周围有2个细胞为生，则该细胞的生死状态保持不变；
        在其它情况下，该细胞为死（即该细胞若原先为生，则转为死，若原先为死，则保持不变）*/

        int liveNeighbors = 0;

        for (int i = 0; i < neighbors.length; i++) {
            for (int j = 0; j < neighbors[i].length; j++) {
                if(i == 1 && j == 1)continue;
                if (neighbors[i][j] == 1) {
                    liveNeighbors++;
                }
            }
        }

        if (liveNeighbors == 3) return 1;
        if (liveNeighbors == 2) return neighbors[1][1];
        return 0;
    }

//    public static byte[][] processBoardUseGlobalBoard(byte[][] board) {
//        byte[][] newboard=;
//        return newboard;
//    }

}