package com.lowwdel.ParrallelComputing.SocketProdCons;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConsumerHandler implements Runnable{
    private final Socket consummerLink;
    private final Buffer buffer;
    private  int consumedAmount = 0;
    public ConsumerHandler(Socket consumerLink,Buffer buffer){
        this.buffer = buffer;
        this.consummerLink = consumerLink;
    }

    @Override
    public void run(){
        try {
            DataOutputStream out = new DataOutputStream(consummerLink.getOutputStream());
            Long product ;
            while (true){
                consumedAmount++;

                product = buffer.take();
                out.writeLong(product);
                out.flush();

                if(consumedAmount % 10 == 0){
                    System.out.println("已经消耗"+consumedAmount+"个");
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
