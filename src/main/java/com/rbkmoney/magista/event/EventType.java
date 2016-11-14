package com.rbkmoney.magista.event;

import com.rbkmoney.thrift.filter.Filter;
import com.rbkmoney.thrift.filter.PathConditionFilter;
import com.rbkmoney.thrift.filter.condition.Condition;
import com.rbkmoney.thrift.filter.condition.IsNullCondition;
import com.rbkmoney.thrift.filter.rule.PathConditionRule;

/**
 * Created by tolkonepiu on 10/11/2016.
 */
public enum EventType {

    INVOICE_CREATED("source_event.processing_event.payload.invoice_event.invoice_created", new IsNullCondition().not()),
    INVOICE_STATUS_CHANGED("source_event.processing_event.payload.invoice_event.invoice_status_changed", new IsNullCondition().not()),
    INVOICE_PAYMENT_STARTED("source_event.processing_event.payload.invoice_event.invoice_payment_event.invoice_payment_started", new IsNullCondition().not()),
    INVOICE_PAYMENT_STATUS_CHANGED("source_event.processing_event.payload.invoice_event.invoice_payment_event.invoice_payment_status_changed", new IsNullCondition().not());

    Filter filter;

    EventType(String path, Condition... conditions) {
        this.filter = new PathConditionFilter(new PathConditionRule(path, conditions));
    }

    public Filter getFilter() {
        return filter;
    }
}
