import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * SearchAndFilter.java
 * This class handles the searching and filtering of the 
 * data extraction algorithm. The class also has helper methods
 * for obtaining input for the search and extracting full text links
 * after the filtration process.
 * 
 * @author JeremyTien
 * @since November 6, 2017
 */
public class SearchAndFilter {
	
	/** URLs returned by the PubMed Search */
	private ArrayList<String> URLs;
	
	/** URLs to article abstracts that pass the filtration process */
	private ArrayList<String> approvedURLs;
	
	/** URLs extracted from the abstract page that contain the article full text */
	private ArrayList<String> fullTextURLs;
	
	private ArrayList<String> userInputKeywords;
	private int userInputThreshold;
	private String userInputFile;
	private String query;
	private String desiredNumResults;
	
	public static void main(String[]args)
	{
		//tester code
		SearchAndFilter runner = new SearchAndFilter();
		runner.run();
	}
	
	/**
	 * No-args constructor initializes global vars.
	 */
	public SearchAndFilter()
	{
		URLs = new ArrayList<String>();
		approvedURLs = new ArrayList<String>();
		fullTextURLs = new ArrayList<String>();
		userInputKeywords = new ArrayList<String>();
		userInputThreshold = 0;
		userInputFile = new String();
	}
	
	/**
	 * Runner method that calls input, search,
	 * filter, and extraction methods, respectively.
	 */
	public void run()
	{
		Scanner refine = new Scanner(System.in);
		String input = new String();
		do{
			getInputFromUser();
			searchForURLs(query, desiredNumResults);
			filterArticles();
			System.out.println("Would you like to refine/broaden your search query and parameters?");
			input = refine.nextLine();
		}while(input.equalsIgnoreCase("yes"));
		extractFullLinks();
	}
	
	/**
	 * Method that prompts the user for the
	 * details needed in the PubMed Search and 
	 * the various parameters for the article
	 * filtration process.
	 */
	public void getInputFromUser()
	{
		Scanner commandLine = new Scanner(System.in);
		System.out.println("Pubmed Search\n");
		System.out.print("Please input your search query (to be searched in PubMed database): ");
		query = commandLine.nextLine();
		System.out.print("Please input the amount of results (article abstracts) you would like to filter through: ");
		desiredNumResults = commandLine.nextLine();
		System.out.println("You will now input keyword parameters that will be used in the article filtration process.");
		System.out.println("Article abstracts returned by the search will be scored based on the amount of keywords contained,");
		System.out.println("and will either be accepted or rejected based on whether it meets the keyword threshold (inputted by the user).");
		System.out.println("You may also input \"mandatory\" keywords that must be present in the article abstract. Articles ");
		System.out.println("without these mandatory keywords are eliminated.");
		System.out.print("Select how you would like to input the parameter keywords to be used in filtering the articles (FILE or COMMANDLINE): ");
		String input = commandLine.nextLine();
		if(input.equalsIgnoreCase("COMMANDLINE"))
		{
			System.out.println("Please input the keyword parameters for the article filtration process - press Enter after each keyword or phrase.");
			System.out.println("Input mandatory keywords with a '*' proceeding the word or phrase, and press Q when finished: ");
			do
			{
				input = commandLine.nextLine();
				if(input.equalsIgnoreCase("Q"))
					break;
				userInputKeywords.add(input);
			}while(true);
		}
		else
		{
			System.out.println("Please input the keyword parameters for the article filtration process in an unformatted text file.");
			System.out.println("Input mandatory keywords with a '*' proceeding the word or phrase. Press Enter when finished.");
			input = commandLine.nextLine();
			System.out.print("Please enter the file path of the input file: ");
			userInputFile = commandLine.nextLine();
		}
		System.out.print("Please input the keyword threshold for the article search: ");
		userInputThreshold = commandLine.nextInt();
	}
	
	/**
	 * Method that implements NCBI's ESearch 
	 * utility and returns URL results
	 * @param query - the search query
	 * @param results - the maximum number of desired results
	 */
	public void searchForURLs(String query, String results)
	{
		System.out.println("\nSearching...\n");
		PubmedSearch engine = new PubmedSearch();
		URLs = engine.search(query, results);
		System.out.println("Number of results: " + URLs.size());
	}
	
	/**
	 * Filters articles returned by the search method.
	 * Opens files containing keyword parameters for
	 * filtering, and keeps or eliminates articles based on 
	 * the count of keyword occurrences within the abstract
	 * and the presence of all mandatory keywords.
	 */
	public void filterArticles()
	{
		System.out.println("\n\nFiltering articles...\n");
		for(int i = 0; i < URLs.size(); i++)
		{
			int keywordCount = 0;
			boolean hasAllMandatories = true;
			try 
			{  
				Document doc = Jsoup.connect(URLs.get(i)).get();
				String fileName = doc.select("title").first().text();
				System.out.println(fileName);
				String articleAbstract = doc.body().text();
				if(userInputFile.equals(""))
				{
					for(int j = 0; j < userInputKeywords.size(); j++)
					{
						if(userInputKeywords.get(j).charAt(0) == '*')
						{
							if(!articleAbstract.contains(userInputKeywords.get(j).substring(1, userInputKeywords.get(j).length())))
								hasAllMandatories = false;
						}
						if(articleAbstract.contains(userInputKeywords.get(j)))
							keywordCount++;
					}
				}
				else
				{
					Scanner readr = OpenFile.openToRead(userInputFile); 
					while(readr.hasNextLine())
					{
						String keyword = readr.nextLine();
						if(keyword.charAt(0) == '*')
						{
							if(!articleAbstract.contains(keyword.substring(1,keyword.length())))
								hasAllMandatories = false;
						}
						if(articleAbstract.contains(keyword))
							keywordCount++;
					}
				}
			} 
			catch(IOException ie) {
				ie.printStackTrace();
			}
			if(hasAllMandatories && keywordCount >= userInputThreshold)
				approvedURLs.add(URLs.get(i));
		}
		System.out.println("Number of filtered results: " + approvedURLs.size());
		
	}
	
	/**
	 * Accessor method for approvedURLs
	 * @return reference to approvedURLs
	 */
	public ArrayList<String> getApprovedURLs()
	{
		return approvedURLs;
	}
	
	/**
	 * Accessor method for fullTextURLs
	 * @return reference to fullTextURLs
	 */
	public ArrayList<String> getFullTextURLs()
	{
		return fullTextURLs;
	}
	
	/**
	 * Accesses PubMed abstract page of approvedURLs, extracts
	 * link to the full text of the article from the page.
	 */
	public void extractFullLinks()
	{
		System.out.println("\n\nFinding full text links from PubMed page...\n");
		String pubmed = "https://www.ncbi.nlm.nih.gov/pubmed/?term="; 
		String charset = "UTF-8";
		String userAgent = "ExampleBot 1.0 (+http://example.com/bot)";

		String currentTitle = "";
		for(int i = 0; i < approvedURLs.size(); i++)
		{
			try
			{
				
				Elements links = Jsoup.connect(approvedURLs.get(i)).userAgent(userAgent).get().select("a[href]"); 
				boolean toggleFullText = false;
				for (Element link : links) 
				{
				    currentTitle = link.text();

				    String url = link.absUrl("href"); // Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
				    if (!url.startsWith("http")) {
				        continue; // Ads/news/etc.
				    }
				    
					if(currentTitle.equals("LinkOut - more resources"))
						 toggleFullText = true;
					if(currentTitle.equals("PubMed Commons home"))
						toggleFullText = false;
					if(toggleFullText)
					{
						//System.out.println("Title: " + currentTitle);
						//System.out.println("URL: " + url);
						fullTextURLs.add(url);
					}
				}
			}
			catch(IOException e)
			{
				System.exit(1);
			}
		}
	}
}
