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

/**
 * Bean Copy Exception
 *
 * @author biezhi
 * @date 2018/4/9
 */
public class BeanCopyException extends RuntimeException {

    public BeanCopyException() {
    }

    public BeanCopyException(String message) {
        super(message);
    }

    public BeanCopyException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanCopyException(Throwable cause) {
        super(cause);
    }
}
