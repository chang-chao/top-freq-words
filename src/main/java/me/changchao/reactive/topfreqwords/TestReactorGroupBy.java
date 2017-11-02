package me.changchao.reactive.topfreqwords;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.BaseStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.extern.apachecommons.CommonsLog;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;

//@Component
@CommonsLog
public class TestReactorGroupBy implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		// read a text file line by line
		Flux<String> lines = fromPath(Paths.get(TopFreqWordsApplication.INPUT_TXT_FILE));

		// convert to words
		Flux<String> words = lines.filter(it -> StringUtils.isNotBlank(it)).flatMapIterable(WordUtils::extractWords);

		// group by words
		Flux<GroupedFlux<String, String>> wordGroups = words.groupBy(it -> it);

		// !!the line below gets blocked forever!!
		log.info(wordGroups.count().block());
	}

	private Flux<String> fromPath(Path path) {
		return Flux.using(() -> Files.lines(path), Flux::fromStream, BaseStream::close);
	}

}
