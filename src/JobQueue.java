import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;


public class JobQueue {
	private LinkedList<HashMap<String, String>> queue;
	private Semaphore mutex;
	private int numberOfWriters = 0;
	private boolean writersUnregistered = false;
	
	public JobQueue(){
		mutex = new Semaphore(1, true);
		queue = new LinkedList<>();
	}

	public boolean jobsAreCompleted(){
		return writersUnregistered && (0 == this.size());
	}
	
	public int size(){
		return queue.size();
	}
	
	public void registerWriter() throws InterruptedException {
		mutex.acquire();
		numberOfWriters++;
		mutex.release();
	}
	
	public void unregisterWriter() throws InterruptedException {
		mutex.acquire();
		numberOfWriters--;
		writersUnregistered = (numberOfWriters == 0);
		mutex.release();
	}
	
	public void addJob(HashMap<String, String> job) throws InterruptedException {
		mutex.acquire();
		queue.addLast(job);
		mutex.release();
	}
	
	public HashMap<String, String> getJob() throws InterruptedException {
		mutex.acquire();
		HashMap<String, String> job = queue.removeFirst();
		mutex.release();
		
		return job;
	}
}
