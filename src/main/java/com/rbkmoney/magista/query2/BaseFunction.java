package com.rbkmoney.magista.query2;


/**
 * Created by vpankrashkin on 05.08.16.
 */
public abstract class BaseFunction extends BaseQuery implements FunctionQuery {
    private final String name;

    public BaseFunction(Object descriptor, QueryParameters params, String name) {
        super(descriptor, params);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
