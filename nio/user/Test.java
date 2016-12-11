package user;

public class Test{
	public static void main(String[] args) throws Exception{
		new Thread(new TestUser()).start();
	}
}