/**
 * Copyright (c) 2016, biezhi 王爵 (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.banner;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

public class BannerFont {

    private final int height;
    private final int heightWithoutDescenders;
    private final int maxLine;
    private final int smushMode;
    private final char font[][][] = new char[256][][];
    private final String fontName;

    public static BannerFont load() throws IOException {
        return load("standard.flf");
    }

    public static BannerFont load(String fontName) throws IOException {
        return new BannerFont(BannerFont.class.getResource('/' + fontName));
    }

    public BannerFont(URL url) throws IOException {
        this(fontLines(url));
    }

    private BannerFont(Iterator<String> lines) {
        String dummyS = lines.next();
        StringTokenizer st = new StringTokenizer(dummyS, " ");
        String s = st.nextToken();
        char hardblank = s.charAt(s.length() - 1);
        height = Integer.parseInt(st.nextToken());
        heightWithoutDescenders = Integer.parseInt(st.nextToken());
        maxLine = Integer.parseInt(st.nextToken());
        smushMode = Integer.parseInt(st.nextToken());
        int dummyI = Integer.parseInt(st.nextToken());

        st = new StringTokenizer(lines.next(), " ");
        if (st.hasMoreElements())
            fontName = st.nextToken();
        else
            fontName = "";

        for (int i = 0; i < dummyI - 1; i++) {
            dummyS = lines.next();
        }
        for (int i = 32; i < 256; i++) {
            for (int h = 0; h < height; h++) {
                dummyS = lines.hasNext() ? lines.next() : null;
                if (dummyS == null) {
                    i = 256;
                } else {
                    // System.out.println(dummyS);
                    int iNormal = i;
                    boolean abnormal = true;
                    if (h == 0) {
                        try {
                            i = Integer.parseInt(dummyS);
                        } catch (NumberFormatException e) {
                            abnormal = false;
                        }
                        if (abnormal) {
                            dummyS = lines.next();
                        } else {
                            i = iNormal;
                        }
                    }
                    if (h == 0)
                        font[i] = new char[height][];
                    int t = dummyS.length() - 1 - ((h == height - 1) ? 1 : 0);
                    if (height == 1)
                        t++;
                    font[i][h] = new char[t];
                    for (int l = 0; l < t; l++) {
                        char a = dummyS.charAt(l);
                        font[i][h][l] = (a == hardblank) ? ' ' : a;
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private static Iterator<String> fontLines(URL url) throws IOException {
        InputStream ins = url.openStream();
        DataInputStream dis = new DataInputStream(new BufferedInputStream(ins));
        ArrayList<String> list = new ArrayList<String>();
        String line = null;
        while (true) {
            line = dis.readLine();
            if (line != null) {
                list.add(line);
            } else {
                break;
            }
        }
        dis.close();
        return list.iterator();
    }

    public int getHeight() {
        return height;
    }

    public int getMaxLine() {
        return maxLine;
    }

    public int getSmushMode() {
        return smushMode;
    }

    public int getHeightWithoutDescenders() {
        return heightWithoutDescenders;
    }

    public String getFontName() {
        return fontName;
    }

    private String getCharLineString(int c, int l) {
        if (font[c][l] == null) {
            return null;
        } else {
            return new String(font[c][l]);
        }
    }

    private static String scroll(String text, int offset) {
        StringBuffer result = new StringBuffer();
        StringBuffer shift = new StringBuffer();
        for (int i = 0; i < offset; i++) {
            shift.append(' ');
        }
        StringTokenizer st = new StringTokenizer(text, "\r\n");
        while (st.hasMoreElements()) {
            result.append(shift.toString() + st.nextToken() + "\r\n");
        }
        return result.toString();
    }

    private static String addLine(String text, String line, boolean leftJustify, int splitWidth) {
        String result = text;
        if (leftJustify) {
            result += line;
        } else {
            result += scroll(line, (splitWidth / 2 - width(line) / 2));
        }
        return result;
    }

    public String asAscii(String text) {
        return asAscii(text, false, true, 1024);
    }

    public String asAscii(String text, boolean splitAtWord, boolean leftJustify, int splitWidth) {
        String result = "";
        StringTokenizer st = new StringTokenizer(text, " ");
        if (splitAtWord) {
            while (st.hasMoreElements()) {
                result = addLine(result, convertOneLine(st.nextToken()), leftJustify, splitWidth);
            }
        } else {
            String line = "";
            while (st.hasMoreElements()) {
                String w = st.nextToken(), word;
                if (line.length() == 0) {
                    word = w;
                } else {
                    word = ' ' + w;
                }
                String newLine = append(line, word);
                if ((width(newLine) > splitWidth) && (line.length() > 0)) {
                    result = addLine(result, line + "\r\n", leftJustify, splitWidth);
                    line = append("", w);
                } else {
                    line = newLine;
                }
            }
            if (line.length() > 0) {
                result = addLine(result, line + "\r\n", leftJustify, splitWidth);
            }
        }
        return result;
    }

    private static int width(String text) {
        int w = 0;
        StringTokenizer st = new StringTokenizer(text, "\r\n");
        while (st.hasMoreElements())
            w = Math.max(w, st.nextToken().length());
        return w;
    }

    private String convertOneLine(String text) {
        String result = "";
        for (int l = 0; l < height; l++) { // for each line
            for (int c = 0; c < text.length(); c++) // for each char
                result += getCharLineString((int) text.charAt(c), l);
            result += "\r\n";
        }
        return result;
    }

    private String append(String text, String end) {
        StringBuffer result = new StringBuffer();
        int h = 0;
        if (text.length() == 0) {
            for (int i = 0; i < height; i++) {
                text += " \r\n";
            }
        }
        StringTokenizer st = new StringTokenizer(text, "\r\n");
        int count = st.countTokens();
        while (st.hasMoreElements()) {
            result.append(st.nextToken());
            for (int c = 0; c < end.length(); c++) {
                result.append(getCharLineString((int) end.charAt(c), h));
            }
            if (h < (count - 2)) {
                result.append("\r\n");
            }
            h++;
        }
        return result.toString();
    }
}
