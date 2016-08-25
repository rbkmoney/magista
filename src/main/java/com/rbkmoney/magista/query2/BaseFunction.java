package com.rbkmoney.magista.query2;


/**
 * Created by vpankrashkin on 05.08.16.
 */
public abstract class BaseFunction extends BaseQuery implements FunctionQuery {
    private final String name;

    public BaseFunction(QueryParameters params, String name) {
        super(params);
        this.name = name;
    }

    public BaseFunction(QueryParameters params, Query parentQuery, String name) {
        super(params, parentQuery);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
