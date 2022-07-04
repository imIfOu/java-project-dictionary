package com.github.imifou.resource;

import com.github.imifou.test.server.RestServerTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class UserResourceTest extends RestServerTest {

    @LocalServerPort
    private int port;

    @Override
    public Integer getPort() {
        return port;
    }
}
