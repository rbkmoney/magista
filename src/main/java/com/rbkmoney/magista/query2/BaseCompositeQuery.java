package com.rbkmoney.magista.query2;


import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public class BaseCompositeQuery extends BaseQuery implements CompositeQuery {
    private List<Query> queries;
    private boolean parallel;

    public BaseCompositeQuery(Object descriptor, QueryParameters params) {
        super(descriptor, params);
    }

    public List<Query> getChildQueries() {
        return queries;
    }

    public boolean isParallel() {
        return parallel;
    }

/*    @Override
    protected QueryParameters createQueryParameters(QueryParameters parameters, QueryParameters derivedParameters) {
        return parameters;
    }*/

    @Override
    public void setChildQueries(List<Query> queries, boolean parallel) {

        this.queries = queries.stream().peek(query -> query.setParentQuery(this)).collect(Collectors.toList());
        this.parallel = parallel;
    }
}
