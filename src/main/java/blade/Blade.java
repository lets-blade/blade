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
package blade;

import blade.exception.BladeException;

/**
 * Blade Core Class
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 *
 */
public final class Blade extends BladeBase{
	
    public static void halt() {
        throw new BladeException();
    }
    
    public static void halt(int status) {
        throw new BladeException(status);
    }
    
    public static void halt(String body) {
        throw new BladeException(body);
    }

    public static void halt(int status, String body) {
        throw new BladeException(status, body);
    }
    
}
