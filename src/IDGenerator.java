import java.util.concurrent.Semaphore;


public class IDGenerator {
	private int counter = 0;
	private Semaphore mutex;
	
	public IDGenerator() {
		mutex = new Semaphore(1, true);
	}
	
	public int generate() throws InterruptedException {
		mutex.acquire();
		int id = ++counter;
		mutex.release();
		
		return id;
	}
}
