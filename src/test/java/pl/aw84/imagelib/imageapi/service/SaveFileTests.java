package pl.aw84.imagelib.imageapi.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SaveFileTests {
    @Autowired
    SaveFile saveFile;

    @Test
    void splitHexDigestByTwoChars() {

        assertArrayEquals(
                Arrays.asList("01", "23", "45", "67", "89", "ab").toArray(),
                saveFile.split("0123456789ab"));

        assertArrayEquals(
                Arrays.asList("01", "23", "45", "67", "89", "ab").toArray(),
                saveFile.split("0123456789abCD"));

        assertThrows(IllegalArgumentException.class,
                () -> {
                    saveFile.split("12");
                });
    }
}
