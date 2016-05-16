# DocxBuilder (WIP)

DocxBuilder is a Java application to automate the process of creating multiple Microsoft word documents from a single document template while updating variables in the template with values from a CSV file.

The test csv file has been autogenerated using [Mockaroo](https://www.mockaroo.com/)
Parsing of Microsoft Word Documents has been done in part using [Docx4j](http://www.docx4java.org/) I hope to do this part myself soon. It will be a nice challenge!


## Use
Right now, there is no great way to make use of this application. (That's why it's a WIP). For now, you can import it to eclipse and run as a Java Application to see it do its work.

## Next Steps
- Harness the powers of concurrency. See src/DocxBuilder.java for more details
- Make the program executable on a terminal