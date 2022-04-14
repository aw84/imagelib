package pl.aw84.imagelib.imageapi.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;

import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.aw84.imagelib.imageapi.entity.Image;
import pl.aw84.imagelib.imageapi.repository.ImageRepository;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Transactional
    public void printAll() {
        for (Image i : imageRepository.findAll()) {
            System.err.println(i.toString());
        }
    }

    @Transactional
    public Iterable<Image> getAll() {
        return imageRepository.findAll();
    }

    public String getDigest(byte[] rawData) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA3-512");
        byte[] hash = digest.digest(rawData);
        return new String(Hex.encode(hash));

    }

    public void scaleImage(byte[] rawData) {

        ByteArrayInputStream bais = new ByteArrayInputStream(rawData);

        try {
            BufferedImage bufferedImage = ImageIO.read(bais);

            try (FileOutputStream f = new FileOutputStream("some_file.png")) {

                boolean written = ImageIO.write(bufferedImage, "png", f);
                System.err.println("Written: " + written);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
