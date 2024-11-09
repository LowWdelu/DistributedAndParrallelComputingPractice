package com.lowwdel.ParrallelComputing.SocketExample;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    /*服务器2.0版本：
        实现以下功能：
    1、服务器端监听于本地3000端口
    2、需要使用多线程（客户端连接后，新建线程处理对应连接）
    3、实现3个协议（多线程为可选，注意，字符集使用ASCII）：
    1）客户端发送字符串：gettime；服务器端返回当前时间字符串（年月日时分秒）：yyyyMMddHHmmss
    2）客户端发送字符串：test 任意内容；服务器端返回：任意内容
    3）客户端发送字符串：auth 学号；服务器内置自己的学号，当客户端发送的是自己正确学号时返回ok，错误时返回error
    4）多线程测试（可选，注：实验课期间此部分为可选，请选择不同按钮进行测试）
     */
    /*
    * 遇到问题：
    * 1、使用Reader读取客户端数据时，使用readLine()方法，该方法要求结尾有换行符，否则会一直阻塞（需要字符流结尾有个\n）
    **解决办法：修改了代码，使用read()方法读取数据，无需通过换行符来确定字符串结尾
    * 2、使用read()方法后，错误的用循环读取数据，由于请求的字符串中没有结尾，导致程序一直阻塞在循环读取每一个字符中（需要字符流结尾是-1）
    **解决办法：修改了代码改为使用read(char[] buffer,0,lengthOfString)，将Read流中的字符一次性读入buffer中
    * * *以上，在读取数据时出现了问题
    * 3、使用PrintWriter时，使用println()方法，该方法会在字符流中自动添加\r\n，导致输出不是纯字符串
    **解决办法：改为使用print()方法，将字符流输出到客户端
    * 4、使用print()方法时，线程会阻塞在没有换行符作为结尾标志的输出流中
    **解决办法：在每次print(StringToPrint)后，使用flush()方法刷新输出流，将数据发送到客户端
    * */
    private static final String myStuID = "202128310219";
    private static final int BUFFER_SIZE = 1024;
    public static void main(String[] args) {
        // 创建ServerSocket
        ExecutorService threadPool = Executors.newFixedThreadPool(5);

        try (ServerSocket serverSocket = new ServerSocket(3000)) {
            System.out.println("服务器已启动，等待客户端链接");

            while (true) {
                // 接受Socket请求
                Socket clientSocket = serverSocket.accept();
                System.out.println("客户端已链接" + clientSocket.getInetAddress().getHostAddress());
                //将此处新建线程，修改为调用线程池中的线程处理
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;

        ClientHandler(Socket ClientSocket) {
            this.socket = ClientSocket;
        }

        @Override
        public void run() {
            System.out.println("线程启动");
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.US_ASCII)
            ) {
                // 读取请求
                char[] buffer = new char[BUFFER_SIZE];
                StringBuilder requestBuilder = new StringBuilder();

                int len;
                while ((len = in.read(buffer)) != -1) {
                    // 将当前读入的内容追加到请求构建器中
                    requestBuilder.append(buffer, 0, len);
                    String request = requestBuilder.toString().strip();

                    // 检查请求是否包含完整命令
                    if (isCompleteRequest(request)) {
                        // 处理请求
                        handleRequest(request, out);

                        // 清空请求构建器，以便接收下一个请求
                        requestBuilder.setLength(0);
                    }
                }

            } catch (IOException e) {
                System.err.println("连接处理错误: " + e.getMessage());
            }
        }

        private boolean isCompleteRequest(String request) {
            // 判断请求是否以特定的关键词结尾来确定请求是否完整
            return request.endsWith("gettime") || request.startsWith("test ") || request.startsWith("auth ");
        }

        private void handleRequest(String request, PrintWriter out) {
            if (request.equals("gettime")) {
                String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                out.print(currentTime);
                out.flush();
                System.out.println("输出服务器时间：" + currentTime);
            } else if (request.startsWith("test ")) {
                String content = request.substring(5);
                System.out.println("输出客户端内容：" + content);
                out.print(content);
                out.flush();
            } else if (request.startsWith("auth ")) {
                String studentId = request.substring(5);
                System.out.println("输出客户端学号：" + studentId);
                if (studentId.equals(myStuID)) {
                    out.print("ok");
                    out.flush();
                    System.out.println("输出服务器响应：ok");
                } else {
                    out.print("error");
                    out.flush();
                    System.out.println("输出服务器响应：error");
                }
            }
        }
    }
}
