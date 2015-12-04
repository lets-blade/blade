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
package org.patchca.text.renderer;

import java.util.ArrayList;

public class TextString {

	private ArrayList<TextCharacter> characters = new ArrayList<TextCharacter>();
	
	public void clear() {
		characters.clear();
	}
	
	public void addCharacter(TextCharacter tc) {
		characters.add(tc);
	}
	
	public ArrayList<TextCharacter> getCharacters() {
		return characters;
	}
	
	public double getWidth() {
		double minx = 0;
		double maxx = 0;
		boolean first = true;
		for (TextCharacter tc : characters) {
			if (first) {
				minx = tc.getX();
				maxx = tc.getX() + tc.getWidth();
				first = false;
			} else {
				if (minx > tc.getX()) {
					minx = tc.getX(); 
				}
				if (maxx < tc.getX() + tc.getWidth()) {
					maxx = tc.getX() + tc.getWidth();
				}
			}
			
		}
		return maxx - minx;
	}
	
	public double getHeight() {
		double miny = 0;
		double maxy = 0;
		boolean first = true;
		for (TextCharacter tc : characters) {
			if (first) {
				miny = tc.getY();
				maxy = tc.getY() + tc.getHeight();
				first = false;
			} else {
				if (miny > tc.getY()) {
					miny = tc.getY(); 
				}
				if (maxy < tc.getY() + tc.getHeight()) {
					maxy = tc.getY() + tc.getHeight();
				}
			}
			
		}
		return maxy - miny;
	}

}
