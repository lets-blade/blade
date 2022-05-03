package com.hellokaton.blade.types.controller;

import com.hellokaton.blade.annotation.request.Body;
import com.hellokaton.blade.annotation.request.Query;
import com.hellokaton.blade.annotation.Path;
import com.hellokaton.blade.annotation.request.PathParam;
import com.hellokaton.blade.types.NotifyType;

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
