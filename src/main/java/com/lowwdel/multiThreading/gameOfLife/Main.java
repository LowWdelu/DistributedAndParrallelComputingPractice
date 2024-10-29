package com.lowwdel.multiThreading.gameOfLife;

import com.lowwdel.multiThreading.gameOfLife.utils.ArrayGenerator;
import com.lowwdel.multiThreading.gameOfLife.utils.ViewArray;

import java.io.IOException;
import java.util.Scanner;

import static com.lowwdel.multiThreading.gameOfLife.utils.ViewArray.viewArray;

public class Main {

    public static void main(String[] args) throws IOException {
        /*单线程测试棋盘的更新
        byte[][] board = generateCustomSizeArray();
        viewArray(board);
        System.out.println("this is newboard");
        byte[][] newboard = processBoardWithNeighbors(board);
        viewArray(newboard);
         */
        //生命游戏最终版：自定义棋盘的大小、线程数、迭代次数
        byte[][] board = ArrayGenerator.generateCustomSizeArray();
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter the number of threads:");
        int numberOfThreads = sc.nextInt();
        System.out.println("Please enter the number of iterations:");
        int iterationCount = sc.nextInt();

        ViewArray.viewArray(board);
        MultiThreadGame multiThreadGame = new MultiThreadGame(board, numberOfThreads, iterationCount);
        multiThreadGame.startGame();

        viewArray(board);
        /*从测试用例中读取数组并比较
        byte[][] sourceArray = ArrayGenerator.generateArrayFromFile("src/main/resources/1.init.txt");
        byte[][] targetArray = ArrayGenerator.generateArrayFromFile("src/main/resources/1.end.txt");

        SynchronizedBoard synchronizedBoard = new SynchronizedBoard(sourceArray,numberOfThreads);
        MultiThreadGame multiThreadGame = new MultiThreadGame(synchronizedBoard.getBoard(), numberOfThreads, iterationCount);
        multiThreadGame.startGame();
        boolean isAchieved = ArrayComparator.compare(synchronizedBoard.getBoard(),targetArray);
        System.out.println(isAchieved);
        */

//        SynchronizedBoard synchronizedBoard = new SynchronizedBoard(ArrayGenerator.generateMinorArray(),numberOfThreads);
//        MultiThreadGame multiThreadGame = new MultiThreadGame(synchronizedBoard.getBoard(), numberOfThreads);
//        multiThreadGame.startGame();
    }
}
