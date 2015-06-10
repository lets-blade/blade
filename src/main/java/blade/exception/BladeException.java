/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package blade.exception;

import javax.servlet.http.HttpServletResponse;

/**
 * blade运行时异常基类
 * 
 * @author	biezhi
 * @since	1.0
 *
 */
public class BladeException extends RuntimeException {
	
    private static final long serialVersionUID = 1L;

    private int statusCode = HttpServletResponse.SC_OK;
    private String body = null;

    public BladeException() {
        super();
    }

    public BladeException(int statusCode) {
        this.statusCode = statusCode;
    }

    public BladeException(String body) {
        this.body = body;
    }

    public BladeException(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    /**
     * @return the statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }


    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

}
