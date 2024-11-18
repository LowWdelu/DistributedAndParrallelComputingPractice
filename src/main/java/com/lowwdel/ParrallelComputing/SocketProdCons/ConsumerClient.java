package com.lowwdel.ParrallelComputing.SocketProdCons;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ConsumerClient {
    // 消费者客户端，从服务器接收数据并处理
    private static final int HEARTBEAT_INTERVAL = 2000;
    private static final int HEARTBEAT_TIMEOUT = 5000;
    private static final String TAG = "1";
    private static final String HEARTBEAT_PROTOCOL = "HEARTBEAT";
    private static final String HEARTBEAT_ACK = "ACK";
    private static boolean running = true;
    private static Socket socket;
    private static long lastHeartbeatTime;
    private static DataOutputStream out;
    private static DataInputStream in;
    public static void main(String[] args){
        while(running){
            //客户端始终处于循环中，即使链接断开也在循环中尝试重连。
            try {
                connectAndSubscribe();
                startHeartbeatThread();
                listenForMessages();
            } catch (IOException e) {
                System.out.println("连接失败：" + e.getMessage());
            }

            //如果前面的某个方法出现异常，running设为False，让客户端重连
            if(!running){
                //从注册表中删除该消费者
                try {
                    Thread.sleep(1000);
                    running = true;
                } catch (InterruptedException e) {
                    System.out.println("重连被中断：" + e.getMessage());
                }
            }
        }
    }
    private static void connectAndSubscribe() throws IOException {
        socket = new Socket("localhost", 3001);
        System.out.println("消费者链接成功");
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());

        //发送订阅协议
        out.writeUTF("ImConsumer:" + TAG);
        out.flush();
        System.out.println("消费者发送订阅协议");
        lastHeartbeatTime = System.currentTimeMillis();
    }

    private static void startHeartbeatThread(){
        new Thread(() ->{
            //客户端处于running状态时，保持心跳包机制运行
            while(running){
                try {
                    out.writeUTF(HEARTBEAT_PROTOCOL);
                    System.out.println("发送心跳包");
                    out.flush();
                    Thread.sleep(HEARTBEAT_INTERVAL);
                } catch (IOException | InterruptedException e) {
                    //心跳包机制出现异常，running设为False，让客户端重连
                    System.out.println("发送心跳包失败");
                    running = false;
                    break;
                }
            }
        }).start();
    }

    private static void listenForMessages() throws IOException {
        //客户端处于running状态时，不断接收服务器发来的数据
        while(running){
            //如果有数据，就接收并判断是心跳包响应，还是新消息
            if(in.available() > 0){
                String message = in.readUTF();
                if(message.equals(HEARTBEAT_ACK)){
                    System.out.println("收到心跳包响应");
                    lastHeartbeatTime = System.currentTimeMillis();
                }else{
                    processProduct(message);
                }
            }

            //如果心跳包超时，就关闭连接
            if(System.currentTimeMillis() - lastHeartbeatTime > HEARTBEAT_TIMEOUT){
                System.out.println("心跳超时，尝试重新连接");
                running = false;
                socket.close();
            }
        }
    }

    private static void processProduct(long product) {
        System.out.println("消费者接收到数据：" + product);
    }

    private static void processProduct(String message){
        System.out.println("消费者接收到数据：" + message);
    }
}
