package server;

// TestServer.java
// Server read from clients, decrypt this message
// Then append msgCount 
//

import java.nio.channels.*;
import java.nio.*;
import java.io.IOException;
import java.net.*;
import java.util.*;

public class TestServer implements Runnable{
	private int portNum = 30000;
	private ServerSocketChannel ssc = null;
	private Selector selector;
	
	public TestServer(){
		BufferEncrypt.init();
		try{
			selector = Selector.open();
			ssc = ServerSocketChannel.open();
			ssc.socket().bind(new InetSocketAddress(portNum));
			ssc.configureBlocking(false);
			ssc.register(selector, SelectionKey.OP_ACCEPT);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void run(){
		System.out.println("TestServer");
		try{
			while(true){
				selector.select();
				Set<SelectionKey> readyKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = readyKeys.iterator();
				
				System.out.println("TestServer: selecting");
				
				while(it.hasNext()){
					SelectionKey key = (SelectionKey)it.next();
					it.remove();
					
					if (key.isAcceptable()){
						SocketChannel sc = ssc.accept();
						sc.configureBlocking(false);
						sc.register(selector, SelectionKey.OP_READ);
						System.out.println("TestServer: accept client socket");
					}
					else if (key.isReadable()){
						SocketChannel sc = (SocketChannel) key.channel();
						ByteBuffer buffer = ByteBuffer.allocate(1024);
						
						int byteRead = 0;
						while (true){
							byteRead = sc.read(buffer);
							if (byteRead == 0) break;
							else if (byteRead == -1) {
								sc.close();
								break;
							}
						}
						if (byteRead == -1) 
							continue;
						
						buffer = BufferEncrypt.Decrypt(buffer);
						System.out.println("Decrypt: " + new String(buffer.array()));
						
						// transfer to proxy machine
						buffer = BufferEncrypt.Encrypt(buffer);
						
						while (buffer.hasRemaining()){
							sc.write(buffer);
						}
					}
				}
			}
		} 
		catch (Exception e){
			e.printStackTrace();
		} 
		finally{
			try{
				ssc.close();
				selector.close();
			} 
			catch (IOException e){
				e.printStackTrace();
			}
		}
	}
}
