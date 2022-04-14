package pl.aw84.imagelib.imageapi.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = { "spring.config.import=optional:configserver:http://localhost:9001" })
public class ImageTests {
    
    @Test
    void createImageDefault() {
    }
}
