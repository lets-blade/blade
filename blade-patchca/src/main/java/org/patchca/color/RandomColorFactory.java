/*
 * Copyright (c) 2009 Piotr Piastucki
 * 
 * This file is part of Patchca CAPTCHA library.
 * 
 *  Patchca is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Patchca is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Patchca. If not, see <http://www.gnu.org/licenses/>.
 */
package org.patchca.color;

import java.awt.*;
import java.util.Random;

public class RandomColorFactory implements ColorFactory {

	private Color min;
	private Color max;
	private Color color;

	public RandomColorFactory() {
		min = new Color(20,40,80);
		max = new Color(21,50,140);
	}
	
	public void setMin(Color min) {
		this.min = min;
	}

	public void setMax(Color max) {
		this.max = max;
	}

	@Override
	public Color getColor(int index) {
		if (color == null) {
			Random r = new Random();
			color = new Color( min.getRed() + r.nextInt((max.getRed() - min.getRed())),
					min.getGreen() + r.nextInt((max.getGreen() - min.getGreen())),
					min.getBlue() + r.nextInt((max.getBlue() - min.getBlue())));
		}
		return color;
	}

}
