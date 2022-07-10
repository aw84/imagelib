package pl.aw84.imagelib.imageapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import pl.aw84.imagelib.imageapi.entity.Image;
import pl.aw84.imagelib.imageapi.service.ImageService;

@SpringBootTest
public class ImageControllerTests {

    @MockBean
    ImageService imageService;

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
        ResponseEntity<Iterable<Image>> images = imageController.getImages(0);

        assertEquals(HttpStatus.OK, images.getStatusCode());

        images.getBody().forEach(System.err::println);
    }
    // Endpoints:
    // /image
    // /storage/{imageId}
    // /upload
    // /scale/{imageId}
}
