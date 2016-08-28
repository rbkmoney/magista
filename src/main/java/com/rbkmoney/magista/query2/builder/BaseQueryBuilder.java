package com.rbkmoney.magista.query2.builder;

import com.rbkmoney.magista.query2.BaseCompositeQuery;
import com.rbkmoney.magista.query2.CompositeQuery;
import com.rbkmoney.magista.query2.Query;
import com.rbkmoney.magista.query2.QueryParameters;
import com.rbkmoney.magista.query2.parser.QueryPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vpankrashkin on 24.08.16.
 */
public abstract class BaseQueryBuilder implements QueryBuilder {
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    private final List<QueryBuilder> builders;

    public BaseQueryBuilder(List<QueryBuilder> builders) {
        this.builders = new ArrayList<>(builders);
    }

    @Override
    public Query buildQuery(List<QueryPart> queryParts, QueryPart parentQueryPart, QueryBuilder baseBuilder) throws QueryBuilderException {
        try {
            List<Query> queries = queryParts.isEmpty() ? Collections.emptyList() : builders.stream()
                    .filter(
                            builder -> builder.apply(queryParts, parentQueryPart))
                    .map(
                            builder -> builder.buildQuery(queryParts, parentQueryPart, baseBuilder == null ? this : baseBuilder)
                    )
                    .collect(Collectors.toList());
            if (queries.size() > 1) {
                CompositeQuery compositeQuery = new BaseCompositeQuery(new Object(), new QueryParameters(Collections.emptyMap(), null));
                compositeQuery.setChildQueries(queries, false);
                return compositeQuery;
            } else if (queries.size() == 1) {
                return queries.get(0);
            } else {
                log.warn("No builders matched following query parts: {}", queryParts);
                throw new QueryBuilderException("Can't build query, no match to process");
            }
        } catch (QueryBuilderException e) {
            throw e;
        } catch (IllegalArgumentException iae) {
            throw new QueryBuilderException(iae.getMessage(), iae);
        }
    }

}
