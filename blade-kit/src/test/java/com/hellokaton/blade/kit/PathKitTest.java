package com.hellokaton.blade.kit;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author biezhi
 * @date 2017/9/20
 */
public class PathKitTest {

    @Test
    public void testFixPath() {
        String path = PathKit.fixPath("/a/b/");
        Assert.assertEquals("/a/b", path);

        String path2 = PathKit.cleanPath("/a//b//c//");
        Assert.assertEquals("/a/b/c/", path2);

    }

    @Test
    public void testRouteMatch() {
        PathKit.TrieRouter trieRouter = PathKit.createRoute();
        trieRouter.addRoute("/static/**");
        trieRouter.addRoute("/users/:userId");
        trieRouter.addRoute("/users/bg/**");

        Assert.assertTrue(trieRouter.match("/static/123"));
        Assert.assertTrue(trieRouter.match("/static/abcd/123"));
        Assert.assertTrue(trieRouter.match("/static"));
        Assert.assertTrue(trieRouter.match("/static/"));
        Assert.assertTrue(trieRouter.match("/users/123"));
        Assert.assertTrue(trieRouter.match("/users/bg/123"));
        Assert.assertTrue(trieRouter.match("/users/bg/123/456"));
    }

}
