package com.lowwdel.multiThreading.gameOfLife.utils;

public class ArrayComparator {
    public static boolean compare(byte[][] arr1, byte[][] arr2) {
        if (arr1.length!= arr2.length) {
            return false;
        }

        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i].length != arr2[i].length) {
                return false;
            }

            for (int j = 0; j < arr1[i].length; j++) {
                if (arr1[i][j] != arr2[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }
}
