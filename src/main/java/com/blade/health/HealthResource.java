package com.blade.health;

import com.blade.Blade;
import com.blade.Environment;

import java.util.Optional;

public class HealthResource {

    private static final String BLADE_HEALTH_ACTIVE = "blade.health.active";
    private final Environment env;
    private final Blade blade;

    public HealthResource(Environment env, Blade blade) {
        this.env = env;
        this.blade = blade;
    }

    public void addHealthEndpoint() {
        Optional<Boolean> bladeHealthActive = env.getBoolean(BLADE_HEALTH_ACTIVE);
        if (bladeHealthActive.isPresent() && bladeHealthActive.get()) {
            blade.get("/health", ctx -> ctx.json(HealthResponse.builder().status("UP").build()).status(200));
        }
    }

}
