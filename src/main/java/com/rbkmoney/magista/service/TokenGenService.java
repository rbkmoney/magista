package com.rbkmoney.magista.service;

import com.rbkmoney.magista.config.properties.TokenGenProperties;
import com.rbkmoney.magista.exception.BadTokenException;
import com.rbkmoney.magista.query.QueryParameters;
import com.rbkmoney.magista.util.HmacUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TokenGenService {

    private final TokenGenProperties tokenGenProperties;

    public TokenGenService(TokenGenProperties tokenGenProperties) {
        this.tokenGenProperties = tokenGenProperties;
    }

    public Optional<LocalDateTime> extractTime(String token) {
        if (token == null) {
            return Optional.empty();
        }
        try {
            final long timestamp = Long.parseLong(extractToken(token).getTimestamp());
            return Optional.of(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC));
        } catch (Exception e) {
            log.error("Exception while extract dateTime from: " + token, e);
            return Optional.empty();
        }
    }

    public String generateToken(QueryParameters queryParameters, LocalDateTime createdAt) {
        String val = queryParamsToString(queryParameters);
        try {
            String token = String.format("%s;%d",
                    HmacUtil.encode(tokenGenProperties.getKey(), val.getBytes(StandardCharsets.UTF_8)),
                    createdAt.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
            );
            log.debug("Generated token: {}", token);
            return token;
        } catch (GeneralSecurityException e) {
            throw new TokenGeneratorException("Can't generate token", e);
        }
    }

    public boolean validToken(QueryParameters queryParameters, String validateToken) {
        TokenHolder validateTokenHolder = extractToken(validateToken);
        LocalDateTime createdAt = new Timestamp(Long.valueOf(validateTokenHolder.getTimestamp())).toLocalDateTime();
        String generatedToken = generateToken(queryParameters, createdAt);
        TokenHolder generatedTokenHolder = extractToken(generatedToken);

        return generatedTokenHolder.getToken().equals(validateTokenHolder.getToken());
    }

    private String queryParamsToString(QueryParameters queryParameters) {
        final StringBuilder sb = new StringBuilder();
        do {
            String result = mapToString(queryParameters.getParametersMap());
            sb.append(result);
            queryParameters = queryParameters.getDerivedParameters();
        } while (queryParameters != null);

        return sb.toString();
    }

    private String mapToString(Map<String, ?> map) {
        return map.keySet().stream()
                .map(key -> key + "=" + map.get(key))
                .collect(Collectors.joining(",", "{", "}"));
    }

    private TokenHolder extractToken(String token) {
        String[] tokenSplit = token.split(";");
        if (tokenSplit.length != 2) {
            throw new BadTokenException("Bad token format: " + token);
        }
        return new TokenHolder(tokenSplit[0], tokenSplit[1]);
    }

    @Data
    private static final class TokenHolder {

        private final String token;

        private final String timestamp;

    }


}
