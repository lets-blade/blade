package com.blade.kit;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author biezhi
 * @date 2017/9/20
 */
public class PathKitTest {

    @Test
    public void testFixPath(){
        String path = PathKit.fixPath("/a/b/");
        Assert.assertEquals("/a/b", path);

        String path2 = PathKit.cleanPath("/a//b//c//");
        Assert.assertEquals("/a/b/c/", path2);

    }

}
