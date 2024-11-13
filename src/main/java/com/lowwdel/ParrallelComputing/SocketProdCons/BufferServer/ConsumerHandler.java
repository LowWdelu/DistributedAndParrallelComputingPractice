package com.lowwdel.ParrallelComputing.SocketProdCons.BufferServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ConsumerHandler implements Runnable{
    private final Socket consumerLink;
    private final BufferWithDB buffer;
    private  int consumedAmount = 0;
    public ConsumerHandler(Socket consumerLink,BufferWithDB buffer){
        this.buffer = buffer;
        this.consumerLink = consumerLink;
    }

    @Override
    public void run(){
        try {
            DataOutputStream out = new DataOutputStream(consumerLink.getOutputStream());
            DataInputStream in = new DataInputStream(consumerLink.getInputStream());

            int batchSize = 1;
            //如果有数据输入，则进行批量消息拉取，将batchSize设为需要一次性推送的数据量
            if(in.available() > 0){
                batchSize = in.readInt();
            }
            Long product = null;
            while (true){
                for (int i = 0; i < batchSize; i++) {
                    product = buffer.take();

                    //如果缓冲区为空，等待数据
                    if(product == null){
                        System.out.println("缓冲区为空，等待数据");
                        synchronized (buffer) {
                            try {
                                buffer.wait(1000);
                            } catch (InterruptedException e) {
                                System.out.println("取数据线程被中断，退出");
                                break;
                            }
                        }
                        continue;
                    }
                    //成功取到数据，将数据发送给消费者
                    consumedAmount++;
                    out.writeLong(product);
                }
                out.flush();
                if(consumedAmount % 10 == 0){
                    System.out.println("已经消耗"+consumedAmount+"个");
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
