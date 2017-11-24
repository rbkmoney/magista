package com.rbkmoney.magista.dao;

import com.rbkmoney.magista.dao.field.CollectionConditionField;
import com.rbkmoney.magista.dao.field.ConditionField;
import com.rbkmoney.magista.dao.field.SimpleConditionField;
import org.jooq.Comparator;
import org.jooq.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by tolkonepiu on 31/05/2017.
 */
public class ConditionParameterSource {

    private List<ConditionField> conditionFields;

    public ConditionParameterSource() {
        this.conditionFields = new ArrayList<>();
    }

    public <T> ConditionParameterSource addValue(Field<T> field, T value, Comparator comparator) {
        if (value != null) {
            ConditionField conditionField = new SimpleConditionField<>(field, value, comparator);
            conditionFields.add(conditionField);
        }
        return this;
    }

    public <T> ConditionParameterSource addInConditionValue(Field<T> field, Collection<T> value) {
        if (value != null) {
            ConditionField conditionField = new CollectionConditionField<>(field, value, Comparator.IN);
            conditionFields.add(conditionField);
        }
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
}
