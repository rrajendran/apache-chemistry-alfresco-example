package com.capella.apache.chemistry.mappers;


import com.capella.apache.chemistry.enums.PropertyIdsEnum;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ramesh Rajendran on 4/24/16.
 */
public final class PropertyIdMapper {
    private static List<PropertyIdsEnum> enums = Arrays.asList(PropertyIdsEnum.values());

    private PropertyIdMapper() {
    }

    public static Map<String, String> map(Map<String, String> metadata) {
        Map<String, String> destination = new HashMap<>();
        metadata.entrySet().stream().forEach(entry -> mapping(entry, destination));

        return destination;
    }

    private static void mapping(Map.Entry<String, String> entry, Map<String, String> destination) {
        PropertyIdsEnum first = enums.stream()
                .filter(documentPropertyIdsEnum -> documentPropertyIdsEnum.getLocalName().equals(entry.getKey())).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Property is not mapped - " + entry.getKey()));

        destination.put(first.getId(), entry.getValue());

    }
}
