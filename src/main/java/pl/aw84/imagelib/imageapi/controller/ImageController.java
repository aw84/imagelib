package pl.aw84.imagelib.imageapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pl.aw84.imagelib.imageapi.entity.Image;
import pl.aw84.imagelib.imageapi.service.ImageService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

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
    public String getGreeting() {
        return new String(this.greeting);
    }

    @GetMapping(value = "/image")
    public ResponseEntity<Iterable<Image>> getImages() {
        return new ResponseEntity<>(imageService.getAll(), HttpStatus.OK);
    }

    @PostMapping(value = "/digest")
    public ResponseEntity<String> testDigest(@RequestParam("file") MultipartFile input) {
        try {
            return new ResponseEntity<>(
                    imageService.getDigest(input.getBytes()), HttpStatus.OK);
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

            imageService.scaleImage(input.getBytes());

            return new ResponseEntity<>(digestHex, HttpStatus.OK);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Throwable t) {
            t.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
