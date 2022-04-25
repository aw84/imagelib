package pl.aw84.imagelib.imageapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import pl.aw84.imagelib.imageapi.entity.Image;

@SpringBootTest
public class ImageControllerTests {

    @Autowired
    private ImageController imageController;

    @Test
    void greetingSet() {
        assertEquals(null, imageController.greeting);
    }

    @Test
    void getGreeting() {
        assertEquals("toplevel-local", imageController.getGreeting());
    }

    @Test
    @Transactional
    void getImages() {
        ResponseEntity<Iterable<Image>> images = imageController.getImages();

        assertEquals(HttpStatus.OK, images.getStatusCode());

        images.getBody().forEach(System.err::println);
    }

}
