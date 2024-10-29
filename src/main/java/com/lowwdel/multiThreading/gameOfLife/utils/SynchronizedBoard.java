package com.lowwdel.multiThreading.gameOfLife.utils;

import static com.lowwdel.multiThreading.gameOfLife.utils.ViewArray.viewArray;

public class SynchronizedBoard {
    private byte[][] board;
    private int totalThreads;
    public SynchronizedBoard(byte[][] board , int totalThreads){
        this.board = board;
        this.totalThreads = totalThreads;
    }
    public synchronized byte[][] getBoard(){
        return board;
    }

    public synchronized void updatePartialBoard(int startRow, int endRow, byte[][] block){
        for (int i = startRow; i <= endRow; i++) {
            System.arraycopy(block[i-startRow+1],0,board[i],0,board.length);
        }
    }

}
