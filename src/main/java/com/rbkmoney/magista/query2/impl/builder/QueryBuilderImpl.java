package com.rbkmoney.magista.query2.impl.builder;

import com.rbkmoney.magista.query2.builder.BaseQueryBuilder;
import com.rbkmoney.magista.query2.builder.QueryBuilder;
import com.rbkmoney.magista.query2.impl.*;
import com.rbkmoney.magista.query2.parser.QueryPart;

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
                        new PaymentsGeoStatFunction.PaymentsGeoStatBuilder(),
                        new CustomersRateStatFunction.CustomersRateStatBuilder(),
                        new PaymentsTurnoverStatFunction.PaymentsTurnoverStatBuilder(),
                        new PaymentsConversionStatFunction.PaymentsConversionStatBuilder()
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
