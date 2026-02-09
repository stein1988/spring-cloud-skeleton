package features;

import com.lonbon.cloud.demo.App;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HelloTest {
    
    private final WebTestClient webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:8080").build();

    @Test
    public void hello() {
        webTestClient.get().uri("/hello?name=world").exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(body -> {
                    assert body.contains("world");
                });
        
        webTestClient.get().uri("/hello?name=solon").exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(body -> {
                    assert body.contains("solon");
                });
    }
}