/**
 IntersectionCalculator.java
 Last update: 09-05-2017
 */
import java.awt.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;
import java.awt.*;
import edu.stanford.nlp.*;
import java.text.BreakIterator;



public class IntersectionCalculator {
	
	private ArrayList<String> sentences = new ArrayList();
	private double[] sentenceScores;
	private double[][] intersections;
	private String sentence;
	private String title = "";
	
	public IntersectionCalculator(String paragraph){
		splitSentences(paragraph);
	}
	
	//Splits a paragraph into a sentence
	public void splitSentences(String paragraph){		
		Pattern re = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE | Pattern.COMMENTS);
		Matcher reMatcher = re.matcher(paragraph);
		while(reMatcher.find()){
			sentences.add(reMatcher.group());
		}
		//System.out.println("sentences.size() = " + sentences.size());
	}
	
	//Compares two sentences and determines how many words they have in common
	//Then averages it out based on sentence length
	public double intersection(String s1, String s2) throws Exception{
		double avgLength = (s1.length() + s2.length()) / 2;
		Preprocessor wordz = new Preprocessor();
		ArrayList<String> s1words = wordz.lemmatize(s1);
		ArrayList<String> s2words = wordz.lemmatize(s2);
		double count = 0;
		for(int i = 0; i < s1words.size(); i++){
			for(int j = 0; j < s2words.size(); j++){
				if(s1words.get(i).equals(s2words.get(j))) count++;
			}
		}
		
		double score = count / avgLength;
		return score;
	}
	
	public ArrayList<String> tokenize(String text) {
	    ArrayList<String> words = new ArrayList<String>();
	    BreakIterator breakIterator = BreakIterator.getWordInstance();
	    breakIterator.setText(text);
	    int lastIndex = breakIterator.first();
	    while (BreakIterator.DONE != lastIndex) {
	        int firstIndex = lastIndex;
	        lastIndex = breakIterator.next();
	        if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(text.charAt(firstIndex))) {
	            words.add(text.substring(firstIndex, lastIndex));
	        }
	    }

	    return words;
	}
	
	public void updateIntersections() throws Exception{
		intersections = new double[sentences.size()][sentences.size()];
		for(int i = 0; i < intersections.length; i++) {
			for(int j = 0; j < intersections.length; j++){
				if(i == j) {
					intersections[i][j] = intersection(sentences.get(i), title);
				}else {
					intersections[i][j] = intersection(sentences.get(i), sentences.get(j));
				}
			}
		}
	}
	
	public void calcScores(){
		sentenceScores = new double[sentences.size()];
		for(int i = 0; i < sentenceScores.length; i++) {
			double totalScore = 0;
			for(int j = 0; j < intersections[0].length; j++) {
				totalScore = totalScore + intersections[i][j];
			}
			sentenceScores[i] = totalScore;
		}
	}
	
	public ArrayList<String> getSentences(){
		return sentences;
	}
	
	public double[][] getIntersections(){
		return intersections;
	}
	
	public double[] getSentenceScores() {
		return sentenceScores;
	}
	
	//Finds the sentence with the highest score, currently in a dumbed down method
	public String getSentence() {
		//double maxVal= sentenceScores[0];
		int maxPlace = 0;
		for(int i = 0; i < sentenceScores.length; i++) {
			if(sentenceScores[i] > sentenceScores[maxPlace]) {
				//maxVal = sentenceScores[i];
				maxPlace = i;
			}
		}
		sentence = sentences.get(maxPlace);
		return sentence;
	}
	
	public void addTitle(String title) {
		this.title = title;
	}
	
}
