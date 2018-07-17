package com.rbkmoney.magista;

import com.rbkmoney.magista.query.QueryParameters;
import com.rbkmoney.magista.util.TokenUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TokenUtilTest {

    @Test
    public void testBuildAndValidate() {
        Map parameters = new HashMap<>();
        parameters.put("test", 42);
        parameters.put("test2", 42);
        parameters.put("test3", 42);
        parameters.put("test4", 42);
        QueryParameters queryParameters = new QueryParameters(parameters, null);
        String token = TokenUtil.buildToken(queryParameters, 12342L);
        TokenUtil.validateToken(queryParameters, token);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidUUIDToken() {
        TokenUtil.validateToken(new QueryParameters(new HashMap<>(), null), "test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidToken() {
        TokenUtil.validateToken(new QueryParameters(new HashMap<>(), null), UUID.randomUUID().toString());
    }

}
