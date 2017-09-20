package com.blade.kit;

import org.junit.Test;

import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * @author biezhi
 * @date 2017/9/20
 */
public class ConvertKitTest {

    private byte[] mBytes    = new byte[]{0x00, 0x08, (byte) 0xdb, 0x33, 0x45, (byte) 0xab, 0x02, 0x23};
    private String hexString = "0008DB3345AB0223";

    private char[] mChars1 = new char[]{'0', '1', '2'};
    private byte[] mBytes1 = new byte[]{48, 49, 50};

    @Test
    public void bytes2HexString() throws Exception {
        assertEquals(
                hexString,
                ConvertKit.bytes2HexString(mBytes)
        );
    }

    @Test
    public void hexString2Bytes() throws Exception {
        assertTrue(
                Arrays.equals(
                        mBytes,
                        ConvertKit.hexString2Bytes(hexString)
                )
        );
    }

    @Test
    public void chars2Bytes() throws Exception {
        assertTrue(
                Arrays.equals(
                        mBytes1,
                        ConvertKit.chars2Bytes(mChars1)
                )
        );
    }

    @Test
    public void bytes2Chars() throws Exception {
        assertTrue(
                Arrays.equals(
                        mChars1,
                        ConvertKit.bytes2Chars(mBytes1)
                )
        );
    }

    @Test
    public void byte2MemorySize() throws Exception {
        assertEquals(
                1024,
                ConvertKit.byte2MemorySize(MemoryConst.GB, MemoryConst.MB),
                0.001
        );
    }

    @Test
    public void byte2FitMemorySize() throws Exception {
        assertEquals(
                "3.098MB",
                ConvertKit.byte2FitMemorySize(1024 * 1024 * 3 + 1024 * 100)
        );
    }

    @Test
    public void bytes2Bits_bits2Bytes() throws Exception {
        assertEquals(
                "0111111111111010",
                ConvertKit.bytes2Bits(new byte[]{0x7F, (byte) 0xFA})
        );
        assertEquals(
                "0111111111111010",
                ConvertKit.bytes2Bits(ConvertKit.bits2Bytes("111111111111010"))
        );
    }

    @Test
    public void inputStream2Bytes_bytes2InputStream() throws Exception {
        String string = "this is test string";
        assertTrue(
                Arrays.equals(
                        string.getBytes("UTF-8"),
                        ConvertKit.inputStream2Bytes(ConvertKit.bytes2InputStream(string.getBytes("UTF-8")))
                )
        );
    }

    @Test
    public void inputStream2String_string2InputStream() throws Exception {
        String string = "this is test string";
        assertEquals(
                string,
                ConvertKit.inputStream2String(ConvertKit.string2InputStream(string, "UTF-8"), "UTF-8")
        );
    }

}
