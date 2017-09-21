package com.blade.kit;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author biezhi
 * @date 2017/9/21
 */
public class IOKitTest {

    @Test
    public void testCloseQuietly() throws FileNotFoundException {
        InputStream ins = IOKitTest.class.getResourceAsStream("/app.properties");
        IOKit.closeQuietly(ins);

        IOKit.closeQuietly(null);
    }

    @Test
    public void testReadToString() throws IOException {
        String content = IOKit.readToString(IOKitTest.class.getResourceAsStream("/app.properties"));
        Assert.assertEquals(true, StringKit.isNotBlank(content));

        content = IOKit.readToString(IOKitTest.class.getResource("/app.properties").getPath());
        Assert.assertEquals(true, StringKit.isNotBlank(content));
    }

    @Test
    public void testCopyFile() throws IOException {
        IOKit.copyFile(new File(IOKitTest.class.getResource("/app.properties").getPath()), new File("./tmp.properties"));
        File tmp = new File("./tmp.properties");
        Assert.assertEquals(true, tmp.exists() && tmp.isFile());
        tmp.delete();
    }

}
