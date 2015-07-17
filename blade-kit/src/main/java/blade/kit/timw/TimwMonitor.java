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
package blade.kit.timw;

import blade.kit.TimwKit;

/**
 * 计时器
 * <p>
 * 用于统计计时的类
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class TimwMonitor {
	
	/**
	 * 计时器
	 */
	private TimwCounter timeCounter;
	
	/**
	 * 均值器
	 */
	private TimwKit timwKit;
	
	TimwMonitor() {
		this.timeCounter = new TimwCounter();
		this.timwKit = new TimwKit();
	}
	
	
	public TimwCounter getTimeCounter() {
		return timeCounter;
	}

	public TimwKit getTimwKit() {
		return timwKit;
	}

	/**
	 * 一个计时开始
	 */
	public void start() {
		timeCounter.start();
	}

	/**
	 * 一个计时结束
	 */
	public void end() {
		long time = timeCounter.duration();
		timwKit.add(time);
	}

	/**
	 * 一个计时结束,并且启动下次计时。
	 */
	public long endAndRestart() {
		long time = timeCounter.durationRestart();
		timwKit.add(time);
		return time;
	}

	public int size() {
		return timwKit.size();
	}
	
	/**
	 * 求全部计时均值
	 */
	public Number avg() {
		return timwKit.avg();
	}
	
	/**
	 * 求全部计时均值
	 */
	public Number current() {
		return timwKit.current();
	}

	/**
	 * 打印全部时间值
	 */
	public String render() {
		return timwKit.print();
	}

	/**
	 * 打印全部时间值
	 */
	public String renderAvg() {
		return timwKit.printAvg();
	}

	/**
	 * 清楚数据
	 */
	public void clear() {
		timwKit.clear();
	}

}
