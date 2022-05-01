package pl.aw84.imagelib.imageapi.service;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import pl.aw84.imagelib.imageapi.entity.Storage;

public class ImageScaler {
    private Storage originalStorage;
    private String baseDir;

    public ImageScaler(String baseDir, Storage originalStorage) {
        this.originalStorage = originalStorage;
        this.baseDir = baseDir;
    }

    public ByteArrayOutputStream getScaledImage() throws FileNotFoundException, IOException {
        try (FileInputStream fin = new FileInputStream(this.baseDir + "/" + originalStorage.getRelativePath())) {

            BufferedImage bufferedImage = ImageIO.read(fin);
            java.awt.Image scaledInstance = bufferedImage.getScaledInstance(200, 200, java.awt.Image.SCALE_SMOOTH);

            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();

            BufferedImage bi = convertToBufferedImage(scaledInstance, bufferedImage.getType());
            System.err.println("Scaled image width: " + bi.getWidth());
            ImageIO.write(bi, "jpg", byteOutput);

            System.err.println("Image length: " + byteOutput.size());

            return byteOutput;
        }
    }

    public static BufferedImage convertToBufferedImage(java.awt.Image img, int type) {

        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }
        BufferedImage bi = new BufferedImage(
                img.getWidth(null), img.getHeight(null),
                type);

        Graphics2D graphics2D = bi.createGraphics();
        graphics2D.drawImage(img, 0, 0, null);
        graphics2D.dispose();

        return bi;
    }
}
