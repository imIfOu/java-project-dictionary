package com.github.imifou;

import com.github.imifou.data.CorrelationId;
import com.github.imifou.utils.ResourceUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import org.slf4j.MDC;

import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public final class MockServer {

    private static final String EMPTY_JSON_OBJECT = "{}";
    private static final String REQUEST_REST_PATH = "payload/rest/%s/request.json";
    private static final String RESPONSE_REST_PATH = "payload/rest/%s/response.json";
    private static final String QUERY_GRAPHQL_PATH = "payload/graphql/%s/query.graphql";
    private static final String VAR_GRAPHQL_PATH = "payload/graphql/%s/variables.json";
    private static final String RESPONSE_GRAPHQL_PATH = "payload/graphql/%s/response.json";


    public static void mockRestServer(WireMockServer server, RequestMethod method, String path, int status, String dir) {
        String request = ResourceUtils.loadResourceToString(String.format(REQUEST_REST_PATH, dir));
        String response = ResourceUtils.loadResourceToString(String.format(RESPONSE_REST_PATH, dir));

        server.stubFor(WireMock.request(method.getName(), WireMock.urlEqualTo(path))
                .withHeader(CorrelationId.CORRELATION_ID_HEADER, containing(MDC.get(CorrelationId.CORRELATION_ID)))
                .withRequestBody(Objects.isNull(request) ? absent() : equalToJson(request))
                .willReturn(WireMock.aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
    }

    public static void mockGraphqlServer(WireMockServer server, String dir) {
        String query = ResourceUtils.loadResourceToString(String.format(QUERY_GRAPHQL_PATH, dir));
        String variables = ResourceUtils.loadResourceToString(String.format(VAR_GRAPHQL_PATH, dir));
        String response = ResourceUtils.loadResourceToString(String.format(RESPONSE_GRAPHQL_PATH, dir));

        server.stubFor(WireMock.post("/")
                .withHeader(CorrelationId.CORRELATION_ID_HEADER, containing(MDC.get(CorrelationId.CORRELATION_ID)))
                .withRequestBody(matchingJsonPath("$.query", equalTo(query)))
                .withRequestBody(matchingJsonPath("$.variables", equalToJson(Objects.isNull(variables) ? EMPTY_JSON_OBJECT : variables)))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
    }
}
