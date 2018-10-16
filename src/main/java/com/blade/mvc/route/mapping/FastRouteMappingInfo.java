package com.blade.mvc.route.mapping;

import com.blade.mvc.route.Route;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FastRouteMappingInfo {

    private Route        route;
    private List<String> variableNames;

}