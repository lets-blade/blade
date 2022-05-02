package com.blade.mvc.route;

import lombok.Builder;
import lombok.Data;

/**
 * build regex path
 * three kinds of forms
 * <p>
 * 1. /path/        : common path
 * 2. /:param/      : common regex
 * 3, /name:regex   : regex path
 *
 * @author : <a href="mailto:chenchen_839@126.com" target="_blank">ccqy66</a>
 * @since
 */
@Data
@Builder
public class PathBuilderMeta {

    private String       name;
    private String       regex;
    private PathTypeEnum type;

    public enum PathTypeEnum {
        COMMON, PARAM, REGEX
    }

}
