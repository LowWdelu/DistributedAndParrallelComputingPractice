package com.lowwdel.ParrallelComputing.SocketProdCons;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class ProducerClinet {
    private static Random random = new Random();
    public static void main(String[] args) {
        try(Socket socket = new Socket("localhost",3000)){
            System.out.println("生产者已连接");
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            //为了与消耗速度一致，在这里等待1s
            while(true){
                Thread.sleep(1000);
                long number = generateNumber();
                out.writeLong(number);
                out.flush();
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private static long generateNumber(){
        return 2000000000L + random.nextInt(Integer.MAX_VALUE -2000000000);
    }
}
