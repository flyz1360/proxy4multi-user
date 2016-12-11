package client;

import java.nio.channels.*;
import java.nio.*;
import java.io.IOException;
import java.net.*;
import java.util.*;

public class TestClient implements Runnable{
	private ServerSocketChannel ssc;
	private SocketChannel server;
	private SocketChannel user;
	private Selector selector;
	
	private String serverAddr = "localhost";
	private int serverPort = 30000;
	private int portNum = 20000;
	
	public TestClient() throws Exception{
		BufferEncrypt.init();
		selector = Selector.open();
		ssc = ServerSocketChannel.open();
		server = SocketChannel.open();
		user = SocketChannel.open();
		
		ssc.socket().bind(new InetSocketAddress(portNum));
		ssc.configureBlocking(false);
		ssc.register(selector, SelectionKey.OP_ACCEPT);
		
		server.socket().connect(new InetSocketAddress(serverAddr, serverPort));
		server.configureBlocking(false);
		server.register(selector, SelectionKey.OP_READ);
	}
	
	public void ProxyToUser() throws Exception{
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		int byteRead = 0;
		while (true){
			byteRead = server.read(buffer);
			if (byteRead == 0) break;
			else if (byteRead == -1){
				server.close();
				break;
			}
		}
		if (byteRead == -1)
			return;
		System.out.println("TestClient: Read from server finished");
		
		buffer = BufferEncrypt.Decrypt(buffer);
		while (buffer.hasRemaining()){
			user.write(buffer);
		}
		System.out.println("TestClient: Write to user finished");
	}
	
	public void UserToProxy() throws Exception{
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			int byteRead = 0;
			while (true){
				byteRead = user.read(buffer);
				if (byteRead == 0)	break;
				else if (byteRead == -1){
					user.close();
					break;
				}
			}
			if (byteRead == -1)
				return;
			System.out.println("TestClient: Read from user finished ");
			
			buffer  = BufferEncrypt.Encrypt(buffer);
			while (buffer.hasRemaining()){
				server.write(buffer);
			}
			System.out.println("TestClient: Write to server finished");
	}
	
	@Override
	public void run(){
		System.out.println("TestClient");
		try{
			while(true){
				selector.select();
				Set<SelectionKey> readyKeys = selector.selectedKeys();
				Iterator<SelectionKey> it =  readyKeys.iterator();
				System.out.println("TestClient: Selecting");
				
				while (it.hasNext()){
					SelectionKey key = it.next();
					it.remove();
					
					if (key.isAcceptable()){
						user = ssc.accept();
						user.configureBlocking(false);
						user.register(selector, SelectionKey.OP_READ);
						System.out.println("TestClient: Accept user socket");
					}
					else if (key.isReadable()){
						SocketChannel sc = (SocketChannel)key.channel();
						String addr = sc.getRemoteAddress().toString();
						
						if (addr.contains(Integer.toString(serverPort)))
							ProxyToUser();
						else
							UserToProxy();
						
					}
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally{
			try{
				ssc.close();
				server.close();
				user.close();
				selector.close();
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}
}