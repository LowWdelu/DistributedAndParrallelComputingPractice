package com.lowwdel.ParrallelComputing.SocketProdCons.BufferServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class ConsumerHandler implements Runnable{
    private final Socket consumerLink;
    //不再需要bufferWithDB，该线程只需等待被调用send()方法
    private ConsumerRegistry registry;
    private final String HEARTBEAT_ACK = "ACK";
    private boolean connectionAlive = true;
    private String subscribeTag = null;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Object lock = new Object();
    public ConsumerHandler(Socket consumerLink,ConsumerRegistry registry) throws IOException {
        this.consumerLink = consumerLink;
        this.registry = registry;
        this.in = new DataInputStream(consumerLink.getInputStream());
        this.out = new DataOutputStream(consumerLink.getOutputStream());
    }

    @Override
    public void run(){
        try {
            //【订阅者模式】订阅者模式不支持拉取，更不支持批量拉取，应当与批量拉取在不同的分支

            //【注册】接收消费者发来的协议，协议体：“ImConsumer:tag”,将其存入注册表中
            while(connectionAlive){
                String protocol = in.readUTF();
                if(protocol.startsWith("ImConsumer:")){
                    subscribeTag = protocol.substring("ImConsumer:".length());
                    System.out.println("消费者订阅了tag为"+ subscribeTag +"的消息");
                    //订阅tag
                    registry.registerConsumer(subscribeTag,this);
                    responseHeartbeat();
                    break;
                }else{
                    System.out.println("消费者连接失败，协议错误");
                    closeConnection();
                }
            }
        } catch (IOException e) {
            System.out.println("消费者连接失败：" + e.getMessage());
            closeConnection();
        }
    }
    private void responseHeartbeat(){
        Thread heartbeatResponseThread = new Thread(() ->{
            while(connectionAlive){
                try {
                    //需要先加入输入流的检测，否则会阻塞在输入流的readUTF()方法上
                    if(in.available() > 0) {
                        String protocol = in.readUTF();
                        if (protocol.equals("HEARTBEAT")) {
                            System.out.println("收到心跳包");
                            //频率发送ACK响应，可能与随机的send(Message)方法冲突
                            //封装出去用锁机制确保不会发生IO冲突
                            sendHeartbeatACK();
                        }
                    }
                }catch (EOFException e){
                    System.out.println("消费者连接已关闭");
                    closeConnection();

                } catch (IOException e){
                System.out.println("心跳包响应失败：" + e.getMessage());
                closeConnection();
                }
            }
        });
        heartbeatResponseThread.start();
    }

    private void sendHeartbeatACK(){
        synchronized (lock){
            try {
                out.writeUTF(HEARTBEAT_ACK);
                out.flush();
            } catch (IOException e) {
                System.out.println("心跳包响应失败：" + e.getMessage());
                closeConnection();
            }
        }
    }

    public synchronized void send(Message message){
        synchronized (lock){
            try {
                DataOutputStream out = new DataOutputStream(consumerLink.getOutputStream());
                if (connectionAlive) {
                    out.writeUTF(message.toString());
                    out.flush();
                }
            } catch (IOException e) {
                System.out.println("发送数据失败" + e.getMessage());
                closeConnection();
            }
        }
    }

    private void closeConnection(){
        connectionAlive = false;
        try {
            if (subscribeTag != null) {
                registry.unregisterConsumer(subscribeTag,this);
            }
            consumerLink.close();
            System.out.println("消费者连接已关闭");
        } catch (IOException e) {
            System.out.println("关闭连接失败：" + e.getMessage());
        }
    }
}
