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
package blade.cache;

import java.util.Set;

/**
 * 
 * <p>
 * Regularly clean cache threads
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class CacheCleaner<K, V> extends Thread {
	
	/**
	 * Clean interval
	 */
	private long _cleanInterval;
	
	/**
	 * Is sleep
	 */
	private boolean _sleep = false;
	
	/**
	 * Set cleanInterval and initialize
	 *
	 * @param cleanInterval
	 */
	public CacheCleaner(long cleanInterval) {
		_cleanInterval = cleanInterval;
		setName(this.getClass().getName());
		setDaemon(true);
	}

	/**
	 * Set clean interval
	 * 
	 * @param cleanInterval
	 */
	public void setCleanInterval(long cleanInterval) {
		_cleanInterval = cleanInterval;
		synchronized (this) {
			if (_sleep) {
				interrupt();
			}
		}
	}

	/**
	 * Run cache clean
	 */
	@Override
	public void run() {
		while (true) {
			try {
				try {
					sleep(_cleanInterval);
				} catch (Throwable t) {
				} finally {
					_sleep = false;
				}
				CacheManager cacheFactory = CacheManager.getInstance();
				Set<String> cacheIds = cacheFactory.getCacheIds();
				if (null != cacheIds) {
					for (String cacheId : cacheIds) {
						Cache<K, V> cache = cacheFactory.getCache(cacheId);
						if (cache != null) {
							cache.clear();
						}
						yield();
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
			_sleep = true;
//			try {
//				sleep(_cleanInterval);
//			} catch (Throwable t) {
//			} finally {
//				_sleep = false;
//			}
		}
	}

}