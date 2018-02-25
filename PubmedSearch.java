import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.ArrayList;

import com.google.gson.Gson;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

/**
 * This class contains the search function. It utilizes
 * NCBI's ESearch to obtain the results of the PubMed Search.
 * 
 * @author JeremyTien
 * @since October 19, 2017
 */
public class PubmedSearch 
{
	public static void main(String[] args) throws IOException 
	{
		//Tester block of code
		String pubmed = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&retmode=xml&retmax=1000&term=";
		String search = "yap taz";
		String charset = "UTF-8";
		String userAgent = "ExampleBot 1.0 (+http://example.com/bot)"; // Change this to your company's name and bot homepage!
		String links = Jsoup.connect(pubmed + URLEncoder.encode(search, charset)).userAgent(userAgent).get().toString();
		System.out.println(links);
		for(int i = 0; i < links.length(); i++)
		{
			String tag = links.substring(i, i+4);
			if(tag.equals("<Id>"))
			{
				String linkNumber = links.substring(i+8, i+16);
				System.out.println("https://www.ncbi.nlm.nih.gov/pubmed/" + linkNumber);
			}
		}
		
	}
	
	/**
	 * This method calls NCBI's ESearch tool. The method fires
	 * a search query through this tool, and obtains a  returned list 
	 * of article UIDs that are used to access the article abstracts. 
	 * @param query
	 * @param results
	 * @return ArrayList<String> of article UIDs of the articles returned from the search. 
	 */
	public ArrayList<String> search(String query, String results)
	{
		ArrayList<String> out = new ArrayList<String>();
		String pubmed = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?db=pubmed&retmode=xml&retmax=" + results + "&term=";
		String charset = "UTF-8";
		String userAgent = "ExampleBot 1.0 (+http://example.com/bot)"; 
		try
		{
			String links = Jsoup.connect(pubmed + URLEncoder.encode(query, charset)).userAgent(userAgent).get().toString();
			for(int i = 0; i < links.length()-16; i++)
			{
				String tag = links.substring(i, i+4);
				if(tag.equals("<Id>"))
				{
					String linkNumber = links.substring(i+8, i+16);
					String url = "https://www.ncbi.nlm.nih.gov/pubmed/" + linkNumber;
					out.add(url);
				}
			}
		}
		catch(IOException e)
		{
			System.exit(1);
		}
		return out;
	}
}
