package pl.aw84.imagelib.imageapi.service;

import java.io.File;

import org.springframework.stereotype.Service;

@Service
public class SaveFile {
    private final int dirLevels = 6;

    public String createDirTree(String dirBase, String hexDigest) {
        String dir_path = dirBase + "/" + String.join("/", this.split(hexDigest));
        new File(dir_path).mkdirs();
        return dir_path;
    }

    public String[] split(String hexDigest) {
        if (hexDigest.length() < dirLevels * 2) {
            throw new IllegalArgumentException("Hex Digest length error");
        }
        String important_base = hexDigest.substring(0, dirLevels * 2);
        return important_base.split("(?<=\\G..)");
    }
}
