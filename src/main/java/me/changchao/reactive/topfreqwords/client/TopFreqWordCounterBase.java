package me.changchao.reactive.topfreqwords.client;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.CommandLineRunner;

import lombok.extern.apachecommons.CommonsLog;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;

@CommonsLog
public abstract class TopFreqWordCounterBase implements CommandLineRunner {
	abstract protected Flux<String> getLines();

	@Override
	public void run(String... args) throws Exception {
		long top = 3;
		Flux<String> lines = getLines();

		// convert to words
		Flux<String> words = lines.filter(it -> StringUtils.isNotBlank(it)).flatMapIterable(this::extractWords);

		// group by words
		Flux<GroupedFlux<String, String>> wordGroups = words.groupBy(it -> it);

		// get the number of for each word
		Flux<Pair<String, Long>> wordCounts = wordGroups.flatMap(gr -> gr.count().map(cnt -> Pair.of(gr.key(), cnt)));

		// get top N words
		Flux<Pair<String, Long>> topFreqWords = wordCounts.sort((p1, p2) -> Long.compare(p2.getRight(), p1.getRight()))
				.take(top);

		// print the top N words
		wordCounts.subscribe(log::info);
	}

	private Pattern wordPattern = Pattern.compile("[\\w']+");

	/**
	 * extract all words of a text line
	 * 
	 * @param textLine
	 *            the text line
	 * @return all words
	 */
	List<String> extractWords(String textLine) {
		List<String> wordList = new ArrayList<>();
		Matcher m = wordPattern.matcher(textLine);
		while (m.find()) {
			String word = textLine.substring(m.start(), m.end());
			wordList.add(word);
		}

		return wordList;

	}

}
