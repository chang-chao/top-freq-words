package me.changchao.reactive.topfreqwords;

import java.util.HashMap;

import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Test;

import io.reactivex.Observable;

public class TestCountAfterGroupBy {
	@Test
	public void test_collect() {
		Observable<String> source = Observable.just("A", "B", "A", "C", "C", "A");

		source.collectInto(new HashMap<String, MutableInt>(), (map, elem) -> {
			if (map.containsKey(elem)) {
				map.get(elem).increment();
			} else {
				map.put(elem, new MutableInt(1));
			}

		}).subscribe(System.out::println);

	}
}
