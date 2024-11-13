package com.lowwdel.ParrallelComputing.SocketProdCons;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ConsumerClient {
    // 消费者客户端，从服务器接收数据并处理
    public static void main(String[] args) throws IOException {
        try(Socket socket = new Socket("localhost", 3001)){
            System.out.println("消费者已连接");
            //询问是否要批量消息拉取
            Scanner scanner = new Scanner(System.in);
            System.out.println("是否要批量消息拉取？(y/n)");
            String answer = scanner.nextLine();
            if(answer.equals("y")){
                System.out.println("请输入批量消息拉取的数量：");
                int batchSize = scanner.nextInt();
                scanner.nextLine();

                //将批量消息拉取的数量发送给服务器
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                out.writeInt(batchSize);
                out.flush();

                //接收批量消息并处理
                DataInputStream in = new DataInputStream(socket.getInputStream());
                }

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
