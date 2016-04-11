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
 * ���ؿͻ��� 
 */  
public class UDPClient extends JFrame {  
    // ��ʾ�����ص��ļ�  
    private JTextArea textArea = new JTextArea();  
  
    private JPanel panel = new JPanel();  
    // ����ʱ�����ļ�  
    private JFileChooser saveFile = new JFileChooser(".");  
  
    private JButton showButton = new JButton("��ʾ�ļ�");  
    private JButton downloadButton = new JButton("����...");  
    // ����ʱ����Ҫ���ص� �ļ�����ע���ļ���������textArea��ʾ���ļ���  
    private JTextField downloadFile = new JTextField("");  
    
    private JLabel timeCost = new JLabel("time: 0 ms");
    private DatagramSocket dataSocket=null;  
  
    public UDPClient() {  
        // frame �Ļ�������  
        this.setTitle("UDPClient");  
        this.setVisible(true);  
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        this.setSize(400, 500);  
        this.setLayout(new BorderLayout());  
        this.setResizable(false);  
  
        // ���ò��ɱ༭  
        textArea.setEditable(false);  
  
        panel.setLayout(new GridLayout(3, 2, 5, 5));  
        panel.add(new JLabel("�����ť��ʾ�����ص��ļ�"));  
        panel.add(showButton);  
        panel.add(downloadFile);  
        panel.add(downloadButton);  
        panel.add(timeCost);
        // �������frame��  
        add(new JScrollPane(textArea));  
        add(panel, BorderLayout.SOUTH);  
  
        // saveFileֻ�ܴ�Ŀ¼  
        saveFile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);  
  
        // ��ʾ�ļ���ťע���¼�  
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
  
        // ���ذ�ťע���¼�  
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
  
    // ��ʾ�ļ�  
    private void showFiles() {  
        try {  
            if (dataSocket == null)  
                dataSocket = new DatagramSocket();  
            // �����������ݰ������͸�������  
            byte[] request = { 0 };  
            DatagramPacket requestPacket = new DatagramPacket(request, request.length, InetAddress.getLocalHost(), 8821);  
            dataSocket.send(requestPacket);  
  
            // ���շ����������ݰ�����ʾ��textArea��  
            byte[] receive = new byte[1024 * 1024];  
            DatagramPacket receivePacket = new DatagramPacket(receive, receive.length);  
            dataSocket.receive(receivePacket);  
            String str = new String(receivePacket.getData(), 0, receivePacket.getLength());  
            textArea.setText(str);  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
    // �����ļ�  
    private void downloadFile() {  
        // ��ȡҪ���ص��ļ���  
        String fileName = downloadFile.getText().trim();  
        // ���п������ص��ļ�  
        String allFiles = textArea.getText();  
        // �ļ���Ϊ��  
        if (fileName == null || "".equals(fileName))  
            JOptionPane.showMessageDialog(null, "��ѡ����ȷ���ļ���", "�ļ�������", JOptionPane.WARNING_MESSAGE);  
        // �ļ������ڿ������ص��ļ���  
        else if (allFiles.contains((fileName + '\n'))) {  
            saveFile.showSaveDialog(null);  
            File f = saveFile.getSelectedFile();// ��ȡѡ�е��ļ���  
            if (f.exists()) {  
                // �����ļ��Ƿ��Ѿ�������Ŀ¼��  
                String[] fileNames = f.list();  
                boolean exit = false;  
                for (String name : fileNames)  
                    if (name.equals(fileName)) {  
                        exit = true;  
                        break;  
                    }  
  
                if (exit)// ���Ҫ���ص��ļ��Ѿ�����  
                    JOptionPane.showMessageDialog(null, "���ļ��Ѿ�����", "��ѡ��������ļ�����", JOptionPane.WARNING_MESSAGE);  
                else {  
                    // ���͵�����  
                	
                	long startTime=System.currentTimeMillis();
                	long endTime;
                    byte[] request = (new String(new byte[] { 1 }) + fileName).getBytes();  
                    try {  
                        if (dataSocket == null)  
                            dataSocket = new DatagramSocket();  
                        // �����������ݰ������͸�������  
                        DatagramPacket requestPacket = new DatagramPacket(request, request.length, InetAddress.getLocalHost(), 8821);  
                        dataSocket.send(requestPacket);  
  
                        // ���շ����������ݰ�,���ļ�������ѡ�е��ļ�����  
                        OutputStream out = new FileOutputStream(f.getAbsolutePath() + "/" + fileName, true);  
                        byte[] receive = new byte[60000];// ÿ�ν���60000�ֽ�  
                        DatagramPacket receivePacket;  
                        // ���Ͻ������Է����������ݰ�  
                        while (true) {  
                            receivePacket = new DatagramPacket(receive, receive.length);  
                            dataSocket.receive(receivePacket);  
                            out.write(receivePacket.getData(), 0, receivePacket.getLength());// ��������ļ�����������ļ���  
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
                // ѡ����ļ��в�����  
                JOptionPane.showMessageDialog(null, "��ѡ����ȷ�Ĵ洢·��", "�洢·������", JOptionPane.WARNING_MESSAGE);  
  
        } else {// �ļ�������  
            JOptionPane.showMessageDialog(null, "��ѡ����ȷ���ļ���", "�ļ�������", JOptionPane.WARNING_MESSAGE);  
        }  
    }  
  
    public static void main(String[] args) {  
        new UDPClient();  
    }  
}  