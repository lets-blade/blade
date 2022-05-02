package com.blade.kit;

import org.junit.Assert;
import org.junit.Test;

public class MimeTypeTest {

    @Test
    public void testMimeType() {
        String mimeType = MimeTypeKit.parse("a.png");
        org.junit.Assert.assertEquals("image/png", mimeType);

        mimeType = MimeTypeKit.parse("a.txt");
        org.junit.Assert.assertEquals("text/plain", mimeType);

        mimeType = MimeTypeKit.parse("a.pdf");
        Assert.assertEquals("application/pdf", mimeType);
    }

}
