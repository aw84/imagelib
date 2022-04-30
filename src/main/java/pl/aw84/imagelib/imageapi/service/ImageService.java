package pl.aw84.imagelib.imageapi.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;

import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import pl.aw84.imagelib.imageapi.entity.Image;
import pl.aw84.imagelib.imageapi.entity.Storage;
import pl.aw84.imagelib.imageapi.repository.ImageRepository;
import pl.aw84.imagelib.imageapi.repository.StorageRepository;

@Service
@RefreshScope
public class ImageService {

    @Value("${imageDataDir}")
    private String imageDataDir;

    private SaveFile saveFile;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private StorageRepository storageRepository;

    public ImageService(ImageRepository imageRepository, SaveFile saveFile) {
        this.imageRepository = imageRepository;
        this.saveFile = saveFile;
    }

    @Transactional
    public void printAll() {
        for (Image i : imageRepository.findAll()) {
            System.err.println(i.toString());
        }
    }

    @Transactional
    public Iterable<Image> getAll(int page) {
        return imageRepository.findAll(PageRequest.of(page, 5));
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

    public void saveFile(MultipartFile input, String hexDigest) throws IllegalStateException, IOException {

        // SaveFile saveFile = new SaveFile();
        String relativePath = this.saveFile.createDirTree(this.imageDataDir, hexDigest)
                + "/" + input.getOriginalFilename();

        String absolutePath = this.imageDataDir + "/" + relativePath;
        input.transferTo(new File(absolutePath));

        Image image = new Image();
        image.setName(input.getOriginalFilename());
        image = imageRepository.save(image);

        Storage storage = new Storage();
        storage.setImage(image);
        storage.setHash(hexDigest);
        storage.setRelativePath(relativePath);
        this.storageRepository.save(storage);
    }
}
