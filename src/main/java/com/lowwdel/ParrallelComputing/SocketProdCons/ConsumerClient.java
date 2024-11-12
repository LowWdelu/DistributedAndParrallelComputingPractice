package com.lowwdel.ParrallelComputing.SocketProdCons;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ConsumerClient {
    // 消费者客户端，从服务器接收数据并处理
    public static void main(String[] args) throws IOException {
        try(Socket socket = new Socket("localhost", 3001)){
            System.out.println("消费者已连接");

            DataInputStream in = new DataInputStream(socket.getInputStream());
            while(true){
                long product = in.readLong();
                processProduct(product);
            }
        }
    }

    private static void processProduct(long product) {
        System.out.println("消费者接收到数据：" + product);
    }
}
