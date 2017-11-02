package me.changchao.reactive.topfreqwords;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.codec.StringDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import lombok.extern.apachecommons.CommonsLog;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;

@Component
@CommonsLog
public class TopFreqWordCounter implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		// this is the text file we want to analyze
		String txtFileUrl = "https://www.ietf.org/rfc/rfc1.txt";
		// get the top 3 frequent words
		long top = 3;

		// build the webClient
		WebClient client = this.buildWebClient(txtFileUrl);
		// get the content of the text file line by line
		Flux<String> lines = client.get().retrieve().bodyToFlux(String.class);

		// TODO: if the lines Flux is created using a String array,
		// the whole program does work
		// So I think this is related to WebClient.
//		lines = Flux.fromArray(new String[] {
//
//				"That was in the time of Burke and Fox and Rodney.",
//
//				" Spain and France and Holland had combined,",
//
//				" and in one great battle threatened to crush" });

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
		topFreqWords.subscribe(log::info);
	}

	/**
	 * build a web client
	 * 
	 * @param baseUrl
	 *            base url
	 * @return a web client
	 */
	private WebClient buildWebClient(String baseUrl) {
		Builder webClientBuilder = WebClient.builder()
				.exchangeStrategies(ExchangeStrategies.builder().codecs(clientCodecConfigurer -> {
					// We don't need the defaults
					clientCodecConfigurer.registerDefaults(false);
					// We only use StringDecoder
					StringDecoder lineDecoder = StringDecoder.textPlainOnly(true);
					clientCodecConfigurer.customCodecs().decoder(lineDecoder);
				}).build()).baseUrl(baseUrl);

		// build the web client
		return webClientBuilder.build();
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
