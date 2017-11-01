package me.changchao.reactive.topfreqwords;

import java.util.Arrays;

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
		log.info("start");

		String txtFileUrl = "http://norvig.com/big.txt";

		txtFileUrl = "https://www.ietf.org/rfc/rfc1.txt";

		long top = 3;

		Builder webClientBuilder = WebClient.builder()
				.exchangeStrategies(ExchangeStrategies.builder().codecs(clientCodecConfigurer -> {
					// We don't need the defaults
					clientCodecConfigurer.registerDefaults(false);
					// We only use StringDecoder
					StringDecoder lineDecoder = StringDecoder.textPlainOnly(true);
					clientCodecConfigurer.customCodecs().decoder(lineDecoder);
				}).build()).baseUrl(txtFileUrl);

		// build the web client
		WebClient client = webClientBuilder.build();
		// WebClient client=WebClient.create(txtFileUrl);
		Flux<String> lineFlux = client.get().retrieve().bodyToFlux(String.class);
		Flux<String> wordFlux = lineFlux.flatMapIterable(it -> {
			String[] words = it.split("\\s+");
			return Arrays.asList(words);
		}).filter(it -> StringUtils.isNotBlank(it));

		Flux<GroupedFlux<String, String>> wordGoup = wordFlux.groupBy(it -> it);

		// TODO: The code below does not work!
		Flux<Pair<String, Long>> wordCountFlux = wordGoup.flatMap(gr -> {
			return gr.count().map(cnt -> Pair.of(gr.key(), cnt));
		});

		Flux<Pair<String, Long>> topFreqWords = wordCountFlux
				.sort((p1, p2) -> Long.compare(p2.getRight(), p1.getRight())).take(top);

		wordCountFlux.subscribe(x -> System.out.println(x));

		log.info("finished");

	}

}
