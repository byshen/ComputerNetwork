import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {
	public int esc;
	public TCPClient(){
		esc = 0;
	}
    public void receiveFile(String serverPath, String savePath,String ip,int port) throws IOException{
    	Socket socket=null;
        try { 
            socket = new Socket(ip,port); 
          } catch (UnknownHostException e1) { 
            e1.printStackTrace(); 
          } catch (IOException e1) { 
            e1.printStackTrace(); 
          } 
        DataInputStream dis=null;
     
        Writer writer = new OutputStreamWriter(socket.getOutputStream());
        writer.write(serverPath);
        writer.write("eof");  
        writer.flush();
        
        //System.out.println("aaaaa\n");
        
        try {
            dis = new DataInputStream(new BufferedInputStream(socket 
                .getInputStream())); 
          } catch (IOException e1) { 
            e1.printStackTrace(); 
          } 
        int bufferSize = 1024; 
        // ������ 
        byte[] buf = new byte[bufferSize]; 
        int passedlen = 0; 
        long len = 0; 
        // ��ȡ�ļ����� 
       try{
    //	   System.out.println("afajdshfaksjldhfaksdf\n");
    	savePath += dis.readUTF(); 
        DataOutputStream fileOut = new DataOutputStream( 
            new BufferedOutputStream(new BufferedOutputStream( 
                new FileOutputStream(savePath)))); 
        len = dis.readLong(); 
        System.out.println("�ļ��ĳ���Ϊ:" + len + "    KB"); 
        System.out.println("��ʼ�����ļ�!"); 
        while (esc == 0) { 
            int read = 0; 
            if (dis!= null) { 
              read = dis.read(buf); 
            } 
            passedlen += read; 
            if (read == -1) { 
              break; 
            } 
            System.out.println("�ļ�������" + (passedlen * 100 / len) + "%"); 
            fileOut.write(buf, 0, read); 
          } 
          System.out.println("������ɣ��ļ���Ϊ" + savePath); 
          fileOut.close(); 
        } catch (Exception e) { 
          e.printStackTrace(); 
          return; 
        } 
    }
    public static void main(String[] args) throws IOException { 
        new TCPClient().receiveFile("H:\\test.txt", "F:\\",  "localhost", 8821); 
    }  

}
