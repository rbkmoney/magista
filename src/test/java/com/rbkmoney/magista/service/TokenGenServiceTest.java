package com.rbkmoney.magista.service;

import com.rbkmoney.magista.config.properties.TokenGenProperties;
import com.rbkmoney.magista.exception.BadTokenException;
import com.rbkmoney.magista.query.QueryParameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableConfigurationProperties
@ContextConfiguration(classes = {TokenGenProperties.class, TokenGenService.class})
public class TokenGenServiceTest {

    @Autowired
    private TokenGenService tokenGenService;

    @Test
    public void generateTokenTest() {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("test", 64);
        final QueryParameters queryParameters = new QueryParameters(parameters, null);

        final String token = tokenGenService.generateToken(queryParameters, LocalDateTime.now());
        Assert.assertTrue(tokenGenService.validToken(queryParameters, token));
    }

    @Test
    public void validateTokenAfterUrlDecoderTest() {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("test", 64);
        parameters.put("test_2", 64);
        final QueryParameters queryParameters = new QueryParameters(parameters, null);

        final String token = tokenGenService.generateToken(queryParameters, LocalDateTime.now());
        Assert.assertTrue(tokenGenService.validToken(queryParameters, URLDecoder.decode(token, StandardCharsets.UTF_8)));
    }

    @Test
    public void generateTokenNotValidTest() {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("test", 64);
        final Map<String, Object> derivedParameters = new HashMap<>();
        derivedParameters.put("test_2", 64);
        final QueryParameters queryParameters = new QueryParameters(parameters, new QueryParameters(derivedParameters, null));
        final String firstToken = tokenGenService.generateToken(queryParameters, LocalDateTime.now());
        derivedParameters.put("test_3", 64);
        final String secondToken = tokenGenService.generateToken(new QueryParameters(parameters, new QueryParameters(derivedParameters, null)), LocalDateTime.now());

        Assert.assertFalse(tokenGenService.validToken(queryParameters, secondToken));
    }

    @Test
    public void extractTimeTest() {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("test", 64);
        parameters.put("test_2", 64);
        final QueryParameters queryParameters = new QueryParameters(parameters, null);
        final LocalDateTime nowDateTime = LocalDateTime.now();
        final String token = tokenGenService.generateToken(queryParameters, nowDateTime);
        final Optional<LocalDateTime> tokenDateOptional = tokenGenService.extractTime(token);
        Assert.assertTrue(tokenDateOptional.isPresent());
        final String token2 = tokenGenService.generateToken(queryParameters, tokenDateOptional.get());
        assertEquals(token, token2);
    }

    @Test
    public void validTokenTest() {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("test", 64);
        final Map<String, Object> derivedParameters = new HashMap<>();
        derivedParameters.put("test_2", 64);
        final QueryParameters queryParameters = new QueryParameters(parameters, new QueryParameters(derivedParameters, null));
        final boolean validToken = tokenGenService.validToken(queryParameters, "mH6CM2lOiArjXgVjEdKvQdQ0FpSF_AtmOXTkuoG5bZw;2019-08-07T16:26:39.611932Z");
        Assert.assertTrue(validToken);

    }

    @Test(expected = BadTokenException.class)
    public void invalidTokenTest() {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("test", 64);
        final Map<String, Object> derivedParameters = new HashMap<>();
        derivedParameters.put("test_2", 64);
        final QueryParameters queryParameters = new QueryParameters(parameters, new QueryParameters(derivedParameters, null));
        tokenGenService.validToken(queryParameters, "2019-08-07T16:26:39.611932Z");
    }

    @Test
    public void nanosecResolutionInTokenTest() {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("test", 64);
        final Map<String, Object> derivedParameters = new HashMap<>();
        derivedParameters.put("test_2", 64);
        LocalDateTime now = LocalDateTime.now();
        final QueryParameters queryParameters = new QueryParameters(parameters, new QueryParameters(derivedParameters, null));
        String token = tokenGenService.generateToken(queryParameters, now);
        LocalDateTime tokenTime = tokenGenService.extractTime(token).get();
        assertEquals(now, tokenTime);
    }

}
