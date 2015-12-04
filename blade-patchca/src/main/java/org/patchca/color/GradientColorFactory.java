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

public class GradientColorFactory implements ColorFactory {

	private Color start;
	private Color step;
	
	public GradientColorFactory() {
		start = new Color(192, 192, 0);
		step = new Color(192, 128, 128);
	}
	
	@Override
	public Color getColor(int index) {
		return new Color((start.getRed() + step.getRed() * index) % 256, 
				(start.getGreen() + step.getGreen() * index) % 256,  
				(start.getBlue() + step.getBlue() * index) % 256);
	}

	public void setStart(Color start) {
		this.start = start;
	}

	public void setStep(Color step) {
		this.step = step;
	}

}
