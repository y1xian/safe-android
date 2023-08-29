package com.yyxnb.android.secure.base.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Locale;

/**
 * FileUtil
 *
 * <pre>
 * </pre>
 *
 * @author yyx
 * @date 2023/8/30
 */
public class FileUtil {
    /**
     * 文件路径的校验，防止跨目录问题
     */
    public static boolean filePathIsValid(String intendedDir, String entryName) throws IllegalArgumentException, IOException {
        if (intendedDir == null || intendedDir.equals("") || entryName == null || entryName.equals("")) {
            return false;
        }

        try {
            entryName = URLDecoder.decode(entryName, "utf-8");
            intendedDir = URLDecoder.decode(intendedDir, "utf-8");
        } catch (IllegalArgumentException e) {
            return false;
        }

        if (intendedDir.contains("..") || intendedDir.contains("./") || intendedDir.contains(".\\.\\")
                || intendedDir.contains("%00")) {
            return false;
        }
        if (entryName.contains("..") || entryName.contains("./") || entryName.contains(".\\.\\")
                || entryName.contains("%00")) {
            return false;
        }
        File file = new File(intendedDir, entryName);
        String canonicalPath = file.getCanonicalPath();

        File dir = new File(intendedDir);
        String canonicalID = dir.getCanonicalPath();

        return canonicalPath.startsWith(canonicalID);
    }

    /**
     * 文件路径的校验，防止跨目录问题
     */
    public static boolean filePathIsValid(String filePath) throws UnsupportedEncodingException {
        if (filePath == null || filePath.equals("")) {
            return true;
        }
        if (filePath.contains("%")) {
            filePath = filePath.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
        }
        filePath = URLDecoder.decode(filePath, "utf-8");
        if (filePath.contains("..") || filePath.contains("./") || filePath.contains(".\\.\\")
                || filePath.contains("%00")) {
            return false;
        }
        return true;
    }

    /**
     * 文件名后缀校验
     */
    public static boolean checkFileExtValid(String fileName, String[] fileExtArray) {
        if (fileName == null || fileName.isEmpty() || fileExtArray == null) {
            return false;
        }

        String fileExt = getExt(fileName);
        for (int i = 0; i < fileExtArray.length; i++) {
            if (fileExt.equalsIgnoreCase(fileExtArray[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件后缀名
     */
    private static String getExt(String fileName) {
        if (!fileName.isEmpty()) {
            int i = fileName.lastIndexOf(".");
            if (i != -1 && (i + 1) != fileName.length()) {
                return fileName.substring(i + 1).toLowerCase(Locale.ENGLISH);
            }
        }
        return "";
    }
}
