package com.github.imifou.resource;

import com.github.imifou.test.server.RestServerTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserResourceTest extends RestServerTest {

    @LocalServerPort
    private int port;

    @Override
    public Integer getPort() {
        return port;
    }
}
