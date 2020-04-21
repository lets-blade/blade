package com.blade.health;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HealthResponse {

    private String status;
}
