package com.rbkmoney.magista.query2.impl.parser;

import com.rbkmoney.magista.query2.impl.*;
import com.rbkmoney.magista.query2.parser.BaseQueryParser;
import com.rbkmoney.magista.query2.parser.QueryParser;
import com.rbkmoney.magista.query2.parser.QueryPart;

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
                        new InvoicesFunction.InvoicesParser(),
                        new CustomersRateStatFunction.CustomersRateStatParser(),
                        new PaymentsConversionStatFunction.PaymentsConversionStatParser(),
                        new PaymentsGeoStatFunction.PaymentsGeoStatParser(),
                        new PaymentsTurnoverStatFunction.PaymentsTurnoverStatParser()
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
