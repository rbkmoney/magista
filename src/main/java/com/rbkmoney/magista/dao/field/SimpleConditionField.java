package com.rbkmoney.magista.dao.field;

import org.jooq.Comparator;
import org.jooq.Field;

public class SimpleConditionField<T> implements ConditionField<T, T> {

    private final Field<T> field;

    private final T value;

    private final Comparator comparator;

    public SimpleConditionField(Field<T> field, T value, Comparator comparator) {
        this.field = field;
        this.value = value;
        this.comparator = comparator;
    }

    @Override
    public Field<T> getField() {
        return field;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public Comparator getComparator() {
        return comparator;
    }

    @Override
    public String toString() {
        return "SimpleConditionField{" +
                "field=" + field +
                ", value=" + value +
                ", comparator=" + comparator +
                '}';
    }
}
