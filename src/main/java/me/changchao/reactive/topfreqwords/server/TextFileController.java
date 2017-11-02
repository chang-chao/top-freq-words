package me.changchao.reactive.topfreqwords.server;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.assertj.core.util.Files;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import me.changchao.reactive.topfreqwords.FluxUtils;
import me.changchao.reactive.topfreqwords.SingleLine;
import me.changchao.reactive.topfreqwords.TopFreqWordsApplication;
import reactor.core.publisher.Flux;

@RestController
public class TextFileController {

	@GetMapping("/json")
	public Flux<SingleLine> json() {
		Path path = Paths.get(TopFreqWordsApplication.INPUT_TXT_FILE);
		return FluxUtils.fromPath(path).map(SingleLine::new);
	}

	@GetMapping(value = "/txt", produces = "text/plain")
	@ResponseBody
	public String txt() {
		return Files.contentOf(new File(TopFreqWordsApplication.INPUT_TXT_FILE), StandardCharsets.UTF_8);
	}

}
