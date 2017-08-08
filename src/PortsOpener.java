import com.thucloud.scholar.proxy.Client;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PortsOpener {
	public static void main(String[] args) {
		Executor e = Executors.newCachedThreadPool();
		int[] port_list = {20000, 20001, 20002, 20003, 20004, 20005};	// 可以遍历一个范围生成

		for (int i : port_list) {
			Client c = new Client(i);
			e.execute(c);
		}
	}
}
