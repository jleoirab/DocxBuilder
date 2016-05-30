import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class CSVParser extends Thread {
	private String csvFile;
	private JobQueue queue;
	
	public CSVParser(JobQueue queue, String csvFile){
		this.csvFile = csvFile;
		this.queue = queue;
	}
	
	public void run(){
		try {
			System.out.println("Parser working");
			queue.registerWriter();
			
			BufferedReader reader = new BufferedReader(new FileReader(this.csvFile));
			HashMap<String, Integer> headMapping = this.getTableHeadMapping(reader);
			
			for(String row; (row = reader.readLine()) != null;){
				queue.addJob(this.getMappingForRow(row, headMapping));
			}
			
			queue.unregisterWriter();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private HashMap<String, Integer> getTableHeadMapping(BufferedReader br) throws IOException{
		String[] head = br.readLine().split(",");
		
		HashMap<String, Integer> headMapping = new HashMap<>();
		
		for(int i = 0; i < head.length; ++i){
			// Our hopefully very unique variable string. It would be weird to have a document
			// with valid strings that match this.
			String key = "XXX" + head[i].replaceAll("\\s", "") + "XXX";
			
			headMapping.put(key, i);
		}
		
		return headMapping;
	}
	
	private HashMap<String, String> getMappingForRow(String row, HashMap<String, Integer> headMapping){
		HashMap<String, String> mapping = new HashMap<>();
		String[] columns = row.split(",");
		
		for(String key : headMapping.keySet()){
			mapping.put(key, columns[headMapping.get(key)]);
		}
		
		return mapping;
	}

}
