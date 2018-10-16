package com.blade.mvc.route.mapping;


import com.blade.mvc.route.Route;

import java.util.List;

public class FastRouteMappingInfo {
    Route        route;
    List<String> variableNames;

    FastRouteMappingInfo(Route route, List<String> variableNames) {
        this.route = route;
        this.variableNames = variableNames;
    }

    public Route getRoute() {
        return route;
    }

    public List<String> getVariableNames() {
        return variableNames;
    }

}