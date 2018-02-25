import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Iterator;
import java.util.List;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.awt.Desktop;
import java.nio.file.*;
import java.util.stream.Stream;

/**
 * KeywordAlgoJava.java
 * This is the main runner class for the Keyword-based PubMed Journal
 * Data Extration tool. This class handles user input regarding file input and
 * output, reads in the appropriate files for user input, and creates and writes
 * to the appropriate output files. The user-input files are plain text (.txt) files, 
 * and the output file is an Excel spreadsheet (.xlsx).
 * This class will make instances of and call methods from the classes
 * that implement the search, filtration, extraction, and categorization 
 * functions of this algorithm. 
 * 
 * @author Jeremy Tien
 * @since November 12, 2017
 */
public class KeywordAlgoJava 
{	
	/** Global variables for output */
	private Desktop dt;
	private XSSFWorkbook workbook;
	private XSSFSheet worksheet;
	private String outputFilePath; // /Users/JeremyTien/Documents/RMP_2017/CollectedData.xlsx
	
	/** Global variables for input */
	private ArrayList<String> categoryNames;
	private ArrayList<String> parameterFilePaths;
	
	/**
	 * No- args constructor, initializes global vars. 
	 */
	public KeywordAlgoJava()
	{
		dt = Desktop.getDesktop();
		workbook = new XSSFWorkbook();
		worksheet = workbook.createSheet();
		categoryNames = new ArrayList<String>();
		parameterFilePaths = new ArrayList<String>();
	}
	
	/**
	 * Prompts the user to enter the categories they 
	 * would like to sort the data, and the keyword parameters
	 * that correspond to each cateogory.
	 */
	public void getInput()
	{
		Scanner line = new Scanner(System.in);
		String input = new String();
		System.out.println("Please input the full file path of the Excel spreadsheet (.xls or .xlsx) you would like the algorithm to output the results to:");
		outputFilePath = line.nextLine();
		do
		{
			System.out.println("Please input the category of information you would like filtered below (press Q when finished): ");
			input = line.nextLine();
			if(!(input.equalsIgnoreCase("Q")))
			{
				categoryNames.add(input);
				System.out.println("Please input the keyword parameters of the category in a text file and enter the file path below: ");
				input = line.nextLine();
				parameterFilePaths.add(input);
			}
		}
		while(!(input.equalsIgnoreCase("Q")));
	}
	
	/**
	 * Creates the Excel output spreadsheet, populates columns
	 * with appropriate headings. 
	 */
	public void createSpreadsheet()
	{
		Row row = worksheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue("ARTICLE NAME");
		for(int i = 0; i < categoryNames.size(); i++)
		{
			cell = row.createCell(i+1);
			cell.setCellValue(categoryNames.get(i).toUpperCase());
		}
		cell = row.createCell(categoryNames.size()+1);
		cell.setCellValue("SUMMARY");
		cell = row.createCell(categoryNames.size()+2);
		cell.setCellValue("URLS");
	}
	
	/**
	 * Calls the class that contains the searching and filtering
	 * functions, and returns the list of URLs to the articles that 
	 * pass the filter process. 
	 * @return an ArrayList<String> of the URLs to the full text of the articles that have been filtered
	 */
	public ArrayList<String> search()
	{
		SearchAndFilter saf = new SearchAndFilter();
		saf.run();
		return saf.getFullTextURLs();
	}
	
	/**
	 * Calls the class that contains the functions for extracting 
	 * and categorizing the data from a journal's text 
	 * based on the user-input parameters, and 
	 * writes the data to the output Excel spreadsheet.
	 * Only url or path should be input as parameters, the other should be initialized as 
	 * null; only a url or a local filepath is necessary to access the full text. 
	 * @param url String that represents the URL to the full text of the journal
	 * @param path String that represents the local computer filepath to a PDF of the journal
	 * @param row int representing the current row of the Excel spreadsheet output
	 */
	public void createEntry(String url, String path, int row)
	{
		FileEntry fe = new FileEntry(parameterFilePaths, url, path, row);
		fe.openText();
		fe.parseText();
		fe.write(outputFilePath, workbook, worksheet);
	}
	
	/**
	 * Opens the Excel spreadsheet output. 
	 */
	public void displaySpreadsheet()
	{
        try 
        {
	        dt.open(new File(this.outputFilePath));
	    } 
        catch (IOException ex) 
        {
	        ex.printStackTrace();
	    }
	}
	
	/**
	 * Catch-all loop that allows user to enter URLs or PDFs of full text articles
	 * "manually," through either inputting the URLs to the full text or the 
	 * local computer file path to a PDF of the full text into a plain text file.
	 * These are then processed and added into the final Excel spreadsheet output. 
	 * @param urls
	 */
	public void postProcessing(ArrayList<String> urls)
	{
		Scanner line = new Scanner(System.in);
		int count = urls.size()*2 + 1;
		do
		{
			System.out.println("You may now input any URLs or PDFs of articles that you wish to process (that may have been missed in the searching process). Press Enter if you would like to continue. Press Q if you wish to quit the program.");
			if(line.nextLine().equalsIgnoreCase("q"))
				break;
			System.out.println("How would you like to input the articles? You may either input the URL to the full text of the article, or a PDF of the full text. Enter URL or PDF below to select the respective input option:");
			String inputType = line.nextLine();
			//URL input
			if(inputType.equalsIgnoreCase("URL"))
			{
				System.out.println("Please input the URLs to the full text of the articles in an unformatted text file (new line after each one):");
				System.out.println("Press Enter when finished.");
				line.nextLine();
				System.out.println("Please enter the complete file path of the text file below: ");
				String filepath = line.nextLine();
				Scanner urlReader = OpenFile.openToRead(filepath);
				while(urlReader.hasNext())
				{
					String url = urlReader.nextLine();
					createEntry(url, null, count);
					count += 2;
				}	
			}
			//PDF input
			else if(inputType.equalsIgnoreCase("PDF"))
			{
				System.out.println("You may now input PDFs either by entering individual filepaths or by specifying their common parent directory.");
				System.out.println("Please enter method of input below (FILEPATH or DIRECTORY):");
				if(line.nextLine().equalsIgnoreCase("FILEPATH"))
				{
					System.out.println("Please input the full file paths to the PDFs of the full text of the articles in an unformatted text file (new line after each one):");
					System.out.println("Press Enter when finished.");
					line.nextLine();
					System.out.println("Please enter the complete file path of the text file below: ");
					String filepath = line.nextLine();
					Scanner pdfReader = OpenFile.openToRead(filepath);
					while(pdfReader.hasNext())
					{
						createEntry(null, pdfReader.nextLine(), count);
						count += 2;
					}
				}
				else
				{
					System.out.println("Please input the full path of the common parent directory:");
					String directory = line.nextLine();
					
					try (Stream<Path> paths = Files.walk(Paths.get(directory))) {
					    Object[] filepaths = paths.filter(Files::isRegularFile).toArray();
					    for(int i = 0; i < filepaths.length; i++) {
					    	if(filepaths[i].toString().endsWith(".pdf"))
					    	{
					    		System.out.println(filepaths[i]);
						    	createEntry(null, filepaths[i].toString(), count);
								count += 2;
					    	}
					    }
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("Was not able to find " + directory);
						e.printStackTrace();
					}
				}
			}				
			displaySpreadsheet();
			System.out.println("Are you satisfied with the entries?");
		}while(line.nextLine().equalsIgnoreCase("no"));
	}
	
	/**
	 * Runner method that calls all the above methods.
	 */
	public void run()
	{	
		Scanner line = new Scanner(System.in);
		String input;
		System.out.println("Would you like to skip the searching process (PubMed) and jump to manual file input?");
		input = line.nextLine();
		ArrayList<String> urls = new ArrayList<String>();
		if(input.equalsIgnoreCase("no"))
			urls = search();
		getInput();
		createSpreadsheet();
		for(int i = 0; i < urls.size(); i++)
		{
			createEntry(urls.get(i), null, i*2 + 1);
		}
		displaySpreadsheet();
		postProcessing(urls);
	}
	
	/**
	 * Main, creates instance of the class and runs the program. 
	 * @param args
	 */
	public static void main(String[]args)
	{
		KeywordAlgoJava runner = new KeywordAlgoJava();
		runner.run();
	}
}
