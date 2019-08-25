## Twitter-Sentimental-Analysis
 Tweet sentiment tagging as positive, negative or netural.
 
## Data Preprocessing
*	Cleaned all the URLS, Non-Printable characters, Mentions and Hashtags (Note: Just removed “@” and “#” since I’ve used nouns in my analysis).
* Transformed emoticons to their actual meaning.

## Analysis
*	Extracted adjectives, adverbs and nouns from the tweets using POS tagger. (Relying on Bag of Words concept)
* If a sentence had any conjunctions, then divided the sentence using either “and” or “or” based on the CC word.
*	Assigned polarity to each word in the sentence based on the associated sentiment. Sentiment is computed from the list of already derived lexicon [1].
*	Finally, if any CC exists, then performed the respective “and” or “or” operation to aggregate the result.

## Libraries 
* Open NLP - used for [tokenizing](https://opennlp.apache.org/docs/1.9.1/manual/opennlp.html#tools.tokenizer.introduction) and [POS](https://opennlp.apache.org/docs/1.9.1/manual/opennlp.html#tools.postagger) tagging.
* emoji4j - used to convert [emoticons](https://github.com/kcthota/emoji4j) to its equivalent word.

## Observation
226 tweets out of 1000 has been tagged as positive and 331 were tagged as negative. The resultant set had no false positives and false negatives. But many tweets has been mis-classified as netural. This in turn can be rectified by adding a polarity value for each positive and negative word.

## References
1.	Breen, J. (2011, July 13). Jeffreybreen/twitter-sentiment-analysis-tutorial-201107. Retrieved from https://github.com/jeffreybreen/twitter-sentiment-analysis-tutorial-201107/
2.	Donkor, B. (2014, November 23). Sentiment Analysis: Why It's Never 100% Accurate. Retrieved from https://brnrd.me/posts/sentiment-analysis-never-accurate
3.	Khong, W., Soon, L., Goh, H., & Haw, S. (2018). Leveraging Part-of-Speech Tagging for Sentiment Analysis in Short Texts and Regular Texts. Semantic Technology Lecture Notes in Computer Science,182-197. doi:10.1007/978-3-030-04284-4_13
4.	Hu, M., & Liu, B. (2004). Mining and summarizing customer reviews. Proceedings of the 2004 ACM SIGKDD International Conference on Knowledge Discovery and Data Mining - KDD 04. doi:10.1145/1014052.1014073
5.	Liu, B. (2012). Sentiment Analysis and Opinion Mining. Synthesis Lectures on Human Language Technologies,5(1), 1-167. doi:10.2200/s00416ed1v01y201204hlt016
