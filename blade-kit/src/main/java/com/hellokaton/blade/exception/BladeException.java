/**
 * Copyright (c) 2018, biezhi 王爵nice (hellokaton@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hellokaton.blade.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Blade Exception, Base RuntimeException
 *
 * @author biezhi
 * 2017/5/31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BladeException extends RuntimeException {

    protected int status;
    protected String name;

    public BladeException(Throwable cause) {
        super(cause);
    }

    public BladeException(int status, String name) {
        this.status = status;
        this.name = name;
    }

    public BladeException(int status, String name, String message) {
        super(message);
        this.status = status;
        this.name = name;
    }

    public static BladeException wrapper(Exception e) {
        return new BladeException(e);
    }

}
