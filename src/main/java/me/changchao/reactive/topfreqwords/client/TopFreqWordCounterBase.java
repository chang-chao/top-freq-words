package me.changchao.reactive.topfreqwords.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.CommandLineRunner;

import lombok.extern.apachecommons.CommonsLog;
import me.changchao.reactive.topfreqwords.WordUtils;
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
		Flux<String> words = lines.filter(it -> StringUtils.isNotBlank(it)).flatMapIterable(WordUtils::extractWords);

		// group by words
		Flux<GroupedFlux<String, String>> wordGroups = words.groupBy(it -> it);

		// get the number of for each word
		Flux<Pair<String, Long>> wordCounts = wordGroups.flatMap(gr -> gr.count().map(cnt -> Pair.of(gr.key(), cnt)))
				.log();

		// get top N words
		Flux<Pair<String, Long>> topFreqWords = wordCounts.sort((p1, p2) -> Long.compare(p2.getRight(), p1.getRight()))
				.log().take(top);

		// print the top N words
		topFreqWords.subscribe(log::info);
	}

}
