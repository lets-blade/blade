package com.blade.types.controller;

import com.blade.mvc.annotation.BodyParam;
import com.blade.mvc.annotation.Param;
import com.blade.mvc.annotation.Path;
import com.blade.mvc.annotation.PathParam;
import com.blade.types.NotifyType;

/**
 * @author biezhi
 * @date 2017/9/19
 */
@Path
public class IndexController {

    public void findUser(@PathParam Long uid) {

    }

    public void users(@Param String name) {

    }

    public void notify(@BodyParam NotifyType notifyType) {

    }

}
