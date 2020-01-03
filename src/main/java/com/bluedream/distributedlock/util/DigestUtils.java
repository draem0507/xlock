package com.bluedream.distributedlock.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author: draem0507
 * @date: 2020-01-03 20:41
 * @desc:
 */
public class DigestUtils {
    private static final Logger logger = LoggerFactory.getLogger(DigestUtils.class);
    private static final String digestType = "MD5";

    private DigestUtils() {
    }

    /**
     * @param str
     * @return
     * @Description: 32位小写MD5
     */
    public static String parseStrToMd5L32(String str) {
        String reStr = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance(digestType);
            byte[] bytes = md5.digest(str.getBytes());
            reStr = byte2Str(bytes);
        } catch (NoSuchAlgorithmException e) {
            logger.error("parse str to MD5 failed", e);
        } catch (Exception e) {
            logger.error("Exception when parse str to MD5", e);
        }
        return reStr;
    }

    /**
     * @param str
     * @return
     * @Description: 32位大写MD5
     */
    public static String parseStrToMd5U32(String str) {
        String reStr = parseStrToMd5L32(str);
        if (reStr != null) {
            reStr = reStr.toUpperCase();
        }
        return reStr;
    }

    /**
     * @param str
     * @return
     * @Description: 16位小写MD5
     */
    public static String parseStrToMd5U16(String str) {
        String reStr = parseStrToMd5L32(str);
        if (reStr != null) {
            reStr = reStr.toUpperCase().substring(8, 24);
        }
        return reStr;
    }

    /**
     * @param str
     * @return
     * @Description: 16位大写MD5
     */
    public static String parseStrToMd5L16(String str) {
        String reStr = parseStrToMd5L32(str);
        if (reStr != null) {
            reStr = reStr.substring(8, 24);
        }
        return reStr;
    }

    private static String byte2Str(byte[] bytes) {
        char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        char[] resultCharArray = new char[bytes.length * 2];
        int index = 0;
        for (byte b : bytes) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);
    }
}