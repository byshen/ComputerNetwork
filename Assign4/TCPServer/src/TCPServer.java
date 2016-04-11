import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
	private ServerSocket ss=null;
	public TCPServer(){
		
	}

	public void sendFile(String filePath,int port){
		DataOutputStream dos=null;
		DataInputStream dis=null;
		///
		Reader reader;
		
		Socket socket=null;
		
		try {
			
			ss=new ServerSocket(port);
			socket=ss.accept();
			System.out.println("afajdshfaksjldhfaksdf\n");
			/* get file path first */
			reader = new InputStreamReader(socket.getInputStream());
			
			
			char chars[] = new char[64];
			int len;
			StringBuilder sb = new StringBuilder();
			String temp;
			int index;
			while ((len=reader.read(chars)) != -1) {
				temp = new String(chars, 0, len);
				if ((index = temp.indexOf("eof")) != -1) {//遇到eof时就结束接收
					sb.append(temp.substring(0, index));
		            break;
				}
				sb.append(temp);
			}
			System.out.println("from client: " + sb);
			
			
			filePath = sb.toString();
					
			System.out.println("from client: " + filePath);
			
			
			dos=new DataOutputStream(socket.getOutputStream());
			dis=new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));
			
			int buffferSize=1024;
			byte[]bufArray=new byte[buffferSize];
			File file=new File(filePath);
			dos.writeUTF(file.getName()); 
			dos.flush(); 
			dos.writeLong((long) file.length()); 
			dos.flush(); 
			while (true) { 
			    int read = 0; 
			    if (dis!= null) { 
			      read = dis.read(bufArray); 
			    } 

			    if (read == -1) { 
			      break; 
			    } 
			    dos.write(bufArray, 0, read); 
			  } 
			  dos.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { 
		      // 关闭所有连接 
		      try { 
		        if (dos != null) 
		          dos.close(); 
		      } catch (IOException e) { 
		      } 
		      try { 
		        if (dis != null) 
		          dis.close(); 
		      } catch (IOException e) { 
		      } 
		      try { 
		        if (socket != null) 
		          socket.close(); 
		      } catch (IOException e) { 
		      } 
		      try { 
		        if (ss != null) 
		          ss.close(); 
		      } catch (IOException e) { 
		      } 
		    }

	}
	public static void main(String[] args) throws IOException { 
        new TCPServer().sendFile("null", 8821); 
    } 
}
