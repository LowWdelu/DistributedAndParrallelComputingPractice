package com.lowwdel.ParrallelComputing.SocketExample;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try(Socket socket  = new Socket("localhost", 3000)){
            //向服务器发送请求
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            out.println("getdatetime");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = in.readLine();

            System.out.println("服务器时间：" + response);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
