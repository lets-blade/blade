package com.hellokaton.blade.kit;

import com.hellokaton.blade.mvc.multipart.MimeType;
import lombok.experimental.UtilityClass;

import java.net.URLConnection;

@UtilityClass
public class MimeTypeKit {

    public static String parse(String fileName) {
        try {
            String mimeType = URLConnection.guessContentTypeFromName(fileName);
            if (StringKit.isNotEmpty(mimeType)) {
                return mimeType;
            }
            String ext = fileExt(fileName);
            if (null == ext) {
                return null;
            }
            return MimeType.get(ext);
        } catch (Exception e) {
            return null;
        }
    }

    public static String fileExt(String fname) {
        if (StringKit.isBlank(fname) || fname.indexOf('.') == -1) {
            return null;
        }
        return fname.substring(fname.lastIndexOf('.') + 1);
    }


}
