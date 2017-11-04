package me.changchao.reactive.topfreqwords.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.CommandLineRunner;

import lombok.extern.apachecommons.CommonsLog;
import me.changchao.reactive.topfreqwords.WordUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;

@CommonsLog
public abstract class TopFreqWordCounterBase implements CommandLineRunner {
	abstract protected Flux<String> getLines();

	@Override
	public void run(String... args) throws Exception {
		long top = 3;
		Flux<String> lines = getLines();
		// convert to words
		Flux<String> words = lines.filter(it -> StringUtils.isNotBlank(it)).flatMapIterable(WordUtils::extractWords);

		Flux<Pair<String, MutableInt>> topWords = getTopWordsUsingCollect(top, words);

		// print the top N words
		topWords.subscribe(log::info);

	}

	/**
	 * get top frequent words using groupBy
	 * 
	 * @param top
	 *            topN
	 * @param words
	 *            words
	 * @return top frequent words
	 * @deprecated:this method just doesn't work, for the details,see <a href=
	 *                  "https://github.com/reactor/reactor-core/issues/931">
	 *                  this issue</a>
	 */
	public Flux<Pair<String, Long>> getTopWordsUsingGoupBy(long top, Flux<String> words) {
		// group by words
		Flux<GroupedFlux<String, String>> wordGroups = words.groupBy(it -> it);

		// get the number of for each word
		Flux<Pair<String, Long>> wordCounts = wordGroups.flatMap(gr -> gr.count().map(cnt -> Pair.of(gr.key(), cnt)))
				.log();

		// get top N words
		Flux<Pair<String, Long>> topFreqWords = wordCounts.sort((p1, p2) -> Long.compare(p2.getRight(), p1.getRight()))
				.log().take(top);

		return topFreqWords;

	}

	/**
	 * get top frequent words using collect
	 * 
	 * @param top
	 *            topN
	 * @param words
	 *            words
	 * @return top frequent words
	 */
	private Flux<Pair<String, MutableInt>> getTopWordsUsingCollect(long top, Flux<String> words) {
		Mono<HashMap<String, MutableInt>> wordCountMap = words.collect(() -> new HashMap<String, MutableInt>(),
				(map, word) -> {
					if (map.containsKey(word)) {
						map.get(word).increment();
					} else {
						map.put(word, new MutableInt(1));
					}
				});

		Flux<Pair<String, MutableInt>> topWords = wordCountMap.flatMapMany(map -> {

			List<Entry<String, MutableInt>> entries = new ArrayList<>(map.entrySet());
			Collections.sort(entries, (e1, e2) -> e2.getValue().compareTo(e1.getValue()));
			return Flux.fromIterable(entries).take(top);
		}).map(entry -> Pair.of(entry.getKey(), entry.getValue()));

		return topWords;
	}

}
