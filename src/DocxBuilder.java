import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.LinkedList;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.io.SaveToZipFile;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Document;


@SuppressWarnings("deprecation")
public class DocxBuilder {
	private static final String baseDir = System.getProperty("user.dir") + "\\test\\";
	private static final String testCSV = baseDir + "csvFile.csv";
	private static final String coverLetterTemplate = baseDir + "template.docx";
	
	private static LinkedList<HashMap<String, String>> list;
	private static CSVParser parser;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO: Turn this into a reader/writer problem. 
		// That's actually the whole point of all this LOL.
		parser = new CSVParser(testCSV);
		
		try {
			list = parser.parse();
			int size = list.size();
			int index = 0;
			
			for(HashMap<String, String> mapping : list){
				updateDocument(mapping, (index + 1));
				System.out.println("SUCCESS (" + (++index) + "/" + size + ")");
			}
			
			System.out.println("...DONE");
		} catch (Exception e){
			System.out.println(e.toString());
		}

	}
	
	private static void updateDocument(HashMap<String, String> mapping, int id) throws Exception{
		String targetDocDir = baseDir + id + ".docx";
		Path template = FileSystems.getDefault().getPath(coverLetterTemplate);
		Path targetDoc = FileSystems.getDefault().getPath(targetDocDir);
		
		Files.copy(template, targetDoc, StandardCopyOption.REPLACE_EXISTING);
		
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File(targetDocDir));
		
		replacePlaceholders(wordMLPackage, mapping);
		
		SaveToZipFile saver = new SaveToZipFile(wordMLPackage);
		saver.save(targetDocDir);
	}
		
	private static void replacePlaceholders(WordprocessingMLPackage workPackage, 
			HashMap<String, String> mapping) throws Exception{	
		
		// Convert word package to string xml.
		String xml = XmlUtils.marshaltoString(workPackage.getMainDocumentPart().getJaxbElement(), true);
		
		for(String key : mapping.keySet()){
			xml = xml.replaceAll("xxx" + key + "xxx", mapping.get(key));
		}
		
		Object obj = XmlUtils.unmarshallFromTemplate(xml, null);
		workPackage.getMainDocumentPart().setJaxbElement((Document) obj);
	}

}
