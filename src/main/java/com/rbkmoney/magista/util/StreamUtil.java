package com.rbkmoney.magista.util;

import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StreamUtil {

    public static <T, R> List<T> groupAndReduce(List<T> elements, Function<T, R> groupFunction, BinaryOperator<T> reduceFunction) {
        return elements.stream()
                .collect(
                        Collectors.groupingBy(
                                groupFunction,
                                Collectors.reducing(reduceFunction)
                        )
                )
                .values()
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

    }

}
