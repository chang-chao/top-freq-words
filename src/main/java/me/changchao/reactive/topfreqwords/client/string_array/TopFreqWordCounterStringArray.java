package me.changchao.reactive.topfreqwords.client.string_array;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.assertj.core.util.Files;
import org.springframework.stereotype.Component;

import me.changchao.reactive.topfreqwords.TopFreqWordsApplication;
import me.changchao.reactive.topfreqwords.client.TopFreqWordCounterBase;
import reactor.core.publisher.Flux;

//@Component
public class TopFreqWordCounterStringArray extends TopFreqWordCounterBase {

	@Override
	protected Flux<String> getLines() {
		String[] lines = Files.linesOf(new File(TopFreqWordsApplication.INPUT_TXT_FILE), StandardCharsets.UTF_8)
				.toArray(new String[] {});
		return Flux.fromArray(lines);
	}

}
