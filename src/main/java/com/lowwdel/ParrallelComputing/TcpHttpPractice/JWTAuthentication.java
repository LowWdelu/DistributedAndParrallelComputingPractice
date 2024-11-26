package com.lowwdel.ParrallelComputing.TcpHttpPractice;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Scanner;

import static com.lowwdel.ParrallelComputing.TcpHttpPractice.HttpUtils.*;

public class JWTAuthentication {
/*    在本实验中，老师已经准备好对应接口，接口的调用顺序为：
1、通过用户名、密码，获取JWT token
2、JWT token保存在本地客户端中
3、客户端每次调用需要验证的接口，每次请求把步骤2的token带上，接口才会有返回；否则接口会返回401需授权

接口1：传入用户名密码，获取JWT token
接口2：JWT测试，URL：http://47.115.44.145:7000/api/auth/authtest，Get方法，传入参数：userName
接口3：post方法，带参数（Json格式），返回Json字符串结果，URL：http://47.115.44.145:7000/api/Test/test4
接口4：post方法，带参数（在body的form里面），返回Json字符串结果，URL：http://47.115.44.145:7000/api/Test/test4
*/
    private static Gson gson =new Gson();
    private volatile static String JWT = null;
    private static final int TOKEN_EXPIRATION_TIME = 10 * 60 * 1000; // 10分钟的过期时间

    public static void main(String[] args) {
        String path = "/api/auth";
        String testPath = "/api/auth/authtest";
        String userName ;
        String testParam ;
        String host = "47.115.44.145";
        int port = 7000;

        /*接口1：传入用户名密码，返回JWT token
        注：
        1）userName为12位字符串
        2）passWord为userName的Md5值（全部小写），
        当passWord报错时，目前会直接返回对应的Md5正确值
        3）token有效期为10分钟
         */

        //先发送username和password，得到passWord
        System.out.println("请输入用户名userName（12位字符串）：");
        Scanner scanner = new Scanner(System.in);
        userName = scanner.next();
        testParam = "?userName=" + "123456";

        System.out.println("获取正确的passWord：");
        String responseWithPassword = sendPOSTRequest(host,port,path,createJSONParam(userName));
        String passWord = extractPasswordFromResponse(responseWithPassword);

        // 启动一个线程来维护JWT token, 每10分钟更新一次JWT token
        new Thread(() ->{
            // 发送带有用户名和密码的请求, 得到JWT token
            maintainJWT(userName,passWord,host,port,path);
            System.out.println("已从接口1中获取JWT token：\n" + JWT +'\n');
        }).start();

        //维护JWT的线程未获取到JWT token时，主线程等待
        while (JWT == null){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


        System.out.println("正在测试接口2：");
        //在后续的请求中，将JWT token放入请求头中
        String responseOfInterface2 = sendGETRequest(host,port,testPath,testParam,JWT);

        System.out.println("正在测试接口3：");
        String responseOfInterface3 = sendPOSTRequest(host,port,"/api/Test/test4",createJSONParam(),JWT);

        System.out.println("正在测试接口4：");
        String responseOfInterface4 = sendFormPOSTRequest(host,port,"/api/Test/test5",createFormParam(),JWT);
    }
    private static void maintainJWT(String userName,String passWord,String host,int port,String path){
        while (true){
            //更新JWT
            try {
                JWT = fetchJWTFromInterface1(userName,passWord,host,port,path);
                System.out.println("JWT 已更新：\n" + JWT +'\n');

                Thread.sleep(TOKEN_EXPIRATION_TIME);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private static String fetchJWTFromInterface1(String userName,String passWord,String host,int port,String path){
        System.out.println("通过正确的userName和passWord，获取JWT token：");
        String responseWithJWT = sendPOSTRequest(host,port,path,createJSONParam(userName,passWord));
        return extractJWTFromResponse(responseWithJWT);
    }
    private static String extractPasswordFromResponse(String Response){
        JsonObject jsonObject = gson.fromJson(Response, JsonObject.class);

        String message = jsonObject.get("message").getAsString(); // 提取message字段的值

        return message.substring(35);
    }
    private static String extractJWTFromResponse(String responseWithJWT){
        JsonObject jsonObject = gson.fromJson(responseWithJWT,JsonObject.class);

        return jsonObject.get("token").getAsString();
    }
}

