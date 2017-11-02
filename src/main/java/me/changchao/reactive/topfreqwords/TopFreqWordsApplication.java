package me.changchao.reactive.topfreqwords;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication()
public class TopFreqWordsApplication {
	public static final String INPUT_TXT_FILE = "text.txt";

	public static void main(String[] args) {
		SpringApplication.run(TopFreqWordsApplication.class, args);
	}
}
