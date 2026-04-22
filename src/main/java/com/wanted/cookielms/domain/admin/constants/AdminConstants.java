package com.wanted.cookielms.domain.admin.constants;

import java.util.Set;

public class AdminConstants {
    public static final Set<String> EXCLUDE_ENDPOINT_PREFIXES = Set.of(
            "/admin/logs",
            "/admin/users/",
            "/uploads/",
            "/css/",
            "/js/",
            "/favicon",
            "/error",
            "/actuator"
    );

    public static boolean isPageEndpoint(String endpoint) {
        return EXCLUDE_ENDPOINT_PREFIXES.stream()
                .noneMatch(prefix -> endpoint.startsWith(prefix));
    }
}