package cz.vutbr.fit.zpo.utils;

import cz.vutbr.fit.zpo.dto.FileInformation;
import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;

public class FileUtils {
    private static final Tika tika = new Tika();

    public static String getMimeType(File file) throws IOException {
        return tika.detect(file);
    }

    public static boolean isFileImage(File file) {
        String mimeType = null;
        try {
            mimeType = getMimeType(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mimeType != null && mimeType.startsWith("image/");
    }

    public static FileInformation getFileInformation(File file) {
        String name = file.getName();
        String mimeType = null;
        try {
            mimeType = getMimeType(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long size = file.length();

        return new FileInformation(name, mimeType, size);
    }
}
