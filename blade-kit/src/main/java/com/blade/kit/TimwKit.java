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
package com.blade.kit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 计数均衡器
 * <p>
 * 用于计数和平均值计算
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class TimwKit {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TimwKit.class);
	
	private ArrayList<Number> numList = new ArrayList<Number>();

	/**
	 * 添加一个数字
	 *
	 * @param num
	 */
	public synchronized void add(Number num) {
		numList.add(num);
	}

	/**
	 * 清除全部
	 */
	public void clear() {
		numList.clear();
	}

	/**
	 * 返回参与均值计算的数字个数
	 *
	 * @return
	 */
	public int size() {
		return numList.size();
	}

	public List<Number> numbers() {
		return numList;
	}
	
	public Number current() {
		return numList.get(numList.size() - 1);
	}
	
	/**
	 * 获取平均数
	 *
	 * @return
	 */
	public Number avg() {
		if (numList.size() == 0) {
			return 0;
		} else {
			Float sum = 0f;
			for (int i = 0, size = numList.size(); i < size; i++) {
				sum = sum.floatValue() + numList.get(i).floatValue();
			}
			return sum / numList.size();
		}
	}

	/**
	 * 打印数字列
	 *
	 * @return
	 */
	public String print() {
		String str = "Execute count = " + size() + ", elapsed time: " + numList + " ms.";
		LOGGER.debug(str);
		return str;
	}
	
	/**
	 * 打印数字列
	 *
	 * @return
	 */
	public String printAvg() {
		Number number = avg();
		BigDecimal b = new BigDecimal(number.doubleValue());
		double avg = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
		String str = "average time cost: " + avg + " ms";
		LOGGER.debug(str);
		return str;
	}

}