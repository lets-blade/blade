package com.blade.security.web;

import com.blade.mvc.hook.Signature;
import com.blade.mvc.hook.WebHook;
import lombok.extern.slf4j.Slf4j;

/**
 * Xss middleware
 *
 * @author biezhi
 *         2017/6/5
 */
@Slf4j
public class XssMiddleware implements WebHook {

    @Override
    public boolean before(Signature signature) {

        return true;
    }

}