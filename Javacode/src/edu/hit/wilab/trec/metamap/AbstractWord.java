package edu.hit.wilab.trec.metamap;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class AbstractWord {
	
	public static void main(String[] args) {
		
		AbstractWord aw = new AbstractWord();
		aw.abstractWord("58-year-old woman with hypertension and obesity presents with exercise-related episodic chest pain radiating to the back.");
		
	}

	public void abstractWord(String topic_text) {

		// Stanford词性标注
		MaxentTagger tagger = new MaxentTagger(
				"./resource_post/english-bidirectional-distsim.tagger");
		
		stanfordPOS(topic_text, tagger);

	}

	public List<String> stanfordPOS(String content_all, MaxentTagger tagger) {

		List<String> list_words = new ArrayList<String>();

		String tagged = new String();

		try {
			tagged = tagger.tagString(content_all);
		} catch (OutOfMemoryError e) {
			// TODO: handle exception
			return null;
		}

		String[] sentences = tagged.split(" ");

		for (int i = 0; i < sentences.length; i++) {

			String sentence = sentences[i];

			System.out.println(sentence);

		}

		return list_words;

	}

}
