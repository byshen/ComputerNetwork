package com.file;

import java.awt.BorderLayout;  
import java.awt.GridLayout;  
import java.awt.event.ActionEvent;  
import java.awt.event.ActionListener;  
import java.io.File;  
import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.OutputStream;  
import java.net.DatagramPacket;  
import java.net.DatagramSocket;  
import java.net.InetAddress;  
  
import javax.swing.*;  
  
/** 
 * 下载客户端 
 */  
public class UDPClient extends JFrame {  
    // 显示可下载的文件  
    private JTextArea textArea = new JTextArea();  
  
    private JPanel panel = new JPanel();  
    // 下载时保存文件  
    private JFileChooser saveFile = new JFileChooser(".");  
  
    private JButton showButton = new JButton("显示文件");  
    private JButton downloadButton = new JButton("下载...");  
    // 下载时填入要下载的 文件名，注意文件名必须是textArea显示的文件名  
    private JTextField downloadFile = new JTextField("");  
    
    private JLabel timeCost = new JLabel("time: 0 ms");
    private DatagramSocket dataSocket=null;  
  
    public UDPClient() {  
        // frame 的基本设置  
        this.setTitle("UDPClient");  
        this.setVisible(true);  
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        this.setSize(400, 500);  
        this.setLayout(new BorderLayout());  
        this.setResizable(false);  
  
        // 设置不可编辑  
        textArea.setEditable(false);  
  
        panel.setLayout(new GridLayout(3, 2, 5, 5));  
        panel.add(new JLabel("点击按钮显示可下载的文件"));  
        panel.add(showButton);  
        panel.add(downloadFile);  
        panel.add(downloadButton);  
        panel.add(timeCost);
        // 组件加入frame中  
        add(new JScrollPane(textArea));  
        add(panel, BorderLayout.SOUTH);  
  
        // saveFile只能打开目录  
        saveFile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);  
  
        // 显示文件按钮注册事件  
        showButton.addActionListener(new ActionListener() {  
            @Override  
            public void actionPerformed(ActionEvent e) {  
                showButton.setEnabled(false);  
                downloadButton.setEnabled(false);  
                showFiles();  
                showButton.setEnabled(true);  
                downloadButton.setEnabled(true);  
            }  
        });  
  
        // 下载按钮注册事件  
        downloadButton.addActionListener(new ActionListener() {  
            @Override  
            public void actionPerformed(ActionEvent e) {  
                showButton.setEnabled(false);  
                downloadButton.setEnabled(false);  
                downloadFile();  
                showButton.setEnabled(true);  
                downloadButton.setEnabled(true);  
            }  
        });  
    }  
  
    // 显示文件  
    private void showFiles() {  
        try {  
            if (dataSocket == null)  
                dataSocket = new DatagramSocket();  
            // 创建发送数据包并发送给服务器  
            byte[] request = { 0 };  
            DatagramPacket requestPacket = new DatagramPacket(request, request.length, InetAddress.getLocalHost(), 8821);  
            dataSocket.send(requestPacket);  
  
            // 接收服务器的数据包，显示在textArea中  
            byte[] receive = new byte[1024 * 1024];  
            DatagramPacket receivePacket = new DatagramPacket(receive, receive.length);  
            dataSocket.receive(receivePacket);  
            String str = new String(receivePacket.getData(), 0, receivePacket.getLength());  
            textArea.setText(str);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
    // 下载文件  
    private void downloadFile() {  
        // 获取要下载的文件名  
        String fileName = downloadFile.getText().trim();  
        // 所有可以下载的文件  
        String allFiles = textArea.getText();  
        // 文件名为空  
        if (fileName == null || "".equals(fileName))  
            JOptionPane.showMessageDialog(null, "请选中正确的文件名", "文件名错误", JOptionPane.WARNING_MESSAGE);  
        // 文件名是在可以下载的文件中  
        else if (allFiles.contains((fileName + '\n'))) {  
            saveFile.showSaveDialog(null);  
            File f = saveFile.getSelectedFile();// 获取选中的文件夹  
            if (f.exists()) {  
                // 检测该文件是否已经存在于目录中  
                String[] fileNames = f.list();  
                boolean exit = false;  
                for (String name : fileNames)  
                    if (name.equals(fileName)) {  
                        exit = true;  
                        break;  
                    }  
  
                if (exit)// 如果要下载的文件已经存在  
                    JOptionPane.showMessageDialog(null, "此文件已经存在", "请选择另外的文件下载", JOptionPane.WARNING_MESSAGE);  
                else {  
                    // 发送的请求  
                	
                	long startTime=System.currentTimeMillis();
                	long endTime;
                    byte[] request = (new String(new byte[] { 1 }) + fileName).getBytes();  
                    try {  
                        if (dataSocket == null)  
                            dataSocket = new DatagramSocket();  
                        // 创建发送数据包并发送给服务器  
                        DatagramPacket requestPacket = new DatagramPacket(request, request.length, InetAddress.getLocalHost(), 8821);  
                        dataSocket.send(requestPacket);  
  
                        // 接收服务器的数据包,把文件保存在选中的文件夹中  
                        OutputStream out = new FileOutputStream(f.getAbsolutePath() + "/" + fileName, true);  
                        byte[] receive = new byte[60000];// 每次接收60000字节  
                        DatagramPacket receivePacket;  
                        // 不断接收来自服务器的数据包  
                        while (true) {  
                            receivePacket = new DatagramPacket(receive, receive.length);  
                            dataSocket.receive(receivePacket);  
                            out.write(receivePacket.getData(), 0, receivePacket.getLength());// 输出流把文件内容输出到文件中  
                            out.flush();  
                            if (receivePacket.getLength() != receive.length)  
                                break;  
                        }  
                        out.close();  
                        
                        endTime = System.currentTimeMillis();
                        long time=endTime-startTime;
                        System.out.println(time);
                        timeCost.setText("Download " + fileName + " cost time: " + time + "ms.");
                    } catch (IOException e) {  
                        e.printStackTrace();  
                    }  
                }  
  
            } else  
                // 选择的文件夹不存在  
                JOptionPane.showMessageDialog(null, "请选择正确的存储路径", "存储路径错误", JOptionPane.WARNING_MESSAGE);  
  
        } else {// 文件名错误  
            JOptionPane.showMessageDialog(null, "请选择正确的文件名", "文件名错误", JOptionPane.WARNING_MESSAGE);  
        }  
    }  
  
    public static void main(String[] args) {  
        new UDPClient();  
    }  
}  