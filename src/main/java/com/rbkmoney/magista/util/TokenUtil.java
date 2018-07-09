package com.rbkmoney.magista.util;

import com.rbkmoney.magista.query.QueryParameters;

import java.util.Optional;
import java.util.UUID;

public class TokenUtil {

    public static Optional<Long> extractIdValue(String token) {
        return Optional.ofNullable(token)
                .map(uuid -> UUID.fromString(uuid).getLeastSignificantBits());
    }

    public static String buildToken(QueryParameters queryParameters, long id) {
        return new UUID(queryParameters.hashCode(), id).toString();
    }

    public static void validateToken(QueryParameters queryParameters, String token) {
        if (token != null && queryParameters.hashCode() != UUID.fromString(token).getMostSignificantBits()) {
            throw new IllegalArgumentException("Invalid token");
        }
    }

}
