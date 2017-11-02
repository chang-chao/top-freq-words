package me.changchao.reactive.topfreqwords;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
public class WordUtils {

	/**
	 * extract all words of a text line
	 * 
	 * @param textLine
	 *            the text line
	 * @return all words
	 */
	public static List<String> extractWords(String textLine) {
		List<String> wordList = new ArrayList<>();
		Matcher m = WordUtils.wordPattern.matcher(textLine);
		while (m.find()) {
			String word = textLine.substring(m.start(), m.end());
			wordList.add(word);
		}
		log.info("text=" + textLine);
		return wordList;

	}

	public static Pattern wordPattern = Pattern.compile("[\\w']+");

}
