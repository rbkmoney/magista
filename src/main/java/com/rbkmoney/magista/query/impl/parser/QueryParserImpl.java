package com.rbkmoney.magista.query.impl.parser;

import com.rbkmoney.magista.query.impl.*;
import com.rbkmoney.magista.dsl.parser.BaseQueryParser;
import com.rbkmoney.magista.dsl.parser.QueryParser;
import com.rbkmoney.magista.dsl.parser.QueryPart;
import com.rbkmoney.magista.dsl.RootQuery;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by vpankrashkin on 26.08.16.
 */
public class QueryParserImpl extends BaseQueryParser {

    public QueryParserImpl() {
        this(
                Arrays.asList(
                        new RootQuery.RootParser(),
                        new PaymentsFunction.PaymentsParser(),
                        new PaymentsForReportFunction.PaymentsForReportParser(),
                        new InvoicesFunction.InvoicesParser(),
                        new RefundsFunction.RefundsParser(),
                        new PayoutsFunction.PayoutsParser(),
                        new CustomersRateStatFunction.CustomersRateStatParser(),
                        new PaymentsConversionStatFunction.PaymentsConversionStatParser(),
                        new PaymentsGeoStatFunction.PaymentsGeoStatParser(),
                        new PaymentsCardTypesStatFunction.PaymentsCardTypesStatParser(),
                        new PaymentsTurnoverStatFunction.PaymentsTurnoverStatParser(),
                        new AccountingReportFunction.AccountingReportParser()
                )
        );
    }

    public QueryParserImpl(List<QueryParser<Map<String, Object>>> parsers) {
        super(parsers);
    }

    @Override
    public boolean apply(Map source, QueryPart parent) {
        return true;
    }
}
