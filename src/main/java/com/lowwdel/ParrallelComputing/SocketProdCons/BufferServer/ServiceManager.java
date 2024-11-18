package com.lowwdel.ParrallelComputing.SocketProdCons.BufferServer;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Provider;
import java.util.concurrent.ExecutorService;

public class ServiceManager {
    private final BufferWithDB buffer;
    private final ExecutorService threadPool;
    private ConsumerRegistry consumerRegistry;
    private final int producerPort;
    private final int consumerPort;
    public ServiceManager(BufferWithDB buffer, ExecutorService threadPool,ConsumerRegistry consumerRegistry, int producerPort, int consumerPort) {
        this.buffer = buffer;
        this.threadPool = threadPool;
        this.consumerRegistry = consumerRegistry;
        this.producerPort = producerPort;
        this.consumerPort = consumerPort;
    }

    public void startService(){
        startProducerListener();
        startConsumerListener();
        startMessageServer();
    }

    private void startProducerListener(){
        new Thread(() -> {
            try{
                ServerSocket producerSocket = new ServerSocket(producerPort);
                System.out.println("生产者监听器已启动，等待客户端链接");

                // 循环监听Socket请求
                while(!producerSocket.isClosed()){
                    try {
                        Socket producerLink = producerSocket.accept();
                        System.out.println("生产者已连接");
                        threadPool.execute(new ProducerHandler(producerLink,buffer));
                    } catch (IOException e) {
                        if(producerSocket.isClosed()){
                            System.out.println("生产者Socket已关闭");
                        }else{
                            System.out.println("生产者监听线程异常");
                        }
                    }
                }
            }catch (IOException e){
                System.out.println("无法启动生产者监听线程");
            }
        }).start();
    }

    private void startConsumerListener(){
        new Thread(() -> {
            try {
                ServerSocket consumerSocket = new ServerSocket(consumerPort);
                System.out.println("消费者监听线程启动");
                while(!consumerSocket.isClosed()){
                    try {
                        Socket consumerLink = consumerSocket.accept();
                        System.out.println("消费者已连接");
                        threadPool.execute(new ConsumerHandler(consumerLink,consumerRegistry));
                    } catch (IOException e) {
                        if(consumerSocket.isClosed()){
                            System.out.println("消费者Socket已关闭");
                        }else{
                            System.out.println("消费者监听线程异常");
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("无法启动消费者线程");
            }
        }).start();
    }

    private void startMessageServer(){
        // 启动消息服务器线程，针对消费者端进行消息tag的比对和广播
        new Thread(() -> {
            Message message = null;
            try {
                // 启动消息服务器
                System.out.println("消息服务器已启动");
                while(true){
                    Thread.sleep(1000);
                    //【待完善】应当分不同的tag，进行判断有无对应消费者
                    if(consumerRegistry.isConsuming()){
                        message = buffer.takeWithTag();
                        if(message != null){
                            for(ConsumerHandler consumerHandler : consumerRegistry.getConsumersByTag(message.tag)){
                                consumerHandler.send(message);
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("消息服务器线程被中断");
            }
        }).start();
    }
}

