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
package org.patchca.word;

import java.util.Random;

public class AdaptiveRandomWordFactory extends RandomWordFactory {

	protected String wideCharacters;

	public void setWideCharacters(String wideCharacters) {
		this.wideCharacters = wideCharacters;
	}

	public AdaptiveRandomWordFactory() {
		characters = "absdegkmnopwx23456789";
		wideCharacters = "mw";
	}
	
	@Override
	public String getNextWord() {
		Random rnd = new Random();
		StringBuffer sb = new StringBuffer();
		StringBuffer chars = new StringBuffer(characters);
		int l = minLength + (maxLength > minLength ? rnd.nextInt(maxLength - minLength) : 0);
		for (int i = 0; i < l; i++) {
			int j = rnd.nextInt(chars.length());
			char c = chars.charAt(j);
			if (wideCharacters.indexOf(c) != -1) {
				for (int k = 0; k < wideCharacters.length(); k++) {
					int idx = chars.indexOf(String.valueOf(wideCharacters.charAt(k)));
					if (idx != -1) {
						chars.deleteCharAt(idx);
					}
				}
			}
			sb.append(c);
		}
		return sb.toString();
	}

}
