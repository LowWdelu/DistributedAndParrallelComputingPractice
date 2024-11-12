package com.lowwdel.ParrallelComputing.SocketProdCons;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*目标：
（1）在生产者-消费者模型中，在原有代码基础上，
把生产者、消费者、队列各独立为项目实现
（即3个项目，以进程方式运行），通过接口，由生产者和消费者调用。*/

/*
* 遇到问题：
* 1、在不确定一个端口中会接入几个Socket长链接时，顺序执行针对每个Socket的线程启动会导致阻塞
**解决办法：为每个端口启动一个线程来进行监听链接
* 2、在使用ServerSocket时，用try包围后，在try中创建ServerSocket，会导致try语块（其中创建
* 了两个线程，但是循环只在线程之中，线程创建完后try语块结束）结束后，Socket接口直接关闭。
**解决办法：服务器（带监听线程）的有效编写方法：
***直接只启动两个监听线程，端口和线程池作为服务器的固定成员变量直接在Main之外声明
* */
public class BufferServer {
    //服务端，缓冲区队列，接收Socket请求，
    //来自Consumer的请求，队列出队，将数据发给Consumer
    //来自Producer的请求，队列入队，将数据发给Producer
    private static final int PRODUCER_PORT = 3000;
    private static final int CONSUMER_PORT = 3001;
    private static final int THREAD_POOL_SIZE = 5;
    private static ServerSocket producerSocket;
    private static ServerSocket consumerSocket;
    private static Buffer buffer = new Buffer(10);
    private static ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public static void main(String[] args) {
        startProducerListener();
        startConsumerListener();
    }
    private static void startProducerListener(){
        new Thread(() -> {
            try{
                producerSocket = new ServerSocket(PRODUCER_PORT);
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

    private static void startConsumerListener(){
        new Thread(() -> {
            try {
                consumerSocket = new ServerSocket(CONSUMER_PORT);
                System.out.println("消费者监听线程启动");
                while(!consumerSocket.isClosed()){
                    try {
                        Socket consumerLink = consumerSocket.accept();
                        System.out.println("消费者已连接");
                        threadPool.execute(new ConsumerHandler(consumerLink,buffer));
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
}
