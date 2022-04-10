package pl.aw84.imagelib.imageapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.aw84.imagelib.imageapi.entity.Image;
import pl.aw84.imagelib.imageapi.service.ImageService;

@RefreshScope
@RestController
public class ImageController {
    @Autowired
    private ImageService imageService;
    @Value("${greeting}")
    String greeting;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(value = "/greeting")
    public String getMethodName() {
        return new String(this.greeting);
    }

    @GetMapping(value = "/image")
    public ResponseEntity<Iterable<Image>> getImages() {
        return new ResponseEntity<>(imageService.getAll(), HttpStatus.OK);
    }
}
