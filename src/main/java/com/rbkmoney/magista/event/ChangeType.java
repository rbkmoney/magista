package com.rbkmoney.magista.event;

import com.rbkmoney.geck.filter.Condition;
import com.rbkmoney.geck.filter.Filter;
import com.rbkmoney.geck.filter.PathConditionFilter;
import com.rbkmoney.geck.filter.condition.IsNullCondition;
import com.rbkmoney.geck.filter.rule.PathConditionRule;

/**
 * Created by tolkonepiu on 10/11/2016.
 */
public enum ChangeType {

    INVOICE_CREATED("invoice_created", new IsNullCondition().not()),
    INVOICE_STATUS_CHANGED("invoice_status_changed", new IsNullCondition().not()),
    INVOICE_PAYMENT_STARTED("invoice_payment_change.invoice_payment_started", new IsNullCondition().not()),
    INVOICE_PAYMENT_STATUS_CHANGED("invoice_payment_change.invoice_payment_status_changed", new IsNullCondition().not()),
    INVOICE_PAYMENT_ADJUSTMENT_CREATED("invoice_payment_change.invoice_payment_adjustment_event.invoice_payment_adjustment_created", new IsNullCondition().not()),
    INVOICE_PAYMENT_ADJUSTMENT_STATUS_CHANGED("invoice_payment_change.invoice_payment_adjustment_change.invoice_payment_adjustment_status_changed", new IsNullCondition().not());

    Filter filter;

    ChangeType(String path, Condition... conditions) {
        this.filter = new PathConditionFilter(new PathConditionRule(path, conditions));
    }

    public Filter getFilter() {
        return filter;
    }
}
