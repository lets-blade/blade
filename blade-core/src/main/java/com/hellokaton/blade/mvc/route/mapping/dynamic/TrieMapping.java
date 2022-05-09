package com.hellokaton.blade.mvc.route.mapping.dynamic;

import com.hellokaton.blade.kit.StringKit;
import com.hellokaton.blade.mvc.http.HttpMethod;
import com.hellokaton.blade.mvc.route.DynamicMapping;
import com.hellokaton.blade.mvc.route.Route;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

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
        private final Map<HttpMethod, Route> routeMap = new LinkedHashMap<>(4);

        /**
         * in staticChildren and dynamicChild, only one is real children, according to ChildType
         * when in dynamic ChildType, staticChildren is used to store static end node, which is last node
         * with static text.
         */
        private final Map<String, Node> staticChildren = new LinkedHashMap<>(8);

        private Node dynamicChild;

        private Node anyStaticChild() {
            for (Node value : staticChildren.values()) {
                return value;
            }
            return null;
        }

        public Node putChildIfAbsent(Node child, boolean isEnd) {
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
                        boolean canDynamic = staticChildren.values()
                                .stream()
                                .allMatch(Node::isEnd);
                        if (!canDynamic) {
                            throw new IllegalStateException(
                                    String.format("%s conflict with path %s", child.getPart(),
                                            anyStaticChild().getPart())
                            );
                        }
                        // convert to Dynamic ChildType
                        dynamicChild = child;
                        return dynamicChild;
                    }
                    return staticChildren.computeIfAbsent(child.getPart(), ignore -> child);
                case DYNAMIC:
                    if (Objects.equals(dynamicChild.getPart(), child.getPart())) {
                        return dynamicChild;
                    }
                    if (isEnd && child.getType() == NodeType.NORMAL) {
                        return staticChildren.computeIfAbsent(child.getPart(), ignore -> child);
                    }
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
            } else if (dynamicChild != null) {
                return ChildType.DYNAMIC;
            } else {
                return ChildType.STATIC;
            }
        }

    }

    @Override
    public void addRoute(HttpMethod httpMethod, Route route, List<String> uriVariableNames) {
        String path = route.getPath();

        Node prev = root;

        if ("/".equals(route.getPath())) {
            Node nodeByPart = getNodeByPart(path);
            if (NodeType.WILD.equals(nodeByPart.type)) {
                route.setWildcard(true);
            }
            prev = prev.putChildIfAbsent(nodeByPart, true);
            prev.getRouteMap().put(httpMethod, route);
            return;
        }

        path = StringKit.strip(route.getPath(), "/");

        String[] parts = path.split("/");
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (StringKit.isBlank(part)) {
                continue;
            }
            boolean isEnd = i == parts.length - 1;
            Node nodeByPart = getNodeByPart(part);
            if (NodeType.WILD.equals(nodeByPart.type)) {
                route.setWildcard(true);
            }
            prev = prev.putChildIfAbsent(nodeByPart, isEnd);
            if (isEnd) {
                prev.getRouteMap().put(httpMethod, route);
            }
        }
    }

    private Node getNodeByPart(String part) {
        NodeType type;
        if (part.charAt(0) == ':') {
            type = NodeType.PARAM;
        } else {
            if (part.startsWith("*")) {
                type = NodeType.WILD;
            } else {
                type = NodeType.NORMAL;
            }
        }
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
        Map<String, String> uriVariables = new LinkedHashMap<>(2);
        Iterator<String> partIter = partIter(path);
        Node prev = root;
        walk:
        for (; partIter.hasNext(); ) {
            String part = partIter.next();
            if (StringKit.isBlank(part)) continue;
            boolean isEnd = !partIter.hasNext();
            switch (prev.childType()) {
                case STATIC:
                    Node child = prev.getStaticChildren().get(part);
                    if (child == null) {
                        return null;
                    }
                    prev = child;
                    break;
                case DYNAMIC:
                    if (isEnd) {
                        Node staticEnd = prev.getStaticChildren().get(part);
                        if (staticEnd != null) {
                            prev = staticEnd;
                            break;
                        }
                    }
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
                (!partIter.hasNext() || prev.getType() == NodeType.WILD)) {
            Route selectedRoute = prev.selectRoute(requestMethod);
            if (selectedRoute == null) {
                return null;
            }
            Route route = new Route(selectedRoute);
            route.setPathParams(uriVariables);
            return route;
        }

        return null;
    }

    protected Iterator<String> partIter(String path) {
        return new Iterator<String>() {
            private int start = 1;
            private int end = start + 1;

            @Override
            public boolean hasNext() {
                return end <= path.length();
            }

            @Override
            public String next() {
                while (end < path.length() && path.charAt(end) != '/') {
                    end++;
                }
                String part = path.substring(start, end);
                start = end + 1;
                end = start + 1;
                return part;
            }
        };
    }

    @Override
    public void clear() {
        root = Node.builder().build();
    }
}
