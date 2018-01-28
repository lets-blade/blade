package com.blade.mvc.route;

import com.blade.kit.StringKit;

import java.util.Arrays;

/**
 * build regex path
 * @author : <a href="mailto:chenchen_839@126.com" target="_blank">ccqy66</a>
 * @since
 */
public class PathRegexBuilder {
    private final static String PATH_VARIABLE_REPLACE = "([^/]+)";
    private final static String DEFAULT_SEPARATOR = "/";
    private final static String DEFAULT_START = "(/";
    private final static String DEFAULT_END   =")|";

    private final StringBuilder pathBuilder;
    private final String separator;

    public PathRegexBuilder(StringBuilder pathBuilder, String separator) {
        this.pathBuilder = pathBuilder;
        this.separator = separator;
    }

    public PathRegexBuilder(StringBuilder pathBuilder) {
        this.pathBuilder = pathBuilder;
        this.separator = DEFAULT_SEPARATOR;
    }

    public PathRegexBuilder() {
        this.pathBuilder = new StringBuilder(DEFAULT_START);
        this.separator = DEFAULT_SEPARATOR;
    }

    public PathRegexBuilder join(String content) {
        pathBuilder.append(content).append(separator);
        return this;
    }

    public String build(boolean isSuffix) {
        if (isSuffix) {
            pathBuilder.append(DEFAULT_END);
        }
        return pathBuilder.toString();
    }

    public String parsePath(String path) {
        if (StringKit.isBlank(path)) return path;
        String[] pathModule = path.split("/");
        Arrays.stream(pathModule)
                .filter(row -> !row.isEmpty())
                .map(row -> {
                    if (row.indexOf(":") == -1) {
                        return new PathBuilderMeta.PathBuilder()
                                .withName(row)
                                .withType(PathBuilderMeta.PathTypeEnum.COMMON)
                                .build();
                    }
                    String[] itemPath = row.split(":");
                    if (StringKit.isBlank(itemPath[0])) {
                        return new PathBuilderMeta.PathBuilder()
                                .withName(itemPath[1])
                                .withRegex(PATH_VARIABLE_REPLACE)
                                .withType(PathBuilderMeta.PathTypeEnum.PARAM)
                                .build();
                    }else {
                        return new PathBuilderMeta.PathBuilder()
                                .withName(itemPath[0])
                                .withRegex("("+itemPath[1]+")")
                                .withType(PathBuilderMeta.PathTypeEnum.REGEX)
                                .build();
                    }
                })
                .forEach(row -> {
                    if (row.getType() == PathBuilderMeta.PathTypeEnum.COMMON) {
                        join(row.getName());
                    }else {
                        join(row.getRegex());
                    }
                });
        pathBuilder.deleteCharAt(pathBuilder.length()-1);
        return build(true);
    }
}
