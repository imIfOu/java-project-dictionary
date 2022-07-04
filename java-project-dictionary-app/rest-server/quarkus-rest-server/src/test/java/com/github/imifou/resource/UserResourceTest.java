package com.github.imifou.resource;

import com.github.imifou.test.server.RestServerTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@QuarkusTest
@TestHTTPEndpoint(UserResource.class)
public class UserResourceTest extends RestServerTest {

    @ConfigProperty(name = "quarkus.http.test-port")
    Integer port;

    @Override
    public Integer getPort() {
        return port;
    }
}