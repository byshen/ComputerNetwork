package com.file;

import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileNotFoundException;  
import java.io.IOException;  
import java.io.InputStream;  
import java.net.DatagramPacket;  
import java.net.DatagramSocket;  
import java.net.SocketException;  
import java.util.concurrent.ExecutorService;  
import java.util.concurrent.Executors;  

public class UDPServer {  
    // 提供服务  
    public void service() {  
        try {
            DatagramSocket dataSocket = new DatagramSocket(8821);  
            // 线程池，固定有十个线程  
            ExecutorService ThreadPool = Executors.newFixedThreadPool(10);  
  
            while (true) {// 不断接收来自客户端的请求  
                byte[] buff = new byte[101];// 文件名长度不超过50  
                DatagramPacket dataPacket = new DatagramPacket(buff, buff.length);  
                dataSocket.receive(dataPacket);// 等待接收来自客户端的数据包  
                // 接收到数据包，开一个线程为该客户服务  
                System.out.println("new thread");
                ThreadPool.execute(new WorkThread(dataPacket));  
            }  
        } catch (SocketException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
    // 内部类，为每个客户提供服务  
    private class WorkThread implements Runnable {  
        private DatagramPacket packet;  
        private DatagramSocket dataSocket;  
  
        public WorkThread(DatagramPacket packet) {  
            this.packet = packet;  
            try {// 创建本机可以端口的DatagramSocket  
                dataSocket = new DatagramSocket();  
            } catch (SocketException e) {  
                e.printStackTrace();  
            }  
        }  
  
        // 获取可以下载的文件列表传送给客户端  
        private void showFiles() {  
            File files = new File("upload_download");  
            File[] allFile = files.listFiles();// 获取所有文件  
            StringBuffer message = new StringBuffer();  
            for (File f : allFile) {  
                if (f.isFile()) {  
                    message.append(f.getName());  
                    message.append('\n');  
                }  
            }  
            // 构造响应数据包  
            byte[] response = message.toString().getBytes();  
            DatagramPacket dataPacket = new DatagramPacket(response, response.length, packet.getAddress(), packet.getPort());  
            try {// 发送  
                dataSocket.send(dataPacket);  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
  
        // 下载指定的文件  
        private void download(String fileName) {  
            try {  
                InputStream in = new FileInputStream("upload_download/" + fileName);  
                DatagramPacket dataPacket;  
                byte[] response = new byte[60000];// 每次发送60000字节  
                while (true) {  
                    int len = in.read(response, 0, response.length);  
                    dataPacket = new DatagramPacket(response, len, packet.getAddress(), packet.getPort());  
                    dataSocket.send(dataPacket);// 发送  
                    if (in.available() == 0)// 发送完毕  
                        break;  
                }  
                in.close();  
            } catch (FileNotFoundException e) {  
                e.printStackTrace();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
  
        @Override  
        public void run() {  
            // 获取客户端传送过来的数据  
            byte[] data = packet.getData();  
            // 表示客户端点击显示文件按钮，该请求是要得到所有可以下载的文件  
            if (data[0] == 0)  
                showFiles();  
            else if (data[0] == 1)// 表示客户端的请求是下载请求  
                download(new String(data, 1, packet.getLength()).trim());  
            else  
                System.out.println("请求错误");  
        }  
    }  
  
    public static void main(String[] args) {  
        new UDPServer().service();  
    }  
}  