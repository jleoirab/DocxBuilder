import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.io.SaveToZipFile;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Document;


@SuppressWarnings("deprecation")
public class DocBuilder extends Thread {
	private JobQueue queue;
	private Path templateCopy;
	private boolean started = false;
	private boolean finished = true;
	private int id;
	
	public DocBuilder(JobQueue queue, int id){
		this.queue = queue;
		this.id = id;
		
		String templateCopyUrl = "template_copy_" + id + ".docx"; 
		
		try {
			this.templateCopy = this.copyFile(Application.coverLetterTemplate, templateCopyUrl);
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void run(){
		started = true;
		try {
			for (;;){	
				if (queue.jobsAreCompleted()) break;
		
				HashMap<String, String> mapping;
				if (queue.size() > 0){
					mapping = queue.getJob();
					buildDocument(mapping);	
				}
			}
			
			Files.delete(templateCopy);	
		} catch (Exception e){
			e.printStackTrace();
		}
		
		System.out.println("[" + this.id + "]: Finished");
		finished = true;
	}
	
	public boolean hasCompletedWork(){
		return started && finished;
	}
	
	private void buildDocument(HashMap<String, String> mapping) {
		try {
			int id = Application.generateID();
			String targetDocDir = Application.baseDir + id + ".docx";
			
			copyFile(null, targetDocDir);
			
			WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File(targetDocDir));
			
			replacePlaceholders(wordMLPackage, mapping);
			
			SaveToZipFile saver = new SaveToZipFile(wordMLPackage);
			saver.save(targetDocDir);
			
			System.out.println("[" + this.id + "]: Finished building document " + id);

		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private void replacePlaceholders(WordprocessingMLPackage workPackage, 
			HashMap<String, String> mapping) throws Exception{	
		
		// Convert word package to string xml.
		String xml = XmlUtils.marshaltoString(workPackage.getMainDocumentPart().getJaxbElement(), true);
		
		for(String key : mapping.keySet()){
			xml = xml.replaceAll(key, mapping.get(key));
		}
		
		Object obj = XmlUtils.unmarshallFromTemplate(xml, null);
		workPackage.getMainDocumentPart().setJaxbElement((Document) obj);
	}
	
	private Path copyFile(String targetUrl, String destinationUrl) throws IOException{
		Path target = (targetUrl == null) ? templateCopy : FileSystems.getDefault().getPath(targetUrl);
		Path destination = FileSystems.getDefault().getPath(destinationUrl);
		
		Files.copy(target, destination, StandardCopyOption.REPLACE_EXISTING); 
		
		return destination;
	}
}
