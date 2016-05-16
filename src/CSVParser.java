import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;


public class CSVParser {
	private String csvFile;
	
	public CSVParser(String csvFile){
		this.csvFile = csvFile;
	}
	
	public LinkedList<HashMap<String, String>> parse() throws Exception{
		LinkedList<HashMap<String, String>> list = new LinkedList<>();
		BufferedReader reader = new BufferedReader(new FileReader(this.csvFile));
		
		HashMap<String, Integer> headMapping = this.getTableHeadMapping(reader);
		
		for(String row; (row = reader.readLine()) != null;){
			list.addLast(this.getMappingForRow(row, headMapping));
		}
		
		return list;
	}
	
	private HashMap<String, Integer> getTableHeadMapping(BufferedReader br) throws IOException{
		String[] head = br.readLine().split(",");
		
		HashMap<String, Integer> headMapping = new HashMap<>();
		
		for(int i = 0; i < head.length; ++i){
			// Our hopefully very unique variable string. It would be weird to have a document
			// with valid strings that match this.
			String key = "xxx" + head[i].replaceAll("\\s", "") + "xxx";
			
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
