package me.changchao.reactive.topfreqwords;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import io.reactivex.Observable;
import io.reactivex.observables.GroupedObservable;
import lombok.extern.apachecommons.CommonsLog;

@Component
@CommonsLog
public class TestRxJavaGroupBy implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		// read a text file line by line
		int top = 3;
		try (Stream<String> stream = Files.lines(Paths.get(TopFreqWordsApplication.INPUT_TXT_FILE));) {

			Observable<String> lines = Observable.fromIterable(stream::iterator);
			// convert to words
			Observable<String> words = lines.filter(it -> StringUtils.isNotBlank(it))
					.flatMapIterable(WordUtils::extractWords);
			// group by words
			Observable<GroupedObservable<String, String>> wordGroups = words.groupBy(it -> it);

			// get the number of for each word
			Observable<Pair<String, Long>> wordCounts = wordGroups
					.flatMap(group -> group.count().toObservable().map(count -> Pair.of(group.getKey(), count)));

			// get top N words
			Observable<Pair<String, Long>> topFreqWords = wordCounts
					.sorted((p1, p2) -> Long.compare(p2.getRight(), p1.getRight())).take(top);

			// print the top N words
			topFreqWords.subscribe(log::info);

		}
	}

}
