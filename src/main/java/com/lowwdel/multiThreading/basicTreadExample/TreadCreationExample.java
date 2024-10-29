package com.lowwdel.multiThreading.basicTreadExample;

public class TreadCreationExample extends Thread{
    @Override
    public void run() {
        System.out.println("Thread1 is running in a loop");
    }

    public static void main(String[] args) {
        int n = 10;
        for(int i=0;i<n;i++){
            TreadCreationExample thread = new TreadCreationExample();
            thread.start();
        }
        Thread t2 = new Thread(new Runnable(){
            @Override
            public void run() {
                System.out.println("Thread2 is created by Passing an anonymous implementation class as a parameter");
            }
        });
        t2.start();
    }
}
