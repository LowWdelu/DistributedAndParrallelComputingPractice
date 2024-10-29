package com.lowwdel.ParrallelComputing.ProducerConsumer;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //输入生产者的个数和消费者的个数、数字总个数
        System.out.println("Please Enter the amount of the numbers you want to produce:");
        int AmountOfNumbers = scanner.nextInt();
        System.out.println("Please Enter the number of producers:");
        int producerNumber = scanner.nextInt();
        System.out.println("Please Enter the number of consumers:");
        int consumerNumber = scanner.nextInt();

        //创建生产者和消费者，并执行操作，join等待所有线程执行完毕
        Thread[] producers = new Thread[producerNumber];
        Thread[] consumers = new Thread[consumerNumber];
        //创建缓冲区
        Buffer buffer = new Buffer(10,AmountOfNumbers);
        for (int i = 0; i < producerNumber; i++) {
            producers[i] = new Thread(new Producer(buffer));
            producers[i].start();
        }
        for (int i = 0; i < consumerNumber; i++) {
            consumers[i] = new Thread(new Consumer(buffer));
            consumers[i].start();
        }
        for(Thread producer : producers){
            try {
                producer.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        for(Thread consumer : consumers){
            try {
                consumer.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
