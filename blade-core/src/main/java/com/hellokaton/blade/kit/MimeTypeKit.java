package com.hellokaton.blade.kit;

import com.hellokaton.blade.mvc.multipart.MimeType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MimeTypeKit {

    public static String parse(String fileName) {
        String ext = fileExt(fileName);
        if (null == ext) {
            return null;
        }
        return MimeType.get(ext);
    }

    public static String fileExt(String fname) {
        if (StringKit.isBlank(fname) || fname.indexOf('.') == -1) {
            return null;
        }
        return fname.substring(fname.lastIndexOf('.') + 1);
    }


}
