package pl.aw84.imagelib.imageapi.service;

import org.springframework.stereotype.Service;

@Service
public class SaveFile {
    private final int dirLevels = 6;

    public String createDirTree(String dirBase, String hexDigest) {
        return "";
    }

    public String[] split(String hexDigest) {
        if (hexDigest.length() < dirLevels * 2) {
            throw new IllegalArgumentException("Hex Digest length error");
        }
        String important_base = hexDigest.substring(0, dirLevels * 2);
        return important_base.split("(?<=\\G..)");
    }
}
