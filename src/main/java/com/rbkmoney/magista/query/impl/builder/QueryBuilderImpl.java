package com.rbkmoney.magista.query.impl.builder;

import com.rbkmoney.magista.query.builder.BaseQueryBuilder;
import com.rbkmoney.magista.query.builder.QueryBuilder;
import com.rbkmoney.magista.query.impl.*;
import com.rbkmoney.magista.query.parser.QueryPart;

import java.util.Arrays;
import java.util.List;

/**
 * Created by vpankrashkin on 28.08.16.
 */
public class QueryBuilderImpl extends BaseQueryBuilder {
    public QueryBuilderImpl() {
        this(
                Arrays.asList(
                        new RootQuery.RootBuilder(),
                        new PaymentsFunction.PaymentsBuilder(),
                        new InvoicesFunction.InvoicesBuilder(),
                        new PayoutsFunction.PayoutsBuilder(),
                        new PaymentsGeoStatFunction.PaymentsGeoStatBuilder(),
                        new PaymentsCardTypesStatFunction.PaymentsCardTypesStatBuilder(),
                        new CustomersRateStatFunction.CustomersRateStatBuilder(),
                        new PaymentsTurnoverStatFunction.PaymentsTurnoverStatBuilder(),
                        new PaymentsConversionStatFunction.PaymentsConversionStatBuilder(),
                        new AccountingReportFunction.AccountingReportBuilder()
                )
        );
    }

    public QueryBuilderImpl(List<QueryBuilder> parsers) {
        super(parsers);
    }

    @Override
    public boolean apply(List<QueryPart> queryParts, QueryPart parent) {
        return true;
    }
}
