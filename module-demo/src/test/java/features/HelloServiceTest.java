package features;

import com.lonbon.cloud.demo.App;
import com.lonbon.cloud.demo.HelloService;
import com.lonbon.cloud.demo.HelloServiceImpl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest(classes = App.class)
public class HelloServiceTest {
    
    @Autowired
    HelloService helloService;

    @Test
    public void hello() {
        assert helloService.hello("world").contains("world");
        assert helloService.hello("solon").contains("solon");
    }
}