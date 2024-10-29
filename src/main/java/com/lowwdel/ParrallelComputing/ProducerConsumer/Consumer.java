package com.lowwdel.ParrallelComputing.ProducerConsumer;

import java.util.concurrent.atomic.AtomicInteger;

public class Consumer implements Runnable{
    private Buffer buffer;
    private Long numberToProcess;
//    private static final AtomicInteger count = new AtomicInteger(0);

    public Consumer(Buffer buffer){
        this.buffer = buffer;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
                numberToProcess = buffer.take(); // 从缓冲区获取数据
                if (numberToProcess == null)break;
                isPrime(numberToProcess);
//                incementCount();
        }
    }

    private void isPrime(long number){
        boolean isPrime = true;
        for (long i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) {
                isPrime = false;
                break;
            }
        }
//        System.out.println(number + (isPrime? " is prime" : " is not prime"));
    }

//    private synchronized void incementCount(){
//        count.incrementAndGet();
//        System.out.println("已经处理了" + count + "个数字");
//    }

}
