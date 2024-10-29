package com.lowwdel.multiThreading.gameOfLife.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ArrayGenerator {
    public static byte[][] generateArray(){
        byte[][] arr = new byte[1000][1000];

        double randomValue = Math.random();

        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 1000; j++) {
                randomValue = Math.random();
                arr[i][j] = (randomValue < 0.5) ?(byte) 0 :(byte) 1;
            }

        }

        return arr;
    }

    public static byte[][] generateCustomSizeArray(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter the size of the array:");
        int size = sc.nextInt();

        byte[][] arr = new byte[size][size];

        double randomValue = Math.random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                randomValue = Math.random();
                arr[i][j] = (randomValue < 0.5)?(byte) 0 :(byte) 1;
            }
        }
        return arr;
    }
    public static byte[][] generateArrayFromFile(String filePath) throws IOException {
        List<Byte> arr = new ArrayList<>();

        FileReader fileReader = new FileReader(filePath);
        BufferedReader reader = new BufferedReader(fileReader);

        String dataStream = reader.readLine();
        if(dataStream != null){
            for(char c : dataStream.toCharArray()){
                arr.add((byte) Character.getNumericValue(c));
            }
        }
        reader.close();

        //默认是整数平方的，所以取平方根
        int size = (int) Math.sqrt(arr.size());

        byte[][] result = new byte[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                result[i][j] = arr.get(i*size + j);
            }
        }
        return result;
    }

}
