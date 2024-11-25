package com.lowwdel.ParrallelComputing.TcpHttpPractice;

import com.google.gson.Gson;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ApiInvokeExercise {
    public static void main(String[] args) {
        //目标URL和端口号
        String url = "47.115.44.145";
        String path1 = "/api/Test/test1";
        String path2 = "/api/Test/test2";
        String path3 = "/api/Test/test3";
        String path4 = "/api/Test/test4";
        String path5 = "/api/Test/test5";
        String path6 = "/WeatherForecast";
        int port = 7000;

        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入测试序号：");
        int num = scanner.nextInt();
        switch (num){
            case 1:
                //get方法，没参数，返回固定字符串（英文）
                sendGetRequest(url,path1,port);
                break;
            case 2:
                //get方法，没参数，返回固定字符串（中英文）
                sendGetRequest(url,path2,port);
                break;
            case 3:
                //（3）get方法，带参数，返回传入字符+固定字符串
                System.out.println("请输入参数：(name)");
                String name = scanner.next();
                String param3 = "?name=" + name;
                sendGetRequest(url,path3 + param3,port);
                break;
            case 4:
                //（4）post方法，带参数（Json格式），返回Json字符串结果
                sendJSONPostRequest(url,path4,port,createJSONParam());
                break;
            case 5:
                //（5）post方法，带参数（在body的form里面），返回Json字符串结果
                sendPostFormRequest(url,path5,port,createFormParam());
                break;
            case 6:
                //获取天气数据
                sendGetRequest(url,path6,port);
                break;
            default:
                System.out.println("输入错误");
                break;
        }

    //    sendGetRequest(url,path1,port);
    //    sendGetRequest(url,path2,port);

    //    sendPostRequest(url,path4,port,createJSONParam());
    }

    public static void sendGetRequest(String host, String path, int port){
        try(Socket socket = new Socket(host,port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))){

            //构造 HTTP GET 请求
            String httpRequest = "GET "+ path +" HTTP/1.1\r\n" +
                    "Host: " + host +"\r\n" +
                    "Connection: close\r\n\r\n";
            System.out.println("Sending request to " + host + path);
            writer.write(httpRequest);
            writer.flush();

            System.out.println("Response from " + host + ":");
            String responseLine;
            while((responseLine = reader.readLine()) !=null){
                System.out.println(responseLine);
            }
        }catch (IOException e){
            System.out.println("与" + host +"的通信出错");
        }
    }
    public static void sendJSONPostRequest(String host, String path, int port, String jsonPayload){
        try (
            Socket socket = new Socket(host,port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))){
            String httpRequest =
                    "POST " + path + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n" +
                    "User-Agent: Java Client\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Content-Length: " + jsonPayload.length() + "\r\n" +
                    "Connection: close\r\n\r\n" +
                    jsonPayload;
            System.out.println("Sending POST request to " + host + path);
            writer.write(httpRequest);
            writer.flush();

            System.out.println("Response from " + host + ":");
            String responseLine;
//            char[] response = new char[1024];
//            int bytesRead = reader.read(response);
//            System.out.println(new String(response,0,bytesRead));
            while((responseLine = reader.readLine()) != null){
                System.out.println(responseLine);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void sendPostFormRequest(String host, String path, int port, String formData){
        try(Socket socket = new Socket(host, port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))){

            String httpRequest =
                    "POST " + path + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n" +
                    "User-Agent: Java Client\r\n" +
                    "Content-Type: application/x-www-form-urlencoded\r\n" +
                    "Content-Length: " + formData.length() + "\r\n" +
                            "Connection: close\r\n\r\n" +
                            formData;
            writer.write(httpRequest);
            writer.flush();

            System.out.println("send POST request to " + host + path);
            System.out.println("Response from " + host + ":");

            String responseLine;
            while((responseLine = reader.readLine())!= null) {
                System.out.println(responseLine);
            }
            }catch (IOException e){
            System.out.println("与" + host + "的通信出错");
        }
    }
    public static String createJSONParam(){
        Map<String, String> params = new HashMap<>();

        params.put("passWord", "56789");
        params.put("userName", "123456");

        Gson gson = new Gson();
        System.out.println("JSON Param: " + gson.toJson(params));
        return gson.toJson(params);

    } 
    public static String createFormParam(){
        return "userName=" + "123456" + "&" + "passWord=" + "56789";
    }
}
