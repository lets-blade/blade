package com.hellokaton.blade.kit;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * PathKit URL
 *
 * @author <a href="mailto:hellokaton@gmail.com" target="_blank">hellokaton</a>
 * @since 1.5
 */
@UtilityClass
public class PathKit {

    public static final String VAR_REGEXP = ":(\\w+)";
    public static final String VAR_REPLACE = "([^#/?.]+)";
    private static final String SLASH = "/";
    public static final Pattern VAR_REGEXP_PATTERN = Pattern.compile(VAR_REGEXP);
    private static final Pattern VAR_FIXPATH_PATTERN = Pattern.compile("\\s");

    public static String fixPath(String path) {
        if (null == path) {
            return SLASH;
        }
        if (path.charAt(0) != '/') {
            path = SLASH + path;
        }
        if (path.length() > 1 && path.endsWith(SLASH)) {
            path = path.substring(0, path.length() - 1);
        }
        if (!path.contains("\\s")) {
            return path;
        }
        return VAR_FIXPATH_PATTERN.matcher(path).replaceAll("%20");
    }

    public static String cleanPath(String path) {
        if (path == null) {
            return null;
        }
        return path.replaceAll("[/]+", SLASH);
    }

    private class Node {
        private String path;
        private String segment;
        private Map<String, Node> staticRouters;
        private Node dynamicRouter;
        private boolean isWildcard;
    }

    public static TrieRouter createRoute() {
        return new TrieRouter();
    }

    // source code by https://makefile.so/2021/01/23/trie-tree-and-route-match/
    public class TrieRouter {
        private final Node root;

        public TrieRouter() {
            this.root = new Node();
            this.root.path = "/";
            this.root.segment = "/";
        }

        // add route
        public TrieRouter addRoute(String path) {
            if (StringKit.isEmpty(path)) {
                return this;
            }
            String strippedPath = StringKit.strip(path, "/");
            String[] strings = strippedPath.split("/");
            if (strings.length != 0) {
                Node node = root;
                // split by /
                for (String segment : strings) {
                    node = addNode(node, segment);
                    if ("**".equals(segment)) {
                        break;
                    }
                }
                // At the end, set the path of the child node
                node.path = path;
            }
            return this;
        }

        // add note
        private Node addNode(Node node, String segment) {
            // If it is a wildcard node, return the current node directly:
            if ("**".equals(segment)) {
                node.isWildcard = true;
                return node;
            }
            // if it is a dynamic route,
            // create a child node and then hang the child node under the current node:
            if (segment.startsWith(":")) {
                Node childNode = new Node();
                childNode.segment = segment;
                node.dynamicRouter = childNode;
                return childNode;
            }

            Node childNode;
            // Static route, put in a Map,
            // the key of the Map is the URL segment, value is the new child node:
            if (node.staticRouters == null) {
                node.staticRouters = new HashMap<>();
            }
            if (node.staticRouters.containsKey(segment)) {
                childNode = node.staticRouters.get(segment);
            } else {
                childNode = new Node();
                childNode.segment = segment;
                node.dynamicRouter = childNode;
                node.staticRouters.put(segment, childNode);
            }
            return childNode;
        }

        // match route
        public String matchRoute(String path) {
            if (StringKit.isEmpty(path)) {
                return null;
            }
            String strippedPath = StringKit.strip(path, "/");
            String[] strings = strippedPath.split("/");
            if (strings.length != 0) {
                Node node = root;
                // split by /
                for (String segment : strings) {
                    node = matchNode(node, segment);
                    // if no route is matched or a wildcard route is used, break:
                    if (node == null || node.isWildcard) {
                        break;
                    }
                }
                if (node != null) {
                    return node.path;
                }
            }
            return null;
        }

        public boolean match(String path) {
            return matchRoute(path) != null;
        }

        // match child node
        private Node matchNode(Node node, String segment) {
            if (node.staticRouters != null && node.staticRouters.containsKey(segment)) {
                return node.staticRouters.get(segment);
            }
            if (node.dynamicRouter != null)
                return node.dynamicRouter;
            if (node.isWildcard)
                return node;
            return null;
        }
    }

}