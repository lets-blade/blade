package com.hellokaton.blade.mvc.route;

import com.hellokaton.blade.kit.StringKit;

import java.util.Arrays;

/**
 * build regex path
 *
 * @author <a href="mailto:chenchen_839@126.com" target="_blank">ccqy66</a>
 * @since 2.0
 */
public class PathRegexBuilder {

    private final static String PATH_VARIABLE_REPLACE = "([^/]+)";
    private final static String DEFAULT_SEPARATOR     = "/";
    private final static String DEFAULT_START         = "(/";
    private final static String DEFAULT_END           = ")|";

    private final StringBuilder pathBuilder;
    private final String        separator;

    public PathRegexBuilder() {
        this.pathBuilder = new StringBuilder(DEFAULT_START);
        this.separator = DEFAULT_SEPARATOR;
    }

    private void join(PathBuilderMeta row) {
        if (row.getType() == PathBuilderMeta.PathTypeEnum.COMMON) {
            pathBuilder.append(row.getName()).append(separator);
        } else {
            pathBuilder.append(row.getRegex()).append(separator);
        }
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
                .map(this::rowToPath)
                .forEach(this::join);

        pathBuilder.deleteCharAt(pathBuilder.length() - 1);
        return build(true);
    }

    private PathBuilderMeta rowToPath(String row) {
        if (!row.contains(":")) {
            return PathBuilderMeta.builder()
                    .name(row)
                    .type(PathBuilderMeta.PathTypeEnum.COMMON)
                    .build();
        }
        String[] itemPath = row.split(":");
        if (StringKit.isBlank(itemPath[0])) {
            return PathBuilderMeta.builder()
                    .name(itemPath[1])
                    .regex(PATH_VARIABLE_REPLACE)
                    .type(PathBuilderMeta.PathTypeEnum.PARAM)
                    .build();
        } else {
            return PathBuilderMeta.builder()
                    .name(itemPath[0])
                    .regex("(" + itemPath[1] + ")")
                    .type(PathBuilderMeta.PathTypeEnum.REGEX)
                    .build();
        }
    }
}
