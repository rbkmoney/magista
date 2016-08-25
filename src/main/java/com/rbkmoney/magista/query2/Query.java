package com.rbkmoney.magista.query2;

/**
 * Created by vpankrashkin on 03.08.16.
 */
public interface Query {

    Query getParentQuery();

    QueryParameters getQueryParameters();

}
