package pl.aw84.imagelib.imageapi.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
import pl.aw84.imagelib.imageapi.entity.ImageQualityEnum;
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
        return imageRepository.findAll(PageRequest.of(page, 16));
    }

    public String getDigest(byte[] rawData) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA3-512");
        byte[] hash = digest.digest(rawData);
        return new String(Hex.encode(hash));

    }

    @Transactional
    public void saveFile(MultipartFile input, String hexDigest) throws IllegalStateException, IOException {

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
        storage.setQuality(ImageQualityEnum.original);
        this.storageRepository.save(storage);
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

    @Transactional
    public void addScaledImage(Storage originalStorage, ImageQualityEnum quality, ByteArrayOutputStream scaledImage)
            throws NoSuchAlgorithmException, FileNotFoundException, IOException {

        String hexDigest = getDigest(scaledImage.toByteArray());

        String relativePath = this.saveFile.createDirTree(this.imageDataDir, hexDigest)
                + "/" + originalStorage.getImage().getName();
        String absolutePath = this.imageDataDir + "/" + relativePath;

        try (FileOutputStream fout = new FileOutputStream(new File(absolutePath))) {
            scaledImage.writeTo(fout);
        } catch (IOException e) {
            throw e;
        }
        Storage storage = new Storage();
        storage.setImage(originalStorage.getImage());
        storage.setHash(hexDigest);
        storage.setRelativePath(relativePath);
        storage.setQuality(quality);
        this.storageRepository.save(storage);
    }

    public Optional<Storage> getImageStorage(UUID p) {

        Optional<Storage> storage = this.imageRepository.findById(p).stream()
                .map(i -> i.getStorage())
                .flatMap(a -> a.stream())
                .findAny();

        return storage;
    }

    public void scaleImage(UUID imageId) {
        Optional<Image> image = imageRepository.findById(imageId);
        Set<ImageQualityEnum> definedQuality = image.stream()
                .map(i -> i.getStorage())
                .flatMap(a -> a.stream())
                .map(p -> p.getQuality())
                .collect(Collectors.toSet());

        Set<ImageQualityEnum> allValues = new HashSet<>(Arrays.asList(ImageQualityEnum.values()));
        allValues.removeAll(definedQuality);

        Optional<Storage> originalQualityStorage = image.stream()
                .flatMap(i -> i.getStorage().stream())
                .filter(i -> i.getQuality() == ImageQualityEnum.original)
                .findAny();
        if (originalQualityStorage.isPresent()) {

            for (ImageQualityEnum q : allValues) {
                if (ImageQualityEnum.big == q) {
                    this.scaleImageBig(originalQualityStorage.get());
                } else if (ImageQualityEnum.small == q) {
                    this.scaleImageSmall(originalQualityStorage.get());
                } else if (ImageQualityEnum.tiny == q) {
                    this.scaleImageTiny(originalQualityStorage.get());
                }
            }
        }

        System.err.println(definedQuality);
        System.err.println(allValues);
    }

    private void scaleImageTiny(Storage originalStorage) {
        try {
            System.err.println(originalStorage.getImage().getImageId());

            ImageScaler imageScaler = new ImageScaler(this.imageDataDir, originalStorage);

            this.addScaledImage(originalStorage, ImageQualityEnum.tiny, imageScaler.getScaledImage());
        } catch (NoSuchAlgorithmException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void scaleImageSmall(Storage originalStorage) {
        System.err.println(originalStorage.getImage().getImageId());
    }

    private void scaleImageBig(Storage originalStorage) {
        System.err.println(originalStorage.getImage().getImageId());
    }
}
