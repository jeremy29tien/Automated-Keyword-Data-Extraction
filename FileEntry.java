import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.net.URL;

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

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

/**
 * FileEntry.java
 * This class contains methods for opening the full text of the journal
 * from a URL or PDF, parsing that text for data and categorizing it, and 
 * writing it to the Excel spreadsheet output. 
 * 
 * @author Jeremy Tien
 * @since November 12, 2017
 */
public class FileEntry 
{
	/** Global variable that contains the file paths to the keyword parameters */
	private ArrayList<String> parameterFilePaths;
	
	/** Global variable that contains the sentences of data that correspond to each category. */
	private ArrayList<ArrayList<String>> infoCategories;
	
	private String summary;
	
	//private String inputFilePath = "/Users/JeremyTien/Documents/workspace/KeywordAlgoJava/src/in.txt";
	
	/** Global variable descriptors of the file being read */
	private String fileName;
	private String fileURL;
	private String filePath;
	
	private int rowCount;
	
	private String fullTexts = "";
	
	private boolean isFullText = false;
	private int wordCount = 0;
	
	/**
	 * Constructor initializes global variables. 
	 * @param files
	 * @param url
	 * @param path
	 * @param rc
	 */
	public FileEntry(ArrayList<String> files, String url, String path, int rc)
	{
		parameterFilePaths = files;
		infoCategories = new ArrayList<ArrayList<String>>();
		for(int i = 0; i < parameterFilePaths.size(); i++)
		{
			infoCategories.add(new ArrayList<String>());
		}
		summary = new String();
			
		fileURL = url;
		filePath = path;
		rowCount = rc;
	}
	
	/**
	 * This method opens the full text of the journal. If a URL is provided,
	 * the URL is accessed and the full text obtained from there. If a file path
	 * to a PDF on the local computer is provided, the PDF is accessed and the 
	 * full text obtained. The full text is saved in in.txt.
	 */
	public void openText() 
	{		
		if(fileURL != null)
		{
			System.out.println(fileURL);
			try {
			      Document doc = Jsoup.connect(fileURL).get();
			      Elements paragraphs = doc.select("p");
			      ArrayList<String> sendToSummary = new ArrayList<String>();
			      for(Element paragraph : paragraphs)
			      {
			    	  fullTexts += paragraph.text();
			    	  sendToSummary.add(paragraph.text());
			      }
			      fileName = doc.select("title").first().text();
			      //SummaryRunner sum = new SummaryRunner();
			      //summary = sum.run(fileName, sendToSummary);
			} 
			catch(IOException ie) {
		            ie.printStackTrace();
		    }
		}
		else if(filePath != null)
		{
			PDFReader pdfr = new PDFReader();
			String text = pdfr.pdfToText(filePath);
			fullTexts += text;
			/* summary not working perfectly for PDF input yet
			ArrayList<String> sendToSummary = new ArrayList<String>();
			int paragraphCounter = 0;
			int paragraphBegin = 0;
			for(int i = 0; i < fullTexts.length(); i++)
			{
				sendToSummary.add("");
				sendToSummary.set(paragraphCounter, fullTexts.substring(paragraphBegin, i));
				if(fullTexts.charAt(i) == '\n') {
					paragraphCounter++;
					paragraphBegin = i+1;
				}
			}*/
			fileName = pdfr.getTitle();
			//SummaryRunner sum = new SummaryRunner();
		    //summary = sum.run(fileName, sendToSummary);
		}
	}
	
	/**
	 * Method that extracts and categorizes the sentences 
	 * of data from the full-text articles. 
	 * This method scans the full text of the journal sentence by 
	 * sentence, and classifies a sentence under the several categories provided 
	 * by the user. This is done based on keyword-parameter matches in the sentence.
	 * Also, this method checks to see if the text being processed is the actual
	 * full text of the journal.
	 */
	public void parseText()
	{
		String sentence = new String();
		boolean containsMethod = false;
		boolean containsMaterial = false;
		int index = 0;
		fullTexts = fullTexts.trim();
		while(index < fullTexts.length() && fullTexts.indexOf(" ", index+1) >=0 )
		{
			String nextWord = fullTexts.substring(index, fullTexts.indexOf(" ", index+1));
			index += nextWord.length()+1; 
			sentence += " " + nextWord;
			wordCount++;
			if(nextWord.contains("method"))
				containsMethod = true;
			if(nextWord.contains("material"))
				containsMaterial = true;
			if(nextWord.charAt(nextWord.length()-1) == '.')
			{
				for(int i = 0; i < parameterFilePaths.size(); i++)
				{
					Scanner key = OpenFile.openToRead(parameterFilePaths.get(i));
					while(key.hasNext())
					{
						if(sentence.contains(key.nextLine()))
						{
							infoCategories.get(i).add(sentence);
							break; //test so that the sentences dont repeat
						}
					}
				}
				
				sentence = new String();
			}
		}
		if(containsMethod && containsMaterial && wordCount > 1000)
			isFullText = true;
	}
	
	/**
	 * Writes the categorized data into their respective columns.
	 * If the text processed was not the full text, a WARNING
	 * label is printed next to the processed result of the entry
	 * @param path
	 * @param wb
	 * @param ws
	 */
	public void write(String path, XSSFWorkbook wb, XSSFSheet ws)
	{
		Row row = ws.createRow(rowCount);
		Cell cell = row.createCell(0);
		cell.setCellValue(fileName);
		
		String str = "";
		for(int i = 0; i < infoCategories.size(); i++)
		{
			str = "";
			for(int j = 0; j < infoCategories.get(i).size(); j++)
				str += infoCategories.get(i).get(j) + "\n";
			cell = row.createCell(i+1);
			cell.setCellValue(str);
		}
		
		str = "";
		cell = row.createCell(parameterFilePaths.size()+1);
		//System.out.println(summary);
		//cell.setCellValue(summary);
		cell = row.createCell(parameterFilePaths.size()+2);
		cell.setCellValue(fileURL);
		if(!isFullText)
		{
			cell = row.createCell(parameterFilePaths.size()+3);
			cell.setCellValue("WARNING: May not be analysis of full text (was not able to access).");
		}
		cell = row.createCell(parameterFilePaths.size()+4);
		cell.setCellValue("Word Count = " + wordCount);
        try  
        {
        	FileOutputStream outputStream = new FileOutputStream(path);
            wb.write(outputStream);
            outputStream.close();
        } 
        catch (IOException e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
