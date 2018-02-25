/**
 Summarizer.java
 Last update: 09-05-2017
 */
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Summarizer {
	
	private String researchPaper;
	private String title = "";
	private double paperLength;
	private ArrayList<String> paragraphs = new ArrayList();
	private ArrayList<String> sentences = new ArrayList();
	private String summary;
	private double summaryLength;
	private static final String PARAGRAPH_SPLIT_REGEX = "(?m)(?=^\\s{4})";
	
	public Summarizer() {
		
	}
	public Summarizer(String text, String title) {
		researchPaper = text;
		this.title = title;
		splitParagraphs(researchPaper);
	}
	
	public void splitParagraphs(String text) {
		String[] paragraphs = text.split(PARAGRAPH_SPLIT_REGEX);
	    for (String paragraph : paragraphs) {
	        this.paragraphs.add(paragraph);
	    }
	}
	
	public void buildSummary2() throws Exception{
		String summary = "";
		
	}
	
	public String buildSummary() throws Exception{
		String summary = "";
		for(int i = 0; i < paragraphs.size(); i++) {
			IntersectionCalculator p2 = new IntersectionCalculator(paragraphs.get(i));
			p2.addTitle(title);
			p2.updateIntersections();
			p2.calcScores();
			String sentence = p2.getSentence();
			if(sentence.indexOf("(") >= 0 && sentence.indexOf(")") >= 0)
			{
				//System.out.println(sentence);
				int index = 0;
				while(index < sentence.length())
				{
					if(sentence.indexOf("(", index) >= 0 && sentence.indexOf(")", index) >= 0 && sentence.indexOf("(", index) <= sentence.indexOf(")", index))
					{
						//System.out.println(index);
						//System.out.println(sentence.indexOf("(", index));
						//System.out.println(sentence.indexOf(")", index) + 1);
						if(sentence.substring(sentence.indexOf("(", index), sentence.indexOf(")", index) + 1).contains("et al.,") || sentence.substring(sentence.indexOf("(", index), sentence.indexOf(")", index) + 1).contains("and"))
							sentence = sentence.substring(0, sentence.indexOf("(", index)) + sentence.substring(sentence.indexOf(")",index) + 1);
						else
							index = sentence.indexOf(")", index)+1; //= sentence.indexOf(")", index) + 1
					}
					else
						index = sentence.length();
				}
			}
			summary += sentence;
		}
		//Transcribe results onto a text file
		//PrintWriter summarizer = new PrintWriter(new FileOutputStream("Summary.txt"), true);
		//summarizer.println(summary);
		//System.out.println(summary);
		return summary;
		
	}
	public ArrayList<String> getSentences(){
		return sentences;
	}
	
	public void addParagraphs(String paragraph) {
		paragraphs.add(paragraph);
	}
	
	public void addTitle(String title) {
		this.title = title;
	}
	public String getSummary(){
		return summary;
	}
	
	
	
}
