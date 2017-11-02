package me.changchao.reactive.topfreqwords;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.BaseStream;

import reactor.core.publisher.Flux;

public class FluxUtils {
	public static Flux<String> fromPath(Path path) {
		return Flux.using(() -> Files.lines(path), Flux::fromStream, BaseStream::close);
	}
}
