package net.txconsole.core.support;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapBuilder<K, V> {

    public static <K, V> Map<K, V> build(@SuppressWarnings("unchecked") Pair<K, V>... elements) {
        Map<K, V> map = new LinkedHashMap<K, V>();
        for (Pair<K, V> element : elements) {
            map.put(element.getLeft(), element.getValue());
        }
        return map;
    }

    public static <K, V> MapBuilder<K, V> create() {
        return new MapBuilder<K, V>();
    }

    public static Map<String, Object> emptyParams() {
        return Collections.emptyMap();
    }

    public static MapBuilder<String, Object> params() {
        return create();
    }

    public static MapBuilder<String, Object> params(String key, Object value) {
        return of(key, value);
    }

    public static <K, V> MapBuilder<K, V> of(K key, V value) {
        return MapBuilder.<K, V>create().with(key, value);
    }

    public static <K, V> Map<K, V> singleton(K key, V value) {
        return MapBuilder.of(key, value).get();
    }

    public static <K, V> Map<K, V> dual(K key1, V value1, K key2, V value2) {
        return MapBuilder.of(key1, value1).with(key2, value2).get();
    }

    private final Map<K, V> map = new LinkedHashMap<K, V>();

    public MapBuilder<K, V> with(K key, V value) {
        map.put(key, value);
        return this;
    }

    public Map<K, V> get() {
        return map;
    }
}
