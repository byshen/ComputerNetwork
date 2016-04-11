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
    // �ṩ����  
    public void service() {  
        try {
            DatagramSocket dataSocket = new DatagramSocket(8821);  
            // �̳߳أ��̶���ʮ���߳�  
            ExecutorService ThreadPool = Executors.newFixedThreadPool(10);  
  
            while (true) {// ���Ͻ������Կͻ��˵�����  
                byte[] buff = new byte[101];// �ļ������Ȳ�����50  
                DatagramPacket dataPacket = new DatagramPacket(buff, buff.length);  
                dataSocket.receive(dataPacket);// �ȴ��������Կͻ��˵����ݰ�  
                // ���յ����ݰ�����һ���߳�Ϊ�ÿͻ�����  
                System.out.println("new thread");
                ThreadPool.execute(new WorkThread(dataPacket));  
            }  
        } catch (SocketException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
    // �ڲ��࣬Ϊÿ���ͻ��ṩ����  
    private class WorkThread implements Runnable {  
        private DatagramPacket packet;  
        private DatagramSocket dataSocket;  
  
        public WorkThread(DatagramPacket packet) {  
            this.packet = packet;  
            try {// �����������Զ˿ڵ�DatagramSocket  
                dataSocket = new DatagramSocket();  
            } catch (SocketException e) {  
                e.printStackTrace();  
            }  
        }  
  
        // ��ȡ�������ص��ļ��б��͸��ͻ���  
        private void showFiles() {  
            File files = new File("upload_download");  
            File[] allFile = files.listFiles();// ��ȡ�����ļ�  
            StringBuffer message = new StringBuffer();  
            for (File f : allFile) {  
                if (f.isFile()) {  
                    message.append(f.getName());  
                    message.append('\n');  
                }  
            }  
            // ������Ӧ���ݰ�  
            byte[] response = message.toString().getBytes();  
            DatagramPacket dataPacket = new DatagramPacket(response, response.length, packet.getAddress(), packet.getPort());  
            try {// ����  
                dataSocket.send(dataPacket);  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
  
        // ����ָ�����ļ�  
        private void download(String fileName) {  
            try {  
                InputStream in = new FileInputStream("upload_download/" + fileName);  
                DatagramPacket dataPacket;  
                byte[] response = new byte[60000];// ÿ�η���60000�ֽ�  
                while (true) {  
                    int len = in.read(response, 0, response.length);  
                    dataPacket = new DatagramPacket(response, len, packet.getAddress(), packet.getPort());  
                    dataSocket.send(dataPacket);// ����  
                    if (in.available() == 0)// �������  
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
            // ��ȡ�ͻ��˴��͹���������  
            byte[] data = packet.getData();  
            // ��ʾ�ͻ��˵����ʾ�ļ���ť����������Ҫ�õ����п������ص��ļ�  
            if (data[0] == 0)  
                showFiles();  
            else if (data[0] == 1)// ��ʾ�ͻ��˵���������������  
                download(new String(data, 1, packet.getLength()).trim());  
            else  
                System.out.println("�������");  
        }  
    }  
  
    public static void main(String[] args) {  
        new UDPServer().service();  
    }  
}  