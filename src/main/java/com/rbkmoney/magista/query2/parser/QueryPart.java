package com.rbkmoney.magista.query2.parser;

import com.rbkmoney.magista.query2.QueryParameters;

import java.util.List;

/**
 * Created by vpankrashkin on 24.08.16.
 */
public class QueryPart {
    private final Object descriptor;
    private final QueryParameters parameters;
    private List<QueryPart> children;
    private final QueryPart parent;

    public QueryPart(Object descriptor, QueryParameters parameters) {
        this(descriptor, parameters, null);
    }

    public QueryPart(Object descriptor, QueryParameters parameters, QueryPart parent) {
        if (parameters == null) {
            throw new NullPointerException("Null arguments're not allowed");
        }
        this.descriptor = descriptor;
        this.parameters = parameters;
        this.parent = parent;
    }

    public Object getDescriptor() {
        return descriptor;
    }

    public QueryParameters getParameters() {
        return parameters;
    }

    public List<QueryPart> getChildren() {
        return children;
    }

    public void setChildren(List<QueryPart> children) {
        this.children = children;
    }

    public QueryPart getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "QueryPart{" +
                "descriptor=" + descriptor +
                ", parameters=" + parameters +
                ", children=" + children +
                ", parent=" + (parent == null ? "null" : "notnull") +
                '}';
    }
}
