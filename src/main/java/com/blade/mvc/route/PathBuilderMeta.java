package com.blade.mvc.route;

import lombok.Data;

/**
 * build regex path
 * three kinds of forms
 *
 * 1. /path/        : common path
 * 2. /:param/      : common regex
 * 3, /name:regex   : regex path
 *
 * @author : <a href="mailto:chenchen_839@126.com" target="_blank">ccqy66</a>
 * @since
 */
@Data
public class PathBuilderMeta {

    private String name;
    private String regex;
    private PathTypeEnum type;

    public PathBuilderMeta(PathBuilder pathBuilder) {
        this.name   = pathBuilder.name;
        this.regex  = pathBuilder.regex;
        this.type   = pathBuilder.type;
    }

    public enum PathTypeEnum{
        COMMON,PARAM,REGEX
    }
    public static class PathBuilder {
        private String name;
        private String regex;
        private PathTypeEnum type;
        public PathBuilder withName(String name) {
            this.name = name;
            return this;
        }
        public PathBuilder withRegex(String regex) {
            this.regex = regex;
            return this;
        }
        public PathBuilder withType(PathTypeEnum type) {
            this.type = type;
            return this;
        }
        public PathBuilderMeta build() {
            return new PathBuilderMeta(this);
        }
    }
}
