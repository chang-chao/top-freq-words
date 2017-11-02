package me.changchao.reactive.topfreqwords.client.txt;

import org.springframework.core.codec.StringDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import me.changchao.reactive.topfreqwords.client.TopFreqWordCounterBase;
import reactor.core.publisher.Flux;

//@Component
public class TopFreqWordCounterTxt extends TopFreqWordCounterBase {

	@Override
	protected Flux<String> getLines() {
		// this is the text file we want to analyze
		String txtFileUrl = "http://localhost:8080/txt";

		// build the webClient
		WebClient client = this.buildWebClientForTxt(txtFileUrl);
		// get the content of the text file line by line
		return client.get().retrieve().bodyToFlux(String.class);
	}

	/**
	 * build a web client
	 * 
	 * @param baseUrl
	 *            base url
	 * @return a web client
	 */
	private WebClient buildWebClientForTxt(String baseUrl) {
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

}
