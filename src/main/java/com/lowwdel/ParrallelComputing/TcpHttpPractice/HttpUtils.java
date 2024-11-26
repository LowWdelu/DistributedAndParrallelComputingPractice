package com.lowwdel.ParrallelComputing.TcpHttpPractice;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {
    static String  DEFAULT_PASSWORD = "123456";//用于凑一个键值对发送请求，包含正确passWord的请求返回后会被修改
    public static String createJSONParam(){
        Map<String, String> params = new HashMap<>();

        params.put("passWord", DEFAULT_PASSWORD);
        params.put("userName", "123456");

        Gson gson = new Gson();
        System.out.println("JSON Param: " + gson.toJson(params));
        return gson.toJson(params);
    }
    public static String createJSONParam(String username){
        Gson gson = new Gson();
        Map<String, String> params = new HashMap<>();
        params.put("userName", username);
        params.put("passWord", DEFAULT_PASSWORD);
        return gson.toJson(params);
    }
    public static String createJSONParam(String username ,String password){
        Gson gson = new Gson();
        Map<String, String > params = new HashMap<>();
        params.put("userName", username);
        params.put("passWord", password);
        return gson.toJson(params);
    }
    public static String createFormParam(){
        return "userName=" + "123456" + "&" + "passWord=" + DEFAULT_PASSWORD;
    }
    public static String sendPOSTRequest(String host, int port, String path, String jsonParam) {
        try (
            Socket socket = new Socket(host,port);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));){

            String httpRequest = "POST " + path + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n" +
                    "User-Agent: Java Client\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Content-Length: " + jsonParam.length() + "\r\n" +
                    "Connection: close\r\n\r\n" +
                    jsonParam;
            System.out.println("Sending POST request to" + host + path);
            writer.write(httpRequest);
            writer.flush();

            System.out.println("Response from " + host + ":");
            String jsonResponse = extractResponseBody(reader);

            System.out.println(jsonResponse);
            return jsonResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String sendPOSTRequest(String host, int port, String path, String jsonParam, String JWT_token) {
        try (
                Socket socket = new Socket(host, port);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String httpRequest = "POST " + path + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n" +
                    "Authorization: Bearer " + JWT_token + "\r\n" +
                    "User-Agent: Java Client\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Content-Length: " + jsonParam.length() + "\r\n" +
                    "Connection: close\r\n\r\n" +
                    jsonParam;
            System.out.println("Sending POST request with JWT token to" + host + path);
            writer.write(httpRequest);
            writer.flush();

            System.out.println("Response from " + host + ":");
            String jsonResponse = extractResponseBody(reader);

            System.out.println(jsonResponse);
            return jsonResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String sendFormPOSTRequest(String host, int port, String path, String formData, String JWT_token){
        try (
                Socket socket = new Socket(host, port);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String httpRequest = "POST " + path + " HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n" +
                    "Authorization: Bearer " + JWT_token + "\r\n" +
                    "User-Agent: Java Client\r\n" +
                    "Content-Type: application/x-www-form-urlencoded\r\n" +
                    "Content-Length: " + formData.length() + "\r\n" +
                    "Connection: close\r\n\r\n" +
                    formData;
            System.out.println("Sending POST request with JWT token to" + host + path);
            writer.write(httpRequest);
            writer.flush();

            System.out.println("Response from " + host + ":");
            String jsonResponse = extractResponseBody(reader);

            System.out.println(jsonResponse);
            return jsonResponse;
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String sendGETRequest(String host, int port, String path,String param, String JWT_token) {
        try (
                Socket socket = new Socket(host, port);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String httpRequest = "GET " + path + param +" HTTP/1.1\r\n" +
                    "Host: " + host + "\r\n" +
                    "Authorization: Bearer " + JWT_token + "\r\n" +
                    "User-Agent: Java Client\r\n" +
                    "Connection: close\r\n\r\n";
            System.out.println("Sending GET request with JWT token to" + host + path);
            writer.write(httpRequest);
            writer.flush();

            System.out.println("Response from " + host + ":");
            String jsonResponse = extractResponseBody(reader);
            System.out.println(jsonResponse);
            return jsonResponse;
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String extractResponseBody(BufferedReader reader) throws IOException {
        StringBuilder responseBody = new StringBuilder();
        String line;
        boolean isBody = false;

        while((line = reader.readLine())!= null){
            if(line.isEmpty()){
                isBody = true;
                continue;
            }
            if(isBody){
                responseBody.append(line).append("\n");
            }
        }

        return responseBody.toString();
    }
}

