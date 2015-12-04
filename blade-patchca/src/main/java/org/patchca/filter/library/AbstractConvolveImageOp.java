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


public abstract class AbstractConvolveImageOp extends AbstractImageOp {

	private float[][] matrix; 
	
	protected AbstractConvolveImageOp(float[][] matrix) {
		this.matrix = matrix;
	}

	@Override
	protected void filter(int[] inPixels, int[] outPixels, int width, int height) {
		long time1 = System.currentTimeMillis();
		int matrixWidth = matrix[0].length;
		int matrixHeight = matrix.length;
		int mattrixLeft = - matrixWidth / 2; 
		int matrixTop = - matrixHeight / 2;
		for (int y = 0; y < height; y++) {
			int ytop = y + matrixTop;
			int ybottom = y + matrixTop + matrixHeight; 
			for (int x = 0; x < width; x++) {
				float[] sum = {0.5f, 0.5f, 0.5f, 0.5f};
				int xleft = x + mattrixLeft;
				int xright = x + mattrixLeft + matrixWidth;
				int matrixY = 0;
				for (int my = ytop; my < ybottom; my ++, matrixY++) {
					int matrixX = 0;
					for (int mx = xleft; mx < xright; mx ++, matrixX ++) {
						int pixel = getPixel(inPixels, mx, my, width, height, EDGE_ZERO);
						float m = matrix[matrixY][matrixX];
						sum[0] += m * ((pixel >> 24) & 0xff);
						sum[1] += m * ((pixel >> 16) & 0xff);
						sum[2] += m * ((pixel >> 8) & 0xff);
						sum[3] += m * (pixel & 0xff);
					}
				}
				outPixels[x + y * width] = (limitByte((int)sum[0]) << 24) | (limitByte((int)sum[1]) << 16) | (limitByte((int)sum[2]) << 8) | (limitByte((int)sum[3]));				
			}
		}
		long time2 = System.currentTimeMillis() - time1;
		//System.out.println("AbstractConvolveImageOp " + time2);
		
	}

	
}
