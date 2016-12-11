package server;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class BufferEncrypt {
	public static long SEED;
	
	public static void init(){
		SEED = 1234567;
		OutputControl.genMap();
		InputControl.genMap();
	}
	
	public static ByteBuffer Encrypt(ByteBuffer input){
		byte[] input_bytes = input.array();
		byte[] output_bytes = new byte[input_bytes.length];
		OutputControl writer = new OutputControl();
		
		for (int i = 0;i < input_bytes.length;i ++)
			output_bytes[i] = writer.write(input_bytes[i]);
		
		ByteBuffer output = ByteBuffer.wrap(output_bytes);
		return output;
	}
	
	public static ByteBuffer Decrypt(ByteBuffer input){
		byte[] input_bytes = input.array();
		byte[] output_bytes = new byte[input_bytes.length];
		InputControl reader = new InputControl();
		
		for (int i = 0;i < input_bytes.length;i ++)
			output_bytes[i] = reader.read(input_bytes[i]);
		
		ByteBuffer output = ByteBuffer.wrap(output_bytes);
		return output;
	}
}

class InputControl{
	static final byte[] MAP = new byte[256];
	static final byte[] MASK = new byte[256];
	private int pos = 0;
	
	public static void genMap(){
		byte[] from = OutputControl.MAP;
		for (int i = 0;i < MAP.length;i ++){
			MAP[from[i] & 0xff] = (byte)i;
		}
		System.arraycopy(OutputControl.MASK, 0, MASK, 0, MASK.length);
	}
	
	public byte read(int b){
		int r = MAP[(b ^ MASK[pos]) & 0xff] & 0xff;
		pos = (pos + 1) % MASK.length;
		return (byte)r;
	}
}

class OutputControl{
	static final byte[] MAP = new byte[256];
	static final byte[] MASK = new byte[256];
	private int pos = 0;
	
	public static void genMap(){
		Random rand = new Random(BufferEncrypt.SEED);
		for (int i = 0;i < MAP.length; i++){
			MAP[i] = (byte)i;
		}
		for (int i = 0;i < MAP.length; i++){
			int k = rand.nextInt(MAP.length-i) + i;
			byte t = MAP[i];
			MAP[i] = MAP[k];
			MAP[k] = t;
		}
		for(int i = 0;i < MASK.length; i++){
			MASK[i] = (byte)rand.nextInt(256);
		}
	}
	
	public byte write(int b){
		int r = (MAP[b & 0xFF] ^ MASK[pos]) & 0xFF;
		pos = (pos + 1) % MASK.length;
		return (byte)r;
	}
	
}