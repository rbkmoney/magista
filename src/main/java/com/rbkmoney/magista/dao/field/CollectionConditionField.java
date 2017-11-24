package com.rbkmoney.magista.dao.field;

import org.jooq.Comparator;
import org.jooq.Field;

import java.util.Collection;

public class CollectionConditionField<T> implements ConditionField<T, Collection<T>> {

    private final Field<T> field;

    private Collection<T> value;

    private Comparator comparator;

    public CollectionConditionField(Field<T> field, Collection<T> value, Comparator comparator) {
        this.field = field;
        this.value = value;
        this.comparator = comparator;
    }

    @Override
    public Field<T> getField() {
        return field;
    }

    @Override
    public Collection<T> getValue() {
        return value;
    }

    @Override
    public Comparator getComparator() {
        return comparator;
    }

    @Override
    public String toString() {
        return "CollectionConditionField{" +
                "field=" + field +
                ", value=" + value +
                ", comparator=" + comparator +
                '}';
    }
}
