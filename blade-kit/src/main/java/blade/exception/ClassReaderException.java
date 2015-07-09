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

/**
 * 类读取异常
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class ClassReaderException extends RuntimeException {

	private static final long serialVersionUID = -1L;
	
	public ClassReaderException() {
		super();
	}
	
	public ClassReaderException(Exception e) {
		super(e);
	}
	
	public ClassReaderException(String msg) {
		super(msg);
	}
	
	public ClassReaderException(String msg, Exception e) {
		super(msg, e);
	}
	
	public Throwable fillInStackTrace() {
        return null;
    }
}