package com.lowwdel.ParrallelComputing.SocketProdCons.BufferServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*【实验四】生产者-消费者模型的Socket改造
    目标：
（1）在生产者-消费者模型中，在原有代码基础上，
把生产者、消费者、队列各独立为项目实现
（即3个项目，以进程方式运行），通过接口，由生产者和消费者调用。*/
/*【实验五】
1、标准化消息的结构（必需有的内容：消息序号、时间戳、消息tag、消息体），队列中存放这种结构
2、消费者启动后，首先到队列系统（在本实验中可改称为消息中心）中注册，等于告诉队列，一旦有消息，就立即发送给我；
3、生产者生成数据，这个和以前实验一样；
4、消息队列收到生产者发送过来的数据后，根据步骤（1）所订阅的消费者列表，逐个向已订阅的消费者发送此数据（本实验先采用广播消费模式，即每个订阅者都能收到同一队列的全部消息）；
5、独立为三个exe，首先必须启动消息队列的exe，其次启动消费者，最后启动生产者；
6、考虑消息中心的消息持久化，即消息队列接受到生产者消息后会保存到数据库中；
*/
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
    private static BufferWithDB buffer = new BufferWithDB();
    private static ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private static ConsumerRegistry consumerRegistry = new ConsumerRegistry();

    public static void main(String[] args) {
        ServiceManager serviceManager = new ServiceManager(buffer,threadPool,consumerRegistry,PRODUCER_PORT,CONSUMER_PORT);
        serviceManager.startService();
    }

}
