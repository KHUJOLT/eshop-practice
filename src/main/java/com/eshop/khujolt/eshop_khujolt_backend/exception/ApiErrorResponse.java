package com.eshop.khujolt.eshop_khujolt_backend.exception;

import java.time.Instant;
import java.util.Map;

public record ApiErrorResponse (
        Instant timestamp,
        int status,
        String message,
        String path,
        Map<String, String> fieldErrors
) {

}
