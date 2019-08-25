package group.analysis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.bson.BSON;
import org.bson.Document;

import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import group.analysis.TweetCleaning;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;

public class SentimentalAnalysis {
	static POSTaggerME tagger;

	static ArrayList<String> sentences = new ArrayList<String>();
	static ArrayList<String> conjunctions = new ArrayList<String>();

	static ArrayList<String> listOfNegative = new ArrayList<String>();
	static ArrayList<String> listOfPositive = new ArrayList<String>();
	static int pos = 0;
	static int neg = 0;

	public static void main(String args[]) throws IOException {
		loadModel();
		loadDictionary();

		MongoClient mongo = new MongoClient("localhost", 27017);
		MongoDatabase database = mongo.getDatabase("bigdata");
		MongoCollection<Document> collection = database.getCollection("tweets");
		MongoCollection<Document> collection_new = database.getCollection("tweets_new");
		MongoCollection<Document> collection_cleaned = database.getCollection("tweets_cleaned");

		String sentence;
		String id;

		FindIterable<Document> iterDoc = collection.find(); 
		Iterator result = iterDoc.iterator();
		while (result.hasNext()) {
			Document row = (Document) result.next();
			sentence = (String) row.get("text");
			id = (String) row.get("id_str");
			String cleaned_sentence  = TweetCleaning.perform(sentence);
			String sentiment = analyze(cleaned_sentence);
			
			Document document = new Document("id_str", id) 
					.append("text", cleaned_sentence);
			collection_cleaned.insertOne(document); 
			
			document = new Document("id_str", id) 
					.append("text", sentence)
					.append("retweeted", row.get("retweeted")) 
					.append("timestamp_ms", row.get("timestamp_ms")) 
					.append("sentiment", sentiment);
			collection_new.insertOne(document); 

		}

		System.out.println("Total number of tweets:" + collection.count());
		System.out.println("Number of Positive tweets:" + pos);
		System.out.println("Number of Negative tweets:" + neg);

		mongo.close();	
	}


	private static String analyze(String sentence) {
		String str = "";
		String result = "neutral";

		if(tagger != null) {
			String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
					.tokenize(sentence);
			String[] tags = tagger.tag(whitespaceTokenizerLine);
			for (int i = 0; i < whitespaceTokenizerLine.length; i++) {
				String word = whitespaceTokenizerLine[i].trim();
				String tag = tags[i].trim();
				if(tag.equals("CC")) {
					if(!str.isEmpty()) {
						conjunctions.add(word);
						sentences.add(str);
						str = "";
					}

				} else if(tag.startsWith("NN") || tag.startsWith("JJ") || tag.startsWith("RB")) {
					str = str + word + "  ";
				}

			}
			sentences.add(str);
			int pol = computePolarity();

			if( pol > 0 ) {
				result = "positive";
				pos++;
			} else if (pol  <0) {
				result = "negative";
				neg++;
			}
			
			sentences = new ArrayList<String>(); 
		}
		return result;
	}

	private static int computePolarity() {
		int polarity = 0;
		int totalPolarity = 0;
		boolean negate = false;
		ArrayList<String> ORList = new ArrayList(Arrays.asList("or", "yet"));
		ArrayList<String> negateList = new ArrayList(Arrays.asList("not", "don't"));

		for(int j=0; j< sentences.size(); j++) {
			String words[] = sentences.get(j).split(" ");
			for(int i=0; i< words.length; i++) {
				String word = words[i].trim();
				if(negateList.contains(word)) {
					negate = true;
				}

				if(listOfPositive.contains(word)) {
					if(negate) {
						polarity -= 1; 
					} else {
						polarity += 1;
					}
				} else if(listOfNegative.contains(word)) {
					if(negate) {
						polarity += 1; 
					} else {
						polarity -= 1;
					}
				}
			}

			if(j != 0) {
				String cc = conjunctions.get(j-1);
				if(ORList.contains(cc)) {
					totalPolarity = polarity;
				} else {
					totalPolarity += polarity;
				}
			} else {
				totalPolarity = polarity;
			}
			negate = false;
		}
		return totalPolarity;
	}


	private static void loadDictionary() throws IOException {
		BufferedReader bufReader = new BufferedReader(new FileReader("positive.txt"));
		String line = bufReader.readLine();
		while (line != null) {
			listOfPositive.add(line.trim());
			line = bufReader.readLine();
		}

		bufReader.close();

		bufReader = new BufferedReader(new FileReader("negative.txt"));
		line = bufReader.readLine();
		while (line != null) {
			listOfNegative.add(line.trim());
			line = bufReader.readLine();
		}

		bufReader.close();
	}




	public static void loadModel() {
		try {
			InputStream modelStream = new FileInputStream("en-pos-maxent.bin");
			POSModel model = new POSModel(modelStream);
			if(model != null) {
				tagger = new POSTaggerME(model);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
