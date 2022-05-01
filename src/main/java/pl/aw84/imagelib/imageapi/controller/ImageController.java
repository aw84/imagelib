package pl.aw84.imagelib.imageapi.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pl.aw84.imagelib.imageapi.entity.Image;
import pl.aw84.imagelib.imageapi.entity.ImageQualityEnum;
import pl.aw84.imagelib.imageapi.entity.Storage;
import pl.aw84.imagelib.imageapi.service.ImageService;

@RefreshScope
@RestController
public class ImageController {

    @Autowired
    private ImageService imageService;
    @Autowired
    private Tracer tracer;

    @Value("${greeting}")
    String greeting;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(value = "/greeting")
    public String getGreeting() {
        return new String(this.greeting);
    }

    @GetMapping(value = "/image")
    public ResponseEntity<Iterable<Image>> getImages(@RequestParam(defaultValue = "0") int p) {
        return new ResponseEntity<>(imageService.getAll(p), HttpStatus.OK);
    }

    @GetMapping(value = "/storage/{imageId}")
    public ResponseEntity<String> getImageStorage(@PathVariable(value = "imageId") UUID p) {
        Optional<Storage> storage = imageService.getImageStorage(p, ImageQualityEnum.small);
        if (storage.isEmpty()) {
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(storage.get().getRelativePath(), HttpStatus.OK);
    }

    @PostMapping(value = "/digest")
    public ResponseEntity<String> testDigest(@RequestParam("file") MultipartFile input) {
        try {
            Span span = this.tracer.nextSpan().name("CoputeDigest");
            try (Tracer.SpanInScope ws = this.tracer.withSpan(span.start())) {
                String digest = imageService.getDigest(input.getBytes());
                span.event("Digest completed");
                return new ResponseEntity<>(digest, HttpStatus.OK);
            } finally {
                span.end();
            }
        } catch (NoSuchAlgorithmException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Throwable t) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<String> imageUpload(@RequestParam("file") MultipartFile input) {

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA3-512");
            System.err.println(input.getBytes().length);
            byte[] hash = digest.digest(input.getBytes());
            String digestHex = new String(Hex.encode(hash));

            // imageService.scaleImage(input.getBytes());

            imageService.saveFile(input, digestHex);

            return new ResponseEntity<>(digestHex, HttpStatus.OK);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Throwable t) {
            t.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/scale/{imageId}")
    public ResponseEntity<String> scaleImage(@PathVariable(value = "imageId") UUID imageId) {
        try {
            imageService.scaleImage(imageId);
            return new ResponseEntity<>("", HttpStatus.OK);
        } catch (Throwable e) {
            e.printStackTrace();
            return new ResponseEntity<>("error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
