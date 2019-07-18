package es.manuel.vera.silvestre.util;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MapUtil{
    public static <K, V> Map<K,V> sortMapByValues(Map<K,V> unordered, Comparator<Map.Entry<K,V>> comparator){
        return unordered
            .entrySet()
            .stream()
            .sorted(comparator)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                LinkedHashMap::new));
    }
}
