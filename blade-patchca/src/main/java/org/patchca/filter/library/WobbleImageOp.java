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
package org.patchca.filter.library;


public class WobbleImageOp extends AbstractTransformImageOp {

	private double xWavelength;
	private double yWavelength;
	private double xAmplitude;
	private double yAmplitude;
	private double xRandom;
	private double yRandom;
	private double xScale;
	private double yScale;

	public WobbleImageOp() {
		xWavelength = 15;
		yWavelength = 15;
		xAmplitude = 4.0;
		yAmplitude = 3.0;
		xScale = 1.0;
		yScale = 1.0;
		xRandom = 3 * Math.random();
		yRandom = 10 * Math.random();
	}

	public double getxWavelength() {
		return xWavelength;
	}

	public void setxWavelength(double xWavelength) {
		this.xWavelength = xWavelength;
	}

	public double getyWavelength() {
		return yWavelength;
	}

	public void setyWavelength(double yWavelength) {
		this.yWavelength = yWavelength;
	}

	public double getxAmplitude() {
		return xAmplitude;
	}

	public void setxAmplitude(double xAmplitude) {
		this.xAmplitude = xAmplitude;
	}

	public double getyAmplitude() {
		return yAmplitude;
	}

	public void setyAmplitude(double yAmplitude) {
		this.yAmplitude = yAmplitude;
	}

	public double getxScale() {
		return xScale;
	}

	public void setxScale(double xScale) {
		this.xScale = xScale;
	}

	public double getyScale() {
		return yScale;
	}

	public void setyScale(double yScale) {
		this.yScale = yScale;
	}

	@Override
	protected void transform(int x, int y, double[] t) {
		double tx = Math.cos((double) (xScale * x + y) / xWavelength + xRandom);
		double ty = Math.sin((double) (yScale * y + x) / yWavelength + yRandom);
		t[0] = x + xAmplitude * tx;
		t[1] = y + yAmplitude * ty;

	}

}
