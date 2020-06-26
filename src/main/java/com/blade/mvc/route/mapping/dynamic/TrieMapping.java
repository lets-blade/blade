package com.blade.mvc.route.mapping.dynamic;

import com.blade.kit.StringKit;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.route.DynamicMapping;
import com.blade.mvc.route.Route;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Trie tree based url route
 *
 * Support:
 *  - * : match any one part
 *  - /* : match suffix with any number of parts
 *  - :xxx : match one part as path variable
 *
 * Example:
 *  - /aaa/* :   /aaa/bbb, /aaa/bbb/ccc
 *  - /aaa/*\/ccc : /aaa/bbb/ccc, /aaa/ddd/ccc
 *  - /aaa/*\/ccc/*
 *  - /aaa/bbb/:id : /aaa/bbb/ccc
 *  - /aaa/bbb/:name/:id
 *
 * @author: dqyuan
 * @date: 2020/06/25
 */
public class TrieMapping implements DynamicMapping {

    private Node root = Node.builder().build();

    private enum NodeType {
        NORMAL,   // static text
        WILD,    // wildcard
        PARAM   // path param
    }

    private enum ChildType {
        NONCHILD,  // without child now
        STATIC,    // have multiple static children
        DYNAMIC   // or only have one dynamic child
    }

    @Builder
    @Getter
    @Setter
    private static class Node {

        private String part;

        private NodeType type;

        /**
         * support http method
         */
        private final Map<HttpMethod, Route> routeMap = new LinkedHashMap<>();

        /**
         * in staticChildren and dynamicChild, only one is valid
         */
        private final Map<String, Node> staticChildren = new LinkedHashMap<>();

        private Node dynamicChild;

        private Node anyStaticChild() {
            for (Node value : staticChildren.values()) {
                return value;
            }
            return null;
        }

        public Node putChildIfAbsent(Node child) {
            switch (childType()) {
                case NONCHILD:
                    if (child.getType() == NodeType.NORMAL) {
                        staticChildren.put(child.getPart(), child);
                    } else {
                        dynamicChild = child;
                    }
                    return child;
                case STATIC:
                    if (child.getType() != NodeType.NORMAL) {
                        throw new IllegalStateException(
                                String.format("%s conflict with path %s", child.getPart(),
                                        anyStaticChild().getPart())
                        );
                    }
                    return staticChildren.computeIfAbsent(child.getPart(), ignore -> child);
                case DYNAMIC:
                    throw new IllegalStateException(
                      String.format("%s conflict with path %s", child.getPart(),
                              dynamicChild.getPart())
                    );
            }
            throw new IllegalStateException();
        }

        /**
         * is end of a url
         * @return
         */
        public boolean isEnd() {
            return !routeMap.isEmpty();
        }

        public Route selectRoute(HttpMethod httpMethod) {
            return routeMap.getOrDefault(
                    httpMethod,
                    routeMap.get(HttpMethod.ALL)
            );
        }

        public ChildType childType() {
            if (staticChildren.isEmpty() && dynamicChild == null) {
                return ChildType.NONCHILD;
            } else if (!staticChildren.isEmpty()) {
                return ChildType.STATIC;
            } else {
                return ChildType.DYNAMIC;
            }
        }

    }

    @Override
    public void addRoute(HttpMethod httpMethod, Route route, List<String> uriVariableNames) {
        String path = route.getOriginalPath();
        Node prev = root;
        String[] parts = path.split("/");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (StringKit.isBlank(part)) {
                continue;
            }
            boolean isEnd = i == parts.length - 1;

            prev = prev.putChildIfAbsent(getNodeByPart(part));
            if (isEnd) {
                prev.getRouteMap().put(httpMethod, route);
            }
        }
    }

    private Node getNodeByPart(String part) {
        NodeType type = part.charAt(0) == ':'? NodeType.PARAM:
                "*".equals(part) ? NodeType.WILD:
                        NodeType.NORMAL;
        return Node.builder()
                .part(part)
                .type(type)
                .build();
    }

    @Override
    public void register() {
    }

    @Override
    public Route findRoute(String httpMethod, String path) {
        HttpMethod requestMethod = HttpMethod.valueOf(httpMethod);
        Map<String, String> uriVariables = new LinkedHashMap<>();
        String[] parts = path.split("/");
        Node prev = root;
        int i = 0;
        walk:
        for (; i < parts.length; i++) {
            String part = parts[i];
            if (StringKit.isBlank(part)) continue;
            switch (prev.childType()) {
                case STATIC:
                    Node child = prev.getStaticChildren().get(part);
                    if (child == null) {
                        return null;
                    }
                    prev = child;
                    break;
                case DYNAMIC:
                    prev = prev.getDynamicChild();
                    if (prev.getType() == NodeType.PARAM) {
                        uriVariables.put(prev.getPart().substring(1), part);
                    }
                    break;
                default:
                    // leaf node
                    break walk;
            }
        }

        if (prev.isEnd() &&
                (i == parts.length || prev.getType() == NodeType.WILD)) {
            Route route = new Route(prev.selectRoute(requestMethod));
            route.setPathParams(uriVariables);
            return route;
        }

        return null;
    }

    @Override
    public void clear() {
        root = Node.builder().build();
    }
}
