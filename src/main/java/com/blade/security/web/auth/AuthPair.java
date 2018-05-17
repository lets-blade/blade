package com.blade.security.web.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author biezhi
 * @date 2018/5/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthPair {

    private String user;
    private String value;

}
