package com.lowwdel.ParrallelComputing.ProducerConsumer;

import java.util.Random;
class Producer implements Runnable{
    //v1.0：随机生成一个大于20亿的正整数
    private Buffer buffer;
    private Random random = new Random();

    public Producer(Buffer buffer){
        this.buffer = buffer;
    }

    @Override
    public void run(){
        while (true) {
            long number = generateNumber();
            try {
                buffer.put(number);
                if(buffer.isProductionCompleted()){
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
//        System.out.println("Production Completed"+buffer.getProducedCount() + " numbers is produced");
    }

    private long generateNumber(){
        return 2000000000L + random.nextInt(Integer.MAX_VALUE - 2000000000);
    }
}
