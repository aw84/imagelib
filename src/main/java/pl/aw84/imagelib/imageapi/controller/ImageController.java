package pl.aw84.imagelib.imageapi.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RefreshScope
@RestController
public class ImageController {
    @Value("${greeting}")
    String greeting;

    @GetMapping(value = "/greeting")
    public String getMethodName() {
        return new String(this.greeting);
    }

}
