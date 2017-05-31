package com.rbkmoney.magista.dao;

import org.jooq.Comparator;
import org.jooq.Field;

import java.util.List;

/**
 * Created by tolkonepiu on 31/05/2017.
 */
public class ConditionParameterSource {

    private List<ConditionField> conditionFields;

    public <T> ConditionParameterSource addValue(Field<T> field, T value, Comparator comparator) {
        ConditionField conditionField = new ConditionField(field, value, comparator);
        conditionFields.add(conditionField);
        return this;
    }

    public List<ConditionField> getConditionFields() {
        return conditionFields;
    }

    @Override
    public String toString() {
        return "ConditionParameterSource{" +
                "conditionFields=" + conditionFields +
                '}';
    }

    public class ConditionField<T> {

        private final Field<T> field;
        private final T value;
        private final Comparator comparator;

        public ConditionField(Field<T> field, T value, Comparator comparator) {
            this.field = field;
            this.value = value;
            this.comparator = comparator;
        }

        public Field<T> getField() {
            return field;
        }

        public T getValue() {
            return value;
        }

        public Comparator getComparator() {
            return comparator;
        }

        @Override
        public String toString() {
            return "ConditionField{" +
                    "field=" + field +
                    ", value=" + value +
                    ", comparator=" + comparator +
                    '}';
        }
    }
}
