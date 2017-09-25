package com.blade.kit;

import lombok.NoArgsConstructor;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.util.Base64;

import static com.blade.kit.ConvertKit.hex2Dec;

/**
 * 加解密类
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.0
 */
@NoArgsConstructor
public class EncryptKit {

    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * MD5加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */
    public static String md5(String data) {
        return md5(data.getBytes());
    }

    /**
     * MD5加密
     *
     * @param data 明文字符串
     * @param salt 盐
     * @return 16进制加盐密文
     */
    public static String md5(String data, String salt) {
        return bytes2HexString(md5ToByte((data + salt).getBytes()));
    }

    /**
     * MD5加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    public static String md5(byte[] data) {
        return bytes2HexString(md5ToByte(data));
    }

    /**
     * MD5加密
     *
     * @param data 明文字节数组
     * @param salt 盐字节数组
     * @return 16进制加盐密文
     */
    public static String md5(byte[] data, byte[] salt) {
        if (data == null || salt == null) return null;
        byte[] dataSalt = new byte[data.length + salt.length];
        System.arraycopy(data, 0, dataSalt, 0, data.length);
        System.arraycopy(salt, 0, dataSalt, data.length, salt.length);
        return bytes2HexString(md5ToByte(dataSalt));
    }

    /**
     * MD5加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    static byte[] md5ToByte(byte[] data) {
        return hashTemplate(data, "MD5");
    }

    /**
     * MD5加密文件
     *
     * @param filePath 文件路径
     * @return 文件的16进制密文
     */
    public static String md5File(String filePath) {
        File file = StringKit.isBlank(filePath) ? null : new File(filePath);
        return md5File(file);
    }

    /**
     * MD5加密文件
     *
     * @param filePath 文件路径
     * @return 文件的MD5校验码
     */
    public static byte[] md5FileToByte(String filePath) {
        File file = StringKit.isBlank(filePath) ? null : new File(filePath);
        return md5FileToByte(file);
    }

    /**
     * MD5加密文件
     *
     * @param file 文件
     * @return 文件的16进制密文
     */
    public static String md5File(File file) {
        return bytes2HexString(md5FileToByte(file));
    }

    /**
     * MD5加密文件
     *
     * @param file 文件
     * @return 文件的MD5校验码
     */
    public static byte[] md5FileToByte(File file) {
        if (file == null) return null;
        FileInputStream   fis = null;
        DigestInputStream digestInputStream = null;
        try {
            fis = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("MD5");
            digestInputStream = new DigestInputStream(fis, md);
            byte[] buffer = new byte[256 * 1024];
            while (digestInputStream.read(buffer) > 0) ;
            md = digestInputStream.getMessageDigest();
            return md.digest();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            IOKit.closeQuietly(fis);
            IOKit.closeQuietly(digestInputStream);
        }
    }

    /**
     * SHA1加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */
    public static String SHA1(String data) {
        return SHA1(data.getBytes());
    }

    /**
     * SHA1加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    public static String SHA1(byte[] data) {
        return bytes2HexString(SHA1ToByte(data));
    }

    /**
     * SHA1加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    public static byte[] SHA1ToByte(byte[] data) {
        return hashTemplate(data, "SHA1");
    }

    /**
     * SHA256加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */
    public static String SHA256(String data) {
        return SHA256(data.getBytes());
    }

    /**
     * SHA256加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    public static String SHA256(byte[] data) {
        return bytes2HexString(SHA256ToByte(data));
    }

    /**
     * SHA256加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    public static byte[] SHA256ToByte(byte[] data) {
        return hashTemplate(data, "SHA-256");
    }

    /**
     * SHA512加密
     *
     * @param data 明文字符串
     * @return 16进制密文
     */
    public static String SHA512(String data) {
        return SHA512(data.getBytes());
    }

    /**
     * SHA512加密
     *
     * @param data 明文字节数组
     * @return 16进制密文
     */
    public static String SHA512(byte[] data) {
        return bytes2HexString(SHA512ToByte(data));
    }

    /**
     * SHA512加密
     *
     * @param data 明文字节数组
     * @return 密文字节数组
     */
    public static byte[] SHA512ToByte(byte[] data) {
        return hashTemplate(data, "SHA-512");
    }

    /**
     * hash加密模板
     *
     * @param data      数据
     * @param algorithm 加密算法
     * @return 密文字节数组
     */
    private static byte[] hashTemplate(byte[] data, String algorithm) {
        if (data == null || data.length <= 0) return null;
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * HmacMD5加密
     *
     * @param data 明文字符串
     * @param key  秘钥
     * @return 16进制密文
     */
    public static String hmacMd5(String data, String key) {
        return hmacMd5(data.getBytes(), key.getBytes());
    }

    /**
     * HmacMD5加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 16进制密文
     */
    public static String hmacMd5(byte[] data, byte[] key) {
        return bytes2HexString(hmacMd5ToByte(data, key));
    }

    /**
     * HmacMD5加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 密文字节数组
     */
    public static byte[] hmacMd5ToByte(byte[] data, byte[] key) {
        return hmacTemplate(data, key, "HmacMD5");
    }

    /**
     * HmacSHA1加密
     *
     * @param data 明文字符串
     * @param key  秘钥
     * @return 16进制密文
     */
    public static String hmacSHA1(String data, String key) {
        return hmacSHA1(data.getBytes(), key.getBytes());
    }

    /**
     * HmacSHA1加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 16进制密文
     */
    public static String hmacSHA1(byte[] data, byte[] key) {
        return bytes2HexString(hmacSHA1ToByte(data, key));
    }

    /**
     * HmacSHA1加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 密文字节数组
     */
    public static byte[] hmacSHA1ToByte(byte[] data, byte[] key) {
        return hmacTemplate(data, key, "HmacSHA1");
    }

    /**
     * HmacSHA256加密
     *
     * @param data 明文字符串
     * @param key  秘钥
     * @return 16进制密文
     */
    public static String hmacSHA256(String data, String key) {
        return hmacSHA256(data.getBytes(), key.getBytes());
    }

    /**
     * HmacSHA256加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 16进制密文
     */
    public static String hmacSHA256(byte[] data, byte[] key) {
        return bytes2HexString(hmacSHA256ToByte(data, key));
    }

    /**
     * HmacSHA256加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 密文字节数组
     */
    public static byte[] hmacSHA256ToByte(byte[] data, byte[] key) {
        return hmacTemplate(data, key, "HmacSHA256");
    }

    /**
     * HmacSHA512加密
     *
     * @param data 明文字符串
     * @param key  秘钥
     * @return 16进制密文
     */
    public static String hmacSHA512(String data, String key) {
        return hmacSHA512(data.getBytes(), key.getBytes());
    }

    /**
     * HmacSHA512加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 16进制密文
     */
    public static String hmacSHA512(byte[] data, byte[] key) {
        return bytes2HexString(hmacSHA512ToByte(data, key));
    }

    /**
     * HmacSHA512加密
     *
     * @param data 明文字节数组
     * @param key  秘钥
     * @return 密文字节数组
     */
    public static byte[] hmacSHA512ToByte(byte[] data, byte[] key) {
        return hmacTemplate(data, key, "HmacSHA512");
    }

    /**
     * Hmac加密模板
     *
     * @param data      数据
     * @param key       秘钥
     * @param algorithm 加密算法
     * @return 密文字节数组
     */
    private static byte[] hmacTemplate(byte[] data, byte[] key, String algorithm) {
        if (data == null || data.length == 0 || key == null || key.length == 0) return null;
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, algorithm);
            Mac           mac       = Mac.getInstance(algorithm);
            mac.init(secretKey);
            return mac.doFinal(data);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /************************ DES加密相关 ***********************/
    /**
     * DES转变
     * <p>法算法名称/加密模式/填充方式</p>
     * <p>加密模式有：电子密码本模式ECB、加密块链模式CBC、加密反馈模式CFB、输出反馈模式OFB</p>
     * <p>填充方式有：NoPadding、ZerosPadding、PKCS5Padding</p>
     */
    public static        String DES_Transformation = "DES/ECB/NoPadding";
    private static final String DES_Algorithm      = "DES";

    /**
     * DES加密后转为Base64编码
     *
     * @param data 明文
     * @param key  8字节秘钥
     * @return Base64密文
     */
    public static byte[] DES2Base64(byte[] data, byte[] key) {
        return Base64.getEncoder().encode(DES(data, key));
    }

    /**
     * DES加密后转为16进制
     *
     * @param data 明文
     * @param key  8字节秘钥
     * @return 16进制密文
     */
    public static String DES2HexString(byte[] data, byte[] key) {
        return bytes2HexString(DES(data, key));
    }

    /**
     * DES加密
     *
     * @param data 明文
     * @param key  8字节秘钥
     * @return 密文
     */
    public static byte[] DES(byte[] data, byte[] key) {
        return desTemplate(data, key, DES_Algorithm, DES_Transformation, true);
    }

    /**
     * DES解密Base64编码密文
     *
     * @param data Base64编码密文
     * @param key  8字节秘钥
     * @return 明文
     */
    public static byte[] decryptBase64DES(byte[] data, byte[] key) {
        try {
            return decryptDES(Base64.getDecoder().decode(data), key);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * DES解密16进制密文
     *
     * @param data 16进制密文
     * @param key  8字节秘钥
     * @return 明文
     */
    public static byte[] decryptHexStringDES(String data, byte[] key) {
        return decryptDES(hexString2Bytes(data), key);
    }

    /**
     * DES解密
     *
     * @param data 密文
     * @param key  8字节秘钥
     * @return 明文
     */
    public static byte[] decryptDES(byte[] data, byte[] key) {
        return desTemplate(data, key, DES_Algorithm, DES_Transformation, false);
    }

    /************************ 3DES加密相关 ***********************/
    /**
     * 3DES转变
     * <p>法算法名称/加密模式/填充方式</p>
     * <p>加密模式有：电子密码本模式ECB、加密块链模式CBC、加密反馈模式CFB、输出反馈模式OFB</p>
     * <p>填充方式有：NoPadding、ZerosPadding、PKCS5Padding</p>
     */
    public static        String TripleDES_Transformation = "DESede/ECB/NoPadding";
    private static final String TripleDES_Algorithm      = "DESede";


    /**
     * 3DES加密后转为Base64编码
     *
     * @param data 明文
     * @param key  24字节秘钥
     * @return Base64密文
     */
    public static byte[] encrypt3DES2Base64(byte[] data, byte[] key) {
        try {
            return Base64.getEncoder().encode(encrypt3DES(data, key));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 3DES加密后转为16进制
     *
     * @param data 明文
     * @param key  24字节秘钥
     * @return 16进制密文
     */
    public static String encrypt3DES2HexString(byte[] data, byte[] key) {
        return bytes2HexString(encrypt3DES(data, key));
    }

    /**
     * 3DES加密
     *
     * @param data 明文
     * @param key  24字节密钥
     * @return 密文
     */
    public static byte[] encrypt3DES(byte[] data, byte[] key) {
        return desTemplate(data, key, TripleDES_Algorithm, TripleDES_Transformation, true);
    }

    /**
     * 3DES解密Base64编码密文
     *
     * @param data Base64编码密文
     * @param key  24字节秘钥
     * @return 明文
     */
    public static byte[] decryptBase64_3DES(byte[] data, byte[] key) {
        try {
            return decrypt3DES(Base64.getDecoder().decode(data), key);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 3DES解密16进制密文
     *
     * @param data 16进制密文
     * @param key  24字节秘钥
     * @return 明文
     */
    public static byte[] decryptHexString3DES(String data, byte[] key) {
        return decrypt3DES(hexString2Bytes(data), key);
    }

    /**
     * 3DES解密
     *
     * @param data 密文
     * @param key  24字节密钥
     * @return 明文
     */
    public static byte[] decrypt3DES(byte[] data, byte[] key) {
        return desTemplate(data, key, TripleDES_Algorithm, TripleDES_Transformation, false);
    }

    /************************ AES加密相关 ***********************/
    /**
     * AES转变
     * <p>法算法名称/加密模式/填充方式</p>
     * <p>加密模式有：电子密码本模式ECB、加密块链模式CBC、加密反馈模式CFB、输出反馈模式OFB</p>
     * <p>填充方式有：NoPadding、ZerosPadding、PKCS5Padding</p>
     */
    public static        String AES_Transformation = "AES/ECB/NoPadding";
    private static final String AES_Algorithm      = "AES";


    /**
     * AES加密后转为Base64编码
     *
     * @param data 明文
     * @param key  16、24、32字节秘钥
     * @return Base64密文
     */
    public static byte[] encryptAES2Base64(byte[] data, byte[] key) {
        try {
            return Base64.getEncoder().encode(encryptAES(data, key));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * AES加密后转为16进制
     *
     * @param data 明文
     * @param key  16、24、32字节秘钥
     * @return 16进制密文
     */
    public static String encryptAES2HexString(byte[] data, byte[] key) {
        return bytes2HexString(encryptAES(data, key));
    }

    /**
     * AES加密
     *
     * @param data 明文
     * @param key  16、24、32字节秘钥
     * @return 密文
     */
    public static byte[] encryptAES(byte[] data, byte[] key) {
        return desTemplate(data, key, AES_Algorithm, AES_Transformation, true);
    }

    /**
     * AES解密Base64编码密文
     *
     * @param data Base64编码密文
     * @param key  16、24、32字节秘钥
     * @return 明文
     */
    public static byte[] decryptBase64AES(byte[] data, byte[] key) {
        try {
            return decryptAES(Base64.getDecoder().decode(data), key);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * AES解密16进制密文
     *
     * @param data 16进制密文
     * @param key  16、24、32字节秘钥
     * @return 明文
     */
    public static byte[] decryptHexStringAES(String data, byte[] key) {
        return decryptAES(hexString2Bytes(data), key);
    }

    /**
     * AES解密
     *
     * @param data 密文
     * @param key  16、24、32字节秘钥
     * @return 明文
     */
    public static byte[] decryptAES(byte[] data, byte[] key) {
        return desTemplate(data, key, AES_Algorithm, AES_Transformation, false);
    }

    /**
     * DES加密模板
     *
     * @param data           数据
     * @param key            秘钥
     * @param algorithm      加密算法
     * @param transformation 转变
     * @param isEncrypt      {@code true}: 加密 {@code false}: 解密
     * @return 密文或者明文，适用于DES，3DES，AES
     */
    public static byte[] desTemplate(byte[] data, byte[] key, String algorithm, String transformation, boolean isEncrypt) {
        if (data == null || data.length == 0 || key == null || key.length == 0) return null;
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);
            Cipher        cipher  = Cipher.getInstance(transformation);
            SecureRandom  random  = new SecureRandom();
            cipher.init(isEncrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, keySpec, random);
            return cipher.doFinal(data);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * byteArr转hexString
     * <p>例如：</p>
     * bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns 00A8
     *
     * @param bytes 字节数组
     * @return 16进制大写字符串
     */
    public static String bytes2HexString(byte[] bytes) {
        if (bytes == null) return null;
        int len = bytes.length;
        if (len <= 0) return null;
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = hexDigits[bytes[i] >>> 4 & 0x0f];
            ret[j++] = hexDigits[bytes[i] & 0x0f];
        }
        return new String(ret).toLowerCase();
    }

    /**
     * hexString转byteArr
     * <p>例如：</p>
     * hexString2Bytes("00A8") returns { 0, (byte) 0xA8 }
     *
     * @param hexString 十六进制字符串
     * @return 字节数组
     */
    public static byte[] hexString2Bytes(String hexString) {
        if (StringKit.isBlank(hexString)) return null;
        int len = hexString.length();
        if (len % 2 != 0) {
            hexString = "0" + hexString;
            len = len + 1;
        }
        char[] hexBytes = hexString.toUpperCase().toCharArray();
        byte[] ret      = new byte[len >> 1];
        for (int i = 0; i < len; i += 2) {
            ret[i >> 1] = (byte) (hex2Dec(hexBytes[i]) << 4 | hex2Dec(hexBytes[i + 1]));
        }
        return ret;
    }

}