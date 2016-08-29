package com.rbkmoney.magista.query.parser;

import java.util.List;

/**
 * Created by vpankrashkin on 24.08.16.
 */
public interface QueryParser<Src> {
    List<QueryPart> parseQuery(Src source, QueryPart parent) throws QueryParserException;
    boolean apply(Src source, QueryPart parent);
}
