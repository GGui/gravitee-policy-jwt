/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.policy.jwt.token;

import io.gravitee.common.http.HttpHeaders;
import io.gravitee.common.util.LinkedMultiValueMap;
import io.gravitee.gateway.api.Request;
import io.gravitee.policy.jwt.exceptions.AuthorizationSchemeException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class TokenExtractorTest {

    @Mock
    private Request request;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldNotExtract_noAuthorizationHeader() throws AuthorizationSchemeException {
        HttpHeaders headers = new HttpHeaders();
        when(request.headers()).thenReturn(headers);
        when(request.parameters()).thenReturn(new LinkedMultiValueMap<>());

        String token = TokenExtractor.extract(request);

        Assert.assertNull(token);
    }

    @Test(expected = AuthorizationSchemeException.class)
    public void shouldNotExtract_unknownAuthorizationHeader() throws AuthorizationSchemeException {
        String jwt = "dummy-token";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + jwt);
        when(request.headers()).thenReturn(headers);

        String token = TokenExtractor.extract(request);

        Assert.assertNull(token);
    }

    @Test(expected = AuthorizationSchemeException.class)
    public void shouldNotExtract_bearerAuthorizationHeader_noValue() throws AuthorizationSchemeException {
        String jwt = "dummy-token";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", TokenExtractor.BEARER);
        when(request.headers()).thenReturn(headers);

        String token = TokenExtractor.extract(request);

        Assert.assertNull(token);
    }

    @Test
    public void shouldExtract_fromHeader() throws AuthorizationSchemeException {
        String jwt = "dummy-token";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", TokenExtractor.BEARER + ' ' + jwt);
        when(request.headers()).thenReturn(headers);

        String token = TokenExtractor.extract(request);

        Assert.assertNotNull(token);
        Assert.assertEquals(jwt, token);
    }

    @Test
    public void shouldExtract_fromQueryParameter() throws AuthorizationSchemeException {
        String jwt = "dummy-token";

        HttpHeaders headers = new HttpHeaders();
        when(request.headers()).thenReturn(headers);

        LinkedMultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add(TokenExtractor.ACCESS_TOKEN, jwt);
        when(request.parameters()).thenReturn(parameters);

        String token = TokenExtractor.extract(request);

        Assert.assertNotNull(token);
        Assert.assertEquals(jwt, token);
    }
}
