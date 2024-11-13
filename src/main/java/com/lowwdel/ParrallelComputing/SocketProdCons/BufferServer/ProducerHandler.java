package com.lowwdel.ParrallelComputing.SocketProdCons.BufferServer;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ProducerHandler implements Runnable {
    private final Socket producerLink;
    private final BufferWithDB buffer;

    public ProducerHandler(Socket ProducerLink,BufferWithDB buffer) {
        this.producerLink = ProducerLink;
        this.buffer = buffer;
    }

    @Override
    public void run() {
        System.out.println("生产者处理线程启动");
        try (DataInputStream in = new DataInputStream(producerLink.getInputStream())) {
            while(true){

                Long product = in.readLong();
                buffer.put(product);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}