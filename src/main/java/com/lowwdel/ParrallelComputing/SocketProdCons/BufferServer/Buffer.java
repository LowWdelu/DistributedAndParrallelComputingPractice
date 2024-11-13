package com.lowwdel.ParrallelComputing.SocketProdCons.BufferServer;

import java.util.LinkedList;
import java.util.Queue;

public class Buffer {
        private final int capacity;
        private final Queue<Long> queue = new LinkedList<>();
        public Buffer(int capacity){
            this.capacity = capacity;
        }

        public synchronized void put(Long number) throws InterruptedException {
            while(queue.size() == capacity){
                wait();
                System.out.println("队列满了");
            }
            queue.offer(number);
            notifyAll();
        }

        public synchronized Long take() throws InterruptedException {
            while(queue.isEmpty()){
                wait();
                System.out.println("队列空了");
            }
            Long number = queue.poll();
            notifyAll();
            return number;
        }
}
