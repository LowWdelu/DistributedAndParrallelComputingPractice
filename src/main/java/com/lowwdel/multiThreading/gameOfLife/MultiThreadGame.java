package com.lowwdel.multiThreading.gameOfLife;

import com.lowwdel.multiThreading.gameOfLife.utils.SynchronizedBoard;
import com.lowwdel.multiThreading.gameOfLife.utils.ViewArray;

import java.util.concurrent.*;

public class MultiThreadGame {
    private SynchronizedBoard synchronizedBoard;
    private byte[][] newBoard;
    private int numberOfThreads;

    private int iterationCount;

    private ExecutorService threadPool;

    public MultiThreadGame(byte[][] board , int numberOfThreads , int iterationCount) {
        this.synchronizedBoard = new SynchronizedBoard(board,numberOfThreads);
        this.numberOfThreads = numberOfThreads;
        this.iterationCount = iterationCount;
        //根据需求创建线程池
        this.threadPool = Executors.newFixedThreadPool(numberOfThreads);
    }

    public void startGame(){//for循环分割棋盘并启动每块的线程
        System.out.println("Game is starting");

        //储存每轮结果的Future数组
        Future<?>[] futures = new Future<?>[numberOfThreads];

        for (int round = 0; round < iterationCount; round++) {//round回合数，每一轮迭代都要更新棋盘
            System.out.println("Round " + (round + 1));

            try {
                runOneIteration();
            }catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        threadPool.shutdown();//所有轮次结束后关闭线程池
    }

    private void runOneIteration() throws InterruptedException, ExecutionException{//对棋盘的每一轮迭代
        int boardSize = synchronizedBoard.getBoard().length;
        int rowsPerThread = boardSize / numberOfThreads;

        int startRow = 0;
        int endRow = 0;

        @SuppressWarnings("unchecked")
        Future<byte[][]>[] futures =(Future<byte[][]>[]) new Future[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {//给每个线程分配块，并在线程池中启动
            //从0开始，每i个线程分配一块，每块的核心行是i*numberOfThreads
            startRow = i*rowsPerThread;
            /*考虑到可能有棋盘行数board.length不能将numberOfThread整除的情况，
             *这种情况下，最后一块与其他线程分得的不一样长，因此需要单独考虑最后一行
             */
            endRow = (i == numberOfThreads - 1)? boardSize - 1 : startRow + rowsPerThread - 1;

            byte[][] block = getBlockWithGhostRows(startRow,endRow);

            futures[i] = threadPool.submit(new GameOfLifeThread(block, startRow, endRow));
        }
        for (int i = 0; i < numberOfThreads; i++) {

            startRow = i*rowsPerThread;
            endRow = (i == numberOfThreads - 1)? boardSize - 1 : startRow + rowsPerThread - 1;
            byte[][] resultBlock = futures[i].get();//获取每个线程的结果块
            synchronizedBoard.updatePartialBoard(startRow,endRow,resultBlock);

        }
    }
    private byte[][] getBlockWithGhostRows(int startRow, int endRow){
        int boardWidth = synchronizedBoard.getBoard().length;

//        System.out.println("starRow:"+startRow+"endRow:"+endRow);

        int blockHeight = endRow - startRow + 1;
        boolean isFirstRow = (startRow == 0);
        boolean isLastRow = (endRow == boardWidth - 1);

        byte[][] block = new byte[blockHeight + 2][boardWidth];

        for (int i = -1; i <= blockHeight; i++) {
            int sourceRow = startRow + i;//原棋盘上的行索引
            int targetRow = i + 1;        //block中的行索引

            if(i == -1){//上边界
                if(isFirstRow){
                    //如果是第一行，将上边界设为0
                    for (int j = 0; j < boardWidth; j++) {
                        block[0][j] = 0;
                    }
                }else{
                    //如果不是第一行，将上一块的最后一行作为幽灵行
                    System.arraycopy(synchronizedBoard.getBoard()[sourceRow], 0, block[0], 0, boardWidth);
                }
            }else if(i == blockHeight){//下边界
                if(isLastRow){
                    //如果是棋盘的下边界行，将下边界设为0
                    for (int j = 0; j < boardWidth; j++) {
                        block[blockHeight + 1][j] = 0;
                    }
                }else{
                    //如果不是棋盘的下边界行，将下一块的第一行作为幽灵行
                    System.arraycopy(synchronizedBoard.getBoard()[sourceRow], 0, block[blockHeight + 1], 0, boardWidth);
                }
            }else{
                //复制核心行
                System.arraycopy(synchronizedBoard.getBoard()[sourceRow], 0, block[targetRow], 0, boardWidth);
            }
        }

//        synchronized (System.out) {
//            System.out.println("这是第 " + startRow + " 个线程处理的块:(GetBlock)");
//            synchronizedBoard.printBoard(block);
//        }

        return block;
    }

    //每个处理棋盘块的线程，采用幽灵行，
    class GameOfLifeThread implements Callable<byte[][]>{
        private byte[][] blockWithGhostRows;
        private byte[][] resultBlock;
        private int startRow , endRow;

        public GameOfLifeThread(byte[][] block, int startRow, int endRow) {
            this.blockWithGhostRows = block;
            this.resultBlock = new byte[block.length][block[0].length];
            this.startRow = startRow;
            this.endRow = endRow;
        }

        @Override
        public byte[][] call() throws Exception{
            //处理每个块的核心行
            for (int i = 1; i <= (endRow - startRow + 1); i++) {
                for (int j = 0; j < blockWithGhostRows[0].length; j++) {
                    resultBlock[i][j] = processCell(i,j);
                }
            }

//            synchronized (System.out) {
//                System.out.println("这是第 " + startRow + " 个线程处理以后的块:");
//                synchronizedBoard.printBoard(blockWithGhostRows);
//            }

            //应该在线程外更新棋盘，而不是在线程内更新，因为多线程会导致数据不一致
//            synchronizedBoard.updatePartialBoard(startRow,endRow,resultBlock);
            return resultBlock;
        }
        private byte processCell(int i, int j){
            byte liveNeighbors = countLiveNeighbors(i,j);
            byte currentCell = blockWithGhostRows[i][j];

            if (liveNeighbors == 3) return 1;
            if (liveNeighbors == 2) return currentCell;
            return 0 ;
        }

        private byte countLiveNeighbors(int i, int j){
            byte count = 0;
            for (int k = -1; k <= 1 ; k++) {
                for (int l = -1; l <= 1; l++) {
                    if(k == 0 && l == 0)continue;//不计算自身
                    //如果是左边界，列号j=0,则k=-1时，j+k<0,要求j+k>= 0 才参与计算

                    int neighborRow = i + k;//邻居的行号（9*9的邻居）
                    int neighborCol = j + l;//邻居的列号（9*9的邻居）

                    if (neighborCol >= 0 && neighborCol < blockWithGhostRows[0].length){
                        count += blockWithGhostRows[neighborRow][neighborCol];
                    }

                }
            }
            return count;
        }
    }
}
