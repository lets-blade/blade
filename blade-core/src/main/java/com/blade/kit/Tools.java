/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.kit;

import com.blade.mvc.http.Request;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Random;


/**
 * Blade Core Class
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.7.1-alpha
 */
public final class Tools {

    private static final Random random = new Random();

    private Tools() {
    }

    /**
     * get ip address
     *
     * @param request
     * @return
     */
    public static String getIpAddr(Request request) {
        if (null == request) {
            return "0.0.0.0";
        }
        String ip = request.header("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.header("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.header("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.address();
        }
        return ip;
    }

    public static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            assert inputChannel != null;
            inputChannel.close();
            assert outputChannel != null;
            outputChannel.close();
        }
    }

    public static int rand(int min, int max) {
        return random.nextInt(max) % (max - min + 1) + min;
    }

    public static String flowAutoShow(int value) {
        int kb = 1024;
        int mb = 1048576;
        int gb = 1073741824;
        if (Math.abs(value) > gb) {
            return Math.round(value / gb) + "GB";
        } else if (Math.abs(value) > mb) {
            return Math.round(value / mb) + "MB";
        } else if (Math.abs(value) > kb) {
            return Math.round(value / kb) + "KB";
        }
        return Math.round(value) + "";
    }

    public static String md5(String str) {
        return EncrypKit.md5(str);
    }

    public static String md5(String str1, String str2) {
        return EncrypKit.md5(str1 + str2);
    }

    public static String sha1(String str) {
        return EncrypKit.encryptSHA1ToString(str);
    }

    public static String sha256(String str) {
        return EncrypKit.encryptSHA256ToString(str);
    }

    public static String sha512(String str) {
        return EncrypKit.encryptSHA512ToString(str);
    }

    public static String enAes(String data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return new BASE64Encoder().encode(encryptedBytes);
    }

    public static String deAes(String data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] cipherTextBytes = new BASE64Decoder().decodeBuffer(data);
        byte[] decValue = cipher.doFinal(cipherTextBytes);
        return new String(decValue);
    }

}