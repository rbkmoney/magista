package com.rbkmoney.magista.util;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TypeUtil {

    public static <T extends Enum<T>> T toEnumField(String name, Class<T> enumType) {
        return Optional.ofNullable(name)
                .map(value -> Enum.valueOf(enumType, name))
                .orElse(null);
    }

    public static <T extends Enum<T>> List<T> toEnumFields(List<String> names, Class<T> enumType) {
        return Optional.ofNullable(names)
                .map(
                        values -> values.stream()
                                .filter(name -> name != null)
                                .map(name -> toEnumField(name, enumType))
                                .collect(Collectors.toList())
                ).orElse(null);
    }

}
