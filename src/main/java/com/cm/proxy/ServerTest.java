package com.cm.proxy;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @program: ssm
 * @description: Proxy Server端
 * @author: DongKe
 * @create: 2019-03-12 21:18
 **/
public class ServerTest {
    private static String serverResponse = "";
    private static boolean finishFlag = false;
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8077);
        System.out.println("Wait for connect...");
        Socket socket = serverSocket.accept();
        System.out.println("Connected!!!");
        ProcessSocket processSocket = new ProcessSocket(socket);
        processSocket.start();
        while (finishFlag){
            System.out.println("ServerTest get callback:" + serverResponse);
            // 返回给客户端？？

        }
    }

    /**
     * 主线程回调
     */
    public static void finishNotify(String responseStr) {
        if (!finishFlag) {
            finishFlag = true;
            serverResponse = responseStr;
        }
    }
    /**
     * 处理client端数据的
     */
    static class ProcessSocket extends Thread {
        Socket processSocket;
        String proxyHost = "";
        // 构造
        public ProcessSocket (Socket socket) {
            this.processSocket = socket;
        }
        @Override
        public void run() {
            try {
                if (this.processSocket == null) {
                    return;
                }
                InputStream inputStream = processSocket.getInputStream();// 从socket获取输入流
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");// 用InputStreamReader 读取 InputStream
                BufferedReader reader = new BufferedReader(inputStreamReader);// 用BufferReader读取InputStreamReader中内容
                StringBuilder builder = new StringBuilder(reader.readLine());
                for (String temp = reader.readLine(); temp != null; temp = reader.readLine()) {
                    System.out.println(temp);
                    // 获取host：字符串，校验客户端要访问的url是否合法
                    // 然后开启线程访问合法的url
                    if (temp.contains("Host")) {
                        proxyHost = getProxyHost(temp);
                        ProxyConnect proxyConnect = new ProxyConnect(proxyHost);
                        proxyConnect.start();
                        break;
                    }
                    // builder.append(temp);
                }
                // System.out.println(" ServerTest-:"+builder.toString());
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //获取客户端真正想要访问的URL
        private String getProxyHost(String temp) {
            String urlHost = "";
            if (temp != null && !"".equals(temp)) {
                urlHost = "http://" + (temp.split(" ").length == 1 ? "" : temp.split(" ")[1]);
            }
            System.out.println("Split host url :" + urlHost);
            return urlHost;
        }
    }


}

