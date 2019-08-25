package group.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import emoji4j.EmojiUtils;


public class TweetCleaning {

	public static void main(String args[]) {
		String sentence="Inna Lillahi wa inna ilayhi raji\u0027un\n\n😭💔💔💔💔💔💔💔💔";
		System.out.println(perform(sentence));
	}

	public static String perform(String tweet) {
		String newData  = EmojiUtils.shortCodify(tweet);
		newData = newData.replaceAll("[#@]","");
		newData = newData.replaceAll("[^\\p{ASCII}]|[\\r\\n]|:"," ");

		String urlregex = "((https?|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";

		Pattern urlPattern = Pattern.compile(urlregex, Pattern.CASE_INSENSITIVE);
		Matcher urlMatcher = urlPattern.matcher(newData);

		while (urlMatcher.find()) {
			newData = newData.replace(urlMatcher.group(), "");
		}


		String transformedTweet = newData;
		return transformedTweet.toLowerCase();
	}
}
