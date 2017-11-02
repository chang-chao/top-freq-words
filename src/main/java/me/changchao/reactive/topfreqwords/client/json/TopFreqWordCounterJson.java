package me.changchao.reactive.topfreqwords.client.json;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import me.changchao.reactive.topfreqwords.SingleLine;
import me.changchao.reactive.topfreqwords.client.TopFreqWordCounterBase;
import reactor.core.publisher.Flux;

@Component
public class TopFreqWordCounterJson extends TopFreqWordCounterBase {

	@Override
	protected Flux<String> getLines() {
		String url = "http://localhost:8080/json";
		WebClient client = WebClient.create(url);
		return client.get().retrieve().bodyToFlux(SingleLine.class).map(l -> l.getLine());
	}

}
