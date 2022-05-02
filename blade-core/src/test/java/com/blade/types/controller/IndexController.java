package com.blade.types.controller;

import com.blade.annotation.request.Body;
import com.blade.annotation.request.Query;
import com.blade.annotation.request.Path;
import com.blade.annotation.request.PathParam;
import com.blade.types.NotifyType;

/**
 * @author biezhi
 * @date 2017/9/19
 */
@Path
public class IndexController {

    public void findUser(@PathParam Long uid) {

    }

    public void users(@Query String name) {

    }

    public void notify(@Body NotifyType notifyType) {

    }

}
