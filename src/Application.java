
public class Application {
	public static final String baseDir = System.getProperty("user.dir") + "\\test\\";
	public static final String testCSV = baseDir + "csvFile.csv";
	public static final String coverLetterTemplate = baseDir + "template.docx";
	private static final IDGenerator generator = new IDGenerator();

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {		
		final JobQueue jobQueue = new JobQueue();
		CSVParser parser = new CSVParser(jobQueue, testCSV);
		
		DocBuilder[] builders = new DocBuilder[]{
			new DocBuilder(jobQueue, 1),
			new DocBuilder(jobQueue, 2)
		};
		
		long time0 = System.nanoTime();
		
		parser.start();
		builders[0].start();
		builders[1].start();
		
		parser.join();
		builders[0].join();
		builders[1].join();
				
		float time = (float) ((System.nanoTime() - time0)/1E+9);
		System.out.println("...COMPLETED (" + time + "s)");
	}
	
	
	public static int generateID() throws InterruptedException{
		return generator.generate();
	}

}
