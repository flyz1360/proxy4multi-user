package client;

public class Test{
	public static void main(String[] args) throws Exception{
		new Thread(new TestClient()).start();
	}
}