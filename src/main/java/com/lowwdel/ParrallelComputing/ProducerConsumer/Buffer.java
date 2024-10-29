package com.lowwdel.ParrallelComputing.ProducerConsumer;

import java.util.LinkedList;
import java.util.Queue;

public class Buffer {
    private final int capacity;
    private final int totalAmount;
    private int producedCount = 0;
    private final Queue<Long> queue = new LinkedList<>();

    public Buffer(int capacity , int totalAmount){
        this.totalAmount = totalAmount;
        this.capacity = capacity;
    }

    public int getProducedCount(){
        return producedCount;
    }
    public int getTotalAmount(){
        return totalAmount;
    }

    public synchronized void put(Long number) throws InterruptedException {
        while(queue.size() == capacity ){
            wait();
        }
        if(producedCount < totalAmount){
            queue.offer(number);
            producedCount++;
//            System.out.println("Produced: " + producedCount);
            notifyAll();
        }
    }

    public synchronized Long take(){
        while(queue.isEmpty()&& !isProductionCompleted()){
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        Long number = queue.poll();
        notifyAll();
        return number;
    }

    public boolean isProductionCompleted(){
        return producedCount >= totalAmount;
    }
}
