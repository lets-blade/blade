package com.blade.kit;

import org.junit.Test;
import java.io.File;
import java.util.Arrays;
import java.util.Base64;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * @author biezhi
 * @date 2017/9/20
 */
public class EncryptKitTest {

    @Test
    public void testMd5() throws Exception {
        String biezhiMD5 = "b3b71cd2fbee70ae501d024fe12a8fba";
        assertEquals(
                biezhiMD5,
                EncryptKit.md5("biezhi")
        );
        assertEquals(
                biezhiMD5,
                EncryptKit.md5("biezhi".getBytes())
        );
        assertTrue(
                Arrays.equals(
                        ConvertKit.hexString2Bytes(biezhiMD5),
                        EncryptKit.md5ToByte("biezhi".getBytes())
                )
        );
    }

    @Test
    public void testSHA1() throws Exception {
        String biezhiSHA1 = "2aa70e156cfa0d5928574ee2d8904fb1d9c74ea0";
        assertEquals(
                biezhiSHA1,
                EncryptKit.SHA1("biezhi")
        );
        assertEquals(
                biezhiSHA1,
                EncryptKit.SHA1("biezhi".getBytes())
        );
        assertTrue(
                Arrays.equals(
                        ConvertKit.hexString2Bytes(biezhiSHA1),
                        EncryptKit.SHA1ToByte("biezhi".getBytes())
                )
        );
    }

    @Test
    public void testSHA256() throws Exception {
        String biezhiSHA256 = "8fcbefd5c7a6c81165f587e46bffd821214a6fc1bc3842309f3aef6938e627a7";
        assertEquals(
                biezhiSHA256,
                EncryptKit.SHA256("biezhi")
        );
        assertEquals(
                biezhiSHA256,
                EncryptKit.SHA256("biezhi".getBytes())
        );
        assertTrue(
                Arrays.equals(
                        ConvertKit.hexString2Bytes(biezhiSHA256),
                        EncryptKit.SHA256ToByte("biezhi".getBytes())
                )
        );
    }

    @Test
    public void testSHA512() throws Exception {
        String biezhiSHA512 = "cf3b5d0ed88f7945edf687d730b9b7d8e7817c5dcff1b1907c77a8bf6ae8d85fd8e1c7973ef5a6391df6cfb647f891c19ccf3a7f21ecdc7ca18322131aba5cc6";
        assertEquals(
                biezhiSHA512,
                EncryptKit.SHA512("biezhi")
        );
        assertEquals(
                biezhiSHA512,
                EncryptKit.SHA512("biezhi".getBytes())
        );
        assertTrue(
                Arrays.equals(
                        ConvertKit.hexString2Bytes(biezhiSHA512),
                        EncryptKit.SHA512ToByte("biezhi".getBytes())
                )
        );
    }

    private String biezhiHmacSHA512 = "530b7ab6effd5c83a6c0d3c50938f02e218510134903a3a539ef5ccfc7720aaa5463c1d4d1cc0afde06e3ef3a6282741010795b4fdbdeb92e2a713f4af5f1e66";
    private String biezhiHmackey    = "biezhi";

    @Test
    public void testHmacMD5() throws Exception {
        String biezhiHmacMD5 = "c3b11ef266e3eab92d7870b43483640c";
        assertEquals(
                biezhiHmacMD5,
                EncryptKit.hmacMd5("biezhi", biezhiHmackey)
        );
        assertEquals(
                biezhiHmacMD5,
                EncryptKit.hmacMd5("biezhi".getBytes(), biezhiHmackey.getBytes())
        );
        assertTrue(
                Arrays.equals(
                        ConvertKit.hexString2Bytes(biezhiHmacMD5),
                        EncryptKit.hmacMd5ToByte("biezhi".getBytes(), biezhiHmackey.getBytes())
                )
        );
    }

    @Test
    public void testHmacSHA1() throws Exception {
        String biezhiHmacSHA1 = "39da095531c5801f4bfb0e9b0244b62e6229bfb5";
        assertEquals(
                biezhiHmacSHA1,
                EncryptKit.hmacSHA1("biezhi", biezhiHmackey)
        );
        assertEquals(
                biezhiHmacSHA1,
                EncryptKit.hmacSHA1("biezhi".getBytes(), biezhiHmackey.getBytes())
        );
        assertTrue(
                Arrays.equals(
                        ConvertKit.hexString2Bytes(biezhiHmacSHA1),
                        EncryptKit.hmacSHA1ToByte("biezhi".getBytes(), biezhiHmackey.getBytes())
                )
        );
    }

    @Test
    public void testHmacSHA256() throws Exception {
        String biezhiHmacSHA256 = "65e377c552b81d0978343e5fe7cf92bdc867d19a73d8479f0437db93b0f0b2af";
        assertEquals(
                biezhiHmacSHA256,
                EncryptKit.hmacSHA256("biezhi", biezhiHmackey)
        );
        assertEquals(
                biezhiHmacSHA256,
                EncryptKit.hmacSHA256("biezhi".getBytes(), biezhiHmackey.getBytes())
        );
        assertTrue(
                Arrays.equals(
                        ConvertKit.hexString2Bytes(biezhiHmacSHA256),
                        EncryptKit.hmacSHA256ToByte("biezhi".getBytes(), biezhiHmackey.getBytes())
                )
        );
    }

    @Test
    public void testHmacSHA512() throws Exception {
        assertEquals(
                biezhiHmacSHA512,
                EncryptKit.hmacSHA512("biezhi", biezhiHmackey)
        );
        assertEquals(
                biezhiHmacSHA512,
                EncryptKit.hmacSHA512("biezhi".getBytes(), biezhiHmackey.getBytes())
        );
        assertTrue(
                Arrays.equals(
                        ConvertKit.hexString2Bytes(biezhiHmacSHA512),
                        EncryptKit.hmacSHA512ToByte("biezhi".getBytes(), biezhiHmackey.getBytes())
                )
        );
    }


    private String dataDES      = "0008DB3345AB0223";
    private String keyDES       = "6801020304050607";
    private String resDES       = "1F7962581118F360".toLowerCase();
    private byte[] bytesDataDES = ConvertKit.hexString2Bytes(dataDES);
    private byte[] bytesKeyDES  = ConvertKit.hexString2Bytes(keyDES);
    private byte[] bytesResDES  = ConvertKit.hexString2Bytes(resDES);

    @Test
    public void testEncryptDES() throws Exception {
        assertTrue(
                Arrays.equals(
                        bytesResDES,
                        EncryptKit.DES(bytesDataDES, bytesKeyDES)
                )
        );
        assertEquals(
                resDES,
                EncryptKit.DES2HexString(bytesDataDES, bytesKeyDES)
        );
        assertTrue(
                Arrays.equals(
                        Base64.getEncoder().encode(bytesResDES),
                        EncryptKit.DES2Base64(bytesDataDES, bytesKeyDES)
                )
        );
    }

    @Test
    public void testDecryptDES() throws Exception {
        assertTrue(
                Arrays.equals(
                        bytesDataDES,
                        EncryptKit.decryptDES(bytesResDES, bytesKeyDES)
                )
        );
        assertTrue(
                Arrays.equals(
                        bytesDataDES,
                        EncryptKit.decryptHexStringDES(resDES, bytesKeyDES)
                )
        );
        assertTrue(
                Arrays.equals(
                        bytesDataDES,
                        EncryptKit.decryptBase64DES(Base64.getEncoder().encode(bytesResDES), bytesKeyDES)
                )
        );
    }

    private String data3DES      = "1111111111111111";
    private String key3DES       = "111111111111111111111111111111111111111111111111";
    private String res3DES       = "F40379AB9E0EC533".toLowerCase();
    private byte[] bytesDataDES3 = ConvertKit.hexString2Bytes(data3DES);
    private byte[] bytesKeyDES3  = ConvertKit.hexString2Bytes(key3DES);
    private byte[] bytesResDES3  = ConvertKit.hexString2Bytes(res3DES);

    @Test
    public void encrypt3DES() throws Exception {
        assertTrue(
                Arrays.equals(
                        bytesResDES3,
                        EncryptKit.encrypt3DES(bytesDataDES3, bytesKeyDES3)
                )
        );
        assertEquals(
                res3DES,
                EncryptKit.encrypt3DES2HexString(bytesDataDES3, bytesKeyDES3)
        );
        assertTrue(
                Arrays.equals(
                        Base64.getEncoder().encode(bytesResDES3),
                        EncryptKit.encrypt3DES2Base64(bytesDataDES3, bytesKeyDES3)
                )
        );
    }

    @Test
    public void decrypt3DES() throws Exception {
        assertTrue(
                Arrays.equals(
                        bytesDataDES3,
                        EncryptKit.decrypt3DES(bytesResDES3, bytesKeyDES3)
                )
        );
        assertTrue(
                Arrays.equals(
                        bytesDataDES3,
                        EncryptKit.decryptHexString3DES(res3DES, bytesKeyDES3)
                )
        );
        assertTrue(
                Arrays.equals(
                        bytesDataDES3,
                        EncryptKit.decryptBase64_3DES(Base64.getEncoder().encode(bytesResDES3), bytesKeyDES3)
                )
        );
    }

    private String dataAES      = "11111111111111111111111111111111";
    private String keyAES       = "11111111111111111111111111111111";
    private String resAES       = "E56E26F5608B8D268F2556E198A0E01B".toLowerCase();
    private byte[] bytesDataAES = ConvertKit.hexString2Bytes(dataAES);
    private byte[] bytesKeyAES  = ConvertKit.hexString2Bytes(keyAES);
    private byte[] bytesResAES  = ConvertKit.hexString2Bytes(resAES);

    @Test
    public void encryptAES() throws Exception {
        assertTrue(
                Arrays.equals(
                        bytesResAES,
                        EncryptKit.encryptAES(bytesDataAES, bytesKeyAES)
                )
        );
        assertEquals(
                resAES,
                EncryptKit.encryptAES2HexString(bytesDataAES, bytesKeyAES)
        );
        assertTrue(
                Arrays.equals(
                        Base64.getEncoder().encode(bytesResAES),
                        EncryptKit.encryptAES2Base64(bytesDataAES, bytesKeyAES)
                )
        );
    }

    @Test
    public void decryptAES() throws Exception {
        assertTrue(
                Arrays.equals(
                        bytesDataAES,
                        EncryptKit.decryptAES(bytesResAES, bytesKeyAES)
                )
        );
        assertTrue(
                Arrays.equals(
                        bytesDataAES,
                        EncryptKit.decryptHexStringAES(resAES, bytesKeyAES)
                )
        );
        assertTrue(
                Arrays.equals(
                        bytesDataAES,
                        EncryptKit.decryptBase64AES(Base64.getEncoder().encode(bytesResAES), bytesKeyAES)
                )
        );
    }

    @Test
    public void encryptMD5File() throws Exception {
        String fileMd5 = "7f138a09169b250e9dcb378140907378";
        assertEquals(
                fileMd5,
                EncryptKit.md5File(new File(EncryptKitTest.class.getResource("/assets/MD5.txt").getPath()))
        );
    }

}
