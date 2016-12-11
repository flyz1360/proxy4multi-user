package user;

import java.nio.channels.*;
import java.nio.*;
import java.util.*;
import java.net.*;

public class TestUser implements Runnable{
	public String serverAddr = "localhost";
	public int serverPort = 20000;
	private SocketChannel sc;
	private Scanner scanner;
	
	public TestUser() throws Exception{
		sc = SocketChannel.open();
		sc.socket().connect(new InetSocketAddress(serverAddr, serverPort));
		
		scanner = new Scanner(System.in);
	}
	
	@Override
	public void run(){
		System.out.println("TestUser");
		try{
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			while (true){
				String msg = scanner.nextLine();
				buffer = ByteBuffer.wrap(msg.getBytes());
				while (buffer.hasRemaining()){
					sc.write(buffer);
				}
				System.out.println("TestUser: Write to server finished");
				
				buffer.clear();
				int byteRead = 0;
				while (true){
					byteRead = sc.read(buffer);
					System.out.println(byteRead);
					
					if (byteRead == 0)	break;
					else if (byteRead == -1){
						sc.close();
						break;
					}
				}
				if (byteRead == -1)
					return;
				buffer.flip();
				msg = new String(buffer.array());
				System.out.println("TestUser: Msg from server: " + msg);
			}
		}
		catch (Exception e){
			System.out.println("TestUser: Error running TestUser.java");
		}
		finally{
			try{
				sc.close();
				scanner.close();
			}
			catch (Exception e){
				System.out.println("TestUser: Error closing SocketChannel and Scanner");
			}
		}
	}
}
