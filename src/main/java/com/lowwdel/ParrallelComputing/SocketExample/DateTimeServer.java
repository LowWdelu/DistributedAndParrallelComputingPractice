package com.lowwdel.ParrallelComputing.SocketExample;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;


public class DateTimeServer {
    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(8888)){
            System.out.println("服务器已启动，等待客户端链接");

            while(true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("客户端已链接" + clientSocket.getInetAddress().getHostAddress());

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String request = in.readLine();

                if(request.equals("getdatetime")){
                    String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println(currentTime);
                    System.out.println("已从服务端发送当前时间" + currentTime);
                }

                clientSocket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
