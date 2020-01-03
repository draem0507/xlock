package com.bluedream.distributedlock.util;

import org.apache.commons.lang.CharUtils;

/**
 * @author: draem0507
 * @date: 2020-01-03 20:41
 * @desc:
 */
public class CryptUtil {

    private static final String BASE = "cerberus";

    private CryptUtil() {
    }

    public static boolean validSecret(String appKey, String secret) {
        String s = DigestUtils.parseStrToMd5U32(appKey);
        String b = DigestUtils.parseStrToMd5L16(appKey);
        StringBuilder sbl = new StringBuilder();
        char[] chars = b.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            char c = chars[i];
            if (CharUtils.isAsciiAlpha(c)) {
                sbl.append(c - 96);
            } else if (CharUtils.isAsciiNumeric(c)) {
                sbl.append(c);
            }
        }
        long l= Long.valueOf(sbl.toString());
        char[] sChars = s.toCharArray();
        char[] baseChars = BASE.toCharArray();
        int baseLen = BASE.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sChars.length; i++) {
            char c = sChars[i];
            char baseChar = baseChars[i % baseLen];
            int d = baseChar - c;
            sb.append(d);
            Long l1 = c * l;
            short sh = l1.shortValue();
            sb.append(sh);
            sb.append(c);
        }
        if (DigestUtils.parseStrToMd5U32(sb.toString()).equals(secret)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getBASE() {
        return BASE;
    }
}
